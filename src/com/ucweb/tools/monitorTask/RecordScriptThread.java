package com.ucweb.tools.monitorTask;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordScriptThread implements Runnable{
	
	private int oldTime = 0;
	private int newTime = 0;
	private int screenStatus = 2;
	
	private boolean bStopMonitor = false;
	
	private String fileSpliter = null;
	
	private int[] mResolutionData = new int[]{480,800};
	
	private int[] mCorrectionData = null;
	
	public static StringBuffer stringBuffer = null;
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");//�������ڸ�ʽ
	
	private static final String mMonkeyScriptHeader = "#Start Script\n" + "type= raw events\n" + "count= 8\n"
			+"speed= 1.0\n" + "start data >>\n";
	
	public RecordScriptThread(int[] resolutiondata){
		this.mResolutionData = resolutiondata;
		stringBuffer = new StringBuffer("#x=" + resolutiondata[1] + "\n" + "#y=" + resolutiondata[0] + "\n");
		stringBuffer.append(mMonkeyScriptHeader);
	}
	
	@Override
	public void run() {
		fileSpliter = getFilterStr();
		mCorrectionData = getCorrectionData();
		
		getCoordinateData();
	}
	
	public void stopRunning() {
		bStopMonitor = true;
	}
	
	private String getFilterStr(){
		String temp =null;
		String str = null;
		int name = 0;
		int events = 0;
		int key = 0;
		int abs = 0;
		
		Runtime runtime = null;
		Process mProcess = null;
		DataOutputStream mDataOutputStream = null;
		
		InputStream mInputStream = null;
		BufferedReader mBufferedReader = null;
		try {
			runtime = Runtime.getRuntime();
			mProcess = runtime.exec("su");
			mDataOutputStream = new DataOutputStream(mProcess.getOutputStream());
			mDataOutputStream.writeBytes("getevent -p" + "\n");
			
			Log.d("Tag","getevent -p");
			mInputStream = mProcess.getInputStream();
			mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
			String line;
			while((line = mBufferedReader.readLine()) != null){
				if(line.contains("add device")){
					name = 1;
					str = line;
					continue;
				}
				if(line.contains("name:") & (name == 1)){
					name = 0;
					events = 1;
					continue;
				}else{
					name = 0;
				}
				if(line.contains("events:") & events == 1){
					events = 0;
					key = 1;
					continue;
				}else{
					events = 0;
				}
				if(line.contains("KEY (0001):") & key == 1){
					key = 0;
					abs = 1;
					continue;
				}else{
					key = 0;
				}
				if(line.contains("ABS (0003):") & abs == 1){
					temp = str.trim().split("\\s+")[3];
					Log.d("Tag","fileSpliter:" + temp);
					break;
				}
			}
			mDataOutputStream.writeBytes("exit\n");
			mDataOutputStream.flush();
		}catch (IOException e){
			Log.d("Tag",e.getMessage());
		}finally{
			try {
				mProcess.destroy();
				mDataOutputStream.close();
				mInputStream.close();
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}
	
	private int[] getCorrectionData(){
		int[] data = new int[4];
		InputStream mInputStream = null;
		
		Runtime runtime = null;
		Process mProcess = null;
		DataOutputStream mDataOutputStream = null;
		BufferedReader mBufferedReader = null;
		try {
			runtime = Runtime.getRuntime();
			mProcess = runtime.exec("su");
			mDataOutputStream = new DataOutputStream(mProcess.getOutputStream());
			
			mDataOutputStream.writeBytes("getevent -p" + "\n");
			
			mInputStream = mProcess.getInputStream();
			mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
			String line;
			while((line = mBufferedReader.readLine()) != null){
				if(line.contains("0035")){
					
					String[] buf = line.trim().split("\\s+");
					data[0] = changeCorrectionData2Int(buf[5]);
					data[1] = changeCorrectionData2Int(buf[7]);
				}else if(line.contains("0036")){
					
					String[] buf = line.trim().split("\\s+");
					data[2] = changeCorrectionData2Int(buf[5]);
					data[3] = changeCorrectionData2Int(buf[7]);
					break;
				}
			}
			mDataOutputStream.writeBytes("exit\n");
			mDataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				mDataOutputStream.close();
				mInputStream.close();
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	private void getCoordinateData(){
		String[] data = new String[2];
		String[] buf = new String[20];
		
		InputStream mInputStream = null;
		
		BufferedReader mBufferedReader = null;
		int[] coordinate = new int[2];
		
		Runtime runtime = null;
		Process mProcess = null;
		DataOutputStream mDataOutputStream = null;
		try {
			runtime = Runtime.getRuntime();
			mProcess = runtime.exec("su");
			mDataOutputStream = new DataOutputStream(mProcess.getOutputStream());
			
			mDataOutputStream.writeBytes("getevent" + "\n");
			
			mInputStream = mProcess.getInputStream();
			mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
			String line = mBufferedReader.readLine();
			String temp = null;
			while(!bStopMonitor){
				if(line.contains(fileSpliter) && line.contains("0035") && !line.equals(temp)){
					buf = line.split("\\s+");
					data[0] = buf[3];
					temp = line;
				}else if(line.contains(fileSpliter) && line.contains("0036")){
					buf = line.split("\\s+");
					data[1] = buf[3];
					if(data[0] != null /*&& data[1].equals(null)*/){
						
						coordinate = redressCoordinateValue(Long.parseLong(data[0], 16)-1,
								Long.parseLong(data[1], 16)-1
								,mResolutionData,mCorrectionData);
						
						writeScript(coordinate);
					}
					data[0] = null;
					data[1] = null;
				}
				line = mBufferedReader.readLine();
			}
			mDataOutputStream.writeBytes("exit\n");
			mDataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				mDataOutputStream.close();
				mBufferedReader.close();
				mInputStream.close();
				mProcess.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getTime(){
		String[] mTime = mSimpleDateFormat.format(new Date()).split(":");
		if(oldTime != newTime){
			oldTime = newTime;
			newTime = Integer.parseInt(mTime[0])*3600 + Integer.parseInt(mTime[1])*60 
					+ Integer.parseInt(mTime[2]);
		}else{
			newTime = Integer.parseInt(mTime[0])*3600 + Integer.parseInt(mTime[1])*60 
					+ Integer.parseInt(mTime[2]);
		}
	}
	
	private int changeCorrectionData2Int(String value){
		String[] buf = value.trim().split("");
		String temp = "";
		for(int i =0;i<buf.length;i++){
			if(!buf[i].equals(",")){
				temp += buf[i];
			}
		}
		return Integer.parseInt(temp);
	}
	
	private void writeScript(int[] data){
		if(stringBuffer == null){
			stringBuffer = new StringBuffer("");
		}
		
		getTime();
		
		if(oldTime != newTime && oldTime!= 0){
			int waitTime = newTime - oldTime;
			stringBuffer.append("UserWait("+ waitTime*1000 + ");" +"\n");
		}
		if(this.screenStatus == 1){
			
			stringBuffer.append("captureDispatchPointer(1,1,0," + data[1] + "," + 
			(mResolutionData[0] - data[0]) + ",1,0,0,0,0,1,0);\n");
			stringBuffer.append("captureDispatchPointer(1,1,1," + data[1] + "," + 
			(mResolutionData[0] - data[0]) + ",1,0,0,0,0,1,0);\n");
			
		}else if(this.screenStatus == 2){
			
			stringBuffer.append("captureDispatchPointer(1,1,0," + data[0] + "," + 
			data[1] + ",1,0,0,0,0,1,0);\n");
			stringBuffer.append("captureDispatchPointer(1,1,1," + data[0] + "," +
			data[1] + ",1,0,0,0,0,1,0);\n");
		}
	}
	
	private int[] redressCoordinateValue(Long xCoordinate,Long yCoordinate,
			int[] resolutionData,int[] correctionData){
		return new int[] {(int) (resolutionData[0]*(xCoordinate - 
				correctionData[0])/(correctionData[1]-correctionData[0])),
				(int) (resolutionData[1]*(yCoordinate - correctionData[2])/
						(correctionData[3]- correctionData[2]))};
	}
}
	

