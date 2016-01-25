package org.md2k.ema;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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
import org.md2k.utilities.UI.AlertDialogs;

public abstract class ActivityAbstractInterview extends Activity {
    private static final String TAG = ActivityAbstractInterview.class.getSimpleName();
    int state;
    long timeout;
    String display_name;
    String id;
    String name;
    String file_name;
    static final int AT_START = 0;
    static final int TIMED_OUT = 1;
    static final int DONE = 2;
    DataKitAPI dataKitAPI;

    Handler handler;

    QuestionAnswers questionAnswers;

    abstract void updateUI();

//    abstract void setupInitialUI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        dataKitAPI = DataKitAPI.getInstance(getApplicationContext());
        dataKitAPI.connect(new OnConnectionListener() {
            @Override
            public void onConnected() {
            }
        }, new OnExceptionListener() {
            @Override
            public void onException(Status status) {
                android.util.Log.d(TAG, "onException...");
                Toast.makeText(ActivityAbstractInterview.this, "EMA Stopped. DataKit Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
        manageState();
    }

    void manageState() {
        long curTime = DateTime.getDateTime();
        switch (state) {
            case AT_START:
                questionAnswers.setStartTime(curTime);
                handler.removeCallbacks(timeoutInterview);
                handler.postDelayed(timeoutInterview, timeout * 1000);
                updateUI();
                break;
            case TIMED_OUT:
                handler.postDelayed(stopInterview, 3000);
                questionAnswers.setStatus(Constants.EMA_ABANDONED);
                updateUI();
                writeToDataKit();
                break;
            case DONE:
                handler.removeCallbacks(timeoutInterview);
                questionAnswers.setEndTime(DateTime.getDateTime());
                questionAnswers.setStatus(Constants.EMA_COMPLETED);
                handler.postDelayed(stopInterview, 3000);
                updateUI();
                writeToDataKit();
                break;
        }
    }

    void writeToDataKit() {
        Log.d(TAG, "writeToDataKit()...");
        Gson gson = new Gson();
        String sample = gson.toJson(questionAnswers);
        Log.d(TAG, "Sample=" + sample);
        DataSourceClient dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
        DataTypeString dataTypeString = new DataTypeString(DateTime.getDateTime(), sample);
        dataKitAPI.insert(dataSourceClient, dataTypeString);
        dataKitAPI.disconnect();
    }

    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).setMetadata(METADATA.NAME, "Phone").build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.SURVEY).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Survey");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "EMA question");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeString.class.getName());
        return dataSourceBuilder;
    }

    void initQuestionAnswer() {
        QuestionManager.getInstance(this, id, file_name).clear();
        questionAnswers = QuestionManager.getInstance(this, id, file_name).questionAnswers;
    }

    public void initInterviewState() {
        state = AT_START;
    }


    /*
     * This runnable is used to run the prompting behavior of the application
	 */

    private Runnable timeoutInterview = new Runnable() {
        public void run() {
            handler.removeCallbacks(timeoutInterview);
            state = TIMED_OUT;
            manageState();
        }
    };
    private Runnable stopInterview = new Runnable() {
        public void run() {
            finish();
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() ... ActivityAbstractInterview");
        if (dataKitAPI.isConnected()) dataKitAPI.disconnect();
        super.onDestroy();
    }
}
