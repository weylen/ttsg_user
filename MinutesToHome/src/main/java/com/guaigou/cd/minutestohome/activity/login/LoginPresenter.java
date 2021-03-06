package com.guaigou.cd.minutestohome.activity.login;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
import com.guaigou.cd.minutestohome.prefs.LoginPrefs;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.DeviceUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-20.
 */
public class LoginPresenter implements BasePresenter {

    private LoginView loginView;

    public LoginPresenter(@NonNull LoginView loginView){
       this.loginView = Preconditions.checkNotNull(loginView, "LoginView can not be null");
    }

    @Override
    public void start() {

    }

    /**
     * 登录
     * @param user
     * @param pwd
     */
    public void login(String user, String pwd, String deviceId){
        loginView.showWaitDialog();
        Client.request()
                .login(user, pwd, 2, deviceId)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        loginView.dismissWaitDialog();
                        loginView.loginFailed();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        parseLoginData(s);
                        loginView.dismissWaitDialog();
                    }
                });
    }

    /**
     * 解析登录数据
     * @param source
     */
    private void parseLoginData(JsonObject source){
        int status = ResponseMgr.getStatus(source);
        if (status != 1){
            loginView.loginFailed();
        }else{
            Gson gson = new Gson();
            AccountEntity entity = gson.fromJson(ResponseMgr.getData(source), AccountEntity.class);
            // 保存登录信息
            LoginData.INSTANCE.setAccountEntity(entity);
            loginView.loginSuccess(entity);
        }
    }
}
