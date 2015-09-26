package org.md2k.ema;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

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

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class FragmentNumeric extends FragmentBase {
    private static final String TAG = FragmentNumeric.class.getSimpleName();

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static FragmentNumeric create(String emaType, int pageNumber) {
        FragmentNumeric fragment = new FragmentNumeric();
        fragment.setArguments(getArgument(emaType, pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setTypeMultipleChoiceSelect(ViewGroup rootView, final QuestionAnswer questionAnswer) {
        Log.d(TAG, "setTypeMultipleChoiceSelect() questionAnswer=" + questionAnswer.getQuestion_id() + " " + questionAnswer.getResponse_option());
        NumberPicker numberPicker_option = (NumberPicker) rootView.findViewById(R.id.numberPicker_option);
        Log.d(TAG, "size=" + questionAnswer.response_option.size() + " " + questionAnswer.response_option.get(0) + " " + questionAnswer.response_option.get(1));
        int minValue=Integer.valueOf(questionAnswer.response_option.get(0));
        int maxValue=Integer.valueOf(questionAnswer.response_option.get(1));
        Log.d(TAG,"minvalue="+minValue+" maxvalue="+maxValue);

        numberPicker_option.setMaxValue(maxValue);
        numberPicker_option.setMinValue(minValue);
        numberPicker_option.setWrapSelectorWheel(false);
        updateNext(true);

        numberPicker_option.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                ArrayList<String> response=new ArrayList<>();
                response.add(String.valueOf(i1));
                questionAnswer.setResponse(response);
                updateNext(true);

            }
        });
    }

    public boolean isAnswered() {
        return questionAnswer.getQuestion_type() == null || !(questionAnswer.getQuestion_type().equals(Constants.MULTIPLE_SELECT) ||
                questionAnswer.getQuestion_type().equals(Constants.MULTIPLE_CHOICE)) || questionAnswer.getResponse().size() > 0;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(isAnswered());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() mPageNumber=" + mPageNumber);
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_numeric, container, false);
        questionAnswer.setPrompt_time(DateTime.getDateTime());
        setQuestionText(rootView, questionAnswer);

        setTypeMultipleChoiceSelect(rootView, questionAnswer);
        return rootView;
    }
}
