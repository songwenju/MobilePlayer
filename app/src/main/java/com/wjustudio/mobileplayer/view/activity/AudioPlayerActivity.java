package com.wjustudio.mobileplayer.view.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;
import com.wjustudio.mobileplayer.service.AudioPlayerService;
import com.wjustudio.mobileplayer.utils.LogUtil;

/**
 * 作者： songwenju on 2016/7/18 08:01.
 * 邮箱： songwenju@outlook.com
 */
public class AudioPlayerActivity extends BaseActivity {

    private AudioServiceConnection mServiceConnection;
    private ImageView mAudioList;
    private ImageView mAudioPre;
    private ImageView mAudioPause;
    private ImageView mAudioNext;
    private ImageView mAudioOrder;
    private AudioPlayerService.AudioServiceBind mServiceBind;
    private BroadcastReceiver mAudioBroadcastReceiver;
    private TextView mAudioTitle;
    private TextView mAudioArtist;
    private TextView mAudioLyric;
    private ImageView mIvWave;

    @Override
    public int onBindLayout() {
        return R.layout.activity_audio_player;
    }

    @Override
    public void onInitView() {
        mAudioList = (ImageView) findViewById(R.id.iv_audio_list);
        mAudioPre = (ImageView) findViewById(R.id.iv_audio_pre);
        mAudioPause = (ImageView) findViewById(R.id.iv_audio_pause);
        mAudioNext = (ImageView) findViewById(R.id.iv_audio_next);
        mAudioOrder = (ImageView) findViewById(R.id.iv_audio_order);

        mAudioTitle = (TextView) findViewById(R.id.tv_audio_title);
        mAudioArtist = (TextView) findViewById(R.id.tv_audio_artist);
        mAudioLyric = (TextView) findViewById(R.id.tv_audio_lyric);

        mIvWave = (ImageView) findViewById(R.id.iv_wave);
    }

    @Override
    protected void onSetViewData() {
        mAudioList.setOnClickListener(this);
        mAudioPre.setOnClickListener(this);
        mAudioPause.setOnClickListener(this);
        mAudioNext.setOnClickListener(this);
        mAudioOrder.setOnClickListener(this);
    }

    @Override
    protected void onInitData() {
        //把启动Activity传过来的intent获得作为参数，包括intent里的内容。然后传给service。
        Intent intent = new Intent(getIntent());
        intent.setClass(this, AudioPlayerService.class);
        mServiceConnection = new AudioServiceConnection();
        LogUtil.i(this, "AudioPlayerActivity.onInitData: bindService");
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
        startService(intent);

        mAudioBroadcastReceiver = new AudioBroadcastReceiver();
        IntentFilter audioFilter = new IntentFilter("com.wjuStudio.audioPlayerService.startPlay");
        registerReceiver(mAudioBroadcastReceiver, audioFilter);

        //开启示波器动画
        AnimationDrawable animation = (AnimationDrawable) mIvWave.getDrawable();
        animation.start();
    }

    @Override
    public void onProcessClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_list:
                break;
            case R.id.iv_audio_pause:
                updatePlayerState();
                break;
            case R.id.iv_audio_pre:
                break;
            case R.id.iv_audio_next:
                break;
            case R.id.iv_audio_order:
                break;
        }
    }

    /**
     * 更新播放的状态
     */
    private void updatePlayerState() {
        if (mServiceBind.isPlaying()){
            mServiceBind.pause();
        }else {
            mServiceBind.start();
        }

        updatePlayIcon();
    }

    /**
     * 更新播放的图标
     */
    private void updatePlayIcon() {
        if (mServiceBind.isPlaying()){
            mAudioPause.setImageResource(R.drawable.audio_pause_selector);
        }else {
            mAudioPause.setImageResource(R.drawable.audio_play_selector);
        }
    }

    private class AudioServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.i(this, "AudioServiceConnection.onServiceConnected: " + iBinder);
            mServiceBind = (AudioPlayerService.AudioServiceBind) iBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(this, "AudioPlayerActivity.onDestroy: unbindService");
        mServiceBind.stop();
        unbindService(mServiceConnection);
        unregisterReceiver(mAudioBroadcastReceiver);
    }

    private class AudioBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //service 开始播放会发送一个广播
            Audio audio = (Audio) intent.getSerializableExtra("audioItem");
            LogUtil.i(this,"AudioBroadcastReceiver.onReceive: audio"+audio.toString());
            //更新标题
            mAudioTitle.setText(audio.name);
            //更新一下暂停按钮
            updatePlayIcon();

            //更新一下歌手名
            mAudioArtist.setText(audio.artist);
        }
    }
}
