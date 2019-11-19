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

package org.md2k.ema.fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.md2k.ema.ActivityInterview;
import org.md2k.ema.R;
import org.md2k.ema.data.Question;

import java.util.ArrayList;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class FragmentBase extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_QUESTION_ID = "question_id";

    private static final String TAG = FragmentBase.class.getSimpleName();
    Menu menu = null;

    /**
     * The fragment's page number, which is set to the argument value for <code>ARG_QUESTION_ID</code>.
     */
    protected int questionId;
    Question question = null;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    protected static Bundle getArgument(int pageNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_QUESTION_ID, pageNumber);
        return args;
    }

    /**
     * Sets the <code>questionId</code> and <code>question</code> and calls <code>setHasOptionsMenu(true)</code>.
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "FragmentBase-> onCreate()");
        super.onCreate(savedInstanceState);
        questionId = getArguments().getInt(ARG_QUESTION_ID);
        question = ((ActivityInterview) getActivity()).ema.getQuestions().get(questionId);
        setHasOptionsMenu(true);
    }

    /**
     * Sets the <code>TextView</code> for the question.
     * @param rootView Root <code>View</code> object.
     * @param question <code>Question</code> to display.
     */
    void setQuestionText(ViewGroup rootView, Question question) {
        String question_text = question.getQuestion_text();
        ((TextView) rootView.findViewById(R.id.textViewDescription)).setText(Html.fromHtml(question_text));
        if(question.getQuestion_type() == null)
            ((TextView) rootView.findViewById(R.id.textView_header)).setText("Notification:");
        else
            ((TextView) rootView.findViewById(R.id.textView_header)).setText("Question:");
    }

    /**
     * Sets the value of the next menu item's <code>enabled</code> attribute to the given value.
     * @param answered Whether the next item should be answered or not.
     */
    public void updateNext(boolean answered) {
        if (menu != null && menu.findItem(R.id.action_next) != null)
            menu.findItem(R.id.action_next).setEnabled(answered);
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu Options menu
     * @return Always returns true.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        Log.d(TAG, "fragmentBase -> onCreateOptionsMenu");
        updateNext(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Provides actions for menu items.
     * @param item Menu item that was selected.
     * @return Whether the action was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "fragmentBase -> onOptionsItemSelected ->" + item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_previous:
                Log.d(TAG, "fragmentBase -> onOptionsItemSelected -> previous");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Calls <code>super</code>.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Calls <code>super</code>.
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Hides the keyboard.
     */
    void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
