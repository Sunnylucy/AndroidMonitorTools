package com.ucweb.tools.monitorTask;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.ucweb.tools.activity.OperationActivity;
import com.ucweb.tools.utils.UcwebFileUtils;
import com.ucweb.tools.utils.UcwebNetUtils;

public class MonitorScriptDetails implements Runnable{
	
	private String url = null;
	private Context context = null;
	private String filePath = null;
	
	public MonitorScriptDetails(Context context,String filePath ,String url){
		this.url = url;
		this.context = context;
		this.filePath = filePath;
	}
	
	@Override
	public void run() {
		doGetRequire();
	}
	
	private void doGetRequire(){
		Message msg = new Message();
		Bundle bundle = new Bundle();
		InputStream ins = null;

		if((ins = UcwebNetUtils.doGet(this.url)) != null){
			try {
				UcwebFileUtils.writeInfoTophone(context, ins, filePath);
				bundle.putString("msg", "download_success");
				msg.setData(bundle);
				OperationActivity.mHandler.sendMessage(msg);
			} catch (IOException e) {
				bundle.putString("msg", e.getMessage().toString());
				msg.setData(bundle);
				OperationActivity.mHandler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
}
