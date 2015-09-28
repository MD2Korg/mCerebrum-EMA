package org.md2k.ema;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

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
    private NonSwipeableViewPager mPager=null;
    FragmentBase fragmentBase;

    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    void setupInitialUI(){
        setContentView(R.layout.activity_interview_general);
        mPager=null;
        closeOptionsMenu();
        mPagerAdapter=null;

    }
    void setupInterviewUI(){
        setContentView(R.layout.activity_question);
        openOptionsMenu();
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Activity -> onCreateOptionsMenu");

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        if(mPager!=null) {
            getMenuInflater().inflate(R.menu.menu_previous_next, menu);
            menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);
            MenuItem item = menu.findItem(R.id.action_next);
/*        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
*/
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
//                    QuestionManager.getInstance(con)questionManager.questionAnswers.setEndTime(DateTime.getDateTime());
//                    DataKitHandler.getInstance(this).sendData(new QuestionsJSON(Questions.getInstance(), emaType));
//                    Questions.getInstance().destroy();
                    state=DONE;
                    manageState();
//                    finish();
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
            if(questionAnswers.questionAnswers.get(position).getQuestion_type()==null)
                fragmentBase = FragmentMultipleChoiceSelect.create(emaType, position);

            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.MULTIPLE_CHOICE) ||
                    questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.MULTIPLE_SELECT))
                fragmentBase = FragmentMultipleChoiceSelect.create(emaType, position);
            else if (questionAnswers.questionAnswers.get(position).getQuestion_type().equals(Constants.NUMERIC))
                fragmentBase= FragmentNumeric.create(emaType, position);
            else{
                fragmentBase = FragmentMultipleChoiceSelect.create(emaType, position);
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
