package com.guaigou.cd.minutestohome.activity.region;

import com.guaigou.cd.minutestohome.BaseView;
import com.guaigou.cd.minutestohome.entity.RegionEntity;

import java.util.List;

/**
 * Created by weylen on 2016-07-22.
 */
public interface RegionView extends BaseView<RegionPresenter>{

    void onStartLoading();
    void onLoadFailure();
    void onLoadSuccess();
    void onSetProvinceData(List<RegionEntity> data);
    void onSetNextLevelData(List<RegionEntity> data);
    void onSetBackLevelData(List<RegionEntity> data);
    void onNextNone(RegionEntity entity);
}
