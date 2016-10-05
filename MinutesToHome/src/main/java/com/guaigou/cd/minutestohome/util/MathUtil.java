package com.guaigou.cd.minutestohome.util;

import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.CartEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by weylen on 2016-08-07.
 */
public class MathUtil {

    /**
     * double 相加
     * @param d1
     * @param d2
     * @return
     */
    public static String sum(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.add(bd2).setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * double 乘法
     * @param d1
     * @param d2
     * @return
     */
    public static String mul(String d1,String d2){
        BigDecimal bd1 = new BigDecimal(d1);
        BigDecimal bd2 = new BigDecimal(d2);
        return bd1.multiply(bd2).setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static String calculate(){
        List<CartEntity> data = CartData.INSTANCE.getData();
        String allPrice = "0";
        if (data != null && data.size() != 0){
            for (CartEntity entity : data){
                int number = entity.getAmount();
                String price;
                // 促销价格
                String promotionPrice = entity.getPromote();
                if (LocaleUtil.hasPromotion(promotionPrice, entity.getEnd())){
                    price = promotionPrice;
                }else {
                    price = entity.getSalePrice();
                }
                allPrice = sum(allPrice, mul(String.valueOf(number), price));
            }
        }
        return allPrice;
    }
}
