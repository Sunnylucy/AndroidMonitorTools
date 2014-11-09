package com.ucweb.tools.config;

import android.content.Context;

public class Config {
	/**PerformanceTestFile上传URL*/
	public static final String PERFORMANCEFILE_UPLOAD_URL = "http://an.test2.game.uc.cn:28043/Analysis/UploadForWebServlet";
	
	/**MonkeyScriptFile上传URL*/
	public static final String MONKEYSCRIPT_UPLOAD_URL = "http://an.test2.game.uc.cn:28043/Analysis/monkey/UploadServlet";
	
	public static final String UPDATE_URL = "http://an.test2.game.uc.cn:28043/Analysis/uploadsApp/CheckVersion.jsp";
	
	/**线程池大小*/
	public static final int MAX_THREAD_POOL_SIZE = 10;
	
	/**获取Monkey脚本列表的URL*/
	public static final String GET_MONKEY_SCRIPT_LIST_URL = "http://an.test2.game.uc.cn:28043/Analysis/monkey/MonkeyServlet";
	
	/**获取Monkey脚本URL*/
	public static final String GET_MONKEY_SCRIPT_URL = "http://an.test2.game.uc.cn:28043/Analysis/monkey/MonkeyServlet";
	
	
	
	public static final String PROTYPE = "typeName";
	
	public static final String FILETYPE = "fileName";
	
	/**Monkey脚本名字*/
	public static final String MONKEY_SCRIPT_FILE_NAME = "monkey_script.txt";
	
	/**btnStopTest statu*/
	public static boolean btnStopTestStatu = false;
	//屏幕分辩率
	public static int[] resolution = null;
	//app package name
	public static String pkgName = null;
	//monkeyrandomtest setting times
	public static int rTimes = 0;
	//monkeyrandomtest run times
	public static int rRTimes = 0;
	//monkeyrandomtest result
	public static final String mRTResultFile = "mrt_result.txt";
	
	//global context
	public static Context context = null;
	//app state
	public static boolean appState = false;
	//crash count
	public static int crashCount = 0;
	//network state
	public static String networkState = "";
	
	//BatterInfo
	public static String BatteryInfo = "";
	//IOWInfo
	public static String IOWInfo = "";
	//MonitorInfo
	public static String MonitorInfo = "";
	//NetInfo
	public static String NetInfo = "";
	
}
