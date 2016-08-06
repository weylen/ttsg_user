package com.guaigou.cd.minutestohome.activity.shoppingcart;

import com.guaigou.cd.minutestohome.entity.CartEntity;

import java.util.List;

/**
 * Created by weylen on 2016-08-06.
 */
public interface CartView {

    void onStartLoading();
    void onLoadFailure(String message);
    void onLoadSuccess(List<CartEntity> cartEntities);
    void onStartRefresh();
    void onStartEdit();
    void onEditFailure();
    void onEditSuccess();
    boolean isActive();
}
