package com.guaigou.cd.minutestohome.activity.market;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.activity.region.RegionActivity;
import com.guaigou.cd.minutestohome.activity.search.SearchActivity;
import com.guaigou.cd.minutestohome.adapter.LargeTypeAdapter;
import com.guaigou.cd.minutestohome.adapter.SmallTypeAdapter;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.AnimatorUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.view.EmptyViewHelper;
import com.guaigou.cd.minutestohome.view.ZListView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class MarketFragment extends BaseFragment implements MarketView, MarketProductAdapter.OnNumberChangedListener{

    public static final String TAG = MarketFragment.class.getName();

    @Bind(R.id.text_current_location) TextView locationView;
    @Bind(R.id.largeTypeList) ListView largeTypeList;
    @Bind(R.id.horizontal_recyclerView) RecyclerView horizontalRecyclerView;
    @Bind(R.id.Generic_List) ZListView zListView;
    @Bind(R.id.refreshLayout) ZRefreshingView zRefreshingView; // 刷新组件
    @Bind(R.id.parentFrameLayout) FrameLayout parentLayout;
    @Bind(R.id.text_hint) TextView hintView;

    private EmptyViewHelper emptyViewHelper; // 空视图辅助类

    private MarketPresenter marketPresenter;

    private MarketProductAdapter adapter; // 商品列表适配器
    private LargeTypeAdapter largeTypeAdapter;
    private SmallTypeAdapter smallTypeAdapter;

    private String lastLargeTypeId;
    private String lastSmallTypeId;
    private boolean isShowing;

    @Override
    public int layoutId() {
        return R.layout.fragment_market;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        RegionEntity regionEntity = RegionPrefs.getRegionData(getActivity());
        locationView.setText(regionEntity.getName());

        largeTypeAdapter = new LargeTypeAdapter(getActivity(), null);
        largeTypeList.setAdapter(largeTypeAdapter);
        largeTypeList.setOnItemClickListener((parent, view1, position, id) -> {
            MarketData.INSTANCE.largeTypeIndex = position;
            MarketData.INSTANCE.smallTypeIndex = 0;
            MarketData.INSTANCE.listSelectIndex = 0;
            MarketDataEntity entity = largeTypeAdapter.getData().get(position);
            if (lastLargeTypeId == null || ! entity.getId().equalsIgnoreCase(lastLargeTypeId)){
                lastLargeTypeId = entity.getId();
                marketPresenter.parseSmallTypeData(lastLargeTypeId);
                largeTypeAdapter.setCheckedPosition(position);
            }
        });

        smallTypeAdapter = new SmallTypeAdapter(getActivity(), null);
        smallTypeAdapter.setOnItemClickListener((position, entity) -> {
            MarketData.INSTANCE.smallTypeIndex = position;
            MarketData.INSTANCE.listSelectIndex = 0;
            if (lastSmallTypeId == null || !entity.getId().equalsIgnoreCase(lastSmallTypeId)){
                marketPresenter.getProductsList(lastLargeTypeId, entity.getId());
                smallTypeAdapter.setCheckedPosition(position);
            }
            lastSmallTypeId = entity.getId();
        });
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        horizontalRecyclerView.setAdapter(smallTypeAdapter);

        // 设置刷新事件
        zRefreshingView.setOnRefreshListener(() -> refresh());
        // 设置列表的加载更多事件
        zListView.setOnLoadmoreListener(new ZListView.OnLoadmoreListener() {
            @Override
            public void onLoadMore() {
                ZListView.State state = zListView.getState();
                if (state == ZListView.State.STATE_COMPLETE || state == ZListView.State.STATE_LOADING){
                    return;
                }
                zListView.setState(ZListView.State.STATE_LOADING);
                marketPresenter.onLoadmore();
            }

            @Override
            public void onError() {
                zListView.setState(ZListView.State.STATE_LOADING);
                marketPresenter.onLoadmore();
            }
        });
        // 创建商品列表适配器
        adapter = new MarketProductAdapter(getActivity(), null);
        adapter.setOnNumberChangedListener(this);
        // 设置适配器
        zListView.setAdapter(adapter);
        // 添加空视图
        emptyViewHelper = new EmptyViewHelper(zListView, "商品不翼而飞，轻触刷新", parentLayout);
        emptyViewHelper.setOnEmptyTouchListener(() -> {
                zRefreshingView.setRefreshing(true);
                refresh();
        });
        emptyViewHelper.setRefreshListener(() -> refresh());

        marketPresenter = new MarketPresenter(this, regionEntity);
        setPresenter(marketPresenter);

        setupScrollViewWidth();
        largeTypeList.setVisibility(View.GONE);

        // 检查是否保存了上一次的区域
        RegionEntity entity = RegionPrefs.getRegionData(getActivity());
        // 没有保存数据
        if (TextUtils.isEmpty(entity.getName()) || TextUtils.isEmpty(entity.getId())){
            locationView.setText("选择地址");
            showChooseRegionDialog();
        }else{
            // request data
            marketPresenter.start();
        }
    }

    @OnItemClick(R.id.Generic_List)
    public void onItemClick(int position){
        if (position < adapter.getData().size()){
            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
            intent.putExtra("Entity", adapter.getData().get(position));
            intent.putExtra("LargeTypeId", lastLargeTypeId);
            intent.putExtra("Position", position);
            startActivityForResult(intent, DETAILS_CODE);
        }
    }

    /**
     * 刷新
     */
    public void refresh(){
        marketPresenter.refresh();
    }

    // 动态设置左边列表的宽度
    private void setupScrollViewWidth(){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float ratio = 0.25f; // 0.618
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (displayMetrics.widthPixels * ratio), -1);
        largeTypeList.setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MarketData.INSTANCE.isChanged){
            marketPresenter.start();
            marketPresenter.refresh();
        }
        MarketData.INSTANCE.isChanged = false;
        isShowing = true;
        // request shop status
        marketPresenter.shopStatus();
    }

    @Override
    public void onStop() {
        super.onStop();
        isShowing = false;
        MarketData.INSTANCE.listSelectIndex = zListView.getSelectedItemPosition();
    }

    // 显示选择区域对话框 如果不选择 则退出程序
    private void showChooseRegionDialog(){
        onLocationClick();
    }

    /**
     * 切换地区
     */
    @OnClick(R.id.text_current_location)
    void onLocationClick(){
        Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getActivity(), RegionActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }

    /**
     * 搜索按钮点击
     */
    @OnClick(R.id.img_search)
    void onSearchClick(){
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDataEmpty() {

    }

    @Override
    public void onStartLoading() {
        showProgressDialog("加载中...");
    }

    @Override
    public void onLoadFailure() {
        dismissProgressDialog();
        zRefreshingView.setRefreshing(false);
        emptyViewHelper.setRefreshing(false);
    }

    @Override
    public void onLoadSuccess() {
        dismissProgressDialog();
        zRefreshingView.setRefreshing(false);
        emptyViewHelper.setRefreshing(false);
    }

    @Override
    public void onLoadLargeTypeData(List<MarketDataEntity> dataEntityList) {
        if (isActive()){
            dismissProgressDialog();
            AnimatorUtil.scaleShow(largeTypeList, null);
            // 设置大类的宽度
            largeTypeAdapter.setData(dataEntityList);
            if (dataEntityList.size() == 0){
                onDataEmpty();
                return;
            }
            final int index = MarketData.INSTANCE.largeTypeIndex;
            largeTypeList.setSelection(index);
            largeTypeAdapter.setCheckedPosition(index);
            lastLargeTypeId = dataEntityList.get(index).getId();

            marketPresenter.parseSmallTypeData(lastLargeTypeId);
        }
    }

    @Override
    public void onLoadSmallTypeData(String parentId, List<MarketDataEntity> dataEntityList) {
        if (isActive()){
            dismissProgressDialog();
            smallTypeAdapter.setDataEntities(dataEntityList);
            final int index = MarketData.INSTANCE.smallTypeIndex;
            smallTypeAdapter.setCheckedPosition(index);
            lastSmallTypeId = dataEntityList.get(index).getId();
            zListView.setState(ZListView.State.STATE_NORMAL);
            marketPresenter.getProductsList(parentId, lastSmallTypeId);
        }
    }

    @Override
    public void onLoadProductData(List<ProductEntity> dataEntityList, boolean isLoadComplete, boolean isLoadmore) {
        dismissProgressDialog();
        if (isActive()){
            if (!isLoadmore){
                adapter.setData(dataEntityList);
                zListView.setSelection(MarketData.INSTANCE.listSelectIndex);
            }else {
                adapter.addData(dataEntityList);
            }
            zRefreshingView.setRefreshing(false);
            emptyViewHelper.setRefreshing(false);
            zListView.setLoadComplete(isLoadComplete);
        }
    }

    @Override
    public void onLoadMoreFailure() {
        if (isActive()){
            dismissProgressDialog();
            zListView.setState(ZListView.State.STATE_ERROR);
        }
    }

    @Override
    public void onStartLoadProductList() {
        zRefreshingView.setRefreshing(true);
    }

    @Override
    public void onLoadMoreComplete() {

    }

    @Override
    public void onRequestShopStatus(boolean isSuccess, int status, String startTime, String endTime) {
        if (isSuccess){
            if (status != 1){
                hintView.setText(ShopStatusData.INSTANCE.getStatus());
            }else {
                hintView.setText("营业时间：" + startTime +" ~ " + endTime);
            }
        }else {
            hintView.setText("获取营业时间失败");
            if (isShowing){
                showErrorStatusDialog();
            }
        }
    }

    // 显示获取商家营业时间失败对话框
    private void showErrorStatusDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("获取商家营业时间失败，将导致无法购买商品，是否重新获取？")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("重新获取", (dialog, which) -> {
                    // request shop status
                    marketPresenter.shopStatus();
                })
                .show();
    }

    @Override
    public void setPresenter(MarketPresenter presenter) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onNumberChanged(String parentId, int count) {
        List<MarketDataEntity> data = largeTypeAdapter.getData();
        if (!LocaleUtil.isListEmpty(data)){
            for (MarketDataEntity entity : data){
                if (parentId.equalsIgnoreCase(entity.getId())){
                    entity.setNumber(entity.getNumber() + count);
                    largeTypeAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == DETAILS_CODE){
            int position = data.getIntExtra("Position", -1);
            int count = data.getIntExtra("Count", 0);
            String largeTypeId = data.getStringExtra("LargeTypeId");
            if (position != -1 && !TextUtils.isEmpty(largeTypeId) && count > 0){
                ProductEntity entity = adapter.getData().get(position);
                entity.setNumber(entity.getNumber() + count);
                adapter.notifyDataSetChanged();
                onNumberChanged(largeTypeId, count);
            }
        }
    }

    private static final int DETAILS_CODE = 100;
}
