package com.itgao.mediaplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itgao.mediaplayer.R;
import com.itgao.mediaplayer.activity.MainActivity;
import com.itgao.mediaplayer.domain.Mp3Info;

import java.util.List;

/**
 * Created by xiaogao.XU on 2016/12/28.
 */
public class SearchListAdapter extends BaseAdapter{
    private Context context;
    private Mp3Info mp3Info;
    private List<Mp3Info> mp3InfoList;
    int pos = -1;

    public SearchListAdapter(Context context, List<Mp3Info> mp3InfoList) {
        this.context = context;
        this.mp3InfoList = mp3InfoList;
    }

    @Override
    public int getCount() {
        return mp3InfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_list_item, null);
            viewHolder.albumImage = (ImageView) convertView.findViewById(R.id.album_image);
            viewHolder.musicName = (TextView) convertView.findViewById(R.id.music_name);
            viewHolder.musicArtist = (TextView) convertView.findViewById(R.id.music_author);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        mp3Info = mp3InfoList.get(position);
        if(position == pos) {
            viewHolder.albumImage.setImageResource(R.drawable.item);
        }

        viewHolder.musicArtist.setText(mp3Info.getArtist());
        viewHolder.musicName.setText(mp3Info.getTitle());

        return convertView;

    }


    public class ViewHolder {

        public ImageView albumImage;
        public TextView musicName;
        public TextView musicArtist;
    }
}
