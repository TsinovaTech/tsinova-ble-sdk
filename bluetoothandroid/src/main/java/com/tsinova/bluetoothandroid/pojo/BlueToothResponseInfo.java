package com.tsinova.bluetoothandroid.pojo;

import java.io.Serializable;
import java.util.List;

public class BlueToothResponseInfo implements Serializable {

	private static final long serialVersionUID = -667800553317484066L;

	private List<BikeBlueToothInfo> da; //为app所需功能的数据。
	
	private String su; //此值为1,说明此次发送的数据是无无异常的。
	
//	private String me; //当“su”为0时,说明数据异常,此值为异常值(例如错误代码等)。

	private String md;// 骑行模式
	
	private String ve;// 固件版本
	
	
	
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

	public List<BikeBlueToothInfo> getDa() {
		return da;
	}

	public void setDa(List<BikeBlueToothInfo> da) {
		this.da = da;
	}

	public String getSu() {
		return su==null?"0000000000":su;
	}

	public void setSu(String su) {
		this.su = su;
	}

//	public String getMe() {
//		return me;
//	}
//
//	public void setMe(String me) {
//		this.me = me;
//	}

	@Override
	public String toString() {
		return "BlueToothResponseInfo [da=" + da + ", su=" + su + ", md=" + md + ", ve=" + ve + "]";
	}

	
	
}
