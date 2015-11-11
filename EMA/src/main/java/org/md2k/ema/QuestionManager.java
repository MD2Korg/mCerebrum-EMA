package org.md2k.ema;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.utilities.Report.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
public class QuestionManager {
    private static final String TAG = QuestionManager.class.getSimpleName();
    QuestionAnswers questionAnswers;
    private static QuestionManager instance=null;
    private static String id =null;
    public static QuestionManager getInstance(Context context, String id, String file_name){
        if(instance==null || QuestionManager.id ==null || !QuestionManager.id.equals(id)){
            instance = new QuestionManager(context, id, file_name);
        }
        return instance;
    }
    void clear(){
        instance=null;
    }
    private QuestionManager(Context context, String id,String file_name){
        QuestionManager.id =id;
        ArrayList<Question> questions=readQuestionsFromFile(context,file_name);
        questionAnswers=new QuestionAnswers(id);
        assert questions != null;
        for(int i=0;i<questions.size();i++){
            QuestionAnswer questionAnswer=new QuestionAnswer(questions.get(i));
            questionAnswers.add(questionAnswer);
        }
    }

    private ArrayList<Question> readQuestionsFromFile(Context context, String filename) {
        ArrayList<Question> questions;
        Log.d(TAG,"readQuestionFromFile() filename="+filename);
        BufferedReader br;
        try {
            if (Constants.FILE_LOCATION == Constants.ASSET) {
                br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            } else {
                if (!isExist(filename)) throw new FileNotFoundException();
                br = new BufferedReader(new FileReader(filename));
            }
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Question>>() {
            }.getType();
            questions = gson.fromJson(br, collectionType);
            return questions;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean isExist(String filename) {
        File file = new File(filename);
        return file.exists();
    }
}
