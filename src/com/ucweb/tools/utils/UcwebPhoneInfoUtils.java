package com.ucweb.tools.utils;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/***
 * @author yangruyao
 */

public class UcwebPhoneInfoUtils {
	private final Context mContext;
	
	private static final String UNKNOWN_VALUE = "Unknown";
	
	public UcwebPhoneInfoUtils(Context context) {
		mContext = context;
	}
	
	public static String getPhoneModel(){
		return android.os.Build.MODEL == null ? UNKNOWN_VALUE : android.os.Build.MODEL;
	}
	
	public String getPhoneIMEI() {
		final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		final String imei = tm.getDeviceId();
		
		return imei != null? imei : UNKNOWN_VALUE;
	}
	
	public static String getOsVersion() {
		final String version = android.os.Build.VERSION.RELEASE;
		
		return version != null? version : UNKNOWN_VALUE;
	}
	
	public static HashMap<String, String> getScreenResolution(Activity activity) {
		HashMap<String, String> mData = new HashMap<String, String>(2);
		//已经获取过一次，则从cache中读取
		if (isAlreadyGetPhoneResolution(activity)) {
			int[] resolution = readCache(activity);
			mData.put("KEY", "屏幕分辨率");
			mData.put("VAL", resolution[0] + "×" + resolution[1]);
			return mData;
		} 
		//还未获取，则获取一次并写入cache
		else {
			int[] screenInfo = getPhoneResolution(activity);
			writeCache(screenInfo,activity);
			mData.put("KEY", "屏幕分辨率");
			mData.put("VAL", screenInfo[0] + "×" + screenInfo[1]);
			return mData;
		}
	}
	
	/**
	 * 获取手机分辨率
	 * */
	private static int[] getPhoneResolution(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return new int[] {(int) (dm.heightPixels), (int) (dm.widthPixels)};
	}
	
	/**
	 * 检测是否已获取屏幕分辨率
	 * */
	private static boolean isAlreadyGetPhoneResolution(Activity activity) {
		SharedPreferences config = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
		return config.getBoolean("isAlreadyGetPhoneResolution", false);
	}
	
	/**
	 * 从缓存中读取
	 * */
	private static int[] readCache(Activity activity) {
		SharedPreferences config = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
		return new int[] {
				config.getInt("height", 0),
				config.getInt("width", 0)
		};
	}
	
	/***
	 * 写入本地缓存
	 * @param screen
	 */
	private static void writeCache(int[] screen,Activity activity) {
		
		SharedPreferences config = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
		Editor editor = config.edit();
		
		editor.putBoolean("isAlreadyGetPhoneResolution", true);
		editor.putInt("height", screen[0]);
		editor.putInt("width", screen[1]);
		editor.commit();
	}
}
