package com.itgao.bookshelf.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import java.io.File;
import java.io.InputStream;
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
                Drawable drawable =LoadImageFromWebOperations(img_path);
                novel_img.setImageDrawable(drawable);
            }else {
                File file = new File(img_path);

                novel_img.setImageURI(Uri.fromFile(file));
            }

        }else{
            // 设置一张默认的背景图片
        }
        novel_name.setText(novel.getNovel_name());
        novel_update.setText(novel.getMax_chapter());
        return view;
    }


    private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }
}
