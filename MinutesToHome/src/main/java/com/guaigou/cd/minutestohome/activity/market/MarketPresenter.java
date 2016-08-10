package com.guaigou.cd.minutestohome.activity.market;

import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.cache.Data;
import com.guaigou.cd.minutestohome.cache.DataCache;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-22.
 */
public class MarketPresenter implements BasePresenter{

    private MarketView marketView;
    private RegionEntity entity;
    private boolean isLoading;
    public MarketPresenter(MarketView marketView, RegionEntity entity){
        this.marketView = Preconditions.checkNotNull(marketView, "MarketView can't be null");
        this.entity = Preconditions.checkNotNull(entity, "RegionEntity can't be null");
    }

    @Override
    public void start() {
        marketView.onStartLoading();

        JsonObject allKindData = MarketData.INSTANCE.getKindData();
        if (allKindData == null){
            getRemoteAllKindData();
        }else{
            // 数据对应的地区信息相同
            if (entity.getName().equalsIgnoreCase(MarketData.INSTANCE.name)
                    && entity.getId().equalsIgnoreCase(MarketData.INSTANCE.id)){
                // 获取缓存的地区数据
                JsonObject regionKindData = MarketData.INSTANCE.getRegionKindData();
                if (regionKindData != null){
                    // 处理数据
                    doParseData();
                }else{
                    getRemoteRegionKindData(entity);
                }
            }else{
                getRemoteRegionKindData(entity);
            }

        }
    }

