package com.wjustudio.mobileplayer.view.activity;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.VideoView;

import com.wjustudio.mobileplayer.Bean.Video;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;
import com.wjustudio.mobileplayer.utils.LogUtil;

/**
 * 作者： songwenju on 2016/7/4 08:43.
 * 邮箱： songwenju@outlook.com
 */
public class VideoPlayerActivity extends BaseActivity {

    private VideoView mVideoView;

    @Override
    public int onBindLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    public void onInitView() {
        mVideoView = (VideoView) findViewById(R.id.video_view);


    }

    @Override
    protected void onSetViewData() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
    }

    @Override
    protected void onInitData() {
        Video video = (Video) getIntent().getSerializableExtra("video");
        LogUtil.i(this, video.toString());
        mVideoView.setVideoPath(video.path);
//        mVideoView.setMediaController(new MediaController(this));
    }

    @Override
    public void onProcessClick(View v) {

    }
}
