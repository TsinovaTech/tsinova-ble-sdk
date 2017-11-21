package com.tsinova.bluetoothandroid.pojo;

import android.content.Context;

import com.tsinova.bluetoothandroid.util.CommonUtils;

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
        CommonUtils.log(bikeBluetoothNumber);
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
        CommonUtils.log(packageName);
        return packageName;
    }

    public void setPackageName(String packgaeName) {
        this.packageName = packageName;
    }

    public Context getApplicationContext() {
        CommonUtils.log(applicationContext.toString());
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }



}
