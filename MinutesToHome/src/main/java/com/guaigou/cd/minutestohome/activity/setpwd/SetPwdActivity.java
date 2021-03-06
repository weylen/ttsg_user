package com.guaigou.cd.minutestohome.activity.setpwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.login.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-23.
 * 设置用户密码
 */
public class SetPwdActivity extends BaseActivity implements SetPwdView{

    @Bind(R.id.text_title) TextView mTextTitle;
    @Bind(R.id.user_login_name) EditText mPwdView;
    @Bind(R.id.user_login_pass) EditText mRePwdView;

    private SetPwdPreseter setPwdPreseter;
    private String phoneNum; // 手机号码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpwd);
        ButterKnife.bind(this);

        phoneNum = getIntent().getStringExtra("PhoneNum");

        mTextTitle.setText(R.string.SetPwd);

        setPwdPreseter = new SetPwdPreseter(this);
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

        if (pwd1.length() <= 5){
            showToast("密码长度必须大于6位");
            return;
        }

        if (!pwd1.equalsIgnoreCase(pwd2)){
            showToast("两次密码不一致");
            return;
        }

        setPwdPreseter.register(phoneNum, pwd1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestStart() {
        showProgressDialog("注册中...");
    }

    @Override
    public void onRequestFailure() {
        dismissProgressDialog();
        showToast("注册失败，请重新操作");
    }

    @Override
    public void onRequestSuccess(int status) {
        dismissProgressDialog();

        String message = "";
        switch (status){
            case -1:
                message = "服务器忙，注册失败";
                break;
            case 1:
                message = "注册成功";
                break;
            case 2:
                message = "数据不完整";
                break;
            case 3:
                message = "该号码已被注册，请更换";
                break;
            case 4:
                message = "注册失败";
                break;
        }
        showToast(message);
        if (status == 1){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
