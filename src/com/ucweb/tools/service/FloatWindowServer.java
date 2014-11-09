package com.ucweb.tools.service;

import com.ucweb.tools.utils.UcwebFWindow;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class FloatWindowServer extends Service{
	
	private static int statu = 0;
	private static String pkgName = null;
	private static int times = 0;
	private static int[] resolution = null;
	
	private static Intent intent = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			this.intent = intent;
			Bundle bundle = this.intent.getExtras();
			statu = bundle.getInt("STATU");
			pkgName = bundle.getString("PKGNAME");
			times = bundle.getInt("TIMES");
			resolution = bundle.getIntArray("RESOLUTION");
		}
		
		Handler handler = new Handler();
		handler.post(new Runnable(){

			@Override
			public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					UcwebFWindow ucwebFWindow = new UcwebFWindow.Builder(getApplicationContext())
								.setPKGName(pkgName).setResolution(resolution).setStatu(statu).setTimes(times).build();
					
					ucwebFWindow.createFWindow();
					
//					WindowManagerTools mWindowManager = new WindowManagerTools();
//					mWindowManager.createFloatWindow(getApplicationContext(),statu,pkgName,times,resolution);
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
