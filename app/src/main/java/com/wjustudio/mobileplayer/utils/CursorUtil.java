package com.wjustudio.mobileplayer.utils;

import android.database.Cursor;

/**
 * 作者： songwenju on 2016/6/30 22:18.
 * 邮箱： songwenju@outlook.com
 */
public class CursorUtil {
    private static final String TAG = "CursorUtil";

    /**
     * 展示cursor的内容
     * @param cursor 参数
     */
    public static void showCursor(Cursor cursor){
        if (cursor != null) {
            LogUtil.i(TAG, "cursor.size:" + cursor.getCount());
        }
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    LogUtil.i(TAG, "------------------------------------------------------");
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        LogUtil.i(TAG, "Cursor: name=" + cursor.getColumnName(i) + ";value = " + cursor.getString(i));
                    }
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
