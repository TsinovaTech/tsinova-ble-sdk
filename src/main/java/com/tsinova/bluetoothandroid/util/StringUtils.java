package com.tsinova.bluetoothandroid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private static final String KEY = "";
	/**
	 * 获取蓝牙信息的加密key
	 * @param btName
	 * @return
	 */
	public static String getBikeKey(String btName) {
		if (TextUtils.isEmpty(btName)) {
			return "";
		}
		String btKey = btName.substring(btName.length() - 8, btName.length());
		return btKey + KEY;
	}
	
	/**
	 * 去掉解密后多余的字符
	 */
	public static String fromateJson(String json){
		String str;
		int last = json.lastIndexOf("}");
		if(last > 0){
			str = json.substring(0, last + 1);
//			if(StringUtils.isJson(str)){
				return str;
//			}
		}

		return null;
	}

	/**
	 * 判断是否为完整的json类型
	 * (简单判断--通过花括号个数判断)
	 */
	public static boolean isJson(String json) {
		int left = -1;
		if (!TextUtils.isEmpty(json) && json.startsWith("{")) {
			char[] strs = json.toCharArray();
			left = 0;
			for (char str: strs) {
				if(String.valueOf(str).equals("{")) {
					left++;
				} else if (String.valueOf(str).equals("}")) {
					left--;
				}
			}
		}
		return left == 0;
	}
}
