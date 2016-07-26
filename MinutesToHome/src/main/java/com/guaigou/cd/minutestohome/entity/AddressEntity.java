package com.guaigou.cd.minutestohome.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-06-18.
 */
public class AddressEntity implements Serializable{

    private String contacts; // 联系人
    private String mobilePhone; // 电话
    private String community; // 小区
    private String detailsAddress; // 详细地址
    private boolean isDefaultAddress;

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getDetailsAddress() {
        return detailsAddress;
    }

    public void setDetailsAddress(String detailsAddress) {
        this.detailsAddress = detailsAddress;
    }

    public boolean isDefaultAddress() {
        return isDefaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        isDefaultAddress = defaultAddress;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "contacts='" + contacts + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", community='" + community + '\'' +
                ", detailsAddress='" + detailsAddress + '\'' +
                ", isDefaultAddress=" + isDefaultAddress +
                '}';
    }
}
