package com.guaigou.cd.minutestohome.entity;

import java.util.List;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderEntity {

    private String orderId;
    private String total;
    private List<OrderProductsEntity> products;

    public OrderEntity(){}

    public OrderEntity(String orderId, String total, List<OrderProductsEntity> products) {
        this.orderId = orderId;
        this.total = total;
        this.products = products;
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

    public List<OrderProductsEntity> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductsEntity> products) {
        this.products = products;
    }
}
