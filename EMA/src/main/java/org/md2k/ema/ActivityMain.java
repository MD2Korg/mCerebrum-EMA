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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.md2k.ema.data.EMAInfo;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main activity of the application.
 */
public class ActivityMain extends Activity {

    /**
     * Permissions are requested upon activity creation.
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permission.requestPermission(this, new PermissionCallback() {
            /**
             * Generates an error if permissions are not granted, calls <code>load()</code> otherwise.
             * @param isGranted Whether permissions are granted or not.
             */
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    Toast.makeText(getApplicationContext(),
                            "EMA app ... !PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    load();
                }
            }
        });
    }

    /**
     * Loads the UI and buttons unless an <code>Intent</code> starts an EMA.
     */
    void load() {
        if (getIntent().hasExtra("id") || getIntent().hasExtra("type")) {
            startEMAUsingIntent();
            finish();
        } else {
            showUIAndButtons();
        }
    }

    /**
     * Starts an EMA from an intent.
     */
    private void startEMAUsingIntent(){
        String id = getIntent().getStringExtra("id");
        String type = getIntent().getStringExtra("type");
        String title = getIntent().getStringExtra("title");
        String summary = getIntent().getStringExtra("summary");
        String description = getIntent().getStringExtra("description");
        String question = getIntent().getStringExtra("question");
        if(question == null) {
            String filename = getIntent().getStringExtra("filename");
            if(filename == null)
                question = getQuestion(id, type);
            else question = getQuestion(filename);
        }
        startEMA(id, type, title, summary, description, question);
    }

    /**
     * Returns a question for the given EMA survey.
     * @param id Id of the EMA.
     * @param type Type of EMA.
     * @return A question for the given EMA survey.
     */
    private String getQuestion(String id, String type){
        final EMAInfo[] emas = EMAInfo.getEMAs(this);
        for (EMAInfo ema : emas) {
            if (ema.isEqual(id, type))
                return ema.getQuestion(this);
        }
        return null;
    }

    /**
     * Returns questions from the given file.
     * @param filename File to extract questions from.
     * @return Questions from the given file.
     */
    public String getQuestion(String filename) {
        StringBuilder text = new StringBuilder();
        String f = Constants.CONFIG_DIRECTORY + filename;
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ignored) {
        }finally {
            if(br != null) try {
                br.close();
            } catch (IOException ignored) {}
        }
        return text.toString();
    }

    /**
     * Creates the <code>ContentView</code> and creates buttons for each EMA.
     */
    void showUIAndButtons() {
        final EMAInfo[] emas = EMAInfo.getEMAs(this);
        setContentView(R.layout.activity_main);
        for (EMAInfo ema : emas) {
            Button myButton = createButton(ema);
            LinearLayout ll = (LinearLayout) findViewById(R.id.linear_layout_buttons);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(myButton, lp);
        }
    }

    /**
     * Returns a button to start an EMA with the given info.
     * @param ema <code>EMAInfo</code> for the EMA to start.
     * @return A button to start an EMA with the given info.
     */
    private Button createButton(final EMAInfo ema) {
        Button myButton = new Button(this);
        myButton.setText(ema.getTitle());
        myButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Calls <code>startEMA()</code> when the given button is clicked.
             * @param view Button clicked.
             */
            @Override
            public void onClick(View view) {
                startEMA(ema.getId(), ema.getType(), ema.getTitle(), ema.getSummary(), ema.getDescription(),
                        ema.getQuestion(ActivityMain.this));
            }
        });
        return myButton;
    }

    /**
     * Starts an EMA survey activity.
     * @param id Id of the EMA.
     * @param type Type of EMA.
     * @param title Title of the EMA.
     * @param summary EMA summary.
     * @param description Description of the EMA.
     * @param question EMA question.
     */
    private void startEMA(String id, String type, String title, String summary, String description, String question) {
        Intent intent = new Intent(ActivityMain.this, ActivityInterview.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        intent.putExtra("summary", summary);
        intent.putExtra("description", description);
        intent.putExtra("question", question);
        startActivity(intent);
    }


    /**
     * Provides actions for menu items.
     *
     * @param item Menu item that was selected.
     * @return Whether the action was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
