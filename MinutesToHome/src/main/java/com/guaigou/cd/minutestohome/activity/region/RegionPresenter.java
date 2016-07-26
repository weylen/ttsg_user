package com.guaigou.cd.minutestohome.activity.region;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-22.
 */
public class RegionPresenter implements BasePresenter{

    private RegionView regionView;
    public RegionPresenter(RegionView regionView){
        this.regionView = Preconditions.checkNotNull(regionView, "RegionView can not be null");
    }

    @Override
    public void start() {
        // 获取缓存数据
        JsonObject regionData = RegionData.INSTANCE.getRegionData();
        if (regionData == null){ // 没有缓存数据 联网获取
            regionView.onStartLoading();
            getRemoteRegionData();
        }else{
            setProvinceRegionData(regionData);
        }
    }

    /**
     * 获取地区数据
     */
    private void getRemoteRegionData(){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getRegion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(e.getMessage());
                        regionView.onLoadFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("LoginPresenter onNext s:" + s);
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            regionView.onLoadSuccess();
                            // 缓存数据
                            RegionData.INSTANCE.setRegionData(s);
                            setProvinceRegionData(s);
                        }else {
                            regionView.onLoadFailure();
                        }
                    }
                });
    }

    /**
     * 设置省份数据
     */
    public void setProvinceRegionData(JsonObject regionData){
        JsonObject provinceData = ResponseMgr.getData(regionData);
        JsonArray array = provinceData.get("0").getAsJsonArray();
        Gson gson = new Gson();
        List<RegionEntity> data = gson.fromJson(array, new TypeToken<List<RegionEntity>>(){}.getType());
        regionView.onSetProvinceData(data);
    }

    /**
     * 设置下一级视图
     * @param entity
     */
    public void setNextLevelData(RegionEntity entity){
        String id = entity.getId();
        JsonObject jsonObject = ResponseMgr.getData(RegionData.INSTANCE.getRegionData());
        JsonElement element = jsonObject.get(id);
        if (element == null){ // 没有下一级
            regionView.onNextNone(entity);
        }else{
            JsonArray array = element.getAsJsonArray();
            Gson gson = new Gson();
            List<RegionEntity> data = gson.fromJson(array, new TypeToken<List<RegionEntity>>(){}.getType());
            regionView.onSetNextLevelData(data);
        }
    }

    public void onBackClick(String pid){
        JsonObject jsonObject = ResponseMgr.getData(RegionData.INSTANCE.getRegionData());
        JsonArray array = jsonObject.get(pid).getAsJsonArray();
        Gson gson = new Gson();
        List<RegionEntity> data = gson.fromJson(array, new TypeToken<List<RegionEntity>>(){}.getType());
        regionView.onSetBackLevelData(data);
    }
}
