package com.luolc.uberwalker;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
