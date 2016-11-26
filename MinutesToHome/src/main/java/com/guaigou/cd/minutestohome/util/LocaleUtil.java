package com.guaigou.cd.minutestohome.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.guaigou.cd.minutestohome.activity.market.ShopStatusData;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by weylen on 2016-08-03.
 */
public class LocaleUtil {


    public static double pareseDouble(String str){
        if (TextUtils.isEmpty(str)){
            return 0;
        }
        try {
            return Double.parseDouble(str);
        }catch (Exception e){}
        return 0;
    }

    /**
     * 是否有促销信息
     * @param promotionPrice
     * @return true 有促销
     */
    public static boolean hasPromotion(String promotionPrice, String endTime){
        if (!TextUtils.isEmpty(promotionPrice)){
            try{
                double d = Double.valueOf(promotionPrice);
                if (d > 0){ // 促销价格>0的时候 再检查时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(endTime);
                    if (System.currentTimeMillis() >= date.getTime()){
                        return false;
                    }
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
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
     * 判断购物车的商品是否已经下架
     * @param cartEntity
     * @return
     */
    public static boolean isShelves(CartEntity cartEntity){
        return "2".equalsIgnoreCase(cartEntity.getStauts());
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

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    public static String getIp(Context context){
        String ip;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            ip = int2ip(i);
        }else {
            ip = getPhoneIp();
        }
        return ip;
    }

    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "127.0.0.1";
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

    public static boolean isOnTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        try {
            Date startDate = sdf.parse(ShopStatusData.INSTANCE.startTime);
            Date endDate = sdf.parse(ShopStatusData.INSTANCE.endTime);
            Calendar calendar = Calendar.getInstance();
            String nowTime = CalendarUtil.getStandardTime(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY));
            Date nowDate = sdf.parse(nowTime);

            long start = startDate.getTime();
            long end = endDate.getTime();
            long now = nowDate.getTime();

            if (end < start) {
                if (now >= end && now <= start) {
                    return false;
                } else {
                    return true;
                }
            }else {
                if (now >= start && now <= end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否在夜间模式时间范围内
     * @return
     */
    public static boolean isOnNightTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        try {
            Date startDate = sdf.parse(ShopStatusData.INSTANCE.nightStart);
            Date endDate = sdf.parse(ShopStatusData.INSTANCE.nightEnd);
            Calendar calendar = Calendar.getInstance();
            String nowTime = CalendarUtil.getStandardTime(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY));
            Date nowDate = sdf.parse(nowTime);

            long start = startDate.getTime();
            long end = endDate.getTime();
            long now = nowDate.getTime();

            if (end < start) {
                if (now >= end && now <= start) {
                    return false;
                } else {
                    return true;
                }
            }else {
                if (now >= start && now <= end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
