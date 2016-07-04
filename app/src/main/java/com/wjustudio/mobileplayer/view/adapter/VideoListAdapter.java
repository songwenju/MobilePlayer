package com.wjustudio.mobileplayer.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wjustudio.mobileplayer.Bean.Video;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.utils.TimeUtil;

/**
 * CursorAdapter
 * 作者： songwenju on 2016/6/30 22:33.
 * 邮箱： songwenju@outlook.com
 */
public class VideoListAdapter extends CursorAdapter {

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public VideoListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //用于创建新的view
        View view = View.inflate(context, R.layout.fragment_video_list_item,null);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //用于填充view
        ViewHolder holder = (ViewHolder) view.getTag();
        Video video = Video.instanceFromCursor(cursor);
        holder.tvVideoName.setText(video.name);
        holder.tvVideoSize.setText(Formatter.formatFileSize(mContext,video.size));
        holder.tvVideoTime.setText(TimeUtil.formatTime(video.duration));

    }

    public static class ViewHolder{
        TextView tvVideoName,tvVideoSize,tvVideoTime;

        public ViewHolder(View view) {
            tvVideoName  = (TextView) view.findViewById(R.id.tv_video_name);
            tvVideoSize  = (TextView) view.findViewById(R.id.tv_video_size);
            tvVideoTime  = (TextView) view.findViewById(R.id.tv_video_time);
        }
    }
}
