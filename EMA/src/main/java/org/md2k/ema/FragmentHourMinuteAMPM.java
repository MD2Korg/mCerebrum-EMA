package org.md2k.ema;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import org.md2k.datakitapi.time.DateTime;

import java.util.ArrayList;
import java.util.Locale;

import static org.md2k.ema.R.id.numberPickerHour;
import static org.md2k.ema.R.id.numberPickerMinute;


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
public class FragmentHourMinuteAMPM extends FragmentBase {
    private static final String TAG = FragmentHourMinuteAMPM.class.getSimpleName();
    TimePicker timePicker;
//    NumberPicker numberPickerHour, numberPickerMinute;
    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static FragmentHourMinuteAMPM create(int pageNumber, String id, String file_name) {
        FragmentHourMinuteAMPM fragment = new FragmentHourMinuteAMPM();
        fragment.setArguments(getArgument(pageNumber, id, file_name));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setHourMinute(ViewGroup rootView) {
        timePicker= (TimePicker) rootView.findViewById(R.id.timePicker);
        if(questionAnswer.getResponse()==null || questionAnswer.getResponse().size()==0) {
            ArrayList<String> s = new ArrayList<>();
            s.add("00:00:00");
            questionAnswer.setResponse(s);
        }
        String s=questionAnswer.getResponse().get(0);
        String split[]=s.split(":");
        int hour=Integer.valueOf(split[0]);
        int min=Integer.valueOf(split[1]);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        updateNext(true);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                ArrayList<String> s=new ArrayList<>();
                s.add(String.format(Locale.US, "%02d:%02d:00",hourOfDay, minute));
                questionAnswer.setResponse(s);
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
                .inflate(R.layout.fragment_hour_minute_ampm, container, false);
        questionAnswer.setPrompt_time(DateTime.getDateTime());
        setQuestionText(rootView, questionAnswer);
        setHourMinute(rootView);
        return rootView;
    }
}
