package com.wjustudio.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.config.AppConstant;
import com.wjustudio.mobileplayer.utils.LogUtil;
import com.wjustudio.mobileplayer.utils.SpUtil;
import com.wjustudio.mobileplayer.utils.ToastUtil;
import com.wjustudio.mobileplayer.view.activity.AudioPlayerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 作者： songwenju on 2016/7/20 08:20.
 * 邮箱： songwenju@outlook.com
 */
public class AudioPlayerService extends Service {
    private Context mContext;
    private ArrayList<Audio> mAudioList;
    private AudioServiceBind mAudioServiceBind;
    private int mPosition;
    private MediaPlayer mPlayer;
    public static final String PLAY_MODE = "PLAY_MODE";
    public static final int PLAY_ALL_REPEAT = 0;
    public static final int PLAY_RANDOM = 1;
    public static final int PLAY_SINGLE_REPEAT = 2;

    public static final String NOTIFY_TYPE = "notify_type";
    public static final int NOTIFY_TYPE_CONTENT = 3;
    public static final int NOTIFY_TYPE_PRE = 4;
    public static final int NOTIFY_TYPE_NEXT = 5;


    private int mPlayMode = SpUtil.getInt(PLAY_MODE, PLAY_ALL_REPEAT);

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
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(this, "AudioPlayerService.onStartCommand");
        int type = intent.getIntExtra(NOTIFY_TYPE, -1);
        if (type != -1) {
            switch (type) {
                case NOTIFY_TYPE_PRE:
                    mAudioServiceBind.playPre();
                    break;
                case NOTIFY_TYPE_NEXT:
                    mAudioServiceBind.playNext();
                    break;
                case NOTIFY_TYPE_CONTENT:
                    notifyUiUpdate();
                    break;
            }
        } else {
            //获得传过来的数据
            int position = intent.getIntExtra("position", -1);
            if (position == mPosition) {
                //同一首歌
                notifyUiUpdate();
            } else {
                mPosition = position;
                mAudioList = (ArrayList<Audio>) intent.getSerializableExtra("audioList");
                LogUtil.i(this, "arrayList:" + mAudioList.toString());
                mAudioServiceBind.play();
            }

        }
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
            if (mPlayer != null) {
                mPlayer.reset();
            } else {
                mPlayer = new MediaPlayer();

            }
            try {
                mPlayer.setDataSource(audio.path);
//                mPlayer.prepare(); //使用prepareAsync要加上回调监听
                mPlayer.setOnPreparedListener(new OnAudioPreparedListener());
                mPlayer.setOnCompletionListener(new OnAudioCompletionListener());
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 暂停播放
         */
        public void pause() {
            mPlayer.pause();
            cancelNitifation();
        }

        /**
         * 开始播放
         */
        public void start() {
            mPlayer.start();
            showCustomNotification();
        }

        /**
         * 是否正在播放
         *
         * @return true 表示正在播放，false 表示暂停
         */
        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }

        /**
         * 停止播放
         */
        public void stop() {
            mPlayer.stop();
        }

        /**
         * 获得音乐总时长
         *
         * @return 总时长
         */
        public int getDuration() {
            return mPlayer.getDuration();
        }

        /**
         * 获得当前播放时长
         *
         * @return 当前播放时长
         */
        public int getCurrentPotion() {
            return mPlayer.getCurrentPosition();
        }

        private class OnAudioPreparedListener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //音乐资源准备好之后开始播放
                mPlayer.start();
                //通知Activity进行界面更新，这里使用广播，后期会使用EventBus
                notifyUiUpdate();

