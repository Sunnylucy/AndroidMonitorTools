package com.ucweb.tools.service;

import java.util.concurrent.ExecutorService;

import com.ucweb.tools.config.Config;
import com.ucweb.tools.monitorTask.BatterMonitor;
import com.ucweb.tools.monitorTask.CpuMemMonitor;
import com.ucweb.tools.monitorTask.IOWMonitor;
import com.ucweb.tools.monitorTask.MonitorProxy;
import com.ucweb.tools.monitorTask.NetMonitor;
import com.ucweb.tools.utils.MonitorType;
import com.ucweb.tools.utils.UcwebFWindow;
import com.ucweb.tools.utils.UcwebThreadPoolsManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MonitorService extends Service{
	
	private final UcwebThreadPoolsManager manager = UcwebThreadPoolsManager.getThreadPoolManager();
	private final ExecutorService executor = manager.getExecutorService();
	
	private MonitorProxy mCPUMEMProxy = null;
	private MonitorProxy mIOWProxy = null;
	private MonitorProxy mBATTERYProxy = null;
	private MonitorProxy mNETProxy = null;
			
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		
		final int monkeyTestType = intent.getIntExtra("MonkeyTestType",-1);
		Config.rTimes = intent.getIntExtra("TIMES",1);
		String resolutionData = intent.getStringExtra("RESOLUTION");
		
		Config.context = getApplicationContext();
		Config.pkgName = intent.getStringExtra("pkgName");
		final String fileSavePath = intent.getStringExtra("file path");
		
		if(monkeyTestType > 0){
			String[] resolutionStr = resolutionData.trim().split("×");
			Config.resolution = new int[]{Integer.parseInt(resolutionStr[1]),Integer.parseInt(resolutionStr[0])};
			
			UcwebFWindow ucwebFWindow = new UcwebFWindow.Builder(getApplicationContext())
			.setPKGName(Config.pkgName).setResolution(Config.resolution).setStatu(monkeyTestType)
			.setTimes(Config.rTimes).build();
			
			if(ucwebFWindow != null){
				
//				MonitorGScript monitorGScript = new MonitorGScript.Builder(getApplicationContext()).
//						setResolution(Config.resolution).setRTimes(100).build();
//					new Thread(monitorGScript).start();
					
				ucwebFWindow.createFWindow();
			}
		}
		
		if(MonitorType.MONITOR_FLAG_CPUMEM){
			CpuMemMonitor cpuMemMonitor = new CpuMemMonitor.Builder(getApplicationContext()).
					setMonitorIntervalSeconds(5).
					setMonitorPkg(Config.pkgName).
					setFileSavePath(fileSavePath).
					build();
			mCPUMEMProxy = new MonitorProxy(cpuMemMonitor);
			executor.execute(new Runnable() {

				@Override
				public void run() {
					mCPUMEMProxy.start();
				}
			});
		}
		
		if(MonitorType.MONITOR_FLAG_IOW){
			IOWMonitor iowMonitor = new IOWMonitor(getApplicationContext(), fileSavePath, Config.pkgName);
			mIOWProxy = new MonitorProxy(iowMonitor);
			executor.execute(new Runnable() {
					
				@Override
				public void run() {
					mIOWProxy.start();
				}
			});
		}
		
		if(MonitorType.MONITOR_FLAG_BATTERY){
			BatterMonitor bm = new BatterMonitor(getApplicationContext(), fileSavePath, Config.pkgName);
			mBATTERYProxy = new MonitorProxy(bm);
			mBATTERYProxy.start();
		}
		
		if(MonitorType.MONITOR_FLAG_NET){
			NetMonitor netMonitor = new NetMonitor(getApplicationContext(), fileSavePath, Config.pkgName);
			mNETProxy = new MonitorProxy(netMonitor);
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					mNETProxy.start();
				}
			});
		}
		super.onStart(intent, startId);	
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	 
	@Override
	public void onDestroy(){
		releaseResource();
		super.onDestroy();
	}
	
	private void releaseResource(){
		if(mCPUMEMProxy != null){
			mCPUMEMProxy.stop();
		}
		if(mIOWProxy != null){
			mIOWProxy.stop();
		}
		if(mBATTERYProxy != null){
			mBATTERYProxy.stop();
		}
		if(mNETProxy != null){
			mNETProxy.stop();
		}
	}
}
