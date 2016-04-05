package org.md2k.ema;


import android.os.Parcel;
import android.os.Parcelable;

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

public class QuestionAnswer extends Question implements Parcelable {
    private ArrayList<String> response;
    private long prompt_time;

    protected QuestionAnswer(Parcel in) {
        super(in);
        response = in.createStringArrayList();
        prompt_time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringList(response);
        dest.writeLong(prompt_time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuestionAnswer> CREATOR = new Creator<QuestionAnswer>() {
        @Override
        public QuestionAnswer createFromParcel(Parcel in) {
            return new QuestionAnswer(in);
        }

        @Override
        public QuestionAnswer[] newArray(int size) {
            return new QuestionAnswer[size];
        }
    };

    boolean hasResponseSelected(String response){
        if(this.response ==null) return false;
        if(this.response.size()==0) return false;
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
            String part;
            if(separated[1].startsWith("~")) {
                part=separated[1].substring(1);
                if(questions.get(qid).response==null || questions.get(qid).response.size()==0) return false;
                if (!questions.get(qid).hasResponseSelected(part)) return true;
            }else{
                part=separated[1];
                if (questions.get(qid).hasResponseSelected(part)) return true;
            }
        }
        prompt_time=-1;
        response.clear();
        return false;
    }
    boolean isValid() {
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
        if(question_type.equals(Constants.TEXT_NUMERIC)){
            if(response==null) return false;
            if(response.size()>0 && response.get(0).length()!=0) return  true;
            return false;
        }
        return true;
    }
}

