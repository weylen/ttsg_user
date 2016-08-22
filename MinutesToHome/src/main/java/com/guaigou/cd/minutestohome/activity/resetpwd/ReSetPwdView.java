package com.guaigou.cd.minutestohome.activity.resetpwd;

/**
 * Created by weylen on 2016-07-23.
 */
public interface ReSetPwdView{

    void onRequestStart();

    void onRequestFailure(String errorMessage);

    void onRequestSuccess();
}
