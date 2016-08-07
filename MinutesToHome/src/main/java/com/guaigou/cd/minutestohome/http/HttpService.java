package com.guaigou.cd.minutestohome.http;

import com.google.gson.JsonObject;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by weylen on 2016-07-20.
 * 数据请求类
 */
public interface HttpService {

    /**
     * 登录
     * @param name
     * @param pwd
     * @return
     */
    @FormUrlEncoded
    @POST("scca-clogin")
    Observable<JsonObject> login(
            @Field("uname") String name, // 用户名
            @Field("upass") String pwd // 密码
    );

    /**
     * 获取验证码
     * @param phoneNum 手机号码
     * @return
     */
    @FormUrlEncoded
    @POST("scca-smsyzm")
    Observable<String> requestValidateCode(
            @Field("uname") String phoneNum);

    /**
     * 注册
     * @param phoneNum 手机号码
     * @param pwd 密码
     * @return
     */
    @FormUrlEncoded
    @POST("scca-creg")
    Observable<JsonObject> register(
            @Field("uname") String phoneNum, // 手机号
            @Field("upass") String pwd // 密码
    );

    /**
     * 获取地区数据
     * @return
     */
    @POST("scca-")
    Observable<JsonObject> getRegion();

    /**
     * 获取所有品种 包括大类和小类
     * @return
     */
    @POST("scca-getKind")
    Observable<JsonObject> getAllKind();

    /**
     * 获取选择地区的品种数据
     * @return
     */
    @FormUrlEncoded
    @POST("scca-getAreaKind")
    Observable<JsonObject> getRegionKind(@Field("areaId") String areaId);

    /**
     * 地区商品获取
     * @param areaId 地区id
     * @param sortId 类别id 传大类id获取整个大类的数据 传小类id获取小类数据 不传获取全部数据
     * @param pageNum 页码 默认为1
     */
    @FormUrlEncoded
    @POST("scca-getAreaProduct")
    Observable<JsonObject> getRegionProducts(
            @Field("areaId") String areaId,
            @Field("sort") String sortId,
            @Field("pageNum") int pageNum,
            @Field("uname") String keyword);

    /**
     * 添加商品到购物车
     * @return
     */
    @FormUrlEncoded
    @POST("csca-add")
    Observable<JsonObject> addProductToCart(
            @Field("key") String info
    );

    /**
     * 获取购物车列表
     * @return
     */
    @POST("csca-")
    Observable<JsonObject> getCartList();


    /**
     * 注销
     * @return
     */
    @POST("scca-logout")
    Observable<JsonObject> logout();


    /**
     * 意见反馈
     * @param key
     * @param content
     * @return
     */
    @FormUrlEncoded
    @POST("cia- suggest")
    Observable<JsonObject> feedBack(
            @Field("key") String key,
            @Field("text") String content
    );

    @FormUrlEncoded
    @POST("csca-del")
    Observable<JsonObject> deleteCart(
            @Field("key") String key
    );

    @FormUrlEncoded
    @POST("csca-store")
    Observable<JsonObject> save2cart(
            @Field("key") String key
    );
}
