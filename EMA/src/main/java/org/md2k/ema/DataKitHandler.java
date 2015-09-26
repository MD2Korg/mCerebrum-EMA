package org.md2k.ema;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitApi;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;

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
public class DataKitHandler {
    DataKitApi dataKitApi;
    Context context;
    private static DataKitHandler instance=null;
    public static DataKitHandler getInstance(Context context){
        if(instance==null){
            instance=new DataKitHandler(context);
        }
        return instance;
    }
    private DataKitHandler(Context context){
        this.context=context;
        dataKitApi = new DataKitApi(context);
    }
    boolean connectDataKit(OnConnectionListener onConnectionListener) {
        return dataKitApi.connect(onConnectionListener);
    }
    void insert(DataSourceClient dataSourceClient, DataType data){
        dataKitApi.insert(dataSourceClient, data);
    }
    DataSourceClient register(DataSource dataSource){
        return dataKitApi.register(dataSource).await();
    }
    public void disconnect(){
        dataKitApi.disconnect();
    }
/*    public void sendData(final QuestionsJSON questionsJSON){
        connectDataKit(new OnConnectionListener() {
            @Override
            public void onConnected() {
                TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                PlatformBuilder platformBuilder=new PlatformBuilder().setType(PlatformType.PHONE).setId(mngr.getDeviceId());
                DataSourceBuilder dataSourceBuilder=new DataSourceBuilder().setType(DataSourceType.SURVEY).setPlatform(platformBuilder.build());

                DataSourceClient dataSourceClient=register(dataSourceBuilder.build());
                Gson gson = new Gson();
                String json = gson.toJson(questionsJSON);
                DataTypeString dataTypeString = new DataTypeString(DateTime.getDateTime(),json);
                insert(dataSourceClient,dataTypeString);
                disconnect();
            }
        });
    }
*/
}
