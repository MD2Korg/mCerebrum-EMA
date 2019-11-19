package org.md2k.ema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.md2k.ema.R;

import java.util.ArrayList;
import java.util.Locale;


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

public class FragmentStringPicker extends FragmentBase {
    private NumberPicker numberPicker;
    String[] strings;
    public static FragmentStringPicker create(int pageNumber) {
        FragmentStringPicker fragment = new FragmentStringPicker();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setStringValues(ViewGroup rootView) {
        numberPicker = (NumberPicker) rootView.findViewById(R.id.number_picker);
/*
        TextView t = (TextView) rootView.findViewById(R.id.textView_numberpicker);
        t.setText(question.getResponse_option().get(0));
*/
        strings = new String[question.getResponse_option().size()];
        for(int i=0;i<question.getResponse_option().size();i++){
            strings[i]=question.getResponse_option().get(i);
        }
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(question.getResponse_option().size()-1);

        numberPicker.setDisplayedValues(strings);
        if(question.getResponse()==null || question.getResponse().size()==0) {
            ArrayList<String> s = new ArrayList<>();
            s.add(String.valueOf(strings[0]));
            question.setResponse(s);
        }
        String s=question.getResponse().get(0);
        numberPicker.setValue(0);
        for(int i=0;s!=null && i<strings.length;i++){
            if(s.equals(strings[i]))
                numberPicker.setValue(i);
        }
        updateNext(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ArrayList<String> s=new ArrayList<>();
                s.add(strings[newVal]);
//                s.add(String.format(Locale.getDefault(), "%d",numberPicker.getValue()));
                question.setResponse(s);
                updateNext(true);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_string_picker, container, false);
        setQuestionText(rootView, question);
        setStringValues(rootView);
        return rootView;
    }
}
