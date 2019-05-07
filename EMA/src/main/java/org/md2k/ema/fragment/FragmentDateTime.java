package org.md2k.ema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.md2k.ema.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class FragmentDateTime extends FragmentBase {
    private static final String TAG = FragmentDateTime.class.getSimpleName();
    TimePicker timePicker;
    DatePicker datePicker;
    public static FragmentDateTime create(int pageNumber) {
        FragmentDateTime fragment = new FragmentDateTime();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setDate(ViewGroup rootView) {
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        String s = question.getResponse().get(0);
        String split[] = s.split("-");
        int month = Integer.valueOf(split[0]);
        int day = Integer.valueOf(split[1]);
        int year = Integer.valueOf(split[2]);
//        datePicker.setMaxDate(System.currentTimeMillis());
        updateNext(true);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ArrayList<String> str = question.getResponse();
                String ans = String.format(Locale.getDefault(), "%02d-%02d-%04d",monthOfYear, dayOfMonth, year);
                str.set(0, ans);
                question.setResponse(str);
                updateNext(true);
            }
        });
    }

    void setHourMinute(ViewGroup rootView) {
        timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);

        String s = question.getResponse().get(1);
        String split[] = s.split(":");
        int hour = Integer.valueOf(split[0]);
        int min = Integer.valueOf(split[1]);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        updateNext(true);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                ArrayList<String> str = question.getResponse();
                String ans = String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute);
                str.set(1, ans);
                question.setResponse(str);
                updateNext(true);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(true);
    }
    private void setQuestionValue(){
        if (question.getResponse() == null || question.getResponse().size() != 2) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            String date = String.format(Locale.getDefault(), "%02d-%02d-%04d",month, day, year);
            String time = String.format(Locale.US, "%02d:%02d:00", hour, minute);
            ArrayList<String> res = new ArrayList<>();
            res.add(date);
            res.add(time);
            question.setResponse(res);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_datetime, container, false);
        setQuestionText(rootView, question);
        setQuestionValue();
        setDate(rootView);
        setHourMinute(rootView);
        return rootView;
    }
}
