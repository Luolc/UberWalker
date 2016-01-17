package com.luolc.uberwalker;

import android.widget.ImageView;

/**
 * Created by LuoLiangchen on 16/1/17.
 */
public class ScheduleItemEntity {
    int imWayRes;
    String contentTest;
    String label;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getImWayRes() {
        return imWayRes;
    }

    public void setImWayRes(int imWayRes) {
        this.imWayRes = imWayRes;
    }

    public String getContentTest() {
        return contentTest;
    }

    public void setContentTest(String contentTest) {
        this.contentTest = contentTest;
    }
}
