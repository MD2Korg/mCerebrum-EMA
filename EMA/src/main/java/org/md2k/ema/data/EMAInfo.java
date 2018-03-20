package org.md2k.ema.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.md2k.ema.Constants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
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
public class EMAInfo {
    private String id;
    private String type;
    private String title;
    private String summary;
    private String description;
    private String filename;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public static EMAInfo[] getEMAs(Context context) {
        EMAInfo[] ema;
        String filename= Constants.CONFIG_DIRECTORY+Constants.CONFIG_FILENAME;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<EMAInfo[]>() {
            }.getType();
            ema = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            return readFromAsset(context);
        }finally {
            if(br!=null) try {
                br.close();
            } catch (IOException ignored) {

            }
        }
        return ema;
    }
    private static EMAInfo[] readFromAsset(Context context){
        EMAInfo[] ema;
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("config.json")));
            Gson gson = new Gson();
            Type collectionType = new TypeToken<EMAInfo[]>() {
            }.getType();
            ema = gson.fromJson(br, collectionType);
        } catch (IOException e) {
            ema=null;
        }finally {
            if(br!=null) try {
                br.close();
            } catch (IOException ignored) {

            }
        }
        return ema;

    }

    public String getQuestion(Context context) {
        StringBuilder text = new StringBuilder();
        String f=Constants.CONFIG_DIRECTORY+filename;
        BufferedReader br=null;
        try {
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ignored) {
            return getQuestionFromAsset(context);
        }finally {
            if(br!=null) try {
                br.close();
            } catch (IOException ignored) {

            }
        }
        return text.toString();
    }

    private String getQuestionFromAsset(Context context) {
        StringBuilder text = new StringBuilder();
        BufferedReader br=null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename)));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ignored) {
        }finally {
            if(br!=null) try {
                br.close();
            } catch (IOException ignored) {

            }
        }
        return text.toString();
    }

    public boolean isEqual(String id, String type) {
        if(this.id==null && id!=null) return false;
        if(this.id!=null && id==null) return false;
        if(this.type==null && type!=null) return false;
        if(this.type!=null && type==null) return false;
        if(this.id!=null && !this.id.equals(id)) return false;
        if(this.type!=null && !this.type.equals(type)) return false;
        return true;
    }
}
