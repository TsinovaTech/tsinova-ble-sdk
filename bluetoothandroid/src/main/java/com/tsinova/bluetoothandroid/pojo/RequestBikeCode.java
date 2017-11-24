package com.tsinova.bluetoothandroid.pojo;

/**
 * 蓝牙容错
 * Created by ihgoo on 2017/11/23.
 */

public class RequestBikeCode {
    private String bike_no;
    private String error;
    private String app;

    public String getBike_no() {
        return bike_no;
    }

    public void setBike_no(String bike_no) {
        this.bike_no = bike_no;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
