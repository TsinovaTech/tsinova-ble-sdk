package com.tsinova.bluetoothandroid.manager;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;


import com.tsinova.bluetoothandroid.bluetooth.BikeBlueToothManager;
import com.tsinova.bluetoothandroid.bluetoothview.ConnBikeActivity;
import com.tsinova.bluetoothandroid.common.Constant;
import com.tsinova.bluetoothandroid.listener.CallBack1;
import com.tsinova.bluetoothandroid.listener.OnAppBikeCallback;
import com.tsinova.bluetoothandroid.listener.OnBikeCallback;
import com.tsinova.bluetoothandroid.pojo.BikeBlueToothInfo;
import com.tsinova.bluetoothandroid.pojo.BlueToothRequstInfo;
import com.tsinova.bluetoothandroid.pojo.BlueToothResponseInfo;
import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;
import com.tsinova.bluetoothandroid.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class BikeControlManager extends BikeController {

    private static final String TAG = BikeControlManager.class.getSimpleName();
    private List<OnBikeCallback> mOnBikeCallbacks = new ArrayList<OnBikeCallback>();
    private List<OnAppBikeCallback> mOnAppBikeCallbacks = new ArrayList<OnAppBikeCallback>();
    private static final int NOTIFY_TIMEOUT = 20 * 1000; // 蓝牙通信超时时间
    private FragmentActivity mActivity;
    private BlueToothRequstInfo mRequstInfo; // 发送蓝牙信息
    private BlueToothResponseInfo mResponseInfo; // 接受蓝牙信息
    private BikeBlueToothManager mBLEManager;
//    private SharePreferencesManager mPreferencesManager;
    /*---这些状态可以封装起来-----*/
    private boolean mInitUI; // 开始骑行后是否首次初始化了UI
    private boolean mIsNotify = false; // 是否收到了蓝牙模块的通知
    private boolean mIsCheckedUpdate = false; // 是否请求完成固件升级
    /*---这些状态可以封装起来----*/
    private boolean mStartDriving = false;
    private boolean isAutoConnect = false;// 是否为自动连接
    private boolean isConnect = false; // 是否已经连接
    private boolean isReConnection = false; // 是否是重新连接
//    private String lastOBD = "0";


    static BikeControlManager instance;

    FragmentActivity bikeActivity;

    public static synchronized BikeControlManager getInstance(FragmentActivity activity) {
        if (instance == null) {
            instance = new BikeControlManager(activity);
        }
        return instance;
    }


    public static synchronized BikeControlManager getInstance() {
        return instance;
    }


    public void setBikeActivity(FragmentActivity activity) {
        this.bikeActivity = activity;
    }


    /**
     * 用于获取实例
     *
     * @param activity
     * @return
     */
    public static synchronized BikeControlManager getBikeControlManager(FragmentActivity activity) {
        return instance;
    }


    private BikeControlManager(FragmentActivity activity) {
        this.mActivity = activity;
        mBLEManager = BikeBlueToothManager.getInstant(mActivity);
        mBLEManager.bindService(mActivity);

        init();
    }

    private void init() {
        isConnect = false;
    }


    @Override
    public void bindServiceAndConnectBLE() {
        CommonUtils.log("----> bindServiceAndConnectBLE");
        String btName = SingletonBTInfo.INSTANCE.getBikeBluetoothNumber();
        if (!TextUtils.isEmpty(btName) && mBLEManager != null && !mBLEManager.isConnect()) {
            isAutoConnect = true;
            mBLEManager.searchAndConnect(mActivity, mOnGattNotifyLisener);
            appBindServiceAndConnectBLE();
        }
    }


    /**
     * bikefragment
     */
    public void bindServiceAndConnectBLEBikeFragment() {
        CommonUtils.log("----> bindServiceAndConnectBLE");
        String btName = SingletonBTInfo.INSTANCE.getBikeBluetoothNumber();
        if (!TextUtils.isEmpty(btName)
                && mBLEManager != null && !mBLEManager.isConnect()) {
            isAutoConnect = true;
            appBindServiceAndConnectBLEBikeFragment();
        }

    }

    /**
     * 是否已经开始骑行
     */
    public boolean isStartDriving() {
        return mStartDriving;
    }

    @Override
    public void startDriving() {
        if (mBLEManager == null) {
            CommonUtils.log("startDraiving ---> mBLEManager is null !!!!!");
            return;
        }
        if (mBLEManager.isConnect()) {
            mBLEManager.setOnGattNotifyLisener(mOnGattNotifyLisener);
            mBLEManager.setNotifycation(true);
        } else {
            mBLEManager.connect(mActivity, SingletonBTInfo.INSTANCE.getBikeBluetoothaddress(), mOnGattNotifyLisener);
        }
        mStartDriving = true;
        mInitUI = false;
        mIsNotify = false;
        checkTheConnect();
        appStartDriving();
    }

    @Override
    public void endDriving() {

        appEndDriving();

        CommonUtils.log("BIkeControlManager ----> endDriving()");
        mRequstInfo = null;
        mStartDriving = false;
        mInitUI = true;
        mIsNotify = false;
        mIsCheckedUpdate = true;
    }

    private void writeDataToBike(BlueToothRequstInfo info) {
        if (mBLEManager != null) {
            CommonUtils.log("BikeControlManager ---> writeDataToBike, info :" + info.toString());
            mBLEManager.writeDataToBike(info, true, isStartDriving());
        }
    }

    private void writeDataToBike(BlueToothRequstInfo info, boolean startDriving) {
        if (mBLEManager != null) {
            CommonUtils.log("BikeControlManager ---> writeDataToBike, info :" + info.toString());
            mBLEManager.writeDataToBike(info, startDriving, isStartDriving());
        }
    }

    public void connect(String deviceAddress) {
        if (mBLEManager != null) {
            mBLEManager.connect(mActivity, deviceAddress, mOnGattNotifyLisener);
        }
    }


    public void connect(String deviceAddress, CallBack1 callBack1) {
        if (mBLEManager != null) {
            mBLEManager.connect(mActivity, deviceAddress, mOnGattNotifyLisener);
        }
    }

    @Override
    public void openLight(boolean open) {
        if (mResponseInfo != null) {

            if(mRequstInfo == null){
                mRequstInfo = new BlueToothRequstInfo();
            }
            mRequstInfo.setLt(open ? "1" : "0");

            CommonUtils.log("openLight -----> " + mRequstInfo.toString());
            writeDataToBike(mRequstInfo, false);
            appOpenLight(open);
        }
    }

    @Override
    public void setMDToBike(int md) {
    }

    @Override
    public void shiftedGears(int shift) {

    }

    @Override
    public boolean isConnect() {
        appIsConnect();
        return isConnect;
    }


    @Override
    public void release() {
        CommonUtils.log("BIkeControlManager ---> release");
        if (mBLEManager != null) {
            mBLEManager.unregisterReceiver();
            mBLEManager.unbindService();
            mBLEManager.release();
            mBLEManager = null;
        }
        mActivity = null;
        instance = null;
        mStartDriving = false;
        isReConnection = false;
        appRelease();
    }

    public boolean isOpenLight() {
        if (mResponseInfo != null) {
            return mResponseInfo.getLt().equals("1");
        }
        return false;
    }


    private BikeBlueToothManager.OnGattNotifyLisener mOnGattNotifyLisener = new BikeBlueToothManager.OnGattNotifyLisener() {
        @Override
        public void onDisconnected() {
            CommonUtils.log("mOnGattNotifyLisener ----> onDisconnected");
            if (mBLEManager != null) {
                mBLEManager.disconnect();
                mBLEManager.unregisterReceiver();
                isConnect = false;
                disconnected();
                appDisconnect();
            }
        }

        @Override
        public void onDataAvailable(BlueToothResponseInfo data, String json) {
            CommonUtils.log("mOnGattNotifyLisener ----> onDataAvailable json ： " + json);
            mIsNotify = true;
            if (data == null || TextUtils.isEmpty(json)) {
                CommonUtils.log("MainActivity ------> onDataAvailable --> data is null.....");
                return;
            }

            if (mRequstInfo != null && isReConnection) {
                // writeDataToBike(mRequstInfo);
            }
            isReConnection = false;
            mResponseInfo = data;

            if (!mStartDriving){
                if (data != null) {
//                    tv_battery.setText(info.getBe() + "%");
                    CommonUtils.log("--------> 无骑行状态，刷新电池电量 ：" + data.getBe() + "%");
                    if (!isConnect()) {
                        isConnect = true;
                    }
                }
            }

            dataAvailable(mResponseInfo);
            appDataAvailable(mResponseInfo,json);
        }

        @Override
        public void onConnected() {
            if (mBLEManager != null && !mBLEManager.isConnect()) {
                CommonUtils.log("mOnGattNotifyLisener -----> onConnected " + " / isConnect :" + mBLEManager.isConnect());
                connected();
                appConnected();
                isConnect = true;
                checkTheConnect();
                mBLEManager.setConnect(true);
            }
        }

        @Override
        public void onLeScanEnd(boolean isFound) {
            scanBLEEnd(isFound);
            scanAppBLEEnd(isFound);
            CommonUtils.log("MainActivity ------> onLeScanEnd , isAutoConnect :" + isAutoConnect + " /isFound :" + isFound);
            if (!isAutoConnect) {// 自动搜索蓝牙连接的时候不关闭dialog
                isAutoConnect = false;
            }
            if (isReConnection && !isFound) {
                isAutoConnect = false;
            } else if (!isFound) { // 未找到蓝牙
                isAutoConnect = false;
            }
        }

        @Override
        public void onConnectTimeOut() {
            CommonUtils.log("MainActivity ------> onConnectTimeOut");
            connectTimeOut();
            appConnectTimeOut();
            isConnect = false;
//            以下3行代码，放在AppBikeControlManager里面。
//            if (isReConnection) {
//                showReConnectionDialog();
//            }
        }
    };


    private Handler mHandler = new Handler();

    /**
     * 检查单车是否初始化通信成功
     */
    private void checkTheConnect() {
        CommonUtils.log("checkTheConnect ----> checkTheConnect()");
        BikeBlueToothManager.startTime = System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsNotify) {
                    if (mBLEManager != null) {
                        mBLEManager.unregisterReceiver();
                        mBLEManager.setConnect(false);
                        disconnect();
                    }
                    disconnected();
                    appDisconnected();
                    endDriving();
                }
            }
        }, NOTIFY_TIMEOUT);
    }

    public boolean isReConnection() {
        return isReConnection;
    }

    public void creatSearchDialog(final Activity activity) {
        if (mBLEManager != null) {
            mBLEManager.creatSearchDialog(activity, ConnBikeActivity.class, Constant.ACTIVITY_REQUEST_CONN_BIKE);
        }
    }

    public void disconnect() {
        /**lastOBD = "0";//需要listener*/
        if (mBLEManager != null) {
            if (mOnBikeCallbacks != null) {
                for (OnBikeCallback lisener : mOnBikeCallbacks) {
                    if (lisener != null) {
                        lisener.onDisconnectedByHand();
                    }
                }
            }
//            OBDManager.getObdInfo().setFirst(true);
            mBLEManager.disconnect();
            isConnect = false;
            appDisconnected();
        }
    }

    public void addBikeCallBack(OnBikeCallback callback) {
        if (mOnBikeCallbacks != null) {
            mOnBikeCallbacks.add(callback);
        }
    }

    public void removeBikeCallBack(OnBikeCallback callback) {
        if (mOnBikeCallbacks != null) {
            mOnBikeCallbacks.remove(callback);
        }
    }

    public void addAppBikeCallBack(OnAppBikeCallback callback) {
        if (mOnAppBikeCallbacks != null) {
            mOnAppBikeCallbacks.add(callback);
        }
    }

    public void removeAppBikeCallBack(OnAppBikeCallback callback) {
        if (mOnAppBikeCallbacks != null) {
            mOnAppBikeCallbacks.remove(callback);
        }
    }



    private boolean isNull() {
        if (mOnBikeCallbacks == null) {
            return true;
        }
        if(mOnAppBikeCallbacks == null){
            return true;
        }
        return false;
    }


    private void disconnected() {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onDisconnected();
            }
        }
    }
    private void connected() {
        if (isNull()) return;
        try {
            for (OnBikeCallback lisener : mOnBikeCallbacks) {
                if (lisener != null) {
                    lisener.onConnected();
                }
            }
        } catch (Exception e) {
        }
    }

    private void connectTimeOut() {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onConnectTimeOut();
            }
        }
    }

    private void dataAvailable(BlueToothResponseInfo data) {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onDataAvailable(data);
            }
        }
    }

    private void scanBLEEnd(boolean isFound) {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onLeScanEnd(isFound);
            }
        }
    }

    private void scanAppBLEEnd(boolean isFound) {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppLeScanEnd(isFound);
            }
        }
    }

    public void checkBLEFinish(boolean needUpdate) {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onCheckFinish(needUpdate);
            }
        }
    }

    public void checkBLEError() {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onCheckError();
            }
        }
    }

    public void updateBLEFinish(boolean isSuccess) {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onUpdateFinish(isSuccess);
            }
        }
    }

    public void cancelReconnection() {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onCancelReconnection();
            }
        }
    }

    public void doClickReconnection() {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            if (lisener != null) {
                lisener.onDoClickReconnection();
            }
        }
    }

    public void shouwReconnectionDialog(Dialog dialog) {
        if (isNull()) return;
        for (OnBikeCallback lisener : mOnBikeCallbacks) {
            lisener.onShouwReconnectionDialog(dialog);
        }
    }

    private void appDisconnected() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppDisconnected();
            }
        }
    }

    private void appConnected() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppConnected();
            }
        }
    }

    private void appConnectTimeOut() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppConnectTimeOut();
            }
        }
    }

    private void appDataAvailable(BlueToothResponseInfo data,String json) {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppDataAvailable(data,json);
            }
        }
    }

    private void appDisconnect() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppDisconnect();
            }
        }
    }

    private void appBindServiceAndConnectBLE() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppBindServiceAndConnectBLE();
            }
        }
    }

    private void  appBindServiceAndConnectBLEBikeFragment() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener. onAppBindServiceAndConnectBLEBikeFragment();
            }
        }
    }
    private void appStartDriving() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppStartDriving();
            }
        }
    }
    private void appEndDriving() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppEndDriving();
            }
        }
    }

    private void appOpenLight(boolean open) {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppOpenLight(open);
            }
        }
    }
    private void appSetMDToBike(int md) {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppSetMDToBike(md);
            }
        }
    }
    private void appShiftedGears(int shift){
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppShiftedGears(shift);
            }
        }
    }
    private void appIsConnect() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppIsConnect();
            }
        }
    }
    private void appRelease() {
        if (isNull()) return;
        for (OnAppBikeCallback lisener : mOnAppBikeCallbacks) {
            if (lisener != null) {
                lisener.onAppRelease();
            }
        }
    }

}
