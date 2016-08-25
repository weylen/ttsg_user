package com.guaigou.cd.minutestohome.activity.login;


import android.content.Context;

import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.prefs.LoginPrefs;
import com.guaigou.cd.minutestohome.util.SessionUtil;

/**
 * Created by weylen on 2016-07-21.
 */
public enum LoginData {

    INSTANCE;

    private AccountEntity accountEntity;
    public AccountEntity getAccountEntity(Context context) {
        if (accountEntity == null){
            accountEntity = LoginPrefs.getAccountInfo(context);
        }
        return accountEntity;
    }

    public void setAccountEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    public boolean isLogin(Context context){
        if (getAccountEntity(context) == null){
            return false;
        }
        return true;
    }

    public void logout(Context context){
        SessionUtil.sessionId = null;
        accountEntity = null;
        LoginPrefs.setAccountInfo(context, null);
    }
}
