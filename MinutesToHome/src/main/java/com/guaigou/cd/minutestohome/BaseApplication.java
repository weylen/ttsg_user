package com.guaigou.cd.minutestohome;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-06-17.
 */
public class BaseApplication extends Application{

    // user your appid the key.
    public static final String APP_ID = "2882303761517505998";
    // user your appid the key.
    public static final String APP_KEY = "5171750548998";

    public static final String TAG = "zhou";

    public static Context INSTANCE;

    private static List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = getApplicationContext();
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        CrashReport.initCrashReport(this, "0c87c68816", true);

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void removeActivity(Activity activity){
        if (activity != null){
            activityList.remove(activity);
        }
    }

    public static void exit(){
        for (Activity activity : activityList){
            if (activity != null){
                activity.finish();
            }
        }
        activityList.clear();
    }

    public static Activity getLast(){
        if (!LocaleUtil.isListEmpty(activityList)){
            return activityList.get(activityList.size()-1);
        }
        return null;
    }

}
