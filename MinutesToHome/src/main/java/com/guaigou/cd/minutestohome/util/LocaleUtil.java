package com.guaigou.cd.minutestohome.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;

import java.util.List;

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
     * 判断用户是否选择了小区
     * @param context
     * @return
     */
    public static boolean hasChooseRegion(Context context){
        RegionEntity regionEntity = RegionPrefs.getRegionData(context);
        return regionEntity != null && !TextUtils.isEmpty(regionEntity.getId());
    }

    /**
     * 判断列表数据是否为空
     * @param data
     * @return
     */
    public static boolean isListEmpty(List<?> data){
        return data == null || data.isEmpty();
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

    public static ProductEntity format2ProductEntity(CartEntity entity){
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(entity.getId());
        productEntity.setName(entity.getName());
        productEntity.setBegin(entity.getBegin());
        productEntity.setPrice(entity.getSalePrice());
        productEntity.setEnd(entity.getEnd());
        productEntity.setImg(entity.getImgPath());
        productEntity.setInfo(entity.getInfo());
        productEntity.setPromote(entity.getPromote());
        productEntity.setStandard(entity.getStandard());
        productEntity.setReserve(entity.getStock());
        productEntity.setKind(entity.getKind());
        productEntity.setNumber(entity.getAmount());
        return productEntity;
    }

    public static CartEntity format2CartEntity(ProductEntity entity){
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId(entity.getId());
        cartEntity.setName(entity.getName());
        cartEntity.setBegin(entity.getBegin());
        cartEntity.setSalePrice(entity.getPrice());
        cartEntity.setEnd(entity.getEnd());
        cartEntity.setImgPath(entity.getImg());
        cartEntity.setInfo(entity.getInfo());
        cartEntity.setPromote(entity.getPromote());
        cartEntity.setStandard(entity.getStandard());
        cartEntity.setStock(entity.getReserve());
        cartEntity.setKind(entity.getKind());
        cartEntity.setAmount(1);
        return cartEntity;
    }
}
