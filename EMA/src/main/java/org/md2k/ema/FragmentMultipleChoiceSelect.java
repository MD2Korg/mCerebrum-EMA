package org.md2k.ema;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
public class FragmentMultipleChoiceSelect extends FragmentBase {
    private static final String TAG = FragmentMultipleChoiceSelect.class.getSimpleName();

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static FragmentMultipleChoiceSelect create(String emaType, int pageNumber) {
        FragmentMultipleChoiceSelect fragment = new FragmentMultipleChoiceSelect();
        fragment.setArguments(getArgument(emaType, pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setTypeMultipleChoiceSelect(ViewGroup rootView, final QuestionAnswer questionAnswer) {
        Log.d(TAG, "setTypeMultipleChoiceSelect() questionAnswer=" + questionAnswer.getQuestion_id() + " " + questionAnswer.getResponse_option());
        final ListView listView = (ListView) rootView.findViewById(R.id.listView_options);
        if(questionAnswer.isType(Constants.MULTIPLE_CHOICE))
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        else listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        Log.d(TAG,questionAnswer.getQuestion_id()+" "+questionAnswer.getResponse_option().size());
        String options[]= new String[questionAnswer.getResponse_option().size()];
        for(int i=0;i<questionAnswer.getResponse_option().size();i++) {
            options[i] = questionAnswer.getResponse_option().get(i);
            Log.d(TAG,options[i]);
        }
        ArrayAdapter<String> adapter;
        if(questionAnswer.isType(Constants.MULTIPLE_CHOICE))
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice,options);
        else
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice,options);
        listView.setAdapter(adapter);
        for(int i=0;i<questionAnswer.getResponse_option().size();i++)
            if(questionAnswer.isResponseExist(questionAnswer.getResponse_option().get(i)))
                listView.setItemChecked(i,true);
        if(questionAnswer.isValid()) updateNext(true);
        else updateNext(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ArrayList<String> response=new ArrayList<>();
                int len = listView.getCount();
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = 0; i < len; i++)
                    if (checked.get(i)) {
                        String item = (String) listView.getItemAtPosition(i);
                        response.add(item);
                    }
                questionAnswer.setResponse(response);
                if(questionAnswer.isValid())
                    updateNext(true);
                else updateNext(false);

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

/*    CompoundButton.OnCheckedChangeListener setOnCheckedListenerMultipleSelect(final QuestionAnswer questionAnswer, final ArrayList<ToggleButton> toggleButtons) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    questionAnswer.getResponse().remove(buttonView.getText().toString());
                else if (buttonView.getText().equals("None of the above")) {
                    for (int i = 0; i < toggleButtons.size(); i++) {
                        if (!toggleButtons.get(i).getText().equals(buttonView.getText()))
                            toggleButtons.get(i).setChecked(false);
                    }
                    questionAnswer.getResponse().clear();
                    questionAnswer.getResponse().add(buttonView.getText().toString());
                } else {
                    for (int i = 0; i < toggleButtons.size(); i++)
                        if (toggleButtons.get(i).getText().equals("None of the above")) {
                            toggleButtons.get(i).setChecked(false);
                            questionAnswer.getResponse().remove(toggleButtons.get(i).getText().toString());
                        }
                    questionAnswer.getResponse().add(buttonView.getText().toString());
                }
                updateNext(isAnswered());
            }
        };
    }

    CompoundButton.OnCheckedChangeListener setOnCheckedListenerMultipleChoice(final QuestionAnswer questionAnswer, final ArrayList<ToggleButton> toggleButtons) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    questionAnswer.getResponse().remove(buttonView.getText().toString());
                else {
                    for (int i = 0; i < toggleButtons.size(); i++) {
                        if (!toggleButtons.get(i).getText().equals(buttonView.getText()))
                            toggleButtons.get(i).setChecked(false);
                    }
                    questionAnswer.getResponse().clear();
                    questionAnswer.getResponse().add(buttonView.getText().toString());
                }
                updateNext(isAnswered());
            }
        };
    }
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() mPageNumber=" + mPageNumber);
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_choice_select, container, false);
        questionAnswer.setPrompt_time(DateTime.getDateTime());
        setQuestionText(rootView, questionAnswer);

        if (questionAnswer.isType(Constants.MULTIPLE_CHOICE) || questionAnswer.isType(Constants.MULTIPLE_SELECT)){
            setTypeMultipleChoiceSelect(rootView, questionAnswer);
        }
        else{
        }
        return rootView;
    }

/*    private ToggleButton addToggleButtons(final QuestionAnswer questionAnswer, int response_id) {
        ToggleButton toggleButton = new ToggleButton(this.getActivity());
        String option = questionAnswer.getResponse_option().get(response_id);
        Log.d(TAG, "addToggleButtons() option=" + option);
        toggleButton.setTextOn(option);
        toggleButton.setTextOff(option);
        toggleButton.setText(option);
        if (questionAnswer.isResponseExist(option))
            toggleButton.setChecked(true);
        else toggleButton.setChecked(false);
        return toggleButton;
    }
*/
}
