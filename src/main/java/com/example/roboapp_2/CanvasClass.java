package com.example.roboapp_2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

public class CanvasClass extends View{

    private Paint gray;
    private Paint blue;
    private Paint cyan;
    private Paint mText;
    private Paint known;
    private Paint known2;
    private Paint wall;
    private Rect rect;
    private int length;
    private int width;
    private char[][] room;

    public CanvasClass(Context context,int length, int width, char[][] room){
        super(context);
        setLength(length);
        setWidth(width);
        setRoom(room);
        setPaints();
        rect = new Rect();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth2() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public char[][] getRoom() {
        return room;
    }

    public void setRoom(char[][] room) {
        this.room = new char[getLength()][getWidth2()];
        this.room = room;
        this.room[1][2] = 'w';
        this.room[2][2] = 'z';
        this.room[3][2] = 'z';
    }

    public void setPaints(){
        gray = new Paint();
        blue = new Paint();
        cyan = new Paint();
        mText = new Paint();
        known = new Paint();
        known2 = new Paint();
        wall = new Paint();

        gray.setColor(Color.GRAY);
        blue.setColor(Color.BLUE);
        cyan.setColor(Color.CYAN);
        mText.setColor(Color.BLACK);
        known.setColor(Color.CYAN);
        known2.setColor(Color.BLUE);
        wall.setColor(Color.GRAY);

        gray.setStyle(Paint.Style.FILL_AND_STROKE);
        blue.setStyle(Paint.Style.FILL);
        cyan.setStyle(Paint.Style.FILL);
        mText.setStyle(Paint.Style.FILL);
        known.setStyle(Paint.Style.FILL);
        known2.setStyle(Paint.Style.FILL);
        wall.setStyle(Paint.Style.FILL);

        mText.setTextSize(250);
        known.setTextSize(50);
        known2.setTextSize(50);
        wall.setTextSize(50);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText("Map", 300, 300, mText);
        canvas.drawText("Visited Once",400,canvas.getHeight()-300,known);
        canvas.drawText("Visited Twice",395,canvas.getHeight()-200,known2);
        canvas.drawText("Obstacle/Wall",385,canvas.getHeight()-100,wall);

        for( int i=0;i<getLength();i++){
            for(int j=0;j<getWidth2();j++){
                rect.set(j*canvas.getWidth()/getWidth2(),i*(canvas.getHeight()/(getLength()*2))+(canvas.getHeight()/(getLength()))+(canvas.getHeight()/(getLength()*2)),(j+1)*canvas.getWidth()/getWidth2(),(i+1)*canvas.getHeight()/(getLength()*2)+(canvas.getHeight()/(getLength()))+(canvas.getHeight()/(getLength()*2)));
                if(getRoom()[i][j] == 'w')
                    canvas.drawRect(rect,gray);
                else if(getRoom()[i][j] == 'k')
                    canvas.drawRect(rect,cyan);
                else if(getRoom()[i][j] == 'z')
                    canvas.drawRect(rect,blue);
            }
        }
    }
}
