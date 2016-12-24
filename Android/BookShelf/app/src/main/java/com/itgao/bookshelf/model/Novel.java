package com.itgao.bookshelf.model;

import java.io.Serializable;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class Novel implements Serializable {

    private int id;
    private String novel_name;
    private int chapter_index;
    private String img_path;
    private String max_chapter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNovel_name() {
        return novel_name;
    }

    public void setNovel_name(String novel_name) {
        this.novel_name = novel_name;
    }

    public int getChapter_index() {
        return chapter_index;
    }

    public void setChapter_index(int chapter_index) {
        this.chapter_index = chapter_index;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getMax_chapter() {
        return max_chapter;
    }

    public void setMax_chapter(String max_chapter) {
        this.max_chapter = max_chapter;
    }

    @Override
    public String toString() {
        return "Novel{" +
                "id=" + id +
                ", novel_name='" + novel_name + '\'' +
                ", chapter_index=" + chapter_index +
                ", img_path='" + img_path + '\'' +
                ", max_chapter='" + max_chapter + '\'' +
                '}';
    }
}
