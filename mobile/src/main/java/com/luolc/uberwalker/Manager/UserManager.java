package com.luolc.uberwalker.Manager;

import android.content.Context;

import com.luolc.uberwalker.AppContext;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class UserManager {
    private static final String TAG = "UserManager";

    private AppContext mContext;

    public UserManager(Context context) {
        mContext = (AppContext) context.getApplicationContext();
    }

    public String getUid() {
        return mContext.getUid();
    }
}
