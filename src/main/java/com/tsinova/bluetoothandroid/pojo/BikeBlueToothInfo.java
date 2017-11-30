package com.tsinova.bluetoothandroid.pojo;

import android.text.TextUtils;

import java.io.Serializable;

public class BikeBlueToothInfo implements Serializable {

	private static final long serialVersionUID = 7346698628279512508L;

	private int be; // 电池电量
	
	private String sp; //当前速度
	
	private String lt; // 此值为1,代表前灯打开,此值为0,代表前灯关闭。
	
	private String st; // 此值为1,代表车辆正在行行驶,此值为0,代表车辆停止止运行。
	
	private int ge; // 当前挡位。此值为 1,代表高档,此值为 0,代表低档。

	private int po; // 当前功率

	public int getPo() {
		return po;
	}

	public void setPo(int po) {
		this.po = po;
	}

	public int getBe() {
		return be;
	}

	public void setBe(int be) {
		this.be = be;
	}

	public int getIntSp(){
		int speed = 0;
		try{
			if(!TextUtils.isEmpty(sp)){
				speed = Integer.valueOf(sp);
			}
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return speed;
	}
	
	/**
	 * 速度做除10处理
	 */
	public double getDoubleSp() {
		double speed = 0.0d;
		try{
			if(!TextUtils.isEmpty(sp)){
				speed = Double.valueOf(sp);
			}
		}catch (NumberFormatException e){
			e.printStackTrace();
		}
		return speed / 10d;
	}

	public void setSp(String sp) {
		this.sp = sp;
	}

	public String getLt() {
		return lt;
	}

	public void setLt(String lt) {
		this.lt = lt;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public int getGe() {
		return ge;
	}

	public void setGe(int ge) {
		this.ge = ge;
	}

	@Override
	public String toString() {
		return "[be=" + be + ", sp=" + sp + ", lt=" + lt
				+ ", st=" + st + ", ge=" + ge + "]";
	}
	
	
}
