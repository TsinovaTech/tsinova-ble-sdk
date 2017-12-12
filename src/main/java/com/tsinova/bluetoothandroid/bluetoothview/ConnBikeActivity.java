package com.tsinova.bluetoothandroid.bluetoothview;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tsinova.bluetoothandroid.BuildConfig;
import com.tsinova.bluetoothandroid.R;
import com.tsinova.bluetoothandroid.bluetooth.BikeBlueToothManager;
import com.tsinova.bluetoothandroid.bluetooth.BikeLeScanCallback;
import com.tsinova.bluetoothandroid.bluetooth.OnBikeBTListener;
import com.tsinova.bluetoothandroid.common.Constant;
import com.tsinova.bluetoothandroid.manager.SDKBikeControlManager;
import com.tsinova.bluetoothandroid.network.HttpRequest;
import com.tsinova.bluetoothandroid.pojo.RequestBikeCode;
import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;
import com.tsinova.bluetoothandroid.util.CommonUtils;
import com.tsinova.bluetoothandroid.util.UIUtils;


/**
 * Created by ihgoo on 2017/4/14.
 */

public class ConnBikeActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout llSerachBluetooth;
    private RelativeLayout rlSerach;
    private TextView tv12;
    private TextView btn1;
    private TextView tv13;
    private TextView tv14;
    private TextView tvTel1;
    private TextView tv15;
    private TextView tv11;
    private RelativeLayout rlSerachFail;
    private TextView tv22;
    private TextView btn2;
    private TextView tv23;
    private TextView tv24;
    private TextView tvTel2;
    private TextView tv25;
    private TextView tv21;
    private RelativeLayout rlConnFail;
    private ImageView ivLogo;
    private ImageView ivBike;
    private ImageView iv1;
    private ImageView iv2;
    private TextView tv1;
    private TextView tv2;
    private RelativeLayout rlConnBike;
    private LinearLayout llClose;
    private RelativeLayout activityMain;
    private int mode = MODE_SERACH;
    public static int MODE_RIDING = 1;
    public static int MODE_SERACH = 0;
    public static boolean isShow = false;


