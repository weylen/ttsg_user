package com.guaigou.cd.minutestohome.activity.market;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;

import java.util.List;

/**
 * Created by weylen on 2016-07-22.
 */
public interface MarketView extends BaseView<MarketPresenter> {

    boolean isActive();
    void onDataEmpty();
    void onStartLoading();
    void onLoadFailure();
    void onLoadSuccess();
    void onLoadLargeTypeData(List<MarketDataEntity> dataEntityList);
    void onLoadSmallTypeData(String parentId, List<MarketDataEntity> dataEntityList);

    /**
     * 加载商品数据
     * @param dataEntityList 商品列表
     * @param isLoadComplete 是否加载完成
     * @param isLoadmore 是否为加载更多
     */
    void onLoadProductData(List<ProductEntity> dataEntityList, boolean isLoadComplete, boolean isLoadmore);
    void onLoadMoreFailure();
    void onLoadMoreComplete();
}
