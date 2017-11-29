package com.tsinova.bluetoothandroid.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tsinova.bluetoothandroid.adapter.TimestampTypeAdapter;
import com.tsinova.bluetoothandroid.network.HttpRequest;
import com.tsinova.bluetoothandroid.pojo.BlueToothRequstInfo;
import com.tsinova.bluetoothandroid.pojo.BlueToothResponseInfo;
import com.tsinova.bluetoothandroid.pojo.RequestBikeCode;
import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;
import com.tsinova.bluetoothandroid.util.CommonUtils;
import com.tsinova.bike.util.DESPlus;
import com.tsinova.bluetoothandroid.util.StringUtils;
import com.tsinova.bluetoothandroid.util.UIUtils;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("NewApi")
public class BikeBlueToothManager {
    private final static String TAG = BikeBlueToothManager.class.getSimpleName();
    private static BikeBlueToothManager mInstant;
    private Context mContext;
    /**
     * 搜索BLE终端
     */
    private BluetoothAdapter mBluetoothAdapter;
    private Activity mMainActivity; // 绑定service的activity
    /**
     * 读写BLE终端
     */
    private BluetoothLeService mBluetoothLeService;
    private Handler mHandler;
    private boolean mScanning;

    private static final long SCAN_PERIOD = 10 * 1000; // Stops scanning after 20 seconds.
    private static final long CONNECT_TIMEOUT = 5 * 1000; // 蓝牙连接超时

    private BikeLeScanCallback mLeScanCallback;// Device scan callback.
    private String mDeviceAddress;
    private boolean mConnected = false;

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private BluetoothGattService mGattService;

    private BluetoothDevice mCurrentDevice = null;

    private OnGattNotifyLisener mOnGattNotifyLisener;

    public static final String R = "R"; //
    public static final String K = "K";
    public static final String F = "F";


    private OnBikeBTListener mOnBTListener;

    /**
     * 记录开始搜索蓝牙时间
     */
    public static long startTime;


    public void setOnBikeBTListenerLisener(OnBikeBTListener lisener) {
        this.mOnBTListener = lisener;
    }

