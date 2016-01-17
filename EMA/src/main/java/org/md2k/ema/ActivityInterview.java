package org.md2k.ema;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
            case TIMED_OUT:
                findViewById(R.id.text_view_status).setVisibility(View.VISIBLE);
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.text_view_status)).setText("The interview has timed out.");
                findViewById(R.id.action_previous).setVisibility(View.GONE);
                findViewById(R.id.action_next).setVisibility(View.GONE);
                break;
            case DONE:
                findViewById(R.id.text_view_status).setVisibility(View.VISIBLE);
                findViewById(R.id.view_pager).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.text_view_status)).setText("Interview Completed. Thank you!");
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                break;
            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                Log.d(TAG, "activity -> onOptionsItemSelected -> previous");

                Log.d(TAG, "Previous button: " + mPager.getCurrentItem());
                mPager.getAdapter().notifyDataSetChanged();
                mPager.setCurrentItem(findValidQuestionPrevious(mPager.getCurrentItem()));
                break;
            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                Log.d(TAG, "Next button" + " current=" + mPager.getCurrentItem());

                if (!questionAnswers.questionAnswers.get(mPager.getCurrentItem()).isValid()) {
                    Toast.makeText(getBaseContext(), "Please answer the questionAnswer first", Toast.LENGTH_SHORT).show();
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
                .setContentTitle("Interview is available")
                .setContentText("Please click to resume interview").setSmallIcon(R.drawable.ic_notification_ema)
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
    }

}

