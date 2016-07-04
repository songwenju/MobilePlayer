package com.wjustudio.mobileplayer.view.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wjustudio.mobileplayer.Bean.Video;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseFragment;
import com.wjustudio.mobileplayer.db.MobileAsyncQueryHandler;
import com.wjustudio.mobileplayer.utils.LogUtil;
import com.wjustudio.mobileplayer.view.activity.VideoPlayerActivity;
import com.wjustudio.mobileplayer.view.adapter.VideoListAdapter;

/**
 * 作者： songwenju on 2016/6/27 08:28.
 * 邮箱： songwenju@outlook.com
 */
public class VideoListFragment extends BaseFragment {

    private ListView mVideoListView;
    private VideoListAdapter mVideoListAdapter;

    @Override
    public int onBindLayout() {
        return R.layout.fragment_video_list;
    }

    @Override
    public void onInitView(View view) {
        mVideoListView = (ListView) view.findViewById(R.id.simple_list_view);
    }

    @Override
    protected void onSetViewData() {
        mVideoListAdapter = new VideoListAdapter(mContext, null);
        mVideoListView.setAdapter(mVideoListAdapter);
    }

    @Override
    protected void onInitData() {
        LogUtil.i(this, "VideoListFragment onInitData");
        ContentResolver resolver = mContext.getContentResolver();
        //Media这里类负责MediaProvider的内容的封装。
        //注意使用CursorAdapter必须要有_id,如果没有的话可以用uId as _id
//        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
//                new String[]{Media._ID,Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);
        //自动notifyDataSetInvalidated

        //查询放在子线程开启异步查询
        MobileAsyncQueryHandler asyncQueryHandler = new MobileAsyncQueryHandler(resolver);

        //token相当于massage.what ,cookie相当于massage.obj
        asyncQueryHandler.startQuery(0, mVideoListAdapter, Media.EXTERNAL_CONTENT_URI,
                new String[]{Media._ID, Media.DATA, Media.TITLE, Media.SIZE, Media.DURATION}, null, null, null);
        mVideoListView.setOnItemClickListener(new MyOnItemClickListener());

    }

    @Override
    public void onProcessClick(View v) {

    }

    private class MyOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
            //获得被点击的条目
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            Video video = Video.instanceFromCursor(cursor);
            //跳转到播放界面
            Intent intent = new Intent(mContext,VideoPlayerActivity.class);
            intent.putExtra("video",video);
            startActivity(intent);
        }
    }
}