    public void setOnGattNotifyLisener(OnGattNotifyLisener lisener) {
        this.mOnGattNotifyLisener = lisener;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mMainActivity.finish();
                return;
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            CommonUtils.log("BikeBlueToothManager ---> onReceive ,action :" + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 蓝牙连接成功
                if (!mConnected) {
                    long connectTime = System.currentTimeMillis(); // 用于记录连接成功的时间
                    CommonUtils.log("*****************蓝牙连接时间：" + (connectTime - startTime) / 1000.0 + "秒");
                    mOnGattNotifyLisener.onConnected();
                    mScanning = false;
                    if (mOnBTListener != null) {


//						eventBus.post(new ConnedEvent());
                        mOnBTListener.connectSuccess();
                    }


                } else {
                    CommonUtils.log("mConnected is true!!!!");
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mOnGattNotifyLisener.onDisconnected();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                if (mBluetoothLeService != null && mBluetoothLeService.getSupportedGattServices() != null) {
                    displayGattServices(mBluetoothLeService.getSupportedGattServices());
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                BlueToothResponseInfo info = null;
                String data = "";
                boolean isUpdateFirmware = false;
                try {
                    if (intent != null) {
                        data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                        isUpdateFirmware = intent.getBooleanExtra(BluetoothLeService.UPDATE_FIRMWARE, false);
                        if (isUpdateFirmware && mOnGattWriteLisener != null) {
                            mOnGattWriteLisener.onCharacteristicWrite(data, true);
                        } else if (mOnGattNotifyLisener != null) {
                            info = gson.fromJson(data, new TypeToken<BlueToothResponseInfo>() {
                            }.getType());
                            mOnGattNotifyLisener.onDataAvailable(info, data);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

            } else if (BluetoothLeService.ACTION_DATA_WRITE_CALLBAK.equals(action)) {

            }
        }
    };


    public boolean isConnect() {
        return mConnected;
    }

    public void setConnect(boolean connect) {
        this.mConnected = connect;
    }

    private Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();//GSON 解析

    private BikeBlueToothManager(Context context) {
        this.mContext = context;
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            UIUtils.toastFalse(mContext, com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_toast_tips_not_ble);
            return;
        }
        init();
    }

    public synchronized static BikeBlueToothManager getInstant(Activity context) {
        if (mInstant == null) {
            mInstant = new BikeBlueToothManager(context);
        }
        return mInstant;
    }

    public void setMDToBike(BlueToothRequstInfo info, int md) {
        if (info != null) {
            info.setMd(String.valueOf(md));
            writeDataToBike(info, false, false);
        } else if (mMainActivity != null) {
//			mMainActivity.setMDToBike(md);
        }
    }

    public void bindService(Activity activity) {
        mMainActivity = activity;
        Intent gattServiceIntent = new Intent(mMainActivity, BluetoothLeService.class);
        mMainActivity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void connect(Activity activity, String deviceAddress, OnGattNotifyLisener lisener) {
        connect(activity, deviceAddress, lisener, false);
    }


    public void connect(final Activity activity, String deviceAddress, OnGattNotifyLisener lisener, final boolean isAuto) {
        if (mMainActivity == null) {
            return;
        }


        startTime = System.currentTimeMillis();
        CommonUtils.log("BLE databaseManager -----> connect :" + deviceAddress);
        this.mOnGattNotifyLisener = lisener;
        this.mDeviceAddress = deviceAddress;
        mMainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(BluetoothLeService.TAG, "Connect request result = " + result);


            if (mOnBTListener != null) {
                mOnBTListener.connectFailure();
            }

            mHandler.postDelayed(new Runnable() { // 设置蓝牙连接超时
                @Override
                public void run() {
                    if (!isConnect()) {


                        if (!isAuto) { // 非自动连接时，toast提示
//                			UIUtils.toastFalse(activity, com.tsinova.bike.R.string.bltmanager_toast_tips_ble_connect_fail);
                            if (mOnBTListener != null) {
                                mOnBTListener.connectFailure();
                            }



                        }
                        try {
                            mMainActivity.unregisterReceiver(mGattUpdateReceiver);
                        } catch (IllegalArgumentException exception) {

                        }
                        disconnect();
                        setConnect(false);
                        if (mOnGattNotifyLisener != null) {
                            mOnGattNotifyLisener.onConnectTimeOut();
                        }
                    }
                }
            }, CONNECT_TIMEOUT);
        }
    }


    private void requestBikeCodeTolerant(String errorNO) {
        RequestBikeCode requestBikeCode = new RequestBikeCode();
        requestBikeCode.setApp(SingletonBTInfo.INSTANCE.getPackageName());
        requestBikeCode.setBike_no(SingletonBTInfo.INSTANCE.getBikeNo());
        requestBikeCode.setError(errorNO);
        Gson gson = new Gson();
        String json = gson.toJson(requestBikeCode);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.post("https//api.tsinova.com/app/bike_codes/tolerant", json);
    }


    public void setNotifycation(boolean enabled) {
        if (mNotifyCharacteristic != null) {
            final int charaProp = mNotifyCharacteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 && mBluetoothLeService != null) {
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, !enabled);
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, enabled);
                }
            }
        }
    }

    private void setNotification(final BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//					mBluetoothLeService.setCharacteristicNotification(characteristic, false); // 为了防止设置监听失败先进行一次 false操作

                        if (mBluetoothLeService != null) {
                            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                        }


                    }
                }, 200);
            }
        }
    }

    private static final int BLE_DEFULT_WRITE_BYTES = 20;

    /**
     * 用于蓝牙通信
     *
     * @param value
     * @param isUpdateFirmware // 是否为升级固件
     */
    public void writeCharacteristic(String value, boolean isUpdateFirmware) {
        if (mWriteCharacteristic == null) {
            return;
        }
        final int charaProp = mWriteCharacteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
//        	if (mNotifyCharacteristic != null) {
//                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
//            }
            try {
                CommonUtils.log("writeCharacteristic", "------------------------------------------------------");

                byte[] data;
                if (SingletonBTInfo.INSTANCE.isEncryption()) {
                    String key = SingletonBTInfo.INSTANCE.getBikeBluetoothNumber();
                    data = DESPlus.getInstant().encryptDES(value, StringUtils.getBikeKey(key));//对请求进行加密
                } else {
                    data = value.getBytes();
                }


                CommonUtils.log("writeCharacteristic", "value :" + value);
                int times_b = (data.length / BLE_DEFULT_WRITE_BYTES);
                int t_b = (data.length % BLE_DEFULT_WRITE_BYTES);
                if (t_b > 0) {
                    times_b++;
                }
                CommonUtils.log("writeCharacteristic", "------------------------------------------------------");
                CommonUtils.log("writeCharacteristic", "times_b : " + times_b);
                CommonUtils.log("writeCharacteristic", "value.length() : " + value.length());
                CommonUtils.log("writeCharacteristic", "****************");

                for (int i = 0; i < times_b; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] b;
                    if (i == times_b - 1) {
                        b = subBytes(data, i * BLE_DEFULT_WRITE_BYTES, data.length - i * BLE_DEFULT_WRITE_BYTES);
                    } else {
                        b = subBytes(data, i * BLE_DEFULT_WRITE_BYTES, BLE_DEFULT_WRITE_BYTES);
                    }
                    mWriteCharacteristic.setValue(b);
                    mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    boolean status = mBluetoothLeService.writeCharacteristic(mWriteCharacteristic, isUpdateFirmware);
                    CommonUtils.log("writeCharacteristic", "writeCharacteristic -----> " + new String(b) + " / length : " + b.length + " / status : " + status);
                }
                CommonUtils.log("writeCharacteristic", "****************");
            } catch (Exception e) {
                e.printStackTrace();
            }
