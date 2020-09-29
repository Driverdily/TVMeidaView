package com.hy.tvmeidaview;

import android.app.Application;
import android.content.Context;

/**
 * 功能描述：
 * Created by 卢
 * 日期  2020/8/22
 */
public class MyApp extends Application {
    public static Context mmcontext;
    @Override
    public void onCreate() {
        super.onCreate();
        mmcontext = this;
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");//默认false
        System.setProperty("jcifs.smb.client.soTimeout", "1000000");//超时
        System.setProperty("jcifs.smb.client.responseTimeout", "30000");//超时
    }
}
