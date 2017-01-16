package com.example.uberv.airhockey.utils;


public class MatrixHelper {

    /** frustumM has a bug, but perspectiveM is not available on all API versions
     * this method is similar to perspectiveM */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f){
        // convert FOV to radians
        final float angleInRadians=(float)(yFovInDegrees*Math.PI/180.0);
        // calculate focal length
        final float a=(float)(1.0/Math.tan(angleInRadians/2.0));

        // construct our projection matrix
        m[0]=a/aspect;
        m[1]=0f;
        m[2]=0f;
        m[3]=0f;

        m[4]=0f;
        m[5]=a;
        m[6]=0f;
        m[7]=0f;

        m[8]=0f;
        m[9]=0f;
        m[10]=-((f+n)/(f-n));
        m[11]=-1f;

        m[12]=0f;
        m[13]=0f;
        m[14]=-((2f*f*n)/(f-n));
        m[15]=0f;

    }

}
