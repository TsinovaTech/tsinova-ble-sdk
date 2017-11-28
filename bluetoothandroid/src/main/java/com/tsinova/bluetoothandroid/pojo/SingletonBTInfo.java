package com.tsinova.bluetoothandroid.pojo;

import android.content.Context;

import com.tsinova.bluetoothandroid.util.CommonUtils;

import java.util.List;

import okhttp3.Cookie;

/**
 * Created by xucong on 17/11/9.
 */

public enum SingletonBTInfo {

    INSTANCE;
    private String bikeBluetoothNumber;
    private String bikeBluetoothaddress;
    private String packageName;
    private Context applicationContext;
    private String bikeImageUrl;
    private String bikeBrandImage;
    private String bikeNo;


    public String getBikeNo() {
        return bikeNo;
    }

    public void setBikeNo(String bikeNo) {
        this.bikeNo = bikeNo;
    }

    private List<Cookie> cookies;

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public String getBikeImageUrl() {
        return bikeImageUrl;
    }

    public void setBikeImageUrl(String bikeImageUrl) {
        this.bikeImageUrl = bikeImageUrl;
    }

    public String getBikeBrandImage() {
        return bikeBrandImage;
    }

    public void setBikeBrandImage(String bikeBrandImage) {
        this.bikeBrandImage = bikeBrandImage;
    }

    private boolean isEncryption = true;

    public boolean isEncryption() {
        return isEncryption;
    }

    public void setEncryption(boolean encryption) {
        isEncryption = encryption;
    }

    public String getBikeBluetoothNumber() {
        return bikeBluetoothNumber;
    }

    public void setBikeBluetoothNumber(String bikeBluetoothNumber) {
        this.bikeBluetoothNumber = bikeBluetoothNumber;
    }

    public String getBikeBluetoothaddress() {
        return bikeBluetoothaddress;
    }

    public void setBikeBluetoothaddress(String bikeBluetoothaddress) {
        this.bikeBluetoothaddress = bikeBluetoothaddress;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packgaeName) {
        this.packageName = packageName;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }



}
