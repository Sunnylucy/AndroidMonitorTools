package com.ucweb.tools.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ucweb.tools.R;
import com.ucweb.tools.context.UcwebContext;
import com.ucweb.tools.monitorTask.MonitorCheckAppStatu;
import com.ucweb.tools.monitorTask.MonitorExecuteMonkeyScript;
import com.ucweb.tools.monitorTask.RecordScriptThread;
import com.ucweb.tools.service.FloatWindowServer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class UcwebFWindow extends View{
	
	@SuppressWarnings("unused")
	private int times = 0;
	private int statu = 0;
	private String pkgName = null;
	private Context context = null;
	private int[] resolution = null;
	
	private UcwebFWindow(Builder builder){
		super(builder.context);
		Log.d("Tag", "haha");
		this.context = builder.context;
		this.statu = builder.statu;
		this.resolution = builder.resolution;
		this.pkgName = builder.pkgName;
		this.times = builder.times;
	}
	private static final int CPUMEMINFO = 1;
	private static final int IOWINFO = 2;
	private static final int BATTERYINFO = 3;
	private static final int NETINFO = 4;
	
	private View floatingWindow;
	private ImageView imageView = null;
	private TextView sCpuMem = null;
	private TextView sIOW = null;
	private TextView sBattery = null;
	private TextView sNet = null;
	private WindowManager windowManager = null;
	private WindowManager.LayoutParams wmParams = null;
	private float startX;
	private float startY;
	private float endX;
	private float endY;
	
	private int Record_Monkey_Script_Statu = 1;
	private RecordScriptThread recordScriptThread = null;
	
	private SimpleDateFormat format = UcwebDateUtil.YMDDateFormat.getYMDFormat();
	
	@SuppressLint("InflateParams")
	public void createFWindow(){
		if(floatingWindow == null){
			UcwebFWindow.this.windowManager = ((WindowManager)context.getSystemService("window"));
			UcwebFWindow.this.floatingWindow = LayoutInflater.from(context).inflate(R.layout.float_window, null);
			UcwebFWindow.this.imageView = (ImageView)floatingWindow.findViewById(R.id.FloatWindowImageButton);
			UcwebFWindow.this.sCpuMem = (TextView)floatingWindow.findViewById(R.id.FloatWindowCpuMem);
			UcwebFWindow.this.sIOW = (TextView)floatingWindow.findViewById(R.id.FloatWindowIOW);
			UcwebFWindow.this.sBattery = (TextView)floatingWindow.findViewById(R.id.FloatWindowBattery);
			UcwebFWindow.this.sNet = (TextView)floatingWindow.findViewById(R.id.FloatWindowNet);
			setInVisible();
			rotateImage(R.drawable.play);
			UcwebFWindow.this.wmParams = new LayoutParams();
			UcwebFWindow.this.wmParams.type = LayoutParams.TYPE_PHONE;
			UcwebFWindow.this.wmParams.format = PixelFormat.RGBA_8888;  
			UcwebFWindow.this.wmParams.flags = 40;
			UcwebFWindow.this.wmParams.gravity = 51;
			UcwebFWindow.this.wmParams.x = 100;
			UcwebFWindow.this.wmParams.y = 100;
			UcwebFWindow.this.wmParams.width = -2;
			UcwebFWindow.this.wmParams.height = -2;
			
			UcwebFWindow.this.windowManager.addView(floatingWindow, wmParams);
			UcwebFWindow.this.floatingWindow.setOnTouchListener(new OnTouchListener() {
				
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int eventaction = event.getAction();
					switch (eventaction) {
					
					case MotionEvent.ACTION_DOWN:
						UcwebFWindow.this.startX = event.getRawX();
						UcwebFWindow.this.startY = event.getRawY();
						break;
						
					case MotionEvent.ACTION_MOVE:
						UcwebFWindow.this.endX = event.getRawX();
						UcwebFWindow.this.endY = event.getRawY();
						updateFWindowPos();
						break;
						
					case MotionEvent.ACTION_UP:
						UcwebFWindow.this.endX = event.getRawX();
						UcwebFWindow.this.endY = event.getRawY();
						
						updateFWindowPos();
						onFButtonClicked();
						break;
						
					default:
						break;
					}
					return false;
				}
			});
		}
	}

	private void updateFWindowPos() {
		if(startX == endX && startY == endY){
			
		}else{
			wmParams.x = (int) (UcwebFWindow.this.endX - (UcwebFWindow.this.floatingWindow.getHeight()/2.0));
			wmParams.y = (int) (UcwebFWindow.this.endY - (UcwebFWindow.this.floatingWindow.getWidth()/2.0));
			windowManager.updateViewLayout(UcwebFWindow.this.floatingWindow, wmParams);
		}
	}
	
	@SuppressWarnings("static-access")
	private void onFButtonClicked() {
		if(startX == endX && startY == endY){
			switch(statu) {
			case MonitorType.MONKEY_RANDOM_TEST:
				
				new Thread(new MonitorCheckAppStatu()).start();
				
				windowManager.removeView(floatingWindow);
				break;
			
			case MonitorType.MONKEY_SCRIPT_TEST:
				
				MonitorExecuteMonkeyScript monitorExecuteMonkeyScript = new MonitorExecuteMonkeyScript(
												MonitorType.Monkey_Script_SavePath, MonitorType.Monkey_Script_RunTimes);
				Log.d("Tag", MonitorType.Monkey_Script_SavePath);
				new Thread(monitorExecuteMonkeyScript).start();
				
				windowManager.removeView(floatingWindow);
				break;
			
			case MonitorType.RECORD_MONKEY_SCRIPT:
				if(recordScriptThread == null){
					recordScriptThread = new RecordScriptThread(resolution);
				}
				if(Record_Monkey_Script_Statu == 1){
					
					new Thread(recordScriptThread).start();
					Record_Monkey_Script_Statu += 1;
					
					rotateImage(R.drawable.stop);
				}else if(Record_Monkey_Script_Statu == 2){
					recordScriptThread.stopRunning();
					
					windowManager.removeView(floatingWindow);
					String time = format.format(new Date());
					UcwebFileUtils fileUtils = new UcwebFileUtils(context);
					UcwebContext ucwebContext = UcwebContext.getContext(context);
					try {
						fileUtils.writeFile(ucwebContext.getFileSavePath() + pkgName + "_" + time +
								"_" + "MonkeyScript" + ".txt", recordScriptThread.stringBuffer);
					} catch (IOException e) {
						e.printStackTrace();
					}
					stopFloatWindowServer();
				}
				break;
				
			default:
				break;
			}
		}else {
			updateFWindowPos();
		}
	}
	
	private void rotateImage(int src) {
		Bitmap bitmap = ((BitmapDrawable) (getResources()
				.getDrawable(src))).getBitmap();
		Matrix matrix = new Matrix();
		matrix.setRotate(270);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);

		imageView.setImageBitmap(bitmap);
	}

	private void stopFloatWindowServer() {
		Intent stopFloatWindowServer = new Intent(context,FloatWindowServer.class);
		context.stopService(stopFloatWindowServer);
	}
	
	private void setInVisible(){
		sCpuMem.setVisibility(INVISIBLE);
		sIOW.setVisibility(INVISIBLE);
		sBattery.setVisibility(INVISIBLE);
		sNet.setVisibility(INVISIBLE);
	}
	
	@SuppressWarnings("unused")
	private void onDataChange(String data,int type){
		if(type == CPUMEMINFO){
			sCpuMem.setText(("data"));
			sCpuMem.setVisibility(VISIBLE);
		}
	}
	
	@SuppressLint("HandlerLeak")
	public class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			int type = bundle.getInt("type");
			switch(type){
			case CPUMEMINFO:
				sCpuMem.setText(bundle.getString("data"));
				sCpuMem.setVisibility(VISIBLE);
				break;
			
			case IOWINFO:
				break;
				
			case BATTERYINFO:
				break;
				
			case NETINFO:
				break;
				
			default:
				break;
				
			}
		}
	}
	
	public static class Builder{
		private int times = 0;
		private int statu = 0;
		private String pkgName = null;
		private Context context = null;
		private int[] resolution = null;
		
		public Builder(Context context){
			this.context = context;
		}
		
		public Builder setStatu(int statu){
			this.statu = statu;
			return this;
		}
		
		public Builder setResolution(int[] resolution){
			this.resolution = resolution;
			return this;
		}
		
		public Builder setPKGName(String pkgName){
			this.pkgName = pkgName;
			return this;
		}
		
		public Builder setTimes(int times){
			this.times = times;
			return this;
		}
		
		public UcwebFWindow build(){
			return new UcwebFWindow(this);
		}
	}
}
