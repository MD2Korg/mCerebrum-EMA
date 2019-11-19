/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.ema;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

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

/**
 * Abstract class for an interviewing activity.
 */
public abstract class ActivityAbstractInterview extends FragmentActivity {
    private static final String TAG = ActivityAbstractInterview.class.getSimpleName();
    DataKitAPI dataKitAPI;
    DataSourceClient dataSourceClient;
    public EMA ema;
    IntentFilter intentFilter;

    /**
     * Loads an EMA from an intent.
     */
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

    /**
     * Returns an arraylist of <code>Questions</code>.
     * @param q Question
     * @return An arraylist of <code>Questions</code>.
     */
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

    /**
     * Connects to DataKitAPI, loads an EMA, and registers a broadcast receiver.
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataKitAPI = DataKitAPI.getInstance(this);
        try {
            dataKitAPI.connect(new OnConnectionListener() {
                /**
                 * Registers the EMA with DataKitAPI
                 */
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

    /**
     * Sets the status and end time for the EMA before inserting into DataKit.
     * @param state Status of the EMA.
     */
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

    /**
     * Unregisters the broadcast receiver and disconnects DataKitAPI before destruction.
     */
    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
            dataKitAPI.disconnect();
        }catch (Exception e){}
        super.onDestroy();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        /**
         * Ends the EMA if it has timed out.
         * @param context Android context
         * @param intent Received intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("TYPE");
            if (type.equals("TIMEOUT")) {
                emaEnd(Constants.EMA_ABANDONED_BY_TIMEOUT);
            }
        }
    };

    /**
     * Creates a <code>DataSourceBuilder</code> for the EMA.
     * @return A <code>DataSourceBuilder</code> for the EMA.
     */
    DataSourceBuilder create(){
        DataSourceBuilder d = new DataSourceBuilder().setType(ema.getType()).setId(ema.getId());
        return d;
    }
}
