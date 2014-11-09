package com.ucweb.tools.monitorTask;

import java.io.DataOutputStream;
import java.io.IOException;

public class MonitorExecuteMonkeyScript implements Runnable{
	private String filePath = null;
	private int times = 1;
	
	public MonitorExecuteMonkeyScript(String filepath,int times){
		this.filePath = filepath;
		this.times = times;
	}
	
	@Override
	public void run() {
		executeScript(this.filePath,this.times);
	}

	private void executeScript(String filePath,int times){
		Process mProcess = null;
		DataOutputStream mDataOutputStream = null;
		Runtime runtime = Runtime.getRuntime();
		try {
			mProcess = runtime.exec("su");
			mDataOutputStream = new DataOutputStream(mProcess.getOutputStream());
			mDataOutputStream.writeBytes("monkey -f " + filePath + " -v " + times + "\n");
			mDataOutputStream.flush();
			mDataOutputStream.writeBytes("exit\n");
			mDataOutputStream.flush();
			mProcess.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			if(mDataOutputStream != null){
				try {
					mDataOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mProcess.destroy();
			new Thread(new MonitorCheckAppStatu()).start();
		}
	}
}
