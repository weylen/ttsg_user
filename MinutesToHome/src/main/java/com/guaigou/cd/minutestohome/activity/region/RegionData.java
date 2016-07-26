package com.guaigou.cd.minutestohome.activity.region;

import com.google.gson.JsonObject;

/**
 * Created by weylen on 2016-07-22.
 */
public enum RegionData {

    INSTANCE;

    private JsonObject regionData;

    public JsonObject getRegionData() {
        return regionData;
    }

    public void setRegionData(JsonObject regionData) {
        this.regionData = regionData;
    }
}
