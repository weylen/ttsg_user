package com.guaigou.cd.minutestohome.activity.resetpwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.login.LoginActivity;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdPreseter;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-23.
 */
public class ReSetPwdActivity extends BaseActivity implements ReSetPwdView{

    @Bind(R.id.text_title) TextView mTextTitle;
    @Bind(R.id.user_login_name) EditText mPwdView;
    @Bind(R.id.user_login_pass) EditText mRePwdView;
    @Bind(R.id.Container) View containerView;

    private ReSetPwdPreseter reSetPwdPreseter;
    private String phoneNum; // 手机号码
    private String validateCode; // 验证码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpwd);
        ButterKnife.bind(this);

        phoneNum = getIntent().getStringExtra("PhoneNum");
        validateCode = getIntent().getStringExtra("ValidateCode");

        mTextTitle.setText(R.string.ResetPwd);

        reSetPwdPreseter = new ReSetPwdPreseter(this);
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        finish();
    }

    @OnClick(R.id.text_register)
    void onRegisterClick(){
        String pwd1 = mPwdView.getText().toString();
        String pwd2 = mRePwdView.getText().toString();
        if (TextUtils.isEmpty(pwd1)){
            showToast("请输入密码");
            return;
        }

        if (TextUtils.isEmpty(pwd2)){
            showToast("请再次输入密码");
            return;
        }

        if (pwd1.length() <= 5 || pwd2.length() <= 5){
            showToast("密码长度必须大于6位");
            return;
        }

        if (!pwd1.equalsIgnoreCase(pwd2)){
            showToast("两次密码不一致");
            return;
        }

        reSetPwdPreseter.resePwd(phoneNum, pwd1, validateCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestStart() {
        showProgressDialog("重置密码中...");
    }

    @Override
    public void onRequestFailure(String errorMessage) {
        dismissProgressDialog();
        showToast(errorMessage);
    }

    @Override
    public void onRequestSuccess() {
        dismissProgressDialog();
        showToast("重置密码成功");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
