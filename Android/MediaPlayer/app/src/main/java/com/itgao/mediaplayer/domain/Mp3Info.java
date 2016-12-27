package com.itgao.mediaplayer.domain;

/**
 * Created by xiaogao.XU on 2016/12/26.
 */
public class Mp3Info {
    private long id;
    private String title; // 歌曲名称
    private String album; // 专辑
    private Long albumId; // 专辑ID
    private String displayName; // 显示名称
    private String artist; // 歌手名
    private long duration ; // 歌曲时长
    private long size; // 歌曲大小
    private String url; // 歌曲路径
    private String lrcTitle; // 歌词名称
    private String lrcSuze; // 歌词大小

    @Override
    public String toString() {
        return "Mp3Info{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", displayName='" + displayName + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", lrcTitle='" + lrcTitle + '\'' +
                ", lrcSuze='" + lrcSuze + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLrcTitle() {
        return lrcTitle;
    }

    public void setLrcTitle(String lrcTitle) {
        this.lrcTitle = lrcTitle;
    }

    public String getLrcSuze() {
        return lrcSuze;
    }

    public void setLrcSuze(String lrcSuze) {
        this.lrcSuze = lrcSuze;
    }
}
