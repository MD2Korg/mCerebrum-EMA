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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import org.md2k.ema.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * UI fragment for setting hour and minute.
 */
public class FragmentHourMinuteAMPM extends FragmentBase {
    private static final String TAG = FragmentHourMinuteAMPM.class.getSimpleName();
    TimePicker timePicker;

    /**
     * Creates a new <code>FragmentHourMinuteAMPM</code> with the given page number.
     * @param pageNumber Page number for the new fragment.
     * @return A new <code>FragmentHourMinuteAMPM</code> with the given page number.
     */
    public static FragmentHourMinuteAMPM create(int pageNumber) {
        FragmentHourMinuteAMPM fragment = new FragmentHourMinuteAMPM();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    /**
     * Calls <code>super</code>.
     * @param savedInstanceState Previous state of this activity, if it existed.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Prompts the user to set the hour and minute.
     * @param rootView The <code>View</code> that the picker will be placed in.
     */
    void setHourMinute(ViewGroup rootView) {
        String defaultAnswer = "00:00:00";
        if (question.getResponse_option() != null && question.getResponse_option().size() > 0) {
            defaultAnswer = question.getResponse_option().get(0);
        }
        timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);

        if (question.getResponse() == null || question.getResponse().size() == 0) {
            ArrayList<String> s = new ArrayList<>();
            s.add(defaultAnswer);
            question.setResponse(s);
        }
        String s = question.getResponse().get(0);
        String split[] = s.split(":");
        int hour = Integer.valueOf(split[0]);
        int min = Integer.valueOf(split[1]);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        updateNext(true);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            /**
             * Sets the time response.
             * @param view Time picker.
             * @param hourOfDay New hour of day.
             * @param minute New minute.
             */
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                ArrayList<String> s = new ArrayList<>();
              
                s.add(String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute));

                question.setResponse(s);
                updateNext(true);
            }
        });
    }

    /**
     * Calls <code>super</code> and <code>updateNext</code>.
     * @param menu Menu to create.
     * @param inflater Menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(true);
    }

    /**
     * Creates the <code>rootView</code>.
     * @param inflater Layout inflater.
     * @param container View container.
     * @param savedInstanceState Previous state of this activity, if it existed.
     * @return The <code>rootView</code>
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_hour_minute_ampm, container, false);
        setQuestionText(rootView, question);
        setHourMinute(rootView);
        return rootView;
    }
}
