package com.wjustudio.mobileplayer.model;

import com.wjustudio.mobileplayer.Bean.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 歌词转换类
 * 作者： songwenju on 2016/7/28 08:20.
 * 邮箱： songwenju@outlook.com
 */
public class LyricParse {
    /**
     * 从歌词文件中解析出歌词列表
     * @param file
     * @return
     */
    public static ArrayList<Lyric> parseFromFile(File file){
        ArrayList<Lyric> lyrics = new ArrayList<>();
        if (file == null || !file.exists()){
            lyrics.add(new Lyric(0,"找不到歌词文件"));
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file),"gbk"));
            String line = bufferedReader.readLine();
            while (line != null){
                //解析一行歌词,存在重复的情况[01:45.51][02:58.62]整理好心情再出发
                ArrayList<Lyric> lineList = parseLine(line);
                lyrics.addAll(lineList);
                line = bufferedReader.readLine();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //排序，Lyric必须Comparable接口
        Collections.sort(lyrics);
        return  lyrics;
    }

    /**
     * 解析一行歌词
     * @param line
     * @return
     */
    private static ArrayList<Lyric> parseLine(String line) {
        ArrayList<Lyric> lineList = new ArrayList<>();
        String[] split = line.split("]");
        //[01:45.51   [02:58.62  整理好心情再出发   (分为了三段)
        String content = split[split.length - 1];
        //[01:45.51   [02:58.62
        for (int i = 0; i < split.length - 1; i++) {
            int statPoint = parseTime(split[i]);
            Lyric lyric = new Lyric(statPoint,content);
            lineList.add(lyric);
        }

        return lineList;
    }

    /**
     * 根据timeStr解析出起始时间  [02:58.62
     * @param timeStr
     * @return
     */
    private static int parseTime(String timeStr) {
        int startPoint = 0;
        String[]arr = timeStr.split(":");
        //[02:58.62
        String minStr = arr[0].substring(1,arr[0].length());

        //58.62
        arr = arr[1].split("\\.");

        String secStr = arr[0];
        String mSecStr = arr[1];

        int min = Integer.parseInt(minStr);
        int sec = Integer.parseInt(secStr);
        int mSrc = Integer.parseInt(mSecStr);
        startPoint = min * 60 * 1000 + sec * 1000 + mSrc * 10;
        return startPoint;
    }
}
