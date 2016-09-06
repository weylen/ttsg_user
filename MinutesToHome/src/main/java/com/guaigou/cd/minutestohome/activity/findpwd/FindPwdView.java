package com.guaigou.cd.minutestohome.activity.findpwd;

/**
 * Created by weylen on 2016-07-23.
 */
public interface FindPwdView{

    void onRequestStart();

    void onRequestFailure(String errorMessage);

    /**
     *
     * @param status -1：服务器忙或数据不完整-2：该号码已经注册-3：服务器拒绝-5：短信发送失败，稍候再试，这种情况一般不会发生 1：已经发送的验证码
     * @param result
     */
    void onRequestSuccess(int status, String result);
}
