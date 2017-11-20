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
    private String packgeName;
    private Context applicationContext;
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

    public String getPackgeName() {
        CommonUtils.log(packgeName);
        return packgeName;
    }

    public void setPackgeNam(String packgeName) {
        this.packgeName = packgeName;
    }

    public Context getApplicationContext() {
        CommonUtils.log(applicationContext.toString());
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }



}
