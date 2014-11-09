package com.ucweb.tools.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.ucweb.tools.R;
import com.ucweb.tools.utils.MonitorType;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingActivity extends Activity {
	
	private ListView settingListView = null;
	private ArrayList<HashMap<String, Object>> mDataList = null;
	String[][] listData = {{"Cpu和内存监控(单位:s/次)",MonitorType.COLLECT_FREQUENCY_CPUMEM},
			{"IO监控(单位:s/次)",MonitorType.COLLECT_FREQUENCY_IOW},{"流量监控(单位:s/次)",MonitorType.COLLECT_FREQUENCY_NET}};
	
	private SettingListAdapter settingListAdapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		settingListView = (ListView)findViewById(R.id.settingListView);
		
		assListData(listData);
		settingListAdapter = new SettingListAdapter(SettingActivity.this);
		
		settingListView.setAdapter(settingListAdapter);
		
	}

	private ArrayList<HashMap<String, Object>> assListData(String[][] data){
		mDataList = new ArrayList<HashMap<String, Object>>();
		
		for(int i = 0;i<data.length;i++){
			HashMap<String, Object> settingDataItem = new HashMap<String, Object>(2);
			settingDataItem.put("name", data[i][0]);
			settingDataItem.put("val", data[i][1]);
			mDataList.add(settingDataItem);
		}
		return mDataList;
	}
	
	
	private class SettingListAdapter extends BaseAdapter{
		private LayoutInflater inflater = null;
		
		private int progressVal = 0;
		
		
		public SettingListAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@SuppressWarnings("unused")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder mHolder_thi = null;
			if(mHolder_thi == null){
				mHolder_thi = new ViewHolder();
				convertView = inflater.inflate(R.layout.setting_list_item, null);
				mHolder_thi.paramName = (TextView)convertView.findViewById(R.id.settingParamName);
				mHolder_thi.paramValue = (TextView)convertView.findViewById(R.id.settingParamValue);
				mHolder_thi.seekBar = (SeekBar)convertView.findViewById(R.id.seekBar);
				
				convertView.setTag(mHolder_thi);
			}else{
				mHolder_thi = (ViewHolder)convertView.getTag();
			}
			mHolder_thi.paramName.setText(mDataList.get(position).get("name").toString());
			mHolder_thi.paramValue.setText(mDataList.get(position).get("val").toString());
			mHolder_thi.seekBar.setProgress(Integer.parseInt(mDataList.get(position).get("val").toString()));
			mHolder_thi.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					
					HashMap<String, Object> itemData = new HashMap<String, Object>(2);
					itemData.put("name", mDataList.get(position).get("name"));
					itemData.put("val", progressVal);
					mDataList.remove(position);
					mDataList.add(position, itemData);
					notifyDataSetChanged();
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					progressVal = progress;
				}
				
			});
			return convertView;
		}
	}
	
	public class ViewHolder{
		
		public TextView paramName = null;
		public TextView paramValue = null;
		public SeekBar seekBar = null;
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for(int i = 0;i < mDataList.size();i++){
			if(mDataList.get(i).get("name").toString().contains("Cpu")){
				MonitorType.COLLECT_FREQUENCY_CPUMEM = mDataList.get(i).get("val").toString();
				
			}else if(mDataList.get(i).get("name").toString().contains("IO")){
				MonitorType.COLLECT_FREQUENCY_IOW = mDataList.get(i).get("val").toString();
				
			}else if(mDataList.get(i).get("name").toString().contains("流量监控")){
				MonitorType.COLLECT_FREQUENCY_NET = mDataList.get(i).get("val").toString();
				
			}
		}
	}
	
}
