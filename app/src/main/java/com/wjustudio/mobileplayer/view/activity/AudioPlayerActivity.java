package com.wjustudio.mobileplayer.view.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;
import com.wjustudio.mobileplayer.config.AppConstant;
import com.wjustudio.mobileplayer.model.LyricLoader;
import com.wjustudio.mobileplayer.service.AudioPlayerService;
import com.wjustudio.mobileplayer.utils.LogUtil;
import com.wjustudio.mobileplayer.utils.TimeUtil;
import com.wjustudio.mobileplayer.widget.LyricView;

import java.io.File;

/**
 * 作者： songwenju on 2016/7/18 08:01.
 * 邮箱： songwenju@outlook.com
 */
public class AudioPlayerActivity extends BaseActivity {

    private static final int UPDATE_DURATION = 0;
    private static final int UPDATE_LYRIC_ROLL = 1;
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
    private LyricView mAudioLyric;
    private ImageView mIvWave;
    private TextView mAudioDuration;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DURATION:
                    updateDuration();
                    break;
                case UPDATE_LYRIC_ROLL:
                    updateLyricRoll();
                    break;

            }
        }

    };
    private SeekBar mSkAudioPosition;

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
        mAudioLyric = (LyricView) findViewById(R.id.tv_audio_lyric);

        mIvWave = (ImageView) findViewById(R.id.iv_wave);

        mAudioDuration = (TextView) findViewById(R.id.tv_audio_duration);
        mSkAudioPosition = (SeekBar) findViewById(R.id.sk_audio_position);

    }

    @Override
    protected void onSetViewData() {
        mAudioList.setOnClickListener(this);
        mAudioPre.setOnClickListener(this);
        mAudioPause.setOnClickListener(this);
        mAudioNext.setOnClickListener(this);
        mAudioOrder.setOnClickListener(this);
        mSkAudioPosition.setOnSeekBarChangeListener(new OnAudioSeekBarChangeListener());
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
        IntentFilter audioFilter = new IntentFilter();
        audioFilter.addAction(AppConstant.AUDIO_END_ACTION);
        audioFilter.addAction(AppConstant.AUDIO_START_ACTION);
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
                playPre();
                break;
            case R.id.iv_audio_next:
                playNext();
                break;
            case R.id.iv_audio_order:
                switchPlayMode();
                break;
        }
    }

    /**
     * 切换播放模式
     */
    private void switchPlayMode() {
        mServiceBind.switchPlayMode();
        updatePlayModeIcon();
    }

    /**
     * 更新播放顺序的图标
     */
    private void updatePlayModeIcon() {
        int mode = mServiceBind.getCurrentPlayMode();
        switch (mode) {
            case AudioPlayerService.PLAY_ALL_REPEAT:
                mAudioOrder.setImageResource(R.drawable.audio_play_all_repeat_selector);
                break;
            case AudioPlayerService.PLAY_RANDOM:
                mAudioOrder.setImageResource(R.drawable.audio_play_random_selector);
                break;
            case AudioPlayerService.PLAY_SINGLE_REPEAT:
                mAudioOrder.setImageResource(R.drawable.audio_play_single_repeat_selector);
                break;
        }
    }

    /**
     * 播放下一首
     */
    private void playNext() {
        mServiceBind.playNext();
    }

    /**
     * 播放上一首
     */
    private void playPre() {
        mServiceBind.playPre();
    }

    /**
     * 更新播放的状态
     */
    private void updatePlayerState() {
        if (mServiceBind.isPlaying()) {
            mServiceBind.pause();
        } else {
            mServiceBind.start();
        }

        updatePlayIcon();
    }

    /**
     * 更新播放的图标
     */
    private void updatePlayIcon() {
        if (mServiceBind.isPlaying()) {
            mAudioPause.setImageResource(R.drawable.audio_pause_selector);
        } else {
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
        mHandler.removeCallbacksAndMessages(null);
    }

    private class AudioBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConstant.AUDIO_START_ACTION)) {
                //service 开始播放会发送一个广播
                Audio audio = (Audio) intent.getSerializableExtra("audioItem");
                LogUtil.i(this, "AudioBroadcastReceiver.onReceive: audio" + audio.toString());
                //更新标题
                mAudioTitle.setText(audio.name);
                //更新一下暂停按钮
                updatePlayIcon();

                //更新一下歌手名
                mAudioArtist.setText(audio.artist);
                //设置SeekBar的最大值
                mSkAudioPosition.setMax(mServiceBind.getDuration());
                updateDuration();

                updatePlayModeIcon();
//                File file = new File(Environment.getExternalStorageDirectory(),"Download/test/audio/"+
//                audio.name+".lrc");
                File file = LyricLoader.loaderFile(audio.name);
                mAudioLyric .setLyricFile(file);
                updateLyricRoll();
            } else if (action.equals(AppConstant.AUDIO_END_ACTION)) {
                mAudioDuration.setText(mServiceBind.getDuration() + "/" + mServiceBind.getDuration());
                mAudioPause.setImageResource(R.drawable.audio_play_selector);
            }
        }
    }

    /**
     * 更新时间
     */
    private void updateDuration() {
        int duration = mServiceBind.getDuration();
        updatePosition(duration);
        mHandler.sendEmptyMessageDelayed(UPDATE_DURATION, 500);
    }


    /**
     * 更新歌词高亮行
     */
    private void updateLyricRoll(){
        mAudioLyric.rollLyric(mServiceBind.getCurrentPotion(),mServiceBind.getDuration());
        mHandler.sendEmptyMessageDelayed(UPDATE_LYRIC_ROLL,100);
    }
    /**
     * 更新进度
     *
     * @param duration
     */
    private void updatePosition(int duration) {
        int currentPotion = mServiceBind.getCurrentPotion();
        String durationStr = TimeUtil.formatTime(duration);
        String currentPositionStr = TimeUtil.formatTime(currentPotion);
        mAudioDuration.setText(currentPositionStr + "/" + durationStr);
        mSkAudioPosition.setProgress(currentPotion);
    }

    private class OnAudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (!b) {
                return;
            }
            mServiceBind.seekTo(i);
            updatePosition(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
