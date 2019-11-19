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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Provides methods that prevent a swiping or touch event to switch between pages.
 */
public class NonSwipeableViewPager extends ViewPager {

    /**
     * Constructor
     * @param context Android context
     */
    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor
     * @param context Android context
     * @param attrs Android attributeSet
     */
    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Never allow swiping to switch between pages.
     * @param event Swiping event
     * @return Always returns false to prevent switching.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * Never allow swiping to switch between pages.
     * @param event Touch event
     * @return Always returns false to prevent switching.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}