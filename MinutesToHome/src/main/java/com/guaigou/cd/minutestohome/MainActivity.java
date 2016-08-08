package com.guaigou.cd.minutestohome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.activity.market.MarketData;
import com.guaigou.cd.minutestohome.activity.mine.MeFragment;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartFragment;
import com.guaigou.cd.minutestohome.cache.DataCache;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.activity.market.MarketFragment;
import com.guaigou.cd.minutestohome.prefs.CartPrefs;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.DialogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by Administrator on 2016-06-17.
 */
public class MainActivity extends BaseActivity{

    @Bind(R.id.Generic_Rb01) RadioButton rb01;
    @Bind(R.id.Generic_Rb02) RadioButton rb02;
    @Bind(R.id.Generic_Rb03) RadioButton rb03;
    @Bind(R.id.text_cartnum) TextView cartNumView; // 购物车数量视图
    /**
     * 记录最后一次点击的id
     */
    private int lastChoiceId = R.id.Generic_Rb01;
    private RegionEntity entity;
    private MarketFragment marketFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        entity = getIntent().getParcelableExtra("RegionEntity");
        if (entity != null){
            RegionEntity saveEntity = RegionPrefs.getRegionData(this);
            if (saveEntity == null ){
                RegionPrefs.saveRegionData(this, entity);
            }else{
                // 两次选择不一样
                if (!saveEntity.getId().equalsIgnoreCase(entity.getId())){
                    // 清除所有缓存的数据
                    MarketData.INSTANCE.currentProductId = null;
                    MarketData.INSTANCE.setKindData(null);
                    DataCache.INSTANCE.removeAll();
                    RegionPrefs.saveRegionData(this, entity);
                }
            }

        }
        initViews();

        marketFragment = new MarketFragment();
    }

    /**
     * 初始化视图
     */
    private void initViews(){
        rb01.setOnClickListener(rbClick);
        rb02.setOnClickListener(rbClick);
        rb03.setOnClickListener(rbClick);
    }

    private View.OnClickListener rbClick = v -> {
        int id = v.getId();
        if(lastChoiceId == id){
            return;
        }
        Fragment fragment = null;
        String tag = null;
        switch (id){
            case R.id.Generic_Rb01:
                tag = MarketFragment.TAG;
                fragment = marketFragment;
                break;
            case R.id.Generic_Rb02:
                if (!LoginData.INSTANCE.isLogin(this)){
                    DialogUtil.showLoginDialog(this);
                    return;
                }

                tag = CartFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new CartFragment();
                }
                break;
            case R.id.Generic_Rb03:
                tag = MeFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new MeFragment();
                }
                break;
        }

        if(fragment != null && tag != null){
            replaceFragment(fragment, tag);
        }
        lastChoiceId = id;
    };

    /**
     * 替换Fragment
     * @param fragment
     * @param tag
     */
    public void replaceFragment(Fragment fragment, String tag){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Container, fragment, tag)
                .commit();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        boolean isChooseCart = getIntent().getBooleanExtra("ChooseCart", false);
        if (isChooseCart){
            lastChoiceId = R.id.Generic_Rb02;
            rb02.setChecked(true);
            replaceFragment(new CartFragment(), CartFragment.TAG);
        }else {
            replaceFragment(marketFragment, MarketFragment.TAG);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册观察者
        CartData.INSTANCE.registerObserver(subscriber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        CartData.INSTANCE.unregisterObserver(subscriber);
        // 保存购物车的数据
        saveCartData();
    }

    /**
     * 保存购物车数据
     */
    private void saveCartData(){
        if (LoginData.INSTANCE.isLogin(this)){
            CartPrefs.saveCartData(this, CartData.INSTANCE.getData());
        }
    }

    Subscriber<Integer> subscriber = new Subscriber<Integer>() {
        @Override
        public void onCompleted() {}

        @Override
        public void onError(Throwable e) {}

        @Override
        public void onNext(Integer integer) {
            if (integer <= 0){
                cartNumView.setVisibility(View.GONE);
            }else {
                cartNumView.setVisibility(View.VISIBLE);
                cartNumView.setText(String.valueOf(integer));
            }
        }
    };
}
