package com.guaigou.cd.minutestohome.util;

/**
 * Created by weylen on 2016-07-27.
 */
public class ParseUtil {

    public static int parseInt(String source){
        try{
            return Integer.parseInt(source);
        }catch (NumberFormatException e){
            return -1000;
        }
    }
}
