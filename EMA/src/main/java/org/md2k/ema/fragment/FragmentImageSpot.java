package org.md2k.ema.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private BitmapFactory.Options getBitmapOption(){

        BitmapFactory.Options myOptions = new BitmapFactory.Options();
//        myOptions.inDither = true;
//        myOptions.inScaled = false;
//        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
//        myOptions.inPurgeable = true;
//        myOptions.inScaled=true;
//        myOptions.in
        myOptions = null;
        return myOptions;
    }

    void setCircle() {
        ArrayList<Point> points = readPoints();
        Bitmap bitmap = getImage(getBitmapOption());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);


        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(mutableBitmap);
        for (int i = 0; i < points.size(); i++) {
            canvas.drawCircle(points.get(i).x, points.get(i).y, 50, paint);
        }

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

        imageView.setImageBitmap(getImage(getBitmapOption()));
        if (question.getResponse() == null || question.getResponse().size() < 2) {
            ArrayList<String> res = new ArrayList<>();
            res.add(String.valueOf(imageView.getWidth()));
            res.add(String.valueOf(imageView.getHeight()));
            question.setResponse(res);
        }
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int index = event.getActionIndex();
                final float[] coords = new float[] { event.getX(index), event.getY(index) };

                Matrix matrix = new Matrix();
                imageView.getImageMatrix().invert(matrix); //his drawable view extends ImageView
                //so it has access to the getImageMatrix.
                matrix.postTranslate(imageView.getScrollX(), imageView.getScrollY());
                matrix.mapPoints(coords);

                int xActual= (int) coords[0];
                int yActual= (int) coords[1]-100;
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        break;

                    case MotionEvent.ACTION_DOWN:
                        addPoint(xActual, yActual);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        editLastPoint(xActual,yActual);
                        break;
                }
                setCircle();
/*
                int[] viewCoords = new int[2];
                imageView.getLocationOnScreen(viewCoords);
                int x = (int) (event.getRawX() - viewCoords[0] - event.getXPrecision());
                int y = (int) (event.getRawY() - viewCoords[1] - event.getYPrecision());
                Log.d("abc", "[" + x + " " + y + "] [" + imageView.getWidth() + " " + imageView.getHeight() + "]");
                Log.d("abc", "touch=" + event.getRawX() + " " + event.getRawY());

                setCircle(x, y);
                ArrayList<String> res = new ArrayList<>();
                res.add(String.valueOf(x));
                res.add(String.valueOf(y));
                res.add(String.valueOf(imageView.getWidth()));
                res.add(String.valueOf(imageView.getHeight()));
*/

//                question.setResponse(res);
                return true;
            }
            //            return false;
            //          }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("abc", "Click");
            }
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
        setButton(rootView);
        setCircle();
        return rootView;
    }
    private void setButton(ViewGroup rootView){
        Button b;
        b= (Button) rootView.findViewById(R.id.button_clear_last);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(question.getResponse()!=null && question.getResponse().size()>2){
                    question.getResponse().remove(question.getResponse().size()-1);
                    question.getResponse().remove(question.getResponse().size()-1);
                    setCircle();
                }
            }
        });
        b= (Button) rootView.findViewById(R.id.button_clear_all);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(question.getResponse()!=null && question.getResponse().size()>2){
                    ArrayList<String> p = question.getResponse();
                    ArrayList<String> newP=new ArrayList<>();
                    newP.add(p.get(0));
                    newP.add(p.get(1));
                    question.setResponse(newP);
                    setCircle();
                }
            }
        });

    }
    private ArrayList<Point> readPoints(){
        ArrayList<Point> points = new ArrayList<>();
        for(int i=2;question.getResponse()!=null && i<question.getResponse().size();i+=2){
            Point p=new Point(Integer.parseInt(question.getResponse().get(i)),Integer.parseInt(question.getResponse().get(i+1)));
            points.add(p);
        }
        return points;
    }
    private void addPoint(int x, int y){
        ArrayList<String> res = question.getResponse();
        res.add(String.valueOf(x));
        res.add(String.valueOf(y));
        question.setResponse(res);
    }
    private void editLastPoint(int x, int y){
        ArrayList<String> res = question.getResponse();
        res.remove(res.size()-1);
        res.remove(res.size()-1);
        res.add(String.valueOf(x));
        res.add(String.valueOf(y));
        question.setResponse(res);
    }
}
