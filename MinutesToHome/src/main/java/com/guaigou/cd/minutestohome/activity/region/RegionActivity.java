package com.guaigou.cd.minutestohome.activity.region;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.MainActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.adapter.RegionAdapter;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by weylen on 2016-07-22.
 * 选择区域
 */
public class RegionActivity extends BaseActivity implements RegionView{

    @Bind(R.id.text_title)
    TextView mTextTitleView;
    @Bind(R.id.Generic_List)
    ListView mListView;

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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("RegionEntity", entity);
        startActivity(intent);
    }
}
