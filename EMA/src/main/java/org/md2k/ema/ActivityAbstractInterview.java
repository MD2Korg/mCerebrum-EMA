package org.md2k.ema;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.ema.broadcast.BroadcastSend;
import org.md2k.ema.data.EMA;
import org.md2k.ema.data.Question;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ActivityAbstractInterview extends Activity {
    private static final String TAG = ActivityAbstractInterview.class.getSimpleName();
    DataKitAPI dataKitAPI;
    DataSourceClient dataSourceClient;
    public EMA ema;
    IntentFilter intentFilter;


    private void loadEMA() {
        String id = getIntent().getStringExtra("id");
        String type = getIntent().getStringExtra("type");
        String title = getIntent().getStringExtra("title");
        String summary = getIntent().getStringExtra("summary");
        String description = getIntent().getStringExtra("description");
        ArrayList<Question> questions = getQuestions(getIntent().getStringExtra("question"));
        ema = new EMA(id, type, title, summary, description, questions);
        if (ema.getQuestions() == null || ema.getQuestions().size() == 0) {
            Toast.makeText(this, "Can't load file content", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ema.getQuestions().get(0).setPrompt_time(DateTime.getDateTime());

    }

    private ArrayList<Question> getQuestions(String q) {
        try {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Question>>() {
            }.getType();
            return gson.fromJson(q, collectionType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataKitAPI=DataKitAPI.getInstance(this);
        try {
            dataKitAPI.connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    try {
                        dataSourceClient = dataKitAPI.register(create());
                    } catch (DataKitException e) {
                        Log.e("abc","datakit register error");
                        finish();
                    }
                }
            });
        } catch (DataKitException e) {
            Log.e("abc","datakit connection error");
            finish();
        }
        loadEMA();
        ema.setStart_time(DateTime.getDateTime());
        intentFilter = new IntentFilter("org.md2k.scheduler.request");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    void emaEnd(String state) {
        ema.setStatus(state);
        ema.setEnd_time(DateTime.getDateTime());
        BroadcastSend.result(this, state);
        Gson gson = new Gson();
        JsonElement je = gson.toJsonTree(ema);
        DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), je.getAsJsonObject());
        try {
            dataKitAPI.insert(dataSourceClient, dataTypeJSONObject);
        } catch (DataKitException e) {
            Log.e("abc","datakit error on insertion");
        }
        finish();

    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
            dataKitAPI.disconnect();
        }catch (Exception e){}
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("TYPE");
            if (type.equals("TIMEOUT")) {
                emaEnd(Constants.EMA_ABANDONED_BY_TIMEOUT);
            }
        }
    };
    DataSourceBuilder create(){
        DataSourceBuilder d = new DataSourceBuilder().setType(ema.getType()).setId(ema.getId());
        return d;
    }
}
