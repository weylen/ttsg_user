package com.guaigou.cd.minutestohome.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.confirmorder.ConfirmOrderActivity;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.adapter.CartAdapter;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.view.EmptyViewHelper;
import com.guaigou.cd.minutestohome.view.ZListView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;
import com.rey.material.app.SimpleDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.Subscriber;

/**
 * Created by Administrator on 2016-06-18.
 */
public class CartFragment extends BaseFragment implements CartView{

    public static final String TAG = CartFragment.class.getName();

    @Bind(R.id.action_layout) View actionLayout; // 删除操作视图
    @Bind(R.id.payment_layout) View paymentLayout; // 付款视图
    @Bind(R.id.text_edit) TextView editView; // 编辑文本视图
    @Bind(R.id.Generic_List) ZListView zListView; // 列表视图
    @Bind(R.id.refreshLayout) ZRefreshingView zRefreshingView; // 下拉刷新视图
    @Bind(R.id.parentFrameLayout) FrameLayout parentFrameLayout; // 刷新父视图
    @Bind(R.id.text_price) TextView allPriceView; // 总价格
    @Bind(R.id.Container) View containerView;

    private EmptyViewHelper emptyViewHelper; // 空视图

    private CartAdapter adapter;

    private CartPresenter presenter;

    @Override
    public int layoutId() {
        return R.layout.fragment_cart;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        // 设置刷新事件
        zRefreshingView.setOnRefreshListener(() -> refresh());
        zListView.setShowFooterView(false);
        // 添加空视图
        emptyViewHelper = new EmptyViewHelper(zListView, "没有挑选商品", parentFrameLayout);
        emptyViewHelper.setRefreshListener(() -> refresh());
        // 空视图点击事件
        emptyViewHelper.setOnEmptyTouchListener(()-> {
            zRefreshingView.setRefreshing(true);
            refresh();
        });
        adapter = new CartAdapter(getActivity(), null);
        zListView.setAdapter(adapter);
        zListView.setLoadComplete(true);

        presenter = new CartPresenter(this);

        // TODO 请求服务器数据
        if (adapter.getCount() == 0){
            editView.setVisibility(View.GONE);
            paymentLayout.setVisibility(View.GONE);
            presenter.requestList();
        }else{
            // TODO 计算总价格
            calculateAllPrice();
        }

    }

    void refresh(){

    }

    /**
     * 编辑事件监听
     */
    @OnClick(R.id.text_edit)
    void onEditClick(){
        String text = editView.getText().toString();
        if ("编辑".equalsIgnoreCase(text)){
            adapter.setEditable(true);
            editView.setText("完成");

            actionLayout.setVisibility(View.VISIBLE);
            paymentLayout.setVisibility(View.GONE);
        }else if ("完成".equalsIgnoreCase(text)){
            adapter.setEditable(false);
            editView.setText("编辑");

            paymentLayout.setVisibility(View.VISIBLE);
            actionLayout.setVisibility(View.GONE);
        }
    }

    @OnItemClick(R.id.Generic_List)
    void onItemClick(int position){
        Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
        intent.putExtra("Entity", adapter.getData().get(position));
        startActivity(intent);
    }

    /**
     * 全选事件
     */
    @OnCheckedChanged(R.id.box_all)
    void onAllChecked(boolean isChecked){
        adapter.setSelectAll(isChecked);
    }

    /**
     * 删除事件
     */
    @OnClick(R.id.text_delete)
    void onDeleteClick(){
        List<CartEntity> deleteData = adapter.getDeleteData();
        if (0 == deleteData.size()){
            Snackbar.make(containerView, "您还没有选择商品哦！", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // TODO... 服务器的删除？
    }

    @OnClick(R.id.text_settlement)
    void onSettlementClick(){
        Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
        startActivity(intent);
    }

    private void showDeleteDialog(List<ProductEntity> deleteData){
        SimpleDialog dialog = new SimpleDialog(getActivity());
        dialog.message("删除?")
                .negativeAction("取消")
                .negativeActionClickListener(v -> {
                    dialog.dismiss();
                })
                .positiveAction("删除")
                .positiveActionClickListener(v->{
                    dialog.dismiss();
                    deleteLocaleData(deleteData);
                })
                .show();
    }

    private void deleteLocaleData(List<ProductEntity> deleteData){
        CartData.INSTANCE.removeAll(deleteData);

//        List<ProductEntity> data = adapter.getData();
//        data.removeAll(deleteData);
        adapter.resetStatusArray();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO.. 拉取数据

        // 注册观察者
        CartData.INSTANCE.registerObserver(subscriber);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        // 解除注册观察者
        CartData.INSTANCE.unregisterObserver(subscriber);
    }

    private Subscriber<Integer> subscriber = new Subscriber<Integer>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Integer count) {
            String text = editView.getText().toString();
            if (count == 0){
                editView.setVisibility(View.GONE);
                paymentLayout.setVisibility(View.GONE);
                actionLayout.setVisibility(View.GONE);
            }else {
                editView.setVisibility(View.VISIBLE);
                if ("编辑".equalsIgnoreCase(text)){
                    actionLayout.setVisibility(View.GONE);
                    paymentLayout.setVisibility(View.VISIBLE);
                }else if ("完成".equalsIgnoreCase(text)){
                    paymentLayout.setVisibility(View.GONE);
                    actionLayout.setVisibility(View.VISIBLE);
                }
            }

            // 计算总价格
            calculateAllPrice();
        }
    };

    /**
     * 计算总价格
     */
    private void calculateAllPrice(){
        List<CartEntity> data = adapter.getData();
        double allPrice = 0;
        if (data != null && data.size() != 0){
            for (CartEntity entity : data){
                int number = entity.getAmount();
                double price;
                // 促销价格
                String promotionPrice = entity.getPromote();
                if (LocaleUtil.hasPromotion(promotionPrice)){
                    price = parsePrice(promotionPrice);
                }else {
                    price = parsePrice(entity.getSalePrice());
                }
                allPrice += price * number;
            }
        }
        allPriceView.setText(String.valueOf(allPrice));
    }

    private double parsePrice(String price){
        double p = 0d;
        try{
            p = Double.parseDouble(price);
        }catch (Exception e){
            DebugUtil.d("CartFragment parsePrice 解析价格出现异常：" + price);
        }
        return p;
    }

    @Override
    public void onStartLoading() {
        zRefreshingView.setRefreshing(true);
    }

    @Override
    public void onLoadFailure(String message) {

    }

    @Override
    public void onLoadSuccess(List<CartEntity> cartEntities) {

    }

    @Override
    public void onStartRefresh() {

    }

    @Override
    public void onStartEdit() {

    }

    @Override
    public void onEditFailure() {

    }

    @Override
    public void onEditSuccess() {

    }

    @Override
    public boolean isActive() {
        return false;
    }
}
