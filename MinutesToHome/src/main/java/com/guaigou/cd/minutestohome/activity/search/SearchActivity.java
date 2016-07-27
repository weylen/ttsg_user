package com.guaigou.cd.minutestohome.activity.search;

import android.content.Intent;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.market.MarketProductAdapter;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.KeybordUtil;
import com.guaigou.cd.minutestohome.view.ZListView;

import java.net.URLEncoder;
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
    @Bind(R.id.text_empty) TextView emptyView;

    private MarketProductAdapter adapter;
    private SearchPresenter searchPresenter;

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
    }

    @OnItemClick(R.id.Generic_List)
    public void onItemClick(int position){
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        onCanclClick();
    }

    @OnClick(R.id.text_cancel)
    public void onCanclClick(){
        KeybordUtil.hide(this);
        finish();

    }
    @OnClick(R.id.img_search)
    public void onSearchClick(){
        String keyword = editText.getText().toString();
        if (TextUtils.isEmpty(keyword)){
            showSnakeView(containerView, "输入搜索条件");
            return;
        }
        searchPresenter.onSearch(URLEncoder.encode(keyword));
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
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchSuccess(List<ProductEntity> data, boolean isComplete) {
        dismissProgressDialog();
        adapter.setData(data);
        emptyView.setVisibility(View.VISIBLE);
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