//        	if(mNotifyCharacteristic != null) {
//                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic,true);
//            }
        }
    }

    /**
     * 升级固件专用
     */
    public void writeCharacteristic(String value, OnGattWriteLisener lisener) {
        if (mWriteCharacteristic == null) {
            return;
        }
        final int charaProp = mWriteCharacteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            try {
//				CommonUtils.log("writeCharacteristic", "------------------------------------------------------");
//				int times_s = (value.length() / BLE_DEFULT_WRITE_BYTES);
//				int t_s = (value.length() % BLE_DEFULT_WRITE_BYTES);
//				if(t_s > 0){
//					times_s ++;
//				}
//				CommonUtils.log("writeCharacteristic", "times_s : " + times_s);
//				CommonUtils.log("writeCharacteristic", "t_s : " + t_s);
//				CommonUtils.log("writeCharacteristic", "value.length() : " + value.length());
//				CommonUtils.log("writeCharacteristic", "------------------------------------------------------");
//				
//				String[] strs  = new String[times_s];// 不加密
//				OnGattWriteLisener l = null;
//				for (int i = 0; i < times_s; i++) {
//					try {
//	            	    Thread.sleep(50);
//	            	} catch (InterruptedException e) {
//	            	    e.printStackTrace();
//	           		}
//					if(i == times_s - 1){
//						l = lisener;
//						strs[i] = value.substring(i * BLE_DEFULT_WRITE_BYTES , value.length());
//					} else  {
//						l = mOnGattWriteLisener;
//						strs[i] = value.substring(i * BLE_DEFULT_WRITE_BYTES , i * BLE_DEFULT_WRITE_BYTES + BLE_DEFULT_WRITE_BYTES);
//					}
//					mWriteCharacteristic.setValue(strs[i]);
                mWriteCharacteristic.setValue(value);
                mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mBluetoothLeService.updateFirmware(mWriteCharacteristic, lisener);
//					CommonUtils.log("writeCharacteristic -----> strs[i] : " + strs[i] + " / length : " + strs[i].length());
//				}
//				CommonUtils.log("writeCharacteristic", "****************");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private OnGattWriteLisener mUpdateLisener;
    private int position_s = 0; // 上传固件的分段计数
    private String value_s;
    private int times_s;
    private String firmware;
    /**
     * 用于分段数据发送的回调接收
     */
    private OnGattWriteLisener mOnGattWriteLisener = new OnGattWriteLisener() {
        @Override
        public void onCharacteristicWrite(String result, boolean isFinish) {
            position_s++;
            value_s = getFirmwareSubString(position_s, times_s, firmware);
            if (position_s == times_s - 1) {
                writeCharacteristic(value_s, mUpdateLisener); // 当每组数据发送后回调一次
            } else {
                writeCharacteristic(value_s, mOnGattWriteLisener);// 当每组中的某一段数据发送后回调一次(20字节)
            }

        }
    };

    public void updateFirmware(String value, OnGattWriteLisener lisener) {
        mUpdateLisener = lisener;
        firmware = value;
        times_s = (value.length() / BLE_DEFULT_WRITE_BYTES);
        int t_s = (value.length() % BLE_DEFULT_WRITE_BYTES);
        if (t_s > 0) {
            times_s++;
        }
        position_s = 0;

        value_s = getFirmwareSubString(position_s, times_s, firmware);

        if (position_s == times_s - 1) {
            writeCharacteristic(value_s, mUpdateLisener);
        } else {
            writeCharacteristic(value_s, mOnGattWriteLisener);
        }

    }

    private String getFirmwareSubString(int position, int times_s, String value) {
        if (TextUtils.isEmpty(value) || position >= times_s) {
            return null;
        }
        if (position == times_s - 1) {
            return value.substring(position * BLE_DEFULT_WRITE_BYTES, value.length());
        } else {
            return value.substring(position * BLE_DEFULT_WRITE_BYTES, position * BLE_DEFULT_WRITE_BYTES + BLE_DEFULT_WRITE_BYTES);
        }
    }

    public void disconnect() {
        CommonUtils.log("BikeBlueToothManager ---> disconnect , mConnected :" + mConnected);
        if (mMainActivity == null) {
            return;
        }
        unregisterReceiver();
        if (mBluetoothLeService != null) {
            mConnected = false;
            mBluetoothLeService.disconnect();
        }
        mCurrentDevice = null;

    }


    public void unregisterReceiver() {
        if (mMainActivity == null) {
            return;
        }
        try {
            if (mGattUpdateReceiver != null) {
                mMainActivity.unregisterReceiver(mGattUpdateReceiver);
            }
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    public void registerReceiver() {
        if (mMainActivity == null) {
            return;
        }
        mMainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void unbindService() {
        if (mMainActivity == null) {
            return;
        }
        try {
            if (mServiceConnection != null) {
                mMainActivity.unbindService(mServiceConnection);
            }
            mBluetoothLeService = null;
            mInstant = null;
        } catch (java.lang.IllegalArgumentException e) {
            mInstant = null;
        }
    }

    public void setUpdateFirmwareFinish() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.setUpdateFirmwareFinish();
        }
    }

    public void setLeScanCallback(BikeLeScanCallback callback) {
        mLeScanCallback = callback;
    }

    private void init() {
        mHandler = new Handler();
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            UIUtils.toastFalse(mContext, com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_toast_tips_not_ble);
            return;
        }
        //开启蓝牙
        mBluetoothAdapter.enable();

    }


    private String errorNO;
    private int bestRssi = -9999;

    /**
     * 当用户登录并且连接过单车，会自动搜索并连接
     */
    public void searchAndConnect(final Context context, final OnGattNotifyLisener lisener) {
        errorNO = "";
        bestRssi = -9999;
        startTime = System.currentTimeMillis();
        final String name = SingletonBTInfo.INSTANCE.getBikeBluetoothNumber();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        mLeScanCallback = new BikeLeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (name.equals(device.getName()) && mScanning) {
                    mScanning = false;
                    mCurrentDevice = device;
                    CommonUtils.log("searchAndConnect ---> onLeScan , device.getAddress :" + device.getAddress() + " / device.getName :" + device.getName());
                    long searchTime = System.currentTimeMillis();
                    CommonUtils.log("*****************蓝牙搜索时间：" + (searchTime - startTime) / 1000.0 + "秒");
                    connect(mMainActivity, device.getAddress(), lisener, true);
                } else {
                    if (device.getName() != null) {
                        if (device.getName().length() == 20) {

                            if (rssi > bestRssi) {
                                errorNO = device.getName();
                                bestRssi = rssi;
                            }

                        }
                    }
                }
            }

            @Override
            public void onLeScanStart() {

            }

            @Override
            public void onLeScanEnd() {
                lisener.onLeScanEnd((mCurrentDevice != null));
                mScanning = false;
                requestBikeCodeTolerant(errorNO);
            }
        };
        scanLeDevice(true);
    }

    public void setCurrentDevice(BluetoothDevice device) {
        mCurrentDevice = device;
    }

    /**
     * Starts (stop) a scan for Bluetooth LE devices.
     */
    public void scanLeDevice(final boolean enable) {
        if (mLeScanCallback == null) {
            return;
        }
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mLeScanCallback.onLeScanEnd();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mLeScanCallback.onLeScanStart();
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mLeScanCallback.onLeScanEnd();
        }
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        CommonUtils.log("BikeBlueToothManager -----> displayGattServices()");
        if (gattServices == null) return;
        UUID uuid = null;
        CommonUtils.log(BluetoothLeService.TAG, "gattServices.size() : " + gattServices.size());
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid();
            if (uuid != null && BikeBlueToothUtils.isBikeService(gattService.getUuid())) {
                mGattService = gattService;
                CommonUtils.log(BluetoothLeService.TAG, "\n----------gattService--------------------");
                //-----Service的字段信息-----//
                int type = gattService.getType();
                CommonUtils.log(BluetoothLeService.TAG, "-->service type:" + type + " / " + BikeBlueToothUtils.getServiceType(type));
                CommonUtils.log(BluetoothLeService.TAG, "-->service uuid:" + uuid);

                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid();
                    CommonUtils.log(BluetoothLeService.TAG, "-----------------------------------------------------------");
                    CommonUtils.log(BluetoothLeService.TAG, "---->char uuid:" + gattCharacteristic.getUuid());
                    CommonUtils.log(BluetoothLeService.TAG, "---->char getProperties: " + gattCharacteristic.getProperties() + " / " + BikeBlueToothUtils.getCharPropertie(gattCharacteristic.getProperties()));
                    if (uuid != null && BikeBlueToothUtils.isBikeCharNotifyProperties(uuid)) {
                        mNotifyCharacteristic = gattCharacteristic;
                        CommonUtils.log(BluetoothLeService.TAG, "----------mNotifyCharacteristic--------------------");
                        CommonUtils.log(BluetoothLeService.TAG, "---->char getDescriptors.size :" + gattCharacteristic.getDescriptors().size());
                        for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                            CommonUtils.log(BluetoothLeService.TAG, "----------notify descriptor--------------------");
                            CommonUtils.log(BluetoothLeService.TAG, "---->descriptor uuid:" + descriptor.getUuid());
                        }
                    } else if (uuid != null && BikeBlueToothUtils.isBikeCharWriteProperties(uuid)) {
                        mWriteCharacteristic = gattCharacteristic;
                        CommonUtils.log(BluetoothLeService.TAG, "----------mWriteCharacteristic--------------------");
                        CommonUtils.log(BluetoothLeService.TAG, "---->char getDescriptors.size :" + gattCharacteristic.getDescriptors().size());
                        for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                            CommonUtils.log(BluetoothLeService.TAG, "----------write descriptor--------------------");
                            CommonUtils.log(BluetoothLeService.TAG, "---->descriptor uuid:" + descriptor.getUuid());
                        }
                    }
                }
            }
        }

        setNotification(mNotifyCharacteristic);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_CALLBAK);
        return intentFilter;
    }

    /**
     * 监听蓝牙模块通知消息
     */
    public interface OnGattNotifyLisener {
        void onConnected(); // 蓝牙已连接

        void onDisconnected(); // 蓝牙已断开

        void onDataAvailable(BlueToothResponseInfo data, String json);

        void onLeScanEnd(boolean isFound);

        void onConnectTimeOut(); // 蓝牙连接超时
    }

    /**
     * 发送数据后的监听
     */
    public interface OnGattWriteLisener {
        void onCharacteristicWrite(String result, boolean isFinish);
    }

    /**
     * 从一个byte[]数组中截取一部分
     *
     * @param src
     * @param begin
     * @param count
     * @return
     */
    private byte[] subBytes(byte[] src, int begin, int count) {
        CommonUtils.log("writeCharacteristic", "-------------> begin ： " + begin + " / count : " + count);
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) {
            bs[i - begin] = src[i];
        }
        return bs;
    }

    /**
     * 用于控制车的状态
     *
     * @param info
     * @param needStartDriving 是否需要骑行状态
     */
    public void writeDataToBike(BlueToothRequstInfo info, boolean needStartDriving, boolean isStratDrabing) {
        writeDataToBike(info, needStartDriving, isStratDrabing, false);
    }

    /**
     * 开始准备升级固件
     */
    public void startToUpdateFirmware(BlueToothRequstInfo info, final OnGattWriteLisener lisener) {
        if (info != null && isConnect()) {
            final String value = toJson(info);
            if (!TextUtils.isEmpty(value)) {
                CommonUtils.log("writeDataToBike -----> " + value);
                new Thread() {
                    public void run() {
                        writeCharacteristic(value, true);
                        mBluetoothLeService.setOnGattWriteLisener(lisener);
                        setNotifycation(true);
                    }

                    ;
                }.start();
            }
        }
    }

    /**
     * 将信息写入蓝牙模块
     *
     * @param info
     * @param needStartDriving 是否需要骑行状态
     * @param isUpdateFirmware 是否为固件升级
     */
    public void writeDataToBike(BlueToothRequstInfo info, boolean needStartDriving, boolean isStratDraving, final boolean isUpdateFirmware) {
        boolean tag;
        if (needStartDriving) {
            tag = isStratDraving;
        } else {
            tag = true;
        }
        if (info != null && isConnect() && tag) {
            final String value = toJson(info);
            if (!TextUtils.isEmpty(value)) {
                CommonUtils.log("writeDataToBike -----> " + value);
                new Thread() {
                    public void run() {
                        writeCharacteristic(value, isUpdateFirmware);
                    }

                    ;
                }.start();
            }
        }
    }

    /**
     * 暂时按照顺序获取json数据
     * 注意： json顺序请勿更改！！！！！
     */
    private String toJson(BlueToothRequstInfo info) {
        StringBuffer jsonBuffer = new StringBuffer();
        if (info != null) {
            jsonBuffer.append("{");
            jsonBuffer.append("\"lt\":" + "\"" + info.getLt() + "\"");
            jsonBuffer.append(",\"st\":" + "\"" + info.getSt() + "\"");
            jsonBuffer.append(",\"ge\":" + "\"" + info.getGe() + "\"");
            jsonBuffer.append(",\"md\":" + "\"" + info.getMd() + "\"");
            jsonBuffer.append(",\"ve\":" + "\"" + info.getVe() + "\"");
            jsonBuffer.append("}");
        }
        return jsonBuffer.toString();
    }

    /**
     * 创建是否进行搜索蓝牙的提示
     */
    public void creatSearchDialog(final Activity activity, final Class clazz, final int ConstantString) {
        if (activity == null) {
            return;
        }
        try {
            String title = activity.getResources().getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_title);
            String content = activity.getResources().getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_content);
            String cancel = activity.getResources().getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_cancel);
            String ok = activity.getResources().getString(com.tsinova.bluetoothandroid.R.string.sdk_bltmanager_search_bl_dialog_ok);
            UIUtils.createDialog(activity, title, content, cancel, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, ok, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//					Intent bt = new Intent(activity,BikeBlueToothListActivity.class);
                    Intent bt = new Intent(activity, clazz);
                    bt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    String activityName = activity.toString();
                    bt.putExtra("activity", activityName);
                    CommonUtils.log("BikeBlueToothManager activityName ---------------> " + activityName);
                    activity.startActivityForResult(bt, ConstantString);
                    dialog.dismiss();
                }
            }).show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        CommonUtils.log("BikeBlueToothManager ---> release");
        startTime = 0;
        mInstant = null;
    }

}
