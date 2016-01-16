package com.luolc.uberwalker.Manager;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.luolc.uberwalker.AppContext;
import com.luolc.uberwalker.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public class ApiManager {
    private static final String TAG = "ApiManager";

    public static final int CODE_SUCCESS = 0;

    public static String PROTOCOL = "http";
    public static String DOMAIN = "pkurider.leanapp.cn";

    private static RequestQueue mReqQueue;
    private static ApiManager apiManager;
    private AppContext mContext;

    private ApiManager(Context context) {
        mContext = (AppContext) context.getApplicationContext();
        mReqQueue = Volley.newRequestQueue(mContext);
    }

    public synchronized static void newInstance(Context context) {
        if (apiManager == null) {
            apiManager = new ApiManager(context);
        }
    }

    public static ApiManager getInstance() {
        return apiManager;
    }



    /**
     * 构造接口地址
     * @return 接口地址
     */
    private String buildUrl(String method) {
        String url = PROTOCOL + "://" + DOMAIN + "/" + method;
        Log.v(TAG, "request url= " + url);
        return url;
    }

    /**
     * GET访问接口
     * @param method 接口方法
     * @param params GET的参数
     * @param listener 成功时回调
     * @param errorListener 失败时回调
     */
    public void get(String method, final ArrayList<Parameter> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = buildUrl(method);
        StringRequest stringRequest = new StringRequest(url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params == null || params.isEmpty()) return null;

                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < params.size(); ++i) {
                    map.put(params.get(i).name, params.get(i).value);
                }
                return map;
            }
        };

        mReqQueue.add(stringRequest);
    }

    /**
     * POST访问接口
     * @param method 接口方法
     * @param params POST的参数
     * @param listener 成功时回调
     * @param errorListener 失败时回调
     */
    public void post(String method, final ArrayList<Parameter> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = buildUrl(method);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < params.size(); ++i) {
                    map.put(params.get(i).name, params.get(i).value);
                }
                return map;
            }
        };

        mReqQueue.add(stringRequest);
    }

    public Parameter makeParam(String name, String value) {
        return new Parameter(name, value);
    }

    public class Parameter {
        public String name;
        public String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public void getOAuthUrl(final Callback<String> callback) {

    }
}
