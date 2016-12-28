package com.itgao.mediaplayer.domain;

import java.io.Serializable;

/**
 * Created by xiaogao.XU on 2016/12/28.
 */
public class LrcContent implements Serializable{
    private String lrcStr;
    private int lrcTime;

    public String getLrcStr() {
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }

    @Override
    public String toString() {
        return "LrcContent{" +
                "lrcStr='" + lrcStr + '\'' +
                ", lrcTime=" + lrcTime +
                '}';
    }
}
