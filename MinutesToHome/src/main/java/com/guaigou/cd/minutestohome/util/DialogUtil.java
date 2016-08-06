package com.guaigou.cd.minutestohome.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.guaigou.cd.minutestohome.activity.login.LoginActivity;

/**
 * Created by weylen on 2016-08-06.
 */
public class DialogUtil {

    public static void showLoginDialog(Context context){
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("您必须登录才能继续操作")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }).show();
    }
}
