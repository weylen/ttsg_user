package com.guaigou.cd.minutestohome.activity.login;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.entity.AccountEntity;

/**
 * Created by weylen on 2016-07-20.
 */
public interface LoginView extends BaseView<LoginPresenter> {

    void showWaitDialog();
    void dismissWaitDialog();
    void loginSuccess(AccountEntity accountEntity);
    void loginFailed();
}
