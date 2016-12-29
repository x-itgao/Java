package com.itgao.mediaplayer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itgao.mediaplayer.domain.Mp3Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaogao.XU on 2016/12/29.
 */
public class Mp3DB {
    public static final String DB_NAME = "Mp3";

    public static final int VERSION = 1;
    private static Mp3DB mp3DB;
    private SQLiteDatabase db;
    private Mp3DB(Context context){
        Helper helper = new Helper(context,DB_NAME,null,VERSION);
        if (context == null){
            Log.v("context","nill");
        }
        db = helper.getWritableDatabase();
    }
    public synchronized static Mp3DB getInstance(Context context){
        if(mp3DB == null){
            mp3DB = new Mp3DB(context);
        }
        Log.v("novelDB",mp3DB.toString());
        return mp3DB;
    }

    public void saveMp3s(Mp3Info mp3Info){
        if(mp3Info != null){
            ContentValues values = new ContentValues();
            values.put("id",mp3Info.getId());
            values.put("album",mp3Info.getAlbum());
            values.put("title",mp3Info.getTitle());
            values.put("duration",mp3Info.getDuration());
            values.put("artist",mp3Info.getArtist());
            values.put("size",mp3Info.getSize());
            values.put("url",mp3Info.getUrl());
            db.insert("Mp3",null,values);
        }
    }
    public List<Mp3Info> loadAll(){
        List<Mp3Info> list = new ArrayList<Mp3Info>();
        Cursor cursor = db.query("Mp3",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Mp3Info mp3Info = new Mp3Info();
                mp3Info.setId(cursor.getLong(cursor.getColumnIndex("id")));
                mp3Info.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
                mp3Info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                mp3Info.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                mp3Info.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                mp3Info.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
                mp3Info.setSize(cursor.getLong(cursor.getColumnIndex("size")));
                list.add(mp3Info);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
