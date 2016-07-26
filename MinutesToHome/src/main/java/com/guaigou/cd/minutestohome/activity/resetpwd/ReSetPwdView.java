package com.guaigou.cd.minutestohome.activity.resetpwd;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.activity.setpwd.SetPwdPreseter;

/**
 * Created by weylen on 2016-07-23.
 */
public interface ReSetPwdView extends BaseView<ReSetPwdPreseter>{

    void onRequestStart();

    void onRequestFailure();

    /**
     * 请求成功
     * @param status
     */
    void onRequestSuccess(int status);
}
