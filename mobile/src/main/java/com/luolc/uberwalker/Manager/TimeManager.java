package com.luolc.uberwalker.Manager;

import android.content.Context;

import com.luolc.uberwalker.AppContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by LuoLiangchen on 16/1/17.
 */
public class TimeManager {
    private static final String TAG = "UserManager";

    private static final Calendar standard =
            new GregorianCalendar(2015, 0, 5);

    private AppContext mContext;

    public TimeManager(Context context) {
        mContext = (AppContext) context.getApplicationContext();
    }

    public static String format(long timestamp) {
        return format(timestamp, "HH:mm");
    }

    public static String format(long timestamp, String pattern) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            return simpleDateFormat.format(new Date(timestamp));
        } catch (Exception e) {
            return "";
        }
    }
}
