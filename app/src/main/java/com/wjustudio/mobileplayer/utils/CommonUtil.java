package com.wjustudio.mobileplayer.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.wjustudio.mobileplayer.appBase.BaseApplication;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：songwenju on 2016/1/24 20:29
 * 邮箱：songwenju01@163.com
 */
public class CommonUtil {
    public static final String TAG = "commonUtil";

    /**
     * 在主线程执行任务,该方法在fragment中也可以使用.
     *
     * @param r
     */
    public static void runOnUiThread(Runnable r) {
        BaseApplication.getAppHandler().post(r);
    }

    /**
     * 在主线程中执行延迟任务
     *
     * @param r
     * @param delayMillis
     */
    public static void runOnUiThread(Runnable r, long delayMillis) {
        BaseApplication.getAppHandler().postDelayed(r, delayMillis);
    }

    /**
     * 获得Resources对象
     *
     * @return
     */
    public static Resources getResources() {
        return BaseApplication.getContext().getResources();
    }

    /**
     * 获得String
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
        return getResources().getString(id);
    }

    /**
     * 获取字符数组
     *
     * @param id
     * @return
     */
    public static String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    /**
     * 获得颜色值
     *
     * @param id
     * @return
     */
    public static int getColor(int id) {
        return getResources().getColor(id);
    }

    /**
     * 获得Dimens值
     *
     * @param id
     * @return
     */
    public static int getDimens(int id) {
        return (int) getResources().getDimension(id);
    }

    /**
     * 获得Drawable对象
     *
     * @param id
     * @return
     */
    public static Drawable getDrawable(int id) {
        return getResources().getDrawable(id);
    }

    /**
     * 显示dialog弹框
     *
     * @param context
     * @param message
     */
    public static void showInfoDialog(Context context, String message) {
        showInfoDialog(context, message, "提示", "确定", "", null, null, null);
    }

    /**
     * 显示dialog弹框
     *
     * @param context
     * @param message
     * @param titleStr
     * @param positiveStr
     * @param positiveListener
     */
    public static void showInfoDialog(Context context, String message,
                                      String titleStr, String positiveStr, String negativeStr,
                                      DialogInterface.OnClickListener positiveListener,
                                      DialogInterface.OnClickListener negativeListener,
                                      DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(titleStr);
        localBuilder.setMessage(message);
        if (positiveListener == null) {
            positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };
        }
        localBuilder.setPositiveButton(positiveStr, positiveListener);
        localBuilder.setNegativeButton(negativeStr, negativeListener);
        localBuilder.setOnCancelListener(cancelListener);
        localBuilder.show();
    }

    /**
     * md5加密
     *
     * @param paramString
     * @return
     */
    public static String md5(String paramString) {
        String returnStr;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            returnStr = byteToHexString(localMessageDigest.digest());
            return returnStr;
        } catch (Exception e) {
            return paramString;
        }
    }

    /**
     * 将制定的byte数组转换为16进制的字符串
     *
     * @param digest
     * @return
     */
    private static String byteToHexString(byte[] digest) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(digest[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p;
        Matcher m;
        boolean b;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1, p2;
        Matcher m;
        boolean b;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 邮箱验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isCorrectEmail(String str) {
        Pattern p;
        Matcher m;
        boolean b;
        //验证邮箱
        p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * 姓名验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isCorrectName(String str) {
        Pattern p;
        Matcher m;
        boolean b;
        //验证正确的用户名
        p = Pattern.compile("^[a-zA-Z0-9_]{3,10}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }


    /**
     * 密码格式验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isCorrectPwd(String str) {
        Pattern p;
        Matcher m;
        boolean b;
        p = Pattern.compile("^[a-zA-Z0-9_]{4,15}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    private static final String[] LETTERS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z"};

    /**
     * 判断字符串是否是一个字母
     *
     * @param s
     * @return
     */
    public static boolean isInLatter(String s) {
        List<String> letters = Arrays.asList(LETTERS);
        return letters.contains(s);
    }

    /**
     * 注册广播接收者
     *
     * @param receiver
     * @param action
     */
    public static void registerReceiver(Context context, BroadcastReceiver receiver, String action) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        context.registerReceiver(receiver, filter);
    }


    /**
     * 获取服务是否开启
     *
     * @param context
     * @param className 服务的全类名
     * @return 服务开启的状态
     */
    public static boolean isRunningService(Context context, String className) {
        boolean isRunning = false;
        //这里是进程的管理者,活动的管理者
        ActivityManager am =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的服务,maxNum:上限值，如果小于上限值有多少返回多少，大于只返回上限值个数的服务
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(1000);
        //遍历集合
        for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
            //先得到service component 再通过getClassName得打组件的全类名,这里不是getPackageName,因为一个应用有多个服务
            ComponentName component_Name = serviceInfo.service;
            //获取服务全类名
            String componentName = component_Name.getClassName();

            //判断获取类名和传递进来的类名是否一致，一致返回true表示正在运行，不一致返回false表示没有运行
            if (!TextUtils.isEmpty(className) && className.equals(componentName)) {
                //如果包含该服务,说明服务已经开启了
                isRunning = true;
                LogUtil.i(TAG, "service is running:" + isRunning);
            }
        }
        return isRunning;
    }

    /**
     * 打开输入法面板
     *
     * @param activity
     */
    public static void showInputMethod(final Activity activity) {
        if (activity == null) return;
        InputMethodManager inputMethodManager = ((InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE));
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
        }
    }



    /**
     * 判断是否是url链接
     * @param str
     * @return
     */
    public static boolean isCorrectUrl(String str){
        Pattern p1,p2;
        p1 = Pattern.compile("[a-zA-z]+://[^\\s]*");  // 验证http的

        return p1.matcher(str).matches() ;
    }

    /**
     * 格式化名称
     * @param name
     * @return
     */
    public static String formatName(String name){
        return name.substring(0,name.indexOf('.'));
    }
}
