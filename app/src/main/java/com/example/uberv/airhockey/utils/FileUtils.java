package com.example.uberv.airhockey.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     *  прочитать raw-ресурс по ид и вернёть его содержимое ввиде строки
     * @param context
     * @param resourceId
     * @return
     */
    public static String readTextFromRaw(Context context, int resourceId) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = null;
            try {
                InputStream inputStream = context.getResources().openRawResource(resourceId);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\r\n");
                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (Resources.NotFoundException nfex) {
            nfex.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static float[] readVerticesFromRawObj(Context context, int resourceId){
        List<Float> vertices = new ArrayList<>();
        try {
            BufferedReader bufferedReader = null;
            try {
                InputStream inputStream = context.getResources().openRawResource(resourceId);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.startsWith("v ")) {
                        line = line.replaceAll("v\\s+", "");
                        String[] nums=line.split("\\s+");
                        for(String numStr:nums){
                            vertices.add(Float.parseFloat(numStr));
                        }

                    }
                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (Resources.NotFoundException nfex) {
            nfex.printStackTrace();
        }
        float[] floatVertices = new float[vertices.size()];
        for(int i =0;i<floatVertices.length;i++){
            floatVertices[i]=vertices.get(i);
        }
        return floatVertices;
    }

}