package org.md2k.ema;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.md2k.datakitapi.messagehandler.ResultCallback;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.permission.PermissionInfo;

import io.fabric.sdk.android.Fabric;


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
public class ActivityMain extends Activity {
    private static final String TAG = ActivityMain.class.getSimpleName();
    EMA_Info ema_info;
    boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
       // Fabric.with(this, new Crashlytics());
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.getPermissions(this, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                isPermission = result;
                if (result)
                    load();
                else finish();
            }
        });
    }

    void load() {
        if(getIntent().hasExtra("id")){
            startEMA();
            finish();
        }
        else {
            setContentView(R.layout.activity_main);
            addButtons();
        }
    }
    void addButtons() {
        ema_info = new EMA_Info(getApplicationContext());
        if(ema_info.size()==-1){
            Toast.makeText(this,"ERROR: EMA configuration file is not available. Could not run...",Toast.LENGTH_LONG).show();
            finish();
        }
        for (int i = 0; i < ema_info.size(); i++) {
            Button myButton = new Button(this);
            myButton.setText(ema_info.get(i).name);
            LinearLayout ll = (LinearLayout) findViewById(R.id.linear_layout_buttons);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(myButton, lp);
            final int finalI = i;
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityMain.this, ActivityInterview.class);
                    intent.putExtra("id", ema_info.get(finalI).id);
                    intent.putExtra("name", ema_info.get(finalI).name);
                    intent.putExtra("file_name", ema_info.get(finalI).file_name);
                    intent.putExtra("timeout", ema_info.get(finalI).timeout);
                    startActivity(intent);
                }
            });
        }
    }
    void startEMA(){
        Intent receivedIntent=getIntent();
        Intent intent = new Intent(ActivityMain.this, ActivityInterview.class);
        intent.putExtra("id", receivedIntent.getStringExtra("id"));
        intent.putExtra("name", receivedIntent.getStringExtra("name"));
        intent.putExtra("file_name", receivedIntent.getStringExtra("file_name"));
        intent.putExtra("timeout", receivedIntent.getLongExtra("timeout",0));
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy() ... ActivityMain");
        super.onDestroy();
    }
}
