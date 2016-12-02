package com.guaigou.cd.minutestohome.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by weylen on 2016-11-03.
 */
public class PhonePrefs {

    private static final String DB_NAME = "phone_db";
    public static final void savePhone(Context context, String phone, String pwd){
        SharedPreferences preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString("phone", phone)
                .putString("password", pwd).commit();
    }

    public static final String getPhone(Context context){
        SharedPreferences preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        return preferences.getString("phone", "");
    }

    public static final String getPwd(Context context){
        SharedPreferences preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        return preferences.getString("password", "");
    }
}
