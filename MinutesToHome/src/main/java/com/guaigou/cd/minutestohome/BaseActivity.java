package com.guaigou.cd.minutestohome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016-06-17.
 */
public class BaseActivity extends AppCompatActivity{

    private Toast mToast;
    private static Activity activity;

    protected void showToast(String message){
        if (mToast == null){
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    private ProgressDialog progressDialog;
    protected void showProgressDialog(String message){
        dismissProgressDialog();
        progressDialog = ProgressDialog.show(this, "", message);
    }

    protected void dismissProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    protected void showSnakeView(View containerView, String message){
        Snackbar.make(containerView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getApplication()).addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        activity = null;
        ((BaseApplication)getApplication()).removeActivity(this);
    }

    public static Context getCurrentContext(){
        return activity;
    }
}
