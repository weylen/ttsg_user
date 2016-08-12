package com.guaigou.cd.minutestohome.activity.addressmgr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.BaseActivity;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.adapter.AddressAdapter;
import com.guaigou.cd.minutestohome.entity.AddressEntity;
import com.guaigou.cd.minutestohome.util.AddressUtil;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.jakewharton.rxbinding.widget.RxAdapter;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016-06-18.
 * 编辑收货地址
 */
public class AddressActivity extends BaseActivity {

    @Bind(R.id.Generic_List) ListView listView;
    @Bind(R.id.text_title) TextView titleView;

    private AddressAdapter addressAdapter;

    public static final int NEW_CODE = 100;
    public static final int EDIT_CODE = 200;
    private Subscription subscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        // 标题
        titleView.setText("收货地址");

        listView.setEmptyView(findViewById(R.id.text_empty));

        addressAdapter = new AddressAdapter(this, null);
        addressAdapter.setOnEditActionListener((position, entity) -> {
            Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
            intent.putExtra("Entity", entity);
            intent.putExtra("Position", position);
            startActivityForResult(intent, EDIT_CODE);
        });
        listView.setAdapter(addressAdapter);
        // 加载数据
        loadData();
    }

    @OnClick(R.id.img_back)
    void onBackClick(){
        finish();
    }

    @OnClick(R.id.text_newAddress)
    void onnewAddressClick(){
        Intent intent = new Intent(AddressActivity.this, NewAddressActivity.class);
        startActivityForResult(intent, NEW_CODE);
    }

    @OnItemClick(R.id.Generic_List)
    void onItemClick(int position){
        AddressEntity addressEntity = addressAdapter.getData().get(position);
        boolean isReturnData = getIntent().getBooleanExtra("returndata", false);
        if (isReturnData){
            Intent intent = new Intent();
            intent.putExtra("AddressEntity", addressEntity);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void loadData(){
        List<AddressEntity> data = AddressUtil.getAddressList(this);
        addressAdapter.setData(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            AddressEntity entity;
            switch (requestCode){
                case NEW_CODE:
                    entity = (AddressEntity)data.getSerializableExtra("Entity");
                    if (entity != null){
                        // 判断新增的是否为默认地址
                        if (entity.isDefaultAddress()){
                            resetDefaultAddress(addressAdapter.getData());
                        }
                        addressAdapter.addData(entity);
                    }
                    break;
                case EDIT_CODE:
                    entity = (AddressEntity)data.getSerializableExtra("Entity");
                    boolean isDelete = data.getBooleanExtra("IsDelete", false);
                    int position = data.getIntExtra("Position", -1);
                    if (isDelete){
                        addressAdapter.getData().remove(position);
                        addressAdapter.notifyDataSetChanged();
                    }else{
                        if (entity != null && position >= 0){
                            // 判断新增的是否为默认地址
                            if (entity.isDefaultAddress()){
                                resetDefaultAddress(addressAdapter.getData());
                            }
                            addressAdapter.updateData(position, entity);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册数据变化监听
        subscription = RxAdapter.dataChanges(addressAdapter)
                .subscribe(addressAdapter1 -> {
                    // 保存数据
                    AddressUtil.saveAddressList(getApplicationContext(), addressAdapter1.getData());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

    /**
     * 重置默认的地址
     * @param data
     */
    private void resetDefaultAddress(List<AddressEntity> data){
        int size = data == null ? 0 : data.size();
        for (int i = 0; i < size; i++){
            data.get(i).setDefaultAddress(false);
        }
    }
}
