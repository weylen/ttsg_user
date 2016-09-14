package com.guaigou.cd.minutestohome.activity.myorders;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.guaigou.cd.minutestohome.BasePresenter;
import com.guaigou.cd.minutestohome.entity.OrderEntity;
import com.guaigou.cd.minutestohome.entity.OrderProductsEntity;
import com.guaigou.cd.minutestohome.http.Constants;
import com.guaigou.cd.minutestohome.http.HttpService;
import com.guaigou.cd.minutestohome.http.RespSubscribe;
import com.guaigou.cd.minutestohome.http.ResponseMgr;
import com.guaigou.cd.minutestohome.http.RetrofitFactory;
import com.guaigou.cd.minutestohome.util.CartUtil;
import com.guaigou.cd.minutestohome.util.DebugUtil;
import com.guaigou.cd.minutestohome.util.LocaleUtil;
import com.guaigou.cd.minutestohome.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by weylen on 2016-07-23.
 */
public class OrderPresenter{

    private OrderView orderView;
    public OrderPresenter(OrderView orderView){
        this.orderView = Preconditions.checkNotNull(orderView, "OrderView can not be null");
    }

    // 刷新事件
    void onRefresh(){
        OrderData.INSTANCE.reset();
        remote(OrderData.INSTANCE.pageNum);
    }

    // 加载更多
    void onLoadmore(){
        remote(OrderData.INSTANCE.pageNum + 1);
    }

    void requestOrder(){
        OrderData.INSTANCE.reset();
        orderView.onStartRequest();
        remote(OrderData.INSTANCE.pageNum);
    }

    private void remote(int pageNum){
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .requestOrderList(Constants.EMPTY_STRING, Constants.EMPTY_STRING, pageNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("OrderPresenter 订单列表获取失败：" + e.getMessage());
                        error(pageNum);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("OrderPresenter 订单列表结果：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            error(pageNum);
                        }else {
                            map(jsonObject);
                        }
                    }
                }));
    }

    private void error(int pageNum){
        if (pageNum > 1){
            orderView.onLoadMoreFailure();
        }else {
            orderView.onRequestFailure();
        }
    }

    private void map(JsonObject jsonObject){
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        int maxPage = jsonObject.get("maxPage").getAsInt();
        int currentPage = jsonObject.get("pageNum").getAsInt();
        Gson gson = new Gson();

        List<OrderEntity> listOrders = new ArrayList<>();
        JsonArray dataArray = dataObject.get("data").getAsJsonArray();
        int size = dataArray.size();
        for (int i = 0; i < size; i++){
            JsonObject item = dataArray.get(i).getAsJsonObject();
            String orderId = item.get("orderId").getAsString();
            String total = item.get("total").getAsString();
            String prepay_id = item.get("prepay_id").getAsString();
            ArrayList<OrderProductsEntity> products = gson.fromJson(item.get("products").getAsJsonArray(),
                    new TypeToken<ArrayList<OrderProductsEntity>>(){}.getType());

            OrderEntity orderEntity = new OrderEntity(orderId, total, prepay_id, products);
            listOrders.add(orderEntity);
        }

        // 图片JsonObject
        /**
         * 暂时应该不需要图片 先不用解析
        JsonObject imgObject = dataObject.get("img").getAsJsonObject();
        for (OrderEntity entity : listOrders){
            List<OrderEntity.ProductsEntity> productsEntities = entity.getProducts();
            for (OrderEntity.ProductsEntity productsEntity : productsEntities){
                productsEntity.setImg(imgObject.get(productsEntity.getImg()).getAsString());
            }
        }
         **/

        boolean isFinish = maxPage == currentPage;
        // 记录当前的页码
        OrderData.INSTANCE.pageNum = currentPage;
        OrderData.INSTANCE.isLoadComplete = isFinish;
        if (currentPage == 1){ // 刷新或者首次加载
            orderView.onRequestSuccess(listOrders, isFinish);
        }else{ // 加载更多
            orderView.onLoadMoreSuccess(listOrders, isFinish);
        }
    }

    void cancelOrder(int position, String orderId){
        orderView.onStartCancelOrder();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .alertOrderStatus(orderId, "5")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("OrderPresenter 修改订单失败：" + e.getMessage());
                        orderView.onCancelOrderFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("OrderPresenter 修改订单成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            orderView.onCancelOrderSuccess(position);
                        }else {
                            orderView.onCancelOrderFailure();
                        }
                    }
                }));
    }

    void validatePayment(int position, String orderId){
        orderView.onStartValidateOrder();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .validateOrder(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("OrderPresenter 验证支付订单失败：" + e.getMessage());
                        orderView.onValidateOrderFailure("");
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("OrderPresenter验证支付订单成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            orderView.oNValidateOrderSuccess(position);
                        }else {
                            orderView.onValidateOrderFailure(jsonObject.get("data").getAsString());
                        }
                    }
                }));
    }

    void deleteOrder(int position, String orderId){
        orderView.onStartDeleteOrder();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .deleteOrder(orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        DebugUtil.d("OrderPresenter 验证支付订单失败：" + e.getMessage());
                        orderView.onDeleteOrderFailure();
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        DebugUtil.d("OrderPresenter验证支付订单成功：" + jsonObject);
                        if (ResponseMgr.getStatus(jsonObject) == 1){
                            orderView.onDeleteOrderSuccess(position);
                        }else {
                            orderView.onDeleteOrderFailure();
                        }
                    }
                }));
    }

    /**
     * 修改订单状态
     * @param orderId
     * @param status "1"："订单完成" "2"："订单未支付" "3"："订单已支付未发货" "4"："客户退货" "5"："客户取消订单" "6"："支付结果确认中" "7"："商家已结单" "6"："商家已送达"
     */
    void alertOrderStatus(String orderId, int status, int position){
        orderView.onStartAlertStatus();
        RetrofitFactory.getRetrofit().create(HttpService.class)
                .alertOrderStatus(orderId, String.valueOf(status))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new RespSubscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        orderView.onAlertStatusFailure(position, status);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (ResponseMgr.getStatus(jsonObject) != 1){
                            orderView.onAlertStatusFailure(position, status);
                        }else{
                            orderView.onAlertStatusSuccess(position, status);
                        }
                    }
                }));
    }
}
