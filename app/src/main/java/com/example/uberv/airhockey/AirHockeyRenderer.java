package com.example.uberv.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.uberv.airhockey.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;


public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    public static final String LOG_TAG = AirHockeyRenderer.class.getSimpleName();

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private final Context mContext;
    private int program;

    /** how many floating point values to use per vertex */
    public static final int POSITION_COMPONENT_COUNT=2;
    // float has size of 4 bytes
    private static final int BYTES_PER_FLOAT = 4;
    // class used to allocate data in the native memory
    private FloatBuffer vertexData;

    public AirHockeyRenderer(Context context) {
        mContext=context;
    }

    private void prepareData(){
        float[] vertices = {
                // Triangle 1
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,
                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,
                // Line 1
                -0.5f, 0f,
                0.5f, 0f,
                // Mallets
                0f, -0.25f,
                0f, 0.25f
        };

        // настроить буффер
        vertexData = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT) // выделить блок памяти с нужным размером
                .order(ByteOrder.nativeOrder()) // порядок записи (так требует OpenGL)
                .asFloatBuffer();

        // загрузить вершины в нативную память (copy data from Dalvik memory to native memory)
        vertexData.put(vertices);
    }

    private void bindData(){

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int vertexShader = ShaderUtils.createShader(mContext,GL_VERTEX_SHADER,R.raw.simple_vertex_shader);
        int fragmentShader = ShaderUtils.createShader(mContext,GL_FRAGMENT_SHADER,R.raw.simple_fragment_shader);
        program=ShaderUtils.linkProgram(vertexShader,fragmentShader);
        if(ShaderUtils.validateProgram(program)){
            glUseProgram(program);
        }else{
            Log.e(LOG_TAG,"program validation failed");
            return;
        }

        prepareData();

        // get "u_Color" uniform id from our program
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        // set buffer's pointer to beginning
        vertexData.position(0);
        // tell OpenGL that it can find the data for a_Position in the buffer vertexData
        glVertexAttribPointer(aPositionLocation,
                POSITION_COMPONENT_COUNT, // data count per attribute
                GL_FLOAT, // type of data
                false,
                0,  // . . .
                vertexData); // buffer
        // enable this attribute
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);


        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6); // last attribute - how manye vertices to use

        // starting from 6th vertice, draw 2 vertices as lines
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw the first mallet blue.
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
        // Draw the second mallet red.
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
