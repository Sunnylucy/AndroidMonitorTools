package com.ucweb.tools.utils;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UcwebJsonUtil {
	
	/***
	 * 获取指定tag的值
	 * @param json 需要解析的json
	 * @param tag 需要获取的tag
	 * @return 如tag存在，那么返回对应的值，若tag不存在，则返回空字符串
	 * @throws JSONException
	 */
	public static String getTagText(String json, String tag) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		
		Iterator<?> keys = jsonObj.keys();
		
		while (keys.hasNext()) {
			
			String key = (String) keys.next();
			
			if(tag.equals(key)) {
				return jsonObj.getString(tag);
			}
			else {
				//如果值是json对象，那么递归解析
				Object value = jsonObj.get(key);
				
				if(value instanceof JSONObject) {
					return getTagText(value.toString(), tag);
				}
			}
		
		}
		
		return "";
	}
	
	public HashMap<String, String> parseJson2HMap(StringBuffer data){
		
		if(data == null){
			return null;
		}
		
		HashMap<String, String> configDate = new HashMap<String,String>(7);
		
		try {
			JSONObject jsonObject = new JSONObject(data.toString());
			
			configDate.put("SING_IN_SCRIPT_PATH", jsonObject.getString("SING_IN_SCRIPT_PATH"));
			configDate.put("TESTAPP", jsonObject.getString("TESTAPP"));
			configDate.put("MONKEY_TEST_TIMES", jsonObject.getString("MONKEY_TEST_TIMES"));
			configDate.put("CPUMEM", jsonObject.getString("CPUMEM"));
			configDate.put("IOW", jsonObject.getString("IOW"));
			configDate.put("NET", jsonObject.getString("NET"));
			configDate.put("BATTERY", jsonObject.getString("BATTERY"));
//			Log.d(getClass().getSimpleName(),jsonObject.getString("phone").toString());
			
		} catch (JSONException e) {
			Log.e(getClass().getSimpleName(), e.getMessage().toString());
		}
		return configDate;
	} 
}
