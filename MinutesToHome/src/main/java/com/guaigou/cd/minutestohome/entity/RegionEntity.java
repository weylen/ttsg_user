package com.guaigou.cd.minutestohome.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by weylen on 2016-07-22.
 * 区域数据Entity
 */
public class RegionEntity implements Parcelable{
    private String id;
    private String name;
    private String pid;

    public RegionEntity() {
    }

    public RegionEntity(String id, String name, String pid) {
        this.id = id;
        this.name = name;
        this.pid = pid;
    }

    protected RegionEntity(Parcel in) {
        id = in.readString();
        name = in.readString();
        pid = in.readString();
    }

    public static final Creator<RegionEntity> CREATOR = new Creator<RegionEntity>() {
        @Override
        public RegionEntity createFromParcel(Parcel in) {
            return new RegionEntity(in);
        }

        @Override
        public RegionEntity[] newArray(int size) {
            return new RegionEntity[size];
        }
    };

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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(pid);
    }
}
