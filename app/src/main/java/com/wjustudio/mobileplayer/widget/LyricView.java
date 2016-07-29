package com.wjustudio.mobileplayer.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wjustudio.mobileplayer.Bean.Lyric;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.model.LyricParse;

import java.io.File;
import java.util.List;

/**
 * 作者： songwenju on 2016/7/26 22:06.
 * 邮箱： songwenju@outlook.com
 */
public class LyricView extends TextView {

    private float mHeightLightSize;
    private float mNormalLightSize;
    private int mHeightLightColor;
    private int mNormalLightColor;
    private Paint mPaint;
    private int mHalfViewW;
    private int mHalfViewH;
    private Rect mBound;
    private List<Lyric> mLyricList;
    private int mCurrentLine;
    private int mLineHeigt;
    private int mDuration;
    private int mPosition;

    public LyricView(Context context) {
        super(context);
        init();
    }


    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LyricView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        mHeightLightSize = getResources().getDimension(R.dimen.height_light_size);
        mNormalLightSize = getResources().getDimension(R.dimen.normal_light_size);
        mLineHeigt = getResources().getDimensionPixelSize(R.dimen.line_height);

        mHeightLightColor = Color.GREEN;
        mNormalLightColor = Color.WHITE;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mHeightLightSize);
        mPaint.setColor(mHeightLightColor);

        //模拟歌词数据
//        mLyricList = new ArrayList<>();
//
//        for (int i = 0; i < 30; i++) {
//            mLyricList.add(new Lyric(i * 2000, "播放行数为：" + i));
//        }
//
//        mCurrentLine = 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHalfViewW = w / 2;
        mHalfViewH = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLyricList == null || mLyricList.size() == 0) {
            drawSingleVerticalText(canvas);
        } else {
            drawMultipleVerticalText(canvas);
        }

    }

    /**
     * 绘制多行竖直居中的文本
     *
     * @param canvas
     */
    private void drawMultipleVerticalText(Canvas canvas) {
        Lyric lyric = mLyricList.get(mCurrentLine);

        //偏移的Y = 已消耗时间的百分比 * 行高
        //已消耗时间的百分比 = 已消耗时间 / 行可用时间
        //已消耗时间 = 当前播放时间 - 当前行起始时间
        //行可用时间 = 下一行起始时间 - 当前行起始时间
        int lineTime; //行可用时间
        if (mCurrentLine != mLyricList.size() - 1) {
            Lyric nextLyric = mLyricList.get(mCurrentLine + 1);
            lineTime = nextLyric.startPoint - lyric.startPoint;
        } else {
            lineTime = mDuration - lyric.startPoint;
        }
        int pastTime = mPosition - lyric.startPoint;//已消耗时间

        float passPercent = pastTime / (float) lineTime;

        float passY = passPercent * mLineHeigt;
        canvas.translate(0, -passY);

        mBound = new Rect();
        mPaint.getTextBounds(lyric.content, 0, lyric.content.length(), mBound);
        int centerY = mHalfViewH + mBound.height() / 2;

        //逐行绘制
        for (int i = 0; i < mLyricList.size(); i++) {
            //改变字体大小和颜色
            if (i == mCurrentLine) {
                mPaint.setTextSize(mHeightLightSize);
                mPaint.setColor(mHeightLightColor);
            } else {
                mPaint.setTextSize(mNormalLightSize);
                mPaint.setColor(mNormalLightColor);
            }
            //要绘制的y位置=居中行的y + （要绘制y - 当前行的y）* 行高；
            int y = centerY + (i - mCurrentLine) * mLineHeigt;
            drawHorizontalText(canvas, mLyricList.get(i).content, y);
        }
    }

    /**
     * 绘制一行竖直居中的文本
     *
     * @param canvas
     */
    private void drawSingleVerticalText(Canvas canvas) {
        String text = "正在加载歌词。。。";
        mBound = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), mBound);

        //居中歌词的位置坐标
        //X = View的一半的宽度 - 文字一半的宽度
        //Y = View的一半的高度 + 文字一半的高度
        int y = mHalfViewH + mBound.height() / 2;
        drawHorizontalText(canvas, text, y);
    }

    /**
     * 绘制一行水平居中的文本
     *
     * @param canvas
     * @param text
     * @param y
     */
    private void drawHorizontalText(Canvas canvas, String text, int y) {
        int x = mHalfViewW - mBound.width() / 2;
        canvas.drawText(text, x, y, mPaint);
    }


    /**
     * 根据当前已经播放的position的时间，选择高亮的歌词
     *
     * @param position
     * @param duration
     */
    public void rollLyric(int position, int duration) {
        //高亮行为，起始时间小于position， 且下一行的起始时间大于position
        mDuration = duration;
        mPosition = position;
        for (int i = 0; i < mLyricList.size(); i++) {
            Lyric lyric = mLyricList.get(i);
            int nextTime;
            if (i != mLyricList.size() - 1) {
                //不是最后一行
                Lyric nextLyric = mLyricList.get(i + 1);
                nextTime = nextLyric.startPoint;
            } else {
                //最后一行
                nextTime = mDuration;
            }

            if (lyric.startPoint <= position && nextTime > position) {
                mCurrentLine = i;
            }
        }
        invalidate();
    }

    /**
     * 更新歌词文件
     *
     * @param lyricFile
     */
    public void setLyricFile(File lyricFile) {
        mLyricList = LyricParse.parseFromFile(lyricFile);
        mCurrentLine = 0;
    }

}
