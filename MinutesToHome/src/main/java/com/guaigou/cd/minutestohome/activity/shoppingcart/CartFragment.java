package com.guaigou.cd.minutestohome.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.confirmorder.ConfirmOrderActivity;
import com.guaigou.cd.minutestohome.activity.market.ShopStatusData;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.adapter.CartAdapter;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.util.CalendarUtil;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.util.MathUtil;
import com.guaigou.cd.minutestohome.view.EmptyViewHelper;
import com.guaigou.cd.minutestohome.view.ZListView;
import com.guaigou.cd.minutestohome.view.ZRefreshingView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    @Bind(R.id.text_settlement) TextView settlementView;

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

        adapter = new CartAdapter(getActivity(), CartData.INSTANCE.getData());
        zListView.setAdapter(adapter);
        zListView.setLoadComplete(true);

        presenter = new CartPresenter(this);

        if (adapter.getCount() == 0){
            editView.setVisibility(View.GONE);
            paymentLayout.setVisibility(View.GONE);
        }else{
            editView.setVisibility(View.VISIBLE);
            paymentLayout.setVisibility(View.VISIBLE);
            calculateAllPrice();
        }
    }

    void refresh(){
        zRefreshingView.setRefreshing(false);
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
        CartEntity entity = adapter.getData().get(position);
        intent.putExtra("Entity", LocaleUtil.format2ProductEntity(entity));
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
    void onDeleteClick(View view){
        List<CartEntity> deleteData = adapter.getDeleteData();
        if (0 == deleteData.size()){
            Snackbar.make(view, "您还没有选择商品哦！", Snackbar.LENGTH_SHORT).show();
            return;
        }
        showDeleteDialog(deleteData);
    }

    @OnClick(R.id.text_settlement)
    void onSettlementClick(){
        if (LocaleUtil.isListEmpty(CartData.INSTANCE.getData())){
            showSnakeView(containerView, "购物车为空，请先挑选商品");
            return;
        }
        // 判断当前时间是否为夜间模式
        if (LocaleUtil.isOnNightTime()){
            // 是夜间模式 则判断购物车的商品有没有不属于夜间模式的
            if (!checkProducts(true)){
                return;
            }
        }else if (LocaleUtil.isOnTime()){
            if (!checkProducts(false)){
                return;
            }
        }

        Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
        startActivity(intent);
    }

    /**
     * 返回true表示满足条件 否则不满足条件
     * @param isNightProduct
     * @return
     */
    private boolean checkProducts(boolean isNightProduct){
        List<CartEntity> data = adapter.getData();
        if (!LocaleUtil.isListEmpty(data)){
            int size = data.size();
            for (int i = 0; i < size; i++){
                CartEntity entity = data.get(i);
                if (isNightProduct != entity.isNightProduct()){ // 判断是否为夜间商品，如果不是则提醒用户
                    showNotNightProductDialog(entity.getDisplayName(), isNightProduct);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 展现非直营模式时间的商品
     * @param name
     */
    private void showNotNightProductDialog(String name, boolean isNightProduct){
        String message = isNightProduct ? "当前时间段属于直营模式，而购物车中包含非直营模式的商品(" + name + "),请删除后再进行下单"
                : "当前时间段不属于直营模式，而购物车中包含直营模式的商品(" + name + "),请删除后再进行下单";
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("知道了", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showDeleteDialog(List<CartEntity> deleteData){
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("删除选定的商品?")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    deleteLocaleData(deleteData);
                })
                .show();
    }

    private void deleteLocaleData(List<CartEntity> deleteData){
        CartData.INSTANCE.removeAll(deleteData);
        adapter.getData().removeAll(deleteData);
        adapter.resetStatusArray();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 注册观察者
        CartData.INSTANCE.registerObserver(subscriber);
        ShopStatusData data = ShopStatusData.INSTANCE;
        if (data.status != 1){
            settlementView.setText("未营业");
            settlementView.setEnabled(false);
        }else if (LocaleUtil.isOnNightTime() || LocaleUtil.isOnTime()){
            settlementView.setText("去结算");
            settlementView.setEnabled(true);
        }else {
            DebugUtil.d("CartFragment onResume不在营业时间" );
            settlementView.setText("不在营业时间");
            settlementView.setEnabled(false);
        }
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
                adapter.setData(null);
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
        allPriceView.setText(MathUtil.calculate());
    }
}
