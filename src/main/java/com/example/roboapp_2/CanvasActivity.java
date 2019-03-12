package com.example.roboapp_2;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CanvasActivity extends AppCompatActivity {

    CanvasClass canvas;
    private int length;
    private int width;
    private char[][] room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        Bundle extra = getIntent().getExtras();
        if(extra != null) {
            length = extra.getInt("length");
            width = extra.getInt("width");
            room = new char[length][width];
            for(int i=0;i<this.length;i++){
                room[i] = extra.getCharArray("map"+i);
            }
        }
        canvas = new CanvasClass(this,length,width,room);
        setContentView(canvas);
    }
}