package org.md2k.ema;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.messagehandler.ResultCallback;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.permission.PermissionInfo;

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
public class ActivityInterview extends ActivityAbstractInterview {
    private static final String TAG = ActivityInterview.class.getSimpleName();
    FragmentBase fragmentBase;
    boolean isPermission = false;
    private NonSwipeableViewPager mPager = null;
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        super.onCreate(savedInstanceState);
    }

    void load() {
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        display_name = getIntent().getStringExtra("display_name");
        file_name = getIntent().getStringExtra("file_name");
        timeout = getIntent().getLongExtra("timeout", 0);
        Log.d(TAG, "id=" + id + " display_name=" + display_name + " file_name=" + file_name + " timeout=" + timeout);
        setContentView(R.layout.activity_question);
        initQuestionAnswer();
        if(questionAnswers==null || questionAnswers.questionAnswers==null || questionAnswers.questionAnswers.size()==0) {
            Toast.makeText(this, "Can't load file content", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        initInterviewState();
        initView();
        if(questionAnswers.questionAnswers.get(0).getPrompt_time()<=0) {
            questionAnswers.questionAnswers.get(0).setPrompt_time(DateTime.getDateTime());
            Log.d(TAG,"curPage=0 setprompttime");
        }
    }

    void updateUI() {
        switch (state) {
            case AT_START:
                findViewById(R.id.text_view_status).setVisibility(View.GONE);
                findViewById(R.id.view_pager).setVisibility(View.VISIBLE);
                break;
            case ABANDONED_BY_USER:
                findViewById(R.id.text_view_status).setVisibility(View.GONE);
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.text_view_status)).setText("You have chosen to not to answer this survey");
                findViewById(R.id.action_previous).setVisibility(View.GONE);
                findViewById(R.id.action_next).setVisibility(View.GONE);
                break;
            case TIMEOUT:
                findViewById(R.id.text_view_status).setVisibility(View.GONE);
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.text_view_status)).setText("Time out");
                findViewById(R.id.action_previous).setVisibility(View.GONE);
                findViewById(R.id.action_next).setVisibility(View.GONE);
                break;
            case MISSED:
                findViewById(R.id.text_view_status).setVisibility(View.GONE);
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.text_view_status)).setText("Missed");
                findViewById(R.id.action_previous).setVisibility(View.GONE);
                findViewById(R.id.action_next).setVisibility(View.GONE);
                break;
            case DONE:
                findViewById(R.id.text_view_status).setVisibility(View.GONE);
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.text_view_status)).setText("Survey Completed. Thank you!");
                findViewById(R.id.action_previous).setVisibility(View.GONE);
                findViewById(R.id.action_next).setVisibility(View.GONE);
                break;
        }
    }

    void initView() {
//        openOptionsMenu();
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                Log.d(TAG, "viewpager: onPageSelected: position=" + position);
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Activity -> onCreateOptionsMenu");

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        if (mPager != null) {
            getMenuInflater().inflate(R.menu.menu_previous_next, menu);
            menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);
            MenuItem item = menu.findItem(R.id.action_next);
            if (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                item.setTitle("Finish");
            else
                item.setTitle("Next");
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long lastResponseTime;
        String message;
            switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                break;
            case R.id.action_previous:
                lastResponseTime = DateTime.getDateTime();
                message = "button=previous, question_id=" + String.valueOf(mPager.getCurrentItem());
                sendLastResponseTime(lastResponseTime, message);
                mPager.getAdapter().notifyDataSetChanged();
                int curQuestion=findValidQuestionPrevious(mPager.getCurrentItem());
                mPager.setCurrentItem(curQuestion);
                if(questionAnswers.questionAnswers.get(curQuestion).getPrompt_time()<=0) {
                    questionAnswers.questionAnswers.get(curQuestion).setPrompt_time(DateTime.getDateTime());
                    Log.d(TAG,"curPage="+curQuestion+" setprompttime");
                }
                break;
            case R.id.action_next:
                lastResponseTime = DateTime.getDateTime();
                message = "button=next, question_id=" + String.valueOf(mPager.getCurrentItem());
                sendLastResponseTime(lastResponseTime, message);
                if (!questionAnswers.questionAnswers.get(mPager.getCurrentItem()).isValid()) {
                    Toast.makeText(getBaseContext(), "Please answer the question first", Toast.LENGTH_SHORT).show();
                } else if (mPager.getCurrentItem() >= questionAnswers.questionAnswers.size() - 1) {
                    Log.d(TAG, "" + mPager.getCurrentItem() + " " + questionAnswers.questionAnswers.size());
                    state = DONE;
                    manageState();
                } else if (questionAnswers.questionAnswers.get(mPager.getCurrentItem()).isValid()) {
                    mPager.getAdapter().notifyDataSetChanged();
                    questionAnswers.questionAnswers.get(mPager.getCurrentItem()).setFinish_time(DateTime.getDateTime());
                    curQuestion=findValidQuestionNext(mPager.getCurrentItem());
                    mPager.setCurrentItem(curQuestion);
                    if(questionAnswers.questionAnswers.get(curQuestion).getPrompt_time()<=0) {
                        questionAnswers.questionAnswers.get(curQuestion).setPrompt_time(DateTime.getDateTime());
                        Log.d(TAG,"curPage="+curQuestion+" setprompttime");
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    int findValidQuestionPrevious(int cur) {
        cur--;
        while (cur >= 0) {
            if (!questionAnswers.questionAnswers.get(cur).isValidCondition(questionAnswers.questionAnswers))
                cur--;
            else break;
        }
        return cur;
    }

    int findValidQuestionNext(int cur) {
        cur++;
        while (cur < questionAnswers.questionAnswers.size()) {
            if (!questionAnswers.questionAnswers.get(cur).isValidCondition(questionAnswers.questionAnswers))
                cur++;
            else break;
        }
        return cur;
    }

    @Override
    public void onPause() {
        createNotification();
        super.onPause();
    }

    @Override
    public void onResume() {
        cancelNotification();
        super.onResume();
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, ActivityInterview.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Survey is available")
                .setContentText("Please click to resume...").setSmallIcon(R.drawable.ic_archive_teal_48dp)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0, noti);
    }

    public void cancelNotification() {
        NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nMgr.cancel(0);
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        Log.d(TAG, "onDestroy()...ActivityInterview");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem(): position=" + position);
            if (questionAnswers.questionAnswers.get(position).getQuestion_type() == null)
                fragmentBase = FragmentMultipleChoiceSelect.create(position, id, file_name);

            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.MULTIPLE_CHOICE) ||
                    questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.MULTIPLE_SELECT))
                fragmentBase = FragmentMultipleChoiceSelect.create(position, id, file_name);
            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.TEXT_NUMERIC))
                fragmentBase = FragmentTextNumeric.create(position, id, file_name);
            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.HOUR_MINUTE))
                fragmentBase = FragmentHourMinute.create(position, id, file_name);
            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.HOUR_MINUTE_AMPM))
                fragmentBase = FragmentHourMinuteAMPM.create(position, id, file_name);
            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.MINUTE_SECOND))
                fragmentBase = FragmentMinuteSecond.create(position, id, file_name);

            else {
                fragmentBase = FragmentMultipleChoiceSelect.create(position, id, file_name);
            }
            return fragmentBase;
        }

        @Override
        public int getCount() {
            if (questionAnswers != null)
                return questionAnswers.questionAnswers.size();
            else return 0;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}

