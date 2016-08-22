package com.guaigou.cd.minutestohome.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.MainActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.market.MarketProductAdapter;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.KeyboardUtil;
import com.guaigou.cd.minutestohome.view.ZListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.Subscriber;

/**
 * Created by Administrator on 2016-06-18.
 */
public class SearchActivity extends BaseActivity implements SearchView{

    @Bind(R.id.Generic_List) ZListView zListView;
    @Bind(R.id.titleLayout) View containerView;
    @Bind(R.id.edit_query) EditText editText;
    @Bind(R.id.text_empty) TextView emptyView;
    @Bind(R.id.button_intocart) TextView productNumView;

    private MarketProductAdapter adapter;
    private SearchPresenter searchPresenter;
    private int cartProductNumber = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        adapter = new MarketProductAdapter(this, null);
        zListView.setEmptyView(emptyView);
        zListView.setAdapter(adapter);
        zListView.setFooterVisible(false);
        emptyView.setVisibility(View.GONE);

        RegionEntity entity = RegionPrefs.getRegionData(getApplicationContext());
        Preconditions.checkNotNull(entity);
        searchPresenter = new SearchPresenter(this, entity.getId());

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                onSearchClick();
                return true;
            }
            return false;
        });

        cartProductNumber = CartData.INSTANCE.getNumberAll();
        productNumView.setText("共" + cartProductNumber + "件商品");
    }

    @OnItemClick(R.id.Generic_List)
    public void onItemClick(int position){
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("Entity", adapter.getData().get(position));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        onCancelClick();
    }

    @OnClick(R.id.text_cancel)
    public void onCancelClick(){
        KeyboardUtil.hide(this, editText);
        finish();

    }
    @OnClick(R.id.img_search)
    public void onSearchClick(){
        String keyword = editText.getText().toString();
        if (TextUtils.isEmpty(keyword)){
            showSnakeView(containerView, "输入搜索条件");
            return;
        }
        KeyboardUtil.hide(this, editText);
        searchPresenter.onSearch(keyword);
    }


    @Override
    public void onStartSearch() {
        showProgressDialog("搜索中...");
    }

    @OnClick(R.id.button_intocart)
    public void onIntoCartClick(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("ChooseCart", true);
        startActivity(intent);
    }

    @Override
    public void onSearchFailure() {
        dismissProgressDialog();
        adapter.setData(null);
    }

    @Override
    public void onSearchSuccess(List<ProductEntity> data, boolean isComplete) {
        dismissProgressDialog();
        zListView.setLoadComplete(isComplete);
        adapter.setData(data);
    }

    @Override
    public void onLoadmoreSuccess(List<ProductEntity> data, boolean isComplete) {
        zListView.setLoadComplete(isComplete);
        adapter.addData(data);
    }

    @Override
    public void onLoadmoreFailure() {
        showSnakeView(containerView, "加载更多失败，请重新操作");
    }

    @Override
    public void setPresenter(SearchPresenter presenter) {

    }

    @Override
    public void onResume() {
        super.onResume();
        // 注册观察者
        CartData.INSTANCE.registerObserver(subscriber);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 解除注册观察者
        CartData.INSTANCE.unregisterObserver(subscriber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private Subscriber<Integer> subscriber = new Subscriber<Integer>() {
        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable e) {}

        @Override
        public void onNext(Integer count) {
            productNumView.setText("共" + count + "件商品");
        }
    };
}
