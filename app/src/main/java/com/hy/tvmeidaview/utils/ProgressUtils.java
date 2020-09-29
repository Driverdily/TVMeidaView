package com.hy.tvmeidaview.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hy.tvmeidaview.R;


/**
 * 功能描述：
 * Created by 卢
 * 日期  2019/4/15
 */
public class ProgressUtils extends ProgressDialog {
    protected Context mContext;
    private TextView tv_load;

    public ProgressUtils(Context context) {
        super(context);
    }

    public ProgressUtils(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        setContentView(R.layout.layout_progress_loading);//loading的xml文件
        ImageView iv = findViewById(R.id.iv_loading);
        tv_load = findViewById(R.id.tv_load);
        AnimationDrawable ani = (AnimationDrawable) iv
                .getDrawable();
        ani.start();
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    public void setContent(String ss) {
        tv_load.setText(ss);
    }

    @Override
    public void dismiss() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (!activity.isFinishing()) {
                super.dismiss();
            }
        }
    }
    @Override
    public void show() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (mContext != null & activity != null & !activity.isFinishing()) {
                   super.show();
            }
        }
    }


}
