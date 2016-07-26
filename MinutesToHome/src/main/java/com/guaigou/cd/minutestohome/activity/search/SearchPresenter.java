package com.guaigou.cd.minutestohome.activity.search;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhou on 2016/7/26.
 */
public class SearchPresenter implements BasePresenter {

    private SearchView searchView;
    private String regionId;

    /**
     * SearchPresenter 构造器
     * @param searchView SearchView对象
     * @param regionId 区域id
     */
    public SearchPresenter(SearchView searchView, String regionId){
        this.searchView = Preconditions.checkNotNull(searchView);
        this.regionId = Preconditions.checkNotNull(regionId);
    }

    @Override
    public void start() {

    }

    public void onSearch(String keyword){
        searchView.onStartSearch();
        SearchData.INSTANCE.keyword = keyword; // 保存搜索关键字
        SearchData.INSTANCE.pageNum = 1;
        requestRemoteData(keyword, 1);
    }

    /**
     * 加载更多
     */
    public void onLoadmore(){
        requestRemoteData(SearchData.INSTANCE.keyword, SearchData.INSTANCE.pageNum);
    }

    /**
     * 请求远程数据
     * @param keyword
     * @param pageNum
     */
    private void requestRemoteData(String keyword, int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .getRegionProducts(regionId, keyword, pageNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d(e.getMessage());
                        doRequestError(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SearchPresenter-onNext s:" + s);
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            parseProductData(pageNum, s);
                        }else {
                            doRequestError(pageNum);
                        }
                    }
                });
    }

    /**
     * 处理请求错误
     * @param pageNum
     */
    private void doRequestError(int pageNum){
        if (pageNum > 1){
            searchView.onLoadmoreFailure();
            SearchData.INSTANCE.pageNum--;
        }else {
            searchView.onSearchFailure();
        }
    }

    /**
     * 解析商品数据
     */
    private void parseProductData(int pageNum, JsonObject s){
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
            entity.setImg(imgObject.get(entity.getId()).getAsString());
        }
        if (pageNum > 1){ // 加载更多
            searchView.onLoadmoreSuccess(listData, listData.size() < Constants.DEFAULT_PAGE_SIZE);
        }else {
            searchView.onSearchSuccess(listData, listData.size() < Constants.DEFAULT_PAGE_SIZE);
        }
    }
}
