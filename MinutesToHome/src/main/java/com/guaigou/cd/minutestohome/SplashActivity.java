package com.guaigou.cd.minutestohome;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.activity.login.LoginData;
import com.guaigou.cd.minutestohome.activity.market.MarketData;
import com.guaigou.cd.minutestohome.activity.region.RegionActivity;
import com.guaigou.cd.minutestohome.activity.region.RegionData;
import com.guaigou.cd.minutestohome.activity.shoppingcart.CartData;
import com.guaigou.cd.minutestohome.entity.CartEntity;
import com.guaigou.cd.minutestohome.http.ApiException;
import com.guaigou.cd.minutestohome.http.Client;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RespSubscribe;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.http.Transformer;
import com.guaigou.cd.minutestohome.prefs.NewVersionData;
import com.guaigou.cd.minutestohome.prefs.RegionPrefs;
import com.guaigou.cd.minutestohome.util.CartUtil;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-08-06.
 */
public class SplashActivity extends BaseActivity {

    private boolean isSettingNetwork = false;
    private static final int SLEEP_TIME = 2 * 1000; // 休眠时间2秒钟
    private boolean isHaveData = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isHaveData = MarketData.INSTANCE.getKindData() != null;
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前无网络连接")
                .setCancelable(false)
                .setPositiveButton("设置", (dialog, which) -> {
                    peekInSetting();
                })
                .setNegativeButton("退出", (dialog, which) ->{
                    finish();
                })
                .show();
    }

    private void peekInSetting(){
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        startActivity(intent);
        isSettingNetwork = true;
    }

    private long start;
    private long end;
    @Override
    protected void onResume() {
        super.onResume();
        // 判断是否有网络连接
        if (!LocaleUtil.isNetworkConnected(this)){
            showAlertDialog();
        }else{
            checkNewVersion();
            // 判断是否选择了小区
            if (!LocaleUtil.hasChooseRegion(this)){
                start = System.currentTimeMillis();
                remoteRegion();
                // 判断是否进行了登录
            }else if (LoginData.INSTANCE.isLogin(this)){
                // 已经登录
                start = System.currentTimeMillis();
                remoteCart();
            }else{
                // 没有登录
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }, SLEEP_TIME);
            }
        }
    }

    private void remoteCart() {
        CartUtil.INSTANCE.remoteCart(this, () -> {
            getCartDetails();
        });
    }

    private void getCartDetails(){
        List<CartEntity> entities = CartData.INSTANCE.getData();
        if (LocaleUtil.isListEmpty(entities)){
            peekInHome();
            return;
        }

        if (true){
            peekInHome();
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (CartEntity entity : entities){
            builder.append(entity.getId()+",");
        }
        builder.deleteCharAt(builder.length()-1);
        Client.request()
                .getCartList(builder.toString(), RegionPrefs.getRegionData(this).getId())
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        peekInHome();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        DebugUtil.d("SplashActivity 获取购物车的数据：" + s);
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            parseCartData(s);
                        }
                        peekInHome();
                    }
                }));
    }

    private void parseCartData(JsonObject s){
        Gson gson = new Gson();
        List<CartEntity> cartEntities = gson.fromJson(s.get("data").getAsJsonArray(),
                new TypeToken<ArrayList<CartEntity>>(){}.getType());

        List<CartEntity> entities = CartData.INSTANCE.getData();
        if (!LocaleUtil.isListEmpty(cartEntities)){
            List<CartEntity> cloneData = new ArrayList<>();
            // 暂时先不要已经下架的商品
            for (CartEntity entity : cartEntities){
                if (!LocaleUtil.isShelves(entity)){
                    cloneData.add(entity);
                }
            }

            for (CartEntity entity : cloneData){
                for (CartEntity oldEntity : entities){
                    if (entity.getId().equalsIgnoreCase(oldEntity.getId())){
                        entity.setAmount(oldEntity.getAmount());
                        break;
                    }
                }
            }
            cartEntities = cloneData;
        }
        CartData.INSTANCE.setData(cartEntities);
    }

    private void peekInHome(){
        end = System.currentTimeMillis();
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, isSettingNetwork ? 300 : isHaveData ? 500 : (SLEEP_TIME - (end - start)));
    }

    private void remoteRegion(){
        Client.request().getRegion()
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        end = System.currentTimeMillis();
                        peekInRegion();
                    }

                    @Override
                    public void onNext(JsonObject s) {
                        end = System.currentTimeMillis();
                        int status = ResponseMgr.getStatus(s);
                        if (status == 1){
                            // 缓存数据
                            RegionData.INSTANCE.setRegionData(s);
                        }
                        peekInRegion();
                    }
                });
    }

    private void peekInRegion(){
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, RegionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, isSettingNetwork ? 300 : SLEEP_TIME - (end - start));
    }

    private void checkNewVersion() {
        if (isHaveData){ // 如果数据已经存在 则不检查新版本
            return;
        }
        Client.request().newVersion(2)
                .compose(Transformer.switchSchedulers())
                .compose(Transformer.sTransformer())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("SplashActivity 获取新版本:" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1) {
                            NewVersionData newVersionData = NewVersionData.INSTANCE;
                            JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
                            String vNum = dataObject.get("v_n").getAsString();
                            try {
                                String name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                                DebugUtil.d("SplashActivity vNum:" + vNum + ".name:" + name);
                                // 有新版本
                                if (name != null && !name.equalsIgnoreCase(vNum)) {
                                    newVersionData.isNewVersion = true;
                                    newVersionData.downloadUrl = Constants.BASE_URL + dataObject.get("path").getAsString();
                                    newVersionData.desc = dataObject.get("context").getAsString();
                                    newVersionData.isMust = dataObject.get("update").getAsInt() == 1;
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }));
    }
}
