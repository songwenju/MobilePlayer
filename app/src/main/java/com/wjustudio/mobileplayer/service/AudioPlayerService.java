package com.wjustudio.mobileplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 作者： songwenju on 2016/7/20 08:20.
 * 邮箱： songwenju@outlook.com
 */
public class AudioPlayerService extends Service {
    private ArrayList<Audio> mAudioList;
    private AudioServiceBind mAudioServiceBind;
    private int mPosition;
    private MediaPlayer mPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(this, "AudioPlayerService.onBind");
        return mAudioServiceBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //一个service只要一个iBind，在onCreate方法中创建
        mAudioServiceBind = new AudioServiceBind();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(this, "AudioPlayerService.onStartCommand");
        //获得传过来的数据
        mAudioList = (ArrayList<Audio>) intent.getSerializableExtra("audioList");
        mPosition = intent.getIntExtra("position", -1);
        LogUtil.i(this, "arrayList:" + mAudioList.toString());
        mAudioServiceBind.play();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 在bind中创建一系列的方法
     */
    public class AudioServiceBind extends Binder {
        /**
         * 开始播放
         */
        public void play() {
            Audio audio = mAudioList.get(mPosition);
            LogUtil.i(this, "audio:" + audio.toString());
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(audio.path);
//                mPlayer.prepare(); //使用prepareAsync要加上回调监听
                mPlayer.setOnPreparedListener(new OnAudioPreparedListener());
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 暂停播放
         */
        public void pause(){
            mPlayer.pause();
        }

        /**
         * 开始播放
         */
        public void start(){
            mPlayer.start();
        }

        /**
         * 是否正在播放
         * @return true 表示正在播放，false 表示暂停
         */
        public boolean isPlaying(){
            return mPlayer.isPlaying();
        }

        /**
         * 停止播放
         */
        public void stop(){
            mPlayer.stop();
        }

        private class OnAudioPreparedListener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //音乐资源准备好之后开始播放
                mPlayer.start();
                //通知Activity进行界面更新，这里使用广播，后期会使用EventBus
                Intent intent = new Intent("com.wjuStudio.audioPlayerService.startPlay");
                intent.putExtra("audioItem",mAudioList.get(mPosition));
                sendBroadcast(intent);


            }
        }
    }
}
