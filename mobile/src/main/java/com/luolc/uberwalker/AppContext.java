package com.luolc.uberwalker;

import android.app.Application;

import com.google.gson.Gson;
import com.luolc.uberwalker.Manager.ApiManager;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class AppContext extends Application {
    private static final String TAG = "AppContext";

    private Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化ApiManager
        ApiManager.newInstance(this);
    }
}
