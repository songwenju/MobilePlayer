package com.wjustudio.mobileplayer.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
    private ImageView mVideoPause;
    private TextView mVideoName;
    private ImageView mButteryIcon;
    private BroadcastReceiver mButterReceiver;

    private  class ButteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            updateButteryIcon(level);
        }

    }

    @Override
    public int onBindLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    public void onInitView() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoPause = (ImageView) findViewById(R.id.iv_video_pause);
        mVideoName = (TextView) findViewById(R.id.tv_video_player_name);
        mButteryIcon = (ImageView) findViewById(R.id.iv_buttery_icon);
    }

    @Override
    protected void onSetViewData() {
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();
                updatePauseIcon();
            }
        });

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mButterReceiver = new ButteryBroadcastReceiver();
        registerReceiver(mButterReceiver,filter);
    }

    @Override
    protected void onInitData() {
        Video video = (Video) getIntent().getSerializableExtra("video");
        LogUtil.i(this, video.toString());
        mVideoView.setVideoPath(video.path);
//        mVideoView.setMediaController(new MediaController(this));
        mVideoPause.setOnClickListener(this);
        mVideoName.setText(video.name);
    }

    @Override
    public void onProcessClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video_pause:
                switchPauseStatus();
                break;
        }
    }

    /**
     * 切换暂停状态
     */
    private void switchPauseStatus() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
        updatePauseIcon();


    }

    /**
     * 更新暂停按钮
     */
    private void updatePauseIcon() {
        if (mVideoView.isPlaying()) {
            mVideoPause.setImageResource(R.drawable.btn_pause_selector);
        } else {
            mVideoPause.setImageResource(R.drawable.btn_start_selector);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mButterReceiver);
    }

    /**
     * 根据电池电量更新icon图片
     * @param level 等级
     */
    private void updateButteryIcon(int level) {
        if(level< 10){
            mButteryIcon.setImageResource(R.mipmap.ic_battery_0);
        }else if (level < 20){
            mButteryIcon.setImageResource(R.mipmap.ic_battery_20);
        }else if (level < 40){
            mButteryIcon.setImageResource(R.mipmap.ic_battery_40);
        }else if (level < 60){
            mButteryIcon.setImageResource(R.mipmap.ic_battery_60);
        }else if (level < 80){
            mButteryIcon.setImageResource(R.mipmap.ic_battery_80);
        }else {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_100);
        }
    }
}
