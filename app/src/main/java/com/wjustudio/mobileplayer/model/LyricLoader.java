package com.wjustudio.mobileplayer.model;

import android.os.Environment;

import java.io.File;

/**
 * 歌词加载器
 * 作者： songwenju on 2016/7/28 09:01.
 * 邮箱： songwenju@outlook.com
 */
public class LyricLoader {
    private static File rootDir = new File(
            Environment.getExternalStorageDirectory(), "Download/test/audio");

    /**
     * 加载歌词
     *
     * @param title
     * @return
     */
    public static File loaderFile(String title) {
        File file = null;
        //加载lrc歌词
        file = new File(rootDir, title + ".lrc");
        if (file.exists()) {
            return file;
        }
        //加载txt歌词
        file = new File(rootDir, title + ".txt");
        if (file.exists()) {
            return file;
        }
        //更换目录查找
        //...
        //加载网络歌词
        //...
        return file;
    }
}
