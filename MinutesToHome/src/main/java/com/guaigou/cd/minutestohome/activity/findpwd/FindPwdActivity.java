package com.guaigou.cd.minutestohome.activity.findpwd;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.register.RegisterPreseter;
import com.guaigou.cd.minutestohome.activity.resetpwd.ReSetPwdActivity;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdActivity;
import com.guaigou.cd.minutestohome.util.ValidateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-23.
 */
public class FindPwdActivity extends BaseActivity implements FindPwdView{


    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.text_validatecode)
    TextView vertifyView; // 验证码视图
    @Bind(R.id.user_login_name)
    EditText mUserLoginName;
    @Bind(R.id.user_login_validatecode)
    EditText mValidateCodeView;
    @Bind(R.id.Container)
    View containerView;

    private FindPwdPreseter findPwdPreseter;
    private String validateCode; // 验证码
    private static final int MAX_TIME = 1 * 60 * 1000;

    // 第一个参数是总时间 第二个参数是间隔时间
    private CountDownTimer countDownTimer = new CountDownTimer(MAX_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            vertifyView.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            vertifyView.setEnabled(true);
            vertifyView.setText(R.string.GetValidateCode);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mTextTitle.setText("找回密码");

        findPwdPreseter = new FindPwdPreseter(this);
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        finish();
    }

    private String phoneNum = "";
    /**
     * 获取验证码点击
     */
    @OnClick(R.id.text_validatecode)
    void onRequestValidateCodeClick(){
        phoneNum = mUserLoginName.getText().toString();
        if (TextUtils.isEmpty(phoneNum) || !ValidateUtil.isMobile(phoneNum)){
            showSnakeView(containerView, "请输入正确的手机号码");
            return;
        }
        // 设置不可用
        vertifyView.setEnabled(false);
        // 开始倒计时
        countDownTimer.start();
        findPwdPreseter.requestValidateCode(phoneNum);
    }

    @OnClick(R.id.text_nextstep)
    void onNextStepClick(){
        String code = mValidateCodeView.getText().toString();
        if (TextUtils.isEmpty(code)){
            showSnakeView(containerView, "请输入验证码");
            return;
        }
        if (!code.equalsIgnoreCase(validateCode)){
            showSnakeView(containerView, "验证码不正确");
            return;
        }
        String num = mUserLoginName.getText().toString();
        if (TextUtils.isEmpty(num) || !ValidateUtil.isMobile(num)){
            showSnakeView(containerView, "请输入正确的电话号码");
            return;
        }
        if (!num.equalsIgnoreCase(phoneNum)){
            showSnakeView(containerView, "电话号码和发送短信验证码的号码不一致");
            return;
        }

        Intent intent = new Intent(this, ReSetPwdActivity.class);
        intent.putExtra("PhoneNum", phoneNum);
        intent.putExtra("ValidateCode", validateCode);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestStart() {
        showProgressDialog("发送中");
    }

    @Override
    public void onRequestFailure(String errorMessage) {
        dismissProgressDialog();
        showSnakeView(containerView, errorMessage);
    }

    @Override
    public void onRequestSuccess(String result) {
        dismissProgressDialog();
        validateCode = result;
    }
}
