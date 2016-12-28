package com.itgao.bookshelf.model;

import java.io.Serializable;

/**
 * Created by xiaogao.XU on 2016/12/22.
 */
public class Novel implements Serializable {

    private int id;
    private String novel_name; // 小说名
    private int chapter_index; // 当前阅读的章节数
    private String img_path; // 封面路径
    private String max_chapter; // 最新章节名
    private String novel_url; // 小说主页URL
    private int max_length; // 章节数
    private int is_net; // 作者

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

    public String getNovel_url() {
        return novel_url;
    }

    public void setNovel_url(String novel_url) {
        this.novel_url = novel_url;
    }

    public int getMax_length() {
        return max_length;
    }

    public void setMax_length(int max_length) {
        this.max_length = max_length;
    }

    public int getIs_net() {
        return is_net;
    }

    public void setIs_net(int is_net) {
        this.is_net = is_net;
    }

    @Override
    public String toString() {
        return "Novel{" +
                "id=" + id +
                ", novel_name='" + novel_name + '\'' +
                ", chapter_index=" + chapter_index +
                ", img_path='" + img_path + '\'' +
                ", max_chapter='" + max_chapter + '\'' +
                ", novel_url='" + novel_url + '\'' +
                ", max_length=" + max_length +
                ", is_net=" + is_net +
                '}';
    }
}
