package org.md2k.ema.data;


import org.md2k.ema.Constants;

import java.util.ArrayList;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

public class Question{
    private static final String TAG = Question.class.getSimpleName();
    private int question_id;
    private String question_type;
    private String question_text;
    private ArrayList<String> response_option;
    private ArrayList<String> condition;

    private ArrayList<String> response;
    private long prompt_time;
    private long finish_time;


    Question(int question_id, String question_text, String question_type, ArrayList<String> question_responses, ArrayList<String> condition) {
        this.question_id = question_id;
        this.question_type = question_type;
        this.question_text = question_text;
        this.response_option = question_responses;
        this.condition = condition;
        this.response = new ArrayList<>();
        prompt_time=-1;
        finish_time=-1;

    }

    public boolean isType(String TYPE) {
        return question_type != null && question_type.equals(TYPE);
    }

    private boolean hasResponseSelected(String response){
        if(this.response ==null) return false;
        if(this.response.size()==0) return false;
        for(int i=0;i< this.response.size();i++)
            if(this.response.get(i).equals(response)) return true;
        return false;
    }
    public boolean isResponseExist(String option) {
        if(response ==null) return false;
        for (int i = 0; i < response.size(); i++)
            if (response.get(i).equals(option)) return true;
        return false;
    }

    void addResponse(String option) {
        if(response ==null) response=new ArrayList<>();
        response.add(option);
    }
    public boolean isValidCondition(ArrayList<Question> questions) {
        if (condition == null) return true;
        for(int i=0;i<condition.size();i++) {
            String[] separated = condition.get(i).split(":");
            int qid = Integer.valueOf(separated[0]);
            String part;
            if(separated[1].startsWith("~")) {
                part=separated[1].substring(1);
                if(questions.get(qid).response==null || questions.get(qid).response.size()==0) {
                    prompt_time=-1;
                    finish_time=-1;
                    response.clear();
                    return false;
                }
                if (!questions.get(qid).hasResponseSelected(part)) return true;
            }else{
                part=separated[1];
                if (questions.get(qid).hasResponseSelected(part)) return true;
            }
        }
        prompt_time=-1;
        finish_time=-1;
        response.clear();
        return false;
    }
    public boolean isValid() {
        if (question_type == null) return true;
        if (question_type.equals(Constants.MULTIPLE_CHOICE)) {
            return response != null && response.size() == 1;
        }
        if (question_type.equals(Constants.MULTIPLE_SELECT)) {
            return response != null && response.size() > 0;
        }
        return !question_type.equals(Constants.TEXT_NUMERIC) || response != null && response.size() > 0 && response.get(0).length() != 0;
    }

    public long getPrompt_time() {
        return prompt_time;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setPrompt_time(long prompt_time) {
        if(prompt_time<=0)
        this.prompt_time = prompt_time;
    }

    public void setFinish_time(long finish_time) {
        this.finish_time = finish_time;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public ArrayList<String> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<String> response) {
        this.response = response;
    }

    public ArrayList<String> getResponse_option() {
        return response_option;
    }
}

