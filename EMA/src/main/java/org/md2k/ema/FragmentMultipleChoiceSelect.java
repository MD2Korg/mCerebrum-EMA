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
import android.widget.TextView;

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
    public static final String ITEM_UNSELECT_OTHER = "<UNSELECT_OTHER>";
    ListView listView;
    TextView textViewPleaseSelect;
    ArrayAdapter<String> adapter;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static FragmentMultipleChoiceSelect create(int pageNumber, String id, String file_name) {
        FragmentMultipleChoiceSelect fragment = new FragmentMultipleChoiceSelect();
        fragment.setArguments(getArgument(pageNumber, id, file_name));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setTypeMultipleChoiceSelect() {
        Log.d(TAG, "setTypeMultipleChoiceSelect() questionAnswer=" + questionAnswer.getQuestion_text() + "......" + questionAnswer.getQuestion_id() + " " + questionAnswer.getResponse_option());
        if (questionAnswer.isType(Constants.MULTIPLE_CHOICE))
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        else
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        Log.d(TAG, questionAnswer.getQuestion_id() + " " + questionAnswer.getResponse_option().size());
        String options[] = new String[questionAnswer.getResponse_option().size()];
        for (int i = 0; i < questionAnswer.getResponse_option().size(); i++) {
            options[i] = questionAnswer.getResponse_option().get(i);
            if (questionAnswer.getResponse_option().get(i).contains(ITEM_UNSELECT_OTHER)) {
                options[i] = questionAnswer.getResponse_option().get(i).replaceAll(ITEM_UNSELECT_OTHER, "");
            }
            Log.d(TAG, options[i]);
        }

        if (questionAnswer.isType(Constants.MULTIPLE_CHOICE))
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, options);
        else
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, options);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < questionAnswer.getResponse_option().size(); i++)
            if (questionAnswer.isResponseExist(questionAnswer.getResponse_option().get(i)))
                listView.setItemChecked(i, true);
        else listView.setItemChecked(i, false);
        if (questionAnswer.isValid()) updateNext(true);
        else updateNext(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ArrayList<String> response = new ArrayList<>();
                int len = listView.getCount();
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                if (questionAnswer.getResponse_option().get(position).contains(ITEM_UNSELECT_OTHER)) {
                    for (int i = 0; i < len; i++)
                        if (i != position)
                            listView.setItemChecked(i, false);
                    if (listView.isItemChecked(position))
                        response.add((String) listView.getItemAtPosition(position));
                } else {
                    for (int i = 0; i < len; i++)
                        if (checked.get(i)) {
                            if (questionAnswer.getResponse_option().get(i).contains(ITEM_UNSELECT_OTHER)) {
                                listView.setItemChecked(i, false);
                            } else {
                                String item = (String) listView.getItemAtPosition(i);
                                response.add(item);
                            }
                        }
                }
                questionAnswer.setResponse(response);
                if (questionAnswer.isValid())
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() mPageNumber=" + mPageNumber);
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_choice_select, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView_options);
        textViewPleaseSelect= (TextView) rootView.findViewById(R.id.textView_please_select);
        setQuestionText(rootView, questionAnswer);
        updateView();
        return rootView;
    }
    public void updateView(){
        Log.d(TAG,"updateView()...");
        if (questionAnswer.isType(Constants.MULTIPLE_CHOICE) || questionAnswer.isType(Constants.MULTIPLE_SELECT)) {
            setTypeMultipleChoiceSelect();
        } else {
            textViewPleaseSelect.setVisibility(View.GONE);
        }

    }

}
