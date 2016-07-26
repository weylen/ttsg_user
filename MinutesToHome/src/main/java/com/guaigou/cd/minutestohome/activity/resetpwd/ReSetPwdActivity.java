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

    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.user_login_name)
    EditText mPwdView;
    @Bind(R.id.user_login_pass)
    EditText mRePwdView;
    @Bind(R.id.Container)
    View containerView;

    private ReSetPwdPreseter reSetPwdPreseter;
    private String phoneNum; // 手机号码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpwd);
        ButterKnife.bind(this);

        phoneNum = getIntent().getStringExtra("PhoneNum");

        mTextTitle.setText(R.string.ResetPwd);

        reSetPwdPreseter = new ReSetPwdPreseter(this);
        setPresenter(reSetPwdPreseter);
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
            showSnakeView(containerView, "请输入密码");
            return;
        }

        if (TextUtils.isEmpty(pwd2)){
            showSnakeView(containerView, "请再次输入密码");
            return;
        }

        if (pwd1.length() <= 5){
            showSnakeView(containerView, "密码长度必须大于6位");
            return;
        }

        if (!pwd1.equalsIgnoreCase(pwd2)){
            showSnakeView(containerView, "两次密码不一致");
            return;
        }

        reSetPwdPreseter.register(phoneNum, pwd1);
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
        showSnakeView(containerView, "注册失败，请重新操作");
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
                message = "重置密码成功";
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
        showSnakeView(containerView, message);
        if (status == 1){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void setPresenter(ReSetPwdPreseter presenter) {

    }
}
