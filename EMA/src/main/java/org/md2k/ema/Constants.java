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

package org.md2k.ema;

import android.os.Environment;

/**
 * Provides constant values for this package.
 */
public class Constants {
    public static final String TAP = "(Please 'TAP' here to type)";
    public static final String CONFIG_DIRECTORY= Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.ema/";
    public static final String CONFIG_FILENAME = "config.json";

    public static final String MULTIPLE_CHOICE="multiple_choice";
    public static final String MULTIPLE_SELECT="multiple_select";
    public static final String TEXT="text";
    public static final String TEXT_NUMERIC ="text_numeric";
    public static final String HOUR_MINUTE="hour_minute";
    public static final String MINUTE_SECOND="minute_second";
    public static final String HOUR_MINUTE_AMPM="hour_minute_ampm";

    public static final String EMA_COMPLETED="COMPLETED";
    public static final String EMA_ABANDONED_BY_TIMEOUT ="ABANDONED_BY_TIMEOUT";
    public static final String EMA_MISSED = "MISSED";
    public static final String EMA_ABANDONED_BY_USER="ABANDONED_BY_USER";
}
