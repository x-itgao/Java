package com.itgao.bookshelf.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/9/26.
 */
public class StreamTool {

    public static byte[] read(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);

        }
        inputStream.close();
        return outputStream.toByteArray();
    }

    public static byte[] getHtml(String url) {
        HttpURLConnection connection = null;
        int code = 0;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            String text = "";Log.v("data","om");
            if ((code=connection.getResponseCode()) == 200 || code==304)  {
                InputStream inputStream = connection.getInputStream();
                byte[] data = read(inputStream);

                return data;

            }

        } catch (Exception e) {
            Log.v("linenum","47");
            e.printStackTrace();
        }
        Log.v("data","code"+code);
        return  "error".getBytes();

    }
    public static void getImage(String url,File file){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            if(connection.getResponseCode()==200){
                InputStream in = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=in.read(bytes))!=-1){
                    outputStream.write(bytes,0,len);
                }
                in.close();
                outputStream.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }
}
