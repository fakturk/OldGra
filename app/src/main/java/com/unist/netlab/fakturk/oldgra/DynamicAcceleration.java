package com.unist.netlab.fakturk.oldgra;

/**
 * Created by fakturk on 9/8/16.
 * this class seperate dynamic and static acceleration
 */

class DynamicAcceleration
{
    private float[] oldAcc,oldGyr, dynAcc;
    private long timeInMillis;

    DynamicAcceleration()
    {
        timeInMillis = 10;
        oldAcc = new float[]{0.0f, 0.0f, 0.0f};
        oldGyr = new float[]{0.0f, 0.0f, 0.0f};
        dynAcc = new float[]{0.0f, 0.0f, 0.0f};

    }
    public DynamicAcceleration(long time)
    {
        timeInMillis = time;
        oldAcc = new float[]{0.0f, 0.0f, 0.0f};
        oldGyr = new float[]{0.0f, 0.0f, 0.0f};
        dynAcc = new float[]{0.0f, 0.0f, 0.0f};

    }

    //takes acc and gyr values and seperate dynamic acceleration from gravity
    private float[] dynamicAccDiff(float[] acc, float[] gyr,float[] gravity, float[] oldAcc, float[] oldGyr)
    {
        float[] dynAccDiff = new float[]{0.0f, 0.0f, 0.0f};
        float[] accDiff = accDiff(oldAcc, acc);
        float[] gyrDiff = gyrDiff(oldGyr, gyr);
        float[] graDiff = graDiff(gravity, gyrDiff);

        for (int i = 0; i < 3; i++)
        {
            dynAccDiff[i] = accDiff[i]-graDiff[i];
        }

        return dynAccDiff;

    }

    private float[] dynamicAcc(float[] oldDynamicAcc, float[] dynAccDiff)
    {
        float[] dynAcc= new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++)
        {
            dynAcc[i] = oldDynamicAcc[i] + dynAccDiff[i];
        }

        return dynAcc;
    }

    //find acceleration difference d_t
    private float[] accDiff(float[] oldAcc, float[] newAcc)
    {
        float[] diffAcc = new float[]{0.0f, 0.0f, 0.0f};

        for (int i = 0; i < 3; i++)
        {
            diffAcc[i] = (newAcc[i]-oldAcc[i]);
        }
        return diffAcc;
    }

    //find acceleration difference d_t
    private float[] gyrDiff(float[] oldGyr, float[] newGyr)
    {
        float[] diffGyr = new float[]{0.0f, 0.0f, 0.0f};

        for (int i = 0; i < 3; i++)
        {
            diffGyr[i] = (newGyr[i]-oldGyr[i]);
        }
        return diffGyr;
    }

    float[] eulerAngles(float[] gyrDiff)
    {
        float euler[] = new float[3];
        for (int i = 0; i < 3; i++) {
            euler[i] = gyrDiff[i] * timeInMillis;
        }
        return euler;
    }

    private float[] graDiff(float[] gravity,float[] gyrDiff)
    {
        float[] diffGra = new float[]{0.0f, 0.0f, 0.0f};
        float[] newGravity =gravityFromRotation(gravity, gyrDiff);

        for (int i = 0; i < 3; i++)
        {
            diffGra[i] = newGravity[i] - gravity[i];
        }
//
//        diffGra[0] = (float) Math.cos(gyrDiff[1]*timeInMillis)* GRAVITY_EARTH;
//        diffGra[1] = (float) Math.sin(gyrDiff[0]*timeInMillis)* GRAVITY_EARTH;
//        diffGra[2] = (float) ((Math.sin(gyrDiff[1]*timeInMillis)*Math.cos(gyrDiff[0]*timeInMillis)-1)* GRAVITY_EARTH);

        return  diffGra;
    }

    float[] gravityFromRotation(float[] gravity, float[] gyrDiff)
    {
        float[] euler = eulerAngles(gyrDiff);
        Gravity g = new Gravity();
        Orientation orientation = new Orientation();
        float[] newGravity = g.gravityAfterRotation(gravity, orientation.rotationFromEuler(euler));

        return newGravity;
    }

    private float[] velocity(float[] oldVelocity, float[] dynAcc)
    {
        float[] velocity = new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            velocity[i] = oldVelocity[i]+dynAcc[i];
        }
        return velocity;
    }

    private float[] distance(float[] oldDistance, float[] velocity)
    {
        float[] distance = new float[]{0.0f, 0.0f, 0.0f};
        for (int i = 0; i < 3; i++) {
            distance[i] = oldDistance[i]+velocity[i];
        }
        return distance;
    }

    // calculates and return dynamic acceleration, velocity and distance
    float[] calculate(float[] acc, float[] oldAcc, float[] gyr , float[] oldGyr,float[] oldGra, float[] oldAccVelDisGra)
    {
//        System.out.println("inside method acc:"+acc[0]+", "+acc[1]+", "+acc[2]+", oldAcc:"+oldAcc[0]+", "+oldAcc[1]+", "+oldAcc[2]);
//        System.out.println("inside method gravity:"+oldGra[0]+", "+oldGra[1]+", "+oldGra[2]);
        if (oldAccVelDisGra==null)
        {
            oldAccVelDisGra= new float[9];
            for (int i = 0; i <9; i++) {
                oldAccVelDisGra[i]=0.0f;
            }
        }
        float[] oldDynamicAcc = new float[]{0.0f, 0.0f, 0.0f};
        float[] oldVelocity = new float[]{0.0f, 0.0f, 0.0f};
        float[] oldDistance = new float[]{0.0f, 0.0f, 0.0f};
        float[] newGra = gravityFromRotation(oldGra, gyrDiff(oldGyr,gyr));

        for (int i = 0; i < 3; i++) {
            oldDynamicAcc[i] = oldAccVelDisGra[i];
            oldVelocity[i] = oldAccVelDisGra[i+3];
            oldDistance[i] = oldAccVelDisGra[i+6];
        }
        float[] accVelDisGra = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] dynAccDiff = dynamicAccDiff(acc,gyr,oldGra,oldAcc,oldGyr);
        float[] dynamicAcc = dynamicAcc(oldDynamicAcc, dynAccDiff);
        float[] velocity = velocity(oldVelocity,dynamicAcc);
        float[] distance = distance(oldDistance, velocity);
        for (int i = 0; i < 3; i++) {
            accVelDisGra[i]=dynamicAcc[i];
            accVelDisGra[i+3]= velocity[i];
            accVelDisGra[i+6]=distance[i];
            accVelDisGra[i+9]=newGra[i];
        }
        return accVelDisGra;
    }

}
