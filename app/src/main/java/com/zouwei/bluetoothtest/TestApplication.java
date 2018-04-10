package com.zouwei.bluetoothtest;

import android.app.Application;

/**
 * Created by WangMeng on 2018/4/10.
 */

public class TestApplication extends Application {

    public static TestApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static TestApplication getInstance() {
        return instance;
    }
}
