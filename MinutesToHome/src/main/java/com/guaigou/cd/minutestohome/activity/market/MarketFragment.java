package com.guaigou.cd.minutestohome.activity.market;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.activity.search.SearchActivity;
import com.guaigou.cd.minutestohome.MainActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.entity.MarketDataEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.activity.region.RegionActivity;
import com.guaigou.cd.minutestohome.view.EmptyViewHelper;
import com.guaigou.cd.minutestohome.view.MyHorizontalRadioGroup;
import com.guaigou.cd.minutestohome.view.MyRadioGroup;
import com.guaigou.cd.minutestohome.view.ZListView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;
import com.rey.material.app.SimpleDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class MarketFragment extends BaseFragment implements MarketView{

    public static final String TAG = MarketFragment.class.getName();

    @Bind(R.id.text_current_location) TextView locationView;
    @Bind(R.id.scrollView) ScrollView scrollView;
    @Bind(R.id.generic_group) MyRadioGroup radioGroup;
    @Bind(R.id.horizontal_group) MyHorizontalRadioGroup horizontalGroup;
    @Bind(R.id.Generic_List) ZListView zListView;
    @Bind(R.id.refreshLayout) ZRefreshingView zRefreshingView; // 刷新组件
    @Bind(R.id.parentFrameLayout) FrameLayout parentLayout;

    private EmptyViewHelper emptyViewHelper; // 空视图辅助类

    private MarketPresenter marketPresenter;

    private MarketProductAdapter adapter; // 商品列表适配器

    private String lastLargeTypeId;
    private String lastSmallTypeId;

    @Override
    public int layoutId() {
        return R.layout.fragment_market;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        RegionEntity regionEntity = RegionPrefs.getRegionData(getActivity());
        locationView.setText(regionEntity.getName());

        // 大类点击切换
        radioGroup.setOnCommandChangedListener(entity ->{
            if (lastLargeTypeId == null || !entity.getId().equalsIgnoreCase(lastLargeTypeId)){
                marketPresenter.parseSmallTypeData(entity.getId());
            }
            lastLargeTypeId = entity.getId();
        });

        // 小类点击切换
        horizontalGroup.setOnCommandChangedListener(entity -> {
            if (lastSmallTypeId == null || !entity.getId().equalsIgnoreCase(lastSmallTypeId)){
                marketPresenter.getProductsList(entity.getId());
            }
            lastSmallTypeId = entity.getId();
        });

        // 设置刷新事件
        zRefreshingView.setOnRefreshListener(() -> refresh());
        // 设置列表的加载更多事件
        zListView.setOnLoadmoreListener(()->marketPresenter.onLoadmore());
        // 创建商品列表适配器
        adapter = new MarketProductAdapter(getActivity(), null);
        // 设置适配器
        zListView.setAdapter(adapter);
        // 添加空视图
        emptyViewHelper = new EmptyViewHelper(zListView, "商品不翼而飞，轻触刷新", parentLayout);
        emptyViewHelper.setOnEmptyTouchListener(() -> refresh());
        emptyViewHelper.setRefreshListener(() -> refresh());

        marketPresenter = new MarketPresenter(this, regionEntity);
        setPresenter(marketPresenter);
    }

    @OnItemClick(R.id.Generic_List)
    public void onItemClick(int position){
        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
        intent.putExtra("Entity", adapter.getData().get(position));
        startActivity(intent);
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
        scrollView.setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    // 显示选择区域对话框 如果不选择 则退出程序
    private void showChooseRegionDialog(){
        SimpleDialog dialog = new SimpleDialog(getActivity());
        dialog.canceledOnTouchOutside(false);
//        dialog.title("提示");
        dialog.cancelable(false);
        dialog.message("您必须先选择地址")
                .negativeAction("取消")
                .negativeActionClickListener(v -> {
                    dialog.dismiss();
                    getActivity().finish();
                })
                .positiveAction("确定")
                .positiveActionClickListener(v->{
                    dialog.dismiss();
                    onLocationClick();
                })
                .show();
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
        dismissProgressDialog();
        if (isActive()){
            // 设置大类的宽度
            setupScrollViewWidth();
            radioGroup.setDataAndNotify(dataEntityList);
            if (dataEntityList.size() == 0){
                onDataEmpty();
                return;
            }
            lastLargeTypeId = dataEntityList.get(0).getId();
            marketPresenter.parseSmallTypeData(lastLargeTypeId);
        }
    }

    @Override
    public void onLoadSmallTypeData(String parentId, List<MarketDataEntity> dataEntityList) {
        dismissProgressDialog();
        if (isActive()){
            horizontalGroup.setDataAndNotify(dataEntityList);
            lastSmallTypeId = dataEntityList.get(0).getId();
            marketPresenter.getProductsList(lastSmallTypeId);
        }
    }

    @Override
    public void onLoadProductData(List<ProductEntity> dataEntityList, boolean isLoadComplete, boolean isLoadmore) {
        dismissProgressDialog();
        if (isActive()){
            if (!isLoadmore){
                adapter.setData(dataEntityList);
                zListView.smoothScrollToPosition(0);
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
        dismissProgressDialog();
        Snackbar.make(parentLayout, "加载更多时出现异常，请重新操作", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMoreComplete() {

    }

    @Override
    public void setPresenter(MarketPresenter presenter) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
