package com.itgao.bookshelf.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itgao.bookshelf.model.Chapter;
import com.itgao.bookshelf.model.Novel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class NovelDB {
    public static final String DB_NAME = "novel";

    public static final int VERSION = 1;
    private static NovelDB novelDB;
    private SQLiteDatabase db;

    private NovelDB(Context context){
        Helper helper = new Helper(context,DB_NAME,null,VERSION);
        if (context == null){
            Log.v("context","nill");
        }
        db = helper.getWritableDatabase();
    }

    public synchronized static NovelDB getInstance(Context context){
        if(novelDB == null){
            novelDB = new NovelDB(context);
        }
        Log.v("novelDB",novelDB.toString());
        return novelDB;
    }


    public int saveNovel(Novel novels){
        int id = -1;
        if(novels!=null){
            ContentValues values = new ContentValues();
            values.put("novel_name",novels.getNovel_name());
            values.put("chapter_index",novels.getChapter_index());
            values.put("img_path",novels.getImg_path());
            values.put("max_chapter",novels.getMax_chapter());
            values.put("novel_url",novels.getNovel_url());
            values.put("max_length",novels.getMax_length());
            values.put("is_net",novels.getIs_net());
            db.insert("Novel",null,values);
            Cursor cursor = db.rawQuery("select last_insert_rowid() from Novel",null);

            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        return id;
    }

    public void novel_delete(int id){
        db.delete("Novel","id=?",new String[]{String.valueOf(id)});
    }

    public void updateNovel(Novel novels){
        ContentValues values = new ContentValues();
        values.put("novel_name",novels.getNovel_name());
        values.put("chapter_index",novels.getChapter_index());
        values.put("img_path",novels.getImg_path());
        values.put("max_chapter",novels.getMax_chapter());
        values.put("novel_url",novels.getNovel_url());
        values.put("max_length",novels.getMax_length());
        values.put("is_net",novels.getIs_net());
        db.update("Novel",values,"id = ?",new String[]{String.valueOf(novels.getId())});

    }
    public void update_max(int id,String max_chapter,int max_length){
        ContentValues values = new ContentValues();
        if(!"".equals(max_chapter)){
            values.put("max_chapter",max_chapter);
        }
        values.put("max_length",max_length);
        db.update("Novel",values,"id = ?",new String[]{String.valueOf(id)});

    }
    public void update_net(int id,int is_net){
        ContentValues values = new ContentValues();

        values.put("is_net",is_net);
        db.update("Novel",values,"id = ?",new String[]{String.valueOf(id)});

    }

    public void update_novel(int id,String novel){
        ContentValues values = new ContentValues();
        values.put("novel",novel);
        db.update("Chapter",values,"id = ?",new String[]{String.valueOf(id)});
    }


    public List<Novel> loadAllNovels(){
        List<Novel> list = new ArrayList<Novel>();
        Cursor cursor = db.query("Novel",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Novel novels = new Novel();
                novels.setId(cursor.getInt(cursor.getColumnIndex("id")));
                novels.setNovel_name(cursor.getString(cursor.getColumnIndex("novel_name")));
                novels.setChapter_index(cursor.getInt(cursor.getColumnIndex("chapter_index")));
                novels.setImg_path(cursor.getString(cursor.getColumnIndex("img_path")));
                novels.setMax_chapter(cursor.getString(cursor.getColumnIndex("max_chapter")));
                novels.setNovel_url(cursor.getString(cursor.getColumnIndex("novel_url")));
                novels.setMax_length(cursor.getInt(cursor.getColumnIndex("max_length")));
                novels.setIs_net(cursor.getInt(cursor.getColumnIndex("is_net")));
                list.add(novels);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }


    public void saveChapter(Chapter chapters){
        if(chapters!=null){
            ContentValues values = new ContentValues();
            values.put("chapter_name",chapters.getChapter_name());
            values.put("novel",chapters.getNovel());
            values.put("text_url",chapters.getText_url());
            values.put("novel_id",chapters.getNovel_id());
            values.put("now_index",chapters.getNow_index());
            db.insert("Chapter",null,values);
        }
    }
    public void chapter_delete(int novel_id){
        db.delete("Chapter","novel_id=?",new String[]{String.valueOf(novel_id)});
    }
    public List<Chapter> loadAllChapters(int novelId){
        List<Chapter> list = new ArrayList<Chapter>();
        Cursor cursor = db.query("Chapter",null,"novel_id=?",new String[]{String.valueOf(novelId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Chapter chapters = new Chapter();
                chapters.setId(cursor.getInt(cursor.getColumnIndex("id")));
                chapters.setChapter_name(cursor.getString(cursor.getColumnIndex("chapter_name")));
                chapters.setNovel(cursor.getString(cursor.getColumnIndex("novel")));
                chapters.setText_url(cursor.getString(cursor.getColumnIndex("text_url")));
                chapters.setNovel_id(cursor.getInt(cursor.getColumnIndex("novel_id")));
                chapters.setNow_index(cursor.getInt(cursor.getColumnIndex("now_index")));
                list.add(chapters);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
