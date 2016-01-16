package com.luolc.uberwalker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVOSCloud;
import com.google.gson.Gson;
import com.luolc.uberwalker.Manager.ApiManager;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class AppContext extends Application {
    private static final String TAG = "AppContext";

    private SharedPreferences pref;
    private Gson gson = new Gson();

    String uid;

    @Override
    public void onCreate() {
        super.onCreate();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        AVOSCloud.initialize(this, "do4K7UeJcyGgwQ5mdeW4ep07-gzGzoHsz", "5bv5WWc0LuplJbEoadg4TmLH");

        // 初始化ApiManager
        ApiManager.newInstance(this);

        initUid();
    }

    public String getUid() {
        return uid;
    }

    private void initUid() {
        uid = pref.getString("uid", "");
    }
}
