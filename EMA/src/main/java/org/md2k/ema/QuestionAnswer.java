package org.md2k.ema;


import org.md2k.utilities.Report.Log;

import java.io.Serializable;
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

public class QuestionAnswer extends Question implements Serializable {
    private static final String TAG = QuestionAnswer.class.getSimpleName();
    private ArrayList<String> response;
    private long prompt_time;
    boolean hasResponseSelected(String response){
        if(this.response ==null) return false;
        for(int i=0;i< this.response.size();i++)
            if(this.response.get(i).equals(response)) return true;
        return false;
    }

    public QuestionAnswer(Question question) {
        super(question.getQuestion_id(),question.getQuestion_text(), question.getQuestion_type(), question.getResponse_option(),question.getCondition());
        this.response = new ArrayList<>();
        prompt_time=-1;
    }


    public ArrayList<String> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<String> response) {
        this.response = response;
    }

    public long getPrompt_time() {
        return prompt_time;
    }

    public void setPrompt_time(long prompt_time) {
        this.prompt_time = prompt_time;
    }
    boolean isResponseExist(String option) {
        if(response ==null) return false;
        for (int i = 0; i < response.size(); i++)
            if (response.get(i).equals(option)) return true;
        return false;
    }
    void addResponse(String option) {
        if(response ==null) response=new ArrayList<>();
        response.add(option);
    }

    boolean isValidCondition(ArrayList<QuestionAnswer> questions) {
        if (condition == null) return true;
        for(int i=0;i<condition.size();i++) {
            String[] separated = condition.get(i).split(":");
            int qid = Integer.valueOf(separated[0]);
            if (questions.get(qid).hasResponseSelected(separated[1])) return true;
        }
        return false;
    }
    boolean isValid() {
        Log.d(TAG,"isValid: question_type="+question_type+" selected="+ response);
        if(response !=null)
            Log.d(TAG,"isValid: "+ response.toString());
        if (question_type == null) return true;
        if (question_type.equals(Constants.MULTIPLE_CHOICE)){
            if(response ==null) return false;
            else if(response.size()==1) return true;
            else return false;
        }
        if (question_type.equals(Constants.MULTIPLE_SELECT)){
            if(response ==null) return false;
            else if(response.size()>0) return true;
            else return false;
        }
        return true;
    }
}

