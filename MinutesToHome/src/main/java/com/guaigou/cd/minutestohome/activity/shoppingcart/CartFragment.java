package com.guaigou.cd.minutestohome.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.confirmorder.ConfirmOrderActivity;
import com.guaigou.cd.minutestohome.activity.productdetails.ProductDetailsActivity;
import com.guaigou.cd.minutestohome.adapter.CartAdapter;
import com.guaigou.cd.minutestohome.entity.ProductEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;
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
public class CartFragment extends BaseFragment {

    public static final String TAG = CartFragment.class.getName();

    @Bind(R.id.action_layout) View actionLayout; // 删除操作视图
    @Bind(R.id.payment_layout) View paymentLayout; // 付款视图
    @Bind(R.id.text_edit) TextView editView; // 编辑文本视图
    @Bind(R.id.Generic_List) ZListView zListView; // 列表视图
    @Bind(R.id.refreshLayout) ZRefreshingView zRefreshingView; // 下拉刷新视图
    @Bind(R.id.parentFrameLayout) FrameLayout parentFrameLayout; // 刷新父视图
    @Bind(R.id.Container) View containerView;

    private EmptyViewHelper emptyViewHelper; // 空视图

    private CartAdapter adapter;

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
        // 设置列表的加载更多事件
        zListView.setOnLoadmoreListener(()->{});
        // 添加空视图
        emptyViewHelper = new EmptyViewHelper(zListView, "没有挑选商品", parentFrameLayout);
        emptyViewHelper.setRefreshListener(() -> refresh());
        // 空视图点击事件
        emptyViewHelper.setOnEmptyTouchListener(()->{});

        // -----------------------------测试数据
        List<ProductEntity> data = new ArrayList<>();
        ProductEntity entity = new ProductEntity();
        entity.setNumber(1);
        entity.setStandard("瓶");
        entity.setName("矿泉水");
        entity.setBegin("2016-09-01");
        entity.setEnd("2016-09-05");
        entity.setReserve("3");
        entity.setPromote("1.0");
        entity.setInfo("限时半价");
        entity.setPrice("2.0");
        entity.setKind("水");

        ProductEntity entity1 = new ProductEntity();
        entity1.setNumber(1);
        entity1.setStandard("瓶");
        entity1.setName("矿泉水");
        entity1.setBegin("2016-09-01");
        entity1.setEnd("2016-09-05");
        entity1.setReserve("3");
        entity1.setInfo("限时半价");
        entity1.setPrice("2.0");
        entity1.setKind("水");

        data.add(entity);
        data.add(entity1);
        data.add(entity);
        data.add(entity1);
        data.add(entity);
        List<ProductEntity> saveData = CartData.INSTANCE.getData();
        if (saveData == null || saveData.size() == 0){
            CartData.INSTANCE.addAll(data);
        }
        // -----------------------------测试数据
        adapter = new CartAdapter(getActivity(), CartData.INSTANCE.getData());
        zListView.setAdapter(adapter);
        zListView.setLoadComplete(true);
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
        List<ProductEntity> deleteData = adapter.getDeleteData();
        if (0 == deleteData.size()){
            Snackbar.make(containerView, "您还没有选择商品哦！", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // TODO... 服务器的删除？
        showDeleteDialog(deleteData);
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
            DebugUtil.d("CartFragment onNext count:" + count);
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
        }
    };
}
