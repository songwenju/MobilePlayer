package com.wjustudio.mobileplayer.appBase;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.utils.LogUtil;
import com.wjustudio.mobileplayer.utils.ToastUtil;

/**
 * 作者： songwenju on 2016/6/16 07:50.
 * 邮箱： songwenju@outlook.com
 */


public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    /**
     * 是否沉浸状态栏
     **/
    private boolean isSetStatusBar = false ;
    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = true;
    /**
     * 是否禁止旋转屏幕
     **/
    private boolean isAllowScreenRotate = false;

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(this, "BaseActivity-->onCreate()");

        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (isSetStatusBar) {
            steepStatusBar();
        }
        setContentView(onBindLayout());
        mContext = this;

        if (!isAllowScreenRotate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        onInitView();
        onSetViewData();
        onInitData();
        registerCommonButton();
    }

    /**
     * 通用的button
     */
    private void registerCommonButton() {
        View view = findViewById(R.id.back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    /**
     * [绑定布局]
     */
    public abstract int onBindLayout();

    /**
     * [初始化控件]
     */
    public abstract void onInitView();

    /**
     * [为view设置数据]
     */
    protected abstract void onSetViewData();

    /**
     * [初始化listener]
     */
    protected abstract void onInitData();

    /**
     * [View点击]
     **/
    public abstract void onProcessClick(View v);

    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        } else {
            onProcessClick(v);
        }
    }

    /**
     * 弹出消息为msg的toast
     * @param msg
     */
    protected void toast(String msg) {
        ToastUtil.showToast(msg);
    }

    public View getView(View v, int id) {
        v.findViewById(id);
        return v;
    }

    /**
     * [页面跳转]
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * [携带数据的页面跳转]
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    /**
     * [是否允许全屏]
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * [是否设置沉浸状态栏]
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        this.isSetStatusBar = isSetStatusBar;
    }

    /**
     * [是否允许屏幕旋转]
     */
    public void setScreenRoate(boolean isAllowScreenRoate) {
        this.isAllowScreenRotate = isAllowScreenRoate;
    }

}

