package com.tsinova.bluetoothandroid.bluetooth;

/**
 * 单车蓝牙连接监听
 * Created by ihgoo on 2017/11/14.
 */
public interface OnBikeBTListener {

    /**
     * 连接单车中
     */
    void biekConnecting();


    /**
     * 连接成功
     */
    void connectSuccess();


    /**
     * 连接失败
     */
    void connectFailure();


    /**
     * 通讯失败
     */
    void communicationFailure();

}
