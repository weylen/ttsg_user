package com.guaigou.cd.minutestohome.prefs;

import android.content.Context;

import com.guaigou.cd.minutestohome.entity.AccountEntity;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by weylen on 2016-07-23.
 */
public class LoginPrefs {

    public static final String LOGIN_PREFS_DB = "login_prefs.db";

    public static void setAccountInfo(Context context, AccountEntity accountEntity){
        try {
            if (accountEntity != null){
                // 保存手机号
                PhonePrefs.savePhone(context, accountEntity.getUname());
            }
            OutputStream outputStream =context.openFileOutput(LOGIN_PREFS_DB, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(accountEntity);
            oos.flush();
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AccountEntity getAccountInfo(Context context){
        AccountEntity accountEntity = null;
        try {
            InputStream in = context.openFileInput(LOGIN_PREFS_DB);
            ObjectInputStream ois = new ObjectInputStream(in);
            accountEntity = (AccountEntity) ois.readObject();
            ois.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountEntity;
    }
}
