package org.md2k.ema;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class FragmentBase extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_TYPE = "type";
    private static final String TAG = FragmentBase.class.getSimpleName();
    Menu menu = null;

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    protected int mPageNumber;
    protected String emaType;
    QuestionAnswer questionAnswer = null;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    protected static Bundle getArgument(String emaType, int pageNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_TYPE, emaType);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "FragmentBase-> onCreate()");
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        emaType = getArguments().getString(ARG_TYPE);
        questionAnswer = QuestionManager.getInstance(getActivity(),emaType).questionAnswers.questionAnswers.get(mPageNumber);
        setHasOptionsMenu(true);
    }

    void setQuestionText(ViewGroup rootView, Question question) {
        String question_text = question.getQuestion_text();
        ((TextView) rootView.findViewById(R.id.textViewDescription)).setText(question_text);
    }

    public void updateNext(boolean answered) {
        if (menu != null)
            menu.findItem(R.id.action_next).setEnabled(answered);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        Log.d(TAG, "fragmentBase -> onCreateOptionsMenu");
        updateNext(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "fragmentBase -> onOptionsItemSelected ->" + item.getItemId());
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_previous:
                Log.d(TAG, "fragmentBase -> onOptionsItemSelected -> previous");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
