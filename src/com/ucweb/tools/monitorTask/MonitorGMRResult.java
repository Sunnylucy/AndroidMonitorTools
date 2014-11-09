package com.ucweb.tools.monitorTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ucweb.tools.config.Config;
import com.ucweb.tools.utils.UcwebDateUtil;
import com.ucweb.tools.utils.UcwebFileUtils;

public class MonitorGMRResult implements Runnable{
	
	private SimpleDateFormat format = UcwebDateUtil.YMDDateFormat.getYMDFormat();
	UcwebFileUtils ucwebFileUtils = new UcwebFileUtils(Config.context);
	@Override
	public void run() {
		try {
			ucwebFileUtils.writeFile(gResultFilePath(), gResult());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private StringBuffer gResult(){
		StringBuffer sb = new StringBuffer();
		sb.append("测试执行时间:" + format.format(new Date()));
		sb.append("Monkey随机测试执行组数:"+ Config.rTimes + " (ps:一组为100测点击操作)");
		sb.append("Monkey随机测试实际执行组数:"+ Config.rRTimes + " (ps:一组为100测点击操作)");
		if(Config.rRTimes > Config.rTimes ){
			sb.append("测试中断原因:" + "正常终止");
		}else if(Config.networkState.equals("network disconnected")){
			sb.append("测试中断原因:" + "network disconnected");
		}
		return sb;
	}
	
	private String gResultFilePath(){
		
		return ucwebFileUtils.generateFilePath() + Config.mRTResultFile;
	}
}
