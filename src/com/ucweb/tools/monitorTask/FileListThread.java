package com.ucweb.tools.monitorTask;

import com.ucweb.tools.activity.OperationActivity;
import com.ucweb.tools.utils.UcwebNetUtils;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;


public class FileListThread implements Runnable{

	private String url = null;
	private int type;
	
	public FileListThread(String url,int type){
		this.url = url;
		this.type = type;
	}
	
	@Override
	public void run() {
		doGetRequire();
	}
	
	private void doGetRequire(){
		Message msg = new Message();
		Bundle bundle = new Bundle();
		Log.d("Tag",this.url);
		if(UcwebNetUtils.decodeJSON(UcwebNetUtils.doGet
				(this.url), this.type) != null){
			OperationActivity.FIlENAMELIST = UcwebNetUtils.decodeJSON(UcwebNetUtils.doGet
					(this.url), this.type);
			bundle.putString("msg", "getdata_success");
			msg.setData(bundle);
			OperationActivity.mHandler.sendMessage(msg);
		}else{
			bundle.putString("msg", "getdata_failed");
			msg.setData(bundle);
			OperationActivity.mHandler.sendMessage(msg);
		}
	}

}
