package com.tsinova.bluetoothandroid.util;


import com.getkeepsafe.relinker.ReLinker;
import com.tsinova.bluetoothandroid.pojo.SingletonBTInfo;


public class DESPlus {

	static {
//		System.loadLibrary("native-des");
		ReLinker.loadLibrary(SingletonBTInfo.INSTANCE.getApplicationContext(), "native-des");
	}
	private static DESPlus mInstant;
	
	private DESPlus(){};
	
	public static DESPlus getInstant(){
		if(mInstant == null){
			mInstant = new DESPlus();
		}
		return mInstant;
	}
	
	public native byte[] encryptDES(String data, String key);
	
	public native byte[] decryptDES(byte[] data, String key);
	
}
