package com.guaigou.cd.minutestohome.http;

import com.google.gson.JsonObject;

import retrofit2.Response;
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
     * @param sort 设备标识符，1苹果，2是安卓，3是其他
     * @param deviceId 设备唯一标识
     * @return
     */
    @FormUrlEncoded
    @POST("scca-clogin")
    Observable<Response<JsonObject>> login(
            @Field("uname") String name, // 用户名
            @Field("upass") String pwd, // 密码
            @Field("sort") int sort,
            @Field("areaId") String deviceId // 设备标识
    );

    /**
     * 获取验证码
     * @param phoneNum 手机号码
     * @return
     */
    @FormUrlEncoded
    @POST("scca-smsyzm")
    Observable<Response<JsonObject>> requestValidateCode(
            @Field("uname") String phoneNum);

    /**
     * 注册
     * @param phoneNum 手机号码
     * @param pwd 密码
     * @return
     */
    @FormUrlEncoded
    @POST("scca-creg")
    Observable<Response<JsonObject>> register(
            @Field("uname") String phoneNum, // 手机号
            @Field("upass") String pwd // 密码
    );

    /**
     * 找回密码验证接口
     * @param phoneNum
     * @return
     */
    @FormUrlEncoded
    @POST("scca- ransom")
    Observable<Response<JsonObject>> findPwdValidateCode(
            @Field("uname") String phoneNum
    );

    /**
     * 重置密码
     * @param phoneNum
     *      手机号码
     * @param newPwd
     *      新密码
     * @param validateCode
     *      验证码
     * @return
     */
    @FormUrlEncoded
    @POST("scca- updatePass")
    Observable<Response<JsonObject>> resetPwd(
            @Field("uname") String phoneNum,
            @Field("upass") String newPwd,
            @Field("sort") String validateCode
    );

    /**
     * 获取地区数据
     * @return
     */
    @POST("scca-")
    Observable<Response<JsonObject>> getRegion();

    /**
     * 获取所有品种 包括大类和小类
     * @return
     */
    @POST("scca-getKind")
    Observable<Response<JsonObject>> getAllKind();

    /**
     * 获取选择地区的品种数据
     * @return
     */
    @FormUrlEncoded
    @POST("scca-getAreaKind")
    Observable<Response<JsonObject>> getRegionKind(@Field("areaId") String areaId);

    /**
     * 地区商品获取
     * @param areaId 地区id
     * @param sortId 类别id 传大类id获取整个大类的数据 传小类id获取小类数据 不传获取全部数据
     * @param pageNum 页码 默认为1
     */
    @FormUrlEncoded
    @POST("scca-getAreaProduct")
    Observable<Response<JsonObject>> getRegionProducts(
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
    Observable<Response<JsonObject>> addProductToCart(
            @Field("key") String info
    );

    /**
     * 获取购物车列表
     * @param info 商品id id,id
     * @return
     */
    @FormUrlEncoded
    @POST("csca-detail")
    Observable<Response<JsonObject>> getCartList(
            @Field("Key") String info,
            @Field("areaId") String areaId
    );

    /**
     * 下订单
     * @param orderInfo
     * @return
     */
    @FormUrlEncoded
    @POST("oda-save")
    Observable<Response<JsonObject>> requestOrder(
            @Field("Key") String orderInfo,
            @Field("note") String note,
            @Field("addr") String addr,
            @Field("end") String time,
            @Field("name") String name,
            @Field("tele") String phone
    );

    /**
     * 获取订单列表
     * @param begin  开始时间 选填
     * @param end  结束时间 选填
     * @param pageNum  页码 默认为1
     * @return
     */
    @FormUrlEncoded
    @POST("oda-")
    Observable<Response<JsonObject>> requestOrderList(
            @Field("begin") String begin,
            @Field("end") String end,
            @Field("pageNum") int pageNum
    );

    /**
     * 删除订单
     * @param orderId 订单id
     * @return
     */
    @FormUrlEncoded
    @POST("oda-del")
    Observable<Response<JsonObject>> deleteOrder(
            @Field("Key") String orderId
    );

    /**
     * 修改订单状态
     * @param orderId 订单号
     * @param status "1"："订单完成" "2"："订单未支付" "3"："订单已支付未发货" "4"："客户退货" "5"："客户取消订单" "6"："支付结果确认中"
     * @return
     */
    @FormUrlEncoded
    @POST("oda-modify")
    Observable<Response<JsonObject>> alertOrderStatus(
            @Field("key") String orderId,
            @Field("end") String status
    );

    /**
     * 验证支付请求
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("oda-saveOld")
    Observable<Response<JsonObject>> validateOrder(
            @Field("Key") String orderId
    );

    /**
     * 支付宝信息
     * @return
     */
    @FormUrlEncoded
    @POST("payat-asign")
    Observable<Response<JsonObject>> aliPay(
            @Field("orderNum") String orderNum,
            @Field("body") String details,
            @Field("describe") String desc
    );

    /**
     * 微信同一下单
     * @return
     */
    @FormUrlEncoded
    @POST("payat-wx")
    Observable<Response<JsonObject>> wxPay(
            @Field("describe") String describe,
            @Field("orderNum") String orderNum,
            @Field("payId") String payId
    );
    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("oda-orderDetail")
    Observable<Response<JsonObject>> getOrderDetails(
            @Field("key") String orderId
    );

    /**
     * 注销
     * @return
     */
    @POST("scca-logout")
    Observable<Response<JsonObject>> logout();


    /**
     * 意见反馈
     * @param key
     * @param content
     * @return
     */
    @FormUrlEncoded
    @POST("cia- suggest")
    Observable<Response<JsonObject>> feedBack(
            @Field("key") String key,
            @Field("text") String content
    );

    /**
     * 保存账户信息
     * @return
     */
    @FormUrlEncoded
    @POST("cia- updateInfo")
    Observable<Response<JsonObject>> saveAccountInfos(
            @Field("key") String sex,
            @Field("text") String nickname
    );

    /**
     * 检查新版本
     * @param type 1是安卓商家版，2是安卓用户版
     * @return
     */
    @FormUrlEncoded
    @POST("scca-getLatestVersion")
    Observable<Response<JsonObject>> newVersion(
            @Field("sort") int type
    );

    /**
     * 验证验证码是否正确
     * @param phone 手机号码
     * @param code 验证码
     * @return
     */
    @FormUrlEncoded
    @POST("scca-vilcode")
    Observable<Response<JsonObject>> validateCode(
            @Field("uname") String phone,
            @Field("upass") String code
    );

    /**
     * 获取商家状态和营业时间
     * @param areaId
     * @return
     */
    @FormUrlEncoded
    @POST("scca-getTradeState")
    Observable<Response<JsonObject>> getShopStatus(
            @Field("areaId") String areaId
    );
}
