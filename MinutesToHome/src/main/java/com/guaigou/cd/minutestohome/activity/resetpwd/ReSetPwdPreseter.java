package com.guaigou.cd.minutestohome.activity.resetpwd;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdView;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
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
     * 重置密码
     * @param phoneNum
     *       手机号码
     * @param pwd
     *      新密码
     * @param validateCode
     *      验证码
     */
    void resePwd(String phoneNum, String pwd, String validateCode){
        reSetPwdView.onRequestStart();

        Client.request()
                .resetPwd(phoneNum, pwd, validateCode)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        reSetPwdView.onRequestFailure("请求失败，请重新操作");
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("ReSetPwdPreseter 重置密码成功：" + s);
                        if (ResponseMgr.getStatus(s) == 1){
                            reSetPwdView.onRequestSuccess();
                        }else {
                            reSetPwdView.onRequestFailure(s.get("data").getAsString());
                        }
                    }
                });
    }

}
