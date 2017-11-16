package com.tsinova.bike.listener;

import android.app.Dialog;

import com.tsinova.bluetoothandroid.pojo.BlueToothResponseInfo;

public interface OnBikeCallback {

	/**
	 * 扫描蓝牙完毕
	 * @param isFound 是否发现可以连接的固件
	 */
	void onLeScanEnd(boolean isFound);

	void onDisconnected();

	void onConnected();

	/**
	 * new 手动断开回调
	 */
	void onDisconnectedByHand();

	/**
	 * 连接超时
	 */
	void onConnectTimeOut();

	void onDataAvailable(BlueToothResponseInfo data);


	/**
	 * 检查固件完毕
	 * @param needUpdate 是否需要升级固件
	 */
	void onCheckFinish(boolean needUpdate);

	/**
	 * 检查固件出错
	 */
	void onCheckError();
	/**
	 * 升级固件完毕
	 */
	void onUpdateFinish(boolean isSuccess);

	/**
	 * 显示重新连接蓝牙dialog
	 */
	void onShouwReconnectionDialog(Dialog dialog);

	/**
	 * 点击了重新连接蓝牙
	 */
	void onDoClickReconnection();

	/**
	 * 点击了取消重新连接蓝牙
	 */
	void onCancelReconnection();
}
