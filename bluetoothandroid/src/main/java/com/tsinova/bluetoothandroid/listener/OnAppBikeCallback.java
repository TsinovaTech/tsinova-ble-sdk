package com.tsinova.bluetoothandroid.listener;

import com.tsinova.bluetoothandroid.pojo.BlueToothResponseInfo;

/**
 * Created by xucong on 17/11/16.
 */

public interface OnAppBikeCallback {
    /**
     * 扫描蓝牙完毕
     * @param isFound 是否发现可以连接的固件
     */
    void onAppLeScanEnd(boolean isFound);

    void onAppDisconnected();

    void onAppConnected();


    /**
     * 连接超时
     */
    void onAppConnectTimeOut();

    void onAppDataAvailable(BlueToothResponseInfo data,String json);

    void onAppDisconnect();

    void onAppEndDriving();
}
