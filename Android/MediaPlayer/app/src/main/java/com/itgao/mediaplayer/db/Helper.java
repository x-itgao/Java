package com.itgao.mediaplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaogao.XU on 2016/12/29.
 */
public class Helper extends SQLiteOpenHelper{
    private Context m_context;
    private static final String CREATE_MP3 = "create table Mp3(" +
            "id long primary key," +
            "album text," +
            "artist text,"+
            "title text," +
            "duration long," +
            "size long," +
            "url text)";
    public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.m_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MP3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
