package com.ucweb.tools.monitorTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

import com.ucweb.tools.config.Config;

public class MonitorCheckAppStatu implements Runnable{
	
	private final String[] cmds = {"top", "-m", "5", "-n", "1"};
	
	@Override
	public void run() {
		doMonitorCheck();
	}
	
	private boolean isAppActivities(){
		ActivityManager mActivityManager = (ActivityManager)Config.context.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<ActivityManager.RunningAppProcessInfo> processList = mActivityManager.getRunningAppProcesses();
		
		for(ActivityManager.RunningAppProcessInfo processApp : processList){
			if(processApp.processName.equals(Config.pkgName)){
				return true;
			}
		}
		return false;
	}
	
	private final void doMonitorCheck() {
		
		InputStream is = null;
		BufferedReader br = null;
		Process process = null;
		Runtime runTime = Runtime.getRuntime();
		
		String temp = null;
		try {
			process = runTime.exec(cmds);
			is = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			while((temp = br.readLine()) != null){
				if(temp.contains(Config.pkgName)){
					Config.appState = true;
					break;
				}else{
					continue;
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			
		}finally{
			process.destroy();
			try {
				is.close();
				br.close();
				Thread.sleep(2000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(Config.appState || isAppActivities()){
				MonitorGScript monitorGScript = new MonitorGScript.Builder(Config.context).
						setResolution(Config.resolution).setRTimes(100).build();
				new Thread(monitorGScript).start();
			}else{
				new Thread(new MonitorRStartApp()).start();
			}
		}
	}
}
