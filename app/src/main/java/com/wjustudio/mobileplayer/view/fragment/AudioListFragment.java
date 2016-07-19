package com.wjustudio.mobileplayer.view.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseFragment;
import com.wjustudio.mobileplayer.db.MobileAsyncQueryHandler;
import com.wjustudio.mobileplayer.utils.LogUtil;
import com.wjustudio.mobileplayer.view.activity.AudioPlayerActivity;
import com.wjustudio.mobileplayer.view.adapter.AudioListAdapter;

import java.util.ArrayList;

/**
 * 作者： songwenju on 2016/6/27 08:28.
 * 邮箱： songwenju@outlook.com
 */
public class AudioListFragment extends BaseFragment{

    private ListView mAudioView;
    private AudioListAdapter mAdapter;

    @Override
    public int onBindLayout() {
        return R.layout.fragment_audio_list;
    }

    @Override
    public void onInitView(View view) {
        mAudioView = (ListView) view.findViewById(R.id.simple_list_view);
    }

    @Override
    protected void onSetViewData() {
        mAdapter = new AudioListAdapter(mContext,null);
        mAudioView.setAdapter(mAdapter);
        mAudioView.setOnItemClickListener(new OnItemClickListener());

    }

    @Override
    protected void onInitData() {
        LogUtil.i(this,"onInitData");
        ContentResolver resolver = mContext.getContentResolver();
//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA,
//                Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);
//        mAdapter.swapCursor(cursor);
        MobileAsyncQueryHandler mobileAsyncQueryHandler = new MobileAsyncQueryHandler(resolver);
        mobileAsyncQueryHandler.startQuery(1,mAdapter,Media.EXTERNAL_CONTENT_URI, new String[]{Media._ID, Media.DATA,
                Media.DISPLAY_NAME, Media.ARTIST}, null, null, null);
//        CursorUtil.showCursor(cursor);
    }

    @Override
    public void onProcessClick(View v) {

    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(mContext, AudioPlayerActivity.class);
            ArrayList<Audio> audioList = Audio.getAudioList(cursor);
            intent.putExtra("audioList",audioList);
            intent.putExtra("position",i);
            startActivity(intent);
        }
    }
}