                showCustomNotification();
            }
        }

        /**
         * 跳转到指定位置
         *
         * @param position
         */
        public void seekTo(int position) {
            mPlayer.seekTo(position);
        }

        /**
         * 播放上一曲
         */
        public void playPre() {
            if (mPlayMode == PLAY_RANDOM) {
                setRandomPosition();
                play();
            } else {
                if (mPosition != 0) {
                    mPosition--;
                    play();
                } else {
                    ToastUtil.showToast("已经是第一首歌了");
                }
            }
        }

        /**
         * 播放下一曲
         */
        public void playNext() {
            if (mPlayMode == PLAY_RANDOM) {
                setRandomPosition();
                play();
            } else {
                if (mPosition != mAudioList.size() - 1) {
                    mPosition++;
                    play();
                } else {

                    ToastUtil.showToast("已经是最后一首歌了");
                }
            }
        }


        /**
         * 切换播放模式
         */
        public void switchPlayMode() {
            switch (mPlayMode) {
                case PLAY_ALL_REPEAT:
                    mPlayMode = PLAY_RANDOM;
                    break;
                case PLAY_RANDOM:
                    mPlayMode = PLAY_SINGLE_REPEAT;
                    break;
                case PLAY_SINGLE_REPEAT:
                    mPlayMode = PLAY_ALL_REPEAT;
                    break;
            }

            SpUtil.putInt(PLAY_MODE, mPlayMode);
        }

        /**
         * 获得当前播放的模式
         *
         * @return 当前播放的模式
         */
        public int getCurrentPlayMode() {
            return mPlayMode;
        }

        private class OnAudioCompletionListener implements MediaPlayer.OnCompletionListener {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                //通知Activity进行界面更新，这里使用广播，后期会使用EventBus
//                Intent intent = new Intent(AppConstant.AUDIO_END_ACTION);
//                sendBroadcast(intent);

                //播放结束，根据选中的播放状态去播放下一首歌曲
                switch (mPlayMode) {
                    case PLAY_ALL_REPEAT:
                        if (mPosition != mAudioList.size() - 1) {
                            mPosition++;
                        } else {
                            mPosition = 0;
                        }
                        break;
                    case PLAY_RANDOM:
                        setRandomPosition();
                        break;
                    case PLAY_SINGLE_REPEAT:
                        break;


                }
                play();
            }
        }

        /**
         * 设置随机的位置
         */
        private void setRandomPosition() {
            Random random = new Random();
            int position = random.nextInt(mAudioList.size());
            if (position != mPosition) {
                mPosition = position;
            } else {
                mPosition = position + 1;
            }
            if (mPosition == mAudioList.size()) {
                mPosition = mAudioList.size() - 2;
            }
        }

        /**
         * 取消通知通知
         */
        public void cancelNitifation() {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(0);
        }

        /**
         * 显示自定义的Notification
         */
        private void showCustomNotification() {
            Notification.Builder builder = new Notification.Builder(mContext)
                    .setSmallIcon(R.mipmap.notification_music_playing)  //图标
                    .setTicker("正在播放：" + mAudioList.get(mPosition).name) //弹出
                    .setContent(getRemoteView())
                    .setContentIntent(getContentIntent());              //点击通知栏的响应

            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, notification);                                      //开始通知
        }

        /**
         * 获得RemoteView
         *
         * @return
         */
        private RemoteViews getRemoteView() {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio_notifi_layout);
            //设置文本
            remoteViews.setTextViewText(R.id.tv_audio_name, mAudioList.get(mPosition).name);
            remoteViews.setTextViewText(R.id.tv_audio_author, mAudioList.get(mPosition).artist);

            //设置点击事件
            remoteViews.setOnClickPendingIntent(R.id.iv_audio_pre, getPreIntent());
            remoteViews.setOnClickPendingIntent(R.id.iv_audio_next, getNextIntent());
            remoteViews.setOnClickPendingIntent(R.id.audio_notification_layout, getContentIntent());
            return remoteViews;
        }

        /**
         * 下一曲的pendingIntent
         *
         * @return
         */
        private PendingIntent getNextIntent() {
            Intent intent = new Intent(mContext, AudioPlayerService.class);
            intent.putExtra(NOTIFY_TYPE, NOTIFY_TYPE_NEXT);
            //第二个参数，requestCode不能相同，不然是更新了，即只显示最后一个相同id的内容。
            return PendingIntent.getService(mContext, NOTIFY_TYPE_NEXT, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }

        /**
         * 上一曲的pendingIntent
         *
         * @return
         */
        private PendingIntent getPreIntent() {
            Intent intent = new Intent(mContext, AudioPlayerService.class);
            intent.putExtra(NOTIFY_TYPE, NOTIFY_TYPE_PRE);
            return PendingIntent.getService(mContext, NOTIFY_TYPE_PRE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }


        /**
         * 获得PendingIntent
         *
         * @return PendingIntent
         */
        private PendingIntent getContentIntent() {
            Intent intent = new Intent(mContext, AudioPlayerActivity.class);
            intent.putExtra(NOTIFY_TYPE, NOTIFY_TYPE_CONTENT);
            return PendingIntent.getActivities(mContext, NOTIFY_TYPE_CONTENT, new Intent[]{intent},
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }

    /**
     * 发送开始播放的广播
     */
    private void notifyUiUpdate() {
        Intent intent = new Intent(AppConstant.AUDIO_START_ACTION);
        intent.putExtra("audioItem", mAudioList.get(mPosition));
        sendBroadcast(intent);
    }
}
