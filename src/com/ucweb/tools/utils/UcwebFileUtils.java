package com.ucweb.tools.utils;

/***
 * @author yangruyao
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Environment;
//import android.widget.Toast;
import android.util.Log;

public class UcwebFileUtils {	
	
	private Context mContext;
	
	//date format
	private final SimpleDateFormat sDateFormat;
	
	public UcwebFileUtils(Context context){
		mContext = context;
		sDateFormat = UcwebDateUtil.YMDDateFormat.getYMDFormat();
	}
	
	/**
	 * if sdcard available return true ,else return false
	 * */
	public static boolean isSdcardAvailable (){
		String sdcardStatus = Environment.getExternalStorageState();
		return sdcardStatus.equals(Environment.MEDIA_MOUNTED)? true : false;
	}
	
	public static final boolean isFileValid(File file) {
		return file.exists()? file.isFile() : false;
	}
	
	/**file type*/
	public static enum FileType {
		CpuMemInfoFileType,
		BatterInfoFileType,
		NetInfoFileType,
		IOWInfoFileType
	}
	
	/**根据文件类型，生成前缀名*/
	public static String generateFileNamePrefix(FileType fileFlag) {
		String fileTypeName;
		switch (fileFlag) {
		case CpuMemInfoFileType:
			fileTypeName = "MonitorInfo";
			break;
			
		case BatterInfoFileType:
			fileTypeName = "BatterInfo";
			break;
			
		case NetInfoFileType:
			fileTypeName = "NetInfo";
			break;
			
		case IOWInfoFileType:
			fileTypeName = "IOWInfo";
			break;

		default:
			fileTypeName = "Unknown";
			break;
		}
		
		return fileTypeName;
	}
	
	/**文件扩展名*/
	private static final String FILE_EXTENSION = ".txt";
	
	/**获取文件扩展名*/
	public static String getFileExtension() {
		return FILE_EXTENSION;
	}
	
	/**文件名分隔符*/
	private static final String FILE_NAME_SEPARATION = "_";
	
	/**获取文件名分隔符*/
	public static String getFileNameSeparation() {
		return FILE_NAME_SEPARATION;
	}
	
	/**生成文件名*/
	public String generateFileName(String fileFlag, String pkgName){
		//get file name prefix
		final String fileNamePrefix = fileFlag;		
		//get phone model
		final String PHONE_MODEL = UcwebPhoneInfoUtils.getPhoneModel();
		//get current date
		final String date = sDateFormat.format(new Date());

		return fileNamePrefix + FILE_NAME_SEPARATION + pkgName + FILE_NAME_SEPARATION 
				+ PHONE_MODEL + FILE_NAME_SEPARATION + date + getFileExtension();
	}
	
	/**写数据*/
	public <T> void writeFile(String fileFullPath, List<T> dataList) throws IOException{
		
		StringBuilder sb = new StringBuilder(dataList.size());
		for (T t : dataList) {
			sb.append(t.toString());
		}
		
		writeFile(fileFullPath, sb);
	}
	
	public <T> void writeMScriptFile(String fileFullPath, T data) throws IOException {
		FileOutputStream fos = null;
		Log.d("Tag",fileFullPath);
		deleteFile(fileFullPath);
		try {
			fos = new FileOutputStream(fileFullPath, true);
			fos.write(data.toString().getBytes());
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public <T> void writeFile(String fileFullPath, T data) throws IOException {
		FileOutputStream fos = null;
		Log.d("Tag",fileFullPath);
		try {
			fos = new FileOutputStream(fileFullPath, true);
			fos.write(data.toString().getBytes());
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("resource")
	public static void writeInfoTophone(Context context,InputStream scriptData,String filePath) throws IOException{
		String temp = null;
		Log.d("Tag", "Starting write file");
		FileOutputStream fos = null;
		if (isSdcardAvailable()) {
			removeExistFile(filePath);
			fos = new FileOutputStream(filePath, true);
		} else {
			removeExistFile(filePath);
			fos = context.openFileOutput(filePath, Context.MODE_APPEND);
		}
		BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(scriptData));
		while((temp = mBufferedReader.readLine()) != null){
			fos.write((temp + "\n").getBytes());
		}
		fos.close();
	}
	
	private static void removeExistFile(String path){
		if(path != null){
			File file = new File(path);
			if(file.exists()){
				file.delete();
			}
		}
	}
	
	/***
	 * 生成文件路径，如果是有SD卡，则生成SD卡根目录，没有SD卡，则生成/data/date/包名/下
	 * @return 生成的目录
	 */
	public final String generateFilePath() {
		if(isSdcardAvailable()) 
			return generateSdcardFilePath();
		
		return generateLocalFilePath();
	}
	
	/**生成Sd卡文件路径*/
	public static String generateSdcardFilePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}
	
	/**生成本地文件路径*/
	public String generateLocalFilePath() {
		return mContext.getFilesDir().getPath() + File.separator;
	}
	
	public static void deleteFile(String fileFullPath) {
		File file = new File(fileFullPath);
		
		if(!isFileValid(file)) return;
		
		file.delete();
	}
	
	
	public StringBuffer loadConfig(){
		StringBuffer sb = null;
		BufferedReader bf = null;
		InputStream config = null;
		File configFile = new File("sdcard/config.json");
		if(configFile.exists()){
			String temp = null;
			try {
				config = new FileInputStream(configFile);
				bf = new BufferedReader(new InputStreamReader(config));
				sb = new StringBuffer();
				while((temp = bf.readLine()) != null){
					sb.append(temp);
				}
				return sb;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					config.close();
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
