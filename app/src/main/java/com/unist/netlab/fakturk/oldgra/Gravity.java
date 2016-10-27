package com.unist.netlab.fakturk.oldgra;

import static android.hardware.SensorManager.GRAVITY_EARTH;

/**
 * Created by fakturk on 16. 5. 2.
 */
public class Gravity {

    public Gravity() {
    }



    float[] gravityAfterRotation(float[] gravity, float[][] rotationMatrix)
    {
        float[] newGravity = new float[3];
        for (int i = 0; i < 3; i++) {
            newGravity[i] = gravity[0]* rotationMatrix[i][0]+ gravity[1]* rotationMatrix[i][1]+gravity[2]* rotationMatrix[i][2];
        }
        float graNorm = (float) Math.sqrt(Math.pow(newGravity[0],2)+ Math.pow(newGravity[1],2)+ Math.pow(newGravity[2],2));
        for (int j = 0; j < 3; j++)
        {
            newGravity[j] = newGravity[j]*(GRAVITY_EARTH/graNorm);
        }

        return newGravity;
    }

}
