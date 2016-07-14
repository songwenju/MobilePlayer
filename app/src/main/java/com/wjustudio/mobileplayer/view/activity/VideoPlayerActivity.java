package com.wjustudio.mobileplayer.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wjustudio.mobileplayer.Bean.Video;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;
import com.wjustudio.mobileplayer.utils.LogUtil;
import com.wjustudio.mobileplayer.utils.SystemUtil;
import com.wjustudio.mobileplayer.utils.TimeUtil;
import com.wjustudio.mobileplayer.widget.CustomVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import static android.view.GestureDetector.SimpleOnGestureListener;

/**
 * 作者： songwenju on 2016/7/4 08:43.
 * 邮箱： songwenju@outlook.com
 */
public class VideoPlayerActivity extends BaseActivity {
    private CustomVideoView mVideoView;
    private ImageView mVideoPause;
    private TextView mVideoName;
    private ImageView mButteryIcon;
    private BroadcastReceiver mButterReceiver;
    private TextView mSystemTime;
    private static final int UPDATE_SYSTEM_TIME = 0;
    private static final int UPDATE_POSITION = 1;
    private static final int UPDATE_HIDE_PAN = 2;
    private SeekBar mSkVolume;
    private AudioManager mAudioMgr;
    private ImageView mVideoMute;
    private int mCurrentVolume;
    private BroadcastReceiver mVolumeReceiver;
    private float mStartY;
    private int mStartVolume;
    private int mCurrentLight;
    private int mHalfScreenH;
    private int mHalfScreenW;
    private TextView mCurrentPosition;
    private SeekBar mSkPlayerPosition;
    private TextView mTotalPosition;
    private ImageView mVideoPre;
    private ImageView mVideoNext;
    private ArrayList<Video> mVideoList;
    private int mPosition;
    private LinearLayout mTopTab;
    private LinearLayout mBottomTab;
    private GestureDetector mGestureDetector;
    private boolean isShowPan = false;
    private ImageView mVideoFullScreen;

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean isFromUser) {
            if (!isFromUser) {
                return;
            }
            switch (seekBar.getId()) {
                case R.id.sk_player_position:
                    updateCurrentPosition(i);
                    mVideoView.seekTo(i);
                    break;
                case R.id.sk_volume:
                    setVolume(i);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //手指放在seekBar上
            removeHideMsg();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //手指离开seekBar
            sendHidePanMsg();
        }
    }

    private void sendHidePanMsg() {
        mHandler.sendEmptyMessageDelayed(UPDATE_HIDE_PAN, 5000);
    }

    private class ButteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            updateButteryIcon(level);
        }

    }


    private Handler mHandler = new MyHandler(this);

    /**
     * 使用静态内部类，并在其中创建对Activity的弱引用,消除内存泄露
     */
    private class MyHandler extends Handler {
        private WeakReference<VideoPlayerActivity> weakReference;

        public MyHandler(VideoPlayerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoPlayerActivity activity = weakReference.get();
            if (activity == null) {
                super.handleMessage(msg);
            } else {
                switch (msg.what) {
                    case UPDATE_SYSTEM_TIME:
                        updateSystemTime();
                        break;
                    case UPDATE_POSITION:
                        updatePosition();
                        break;
                    case UPDATE_HIDE_PAN:
                        hideControlPan();
                        break;
                }
            }
        }
    }

    @Override
    public int onBindLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    public void onInitView() {
        mVideoView = (CustomVideoView) findViewById(R.id.video_view);
        mVideoPause = (ImageView) findViewById(R.id.iv_video_pause);
        mVideoName = (TextView) findViewById(R.id.tv_video_player_name);
        mButteryIcon = (ImageView) findViewById(R.id.iv_buttery_icon);
        mSystemTime = (TextView) findViewById(R.id.tv_system_time);
        mSkVolume = (SeekBar) findViewById(R.id.sk_volume);
        mVideoMute = (ImageView) findViewById(R.id.iv_mute);
        mVideoPre = (ImageView) findViewById(R.id.iv_video_pre);
        mVideoNext = (ImageView) findViewById(R.id.iv_video_next);
        mCurrentPosition = (TextView) findViewById(R.id.tv_current_position);
        mSkPlayerPosition = (SeekBar) findViewById(R.id.sk_player_position);
        mTotalPosition = (TextView) findViewById(R.id.tv_total_position);
        mTopTab = (LinearLayout) findViewById(R.id.ll_top_tab);
        mBottomTab = (LinearLayout) findViewById(R.id.ll_bottom_tab);
        mVideoFullScreen = (ImageView) findViewById(R.id.iv_video_full_screen);
    }

    @Override
    protected void onSetViewData() {
        //注册相关的监听器
        mVideoView.setOnPreparedListener(new OnPreparedListener());
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());

        //设置电池变化的receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mButterReceiver = new ButteryBroadcastReceiver();
        registerReceiver(mButterReceiver, filter);
        updateSystemTime();
        MyOnSeekBarChangeListener onSeekBarChangeListener = new MyOnSeekBarChangeListener();
        mSkVolume.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mGestureDetector = new GestureDetector(mContext, new MySimpleOnGestureListener());
        //设置点击事件
        mVideoMute.setOnClickListener(this);
        mVideoPre.setOnClickListener(this);
        mVideoNext.setOnClickListener(this);
        mVideoFullScreen.setOnClickListener(this);

        //设置音量变化的receiver
        filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        mVolumeReceiver = new VolumeBroadcastReceiver();
        registerReceiver(mVolumeReceiver, filter);

        //设置播放的seekBar的时间随着播放进度变化
        mSkPlayerPosition.setOnSeekBarChangeListener(onSeekBarChangeListener);
        initHiddenPan();
    }

    /**
     * 初始化面板的显示
     */
    private void initHiddenPan() {
        //使用getMeasureHeight获取高度
        mTopTab.measure(0, 0);
        int measuredHeight = mTopTab.getMeasuredHeight();
        ViewPropertyAnimator.animate(mTopTab).translationY(-measuredHeight);
        //使用getHeight获取高度,这个是在layout后调用
        mBottomTab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //不执行这一句的话会出现死循环
                mBottomTab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewPropertyAnimator.animate(mBottomTab).translationY(mBottomTab.getHeight());
            }
        });
        isShowPan = false;
    }


    @Override
    protected void onInitData() {
        Uri uri = getIntent().getData();
        if (uri != null){
            //外部调用
            mVideoView.setVideoURI(uri);
            mVideoName.setText(uri.getPath());
            mVideoPre.setEnabled(false);
            mVideoNext.setEnabled(false);
        }else {
            //内部调用
            Bundle bundleObject = getIntent().getExtras();
            mVideoList = (ArrayList<Video>) bundleObject.getSerializable("videoList");
            mPosition = bundleObject.getInt("position", -1);
            LogUtil.i(this, "mVideoList.size:" + mVideoList.size());
            LogUtil.i(this, "mVideoList.1:" + mVideoList.get(0));
            LogUtil.i(this, "mPosition:" + mPosition);
            playItem();
        }
//        mVideoView.setMediaController(new MediaController(this));
        mVideoPause.setOnClickListener(this);
        //初始化音量
        mAudioMgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        int currentVolume = getStreamVolume();
        int maxVolume = getStreamMaxVolume();
        LogUtil.i(this, "maxVolume:" + maxVolume + ",currentVolume:" + currentVolume);
        mSkVolume.setMax(maxVolume);
        mSkVolume.setProgress(currentVolume);

        //屏幕的宽度
        HashMap<String, Integer> windowSize = SystemUtil.getWindowSize(this);
        mHalfScreenH = windowSize.get(SystemUtil.HEIGHT) / 2;
        mHalfScreenW = windowSize.get(SystemUtil.WIDTH) / 2;


    }

    /**
     * 播放条目
     */
    private void playItem() {
        if (mVideoList == null || mVideoList.size() == 0 || mPosition == -1) {
            return;
        }
        Video video = mVideoList.get(mPosition);
        LogUtil.i(this, video.toString());

        mVideoView.setVideoPath(video.path);
        mVideoName.setText(video.name);

        updatePreAndNextBtn();

    }

    private void updatePreAndNextBtn() {
        mVideoPre.setEnabled(mPosition != 0);
        mVideoNext.setEnabled(mPosition != mVideoList.size() - 1);
    }

    @Override
    public void onProcessClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video_pause:
                updatePauseStatus();
                break;
            case R.id.iv_mute:
                updateMuteStatus();
                break;
            case R.id.iv_video_pre:
                playPre();
                break;
            case R.id.iv_video_next:
                playNext();
                break;
            case R.id.iv_video_full_screen:
                switchFullScreen();
                break;

        }
    }

    /**
     * 切换全屏状态
     */
    private void switchFullScreen() {
        int mDefaultH = mVideoView.getMeasuredHeight();
        int mDefaultW = mVideoView.getMeasuredWidth();
        mVideoView.switchFullScreen(mDefaultH, mDefaultW);
        updateFullScreenBtn();
    }

    /**
     * 更新是否是全屏的状态
     */
    private void updateFullScreenBtn() {
        if (mVideoView.isFullSceen()){
            mVideoFullScreen.setImageResource(R.drawable.btn_normal_screen_selector);
        }else {
            mVideoFullScreen.setImageResource(R.drawable.btn_full_screen_selector);

        }
    }


    /**
     * 播放下一曲
     */
    private void playNext() {
        if (mPosition < mVideoList.size()) {
            mPosition++;
            playItem();
        }
    }

    /**
     * 播放上一曲
     */
    private void playPre() {
        if (mPosition > 0) {
            mPosition--;
            playItem();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        //监听手势变化，去修改音量
        /*
            1.手指滑动屏幕的距离 = 当前的距离 - 压下时的距离
            2.手指滑动的百分比 = 手指滑动屏幕的距离  / 屏幕的宽度
            3.音量的改变大小 = 手指滑动的百分比 * 最大音量值
            4.最终的音量 = 当前的音量 + 改变的音量
         */
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float startX = event.getX();
                mStartY = event.getY();
                mStartVolume = getStreamVolume();
                //取得当前亮度
                mCurrentLight = SystemUtil.getCurrentLight(mContext);
                LogUtil.i(this, "currentLight:" + mCurrentLight);
                removeHideMsg();
                break;
            case MotionEvent.ACTION_MOVE:
                //当前手指的位置
                float moveY = event.getY();
                //手指移动的距离
                float offsetY = moveY - mStartY;

                //手指滑动的百分比
                float v = offsetY / mHalfScreenH;
                LogUtil.i(this, "v:" + v);

                if (event.getX() < mHalfScreenW) {
                    //滑动屏幕左侧改变亮度
                    int tmpInt = (int) (mCurrentLight + 255 * v);
                    LogUtil.i(this, "tmpInt:" + tmpInt);
                    SystemUtil.setScreenBrightness(VideoPlayerActivity.this, tmpInt);
                } else {
                    //滑动屏幕右侧改变音量
                    //音量改变大小
                    float offsetVolume = getStreamMaxVolume() * v;
                    setVolume((int) (mStartVolume + offsetVolume));
                }
                break;
            case MotionEvent.ACTION_UP:
//                float endY = event.getY();
//
//                if (Math.abs(endY - mStartY) < 5) {
//                    //滑动距离较少时，认为是点击，暂停视频播放
//                    updatePauseStatus();
//                }
                sendHidePanMsg();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void removeHideMsg() {
        mHandler.removeMessages(UPDATE_HIDE_PAN);
    }


    /**
     * 获得音量的最大值
     *
     * @return 音量的最大值
     */
    private int getStreamMaxVolume() {
        return mAudioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 更新静音状态
     */
    private void updateMuteStatus() {
        if (getStreamVolume() != 0) {
            mCurrentVolume = getStreamVolume();
            setVolume(0);
        } else {
            setVolume(mCurrentVolume);
        }

    }

    /**
     * 更新系统音量
     *
     * @param index 音量位置
     */
    private void setVolume(int index) {
        LogUtil.i(this, "volume:" + index);
        //flag为1表示显示系统音量
        mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        mSkVolume.setProgress(index);
    }

    /**
     * 获得系统的音量
     *
     * @return 音量
     */
    private int getStreamVolume() {
        return mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 切换暂停状态
     */
    private void updatePauseStatus() {
        if (mVideoView.isPlaying()) {
            mHandler.removeMessages(UPDATE_POSITION);
            mVideoView.pause();
        } else {
            //暂停状态
            //播放状态
            updatePosition();
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
        unregisterReceiver(mVolumeReceiver);
        //防止内存泄露
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 根据电池电量更新icon图片
     *
     * @param level 等级
     */
    private void updateButteryIcon(int level) {
        if (level < 10) {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_0);
        } else if (level < 20) {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_20);
        } else if (level < 40) {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_40);
        } else if (level < 60) {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_60);
        } else if (level < 80) {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_80);
        } else {
            mButteryIcon.setImageResource(R.mipmap.ic_battery_100);
        }
    }

    /**
     * 更新系统时间
     */
    private void updateSystemTime() {
//        LogUtil.i(this,"updateSystemTime");
        mSystemTime.setText(TimeUtil.formatSystemTime());
        //这里使用500而不是1000是因为代码执行也要时间
        mHandler.sendEmptyMessageDelayed(UPDATE_SYSTEM_TIME, 500);
    }


    /**
     * 音量发生变化的receiver
     */
    private class VolumeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                mSkVolume.setProgress(getStreamVolume());
            }
        }
    }

    private class OnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //开始播放
            mVideoView.start();
            //更新暂停按钮
            updatePauseIcon();
            //设置播放总时间
            int duration = mVideoView.getDuration();
            mTotalPosition.setText(TimeUtil.formatTime(duration));

            //设置skBar的最大值
            mSkPlayerPosition.setMax(duration);

            //更新开始播放的时间
            updatePosition();

            //更新全屏状态
            updateFullScreenBtn();
        }
    }

    /**
     * 更新播放进度
     */
    private void updatePosition() {
        int currentPosition = mVideoView.getCurrentPosition();
        updateCurrentPosition(currentPosition);
        mHandler.sendEmptyMessageDelayed(UPDATE_POSITION, 500);
    }

    /**
     * 更新当前播放的进度
     *
     * @param currentPosition 当前进度
     */
    private void updateCurrentPosition(int currentPosition) {
        mCurrentPosition.setText(TimeUtil.formatTime(currentPosition));
        //更新当前的seekBar
        mSkPlayerPosition.setProgress(currentPosition);
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mHandler.removeMessages(UPDATE_POSITION);
            //更新一下播放时间
            mCurrentPosition.setText(TimeUtil.formatTime(mVideoView.getDuration()));
            //更新一下暂停按钮
            updatePauseIcon();
        }
    }

    /**
     * 手势监听，监听单击和双击事件
     */
    private class MySimpleOnGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            switchPanShow();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击切换全屏
            switchFullScreen();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //长按事件更新暂停事件
            updatePauseStatus();
            super.onLongPress(e);
        }
    }

    /**
     * 控制面板的显示和隐藏
     */
    private void switchPanShow() {

        if (isShowPan) {
            hideControlPan();
        } else {
            showControlPan();
            sendHidePanMsg();
        }

    }

    /**
     * 隐藏控制面板
     */
    private void hideControlPan() {
        ViewPropertyAnimator.animate(mTopTab).translationY(-mTopTab.getHeight());
        ViewPropertyAnimator.animate(mBottomTab).translationY(mBottomTab.getHeight());
        isShowPan = false;
    }

    /**
     * 显示控制面板
     */
    private void showControlPan() {
        ViewPropertyAnimator.animate(mTopTab).translationY(0);
        ViewPropertyAnimator.animate(mBottomTab).translationY(0);
        isShowPan = true;
    }

}
