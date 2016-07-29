package com.wjustudio.mobileplayer.Bean;

/**
 * 歌词对应的bean
 * 作者： songwenju on 2016/7/27 08:09.
 * 邮箱： songwenju@outlook.com
 */
public class Lyric implements Comparable<Lyric>{
    public int startPoint;
    public String content;

    public Lyric(int startPoint, String content) {
        this.startPoint = startPoint;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "startPoint='" + startPoint + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int compareTo(Lyric another) {
        //我的的值比别人小，在前面
        return startPoint - another.startPoint;
    }
}
