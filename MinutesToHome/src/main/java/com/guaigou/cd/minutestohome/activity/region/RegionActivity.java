package com.guaigou.cd.minutestohome.activity.region;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.MainActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.adapter.RegionAdapter;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-22.
 * 选择区域
 */
public class RegionActivity extends BaseActivity implements RegionView{

    @Bind(R.id.text_title) TextView mTextTitleView;
    @Bind(R.id.Generic_List) ListView mListView;
    @Bind(R.id.Container) View containerView;

    private RegionPresenter regionPresenter;
    private RegionAdapter adapter;

    private String pid; // parentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        ButterKnife.bind(this);

        mTextTitleView.setText(R.string.RegionCommunity);

        regionPresenter = new RegionPresenter(this);
        setPresenter(regionPresenter);

        mListView.setEmptyView(findViewById(R.id.text_empty));
        adapter = new RegionAdapter(this, null);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        onBackClick();
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        DebugUtil.d("RegionActivity-onBackClick pid:" + pid);
        if (pid == null || "0".equalsIgnoreCase(pid)){
            finish();
        }else{
            regionPresenter.onBackClick(pid);
        }
    }

    @OnItemClick(R.id.Generic_List)
    void onItemClick(int position){
        RegionEntity entity = adapter.getData().get(position);
        pid = entity.getId();
        regionPresenter.setNextLevelData(entity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        regionPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStartLoading() {
        showProgressDialog("加载中...");
    }

    @Override
    public void onLoadFailure() {
        dismissProgressDialog();
    }

    @Override
    public void onLoadSuccess() {
        dismissProgressDialog();
    }

    @Override
    public void onSetProvinceData(List<RegionEntity> data) {
        Preconditions.checkNotNull(adapter, "Adapter can't be null");
        adapter.setData(data);
    }

    @Override // 设置下一级数据
    public void onSetNextLevelData(List<RegionEntity> data) {
        adapter.setData(data);
    }

    @Override // 设置上一级数据
    public void onSetBackLevelData(List<RegionEntity> data) {
        pid = data.get(0).getPid();
        adapter.setData(data);
    }

    @Override
    public void setPresenter(RegionPresenter presenter) {

    }

    @Override // 没有下一级 则跳转到主页面
    public void onNextNone(RegionEntity entity) {
        RegionEntity saveEntity = RegionPrefs.getRegionData(this);
            // 两次选择不一样
        if (saveEntity != null && !saveEntity.getId().equalsIgnoreCase(entity.getId())){
            // 清除所有缓存的数据
            showChangeRegionDialog(entity);
        }else{
            forward(entity);
        }
    }

    private void forward(RegionEntity entity){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("RegionEntity", entity);
        startActivity(intent);
    }

    /**
     * 显示切换地区对话框
     * @param entity
     */
    private void showChangeRegionDialog(RegionEntity entity){
        RegionEntity oldEntity = RegionPrefs.getRegionData(getApplicationContext());
        if (!LocaleUtil.hasChooseRegion(this) || entity.getId().equalsIgnoreCase(oldEntity.getId())){
            forward(entity);
            return;
        }

        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("切换地区将会清空购物车")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    clearCart(entity);
                }).show();
    }

    /**
     * 清空购物车
     */
    private void clearCart(RegionEntity entity){
        showProgressDialog("处理中...");

        List<CartEntity> data = CartData.INSTANCE.getData();
        if (data == null || data.size() == 0){
            dismissProgressDialog();
            forward(entity);
            return;
        }
        Observable.just(data)
                .subscribeOn(Schedulers.io())
                .map(cartEntities -> map(data))
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    remoteClearCart(s, entity);
                });
    }

    private String map(List<CartEntity> data){
        RegionEntity oldEntity = RegionPrefs.getRegionData(getApplicationContext());
        String param = "";
        for (CartEntity entity : data){
            param += oldEntity.getId() + "-" + entity.getId();
        }
        if (param.endsWith(",")){
            param = param.substring(0, param.length() - 1);
        }
        return param;
    }

    private void remoteClearCart(String key, RegionEntity entity){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .deleteCart(key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("RegionActivity 清空购物车失败：" + e.getMessage());
                        doResult(false, entity);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("RegionActivity 清空购物车：" + jsonObject);
                        doResult(ResponseMgr.getStatus(jsonObject) == 1, entity);
                    }
                });
    }

    private void doResult(boolean status, RegionEntity entity){
        dismissProgressDialog();
        if (status){
            CartData.INSTANCE.setData(null);
            forward(entity);
        }else {
            showSnakeView(containerView, "清空购物车失败，请重新操作");
        }
    }
}
