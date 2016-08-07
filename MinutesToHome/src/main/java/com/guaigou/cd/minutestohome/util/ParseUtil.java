package com.guaigou.cd.minutestohome.util;

import android.text.TextUtils;

/**
 * Created by weylen on 2016-07-27.
 */
public class ParseUtil {

    public static int parseInt(String source){
        if (!TextUtils.isEmpty(source) && source.matches("^//d+$")){
            return Integer.parseInt(source);
        }else {
            return -10000;
        }
    }
}
