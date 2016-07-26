package com.guaigou.cd.minutestohome.entity;

/**
 * Created by weylen on 2016-07-23.
 */
public class ProductEntity {
    private String id;
    private String name;
    /**
     * 规格
     */
    private String standard;
    /**
     * 种类
     */
    private String kind;
    private String img;
    private String price;
    /**
     * 库存
     */
    private String reserve;
    /**
     * 促销价格
     */
    private String promote;
    /**
     * 促销开始时间
     */
    private String begin;
    /**
     * 促销结束时间
     */
    private String end;
    /**
     * 促销信息
     */
    private String info;

    /**
     * 购买数量
     */
    private int number;

    public ProductEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getPromote() {
        return promote;
    }

    public void setPromote(String promote) {
        this.promote = promote;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
