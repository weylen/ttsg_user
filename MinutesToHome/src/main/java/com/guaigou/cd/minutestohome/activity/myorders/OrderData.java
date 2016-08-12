package com.guaigou.cd.minutestohome.activity.myorders;

/**
 * Created by weylen on 2016-07-23.
 */
public enum OrderData {

    INSTANCE;

    boolean isLoadComplete; // 页面数据是否加载完成

    int pageNum = 1;

    void reset(){
        isLoadComplete = false;
        pageNum = 1;
    }


}
