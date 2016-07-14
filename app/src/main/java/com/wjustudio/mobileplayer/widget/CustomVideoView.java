package com.wjustudio.mobileplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.VideoView;

import com.wjustudio.mobileplayer.utils.LogUtil;

/**
 * 作者： songwenju on 2016/7/13 22:06.
 * 邮箱： songwenju@outlook.com
 */
public class CustomVideoView extends VideoView {
    private int mScreenH;
    private int mScreenW;
    private int mDefaultH;
    private int mDefaultW;
    private boolean isFullSceen;

    public CustomVideoView(Context context) {
        super(context);
        initVideoView(context);
    }


    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenW = metrics.widthPixels;
        mScreenH = metrics.heightPixels;

        LogUtil.i(this,"mScreenW:"+mScreenW+ " ,mScreenH:"+mScreenH);
//        mDefaultH = getMeasuredHeight();
//        mDefaultW = getMeasuredWidth();
//        LogUtil.i(this,"mDefaultH:"+ mDefaultH + " ,mDefaultW:"+ mDefaultW);
//        LogUtil.i(this,"mScreenW:"+mScreenW+ " ,mScreenH:"+mScreenH);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        LogUtil.i(this,"onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);

    }

    /**
     * 切换全屏状态
     * @param mDefaultH
     * @param mDefaultW
     */
    public void switchFullScreen(int mDefaultH, int mDefaultW){
        this.mDefaultH = mDefaultH;
        this.mDefaultW = mDefaultW;
        if (isFullSceen){
            getLayoutParams().width = this.mDefaultW;
            getLayoutParams().height = this.mDefaultH;
        }else {
            getLayoutParams().width = mScreenW;
            getLayoutParams().height = mScreenH;
        }
        requestLayout();

        isFullSceen = !isFullSceen;
    }

    /**
     * 判断当前是否是全屏
     * @return
     */
    public boolean isFullSceen() {
        return isFullSceen;
    }
}
