package com.tsinova.bluetoothandroid.util;


import android.util.Log;

import com.tsinova.bluetoothandroid.common.Constant;

public class CommonUtils {
	/**
	 * 输出日志<br>
	 * 默认的日志级别是Log.Info
	 * 
	 * @param context
	 * @param text
	 */
	public static void log(String text) {
		log(Constant.LOG_TAG, text, Log.INFO);
	}

	/**
	 * 输出日志<br>
	 * 
	 * @param text
	 *            日志内容
	 * @param MODE
	 *            输出的模式<br>
	 *            (Log.VERBOSE Log.DEBUG Log.INFO Log.WARN Log.ERROR)
	 */
	public static void log(String text, int MODE) {
		log(Constant.LOG_TAG, text, MODE);
	}
	
	
	public static void log(String tag, String text) {
		log(tag, text, Log.INFO);
	}

	/**
	 * 输出日志<br>
	 * 
	 * @param tag
	 *            日志标签
	 * @param text
	 *            日志内容
	 * @param MODE
	 *            输出的模式<br>
	 *            (Log.VERBOSE Log.DEBUG Log.INFO Log.WARN Log.ERROR)
	 */
	public static void log(String tag, String text, int MODE) {
		if (Constant.isShowLog) {// 是否显示日志
			switch (MODE) {
			case Log.VERBOSE:
				Log.v(tag, text);
				break;
			case Log.DEBUG:
				Log.d(tag, text);
				break;
			case Log.INFO:
				Log.i(tag, text);
				break;
			case Log.WARN:
				Log.w(tag, text);
				break;
			case Log.ERROR:
				Log.e(tag, text);
				break;
			}
		}
	}
}
