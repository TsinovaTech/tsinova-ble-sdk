package com.tsinova.bluetoothandroid.pojo;

import android.content.Context;


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
    private String cookies;
    private boolean isEncryption = true;

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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
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

    public String getBikeNo() {
        return bikeNo;
    }

    public void setBikeNo(String bikeNo) {
        this.bikeNo = bikeNo;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public boolean isEncryption() {
        return isEncryption;
    }

    public void setEncryption(boolean encryption) {
        isEncryption = encryption;
    }
}
