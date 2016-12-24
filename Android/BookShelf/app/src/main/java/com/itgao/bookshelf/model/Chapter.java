package com.itgao.bookshelf.model;

import java.io.Serializable;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class Chapter implements Serializable{
    private int id;
    private int novel_id;
    private int now_index;
    private String chapter_name;
    private String text_url;
    private String novel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(int novel_id) {
        this.novel_id = novel_id;
    }

    public int getNow_index() {
        return now_index;
    }

    public void setNow_index(int now_index) {
        this.now_index = now_index;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getText_url() {
        return text_url;
    }

    public void setText_url(String text_url) {
        this.text_url = text_url;
    }

    public String getNovel() {
        return novel;
    }

    public void setNovel(String text) {
        this.novel = text;

    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id=" + id +
                ", novel_id=" + novel_id +
                ", now_index=" + now_index +
                ", chapter_name='" + chapter_name + '\'' +
                ", text_url='" + text_url + '\'' +
                ", novel='" + novel + '\'' +
                '}';
    }
}
