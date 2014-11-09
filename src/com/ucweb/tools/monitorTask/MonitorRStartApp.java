package com.ucweb.tools.monitorTask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.ucweb.tools.config.Config;
import com.ucweb.tools.utils.UcwebAppUtil;

public class MonitorRStartApp implements Runnable{

	@Override
	public void run() {
		Config.crashCount += 1;
		
		if(isNETAvailable()){
			reStartApp();
		}
	}
	
	private void reStartApp(){
		UcwebAppUtil apputil = new UcwebAppUtil(Config.context);
		apputil.startAppAndGetPid(Config.pkgName);
		
		MonitorGScript monitorGScript = new MonitorGScript.Builder(Config.context).
				setResolution(Config.resolution).setRTimes(100).build();
		new Thread(monitorGScript).start();
	}
	
	private boolean isNETAvailable(){
		ConnectivityManager connectivityManager = (ConnectivityManager)
				Config.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		//mobile 3G Data Network
		State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		//mobile wifi Network
		State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if(mobile.toString().equals("DISCONNECTED") && wifi.toString().equals("DISCONNECTED")){
			Config.networkState = "network disconnected";
			return false;
		}
		return true;
	}
	
}
