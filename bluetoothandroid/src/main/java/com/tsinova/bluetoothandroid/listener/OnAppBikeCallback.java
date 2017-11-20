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


    /**
     * 第一次启动，或者退出再登录的时候调用
     */
    void onAppBindServiceAndConnectBLE();

    void onAppBindServiceAndConnectBLEBikeFragment();

    /**
     * 开始骑行
     */
   void onAppStartDriving();


    /**
     * 结束骑行
     * --快速启动模式初始档位是档2，在快速启动模式下结束骑行还回到档2
     */
    public abstract void onAppEndDriving();


    /**
     * 打开或者关闭车灯
     * @param open
     */
    public abstract void onAppOpenLight(boolean open);

    /**
     * 更改骑行模式(设置中调用，需要后期修改)
     * @param md
     */
    public abstract void onAppSetMDToBike(int md);


    /**
     * 更改档位
     */
    public abstract void onAppShiftedGears(int shift);


    /**
     * 是否已经连接
     * @return
     */
    public abstract boolean onAppIsConnect();

    /**
     *
     */
    public abstract void onAppRelease();









}
