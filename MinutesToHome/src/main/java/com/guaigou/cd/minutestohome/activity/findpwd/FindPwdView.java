package com.guaigou.cd.minutestohome.activity.findpwd;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.activity.register.RegisterPreseter;

/**
 * Created by weylen on 2016-07-23.
 */
public interface FindPwdView extends BaseView<FindPwdPreseter>{

    void onRequestStart();

    void onRequestFailure();

    /**
     * 请求成功
     * @param result -1：服务器忙，获取验证码失败，需要重新获取
     *  1：该号码已经注册
     *  六位字符串：已经发送的验证码
     */
    void onRequestSuccess(String result);
}
