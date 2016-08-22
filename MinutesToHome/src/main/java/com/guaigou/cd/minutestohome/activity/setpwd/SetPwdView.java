package com.guaigou.cd.minutestohome.activity.setpwd;

/**
 * Created by weylen on 2016-07-23.
 */
public interface SetPwdView{

    void onRequestStart();

    void onRequestFailure();

    /**
     * 请求成功
     * @param status
     */
    void onRequestSuccess(int status);
}
