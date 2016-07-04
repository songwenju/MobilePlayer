package com.wjustudio.mobileplayer.Bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * 作者： songwenju on 2016/6/30 08:22.
 * 邮箱： songwenju@outlook.com
 */
public class Video implements Serializable{
    public String name;
    public String path;
    public int size;
    public int duration;

    /**
     * 解析cursor生成一个对象
     * @param cursor
     * @return
     */
    public static Video instanceFromCursor(Cursor cursor){
        Video video = new Video();
        if (cursor == null || cursor.getCount() ==0){
            return video;
        }

        video.name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        video.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        video.size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        video.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

        return video;

    }
}
