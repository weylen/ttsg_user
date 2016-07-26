package com.guaigou.cd.minutestohome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.guaigou.cd.minutestohome.activity.mine.MeFragment;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartFragment;
import com.guaigou.cd.minutestohome.entity.RegionEntity;
import com.guaigou.cd.minutestohome.activity.market.MarketFragment;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.DebugUtil;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.functions.Action1;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        entity = getIntent().getParcelableExtra("RegionEntity");
        if (entity != null){
            DebugUtil.d("MainActivity onCreate name : " + entity.getName());
            RegionPrefs.saveRegionData(this, entity);
        }

        initViews();
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
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if(fragment == null){
                    fragment = new MarketFragment();
                }
                break;
            case R.id.Generic_Rb02:
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
                .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit)
                .replace(R.id.Container, fragment, tag)
                .commit();
    }

    public void replaceFragment(Fragment fragment, String tag, boolean addToBackStack){
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit)
                .replace(R.id.Container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 默认加载超市
        replaceFragment(new MarketFragment(), MarketFragment.TAG);
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
