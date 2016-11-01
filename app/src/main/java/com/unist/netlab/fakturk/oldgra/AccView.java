package com.unist.netlab.fakturk.oldgra;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fakturk on 24/10/2016.
 */

public class AccView extends View
{
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintGreen= new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint thinLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintTextLarge = new Paint(Paint.ANTI_ALIAS_FLAG);

    float lineStartX, lineStartY, lineFinishX, lineFinishY, lineZStartX, lineZStartY, lineZFinishX, lineZFinishY;
    float accXFinish, accYFinish, accZFinish;




    public AccView(Context context)
    {

        super(context);

        init();

    }
    public AccView(Context context, AttributeSet attrs, int defStyle)
    {

        super(context, attrs, defStyle);


        init();

    }
    public AccView(Context context, AttributeSet attrs)
    {

        super(context, attrs);

        init();

    }



    @Override
    protected void onDraw(Canvas c)
    {
        super.onDraw(c);
        lineStartX= this.getWidth()/2;
        lineStartY = this.getHeight()/2;
        lineZStartX = this.getWidth()-50;
        lineZStartY = lineStartY;
        lineZFinishX = 0;


        //graph itself
        c.drawLine(lineStartX,0,lineStartX,this.getHeight(),thinLine); //yatay vertical
        c.drawLine(0,lineStartY,this.getWidth(),lineStartY,thinLine); //dikey horizontal
        c.drawLine(lineZStartX,0,lineZStartX,this.getHeight(),thinLine); //dikey Z horizontal

        c.drawLine(lineStartX,0,lineStartX+20,20,thinLine);
        c.drawLine(lineStartX,0,lineStartX-20,20,thinLine);
        c.drawLine(lineStartX,this.getHeight(),lineStartX+20,this.getHeight()-20,thinLine);
        c.drawLine(lineStartX,this.getHeight(),lineStartX-20,this.getHeight()-20,thinLine);
        c.drawText("y",lineStartX-50,50,paintTextLarge);
        c.drawText("-y",lineStartX-50,this.getHeight()-50,paintTextLarge);

        c.drawLine(0,lineStartY,20,lineStartY+20,thinLine);
        c.drawLine(0,lineStartY,20,lineStartY-20,thinLine);
        c.drawText("-x",20,lineStartY+50,paintTextLarge);

        c.drawLine(lineZStartX,0,lineZStartX+20,20,thinLine);
        c.drawLine(lineZStartX,0,lineZStartX-20,20,thinLine);
        c.drawLine(lineZStartX,this.getHeight(),lineZStartX+20,this.getHeight()-20,thinLine);
        c.drawLine(lineZStartX,this.getHeight(),lineZStartX-20,this.getHeight()-20,thinLine);
        c.drawText("z",lineZStartX-50,50,paintTextLarge);
        c.drawText("-z",lineZStartX-50,this.getHeight()-50,paintTextLarge);

        c.drawLine(lineStartX,lineStartY,lineStartX+accXFinish,lineStartY+accYFinish,paint);

        for (int i = 0; i <=20; i++)
        {
            c.drawLine(lineStartX-10,lineStartY+20*i-200,lineStartX+10,lineStartY+20*i-200,thinLine); //small ticks on y axis
            if (i-10!=0)
            {
                c.drawText(Integer.toString(10-i),lineStartX+30,lineStartY+20*i-200+5,paintText);
            }


            c.drawLine(lineZStartX-10,lineStartY+20*i-200,lineZStartX+10,lineStartY+20*i-200,thinLine);
            if (i-10!=0)
            {
                c.drawText(Integer.toString(10-i),lineZStartX+20,lineStartY+20*i-200+5,paintText);

            }

            c.drawLine(lineStartX+20*i-200, lineStartY-10,lineStartX+20*i-200, lineStartY+10,thinLine);
            if (i-10!=0)
            {
                c.drawText(Integer.toString(10-i),lineStartX+20*i-200,lineStartY+50,paintText);
            }

        }



        System.out.println("ondraw: "+lineStartX+", "+lineStartY+", "+lineFinishX+", "+lineFinishY);

        if (Math.abs(lineFinishX)>Math.abs(lineFinishY))
        {
            c.drawLine(lineStartX,lineStartY,lineStartX+lineFinishX,lineStartY+lineFinishY,paintGreen);


        }
        else
        {
            c.drawLine(lineStartX,lineStartY,lineStartX+lineFinishX,lineStartY+lineFinishY,paintBlue);
        }
        c.drawLine(lineZStartX,lineZStartY,lineZStartX+lineZFinishX,lineZStartY+lineZFinishY,paintRed);

    }

//

    void setLine(float lineFinishX, float lineFinishY, float lineFinishZ)
    {


        this.lineFinishX = lineFinishX;
        this.lineFinishY = lineFinishY;
        this.lineZFinishY = lineFinishZ;
        System.out.println("setLine: "+lineStartX+", "+lineStartY+", "+lineFinishX+", "+lineFinishY);
        invalidate();

        System.out.println("setLine after invalidate: "+lineStartX+", "+lineStartY+", "+lineFinishX+", "+lineFinishY);

    }
    void setAccLine(float accXFinish, float accYFinish, float accZFinish)
    {
        this.accXFinish = accXFinish;
        this.accYFinish = accYFinish;
        this.accZFinish = accZFinish;
    }

    void init()
    {


        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);


        paintText.setColor(Color.BLACK);
        paintText.setStyle(Paint.Style.STROKE);
        paintText.setTextSize(22);

        paintTextLarge.setColor(Color.BLACK);
//        paintText.setStyle(Paint.Style.STROKE);
        paintTextLarge.setTextSize(36);


        thinLine.setColor(Color.BLACK);
        thinLine.setStyle(Paint.Style.STROKE);
        thinLine.setStrokeWidth(1);

        paintGreen.setColor(Color.GREEN);
        paintGreen.setStyle(Paint.Style.STROKE);
        paintGreen.setStrokeWidth(8);

        paintRed.setColor(Color.RED);
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setStrokeWidth(8);

        paintBlue.setColor(Color.BLUE);
        paintBlue.setStyle(Paint.Style.STROKE);
        paintBlue.setStrokeWidth(8);

        lineStartX=0;
        lineStartY=0;
        lineFinishX=0;
        lineFinishY=0;

    }

}
