package com.ucweb.tools.monitorTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.ucweb.tools.config.Config;
import com.ucweb.tools.utils.MonitorType;
import com.ucweb.tools.utils.UcwebDateUtil;

import android.content.Context;
import android.util.Log;

public class IOWMonitor extends AbstractMonitor{
	
	private boolean mStopMonitor;
	
	private final String[] cmds = {"top", "-m", "5", "-n", "1"};
	
	private final String mFileSavePath;
	
	private String mPkgName;
	
	private final SimpleDateFormat sdf;
	
	public IOWMonitor(Context context, String fileSavePath, String pkgName){
		super(context);
		
		mFileSavePath = fileSavePath;
		mStopMonitor = false;
		mPkgName = pkgName;
		
		sdf = UcwebDateUtil.YMDHMSDateFormat.getYMDHMSFormat();
	}
	
	public void stopIOWMonitor(){
		mStopMonitor = true;
	}
	
	@Override
	public void startMonitor() {
		
		String fileName = createFileName("IOWInfo", mPkgName);
		Log.d("Tag", fileName);
		doMonitorLoop(createFileFullPath(mFileSavePath, fileName));
	}	
	
	@SuppressWarnings("unused")
	private final void doMonitorLoop(String fileFullPath) {
		Config.IOWInfo = fileFullPath;
		
		InputStream is = null;
		BufferedReader br = null;
		
		Process process = null;
		
		while(!mStopMonitor){
			
			Runtime runTime = Runtime.getRuntime();
			
			try {
				process = runTime.exec(cmds);
			}catch (IOException e) {
				Log.e(getLogTag(), e.getMessage());
				return;
			}
			
			is = process.getInputStream();
			
			String temp = null;
				
			br = new BufferedReader(new InputStreamReader(is));
			
			try {
				while((temp = br.readLine())!= null){
					/**如果为空，重新读一条*/
					if(temp == null) {
						TimeUnit.MILLISECONDS.sleep(Integer.parseInt(MonitorType.COLLECT_FREQUENCY_IOW));
						continue;
					}

					if (temp.contains("System") && temp.contains("IOW")) {
						String iow = temp.split(", ")[2].trim();
						addInBuffer(makeOutputStyle(sdf.format(new Date()), iow));
					}else {
						/**读出的东西不是想要的，重新读一条*/
						continue;			
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			try {
				TimeUnit.SECONDS.sleep(5);
				} catch (Exception e) {
					e.printStackTrace();
			}
			
			writeFileWhenBufferReachMaxCount(fileFullPath, 10);
		}
		
		/**结束测试后，刷新buffer，把buffer剩余数据写到文件*/
		flushBufferAndWriteFile(fileFullPath);
		
		/**释放资源*/
		closeInputStream(is);
		closeBufferReader(br);
		destroyProcess(process);
	}
	
	private final String makeOutputStyle(String date, String iow) {
		return date + "|" + iow  + "\n";
	}
	
	@Override
	public void stopMonitor() {
		stopIOWMonitor();
	}
	
	private void closeInputStream(InputStream in) {
		if(in != null) {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closeBufferReader(BufferedReader br) {
		if(br != null) {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void destroyProcess(Process process) {
		try {
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
