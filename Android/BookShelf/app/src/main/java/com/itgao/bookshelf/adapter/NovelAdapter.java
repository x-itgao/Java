package com.itgao.bookshelf.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itgao.bookshelf.R;
import com.itgao.bookshelf.model.Novel;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class NovelAdapter extends ArrayAdapter<Novel>{

    private int resource_id;
    private ImageView novel_img;
    private TextView novel_update;
    private TextView novel_name;

    public NovelAdapter(Context context, int resource, List<Novel> objects) {
        super(context, resource, objects);
        this.resource_id = resource;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    novel_img.setImageBitmap((Bitmap) msg.obj);
                    break;
            }

        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Novel novel = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resource_id,null);
        novel_img = (ImageView) view.findViewById(R.id.novel_img);
        novel_update = (TextView) view.findViewById(R.id.novel_item_update);
        novel_name = (TextView) view.findViewById(R.id.novel_name);
        String img_path = novel.getImg_path();
        if (!TextUtils.isEmpty(img_path)){
            if(img_path.startsWith("http")){
                getHttpBitMap(img_path);
            }else {
                Bitmap bitmap = getLocalBitmap(img_path);
                novel_img.setImageBitmap(bitmap);
                novel_img.setBackgroundResource(R.drawable.nopic);
            }
        }else{
            // 设置一张默认的背景图片
        }
        novel_name.setText(novel.getNovel_name());
        novel_update.setText(novel.getMax_chapter());
        return view;
    }

    public void getHttpBitMap(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                Bitmap bitmap = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setConnectTimeout(0);
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }).start();

    }


   private Bitmap getLocalBitmap(String url){
       try {
           File file = new File(url);
           FileInputStream fis = new FileInputStream(file);
           return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

       } catch (FileNotFoundException e) {
           e.printStackTrace();
           return null;
       }
   }
}
