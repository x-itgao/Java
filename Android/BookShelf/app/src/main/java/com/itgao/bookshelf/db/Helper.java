package com.itgao.bookshelf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class Helper extends SQLiteOpenHelper {

    private Context m_context;
    private static final String CREATE_NOVEL = "create table Novel(" +
            "id integer primary key autoincrement," +
            "novel_name text," +
            "chapter_index integer," +
            "max_chapter text," +
            "img_path text," +
            "novel_url text," +
            "max_length integer，" +
            "author text)";
    private static final String CREATE_CHAPTER = "create table Chapter(" +
            "id integer primary key autoincrement," +
            "chapter_name text," +
            "now_index integer," +
            "text_url text," +
            "novel longtext," +
            "novel_id integer)";



    public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.m_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CHAPTER);
        sqLiteDatabase.execSQL(CREATE_NOVEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
