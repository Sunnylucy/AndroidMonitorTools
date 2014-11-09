package com.ucweb.tools.monitorTask;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Intent;

import com.ucweb.tools.config.Config;
import com.ucweb.tools.context.UcwebContext;
import com.ucweb.tools.service.MonitorService;

public class ESIGNINScript implements Runnable{
	
	private String fileFullPath = null;
	
	public ESIGNINScript(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}
	
	@Override
	public void run() {
		excMScript(this.fileFullPath);
	}
	
	private void excMScript(String fileFullPath){
		if(fileFullPath == null){
			return;
		}
		
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		DataOutputStream mDataOutputStream = null;
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		try {
			process = runtime.exec("su");
			
			mDataOutputStream = new DataOutputStream(process.getOutputStream());
			mDataOutputStream.writeBytes("monkey -f " + fileFullPath + " -v " + 1 + "\n");
			mDataOutputStream.flush();
			mDataOutputStream.writeBytes("exit\n");
			mDataOutputStream.flush();
			process.waitFor();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			process.destroy();
			try {
				mDataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			new Thread(new MonitorCheckAppStatu()).start();
			
			startMonitorService();
		}
	}
	
	
	private void startMonitorService(){
		Intent intent = new Intent();
		intent.setClass(Config.context, MonitorService.class);
		intent.putExtra("pkgName", Config.pkgName);
		intent.putExtra("MonkeyTestType",0);
		
		UcwebContext env = UcwebContext.getContext(Config.context);
		final String fileWritePath = env.getFileSavePath();
		
		intent.putExtra("file path", fileWritePath);
		
		Config.context.startService(intent);
	}
}
