package com.guaigou.cd.minutestohome.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.findpwd.FindPwdActivity;
import com.guaigou.cd.minutestohome.activity.register.RegisterActivity;
import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.prefs.CartPrefs;
import com.guaigou.cd.minutestohome.prefs.LoginPrefs;
import com.guaigou.cd.minutestohome.util.CartUtil;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.DeviceUtil;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;
import com.guaigou.cd.minutestohome.util.SessionUtil;
import com.guaigou.cd.minutestohome.util.ValidateUtil;
import com.xiaomi.mipush.sdk.MiPushClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by weylen on 2016-07-23.
 */
public class LoginActivity extends BaseActivity implements LoginView{

    @Bind(R.id.text_title) TextView mTextTitle;
    @Bind(R.id.user_login_name) EditText mUserLoginName;
    @Bind(R.id.user_login_pass) EditText mUserLoginPass;
    @Bind(R.id.Container) View containerView;

    private LoginPresenter loginPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mTextTitle.setText(R.string.Login);
        loginPresenter = new LoginPresenter(this);

        mUserLoginPass.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                onLoginClick();
                return true;
            }
            return false;
        });
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        finish();
    }

    @OnClick(R.id.text_login)
    void onLoginClick(){
        String user = mUserLoginName.getText().toString();
        String pwd = mUserLoginPass.getText().toString();

        if (TextUtils.isEmpty(user)) {
            showToast("手机号必须输入");
            mUserLoginName.requestFocus();
            return;
        }

        if (!ValidateUtil.isMobile(user)){
            showToast("请输入正确的手机号");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            showToast("密码必须输入");
            mUserLoginPass.requestFocus();
            return;
        }
        KeyboardUtil.hide(this, mUserLoginName);
        loginPresenter.login(user, pwd, DeviceUtil.INSTANCE.getDeviceUuid(this));
    }

    @OnClick(R.id.text_register)
    void onRegisterClick(){
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.text_findpwd)
    void onFindPwdClick(){
        Intent intent = new Intent(this, FindPwdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void showWaitDialog() {
        showProgressDialog("登录中...");
    }

    @Override
    public void dismissWaitDialog() {
        dismissProgressDialog();
    }

    @Override
    public void loginSuccess(AccountEntity accountEntity) {
        LoginPrefs.setAccountInfo(getApplicationContext(), accountEntity);
        DebugUtil.d("LoginActivity 设备id：" + DeviceUtil.INSTANCE.getDeviceUuid(this));
        SessionUtil.sessionId = "JSESSIONID=" + accountEntity.getSid();
        MiPushClient.setAlias(this, DeviceUtil.INSTANCE.getDeviceUuid(this), null);

        String name = CartPrefs.getCartName(this);
        if (!TextUtils.isEmpty(name) && name.equalsIgnoreCase(accountEntity.getUname())){
            // 获取购物车信息
            remoteCart();
        }else {
            CartPrefs.saveCartData(this, null);
            finish();
        }
    }

    @Override
    public void loginFailed() {
        showToast("登录失败，请检查用户名或密码");
    }

    @Override
    public void setPresenter(LoginPresenter presenter) {}

    private void remoteCart(){
        CartUtil.INSTANCE.remoteCart(this, () -> onBackClick());
    }

}
