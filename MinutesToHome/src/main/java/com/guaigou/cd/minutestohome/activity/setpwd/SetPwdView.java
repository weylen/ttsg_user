package com.guaigou.cd.minutestohome.activity.setpwd;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.activity.register.RegisterPreseter;

/**
 * Created by weylen on 2016-07-23.
 */
public interface SetPwdView extends BaseView<SetPwdPreseter>{

    void onRequestStart();

    void onRequestFailure();

    /**
     * 请求成功
     * @param status
     */
    void onRequestSuccess(int status);
}
