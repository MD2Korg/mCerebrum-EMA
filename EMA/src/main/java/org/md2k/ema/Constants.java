package org.md2k.ema;


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
public class Constants {
    public static final int ASSET=0;
    public static final int INTERNAL_SDCARD=1;
    public static final int EXTERNAL_SDCARD=2;
    public static final int FILE_LOCATION=ASSET;
    public static final String CONFIG_FILENAME = "config_ema.json";

    public static final String MULTIPLE_CHOICE="multiple_choice";
    public static final String MULTIPLE_SELECT="multiple_select";
    public static final String TEXT="text";
    public static final String NUMERIC="numeric";

    public static final int RANDOM_EMA = 0;
    public static final int SMOKING_EMA = 1;
    public static final int SURF_THE_MOOD = 2;

    public static final String[] BEGIN_TITLE={
            "Use Your Imagination",
            "Notice and Accept",
            "Surf the Mood"
    };
    public static final String[] BEGIN_DESCRIPTION={
            "This is an exercise to help you use your imagination to manage your unpleasant thoughts or emotions. Please tap 'Begin' to start this exercise",
            "This is an exercise to help you become aware of and accept any physical sensations caused by stress. Please tap 'Begin' to start this exercise",
            "This is an exercise that will guide you through some imagery to help you manage stress. Please tap 'Begin' to start this exercise"
    };
}
