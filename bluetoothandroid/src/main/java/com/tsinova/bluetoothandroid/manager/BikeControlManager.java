package com.tsinova.bluetoothandroid.manager;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;


import com.tsinova.bluetoothandroid.bluetooth.BikeBlueToothManager;
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
//            mBLEManager.searchAndConnect(mActivity, mOnGattNotifyLisener);
//            mBLEManager.connect(mActivity,btAddress,mOnGattNotifyLisener);
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
     /**   try {//需要listener
            ControlFragment.setTipGONE();
        }catch (Exception e){

        }

        try {
            ControlLandFragment.setTipGONE();
        }catch (Exception e){

        }
      */


        CommonUtils.log("BIkeControlManager ----> endDriving()");
//        if(mBLEManager != null){
//            if(mBLEManager.isConnect()){
//                if(mRequstInfo == null){
//                    String ve = AppParams.getInstance().getVe();
//                    mRequstInfo = new BlueToothRequstInfo();
//                    mRequstInfo.setVe(ve);
//                }
//                int md = AppParams.getInstance().getMd() ;
//                mRequstInfo.setMd(md +"");
//                mRequstInfo.setLt("0");
//                mRequstInfo.setSt("0");
//                mRequstInfo.setGe("0");
//                writeDataToBike(mRequstInfo);
//            }
//        }
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
        if (mResponseInfo != null && mResponseInfo.getDa() != null & mResponseInfo.getDa().size() > 0) {
            if (mRequstInfo == null) {
                mRequstInfo = new BlueToothRequstInfo();
                mRequstInfo.setVe(mResponseInfo.getVe());
                mRequstInfo.setSt("1");
                mRequstInfo.setMd(mResponseInfo.getMd());
                mRequstInfo.setGe(String.valueOf(mResponseInfo.getDa().get(0).getGe()));
            }
            mRequstInfo.setLt(open ? "1" : "0");
            mRequstInfo.setSt("1");
//            mRequstInfo.setSt(mStartDriving ? "1" : "0");
            CommonUtils.log("openLight -----> " + mRequstInfo.toString());
            writeDataToBike(mRequstInfo, false);
            appOpenLight(open);
        }
    }

    @Override
    public void setMDToBike(int md) {
        if (mResponseInfo != null && mResponseInfo.getDa() != null && mResponseInfo.getDa().size() > 0 && mBLEManager != null) {
            if (mRequstInfo == null) {
                mRequstInfo = new BlueToothRequstInfo();
            }
            mRequstInfo.setVe(mResponseInfo.getVe());
            mRequstInfo.setSt("1");
            mRequstInfo.setLt(String.valueOf(mResponseInfo.getDa().get(0).getLt()));
            mRequstInfo.setGe(String.valueOf(mResponseInfo.getDa().get(0).getGe()));
            CommonUtils.log("实时档位----------->"+mResponseInfo.getDa().get(0).getGe());
            CommonUtils.log("转换后实时档位----------->"+String.valueOf(mResponseInfo.getDa().get(0).getGe()));
//            }
            mRequstInfo.setSt("1");
            mRequstInfo.setMd(String.valueOf(md));
            mBLEManager.setMDToBike(mRequstInfo, md);
            appSetMDToBike(md);
        }
    }

    @Override
    public void shiftedGears(int shift) {

//        mRequstInfo.setLt(mRoundProgressBar.isOpen() ? "1" : "0");
        mRequstInfo.setMd(mResponseInfo.getMd());
        mRequstInfo.setSt("1");
        mRequstInfo.setGe(shift + "");
        writeDataToBike(mRequstInfo);
        appShiftedGears(shift);
    }

    @Override
    public boolean isConnect() {
        appIsConnect();
        return isConnect;
    }

