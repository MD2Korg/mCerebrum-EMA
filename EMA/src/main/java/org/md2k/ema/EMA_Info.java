package org.md2k.ema;


import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;

import org.md2k.utilities.Report.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public class EMA_Info {
    private static final String TAG = EMA_Info.class.getSimpleName();
    public EMA_General ema_general;
    public EMA_Notification ema_notification;
    public EMA_Timeout ema_timeout;
    public EMA_TriggerType[] ema_triggertype;
    private static EMA_Info instance = null;
    Context context;

    public void show() {
        ema_general.show();
        ema_notification.show();
        ema_timeout.show();
        for (EMA_TriggerType ema_triggerType1 : ema_triggertype) {
            ema_triggerType1.show();
        }
    }

    public static EMA_Info getInstance(Context context) {
        if (instance == null)
            new EMA_Info(context);
        return instance;
    }

    String getFileName(String emaType) {
        for (int i = 0; i < ema_triggertype.length; i++)
            if (ema_triggertype[i].name.equals(emaType))
                return ema_triggertype[i].filename;
        return null;
    }

    EMA_Info(Context context) {
        this.context = context;
        BufferedReader br;
        Log.d(TAG, "File location=" + Constants.FILE_LOCATION);
        try {

            if (Constants.FILE_LOCATION == Constants.ASSET) {
                Log.d(TAG, "inside asset...");
                AssetManager assetManager = context.getAssets();
                Log.d(TAG, "assetManager=" + assetManager.toString() + " filename=" + Constants.CONFIG_FILENAME);
                br = new BufferedReader(new InputStreamReader(context.getAssets().open(Constants.CONFIG_FILENAME)));
                Log.d(TAG, "br=" + br.toString());
            } else {
//            br = new BufferedReader(new FileReader(Constants.DIR_FILENAME()));
            }
            Log.d(TAG, "before gson=");
            Gson gson = new Gson();
            instance = gson.fromJson(br, EMA_Info.class);
            instance.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class EMA_General {
        public String minimum_time_between_ema;
        public int maximum_ema;

        public void show() {
            Log.d(TAG, "minimum_time_between_ema=" + minimum_time_between_ema);
            Log.d(TAG, "maximum_ema=" + maximum_ema);
        }
    }

    class EMA_Notification {
        public String beep_time;
        public String platformType;
        public String platformId;
        public String location;

        public void show() {
            Log.d(TAG, "beep_time=" + beep_time + " platformType=" + platformType + " platformId=" + platformId + " location=" + location);
        }
    }

    class EMA_Timeout {
        public String start_timeout;
        public String interview_timeout;
        public String user_delay;

        public void show() {
            Log.d(TAG, "start_timeout=" + start_timeout + " interview_timeout=" + interview_timeout + " user_delay=" + user_delay);
        }

    }

    public class EMA_TriggerType implements Serializable {
        public String name;
        public String filename;
        public int budget;
        public int expected_event;
        public int trigger_delay;
        public String priority;

        public void show() {
            Log.d(TAG, "name=" + name + " filename=" + filename +
                    " budget=" + budget + " expected_event=" + expected_event +
                    " trigger_delay=" + trigger_delay + "priority=" + priority);
        }

    }
}


