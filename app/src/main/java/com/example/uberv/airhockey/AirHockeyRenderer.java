package com.example.uberv.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.uberv.airhockey.utils.MatrixHelper;
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
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;


public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    public static final String LOG_TAG = AirHockeyRenderer.class.getSimpleName();

    /**
     * how many floating point values to use per vertex
     */
    public static final int POSITION_COMPONENT_COUNT = 2;
    // float has size of 4 bytes
    private static final int BYTES_PER_FLOAT = 4;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String A_COLOR = "a_Color";
    private int aColorLocation;
    public static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private static final int COLOR_COMPONENT_COUNT = 3;
    /**
     * how many bytes to skip per read (how many bytes are between each position/interval between each position/color)
     */
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final Context mContext;
    private int program;
    // class used to allocate data in the native memory
    private FloatBuffer vertexData;

    public AirHockeyRenderer(Context context) {
        mContext = context;
    }

    // map everything between left and right, bottom and top, and near and far into the range -1 to 1

    private void prepareData() {
        float[] vertices = {
                //Order of coordinates:X,Y,R,G,B
                //Triangle Fan
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                //Line1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,
                //Mallets
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
        };

        // настроить буффер
        vertexData = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT) // выделить блок памяти с нужным размером
                .order(ByteOrder.nativeOrder()) // порядок записи (так требует OpenGL)
                .asFloatBuffer();

        // загрузить вершины в нативную память (copy data from Dalvik memory to native memory)
        vertexData.put(vertices);
    }

    private void bindData() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int vertexShader = ShaderUtils.createShader(mContext, GL_VERTEX_SHADER, R.raw.simple_vertex_shader);
        int fragmentShader = ShaderUtils.createShader(mContext, GL_FRAGMENT_SHADER, R.raw.simple_fragment_shader);
        program = ShaderUtils.linkProgram(vertexShader, fragmentShader);

        if (ShaderUtils.validateProgram(program)) {
            Log.d(LOG_TAG, "program validated");
            glUseProgram(program);
        } else {
            Log.e(LOG_TAG, "program validation failed");
            return;
        }

        prepareData();

        // prepare transformation matrix (aspect ratio)
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        // get "a_Position" attribute id from our program
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        // set buffer's pointer to beginning
        vertexData.position(0);
        // tell OpenGL that it can find the data for a_Position in the buffer vertexData
        glVertexAttribPointer(aPositionLocation,
                POSITION_COMPONENT_COUNT, // data count per attribute
                GL_FLOAT, // type of data
                false,
                STRIDE,  // stride (distance between each vertice, how many bytes to skip to get to next element)
                vertexData); // buffer
        // configure a_Color attribute
        vertexData.position(POSITION_COMPONENT_COUNT); // skip first n vertice components (start from color)
        // specify data for a_Color
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        // enable these attributes
        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        /* orthographic matrix (maintain aspect ratio)
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait/Square
           orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
        */

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//        perspectiveM(projectionMatrix,0,45,(float)width/(float)height,1f,10f);

        setIdentityM(modelMatrix, 0);

        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix,0,-60f,1f,0f,0f);
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // bind matrix
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // starting from 6th vertice, draw 2 vertices as lines
        glDrawArrays(GL_LINES, 6, 2);

        // Draw the first mallet blue.
        glDrawArrays(GL_POINTS, 8, 1);
        // Draw the second mallet red.
        // glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f); we use attributes now
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
