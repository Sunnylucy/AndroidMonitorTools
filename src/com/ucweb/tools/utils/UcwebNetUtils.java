package com.ucweb.tools.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.google.gson.stream.JsonReader;
import com.ucweb.tools.config.Config;
import com.ucweb.tools.context.UcwebContext;
import com.ucweb.tools.infobean.RecodeInfo;
import com.ucweb.tools.infobean.RecodeInfo.UploadFlag;
import com.ucweb.tools.utils.UcwebDateUtil.YMDDateFormat;

import android.content.Context;
import android.util.Log;

public class UcwebNetUtils {
	
	/**
	 * @param url			upload url
	 * @param params		post params
	 * @param file			absolute file path
	 * @return				if success, return String; else return null
	 * */
	
	public static boolean ISDATAAVAILABLE = false;
	
	public static String uploadFile(String url, String params, String file) throws IOException{
		Log.d("UcwebNetUtils", "start upload method");
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		
		HttpPost post = new HttpPost(url);
		File uploadFile = new File(file);
		
		InputStream is = null;
		
		// <input type="file" name="userfile" />
		MultipartEntity entity = new MultipartEntity();
		ContentBody cbFile = new FileBody(uploadFile);
		entity.addPart(params, cbFile);
		
		post.setEntity(entity);
		try {
			HttpResponse response = httpClient.execute(post);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				
				HttpEntity httpEntity = response.getEntity();
				
				if (httpEntity != null) {
					
					is = httpEntity.getContent();
    				@SuppressWarnings("unused")
					int bytesRead = -1;
    				byte[] buffer = new byte[1024];
    				StringBuffer stringBuffer = new StringBuffer();
    				while ((bytesRead = is.read(buffer)) != -1) {
    					stringBuffer.append(new String(buffer).toCharArray());
    				}
    				
    				buffer = null;				
    				return stringBuffer.toString();
				}
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}
	
    public static String doGet(String baseUrl, List<NameValuePair> params) throws IOException{
    	HttpClient httpClient = new DefaultHttpClient();
    	final String url = (params != null && !params.isEmpty()? getCompletedUrl(baseUrl, params) : baseUrl);
    	HttpGet httpGet = new HttpGet(url);
    	
    	ResponseHandler<String> handler = new BasicResponseHandler();
    	try {
    		String response = httpClient.execute(httpGet, handler);
    		return response;
    	} finally {
    		httpClient.getConnectionManager().shutdown();
    	} 	
    }
    
	public static InputStream doGet(String url){
		HttpResponse mHttpResponse = null;
		HttpClient mHttpClient = new DefaultHttpClient();
		HttpGet mHttpGet = new HttpGet(url);
		try {
			mHttpResponse = mHttpClient.execute(mHttpGet);
			if(HttpStatus.SC_OK == mHttpResponse.getStatusLine().getStatusCode()){
				HttpEntity mEntity = mHttpResponse.getEntity();
				ISDATAAVAILABLE = true;
				return mEntity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "resource" })
	public static ArrayList<HashMap<String,Object>> decodeJSON(InputStream recivedData,int type){
		ArrayList<HashMap<String,Object>> reciveDataList =  new ArrayList<HashMap<String,Object>>();
		if(recivedData != null){
			try {
				JsonReader mJsonReader = new JsonReader(new InputStreamReader(recivedData,"utf-8"));
				mJsonReader.beginArray();
				while(mJsonReader.hasNext()){
					mJsonReader.beginObject();
					if(type == 1){
						HashMap<String,Object> reciveData = new HashMap<String,Object>();
						while(mJsonReader.hasNext()){
							reciveData.put(mJsonReader.nextName(), mJsonReader.nextString());
						}
						reciveDataList.add(reciveData);

					}else if(type == 2){
						HashMap<String,Object> reciveData = new HashMap<String,Object>(3);
						while(mJsonReader.hasNext()){
							reciveData.put(mJsonReader.nextName(), mJsonReader.nextString());
						}
						reciveDataList.add(reciveData);

					}else if(type == 3){
						
					}
					mJsonReader.endObject();
				}
				mJsonReader.endArray();
				return reciveDataList;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
			}
		}
		return null;
	}

    
    private static String getCompletedUrl(String baseUrl, List<NameValuePair> params) throws IOException{
    	if (!baseUrl.endsWith("?")) {
			baseUrl = baseUrl + "?";
		}
    	return baseUrl + EntityUtils.toString(new UrlEncodedFormEntity(params));
    }
    
	public List<RecodeInfo> doUpload(Context context,List<String> fileList) {
		List<RecodeInfo> infoList = new ArrayList<RecodeInfo>();
		String  uploadUrl = Config.PERFORMANCEFILE_UPLOAD_URL;
		String fileSavePath = UcwebContext.getContext(context).getFileSavePath();
		SimpleDateFormat dateFormater = YMDDateFormat.getYMDFormat();
		
		for(String file : fileList) {
			String fullPath = fileSavePath + file;
			RecodeInfo info = new RecodeInfo();
			if(fullPath.contains("MonkeyScript")){
				uploadUrl = Config.MONKEYSCRIPT_UPLOAD_URL;
			}
			try {
				UcwebNetUtils.uploadFile(uploadUrl, "file", fullPath);
				UcwebFileUtils.deleteFile(fullPath);
				
				info.date = dateFormater.format(new Date());
				info.path = fullPath;
				info.uploadFlag = UploadFlag.UPLOAD_SUCCESS;
			} catch (IOException e) {
				info.uploadFlag = UploadFlag.UPLOAD_FAILED;
				
			}
			infoList.add(info);
		}
		return infoList;
	}
 
}
