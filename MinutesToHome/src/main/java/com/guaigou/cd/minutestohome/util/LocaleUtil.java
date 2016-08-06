package com.guaigou.cd.minutestohome.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by weylen on 2016-08-03.
 */
public class LocaleUtil {

    /**
     * 是否有促销信息
     * @param promotionPrice
     * @return true 有促销
     */
    public static boolean hasPromotion(String promotionPrice){
        return (!TextUtils.isEmpty(promotionPrice)) && (!"-1".equalsIgnoreCase(promotionPrice));
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context){
        boolean isConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network != null){
            isConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return isConnFlag;
    }
}
