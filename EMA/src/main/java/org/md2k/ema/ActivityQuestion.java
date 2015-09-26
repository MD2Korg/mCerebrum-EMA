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
public class ActivityQuestion extends Activity {
    private static final String TAG = ActivityQuestion.class.getSimpleName();
    private NonSwipeableViewPager mPager;
    FragmentBase fragmentBase;

    private PagerAdapter mPagerAdapter;
    private String emaType;
    private String filename;
    ArrayList<QuestionAnswer> questionAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emaType = getIntent().getStringExtra("ema_type");
        filename = getIntent().getStringExtra("filename");
        questionAnswers = QuestionManager.getInstance(this, emaType).questionAnswers.questionAnswers;
        Log.d(TAG, "question no=" + questionAnswers.size());
        QuestionManager.getInstance(this, emaType).questionAnswers.setStartTime(DateTime.getDateTime());
        setContentView(R.layout.activity_question);

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
        getMenuInflater().inflate(R.menu.menu_mood_surfing_exercise, menu);
        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        if (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
            item.setTitle("Finish");
        else
            item.setTitle("Next");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return super.onCreateOptionsMenu(menu);
    }

    PopupMenu popup = null;

    @Override
    public void onStop() {
        super.onStop();
        if (popup != null) popup.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (popup == null) {
                    Window window = getWindow();
                    View v = window.getDecorView();
                    int resId = getResources().getIdentifier("home", "id", "android");
                    popup = new PopupMenu(getActionBar().getThemedContext(), v.findViewById(resId));
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_options, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_home:
                                    NavUtils.navigateUpTo(ActivityQuestion.this, new Intent(ActivityQuestion.this, ActivityEMA.class));
                                    break;
                                case R.id.action_supporting_literature:
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
                }
                popup.show();

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

                if (!questionAnswers.get(mPager.getCurrentItem()).isValid()) {
                    Toast.makeText(getBaseContext(), "Please answer the questionAnswer first", Toast.LENGTH_SHORT).show();
                } else if (mPager.getCurrentItem() >= questionAnswers.size() - 1) {
//                    QuestionManager.getInstance(con)questionManager.questionAnswers.setEndTime(DateTime.getDateTime());
//                    DataKitHandler.getInstance(this).sendData(new QuestionsJSON(Questions.getInstance(), emaType));
//                    Questions.getInstance().destroy();
                    finish();
                } else if (questionAnswers.get(mPager.getCurrentItem()).isValid()) {
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
            if (!questionAnswers.get(cur).isValidCondition(questionAnswers))
                cur--;
            else break;
        }
        return cur;
    }

    int findValidQuestionNext(int cur) {
        cur++;
        while (cur < questionAnswers.size()) {
            if (!questionAnswers.get(cur).isValidCondition(questionAnswers))
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
            if(questionAnswers.get(position).getQuestion_type()==null)
                fragmentBase = FragmentMultipleChoiceSelect.create(emaType, position);

            else if (questionAnswers.get(position).getQuestion_type().equals(Constants.MULTIPLE_CHOICE) ||
                    questionAnswers.get(position).getQuestion_type().equals(Constants.MULTIPLE_SELECT))
                fragmentBase = FragmentMultipleChoiceSelect.create(emaType, position);
            else if (questionAnswers.get(position).getQuestion_type().equals(Constants.NUMERIC))
                fragmentBase= FragmentNumeric.create(emaType, position);
            else{
                fragmentBase = FragmentMultipleChoiceSelect.create(emaType, position);
            }

            return fragmentBase;
        }

        @Override
        public int getCount() {
            if (questionAnswers != null)
                return questionAnswers.size();
            else return 0;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
