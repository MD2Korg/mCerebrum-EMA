package org.md2k.ema;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;

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
//                updateUI();
                sendData();
                handler.post(stopInterview);
                break;
            case MISSED:
                questionAnswers.setStatus(Constants.EMA_MISSED);
                questionAnswers.setEndTime(DateTime.getDateTime());
//                updateUI();
                sendData();
                handler.post(stopInterview);
                break;

            case ABANDONED_BY_USER:
                questionAnswers.setEndTime(DateTime.getDateTime());
                questionAnswers.setStatus(Constants.EMA_ABANDONED_BY_USER);
//                updateUI();
                sendData();
                handler.post(stopInterview);
                break;
            case DONE:
                questionAnswers.setEndTime(DateTime.getDateTime());
                questionAnswers.setStatus(Constants.EMA_COMPLETED);
  //              updateUI();
                sendData();
                handler.post(stopInterview);
                break;
        }
    }
    void sendData(){
        Gson gson = new Gson();
        String sample = gson.toJson(questionAnswers.getQuestionAnswers());
        Intent intent=new Intent();
        intent.setAction("org.md2k.ema_scheduler.response");
        intent.putExtra("TYPE","RESULT");
        intent.putExtra("ANSWER",sample);
        intent.putExtra("STATUS",questionAnswers.getStatus());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }
    void sendLastResponseTime(long lastResponseTime, String message){
        Intent intent=new Intent();
        intent.setAction("org.md2k.ema_scheduler.response");
        intent.putExtra("TYPE","STATUS_MESSAGE");
        intent.putExtra("TIMESTAMP",lastResponseTime);
        intent.putExtra("MESSAGE",message);
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
