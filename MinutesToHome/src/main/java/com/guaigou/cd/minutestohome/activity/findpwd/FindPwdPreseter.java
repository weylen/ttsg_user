package com.guaigou.cd.minutestohome.activity.findpwd;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.activity.register.RegisterView;
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
public class FindPwdPreseter implements BasePresenter{

    private FindPwdView findPwdView;
    public FindPwdPreseter(FindPwdView findPwdView){
        this.findPwdView = Preconditions.checkNotNull(findPwdView, "RegisterView can not be null");
    }

    @Override
    public void start() {

    }

    /**
     * 获取验证码
     * @param phoneNum 手机号码
     */
    void requestValidateCode(String phoneNum){
        findPwdView.onRequestStart();

        RetrofitFactory.getRetrofit().create(HttpService.class)
                .findPwdValidateCode(phoneNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("FindPwdPreseter 找回密码验证码获取失败：" + e.getMessage());
                        findPwdView.onRequestFailure("请求失败，请重新操作");
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("FindPwdPreseter 找回密码验证码获取成功：" + s);
                        if (ResponseMgr.getStatus(s) != 1){
                            findPwdView.onRequestFailure(s.get("data").getAsString());
                        } else {
                            findPwdView.onRequestSuccess(s.get("data").getAsString());
                        }
                    }
                });
    }

}
