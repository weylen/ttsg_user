package com.guaigou.cd.minutestohome.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;

import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.addressmgr.AddressActivity;
import com.guaigou.cd.minutestohome.activity.feedback.FeedbackActivity;
import com.guaigou.cd.minutestohome.activity.login.LoginActivity;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.activity.myorders.OrderActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016-06-18.
 */
public class MeFragment extends BaseFragment {

    public static final String TAG = MeFragment.class.getName();

    @Bind(R.id.Container) View containerView;

    @Override
    public int layoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    /**
     * 地址点击事件
     */
    @OnClick(R.id.text_address)
    void onAddressClick(){
        Intent intent = new Intent(getActivity(), AddressActivity.class);
        startActivity(intent);
    }

    /**
     * 订单点击事件
     */
    @OnClick(R.id.text_order)
    void onOrderClick(){
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        startActivity(intent);
    }

    /**
     * 积分点击事件
     */
    @OnClick(R.id.text_score)
    void onScoreClick(){
        Snackbar.make(containerView, R.string.Score_Building, Snackbar.LENGTH_SHORT).show();
    }

    private Snackbar snackbar = null;
    /**
     * 联系店主点击
     */
    @OnClick(R.id.text_contacts_shopper)
    void onContactsShopper(){
        if (snackbar != null && snackbar.isShownOrQueued()){
            snackbar.dismiss();
        }else{
            snackbar = Snackbar.make(containerView, "拨打店主电话？", Snackbar.LENGTH_LONG);
            snackbar.setAction("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.themeColor));
            snackbar.show();
        }
    }

    /**
     * 意见反馈点击
     */
    @OnClick(R.id.text_feedback)
    void onFeedbackClick(){
        Intent intent = new Intent(getActivity(), FeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.head_layout)
    void onHeadLayoutClick(){
        // 没有登录则跳转到登录界面
        if (!LoginData.INSTANCE.isLogin(getActivity())){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (snackbar != null && snackbar.isShownOrQueued()){
            snackbar.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
