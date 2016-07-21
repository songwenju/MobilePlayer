package com.wjustudio.mobileplayer.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wjustudio.mobileplayer.Bean.Audio;
import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.utils.LogUtil;

/**
 * 作者： songwenju on 2016/7/18 08:39.
 * 邮箱： songwenju@outlook.com
 */
public class AudioListAdapter extends CursorAdapter {
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AudioListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AudioListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.fragment_audio_list_item,null);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Audio audio = Audio.instanceFromCursor(cursor);
        LogUtil.i(this,audio.toString());
        viewHolder.audioName.setText(audio.name);
        viewHolder.artist.setText(audio.artist);
        
    }

    public static class ViewHolder{
        TextView audioName,artist;

        ViewHolder(View view){
            audioName = (TextView) view.findViewById(R.id.tv_audio_name);
            artist = (TextView) view.findViewById(R.id.tv_audio_artist);
        }

    }
}
