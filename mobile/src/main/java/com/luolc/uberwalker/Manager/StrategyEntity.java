package com.luolc.uberwalker.Manager;

/**
 * Created by LuoLiangchen on 16/1/17.
 */
public class StrategyEntity {
    int time;
    int to_work;
    int days_from_now;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTo_work() {
        return to_work;
    }

    public void setTo_work(int to_work) {
        this.to_work = to_work;
    }

    public int getDays_from_now() {
        return days_from_now;
    }

    public void setDays_from_now(int days_from_now) {
        this.days_from_now = days_from_now;
    }
}
