package com.wjustudio.mobileplayer.appBase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wjustudio.mobileplayer.R;
import com.wjustudio.mobileplayer.utils.ToastUtil;

/**
 * 作者： songwenju on 2016/6/16 07:50.
 * 邮箱： songwenju@outlook.com
 */


public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View  view = View.inflate(mContext,onBindLayout(),null);
        onInitView(view);
        onSetViewData();
        onInitData();
        registerCommonButton(view);
        return view;
    }



    /**
     * 通用的button
     * @param v
     */
    private void registerCommonButton(View v) {
        View view = v.findViewById(R.id.back);
        if (view != null){
            view.setOnClickListener(this);
        }
    }

    /**
     * [绑定布局]
     */
    public abstract int onBindLayout();

    /**
     * [初始化控件]
     * @param view
     */
    public abstract void onInitView(View view);

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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
           getFragmentManager().popBackStack();
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
        intent.setClass(mContext, clz);
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
        intent.setClass(mContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

}

