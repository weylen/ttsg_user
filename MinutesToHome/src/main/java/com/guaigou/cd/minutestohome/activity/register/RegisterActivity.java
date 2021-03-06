package com.guaigou.cd.minutestohome.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.resetpwd.ReSetPwdActivity;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdActivity;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;
import com.guaigou.cd.minutestohome.util.ValidateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-23.
 */
public class RegisterActivity extends BaseActivity implements RegisterView{


    @Bind(R.id.text_title) TextView mTextTitle;
    @Bind(R.id.text_validatecode) TextView vertifyView; // 验证码视图
    @Bind(R.id.user_login_name) EditText mUserLoginName;
    @Bind(R.id.user_login_validatecode) EditText mValidateCodeView;
    @Bind(R.id.Container) View containerView;

    private RegisterPresenter presenter;
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

        mTextTitle.setText(R.string.Register);

        presenter = new RegisterPresenter(this);
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
            showToast("请输入正确的手机号码");
            return;
        }
        // 设置不可用
        vertifyView.setEnabled(false);
        // 开始倒计时
        countDownTimer.start();
        presenter.requestValidateCode(phoneNum);
    }

    @OnClick(R.id.text_nextstep)
    void onNextStepClick(){
        KeyboardUtil.hide(this, mUserLoginName);

        String code = mValidateCodeView.getText().toString();
        if (TextUtils.isEmpty(code) || code.length() != 6){
            showToast("请输入6位验证码");
            return;
        }
        String num = mUserLoginName.getText().toString();
        if (TextUtils.isEmpty(num) || !ValidateUtil.isMobile(num)){
            showToast("请输入正确的电话号码");
            return;
        }
        if (!num.equalsIgnoreCase(phoneNum)){
            showToast("电话号码和发送短信验证码的号码不一致");
            return;
        }

        KeyboardUtil.hide(this, mValidateCodeView);
        validate(num, code);
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
        showProgressDialog("请求中");
    }

    @Override
    public void onRequestFailure() {
        dismissProgressDialog();
        showToast("请求失败，请重新操作");
        resetCounter();
    }

    @Override
    public void onRequestSuccess(int status, String result) {
        dismissProgressDialog();
        if(status == -2){
            showToast("该号码已经注册");
            resetCounter();
        }else if (status == 1){
            showToast("验证码发送成功");
        }else {
            resetCounter();
            showToast("验证码发送失败，请重试");
        }
    }

    private void resetCounter(){
        countDownTimer.cancel();
        countDownTimer.onFinish();
    }

    private void validate(String phone, String code){
        showProgressDialog("请稍后...");
        Client.request()
                .validateCode(phone, code)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        dismissProgressDialog();
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            showToast("验证码或者手机号码不正确");
                            return;
                        }
                        Intent intent = new Intent(RegisterActivity.this, SetPwdActivity.class);
                        intent.putExtra("PhoneNum", phoneNum);
                        startActivity(intent);
                    }
                });
    }
}
