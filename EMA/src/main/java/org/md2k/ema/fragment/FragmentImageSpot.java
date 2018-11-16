package org.md2k.ema.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.md2k.ema.Constants;
import org.md2k.ema.R;

import java.io.File;
import java.util.ArrayList;


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

public class FragmentImageSpot extends FragmentBase {
    ImageView imageView;

    public static FragmentImageSpot create(int pageNumber) {
        FragmentImageSpot fragment = new FragmentImageSpot();
        fragment.setArguments(getArgument(pageNumber));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setCircle(int x, int y) {
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        myOptions.inPurgeable = true;
        Bitmap bitmap = getImage(myOptions);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);


        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(x, y, 50, paint);

        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(mutableBitmap);
        imageView.invalidate();
    }

    Bitmap getImage(BitmapFactory.Options myOptions) {
        File imgFile = new File(Constants.CONFIG_DIRECTORY + question.getResponse_option().get(0));

        if (imgFile.exists()) {
            if (myOptions != null)
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath(), myOptions);
            else return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }

    void setImageView(ViewGroup rootView) {
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        imageView.setImageBitmap(getImage(null));
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] viewCoords = new int[2];
                imageView.getLocationOnScreen(viewCoords);
                int x = (int) (event.getRawX() - viewCoords[0] - event.getXPrecision());
                int y = (int) (event.getRawY() - viewCoords[1] - event.getYPrecision());
                Log.d("abc", "[" + x + " " + y+"] ["+imageView.getWidth()+" "+imageView.getHeight()+"]");
                setCircle(x, y);
                ArrayList<String> res = new ArrayList<>();
                res.add(String.valueOf(x));
                res.add(String.valueOf(y));
                res.add(String.valueOf(imageView.getWidth()));
                res.add(String.valueOf(imageView.getHeight()));

                question.setResponse(res);
                return true;
            }
            //            return false;
            //          }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        updateNext(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_image_spot, container, false);
        setQuestionText(rootView, question);
        setImageView(rootView);
        if(question.getResponse()!=null && question.getResponse().size()==4){
            setCircle(Integer.valueOf(question.getResponse().get(0)), Integer.valueOf(question.getResponse().get(1)));
        }
        return rootView;
    }
}
