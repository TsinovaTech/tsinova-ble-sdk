package com.tsinova.bluetoothandroid.pojo;

import android.content.Context;

import com.tsinova.bluetoothandroid.util.CommonUtils;

/**
 * Created by xucong on 17/11/9.
 */

public enum SingletonBTInfo {

    INSTANCE;

    public String getBikeBluetoothNumber() {
        CommonUtils.log(bikeBluetoothNumber);
        return bikeBluetoothNumber;
    }

    public void setBikeBluetoothNumber(String bikeBluetoothNumber) {
        this.bikeBluetoothNumber = bikeBluetoothNumber;
    }

    public String getPageName() {
        CommonUtils.log(pageName);
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Context getApplicationContext() {
        CommonUtils.log(applicationContext.toString());
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    private String bikeBluetoothNumber;
    private String pageName;
    private Context applicationContext;

}
