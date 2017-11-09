package com.tsinova.bluetoothandroid.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter.LeScanCallback;

@SuppressLint("NewApi")
public interface BikeLeScanCallback extends LeScanCallback {

	public void onLeScanStart();

	public void onLeScanEnd();
}
