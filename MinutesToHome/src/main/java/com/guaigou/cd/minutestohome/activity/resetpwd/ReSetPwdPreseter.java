package com.guaigou.cd.minutestohome.activity.resetpwd;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdView;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-23.
 */
public class ReSetPwdPreseter implements BasePresenter{

    private ReSetPwdView reSetPwdView;
    public ReSetPwdPreseter(ReSetPwdView reSetPwdView){
        this.reSetPwdView = Preconditions.checkNotNull(reSetPwdView, "ReSetPwdView can not be null");
    }

    @Override
    public void start() {

    }

    /**
     * 获取验证码
     * @param phoneNum 手机号码
     */
    void register(String phoneNum, String pwd){
        reSetPwdView.onRequestStart();

        RetrofitFactory.getRetrofit().create(HttpService.class)
                .register(phoneNum, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(e.getMessage());
                        reSetPwdView.onRequestFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SetPwdPreseter onNext s:" + s);
                        reSetPwdView.onRequestSuccess(ResponseMgr.getStatus(s));
                    }
                });
    }

}
