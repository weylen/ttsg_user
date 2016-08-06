package com.guaigou.cd.minutestohome.util;

import android.text.TextUtils;

import com.guaigou.cd.minutestohome.BaseApplication;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.entity.AccountEntity;

/**
 * Created by weylen on 2016-08-06.
 */
public class SessionUtil {

    private static String sessionId = null;

    public static String getSessionId(){
        if (TextUtils.isEmpty(sessionId)){
            AccountEntity accountEntity = LoginData.INSTANCE.getAccountEntity(BaseApplication.INSTANCE);
            if (accountEntity == null || accountEntity.getSid() == null){
                return "";
            }
            sessionId = "JSESSIONID=" + accountEntity.getSid();
        }
        return sessionId;
    }
}
