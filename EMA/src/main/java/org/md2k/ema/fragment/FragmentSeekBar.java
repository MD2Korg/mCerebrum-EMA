package org.md2k.ema.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.xw.repo.BubbleSeekBar;

import org.md2k.ema.R;

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
public class FragmentSeekBar extends FragmentBase {
    BubbleSeekBar seekBar;
    TextView textViewSelectedValue;
    public static FragmentSeekBar create(int pageNumber) {
        FragmentSeekBar fragment = new FragmentSeekBar();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setSeekBar(ViewGroup rootView) {
        seekBar = (BubbleSeekBar) rootView.findViewById(R.id.seekbar);
        textViewSelectedValue = (TextView) rootView.findViewById(R.id.textView_selectedValue);
        int minValue = 0;
        int maxValue=10;
        int sectionCount=10;
        int defaultValue=5;
        boolean isAutoAdjust=false;
        String lowerText="";
        String upperText = "";
        String header = "Selected Value:";

        if(question.getResponse_option().size()>0)
            minValue = Integer.parseInt(question.getResponse_option().get(0));
        if(question.getResponse_option().size()>1)
            maxValue = Integer.parseInt(question.getResponse_option().get(1));
        if(question.getResponse_option().size()>2){
            defaultValue = Integer.parseInt(question.getResponse_option().get(2));
        }
        if(question.getResponse_option().size()>3){
            sectionCount = Integer.parseInt(question.getResponse_option().get(3));
        }
        if(question.getResponse_option().size()>4){
            if(question.getResponse_option().get(4).toUpperCase().equals("AUTO_ADJUST"))
                isAutoAdjust = true;
        }
        if(question.getResponse_option().size()>5){
            header = question.getResponse_option().get(5);
        }
        if(question.getResponse_option().size()>6){
            lowerText = question.getResponse_option().get(6);
        }
        if(question.getResponse_option().size()>7){
            upperText = question.getResponse_option().get(7);
        }

        ((TextView)rootView.findViewById(R.id.textView_lower)).setText(lowerText);
        ((TextView)rootView.findViewById(R.id.textView_upper)).setText(upperText);
        ((TextView)rootView.findViewById(R.id.textView_selected)).setText(header);

//        minValue=0;maxValue = 10;sectionCount = 5;defaultValue = 5;isAutoAdjust=true;
//        minValue=0;maxValue = 100;sectionCount = 5;defaultValue = 50; isAutoAdjust=false;
        updateValue(defaultValue);

        seekBar.getConfigBuilder()
                .min(minValue)
                .max(maxValue)
                .progress(defaultValue)
                .sectionCount(sectionCount)
                .trackColor(ContextCompat.getColor(getActivity(), R.color.lightblue_100))
                .secondTrackColor(ContextCompat.getColor(getActivity(), R.color.lightblue_700))
                .thumbColor(ContextCompat.getColor(getActivity(), R.color.deeporange_500))
                .showSectionText()
                .sectionTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .sectionTextSize(18)
                .sectionTextPosition(BubbleSeekBar.TextPosition.SIDES)
//                .showThumbText()
//                .thumbTextColor(ContextCompat.getColor(getActivity(), R.color.deeporange_500))
//                .thumbTextSize(18)
                .bubbleColor(ContextCompat.getColor(getActivity(), R.color.deeporange_500))
                .bubbleTextSize(18)
                .trackSize(3)
                .touchToSeek()
                .showSectionMark()
//                .autoAdjustSectionMark()
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        if(isAutoAdjust){
            seekBar.getConfigBuilder().autoAdjustSectionMark().build();

        }
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                seekBar.correctOffsetWhenContainerOnScrolling();
                updateValue(progress);

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });
    }
    private void updateValue(int value){
        ArrayList<String> result = new ArrayList<>();
        result.add(String.valueOf(value));
        question.setResponse(result);
        textViewSelectedValue.setText(String.valueOf(value));
    }

    @Override
    public void onPause() {
        hideKeyboard();
        super.onPause();
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
                .inflate(R.layout.fragment_seekbar, container, false);
        setQuestionText(rootView, question);
        setSeekBar(rootView);
        return rootView;
    }
}
