package com.ucweb.tools.monitorTask;

import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class MonkeyRandomTest implements Runnable{
	
	private String pkgName = null;
	
	private int times = 100;
	
	public MonkeyRandomTest(String pkgName,int times) {
		this.pkgName = pkgName;
		this.times = times;
	}
	
	@Override
	public void run() {
		startMonkeyRandomTest(pkgName, times);
	}
	
	private void startMonkeyRandomTest(String pkgName,int times){
		
		DataOutputStream mDataOutputStream = null;
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		Log.d("Tag","monkey -p " + pkgName + " -v " + times);
		try {
			process = runtime.exec("su");
			mDataOutputStream = new DataOutputStream(process.getOutputStream());
			mDataOutputStream.writeBytes("monkey -p "+ pkgName + " -v " + times + "\n");
			
			mDataOutputStream.flush();
			process.waitFor();
			mDataOutputStream.writeBytes("exit\n");
		} catch (IOException e) {
			Log.d("Tag",e.getMessage().toString());
		} catch (InterruptedException e) {
			Log.d("Tag",e.getMessage().toString());
		}finally{
			process.destroy();
			try {
				mDataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
