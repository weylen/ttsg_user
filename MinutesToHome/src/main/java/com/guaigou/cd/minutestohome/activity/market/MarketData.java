package com.guaigou.cd.minutestohome.activity.market;

import com.google.gson.JsonObject;

/**
 * Created by weylen on 2016-07-22.
 */
public enum MarketData {

    INSTANCE;

    private JsonObject kindData;

    public JsonObject getKindData() {
        return kindData;
    }

    public void setKindData(JsonObject kindData) {
        this.kindData = kindData;
    }

    private JsonObject regionKindData;

    public JsonObject getRegionKindData() {
        return regionKindData;
    }

    public void setRegionKindData(JsonObject regionKindData) {
        this.regionKindData = regionKindData;
    }

    public String id; // 当前数据的地区id
    public String name; // 当前数据的地区名字

    public String currentProductId; // 当前的商品分类id

    public int largeTypeIndex = 0;
    public int smallTypeIndex = 0;
    public int listSelectIndex = 0;
}
