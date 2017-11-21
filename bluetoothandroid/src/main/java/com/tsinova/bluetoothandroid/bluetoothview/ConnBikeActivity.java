package com.tsinova.bluetoothandroid.bluetoothview;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tsinova.bluetoothandroid.R;
import com.tsinova.bluetoothandroid.bluetooth.BikeBlueToothManager;
import com.tsinova.bluetoothandroid.bluetooth.BikeLeScanCallback;
import com.tsinova.bluetoothandroid.manager.BikeControlManager;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ihgoo on 2017/4/14.
 */

public class ConnBikeActivity extends FragmentActivity {

    private int mode = MODE_SERACH;
    public static int MODE_RIDING = 1;
    public static int MODE_SERACH = 0;
    public static boolean isShow = false;


    /**
     * 连接成功过为true
     */
    private boolean isConned;

    private BikeBlueToothManager mManager;
    private static BikeControlManager mBikeControlManager;
    private EventBus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setTheme(R.style.transparent);
        setContentView(R.layout.activity_blue_conn);
        ButterKnife.bind(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);


        initCarImage();

        startAnim();

        mManager = BikeBlueToothManager.getInstant(this);
        mManager.setLeScanCallback(mLeScanCallback);
        Log.d("event", "蓝牙搜索中。。。");


        mBikeControlManager = BikeControlManager.getBikeControlManager(this);


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
        String carImageUrl = BikePreferencesUtils.getCarImageUrl(this);
        String carBrandImageUrl = BikePreferencesUtils.getCarBrandImageUrl(this);

        ImageLoader.getInstance().displayImage(carImageUrl, ivBike);
        ImageLoader.getInstance().displayImage(carBrandImageUrl, ivLogo);
    }


    private boolean onConn = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnBlueToothEvent(ConnBlueToothEvent event) {

        if (mode == MODE_RIDING) {
            return;
        }


        if (onConn) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnSuccuessBlueToothEvent(ConnedEvent event) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnFailBlueToothEvent(ConnFailEvent event) {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContactFailEvent(ContactFailEvent event) {
        if (mode == MODE_RIDING) {
            return;
        }
        if (isConned) {
            return;
        }

        String msg = "蓝牙通讯失败。。。";
        Log.d("event", msg);


        rlSerachFail.setVisibility(View.GONE);
        rlSerach.setVisibility(View.GONE);
        rlConnBike.setVisibility(View.GONE);
        rlConnFail.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
        eventBus.unregister(this);

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



    //    private int i;
    private BikeLeScanCallback mLeScanCallback = new BikeLeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            if (device == null) {
                return;
            }
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
                        if (device.getName().equals(AppParams.getInstance().getCarInfo().getCarBluetoothNumber())) {

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

            if (eventBus != null) {
                eventBus.post(new ConnFailEvent());
            }

            showRefresh(false);
        }
    };

    public void connBike(BluetoothDevice bt) {
        stopSearch();
//        if(AppParams.getInstance().getUser() == null){
//        	return;
//        }

        CommonUtils.log("BikeBlueToothListActivity ---> onItemClick, bt.name :" + bt.getName() + " / getCarBluetoothNumber : " + BikePreferencesUtils.getCarBluetoothNumber(this));
        if (bt.getName() == null || !bt.getName().equals(BikePreferencesUtils.getCarBluetoothNumber(this))) {
            UIUtils.toastFalse(ConnBikeActivity.this, R.string.btlist_toast_tip_bt_num_wrong);
        } else {
            mManager.setCurrentDevice(bt);
            Intent data = new Intent();
            Intent mIntent = getIntent();
            String activityName = mIntent.getStringExtra("activity");
            if (activityName != null && activityName.startsWith("com.tsinova.bike.activity.BikeActivity")) {
                data.setClass(ConnBikeActivity.this, BikeActivity.class);
            } else {
                data.setClass(ConnBikeActivity.this, HomeActivity.class);
            }


            String address = bt.getAddress();
            String name = bt.getName();
            AppParams.getInstance().setBTAddress(address);
            AppParams.getInstance().setBTName(name);
            CommonUtils.log("ConnBikeActivity -----> ble address : " + address + " / name : " + name);
            if (mBikeControlManager != null && !TextUtils.isEmpty(address)) {
                mBikeControlManager.connect(address);
            }

            setHideAnimation(llSerachBluetooth, 1500);


//            ImageLoader.getInstance().displayImage(BikePreferencesUtils.getCarImageUrl(this), ivBikePic);

//            data.putExtra("address", bt.getAddress());
//            data.putExtra("name", bt.getName());
//            setResult(Constant.ACTIVITY_REQUEST_CODE_SCAN_BLE, data);
//            finish();


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

    @OnClick({R.id.btn_1, R.id.ll_close,R.id.tv_tel1,R.id.tv_tel2,R.id.btn_2})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                rlSerach.setVisibility(View.VISIBLE);
                rlSerachFail.setVisibility(View.GONE);
                rlConnBike.setVisibility(View.GONE);
                rlConnFail.setVisibility(View.GONE);
                startSearch();

                break;
            case R.id.ll_close:
                isShow = false;
                finish();

                break;

            case R.id.btn_2:
                rlSerach.setVisibility(View.GONE);
                rlSerachFail.setVisibility(View.GONE);
                rlConnFail.setVisibility(View.GONE);
                rlConnBike.setVisibility(View.VISIBLE);

                if (mDevice!=null){
                    connBike(mDevice);
                }

                break;

            case R.id.tv_tel1:
                callTel();

                break;
            case R.id.tv_tel2:
                callTel();
                break;


            default:
                break;
        }
    }

    private void callTel() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "4008190660"));
        startActivity(intent);
    }

}
