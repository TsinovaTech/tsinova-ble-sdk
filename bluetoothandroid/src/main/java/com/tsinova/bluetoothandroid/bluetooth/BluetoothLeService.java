/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tsinova.bluetoothandroid.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import com.tsinova.bluetoothandroid.exception.TsinovaApplicationNotFoundException;
import com.tsinova.bluetoothandroid.exception.TsinovaBikeBtNumberNotFoundException;
import com.tsinova.bluetoothandroid.exception.TsinovaPackNameNotFoundException;
import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;
import com.tsinova.bluetoothandroid.util.CommonUtils;
import com.tsinova.bike.util.DESPlus;
import com.tsinova.bluetoothandroid.util.StringUtils;

import java.util.List;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {
    public final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private BikeBlueToothManager.OnGattWriteLisener mOnGattWriteLisener;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            SingletonBTInfo.INSTANCE.getPageName() + ".le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            SingletonBTInfo.INSTANCE.getPageName() + ".le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            SingletonBTInfo.INSTANCE.getPageName() + ".le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            SingletonBTInfo.INSTANCE.getPageName() + ".le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_WRITE_CALLBAK =
            SingletonBTInfo.INSTANCE.getPageName() + ".le.ACTION_DATA_WRITE_CALLBAK";
    public final static String EXTRA_DATA = SingletonBTInfo.INSTANCE.getPageName() + ".le.EXTRA_DATA";
    public final static String UPDATE_FIRMWARE = ".le.UPDATE_FIRMWARE";

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered  status : " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead  status : " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged ------->  : " + characteristic.getUuid());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String result = new String(characteristic.getValue());
            if (mOnGattWriteLisener != null) {
                mOnGattWriteLisener.onCharacteristicWrite(result, false);
            }
        }

        ;
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    public byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        if (byte_1 == null && byte_2 == null) {
            return null;
        }
        if (byte_1 == null && byte_2 != null && byte_2.length > 0) {
            return byte_2;
        }
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

//    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic){
//    	broadcastUpdate(action, characteristic, false);
//    }

    private boolean isUpdateFirmware;
    private int charChangeNum;
    private byte[] byteBuffer;
    private StringBuffer stringBuffer = new StringBuffer();
//    private SingletonBTInfo mSingletonBTInfo;

    public void setUpdateFirmwareFinish() {
        isUpdateFirmware = false;
    }

    /**
     * 用于监听蓝牙返回数据
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        if (!BikeBlueToothUtils.isBikeCharNotifyProperties(characteristic.getUuid())) {
            return;
        }
        final Intent intent = new Intent(action);
        intent.putExtra(UPDATE_FIRMWARE, isUpdateFirmware);
        if (BikeBlueToothUtils.isBikeCharNotifyProperties(characteristic.getUuid())) {
            try {
                if (this.isUpdateFirmware) {// 固件升级不加密


                    byte[] value ;
                    if (SingletonBTInfo.INSTANCE.isEncryption()){
                        String key = StringUtils.getBikeKey(SingletonBTInfo.INSTANCE.getBikeBluetoothNumber());
                        value = DESPlus.getInstant().decryptDES(characteristic.getValue(), (key));
                    }else {
                        value = characteristic.getValue();
                    }


//
//                    byte[] value = Des.decryptDES(characteristic.getValue(), (key));

                    String data = new String(value);
                    CommonUtils.log("broadcastUpdate isUpdateFirmware----> data : " + data);
                    if (mOnGattWriteLisener != null) {
                        mOnGattWriteLisener.onCharacteristicWrite(data, true);
                    } else {
                        intent.putExtra(EXTRA_DATA, data);
                        sendBroadcast(intent);
                    }
                } else { // 行驶状态加密
                    byte[] data = characteristic.getValue();


                    if (data != null && data.length > 0) {
                        String v = new String(data);

                        byte[] value;
                        if (SingletonBTInfo.INSTANCE.isEncryption()){
                            String key = StringUtils.getBikeKey(SingletonBTInfo.INSTANCE.getBikeBluetoothNumber());
                            value = DESPlus.getInstant().decryptDES(data, (key));
                        }else {
                            value = data;
                        }


//
//                        byte[] value = Des.decryptDES(data, (key));


                        String sValue = new String(value);
                        CommonUtils.log(TAG, "broadcastUpdate ----> sValue : " + sValue + " / data.length : " + data.length);
                        if (sValue.startsWith("{\"da\"")) {
                            charChangeNum = 0;
                            byteBuffer = null;
                            stringBuffer = null;
                            stringBuffer = new StringBuffer();
                        }
                        byteBuffer = byteMerger(byteBuffer, data);
                        stringBuffer.append(sValue);
                        charChangeNum++;
                    }

                    if (byteBuffer != null && byteBuffer.length > 0) {

                        byte[] result;
                        if (SingletonBTInfo.INSTANCE.isEncryption()){
                            String key = StringUtils.getBikeKey(SingletonBTInfo.INSTANCE.getBikeBluetoothNumber());
                            result = DESPlus.getInstant().decryptDES((byteBuffer), (key));
                        }else {
                            result = byteBuffer;
                        }


//
//                        byte[] result = Des.decryptDES(byteBuffer, (key));

                        String sResult = new String(result);
                        if (StringUtils.isJson(sResult)) {
                            CommonUtils.log(TAG, "characteristic.byteBuffer.length ： " + byteBuffer.length);
                            CommonUtils.log(TAG, "characteristic.result.length ： " + sResult.length());
                            CommonUtils.log(TAG, "characteristic.result ： " + sResult);
                            CommonUtils.log(TAG, "---------------------------------");
                            sResult = StringUtils.fromateJson(sResult);
                            intent.putExtra(EXTRA_DATA, sResult);
                            sendBroadcast(intent);
                            byteBuffer = null;
                            charChangeNum = 0;
                            stringBuffer = null;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        if (SingletonBTInfo.INSTANCE.getApplicationContext() == null) {
            throw new TsinovaApplicationNotFoundException("call SingletionBTInfo.setApplicationContext() method first");
        }

        if (SingletonBTInfo.INSTANCE.getApplicationContext() == null) {
            throw new TsinovaPackNameNotFoundException("call SingletionBTInfo.setPageName() method first");
        }

        if (SingletonBTInfo.INSTANCE.getApplicationContext() == null) {
            throw new TsinovaBikeBtNumberNotFoundException("call SingletionBTInfo.setBikeBluetoothNumber() method first");
        }



        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }

        try {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        } catch (Exception e) {

        }

    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
//    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//        mBluetoothGatt.readCharacteristic(characteristic);
//    }
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, boolean isUpdateFirmware) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        this.isUpdateFirmware = isUpdateFirmware;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 升级固件专用
     */
    public boolean updateFirmware(BluetoothGattCharacteristic characteristic, BikeBlueToothManager.OnGattWriteLisener lisener) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        isUpdateFirmware = true;
        mOnGattWriteLisener = lisener;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void setOnGattWriteLisener(BikeBlueToothManager.OnGattWriteLisener lisener) {
        this.mOnGattWriteLisener = lisener;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (BikeBlueToothUtils.isBikeCharNotifyProperties(characteristic.getUuid())) {
            if (characteristic.getDescriptors() != null && characteristic.getDescriptors().size() > 0) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
