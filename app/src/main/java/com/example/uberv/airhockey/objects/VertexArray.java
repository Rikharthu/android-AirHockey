package com.example.uberv.airhockey.objects;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.uberv.airhockey.objects.Constants.BYTES_PER_FLOAT;

public class VertexArray {

    /** float buffer used to store vertex array dara in native code */
    private final FloatBuffer mFloatBuffer;


    public VertexArray(float[] vertexData) {
        mFloatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride){
        mFloatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation,componentCount,GL_FLOAT,false,stride,mFloatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        mFloatBuffer.position(0);
    }

}
