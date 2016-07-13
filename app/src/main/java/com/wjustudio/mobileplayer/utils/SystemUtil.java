package com.wjustudio.mobileplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.HashMap;

/**
 * 系统操作的一些工具
 * 作者： songwenju on 2016/7/10 17:08.
 * 邮箱： songwenju@outlook.com
 */
public class SystemUtil {
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    /**
     * 获得宽度和除去通知栏的屏幕的高度
     *
     * @param activity 上下文
     * @return 包含宽高的集合
     */
    public static HashMap<String, Integer> getWindowSize(Activity activity) {
        WindowManager wm = activity.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        height -= getStatusBarHeight(activity);
        HashMap<String, Integer> windowSize = new HashMap<>();
        windowSize.put(SystemUtil.HEIGHT, height);
        windowSize.put(SystemUtil.WIDTH, width);
        return windowSize;
    }

    /**
     * 获得状态栏的高度
     *
     * @param activity 上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int statusHeight;
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusHeight = frame.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").
                        get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获得当前系统的亮度，0～255
     * 需要写入系统setting的权限 <uses-permission android:name="android.permission.WRITE_SETTINGS" />
     * @return 当前亮度
     */
    public static int getCurrentLight(Context context) {
        //先关闭系统的亮度自动调节
        try {
            if(android.provider.Settings.System.getInt(
                    context.getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        //获取当前亮度,获取失败则返回255
        return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);

    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    public static void setScreenBrightness(Activity context,int brightness) {
        //不让屏幕全暗
        if (brightness <= 1) {
            brightness = 1;
        }else if (brightness >= 255){
            brightness = 255;
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = brightness / 255f;
        context.getWindow().setAttributes(lp);

        //保存为系统亮度方法1
        android.provider.Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                brightness);

        //保存为系统亮度方法2
//        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
//        android.provider.Settings.System.putInt(getContentResolver(), "screen_brightness", brightness);
//        // resolver.registerContentObserver(uri, true, myContentObserver);
//        getContentResolver().notifyChange(uri, null);
    }
}
