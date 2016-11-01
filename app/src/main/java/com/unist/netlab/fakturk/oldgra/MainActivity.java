package com.unist.netlab.fakturk.oldgra;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import static android.hardware.SensorManager.GRAVITY_EARTH;

public class MainActivity extends AppCompatActivity {
    DecimalFormat df = new DecimalFormat("#.##");

    TextView tv_gravity;
    Intent i;
    Button buttonStart, buttonGra, buttonAcc, buttonLinear, buttonGyr ;

    float[] acc, gyr, oldAcc, oldGyr, gravity, dynamicAcc;
    String acc_text, textProcessed;
    boolean start;

    DynamicAcceleration dynamic;

    ArrowView arrowView ;
    AccView accView;
    AccGraDiffView accGraDiffView;
    GyrView gyrView;
    LinearLayout linearLayoutHor1, linearLayoutHor2;
//    SurfaceHolder surfaceHolder;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrowView = (ArrowView) findViewById(R.id.arrowView);
        accView = (AccView) findViewById(R.id.accView);
        accGraDiffView = (AccGraDiffView) findViewById(R.id.accGraDiffView);
        gyrView = (GyrView) findViewById(R.id.gyrView);

        linearLayoutHor1 = (LinearLayout) findViewById(R.id.linearLayHor1);
        linearLayoutHor2 = (LinearLayout) findViewById(R.id.linearLayHor2);


        tv_gravity = (TextView) findViewById(R.id.tv_gravity);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonGra = (Button) findViewById(R.id.buttonGra);
        buttonAcc = (Button) findViewById(R.id.buttonAcc);
        buttonLinear = (Button) findViewById(R.id.buttonAccGraDiff);
        buttonGyr = (Button) findViewById(R.id.buttonGyr);
        i = new Intent(this, SensorService.class);

        acc = new float[3];
        gyr = new float[3];
        oldAcc = null;
        oldGyr = null;
        gravity = new float[3];
        dynamicAcc = null;

        dynamic = new DynamicAcceleration();

        start = false;

        arrowView.setLine(100,100,100);
        accView.setLine(200,200,200);






        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                acc = (intent.getFloatArrayExtra("ACC_DATA"));
                acc_text = intent.getStringExtra("ACC");
                gyr = intent.getFloatArrayExtra("GYR_DATA");

                if (acc!=null && oldAcc==null)
                {
                    oldAcc = new float[3];
                    System.out.println("case acc!=null && oldAcc==null");
                    System.arraycopy(acc, 0, oldAcc, 0,acc.length);

                }
                if (gyr!=null && oldGyr==null)
                {
                    oldGyr = new float[3];
                    System.out.println("case gyr!=null && oldGyr==null");
                    System.arraycopy(gyr,0,oldGyr,0,gyr.length);

                }
                if (acc==null && oldAcc!=null)
                {
                    acc = new float[3];
                    System.out.println("case acc==null");
                    System.arraycopy(oldAcc, 0, acc, 0,oldAcc.length);

                }
                if (gyr==null && oldGyr!=null)
                {
                    gyr = new float[3];
                    System.out.println("case gyr==null");
                    System.arraycopy(oldGyr,0,gyr,0,oldGyr.length);

                }


