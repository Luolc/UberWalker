package com.luolc.uberwalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class UberReceiver extends BroadcastReceiver {

    private static final String TAG = "UberReceiver";

    public UberReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.log.v("Get Broadcat");
        try {
            String action = intent.getAction();
            if(intent.getExtras() == null) // 不是想要的消息
                return;
            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
            //获取消息内容，JSON格式
            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));

        } catch (JSONException e) {
            Log.v(TAG, "JSONException: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
