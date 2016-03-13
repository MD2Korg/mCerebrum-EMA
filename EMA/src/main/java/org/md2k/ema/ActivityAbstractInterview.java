package org.md2k.ema;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.messagehandler.OnExceptionListener;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.status.Status;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Report.Log;

public abstract class ActivityAbstractInterview extends Activity {
    private static final String TAG = ActivityAbstractInterview.class.getSimpleName();
    int state;
    long timeout;
    String display_name;
    String id;
    String name;
    String file_name;
    static final int AT_START = 0;
    static final int DONE = 1;
    static final int ABANDONED_BY_USER=2;
    static final int TIMEOUT=3;
    static final int MISSED=4;
    long lastResponseTime=-1;
    Handler handler;

    QuestionAnswers questionAnswers;
    private MyBroadcastReceiver myReceiver;
    IntentFilter intentFilter;

    abstract void updateUI();

//    abstract void setupInitialUI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        manageState();
        intentFilter= new IntentFilter("org.md2k.ema.operation");
        myReceiver = new MyBroadcastReceiver(new Callback() {
            @Override
            public void onTimeOut() {
                state=TIMEOUT;
                manageState();
            }

            @Override
            public void onMissed() {
                state=MISSED;
                manageState();
            }
        });
        if (intentFilter != null) {
            registerReceiver(myReceiver, intentFilter);
        }

    }

    void manageState() {
        long curTime = DateTime.getDateTime();
        switch (state) {
            case AT_START:
                questionAnswers.setStartTime(curTime);
                updateUI();
                break;
            case TIMEOUT:
                questionAnswers.setStatus(Constants.ABANDONED_BY_TIMEOUT);
                questionAnswers.setEndTime(DateTime.getDateTime());
                handler.postDelayed(stopInterview, 2000);
                updateUI();
                sendData();
                break;
            case MISSED:
                questionAnswers.setStatus(Constants.EMA_MISSED);
                questionAnswers.setEndTime(DateTime.getDateTime());
                handler.postDelayed(stopInterview, 2000);
                updateUI();
                sendData();
                break;

            case ABANDONED_BY_USER:
                questionAnswers.setEndTime(DateTime.getDateTime());
                questionAnswers.setStatus(Constants.EMA_ABANDONED_BY_USER);
                handler.postDelayed(stopInterview, 2000);
                updateUI();
                sendData();
                break;
            case DONE:
                questionAnswers.setEndTime(DateTime.getDateTime());
                questionAnswers.setStatus(Constants.EMA_COMPLETED);
                handler.postDelayed(stopInterview, 2000);
                updateUI();
                sendData();
                break;
        }
    }
    void sendData(){
        Gson gson = new Gson();
        String sample = gson.toJson(questionAnswers);
        Intent intent=new Intent();
        intent.setAction("org.md2k.ema_scheduler.response");
        intent.putExtra("type","question_answer");
        intent.putExtra("value",sample);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }
    void sendLastResponseTime(){
        Intent intent=new Intent();
        intent.setAction("org.md2k.ema_scheduler.response");
        intent.putExtra("type","last_response_time");
        intent.putExtra("value",lastResponseTime);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

    void initQuestionAnswer() {
        QuestionManager.getInstance(this, id, file_name).clear();
        questionAnswers = QuestionManager.getInstance(this, id, file_name).questionAnswers;
    }

    public void initInterviewState() {
        state = AT_START;
    }


    private Runnable stopInterview = new Runnable() {
        public void run() {
            finish();
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() ... ActivityAbstractInterview");
        if(myReceiver != null)
            unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
