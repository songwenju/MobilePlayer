package com.wjustudio.mobileplayer.view.activity;

import android.media.MediaPlayer;
import android.view.View;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;
import com.wjustudio.mobileplayer.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 作者： songwenju on 2016/7/18 08:01.
 * 邮箱： songwenju@outlook.com
 */
public class AudioPlayerActivity extends BaseActivity {

    private ArrayList<Audio> mAudioList;

    @Override
    public int onBindLayout() {
        return R.layout.activity_audio_player;
    }

    @Override
    public void onInitView() {

    }

    @Override
    protected void onSetViewData() {

    }

    @Override
    protected void onInitData() {
        //获得传过来的数据
        mAudioList = (ArrayList<Audio>) getIntent().getSerializableExtra("audioList");
        int position = getIntent().getIntExtra("position", -1);
        LogUtil.i(this,"arrayList:"+ mAudioList.toString());
        Audio audio = mAudioList.get(position);
        LogUtil.i(this,"audio:"+audio.toString());
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(audio.path);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProcessClick(View v) {

    }
}