                if (acc!=null && gyr!=null)
                {
                    start = true;
                    float accNorm = (float) Math.sqrt(Math.pow(acc[0],2)+Math.pow(acc[1],2)+Math.pow(acc[2],2));
                    for (int j = 0; j < 3; j++)
                    {
                        gravity[j] = acc[j]*(GRAVITY_EARTH/accNorm);
                    }
                }
                if (start)
                {

                    dynamicAcc = dynamic.calculate(acc,oldAcc,gyr,oldGyr,gravity, dynamicAcc);
                    System.arraycopy(acc, 0, oldAcc, 0,acc.length);
                    System.arraycopy(gyr,0,oldGyr,0,gyr.length);
                    for (int j = 0; j < 3; j++) {
                        gravity[j] = dynamicAcc[j+9];
                    }
                    int lS = 20; // size coefficient of the line
                    arrowView.setLine((-1)*gravity[0]*lS,gravity[1]*lS,gravity[2]*lS);
                    accView.setLine((-1)*acc[0]*lS,acc[1]*lS, acc[2]*lS);
                    accGraDiffView.setLine((-1)*acc[0]*lS - (-1)*gravity[0]*lS,acc[1]*lS - gravity[1]*lS, acc[2]*lS - gravity[2]*lS);
                    gyrView.setLine(gyr[1]*lS,gyr[0]*lS, -1*gyr[2]*lS);
                    textProcessed =
                            "Acc : "+    df.format(dynamicAcc[0])+", "+df.format(dynamicAcc[1])+", "+df.format(dynamicAcc[2])+"\n"
                                    +"Vel : "+    df.format(dynamicAcc[3])+", "+df.format(dynamicAcc[4])+", "+df.format(dynamicAcc[5])+"\n"
                                    +"Dist : "+   df.format(dynamicAcc[6])+", "+df.format(dynamicAcc[7])+", "+df.format(dynamicAcc[8])+"\n"
                                    +"Gra : "+   df.format(dynamicAcc[9])+", "+df.format(dynamicAcc[10])+", "+df.format(dynamicAcc[11])+"\n"
                                    +"gyr : "+    df.format(gyr[0])+", "+df.format(gyr[1])+", "+df.format(gyr[2])+"\n"
                    ;
                    tv_gravity.setText(textProcessed);

                }
            }
        }, new IntentFilter(SensorService.ACTION_SENSOR_BROADCAST));


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStart.getText().equals("Start")) {
                    buttonStart.setText("Stop");
                    startService(new Intent(MainActivity.this, SensorService.class));

                } else {
                    buttonStart.setText("Start");
                    stopService(new Intent(MainActivity.this, SensorService.class));


                }
            }
        });
        buttonGra.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonGra.getText().equals("GRA")) {
                    buttonGra.setText("gra");
                    System.out.println("GRA");
                    makeBigger("GRA");



                } else {
                    buttonGra.setText("GRA");
                    System.out.println("gra");
                    makeSmaller("GRA");




                }
            }
        });

        buttonAcc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonAcc.getText().equals("ACC")) {
                    buttonAcc.setText("acc");
                    System.out.println("ACC");
                    makeBigger("ACC");


                } else {
                    buttonAcc.setText("ACC");
                    System.out.println("acc");
                    makeSmaller("ACC");




                }
            }
        });

        buttonLinear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonLinear.getText().equals("LINEAR")) {
                    buttonLinear.setText("linear");
                    System.out.println("LINEAR");
                    makeBigger("LINEAR");



                } else {
                    buttonLinear.setText("LINEAR");
                    System.out.println("linear");
                    makeSmaller("LINEAR");




                }
            }
        });

        buttonGyr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (buttonGyr.getText().equals("GYR")) {
                    buttonGyr.setText("gyr");
                    System.out.println("GYR");
                    makeBigger("GYR");


                } else {
                    buttonGyr.setText("GYR");
                    System.out.println("gyr");
                    makeSmaller("GYR");



                }
            }
        });

    }
    void makeBigger(String button)
    {
        if (button=="GRA")
        {
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*3/2));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*1/2));
            linearLayoutHor1.removeView(accView);
            linearLayoutHor2.addView(accView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*3/2,arrowView.getHeight()*3/2));
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*1/2,accView.getHeight()*1/2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*1/2,accGraDiffView.getHeight()*1/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*1/2,gyrView.getHeight()*1/2));
        }
        if (button=="ACC")
        {
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*3/2));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*1/2));
            linearLayoutHor1.removeView(arrowView);
            linearLayoutHor2.addView(arrowView);
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*3/2,accView.getHeight()*3/2));
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*1/2,arrowView.getHeight()*1/2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*1/2,accGraDiffView.getHeight()*1/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*1/2,gyrView.getHeight()*1/2));

        }
        if (button=="LINEAR")
        {
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*3/2));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*1/2));
            linearLayoutHor2.removeView(gyrView);
            linearLayoutHor1.addView(gyrView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*1/2,arrowView.getHeight()*1/2));
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*1/2,accView.getHeight()*1/2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*3/2,accGraDiffView.getHeight()*3/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*1/2,gyrView.getHeight()*1/2));
        }
        if (button=="GYR")
        {
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*3/2));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*1/2));
            linearLayoutHor2.removeView(accGraDiffView);
            linearLayoutHor1.addView(accGraDiffView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*1/2,arrowView.getHeight()*1/2));
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*1/2,accView.getHeight()*1/2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*1/2,accGraDiffView.getHeight()*1/2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*3/2,gyrView.getHeight()*3/2));

        }
    }

    void makeSmaller(String button)
    {
        if (button=="GRA")
        {
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2/3));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2));
            linearLayoutHor2.removeView(accView);
            linearLayoutHor1.addView(accView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2/3,arrowView.getHeight()*2/3));
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*2,accView.getHeight()*2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*2,accGraDiffView.getHeight()*2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2,gyrView.getHeight()*2));
        }
        if (button=="ACC")
        {
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2/3));
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2));
            linearLayoutHor2.removeView(arrowView);
            linearLayoutHor1.addView(arrowView);
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*2/3,accView.getHeight()*2/3));
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2,arrowView.getHeight()*2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*2,accGraDiffView.getHeight()*2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2,gyrView.getHeight()*2));
        }
        if (button=="LINEAR")
        {
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2/3));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2));
            linearLayoutHor1.removeView(gyrView);
            linearLayoutHor2.addView(gyrView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2,arrowView.getHeight()*2));
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*2,accView.getHeight()*2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*2/3,accGraDiffView.getHeight()*2/3));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2,gyrView.getHeight()*2));
        }
        if (button=="GYR")
        {
            linearLayoutHor2.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor2.getWidth(),linearLayoutHor2.getHeight()*2/3));
            linearLayoutHor1.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutHor1.getWidth(),linearLayoutHor1.getHeight()*2));
            linearLayoutHor1.removeView(accGraDiffView);
            linearLayoutHor2.addView(accGraDiffView);
            arrowView.setLayoutParams(new LinearLayout.LayoutParams(arrowView.getWidth()*2,arrowView.getHeight()*2));
            accView.setLayoutParams(new LinearLayout.LayoutParams(accView.getWidth()*2,accView.getHeight()*2));
            accGraDiffView.setLayoutParams(new LinearLayout.LayoutParams(accGraDiffView.getWidth()*2,accGraDiffView.getHeight()*2));
            gyrView.setLayoutParams(new LinearLayout.LayoutParams(gyrView.getWidth()*2/3,gyrView.getHeight()*2/3));

        }
    }



    @Override
    protected void onPause() {

        super.onPause();


    }


    @Override
    protected void onResume() {

        super.onResume();
//        startService(new Intent(this, SensorService.class));
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();
    }


    @Override
    public void onStart() {

        super.onStart();


    }

    @Override
    public void onStop() {

        super.onStop();


    }
}
