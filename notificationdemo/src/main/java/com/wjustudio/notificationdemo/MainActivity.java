package com.wjustudio.notificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示通知
     *
     * @param view
     */
    public void show(View view) {
//        showNormalNotification();
        showCustomNotification();
    }

    /**
     * 显示自定义的Notification
     */
    private void showCustomNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.notification_music_playing)  //图标
                .setTicker("正在播放：DiDa")                         //弹出
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
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.audio_layout);
        //设置文本
        remoteViews.setTextViewText(R.id.tv_audio_name, "嘀嗒");
        remoteViews.setTextViewText(R.id.tv_audio_author, "侃侃");

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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "下一曲");
        //第二个参数，requestCode不能相同，不然是更新了，即只显示最后一个相同id的内容。
        return PendingIntent.getActivities(this, 0, new Intent[]{intent},
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    /**
     * 上一曲的pendingIntent
     *
     * @return
     */
    private PendingIntent getPreIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "上一曲");
        return PendingIntent.getActivities(this, 1, new Intent[]{intent},
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * 获得PendingIntent
     *
     * @return PendingIntent
     */
    private PendingIntent getContentIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "我是从通知栏启动了");
        return PendingIntent.getActivities(this, 2, new Intent[]{intent},
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 普通的展示
     */
    private void showNormalNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.notification_music_playing)  //图标
                .setTicker("正在播放：DiDa")                         //弹出
                .setContentTitle("DiDa")                            //标题
                .setContentText("侃侃")                             //内容
                .setWhen(System.currentTimeMillis())                //通知的时间
                .setContentIntent(getContentIntent());              //点击通知栏的响应

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);                                      //开始通知
    }

    /**
     * 取消通知通知
     *
     * @param view
     */
    public void cancel(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);
    }
}
