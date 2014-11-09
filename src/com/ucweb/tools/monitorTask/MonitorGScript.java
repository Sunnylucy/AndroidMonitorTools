package com.ucweb.tools.monitorTask;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.ucweb.tools.config.Config;
import com.ucweb.tools.service.MonitorService;
import com.ucweb.tools.utils.MonitorType;
import com.ucweb.tools.utils.UcwebFileUtils;

public class MonitorGScript implements Runnable{
	
	private static final int SCREEN_HORIZONTAL = 1;
	private static final int SCREEN_VERTICAL = 2;
	
	private static final String mMonkeyScriptHeader = "#Start Script\n" + "type= raw events\n" + "count= 8\n"
			+"speed= 1.0\n" + "start data >>\n";
	private static final int interval = 10; 
	private int[] resolution = {480,800};
	private int times = 0;
	private Context context = null;
//	private static int screenStatu = -1;
	
	private Random random = new Random();
	private StringBuffer monkeyScript = new StringBuffer();
	
	public MonitorGScript(Builder builder){
		this.context = builder.context;
		resolution = builder.resolution;
		times = builder.rTimes;
		
		monkeyScript.append(mMonkeyScriptHeader);
		gScript(1);
	}
	
	private int[] gCoordinate(int[] resolution){
		return new int[]{random.nextInt(resolution[0]),random.nextInt(resolution[1])};
	}
	
	private int gSleepTime(int interval){
		return random.nextInt(interval);
	}
	
	private void gScript(int screenStatu){
		int[] coordinate = null;
		int waittime = 0;
		
		switch (screenStatu) {
		case SCREEN_HORIZONTAL:
			
			for(int i=0;i<times;i++){
				coordinate = gCoordinate(resolution);
				waittime = gSleepTime(interval);
				
				monkeyScript.append("captureDispatchPointer(1,1,0," + coordinate[1] + ","
						+ coordinate[0] + ",1,0,0,0,0,1,0);\n");
				monkeyScript.append("captureDispatchPointer(1,1,1," + coordinate[1] + ","
						+ coordinate[0] + ",1,0,0,0,0,1,0);\n");
				
				monkeyScript.append("UserWait("+ waittime*100 + ");" +"\n");
			}
			break;
			
		case SCREEN_VERTICAL:
			
			for(int i=0;i<times;i++){
				coordinate = gCoordinate(resolution);
				waittime = gSleepTime(interval);
				
				monkeyScript.append("captureDispatchPointer(1,1,0," + coordinate[0] + ","
						+ coordinate[1] + ",1,0,0,0,0,1,0);\n");
				monkeyScript.append("captureDispatchPointer(1,1,1," + coordinate[0] + ","
						+ coordinate[1] + ",1,0,0,0,0,1,0);\n");
				
				monkeyScript.append("UserWait("+ waittime*100 + ");" +"\n");
			}
			break;
			
		default:
			break;
		}
	}
	
	private void writeScript2Storage(){
		UcwebFileUtils fileUtil = new UcwebFileUtils(this.context);
		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + Config.MONKEY_SCRIPT_FILE_NAME;
		MonitorType.Monkey_RScript_SavePath = fullPath;
		try {
			monkeyScript.append("\nquit");
			fileUtil.writeMScriptFile(fullPath, monkeyScript);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		writeScript2Storage();
		runMonkeyRScript();
	}
	
	private void runMonkeyRScript(){
		if(Config.rRTimes <= Config.rTimes && Config.appState){
			if(!MonitorType.Monkey_RScript_SavePath.equals("")){
				MonitorExecuteMonkeyScript monitorExecuteMonkeyRScript = new MonitorExecuteMonkeyScript(
						MonitorType.Monkey_RScript_SavePath, 1);
				new Thread(monitorExecuteMonkeyRScript).start();
			}
			Config.rRTimes += 1;
		}else{
			Config.context.stopService(new Intent(Config.context, MonitorService.class));
//			List<String> uploadFList = new ArrayList<String>(5);
//			uploadFList.add(Config.BatteryInfo);
//			uploadFList.add(Config.IOWInfo);
//			uploadFList.add(Config.MonitorInfo);
//			uploadFList.add(Config.NetInfo);
////			uploadFList.add(Config.mRTResultFile);
//			UcwebNetUtils ucwebNetUtils = new UcwebNetUtils();
//			ucwebNetUtils.doUpload(Config.context,uploadFList);
		}
	}
	
	public static class Builder{
		private Context context = null;
		private int[] resolution = null;
		private int rTimes = 0;
		
		public Builder(Context context){
			this.context = context;
		}
		
		public Builder setResolution(int[] resolution){
			this.resolution = resolution;
			return this;
		}
		
		public Builder setRTimes(int times){
			this.rTimes = times;
			return this;
		}
		
		public MonitorGScript build(){
			return new MonitorGScript(this);
		}
	}
}
