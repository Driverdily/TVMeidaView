package com.hy.tvmeidaview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import androidx.leanback.app.BackgroundManager;

/**
 * 功能描述：
 * Created by 卢
 * 日期  2020/7/16
 */
public class SimpleBackgroundManager {
    private static final String TAG = SimpleBackgroundManager.class.getSimpleName();

    private final int DEFAULT_BACKGROUND_RES_ID = R.drawable.default_background;
    private static Drawable mDefaultBackground;

    private Activity mActivity;
    private BackgroundManager mBackgroundManager;

    public SimpleBackgroundManager(Activity activity) {
        this.mActivity = mActivity;
        mDefaultBackground=activity.getDrawable(DEFAULT_BACKGROUND_RES_ID);
        mBackgroundManager=BackgroundManager.getInstance(activity);
        mBackgroundManager.attach(activity.getWindow());
        activity.getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
    }
    public void updateBackground(Drawable drawable){
        mBackgroundManager.setDrawable(drawable);
    }
    public void clearBackground(){
        mBackgroundManager.setDrawable(mDefaultBackground);
    }

}
