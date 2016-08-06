package com.guaigou.cd.minutestohome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.guaigou.cd.minutestohome.activity.region.RegionActivity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.LocaleUtil;

/**
 * Created by weylen on 2016-08-06.
 */
public class SplashActivity extends BaseActivity{

    private boolean isSettingNetwork = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LocaleUtil.isNetworkConnected(getApplicationContext())){
            showAlertDialog();
        }else {
            new Handler().postDelayed(() -> {
                // 没有选择小区
                if (RegionPrefs.getRegionData(getApplicationContext()) == null){
                    Intent intent = new Intent(getApplicationContext(), RegionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
            }, isSettingNetwork ? 300 : 2000);
        }
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("提示")
                .setMessage("当前无网络连接")
                .setCancelable(false)
                .setPositiveButton("设置", (dialog, which) -> {
                    peekInSetting();
                })
                .setNegativeButton("退出", (dialog, which) ->{
                    finish();
                })
                .show();
    }

    private void peekInSetting(){
        Intent intent;
        // 先判断当前系统版本
        if(android.os.Build.VERSION.SDK_INT > 10){  // 3.0以上
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
            intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        }
        startActivity(intent);
        isSettingNetwork = true;
    }
}
