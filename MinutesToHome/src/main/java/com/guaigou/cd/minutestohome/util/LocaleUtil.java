package com.guaigou.cd.minutestohome.util;

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

}
