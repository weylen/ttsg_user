package com.guaigou.cd.minutestohome.activity.market;

import com.guaigou.cd.minutestohome.http.Constants;

import java.util.HashMap;

/**
 * Created by weylen on 2016-09-12.
 */
public enum ShopStatusData {
    INSTANCE;

    String areaId = Constants.EMPTY_STRING;
    public int status = 3;
    public String startTime = Constants.EMPTY_STRING;
    public String endTime = Constants.EMPTY_STRING;
    public String phone = null;
    public String fare = "0";
    public String fareLimit = "0";

    public String nightStart = Constants.EMPTY_STRING;
    public String nightEnd = Constants.EMPTY_STRING;


    private HashMap<Integer, String> statusStrs;

    ShopStatusData(){
        statusStrs = new HashMap<>();
        statusStrs.put(1, "正在营业");
        statusStrs.put(2, "停业中");
        statusStrs.put(3, "休息中");
    }

    public String getStatus(){
        return statusStrs.get(status);
    }

    public void reset(){
        areaId = Constants.EMPTY_STRING;
        status = 3;
        startTime = Constants.EMPTY_STRING;
        endTime = Constants.EMPTY_STRING;
    }
}
