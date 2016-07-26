package com.guaigou.cd.minutestohome.activity.search;

import android.content.Intent;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.market.MarketProductAdapter;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.view.ZListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class SearchActivity extends BaseActivity implements SearchView{

    public static final String TAG = SearchActivity.class.getName();

    @Bind(R.id.Generic_List) ZListView zListView;
    @Bind(R.id.Container) View containerView;
    @Bind(R.id.edit_query) EditText editText;

    private MarketProductAdapter adapter;
    private SearchPresenter searchPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        adapter = new MarketProductAdapter(this, null);
        zListView.setAdapter(adapter);
        zListView.setFooterVisible(false);

        RegionEntity entity = RegionPrefs.getRegionData(getApplicationContext());
        Preconditions.checkNotNull(entity);
        searchPresenter = new SearchPresenter(this, entity.getId());
    }

    @OnItemClick(R.id.Generic_List)
    public void onItemClick(int position){
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.text_cancel)
    public void onCanclClick(){
        finish();
    }
    @OnClick(R.id.img_search)
    public void onSearchClick(){
        searchPresenter.onSearch(editText.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStartSearch() {
        showProgressDialog("搜索中...");
    }

    @Override
    public void onSearchFailure() {
        dismissProgressDialog();
    }

    @Override
    public void onSearchSuccess(List<ProductEntity> data, boolean isComplete) {
        dismissProgressDialog();
        adapter.setData(data);
        zListView.setLoadComplete(isComplete);
    }

    @Override
    public void onLoadmoreSuccess(List<ProductEntity> data, boolean isComplete) {
        adapter.addData(data);
        zListView.setLoadComplete(isComplete);
    }

    @Override
    public void onLoadmoreFailure() {
        showSnakeView(containerView, "加载更多失败，请重新操作");
    }

    @Override
    public void setPresenter(SearchPresenter presenter) {

    }
}