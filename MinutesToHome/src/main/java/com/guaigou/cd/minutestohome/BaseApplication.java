package com.guaigou.cd.minutestohome;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016-06-17.
 */
public class BaseApplication extends Application{

    public static Context INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = getApplicationContext();
    }
}