//    private OnBikeBTListener mOnBikeBTListener;

    /**
     * 连接成功过为true
     */
    private boolean isConned;

    private BikeBlueToothManager mManager;
    private static SDKBikeControlManager mSDKBikeControlManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isShow = true;
        setTheme(R.style.sdk_transparent);
        setContentView(R.layout.activity_blue_conn);
        llSerachBluetooth = (LinearLayout) findViewById(R.id.ll_serach_bluetooth);
        rlSerach = (RelativeLayout) findViewById(R.id.rl_serach);
        tv12 = (TextView) findViewById(R.id.tv_12);
        btn1 = (TextView) findViewById(R.id.btn_1);
        tv13 = (TextView) findViewById(R.id.tv_13);
        tv14 = (TextView) findViewById(R.id.tv_14);
        tvTel1 = (TextView) findViewById(R.id.tv_tel1);
        tv15 = (TextView) findViewById(R.id.tv_15);
        tv11 = (TextView) findViewById(R.id.tv_11);
        rlSerachFail = (RelativeLayout) findViewById(R.id.rl_serach_fail);
        tv22 = (TextView) findViewById(R.id.tv_22);
        btn2 = (TextView) findViewById(R.id.btn_2);
        tv23 = (TextView) findViewById(R.id.tv_23);
        tv24 = (TextView) findViewById(R.id.tv_24);
        tvTel2 = (TextView) findViewById(R.id.tv_tel2);
        tv25 = (TextView) findViewById(R.id.tv_25);
        tv21 = (TextView) findViewById(R.id.tv_21);
        rlConnFail = (RelativeLayout) findViewById(R.id.rl_conn_fail);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        ivBike = (ImageView) findViewById(R.id.iv_bike);
        iv1 = (ImageView) findViewById(R.id.iv_1);
        iv2 = (ImageView) findViewById(R.id.iv_2);
        tv1 = (TextView) findViewById(R.id.tv_1);
        tv2 = (TextView) findViewById(R.id.tv_2);
        rlConnBike = (RelativeLayout) findViewById(R.id.rl_conn_bike);
        llClose = (LinearLayout) findViewById(R.id.ll_close);
        activityMain = (RelativeLayout) findViewById(R.id.activity_main);


        btn1.setOnClickListener(this);
        llClose.setOnClickListener(this);
        tvTel1.setOnClickListener(this);
        tvTel2.setOnClickListener(this);
        btn2.setOnClickListener(this);

        BikeBlueToothManager.getInstant(this).setOnBikeBTListenerLisener(mOnBikeBTListener);


        initCarImage();

        startAnim();

        mManager = BikeBlueToothManager.getInstant(this);
        mManager.setLeScanCallback(mLeScanCallback);
        Log.d("event", "蓝牙搜索中。。。");


        mSDKBikeControlManager = SDKBikeControlManager.getBikeControlManager(this);


        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", MODE_SERACH);

        if (mode == MODE_SERACH) {
            rlSerach.setVisibility(View.VISIBLE);
            rlSerachFail.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.GONE);
            rlConnFail.setVisibility(View.GONE);
            startSearch();
        } else if (mode == MODE_RIDING) {
            rlSerach.setVisibility(View.GONE);
            rlSerachFail.setVisibility(View.VISIBLE);
            rlConnBike.setVisibility(View.GONE);
            rlConnFail.setVisibility(View.GONE);
        }


    }

    private void startAnim() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bike_side_out);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);

        if (animation != null) {
            ivBike.startAnimation(animation);
        }


        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(lin);


        if (operatingAnim != null) {
            iv1.startAnimation(operatingAnim);
        }
    }

    private void initCarImage() {
        String carImageUrl = SingletonBTInfo.INSTANCE.getBikeImageUrl();
        String carBrandImageUrl = SingletonBTInfo.INSTANCE.getBikeBrandImage();
        ImageLoader.getInstance().displayImage(carImageUrl, ivBike);
        ImageLoader.getInstance().displayImage(carBrandImageUrl, ivLogo);
    }


    /**
     * 正在连接单车
     */
    private boolean onConn = false;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
        stopSearch();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        // Initializes list view adapter.
        showRefresh(true);
        mManager.scanLeDevice(true);
    }

    /**
     * 停止搜索
     */
    private void stopSearch() {
        showRefresh(false);
        mManager.scanLeDevice(false);
    }

    private BluetoothDevice mDevice;

    private String errorNO;
    private int bestRssi = -9999;


    //    private int i;
    private BikeLeScanCallback mLeScanCallback = new BikeLeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            if (device == null) {
                return;
            }


            bestRssi = -9999;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.log("device.getName : " + device.getName());
                    CommonUtils.log("rssi : " + rssi);
                    CommonUtils.log("device.getUuids() is null : " + (device.getUuids() == null));
                    if (device.getUuids() != null) {
                        CommonUtils.log("device.getUuids().toString() : " + device.getUuids().toString());
                    }
                    if (!TextUtils.isEmpty(device.getName())) {
                        if (device.getName().equals(SingletonBTInfo.INSTANCE.getBikeBluetoothNumber())) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    rlSerach.setVisibility(View.GONE);
                                    rlSerachFail.setVisibility(View.GONE);
                                    rlConnFail.setVisibility(View.GONE);
                                    rlConnBike.setVisibility(View.VISIBLE);

                                    mDevice = device;
                                    connBike(device);
                                }
                            }, 5000);


                        }else {
                            if (device.getName() != null) {
                                String bleName = device.getName().replaceAll(" ", "");
                                if (bleName.length() == 20) {

                                    if (rssi > bestRssi) {
                                        errorNO = device.getName();
                                        bestRssi = rssi;
                                    }

                                }
                            }
                        }
                    }
                    if (mManager != null && mManager.isConnect()) {
                        finish();
                    }
                }
            });

        }

        @Override
        public void onLeScanStart() {

        }

        @Override
        public void onLeScanEnd() {

            if (onConn) {
                return;
            }

            requestBikeCodeTolerant(errorNO);
            mOnBikeBTListener.connectFailure();



            showRefresh(false);
        }
    };

    private void requestBikeCodeTolerant(String errorNO) {
        RequestBikeCode requestBikeCode = new RequestBikeCode();
        requestBikeCode.setApp(SingletonBTInfo.INSTANCE.getPackageName()+ " "+BuildConfig.VERSION_NAME);
        requestBikeCode.setBike_no(SingletonBTInfo.INSTANCE.getBikeNo());
        requestBikeCode.setError(errorNO);
        Gson gson = new Gson();
        String json = gson.toJson(requestBikeCode);
        Log.e("requestBikeCodeTolerant",json);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.post("https://api.tsinova.com/app/bike_codes/tolerant", json);
    }

    public void connBike(BluetoothDevice bt) {
        onConn = true;
        stopSearch();
//        if(AppParams.getInstance().getUser() == null){
//        	return;
//        }

        CommonUtils.log("BikeBlueToothListActivity ---> onItemClick, bt.name :" + bt.getName() + " / getCarBluetoothNumber : " + SingletonBTInfo.INSTANCE.getBikeBluetoothNumber());
        if (bt.getName() == null || !bt.getName().equals(SingletonBTInfo.INSTANCE.getBikeBluetoothNumber())) {
            UIUtils.toastFalse(ConnBikeActivity.this, R.string.btlist_toast_tip_bt_num_wrong);
        } else {
            mManager.setCurrentDevice(bt);


            String address = bt.getAddress();
            String name = bt.getName();

            SingletonBTInfo.INSTANCE.setBikeBluetoothaddress(address);
            SingletonBTInfo.INSTANCE.setBikeBluetoothNumber(name);


            CommonUtils.log("ConnBikeActivity -----> ble address : " + address + " / name : " + name);
            if (mSDKBikeControlManager != null && !TextUtils.isEmpty(address)) {
                mSDKBikeControlManager.connect(address);
            }

            setHideAnimation(llSerachBluetooth, 1500);
        }
    }

    private AlphaAnimation mShowAnimation = null;

    private void setHideAnimation(View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        if (null != mShowAnimation) {
            mShowAnimation.cancel();
        }

        view.clearAnimation();
        mShowAnimation = new AlphaAnimation(1.0f, 0.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        view.startAnimation(mShowAnimation);

    }

    private boolean isRefresh;

    private void callTel() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "4008190660"));
        startActivity(intent);
    }


    private void showRefresh(boolean show) {
//        if(show){
//            pb_refresh.setVisibility(View.VISIBLE);
//            tv_refresh.setVisibility(View.GONE);
//        } else {
//            pb_refresh.setVisibility(View.GONE);
//            tv_refresh.setVisibility(View.VISIBLE);
//        }
//        isRefresh = show;
    }

    private OnBikeBTListener mOnBikeBTListener = new OnBikeBTListener() {
        @Override
        public void biekConnecting() {

            if (mode == MODE_RIDING) {
                return;
            }


            if (!onConn) {
                return;
            }

            if (isConned) {
                return;
            }

            onConn = true;


            String msg = "蓝牙连接中。。。";
            Log.d("event", msg);


            rlSerachFail.setVisibility(View.GONE);
            rlSerach.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.VISIBLE);
            rlConnFail.setVisibility(View.GONE);
        }

        @Override
        public void connectSuccess() {
            if (mode == MODE_RIDING) {
                return;
            }

            onConn = false;

            isConned = true;
            String msg = "蓝牙连接成功。。。";
            Log.d("event", msg);

            rlSerachFail.setVisibility(View.GONE);
            rlSerach.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.VISIBLE);
            rlConnFail.setVisibility(View.GONE);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    iv1.setVisibility(View.GONE);
                    iv2.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.GONE);

                }
            }, 5000);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    setResult(Constant.ACTIVITY_RESULT_CONN_BIKE);
                    finish();

                }
            }, 7000);
        }

        @Override
        public void connectFailure() {
            if (mode == MODE_RIDING) {
                return;
            }
            if (isConned) {
                return;
            }

            String msg = "蓝牙连接失败。。。";
            Log.d("event", msg);


            rlSerachFail.setVisibility(View.VISIBLE);
            rlSerach.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.GONE);
            rlConnFail.setVisibility(View.GONE);
        }

        @Override
        public void communicationFailure() {
            if (mode == MODE_RIDING) {
                return;
            }
            if (isConned) {
                return;
            }

            String msg = "蓝牙通信失败。。。";
            Log.d("event", msg);


            rlSerachFail.setVisibility(View.GONE);
            rlSerach.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.GONE);
            rlConnFail.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_1) {
            rlSerach.setVisibility(View.VISIBLE);
            rlSerachFail.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.GONE);
            rlConnFail.setVisibility(View.GONE);
            startSearch();
        } else if (id == R.id.ll_close) {
            isShow = false;
            finish();

        } else if (id == R.id.btn_2) {
            rlSerach.setVisibility(View.GONE);
            rlSerachFail.setVisibility(View.GONE);
            rlConnFail.setVisibility(View.GONE);
            rlConnBike.setVisibility(View.VISIBLE);

            if (mDevice != null) {
                connBike(mDevice);
            }
        } else if (id == R.id.tv_tel1) {
            callTel();
        } else if (id == R.id.tv_tel2) {
            callTel();
        }

    }
}
