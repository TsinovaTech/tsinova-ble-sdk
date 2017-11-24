package com.tsinova.bluetoothandroid.pojo;

import java.io.Serializable;

public class BlueToothRequstInfo implements Serializable {


    private static final long serialVersionUID = -3893283059788527461L;

    private String lt; // 此值为1,代表前灯打开,此值为0,代表前灯关闭。

    public BlueToothRequstInfo() {
    }

    public BlueToothRequstInfo(String lt, String st, String ge, String md, String ve) {
        this.lt = lt;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }


    @Override
    public String toString() {
        return "{\"lt\":\"" + lt + "\"}";
    }
}
