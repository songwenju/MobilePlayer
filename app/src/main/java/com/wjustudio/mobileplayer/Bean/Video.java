package com.wjustudio.mobileplayer.Bean;

import android.database.Cursor;
import android.provider.MediaStore;

import com.wjustudio.mobileplayer.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;

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
     * @param cursor cursor对象
     * @return video实体类
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

    /**
     * 通过Cursor获得Video的播放列表
     * @param cursor cursor对象
     * @return video的列表
     */
    public static ArrayList<Video> getVideoList(Cursor cursor){
        ArrayList<Video> videoArrayList = new ArrayList<>();
//        if (cursor != null && cursor.moveToFirst()){
//            do {
//
//                videoArrayList.add(instanceFromCursor(cursor));
//            }while (cursor.moveToNext());
//        }
        if (cursor == null ||cursor.getCount() == 0){
            return videoArrayList;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()){
            Video video = instanceFromCursor(cursor);
            videoArrayList.add(video);
        }
        LogUtil.i("Video","videoArrayList.size:"+videoArrayList.size());
//        LogUtil.i("Video","videoArrayList.1:"+videoArrayList.get(0));
        return videoArrayList;
    }
    @Override
    public String toString() {
        return "Video{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }
}
