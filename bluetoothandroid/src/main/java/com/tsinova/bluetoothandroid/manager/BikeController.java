package com.tsinova.bluetoothandroid.manage;

public abstract class BikeController {


    /**
     * 第一次启动，或者退出再登录的时候调用
     */
    public abstract void bindServiceAndConnectBLE();

    /**
     * 开始骑行
     */
    public abstract void startDriving();


    /**
     * 结束骑行
     * --快速启动模式初始档位是档2，在快速启动模式下结束骑行还回到档2
     */
    public abstract void endDriving();


    /**
     * 打开或者关闭车灯
     * @param open
     */
    public abstract void openLight(boolean open);

    /**
     * 更改骑行模式(设置中调用，需要后期修改)
     * @param md
     */
    public abstract void setMDToBike(int md);


    /**
     * 更改档位
     */
    public abstract void shiftedGears(int shift);


    /**
     * 是否已经连接
     * @return
     */
    public abstract boolean isConnect();

    /**
     *
     */
    public abstract void release();

}
