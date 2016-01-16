package com.luolc.uberwalker;

/**
 * Created by LuoLiangchen on 16/1/16.
 */
public interface Callback<T> {

    void onFinished(int code, T data);

    void onError(String msg);
}
