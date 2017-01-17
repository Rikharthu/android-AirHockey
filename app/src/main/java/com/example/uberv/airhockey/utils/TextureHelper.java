package com.example.uberv.airhockey.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;

public abstract class TextureHelper {
    public static final String LOG_TAG = TextureHelper.class.getSimpleName();


    /**
     * Will return the id of the loaded OpenGL texture
     */
    public static int loadTexture(Context context, int resourceId) {
        // 1 generate a new texture id
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(LOG_TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }

        // 2 read in image data
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // we need original image data
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.w(LOG_TAG, "Resource ID " + resourceId + " could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // 3 bind bitmap to the texture id
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // 4 configure texture
        // 4.1 filtering (how texture respond to changes in size)
        // set minification filter to trilinear filter
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR);
        // set magnification filter to bilinear
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
    }

}
