package com.unist.netlab.fakturk.oldgra;

/**
 * Created by fakturk on 16. 10. 5.
 */

public class Orientation
{
    float[][] rotationFromEuler(float[] euler)
    {
        float alpha = euler[0];
        float beta = euler[1];
        float theta = euler[2];

        float[][] R = new float[3][3];
        R[0][0] = (float) (Math.cos(theta) * Math.cos(beta));
        R[0][1] = (float) (-Math.sin(theta) * Math.cos(alpha) + Math.cos(theta) * Math.sin(beta) * Math.sin(alpha));
        R[0][2] = (float) (Math.sin(theta) * Math.sin(alpha) + Math.cos(theta) * Math.sin(beta) * Math.cos(alpha));

        R[1][0] = (float) (Math.sin(theta) * Math.cos(beta));
        R[1][1] = (float) (Math.cos(theta) * Math.cos(alpha) + Math.sin(theta) * Math.sin(beta) * Math.sin(alpha));
        R[1][2] = (float) (-Math.cos(theta) * Math.sin(alpha) + Math.sin(theta) * Math.sin(beta) * Math.cos(alpha));

        R[2][0] = (float) -Math.sin(beta);
        R[2][1] = (float) (Math.cos(beta) * Math.sin(alpha));
        R[2][2] = (float) (Math.cos(beta) * Math.cos(alpha));

        return R;


    }

    float[][] linearizedRotationFromEuler(float[] euler)
    {
        float alpha = euler[0];
        float beta = euler[1];
        float theta = euler[2];

        float[][] R = new float[3][3];
        R[0][0] =  (1);
        R[0][1] =  (-theta  + beta*alpha);
        R[0][2] =  theta * alpha + beta;

        R[1][0] =  theta;
        R[1][1] =  1+ theta * beta*alpha;
        R[1][2] =  -alpha + theta *beta;

        R[2][0] =  -beta;
        R[2][1] =  alpha;
        R[2][2] =  1;

        return R;


    }

}
