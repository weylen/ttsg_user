package com.guaigou.cd.minutestohome.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by weylen on 2016-08-14.
 */
public class OrderDetailsEntity implements Serializable{

    private String orderId;
    private String total;
    private ArrayList<OrderDetailsProductsEntity> products;

    public OrderDetailsEntity(){}

    public OrderDetailsEntity(String orderId, String total, ArrayList<OrderDetailsProductsEntity> products) {
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

    public ArrayList<OrderDetailsProductsEntity> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OrderDetailsProductsEntity> products) {
        this.products = products;
    }

}
