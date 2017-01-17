package com.example.uberv.airhockey.programs;

import android.content.Context;
import android.graphics.Shader;

import com.example.uberv.airhockey.utils.FileUtils;
import com.example.uberv.airhockey.utils.ShaderUtils;

import static android.opengl.GLES20.glUseProgram;

public class ShaderProgram {
    
    // Uniform constants
    protected static final String U_COLOR = "u_Color";
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected final int program;


    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderUtils.buildProgram(
                FileUtils.readTextFromRaw(context, vertexShaderResourceId),
                FileUtils.readTextFromRaw(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }


}
