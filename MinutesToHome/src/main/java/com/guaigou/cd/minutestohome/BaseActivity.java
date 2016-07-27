package com.guaigou.cd.minutestohome;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.guaigou.cd.minutestohome.util.KeybordUtil;

/**
 * Created by Administrator on 2016-06-17.
 */
public class BaseActivity extends AppCompatActivity{

    private Toast mToast;

    protected void showToast(String message){
        if (mToast == null){
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    private ProgressDialog progressDialog;
    protected void showProgressDialog(String message){
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
}
