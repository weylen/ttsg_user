package com.guaigou.cd.minutestohome.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.guaigou.cd.minutestohome.BaseFragment;
import com.guaigou.cd.minutestohome.R;
import com.guaigou.cd.minutestohome.activity.addressmgr.AddressActivity;
import com.guaigou.cd.minutestohome.activity.feedback.FeedbackActivity;
import com.guaigou.cd.minutestohome.activity.login.LoginActivity;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.activity.myorders.OrderActivity;
import com.guaigou.cd.minutestohome.entity.AccountEntity;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016-06-18.
 */
public class MeFragment extends BaseFragment {

    public static final String TAG = MeFragment.class.getName();

    @Bind(R.id.Container) View containerView;
    @Bind(R.id.text_title) TextView mTitleView;
    @Bind(R.id.text_phone) TextView mPhoneView;
    @Bind(R.id.text_logout) TextView mLogouView;

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
        }else{
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            getActivity().startActivity(intent);
        }
    }

    @OnClick(R.id.text_logout)
    public void onLogoutClick(){
        showProgressDialog("请稍后");
        RetrofitFactory.getRetrofit()
                .create(HttpService.class)
                .logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        logout();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("MeFragment 注销：" + jsonObject);
                        dismissProgressDialog();
                        logout();
                    }
                });
    }

    private void logout(){
        LoginData.INSTANCE.logout(getContext());
        checkLogin();
    }

    private void checkLogin(){
        // 检查是否登录
        boolean isLogin = LoginData.INSTANCE.isLogin(getContext());
        if (isLogin){
            AccountEntity accountEntity = LoginData.INSTANCE.getAccountEntity(getContext());
            String nickName = accountEntity.getName();
            mTitleView.setText(TextUtils.isEmpty(nickName) ? "点击设置昵称" : nickName);
            mPhoneView.setText(accountEntity.getUname());
            RxView.visibility(mLogouView).call(Boolean.TRUE);
        }else {
            mTitleView.setText("点击登录");
            RxView.visibility(mLogouView).call(Boolean.FALSE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLogin();
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
