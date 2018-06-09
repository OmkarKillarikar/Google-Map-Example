package com.example.okmac.greymap.fragments;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "geo_tag_table")
public class GeoTag {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer tagId;
    private Double latitude;
    private Double longitude;
    private String address;


    @NonNull
    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(@NonNull Integer tagId) {
        this.tagId = tagId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}