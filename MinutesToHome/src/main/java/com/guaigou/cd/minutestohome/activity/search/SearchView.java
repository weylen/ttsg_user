package com.guaigou.cd.minutestohome.activity.search;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.entity.ProductEntity;

import java.util.List;

/**
 * Created by zhou on 2016/7/26.
 */
public interface SearchView extends BaseView<SearchPresenter>{

    /**
     * 开始搜索
     */
    void onStartSearch();

    /**
     * 搜索失败
     */
    void onSearchFailure();

    /**
     * 搜索成功
     * @param data 数据
     * @param isComplete 是否加载完成
     */
    void onSearchSuccess(List<ProductEntity> data, boolean isComplete);

    /**
     * 加载更多成功
     * @param data
     * @param isComplete
     */
    void onLoadmoreSuccess(List<ProductEntity> data, boolean isComplete);

    /**
     * 加载更多失败
     */
    void onLoadmoreFailure();
}
