package com.wjustudio.mobileplayer.utils;

/**
 * 作者： songwenju on 2016/7/2 22:24.
 * 邮箱： songwenju@outlook.com
 */
public class TimeUtil {
    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN = 60 * 1000;
    private static final int SEC = 1000;

    public static String formatTime(int t){
        String time;

        int hour = t / HOUR;

        int min = t % HOUR / MIN;

        int sec = t % MIN / SEC;

        if (hour == 0){
            time = String.format("%02d:%02d",min,sec);
        }else {
            time = String.format("%02d:%02d:%02d",hour,min,sec);
        }

        return time;
    }
}
