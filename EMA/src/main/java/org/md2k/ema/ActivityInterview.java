package org.md2k.ema;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Report.Log;

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
    private NonSwipeableViewPager mPager = null;
    FragmentBase fragmentBase;
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        id=getIntent().getStringExtra("id");
        name=getIntent().getStringExtra("name");
        display_name=getIntent().getStringExtra("display_name");
        file_name=getIntent().getStringExtra("file_name");
        timeout=getIntent().getLongExtra("timeout", 0);
        Log.d(TAG, "id=" + id + " display_name=" + display_name + " file_name=" + file_name + " timeout=" + timeout);
        setContentView(R.layout.activity_question);
        initQuestionAnswer();
        initInterviewState();
        initView();
        super.onCreate(savedInstanceState);
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
                lastResponseTime= DateTime.getDateTime();
                message="button=previous, question_id="+String.valueOf(mPager.getCurrentItem());
                sendLastResponseTime(lastResponseTime, message);
                mPager.getAdapter().notifyDataSetChanged();
                mPager.setCurrentItem(findValidQuestionPrevious(mPager.getCurrentItem()));
                break;
            case R.id.action_next:
                lastResponseTime= DateTime.getDateTime();
                message="button=next, question_id="+String.valueOf(mPager.getCurrentItem());
                sendLastResponseTime(lastResponseTime, message);
                if (!questionAnswers.questionAnswers.get(mPager.getCurrentItem()).isValid()) {
                    Toast.makeText(getBaseContext(), "Please answer the question first", Toast.LENGTH_SHORT).show();
                } else if (mPager.getCurrentItem() >= questionAnswers.questionAnswers.size() - 1) {
                    Log.d(TAG,""+mPager.getCurrentItem()+" "+questionAnswers.questionAnswers.size());
                    state = DONE;
                    manageState();
                } else if (questionAnswers.questionAnswers.get(mPager.getCurrentItem()).isValid()) {
                    mPager.getAdapter().notifyDataSetChanged();
                    mPager.setCurrentItem(findValidQuestionNext(mPager.getCurrentItem()));
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
                .setContentText("Please click to resume...").setSmallIcon(R.drawable.ic_notification_ema)
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
    public void onDestroy(){
        cancelNotification();
        Log.d(TAG,"onDestroy()...ActivityInterview");
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
//        showAlertDialog();

    }
    void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Quit?")
                .setIcon(R.drawable.ic_error_red_50dp)
                .setMessage("Do you want to quit from this survey? Survey will be marked as \"Abandoned\" ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state = ABANDONED_BY_USER;
                        manageState();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        alertDialog.show();
    }

}

