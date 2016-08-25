package com.guaigou.cd.minutestohome.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderEntity implements Serializable{

    private String orderId;
    private String total;
    private String prepay_id;
    private ArrayList<OrderProductsEntity> products;

    public OrderEntity(){}

    public OrderEntity(String orderId, String total, String prepay_id, ArrayList<OrderProductsEntity> products) {
        this.orderId = orderId;
        this.total = total;
        this.prepay_id = prepay_id;
        this.products = products;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<OrderProductsEntity> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OrderProductsEntity> products) {
        this.products = products;
    }

}
