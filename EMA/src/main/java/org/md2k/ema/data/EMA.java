package org.md2k.ema.data;

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

public class EMA {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private long start_time;
    private long end_time;
    private String status;
    private ArrayList<Question> questions;
    public EMA(String id, String type, String title, String summary, String description, ArrayList<Question> questions){
        this.id=id;
        this.type=type;
        this.title = title;
        this.summary= summary;
        this.description=description;
        this.questions=questions;
        start_time =-1;
        end_time =-1;
        status="NOT_ANSWERED";
        for(int i=0;questions!=null && i<questions.size();i++)
            questions.get(i).setResponse(new ArrayList<String>());
    }
    public void setStart_time(long start_time){
        this.start_time = start_time;
    }
    public void setEnd_time(long end_time){
        this.end_time = end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public int findValidQuestionNext(int cur) {
        cur++;
        while (cur < questions.size()) {
            if (!questions.get(cur).isValidCondition(questions))
                cur++;
            else break;
        }
        return cur;
    }
    public int findValidQuestionPrevious(int cur) {
        cur--;
        while (cur >= 0) {
            if (!questions.get(cur).isValidCondition(questions))
                cur--;
            else break;
        }
        return cur;
    }
}
