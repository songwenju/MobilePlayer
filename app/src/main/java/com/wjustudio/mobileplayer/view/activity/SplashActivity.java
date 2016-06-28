package com.wjustudio.mobileplayer.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.appBase.BaseActivity;

/**
 * 作者： songwenju on 2016/6/25 22:28.
 * 邮箱： songwenju@outlook.com
 */
public class SplashActivity extends BaseActivity{

    @Override
    public int onBindLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void onInitView() {

    }

    @Override
    protected void onSetViewData() {

    }

    @Override
    protected void onInitData() {

    }

    @Override
    public void onProcessClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext,MainActivity.class);
                startActivity(intent);
                //这里如果不finish的话会出现退出不了应用
                finish();
            }
        },2000);
    }
}
