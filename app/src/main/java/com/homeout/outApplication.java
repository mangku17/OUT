package com.homeout;

import android.app.Application;

import java.util.ArrayList;


public class outApplication  extends Application {

    public double nowLatitude, nowLongitude;
    public String homeState;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public double getNowLatitude() {
        return nowLatitude;
    }

    public void setNowLatitude(double nowLatitude) {
        this.nowLatitude = nowLatitude;
    }

    public double getNowLongitude() {
        return nowLongitude;
    }

    public void setNowLongitude(double nowLongitude) {
        this.nowLongitude = nowLongitude;
    }

    public String getHomeState() {
        return homeState;
    }

    public void setHomeState(String homeState) {
        this.homeState = homeState;
    }
}
