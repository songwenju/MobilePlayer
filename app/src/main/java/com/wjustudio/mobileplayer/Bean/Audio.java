package com.wjustudio.mobileplayer.Bean;

import android.database.Cursor;
import android.provider.MediaStore;

import com.wjustudio.mobileplayer.utils.CommonUtil;
import com.wjustudio.mobileplayer.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者： songwenju on 2016/6/30 08:22.
 * 邮箱： songwenju@outlook.com
 */
public class Audio implements Serializable{
    public String name;
    public String path;
    public String artist;

    /**
     * 解析cursor生成一个对象
     * @param cursor cursor对象
     * @return Audio实体类
     */
    public static Audio instanceFromCursor(Cursor cursor){
        Audio Audio = new Audio();
        if (cursor == null || cursor.getCount() ==0){
            return Audio;
        }

        Audio.name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        Audio.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        Audio.path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        Audio.name = CommonUtil.formatName(Audio.name);
        return Audio;

    }

    /**
     * 通过Cursor获得Audio的播放列表
     * @param cursor cursor对象
     * @return Audio的列表
     */
    public static ArrayList<Audio> getAudioList(Cursor cursor){
        ArrayList<Audio> AudioArrayList = new ArrayList<>();
//        if (cursor != null && cursor.moveToFirst()){
//            do {
//
//                AudioArrayList.add(instanceFromCursor(cursor));
//            }while (cursor.moveToNext());
//        }
        if (cursor == null ||cursor.getCount() == 0){
            return AudioArrayList;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()){
            Audio Audio = instanceFromCursor(cursor);
            AudioArrayList.add(Audio);
        }
        LogUtil.i("Audio","AudioArrayList.size:"+AudioArrayList.size());
//        LogUtil.i("Audio","AudioArrayList.1:"+AudioArrayList.get(0));
        return AudioArrayList;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
