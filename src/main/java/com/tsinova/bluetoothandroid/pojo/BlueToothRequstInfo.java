package com.tsinova.bluetoothandroid.pojo;

import java.io.Serializable;

public class BlueToothRequstInfo implements Serializable {

	
	private static final long serialVersionUID = -3893283059788527461L;

	private String lt; // 此值为1,代表前灯打开,此值为0,代表前灯关闭。
	
	private String st; // 此值为1,代表车辆正在行行驶,此值为0,代表车辆停止止运行。
	
	private String ge; // 当前挡位。此值为 1,代表高档,此值为 0,代表低档。
	
	private String md; // 此值为"0",代表正常模式;此值为"1",代表快速启动模式。
	
	private String ve; // 此值为当前蓝⽛牙固件版本号,为两位。

	public BlueToothRequstInfo(){}
	
	public BlueToothRequstInfo(String lt, String st, String ge, String md, String ve){
		this.lt = lt;
		this.st = st;
		this.ge = ge;
		this.md = md;
		this.ve = ve;
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

	public String getGe() {
		return ge;
	}

	public void setGe(String ge) {
		this.ge = ge;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getVe() {
		return ve;
	}

	public void setVe(String ve) {
		this.ve = ve;
	}


	@Override
	public String toString() {
		return "BlueToothRequstInfo{" +
				"lt='" + lt + '\'' +
				", st='" + st + '\'' +
				", ge='" + ge + '\'' +
				", md='" + md + '\'' +
				", ve='" + ve + '\'' +
				'}';
	}
}
