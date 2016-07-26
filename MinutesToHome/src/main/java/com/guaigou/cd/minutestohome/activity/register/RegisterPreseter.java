package com.guaigou.cd.minutestohome.activity.register;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-23.
 */
public class RegisterPreseter implements BasePresenter{

    private RegisterView registerView;
    public RegisterPreseter(RegisterView registerView){
        this.registerView = Preconditions.checkNotNull(registerView, "RegisterView can not be null");
    }

    @Override
    public void start() {

    }

    /**
     * 获取验证码
     * @param phoneNum 手机号码
     */
    void requestValidateCode(String phoneNum){
        registerView.onRequestStart();

        RetrofitFactory.getRetrofit().create(HttpService.class)
                .requestValidateCode(phoneNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(e.getMessage());
                        registerView.onRequestFailure();
                    }

                    @Override
                    public void onNext(String s) {
                        DebugUtil.d("RegisterPreseter onNext s:" + s);
                        registerView.onRequestSuccess(s.toString());
                    }
                });
    }

}
