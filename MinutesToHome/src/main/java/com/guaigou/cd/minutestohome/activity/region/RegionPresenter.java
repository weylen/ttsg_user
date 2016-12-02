package com.guaigou.cd.minutestohome.activity.region;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
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
            setFirstRegionData(regionData);
        }
    }

    /**
     * 获取地区数据
     */
    private void getRemoteRegionData(){
        Client.request()
                .getRegion()
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        regionView.onLoadFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            regionView.onLoadSuccess();
                            // 缓存数据
                            RegionData.INSTANCE.setRegionData(s);
                            setFirstRegionData(s);
                        }else {
                            regionView.onLoadFailure();
                        }
                    }
                });
    }

    /**
     * 设置第一级数据
     * classify字段 表示分类数据
     * chaos字段 表示所有的数据
     */
    public void setFirstRegionData(JsonObject regionData){
        JsonObject firstData = ResponseMgr.getData(regionData);
        JsonArray array = firstData.get("classify").getAsJsonObject().get("0").getAsJsonArray();
        Gson gson = new Gson();
        List<RegionEntity> data = gson.fromJson(array, new TypeToken<List<RegionEntity>>(){}.getType());
        regionView.onSetProvinceData(data);
    }

    /**
     * 设置下一级视图
     * classify字段 表示分类数据
     * chaos字段 表示所有的数据
     * @param entity 父级id
     */
    public void setNextLevelData(RegionEntity entity){
        String id = entity.getId();
        JsonObject jsonObject = ResponseMgr.getData(RegionData.INSTANCE.getRegionData());
        JsonElement element = jsonObject.get("classify").getAsJsonObject().get(id);
        if (element == null){ // 没有下一级
            regionView.onNextNone(entity);
        }else{
            JsonArray array = element.getAsJsonArray();
            Gson gson = new Gson();
            List<RegionEntity> data = gson.fromJson(array, new TypeToken<List<RegionEntity>>(){}.getType());
            regionView.onSetNextLevelData(data);
        }
    }

    /**
     * 返回上一级数据
     * classify字段 表示分类数据
     * chaos字段 表示所有的数据
     * @param pid
     */
    public void onBackClick(String pid){
        JsonObject jsonObject = ResponseMgr.getData(RegionData.INSTANCE.getRegionData());
        String id = jsonObject.get("chaos").getAsJsonObject().get(pid).getAsJsonObject().get("pid").getAsString();
        JsonArray array = jsonObject.get("classify").getAsJsonObject().get(id).getAsJsonArray();
        DebugUtil.d("RegionPresenter-onBackClick array:" + array);
        Gson gson = new Gson();
        List<RegionEntity> data = gson.fromJson(array, new TypeToken<List<RegionEntity>>(){}.getType());
        regionView.onSetBackLevelData(data);
    }
}
