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
import android.widget.TextView;

import java.text.DecimalFormat;

import static android.hardware.SensorManager.GRAVITY_EARTH;

public class MainActivity extends AppCompatActivity {
    DecimalFormat df = new DecimalFormat("#.##");

    TextView tv_gravity;
    Intent i;
    Button buttonStart;

    float[] acc, gyr, oldAcc, oldGyr, gravity, dynamicAcc;
    String acc_text, textProcessed;
    boolean start;

    DynamicAcceleration dynamic;

    ArrowView arrowView ;
    AccView accView;
    AccGraDiffView accGraDiffView;
    GyrView gyrView;
//    SurfaceHolder surfaceHolder;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrowView = (ArrowView) findViewById(R.id.arrowView);
        accView = (AccView) findViewById(R.id.accView);
        accGraDiffView = (AccGraDiffView) findViewById(R.id.accGraDiffView);
        gyrView = (GyrView) findViewById(R.id.gyrView);
//        surfaceHolder = arrowView.getHolder();
//        surfaceHolder.addCallback(arrowView);


        tv_gravity = (TextView) findViewById(R.id.tv_gravity);
        buttonStart = (Button) findViewById(R.id.buttonStart);
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