    /**
     * 获取所有品种数据
     */
    private void getRemoteAllKindData(){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getAllKind()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(e.getMessage());
                        marketView.onLoadFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("LoginPresenter onNext s:" + s);
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            // 缓存数据
                            MarketData.INSTANCE.setKindData(s);
                            getRemoteRegionKindData(entity);
                        }else {
                            marketView.onLoadFailure();
                        }
                    }
                });
    }

    /**
     * 获取选择地区品种数据
     */
    private void getRemoteRegionKindData(RegionEntity entity){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getRegionKind(entity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(e.getMessage());
                        marketView.onLoadFailure();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("LoginPresenter onNext s:" + s);
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            marketView.onLoadSuccess();
                            // 缓存数据
                            MarketData.INSTANCE.setRegionKindData(s);
                            MarketData.INSTANCE.id = entity.getId();
                            MarketData.INSTANCE.name = entity.getName();
                            // 处理数据
                            doParseData();
                        }else {
                            marketView.onLoadFailure();
                        }
                    }
                });
    }

    /**
     * @param typeId 类别id 传大类id获取整个大类的数据 传小类id获取小类数据 不传获取全部数据
     */
    public void getProductsList(String typeId){
        // 记录当前数据的父类id
        MarketData.INSTANCE.currentProductId = typeId;
        // 先取缓存的数据
        Data<?> data = DataCache.INSTANCE.getData(typeId);
        if (data == null){ // 判断缓存的数据对象
            marketView.onStartLoadProductList();
            getRemoteProductData(true, typeId, 1);
        }else {
            // 获取缓存的列表数据
            List<ProductEntity> listData = (List<ProductEntity>) data.getListData();
            // 列表数据不存在
            if (listData == null || listData.size() == 0){
                marketView.onStartLoading();
                getRemoteProductData(true, typeId, 1);
            }else {
                boolean isLoadComplete = data.isLoadComplete();
                marketView.onLoadProductData(listData, isLoadComplete, false);
            }
        }
    }

    /**
     * 获取网络数据
     * @param typeId
     */
    private void getRemoteProductData(boolean isRefresh, String typeId, int pageNum){
        DebugUtil.d("getRemoteProductData：" + typeId +", pageNum -->" + pageNum);
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getRegionProducts(entity.getId(), typeId, pageNum, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        DebugUtil.d("onError--"+e.getMessage());
                        doError(pageNum, typeId);
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        isLoading = false;
                        DebugUtil.d("getRemoteProductData onNext s:" + s);
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            parseProductData(isRefresh, typeId, s);
                        }else {
                            doError(pageNum, typeId);
                        }
                    }
                });
    }

    /**
     * 处理请求错误
     * @param pageNum
     */
    private void doError(int pageNum, String typeId){
        if (pageNum > 1){
            marketView.onLoadMoreFailure();
        }else {
            removeTypeIdData(typeId);
            marketView.onLoadProductData(null, false, false);
        }
    }

    private void removeTypeIdData(String id){
        MarketData.INSTANCE.currentProductId = id;
        DataCache.INSTANCE.remove(id);
    }

    /**
     * 解析商品数据
     */
    private void parseProductData(boolean isRefresh, String id, JsonObject s){
        Observable.create((Observable.OnSubscribe<List<ProductEntity>>) subscriber -> {
            JsonObject root = s.get("data").getAsJsonObject();
            JsonArray dataArray = root.get("data").getAsJsonArray();
            Gson gson = new Gson();
            // 解析列表数据
            List<ProductEntity> listData = gson.fromJson(dataArray, new TypeToken<List<ProductEntity>>(){}.getType());
            // 解析图片数据数组
            JsonObject imgObject = root.get("img").getAsJsonObject();
            int size = listData == null ? 0 : listData.size();
            for (int i = 0; i < size; i++){
                ProductEntity entity = listData.get(i);
                entity.setNumber(CartData.INSTANCE.getNumber(entity.getId()));
//            String imgs = entity.getImg();
                entity.setImg(imgObject.get(entity.getImg().split(",")[0]).getAsString());
            }

            int max = s.get("maxPage").getAsInt();
            int current = s.get("pageNum").getAsInt();
            // 缓存数据
            cacheProductData(isRefresh, id, max, current, listData);
            subscriber.onNext(listData);
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ProductEntity>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        marketView.onLoadFailure();
                    }

                    @Override
                    public void onNext(List<ProductEntity> entities) {
                        int max = s.get("maxPage").getAsInt();
                        int current = s.get("pageNum").getAsInt();
                        marketView.onLoadProductData(entities, max == current, current > 1);
                    }
                });
    }

    /**
     * 缓存商品数据
     */
    private void cacheProductData(boolean isRefresh, String id, int maxNum, int pageNum, List<ProductEntity> listData){
        MarketData.INSTANCE.currentProductId = id;
        Data<?> d = DataCache.INSTANCE.getData(id);
        List<ProductEntity> saveListData;
        if (d != null){
            saveListData = (List<ProductEntity>) d.getListData();
            if (saveListData == null){
                saveListData = new ArrayList<>();
            }else if (isRefresh){
                saveListData.clear();
            }
            saveListData.addAll(listData);
        }else {
            saveListData = new ArrayList<>();
            saveListData.addAll(listData);
        }

        Data<ProductEntity> data = new Data<>();
        data.setTag(id);
        data.setLoadComplete(maxNum == pageNum);
        data.setPageNum(pageNum);
        data.setListData(saveListData);
        DataCache.INSTANCE.putData(id, data);
    }

    /**
     * 刷新
     */
    void refresh(){
        if (TextUtils.isEmpty(MarketData.INSTANCE.currentProductId)){
            DebugUtil.d("MarketPresenter refresh 获取大类");
            start();
        }else {
            DebugUtil.d("MarketPresenter refresh 获取当前小类产品");
            getRemoteProductData(true, MarketData.INSTANCE.currentProductId, 1);
        }
    }

    /**
     * 加载更多
     */
    void onLoadmore(){
        if (isLoading){return;}
        DebugUtil.d("MarketPresenter onLoadmore 加载更多：");
        Data<?> data = DataCache.INSTANCE.getData(MarketData.INSTANCE.currentProductId);
        if (data == null){ // 判断缓存的数据对象
            getRemoteProductData(true, MarketData.INSTANCE.currentProductId, 1);
        }else {
            // 获取缓存的列表数据
            List<ProductEntity> listData = (List<ProductEntity>) data.getListData();
            // 列表数据不存在
            if (listData == null || listData.size() == 0){
                getRemoteProductData(true, MarketData.INSTANCE.currentProductId, 1);
            }else {
                boolean isLoadcomplete = data.isLoadComplete();
                if (isLoadcomplete){
                    // 已经加载完成所有的数据
                    marketView.onLoadMoreComplete();
                }else {
                    int pageNum = data.getPageNum() + 1;
                    getRemoteProductData(false, MarketData.INSTANCE.currentProductId, pageNum);
                }
            }
        }
    }

    /**
     * 解析大类数据
     */
    private void doParseData(){
        Observable.create(new Observable.OnSubscribe<List<MarketDataEntity>>() {
            @Override
            public void call(Subscriber<? super List<MarketDataEntity>> subscriber) {
                JsonObject allKindData = MarketData.INSTANCE.getKindData();
                JsonObject regionKindData = MarketData.INSTANCE.getRegionKindData();
                // 获取所有数据中的data字段
                JsonObject allKindDataObject = ResponseMgr.getData(allKindData);
                // 获取最大父类数据
                JsonArray largeTypeArray = allKindDataObject.get("0").getAsJsonArray();
                Gson gson = new Gson();
                // 解析所有大类的数据
                List<MarketDataEntity> largeTypeArrayData = gson.fromJson(largeTypeArray,
                        new TypeToken<List<MarketDataEntity>>(){}.getType());
                // 获取当前区域类需要显示的数据集合 包括大类和小类的数据
                JsonArray regionKindDataArray = regionKindData.get("data").getAsJsonArray();
                // 解析需要显示的大类的数据
                List<MarketDataEntity> largeTypeData = new ArrayList<>();
                int largeTypeLength = largeTypeArrayData.size();
                int regionKindDataLength = regionKindDataArray.size();
                // 循环所有的大类数据
                for (int i = 0; i < largeTypeLength; i++){
                    MarketDataEntity entity = largeTypeArrayData.get(i);
                    // 循环所有的区域id
                    for (int k = 0; k < regionKindDataLength; k++){
                        // 判断2个id是否相等
                        if (entity.getId().equalsIgnoreCase(regionKindDataArray.get(k).getAsString())){
                            entity.setNumber(CartData.INSTANCE.getKindNumber(entity.getId()));
                            // 相等则添加数据
                            largeTypeData.add(entity);
                            break;
                        }
                    }
                }
                subscriber.onNext(largeTypeArrayData);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<MarketDataEntity>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        marketView.onLoadFailure();
                    }

                    @Override
                    public void onNext(List<MarketDataEntity> dataEntities) {
                        marketView.onLoadSuccess();
                        marketView.onLoadLargeTypeData(dataEntities);
                    }
                });
    }

    /**
     * 解析小类数据
     * @param parentId
     */
    public void parseSmallTypeData(String parentId){
        JsonObject allKindData = MarketData.INSTANCE.getKindData();
        JsonObject regionKindData = MarketData.INSTANCE.getRegionKindData();
        // 获取所有数据中的data字段
        JsonObject allKindDataObject = ResponseMgr.getData(allKindData);
        // 获取指定父类的所有数据
        JsonArray largeTypeArray = allKindDataObject.get(parentId).getAsJsonArray();
        Gson gson = new Gson();
        // 解析所有大类的数据
        List<MarketDataEntity> largeTypeArrayData = gson.fromJson(largeTypeArray,
                new TypeToken<List<MarketDataEntity>>(){}.getType());

        // 获取当前区域类需要显示的数据集合 包括大类和小类的数据
        JsonArray regionKindDataArray = regionKindData.get("data").getAsJsonArray();
        // 解析需要显示的大类的数据
        List<MarketDataEntity> smallTypeData = new ArrayList<>();

        int largeTypeLength = largeTypeArrayData.size();
        int regionKindDataLength = regionKindDataArray.size();
        // 循环所有的大类数据
        for (int i = 0; i < largeTypeLength; i++){
            MarketDataEntity entity = largeTypeArrayData.get(i);
            // 循环所有的区域id
            for (int k = 0; k < regionKindDataLength; k++){
                // 判断2个id是否相等
                if (entity.getId().equalsIgnoreCase(regionKindDataArray.get(k).getAsString())){
                    // 相等则添加数据
                    smallTypeData.add(entity);
                    break;
                }
            }
        }

        addSmallTypeAll(parentId, smallTypeData);
        // 回调数据
        marketView.onLoadSmallTypeData(parentId, smallTypeData);
    }

    /**
     * 添加小数据的所有品种
     * @param parentId
     * @param smallTypeData
     */
    private void addSmallTypeAll(String parentId, List<MarketDataEntity> smallTypeData){
        MarketDataEntity entity = new MarketDataEntity(parentId, parentId, "全部");
        smallTypeData.add(0, entity);
    }
}