//    以下8行代码，放在AppBikeControlManager里面。
//    public void setOBDFirstShowSts(boolean show){
//        OBDManager.instance.setOBDFirstShowSts(show);
//    }
//
//    public boolean isOBDFirstShow(){
//        return OBDManager.instance.isOBDFirstShow();
//    }



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
        if (mResponseInfo != null && mResponseInfo.getDa() != null && mResponseInfo.getDa().size() > 0) {
            return mResponseInfo.getDa().get(0).getLt().equals("1");
        }
        return false;
    }


    private BikeBlueToothManager.OnGattNotifyLisener mOnGattNotifyLisener = new BikeBlueToothManager.OnGattNotifyLisener() {
        @Override
        public void onDisconnected() {
            CommonUtils.log("mOnGattNotifyLisener ----> onDisconnected");
            if (mBLEManager != null) {

//                以下8行代码，放在AppBikeControlManager里面。
//                setOBDFirstShowSts(true);
//                lastOBD = "0";
//
//                if (isStartDriving()) {
//                    showReConnectionDialog();
//                }else {
////                    UIUtils.toastFalse(mActivity, R.string.main_toast_tips_disconnect);
//                }
                mBLEManager.disconnect();
                mBLEManager.unregisterReceiver();
//                endDriving();
                isConnect = false;
//                mIsCheckedUpdate = false;
                disconnected();
                appDisconnect();
            }
        }

        @Override
        public void onDataAvailable(BlueToothResponseInfo data, String json) {
            CommonUtils.log("mOnGattNotifyLisener ----> onDataAvailable json ： " + json);
            mIsNotify = true;
            if (data == null || TextUtils.isEmpty(json) || data.getDa().size() < 1) {
                CommonUtils.log("MainActivity ------> onDataAvailable --> data is null.....");
                return;
            }

            if (mRequstInfo != null && isReConnection) {
                // writeDataToBike(mRequstInfo);
            }
            isReConnection = false;
            mResponseInfo = data;

//            以下1行代码，放在AppBikeControlManager里面。
//            dealOBD();

//            以下4行代码，放在AppBikeControlManager里面。
//            AppParams.getInstance().setMd(mResponseInfo.getMd());
//            AppParams.getInstance().setFirmwareVersion(mResponseInfo.getVe());
//            BikePreferencesUtils.setFirmwareVersion(mActivity.getApplicationContext(), AppParams.getInstance().getFirmwareVersion());
//            BikePreferencesUtils.setDefaultMD(mActivity.getApplicationContext(), mResponseInfo.getMd());

            if (mRequstInfo == null) { // 初始化骑行
                String md = data.getMd();
                String ve = data.getVe();
                mRequstInfo = new BlueToothRequstInfo();
                mRequstInfo.setVe(ve);
                mRequstInfo.setMd(md);
                mRequstInfo.setSt("1");
                mRequstInfo.setLt(data.getDa().get(0).getLt());
                mRequstInfo.setGe(data.getDa().get(0).getGe() + "");
            }

            if (mStartDriving) {
                mRequstInfo.setGe(mResponseInfo.getDa().get(0).getGe() + "");
                mRequstInfo.setLt(mResponseInfo.getDa().get(0).getLt() + "");
                CommonUtils.log("onDataAvailable ----> getGe : " + mResponseInfo.getDa().get(0).getGe() + "");
                CommonUtils.log("onDataAvailable ----> getLt : " + mResponseInfo.getDa().get(0).getLt() + "");
//                updateUI(mResponseInfo);
            } else {
                if (data != null && data.getDa() != null && data.getDa().size() > 0) {
                    BikeBlueToothInfo info = data.getDa().get(0);
//                    tv_battery.setText(info.getBe() + "%");
                    CommonUtils.log("--------> 无骑行状态，刷新电池电量 ：" + info.getBe() + "%");
                    if (!isConnect()) {
                        isConnect = true;
                    }
                }
            }

            dataAvailable(mResponseInfo);
            appDataAvailable(mResponseInfo,json);

//            以下固件升级请求模块代码放在AppBikeControlManager里面。
//            if (!mIsCheckedUpdate) { // 请求固件升级
//                mIsCheckedUpdate = true;
//
//                UpdateFirmwareManager fm = new UpdateFirmwareManager(mActivity, mActivity, true);
//
////                UpdateFirmwareManager fm = new UpdateFirmwareManager(mActivity, bikeActivity, true);
//                fm.setUpdateFirmwareManagerListener(new UpdateFirmwareManager.UpdateFirmwareManagerListener() {
//                    @Override
//                    public void checkFinish(boolean needUpdate) {
//                        CommonUtils.log("UpdateFirmwareManagerListener -----> checkFinish : " + needUpdate);
//                        checkBLEFinish(needUpdate);
//                        if (!needUpdate) {
//                            String connect_success = mActivity.getResources().getString(R.string.main_toast_tips_connect_success);
//                            UIUtils.toastSuccess(mActivity, connect_success + ":\n" + AppParams.getInstance().getBTName());
//                        }
//                    }
//
//                    @Override
//                    public void updateFinish(boolean isSuccess) {
//                        CommonUtils.log("UpdateFirmwareManagerListener -----> updateFinish : " + isSuccess);
//                        updateBLEFinish(isSuccess);
//                    }
//
//                    @Override
//                    public void checkError() {
//                        CommonUtils.log("UpdateFirmwareManagerListener -----> checkError");
//                        checkBLEError();
//                        String connect_success = mActivity.getResources().getString(R.string.main_toast_tips_connect_success);
//                        UIUtils.toastSuccess(mActivity, connect_success + ":\n" + AppParams.getInstance().getBTName());
//                    }
//                });
//                fm.sendUpdateRequest();
//                long notifyTime = System.currentTimeMillis();
//                CommonUtils.log("*****************蓝牙通信成功时间：" + (notifyTime - BikeBlueToothManager.startTime) / 1000.0 + "秒");
//
//            }
        }

        @Override
        public void onConnected() {
            if (mBLEManager != null && !mBLEManager.isConnect()) {
//                以下1行代码，放在AppBikeControlManager里面。
//                BikePreferencesUtils.setConnected(mActivity);
                CommonUtils.log("mOnGattNotifyLisener -----> onConnected " + " / isConnect :" + mBLEManager.isConnect());
                connected();
                appConnected();
                isConnect = true;
                checkTheConnect();
                mBLEManager.setConnect(true);
//                以下1行代码，放在AppBikeControlManager里面。
//                mActivity.dismissLoadingView();

//                TsinovaApplication.getInstance().finishActivityByClass(BikeBlueToothListActivity.class);
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
//                以下2行代码，放在AppBikeControlManager里面。
//                showReConnectionDialog();
//                mActivity.dismissLoadingView();
                isAutoConnect = false;
            } else if (!isFound) { // 未找到蓝牙
                isAutoConnect = false;
//                UIUtils.toastFalse(mActivity, R.string.main_toast_tips_not_search_bike_bl);
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

//    一下处理OBD的模块，放在AppBikeControlManager里面。
//    private void dealOBD() {
//        if(mResponseInfo != null) {
//            String obdInfo = mResponseInfo.getSu();
//            OBDManager.getObdInfo().setSu(obdInfo);
//            CommonUtils.log("OBD String ------------------>" + obdInfo);
////            if (!obdInfo.equals("0000000000") && OBDManager.getObdInfo().isFirst() && OBDManager.isHavaOBD()) {
////                OBDManager.setObdErrorLevel(OBDManager.getOBDLevel());
////                OBDManager.getObdInfo().setFirst(false);
////                OBDManager.instance.requestOBDCreate();
////            }
//            if (!obdInfo.equals("0000000000") && OBDManager.isHavaOBD()) {
//                OBDManager.setObdErrorLevel(OBDManager.getOBDLevel());
//                if(!lastOBD.equals(obdInfo)) {
//                    OBDManager.instance.requestOBDCreate();
//                }
//                lastOBD = obdInfo;
//            }
//        }
//
//    }


//    失效代码
//    public void onCancel() {
//        cancelReconnection();
//        isReConnection = false;
//    }
//    public void doClickReconn() {
//        String btName = BikePreferencesUtils.getCarBluetoothNumber(mActivity);
//        CommonUtils.log("showReConnectionDialog ---> do reconnection, btAddress :" + btName);
//        if (TextUtils.isEmpty(btName)) {
//            Intent bt = new Intent(mActivity, BikeBlueToothListActivity.class);
//            String activityName = mActivity.toString();
//            bt.putExtra("activity", activityName);
//            CommonUtils.log("BikeBlueToothManager activityName ---------------> " + activityName);
//            mActivity.startActivityForResult(bt, Constant.ACTIVITY_REQUEST_CODE_SCAN_BLE);
//        } else {
//            bindServiceAndConnectBLE();
//            mActivity.showLoadingView();
//        }
//        doClickReconnection();
//    }


//    以下关于dialog的构造与关闭的代码，放在AppBikeControlManager里面。
//    ReConnBikeDialog dialog;
//    private Dialog mReconnectionDialog = null;
//
//    /**
//     * 显示重新连接蓝牙提示
//     */
//    private void showReConnectionDialog() {
//        if (mActivity == null) {
//            return;
//        }
//        isReConnection = true;
//
//
////        Intent intent = new Intent(mActivity, ConnBikeActivity.class);
////        intent.putExtra("mode",ConnBikeActivity.MODE_RIDING);
////        mActivity.startActivityForResult(intent, Constant.ACTIVITY_REQUEST_DOCLICKRECONN);
//
//
//        boolean land = AppParams.getInstance().isLand();
//
//        if (land) {
//            if (mReconnectionDialog == null) {
//                String title = mActivity.getResources().getString(com.tsinova.bike.R.string.control_sidconnected);
//                String content = mActivity.getResources().getString(com.tsinova.bike.R.string.control_continueride);
//                String cancel = mActivity.getResources().getString(com.tsinova.bike.R.string.bltmanager_search_bl_dialog_cancel);
//                String ok = mActivity.getResources().getString(com.tsinova.bike.R.string.control_reconnect);
//                mReconnectionDialog = UIUtils.createDialog(mActivity, title, content
//                        , cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                cancelReconnection();
//                                dialog.dismiss();
//                                isReConnection = false;
//                            }
//                        }
//                        , ok, new Dialog.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String btName = BikePreferencesUtils.getCarBluetoothNumber(mActivity);
//                                CommonUtils.log("showReConnectionDialog ---> do reconnection, btAddress :" + btName);
//                                if (TextUtils.isEmpty(btName)) {
////                                    Intent bt = new Intent(mActivity, BikeBlueToothListActivity.class);
////                                    String activityName = mActivity.toString();
////                                    bt.putExtra("activity", activityName);
////                                    CommonUtils.log("BikeBlueToothManager activityName ---------------> " + activityName);
////                                    mActivity.startActivityForResult(bt, Constant.ACTIVITY_REQUEST_CODE_SCAN_BLE);
//                                } else {
//                                    bindServiceAndConnectBLE();
//                                    mActivity.showLoadingView();
//                                }
//                                doClickReconnection();
//                                dialog.dismiss();
//                            }
//                        });
//                mReconnectionDialog.setCanceledOnTouchOutside(false);
//                mReconnectionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK||keyCode ==KeyEvent.KEYCODE_SEARCH) {
//                            isReConnection = false;
//                            cancelReconnection();
//                            dialog.dismiss();
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//            }
//
//            if (!mReconnectionDialog.isShowing()) {
//                shouwReconnectionDialog(mReconnectionDialog);
//                mReconnectionDialog.show();
//            }
//        } else {
//            dialog = new ReConnBikeDialog(mActivity);
//            ReConnBikeDialog.Builder builder = new ReConnBikeDialog.Builder(mActivity);
//            builder.setPositiveButton(new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    String btName = BikePreferencesUtils.getCarBluetoothNumber(mActivity);
//                    CommonUtils.log("showReConnectionDialog ---> do reconnection, btAddress :" + btName);
//                    if (TextUtils.isEmpty(btName)) {
////                        Intent bt = new Intent(mActivity, BikeBlueToothListActivity.class);
////                        String activityName = mActivity.toString();
////                        bt.putExtra("activity", activityName);
////                        CommonUtils.log("BikeBlueToothManager activityName ---------------> " + activityName);
////                        mActivity.startActivityForResult(bt, Constant.ACTIVITY_REQUEST_CODE_SCAN_BLE);
//                    } else {
//                        bindServiceAndConnectBLE();
//                        mActivity.showLoadingView();
//                    }
//                    doClickReconnection();
//                    dialog.dismiss();
//
//                }
//            });
//
//            builder.setCloseButton(new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    cancelReconnection();
//                    dialog.dismiss();
//                    isReConnection = false;
//
//                }
//            });
//            dialog = builder.create();
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK||keyCode ==KeyEvent.KEYCODE_SEARCH) {
//                        cancelReconnection();
//                        dialog.dismiss();
//                        isReConnection = false;
//                        return true;
//                    }
//                    return false;
//                }
//            });
//
//
//
//
//            if (!dialog.isShowing()) {
//                shouwReconnectionDialog(dialog);
//                dialog.show();
//            }
//        }
//
//
//    }
//    public void dismissDialog(){
//        try {
//            dialog.dismiss();
//        }catch (Exception e){
//
//        }
//
//        try {
//            mReconnectionDialog.dismiss();
//        }catch (Exception e){
//
//        }
//
//        try {
//            showReConnectionDialog();
//        }catch (Exception e){}
//
//    }


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
//                    UIUtils.toastFalse(mActivity, R.string.main_toast_tips_connect_fail);
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

//    以下判断是否连接过单车的代码，放在AppBikeControlManager里面。
//    /**
//     * 是否连接过单车
//     * (目前用固件信息来判断是否连接过)
//     */
//    public boolean isBoundBike() {
//        if (mActivity != null) {
//            boolean isConnected = BikePreferencesUtils.getConnected(mActivity.getApplicationContext());
//            return isConnected;
//        }
//        return false;
//    }


    public boolean isReConnection() {
        return isReConnection;
    }

    public void creatSearchDialog(final Activity activity) {
        if (mBLEManager != null) {
           /** mBLEManager.creatSearchDialog(activity, ConnBikeActivity.class, Constant.ACTIVITY_REQUEST_CONN_BIKE);*/
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
