package com.guaigou.cd.minutestohome.activity.login;


import android.content.Context;

import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.prefs.LoginPrefs;

/**
 * Created by weylen on 2016-07-21.
 */
public enum LoginData {

    INSTANCE;

    private AccountEntity accountEntity;

    public AccountEntity getAccountEntity() {
        return accountEntity;
    }

    public void setAccountEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    private boolean isLogin;

    public boolean isLogin(Context context){
        if (accountEntity == null){
            accountEntity = LoginPrefs.getAccountInfo(context);
            if (accountEntity == null){
                return false;
            }
        }
        return true;
    }
}
