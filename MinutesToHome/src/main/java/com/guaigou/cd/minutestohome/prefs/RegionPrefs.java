package com.guaigou.cd.minutestohome.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.guaigou.cd.minutestohome.entity.RegionEntity;

/**
 * Created by weylen on 2016-07-22.
 */
public class RegionPrefs {

    private static final String REGION_PREFS_NAME = "region_prefs_db";

    public static void saveRegionData(Context context, RegionEntity entity){
        SharedPreferences prefs = context.getSharedPreferences(REGION_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", entity.getName());
        editor.putString("id", entity.getId());
        editor.putString("pid", entity.getPid());
        editor.commit();
    }

    public static RegionEntity getRegionData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(REGION_PREFS_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String id = prefs.getString("id", "");
        String pid = prefs.getString("pid", "");
        return new RegionEntity(id, name, pid);
    }
}
