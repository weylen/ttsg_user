package com.guaigou.cd.minutestohome.activity.findpwd;

/**
 * Created by weylen on 2016-07-23.
 */
public interface FindPwdView{

    void onRequestStart();

    void onRequestFailure(String errorMessage);

    /**
     * 请求成功
     * @param result -1：服务器忙，获取验证码失败，需要重新获取
     *  1：该号码已经注册
     *  六位字符串：已经发送的验证码
     */
    void onRequestSuccess(String result);
}
