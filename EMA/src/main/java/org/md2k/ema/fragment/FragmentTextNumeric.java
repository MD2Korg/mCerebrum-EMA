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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.md2k.ema.Constants;
import org.md2k.ema.R;

import java.util.ArrayList;

/**
 * UI fragment for numeric text input.
 */
public class FragmentTextNumeric extends FragmentBase {
    EditText editText;

    /**
     * Returns a <code>FragmentMultipleChoiceSelect</code> object with the given page number.
     * @param pageNumber Page number for the new fragment.
     * @return A <code>FragmentMultipleChoiceSelect</code> object with the given page number.
     */
    public static FragmentTextNumeric create(int pageNumber) {
        FragmentTextNumeric fragment = new FragmentTextNumeric();
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
     * Sets the <code>EditText</code> when focused.
     */
    void setEditTextFocused() {
        if (editText.getText().toString().equals(Constants.TAP)) {
            editText.setText("");
        }
        editText.setTextColor(getResources().getColor(android.R.color.black));
        updateNext(false);
    }

    /**
     * Sets the <code>EditText</code> when not focused.
     */
    void setEditTextNotFocused() {
        if (editText.getText().toString().length() == 0) {
            editText.setText(Constants.TAP);
        }
        if (editText.getText().toString().equals(Constants.TAP))
            editText.setTextColor(getResources().getColor(R.color.teal_100));
        else
            editText.setTextColor(getResources().getColor(android.R.color.black));
    }

    /**
     * Sets up the <code>EditText</code> and listeners.
     * @param rootView The <code>View</code> that the picker will be placed in.
     */
    void setEditText(ViewGroup rootView) {
        editText = (EditText) rootView.findViewById(R.id.editTextNumber);
        setEditTextNotFocused();
        editText.addTextChangedListener(new TextWatcher() {
            /**
             * This method is called to notify you that, within <code>s</code>, the <code>count</code>
             * characters beginning at <code>start</code> are about to be replaced by new text with
             * length <code>after</code>.
             * @param s Character sequence before text change.
             * @param start
             * @param count
             * @param after Length of s after text change.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * This method is called to notify you that, within <code>s</code>, the <code>count</code>
             * characters beginning at <code>start</code> have just replaced old text that had length
             * <code>before.</code>
             * @param s Character sequence
             * @param start
             * @param before Length of old text.
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String response = editText.getText().toString();
                response = response.trim();
                ArrayList<String> responses = new ArrayList<>();
                responses.add(response);
                if (!response.equals(Constants.TAP) && response.length() != 0) {
                    question.setResponse(responses);
                } else question.getResponse().clear();

                updateNext(isAnswered());
            }

            /**
             * This method is called to notify you that, somewhere within <code>s</code>, the text has been changed.
             * @param s Character sequence that was changed.
             */
            @Override
            public void afterTextChanged(Editable s) {}
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Switches the focus of the given <code>View</code>.
             * @param view View whose focus changed.
             * @param b Whether the view is focused or not.
             */
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    setEditTextFocused();
                else
                    setEditTextNotFocused();
            }
        });
    }

    /**
     * Hides the keyboard and calls <code>super()</code>.
     */
    @Override
    public void onPause() {
        hideKeyboard();
        super.onPause();
    }

    /**
     * Returns whether the question has been answered and provides input error checking.
     * @return Whether the question has been answered.
     */
    public boolean isAnswered() {
        int lowerLimit = 0, higherLimit = 0;
        boolean lv = false, rv = false;
        if (question.getResponse_option().size() > 0) {
            lowerLimit = Integer.parseInt(question.getResponse_option().get(0));
            lv = true;
        }
        if (question.getResponse_option().size() > 1) {
            higherLimit = Integer.parseInt(question.getResponse_option().get(1));
            rv = true;
        }
        if (question.getResponse().size() > 0) {
            try {
                int num = Integer.parseInt(question.getResponse().get(0));
                if (lv && num < lowerLimit) {
                    Toast.makeText(getActivity(),"Value must be in between "+ lowerLimit +" and "+
                            higherLimit,Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (rv && num > higherLimit){
                    Toast.makeText(getActivity(),"Value must be in between "+ lowerLimit +" and "+
                            higherLimit,Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }catch(Exception e){
                Toast.makeText(getActivity(),"Value must be in between "+ lowerLimit +" and "+
                        higherLimit,Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Calls <code>super</code> and <code>updateNext</code>.
     * @param menu Menu to create.
     * @param inflater Menu inflater.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(isAnswered());
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
                .inflate(R.layout.fragment_text_numeric, container, false);
        setQuestionText(rootView, question);
        setEditText(rootView);
        return rootView;
    }
}
