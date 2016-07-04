package com.wjustudio.mobileplayer.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

/**
 * 自定义的子线程查询
 * 作者： songwenju on 2016/7/2 22:10.
 * 邮箱： songwenju@outlook.com
 */
public class MobileAsyncQueryHandler extends AsyncQueryHandler {

    public MobileAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        CursorAdapter adapter = (CursorAdapter) cookie;
        adapter.swapCursor(cursor);
    }
}
