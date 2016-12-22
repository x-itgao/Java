package com.itgao.bookshelf.adapter;

import android.content.Context;
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
import java.util.List;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class NovelAdapter extends ArrayAdapter<Novel>{

    private int resource_id;
    private ImageView novel_img;
    private TextView novel_update;

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
        String img_path = novel.getImg_path();
        if (!TextUtils.isEmpty(img_path)){
            File file = new File(img_path);
            novel_img.setImageURI(Uri.fromFile(file));
        }else{
            // 设置一张默认的背景图片
        }
        Log.v("zuixin","zhuxom"+novel.getMax_chapter());
        novel_update.setText(novel.getMax_chapter());
        return view;
    }
}
