package com.ucweb.tools.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import com.ucweb.tools.R;
import com.ucweb.tools.config.Config;
import com.ucweb.tools.context.UcwebContext;
import com.ucweb.tools.monitorTask.FileListThread;
import com.ucweb.tools.monitorTask.MonitorScriptDetails;
import com.ucweb.tools.service.MonitorService;
import com.ucweb.tools.utils.MonitorType;
import com.ucweb.tools.utils.UcwebAppUtil;
import com.ucweb.tools.utils.UcwebPhoneInfoUtils;
import com.ucweb.tools.utils.UcwebProgressDialog;
import com.ucweb.tools.utils.UcwebThreadPoolsManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OperationActivity extends Activity {
    
    ArrayList<HashMap<String, String>> monkeySettingCheckableStatu = new ArrayList<HashMap<String,String>>();
    
    private ListView MonkeyTestSettinListView = null;
    private ListView PerformanceTestSettinListView = null;
    
    private static int MonkeyTestType = 0;
    private static int PerfomanceTestType = 0;
    
    private Button startTestButton = null;
    private Button btnStopTest = null;
    
    private EditText monkeyRandomTestTimes = null;
    
    private AlertDialog.Builder builder = null;
    @SuppressWarnings("unused")
	private AlertDialog dialog = null;
    private EditText monkeyScriptTestTimes = null;
    private ListView monkeyScriptList = null;
    public static ArrayList<HashMap<String, Object>> FIlENAMELIST = null;
    public String checkedMonkeyScriptFileName = null;
    private int dialogCheckBoxPosition = 0;
    
    private final ExecutorService executor = UcwebThreadPoolsManager.getThreadPoolManager().getExecutorService();
	
	private String pkgName;

	private UcwebProgressDialog mProgressDialog = null;
	public static MyHandler mHandler = null;
	private UcwebContext env;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_activity);
        
        pkgName = getIntent().getStringExtra("pkgName");
		
		env = UcwebContext.getContext(this);
        mHandler = new MyHandler();
		
        MonkeyTestSettinListView = (ListView)findViewById(R.id.MonkeyTestSetting);
        PerformanceTestSettinListView = (ListView)findViewById(R.id.PerformanceTestSetting);
        initListView();
        
        startTestButton = (Button)findViewById(R.id.btnStartTest);
        btnStopTest = (Button)findViewById(R.id.btnStopTest);
        btnStopTest.setEnabled(Config.btnStopTestStatu);
        
        startTestButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(MonkeyTestType > 0 && PerfomanceTestType > 0){
					
					btnStopTest.setEnabled(true);
					Config.btnStopTestStatu = true;
					
					if(MonkeyTestType == 1){
						new AlertDialog.Builder(OperationActivity.this).setTitle("设置Monkey随机事件的点击次数(单位:百次)").
						setView(initDialogView()).setPositiveButton("确定",new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								HashMap<String, String> resolutionData = UcwebPhoneInfoUtils.
										getScreenResolution(OperationActivity.this);
								String times = null;
								if(!monkeyRandomTestTimes.getText().toString().equals(null)){
									times = monkeyRandomTestTimes.getText().toString();
								}else{
									times = "1";
								}
								startMonitorService(pkgName,MonitorType.MONKEY_RANDOM_TEST,
										Integer.parseInt(times),resolutionData.get("VAL"));
//								startFloatWindowService(pkgName,MonkeyTestType,Integer.parseInt(dialogEditText.getText().toString()));
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								
							}
							
						}).show();
					}else if(MonkeyTestType == 2){
						btnStopTest.setEnabled(true);
						Config.btnStopTestStatu = true;
						
						showProgressDialog();
						FileListThread fileListThread =new FileListThread(assUrl(Config.GET_MONKEY_SCRIPT_URL,2,pkgName), 2);
						new Thread(fileListThread).start();
						
						builder = new AlertDialog.Builder(OperationActivity.this).setView(inflateDialogView()).
								setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								HashMap<String, String> resolutionData = UcwebPhoneInfoUtils.getScreenResolution(OperationActivity.this);
								
								MonitorScriptDetails monitorScriptDetails = new MonitorScriptDetails(OperationActivity.this,
										UcwebContext.getContext(OperationActivity.this).getFileSavePath() + checkedMonkeyScriptFileName,
										assUrl(Config.GET_MONKEY_SCRIPT_URL,
												3,
												FIlENAMELIST.get(dialogCheckBoxPosition).get("md5").toString(),
												resolutionData.get("VAL").trim().split("×"))
												);
								
								new Thread(monitorScriptDetails).start();
								
								String times = null;
								
								if(!monkeyScriptTestTimes.getText().toString().equals(null)){
									times = monkeyScriptTestTimes.getText().toString();
								}else {
									times = "1";
								}
								
									
								MonitorType.Monkey_Script_SavePath = UcwebContext.getContext(OperationActivity.this).getFileSavePath()+ checkedMonkeyScriptFileName;
								MonitorType.Monkey_Script_RunTimes = Integer.parseInt(times);
								
								startMonitorService(pkgName,MonitorType.MONKEY_SCRIPT_TEST,
										Integer.parseInt(times),resolutionData.get("VAL"));
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
					}
					
							
				}else if(MonkeyTestType == 0 && PerfomanceTestType >0){
					
					btnStopTest.setEnabled(true);
					Config.btnStopTestStatu = true;
					
					HashMap<String, String> resolutionData = UcwebPhoneInfoUtils.getScreenResolution(OperationActivity.this);
					startMonitorService(pkgName,MonkeyTestType,1,resolutionData.get("VAL"));
					
				}else if(MonkeyTestType > 0 && PerfomanceTestType == 0){
					
					if(MonkeyTestType == 1){
						new AlertDialog.Builder(OperationActivity.this).setTitle("设置Monkey随机事件的点击次数：").
						setView(initDialogView()).setPositiveButton("确定",new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								HashMap<String, String> resolutionData = UcwebPhoneInfoUtils.getScreenResolution(OperationActivity.this);
								
								String times = null;
								if(!monkeyRandomTestTimes.getText().toString().equals(null)){
									times = monkeyRandomTestTimes.getText().toString();
								}else{
									times = "1";
								}
								
								startMonitorService(pkgName,MonitorType.MONKEY_RANDOM_TEST,
										Integer.parseInt(times),resolutionData.get("VAL"));
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								
							}
							
						}).show();
					}else if(MonkeyTestType == 2){
						
						showProgressDialog();
						FileListThread fileListThread =new FileListThread(assUrl(Config.GET_MONKEY_SCRIPT_LIST_URL,2,pkgName), 2);
						new Thread(fileListThread).start();
						
						builder = new AlertDialog.Builder(OperationActivity.this).setView(inflateDialogView()).
								setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								HashMap<String, String> resolutionData = UcwebPhoneInfoUtils.getScreenResolution(OperationActivity.this);
								
								MonitorScriptDetails monitorScriptDetails = new MonitorScriptDetails(OperationActivity.this,
										UcwebContext.getContext(OperationActivity.this).getFileSavePath() + checkedMonkeyScriptFileName,
										assUrl(Config.GET_MONKEY_SCRIPT_URL,
												3,
												FIlENAMELIST.get(dialogCheckBoxPosition).get("md5").toString(),
												resolutionData.get("VAL").trim().split("×"))
												);
								
								new Thread(monitorScriptDetails).start();
								
								String times = null;
								
								if(!monkeyScriptTestTimes.getText().toString().equals(null)){
									times = monkeyScriptTestTimes.getText().toString();
								}else {
									times = "1";
								}
								
								MonitorType.Monkey_Script_SavePath = UcwebContext.getContext(OperationActivity.this).getFileSavePath()+ checkedMonkeyScriptFileName;
								MonitorType.Monkey_Script_RunTimes = Integer.parseInt(times);
								
								startMonitorService(pkgName,MonitorType.MONKEY_SCRIPT_TEST,
										Integer.parseInt(times),resolutionData.get("VAL"));
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
					}
					
				}else if(MonkeyTestType == 0 && PerfomanceTestType == 0){
					Toast.makeText(OperationActivity.this, "至少选择一项测试任务", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        btnStopTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService(new Intent(OperationActivity.this, MonitorService.class));
				OperationActivity.this.finish();
			}
		});
    }
    
    @SuppressLint("InflateParams")
	private View initDialogView(){
    	LayoutInflater inflater = LayoutInflater.from(OperationActivity.this);
    	
    	View dialogView = inflater.inflate(R.layout.dialog_layout, null);
    	
    	monkeyRandomTestTimes = (EditText)dialogView.findViewById(R.id.MonkeyRandomTestTimes);
    	
    	return dialogView;
    	
    }
    
    private void initListView(){
    	
        MonkeyTestSettinListView.setEnabled(false);
        String[] monkeyTestSetting = {"Monkey随机性测试","Monkey脚本测试"};
        MyAdapter monkeyTestSettingListViewAdapter = new MyAdapter(OperationActivity.this);
        monkeyTestSettingListViewAdapter.onDataChangedListener(assData(monkeyTestSetting));
        MonkeyTestSettinListView.setAdapter(monkeyTestSettingListViewAdapter);
        
        PerformanceTestSettinListView.setEnabled(false);
        String[] performanceTestSetting = {"Cpu和内存监控","电量监控","IO监控","流量监控"};
        MyAdapter performanceTestSettingListViewAdapter = new MyAdapter(OperationActivity.this);
        performanceTestSettingListViewAdapter.onDataChangedListener(assData(performanceTestSetting));
        PerformanceTestSettinListView.setAdapter(performanceTestSettingListViewAdapter);
    }
    
    private ArrayList<HashMap<String, Object>> assData(String[] data){
    	
    	ArrayList<HashMap<String, Object>> listviewData = new ArrayList<HashMap<String,Object>>();
    	HashMap<String, Object> listviewItemData = null;
    	for(int i=0;i<data.length;i++){
    		listviewItemData = new HashMap<String, Object>(2);
    		listviewItemData.put("name", data[i]);
    		listviewItemData.put("checkStatu", null);
    		listviewData.add(listviewItemData);
    	}
    	
    	return listviewData;
    }
    
    
	private void startMonitorService(final String pkgName, final int monkeyTestType, 
		final int times,final String resolutionData){
		final Context applicationContext = this.getApplicationContext();
		
		executor.execute(new Runnable() {
			
			@Override
			public void run() {			
				UcwebAppUtil apputil = new UcwebAppUtil(applicationContext);

				//blocking thread, until test app is running
				apputil.startAppAndGetPid(pkgName);				
				
				Intent intent = new Intent();
				intent.setClass(OperationActivity.this, MonitorService.class);
				intent.putExtra("pkgName", pkgName);
				intent.putExtra("MonkeyTestType",monkeyTestType);
				intent.putExtra("TIMES",times);
				intent.putExtra("RESOLUTION", resolutionData);
				
				final String fileWritePath = env.getFileSavePath();
				
				intent.putExtra("file path", fileWritePath);
				
				startService(intent);
			}
		});
	}
    
    private class MyAdapter extends BaseAdapter{
		private LayoutInflater inflater = null;
		private ArrayList<HashMap<String, Object>> mDataList = null;
		private HashMap<Integer, String> checkableStatu = null;
		
		public MyAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		
		@SuppressLint("UseSparseArrays")
		public void onDataChangedListener(ArrayList<HashMap<String, Object>> dataList){
			mDataList = dataList;
			
			checkableStatu = new HashMap<Integer,String>(mDataList.size());
			
			for(int i = 0;i<mDataList.size();i++){
				checkableStatu.put(i, "TEST");
			}
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
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder mHolder_thi = null;
			if(convertView == null){
				mHolder_thi = new ViewHolder();
				
				convertView = inflater.inflate(R.layout.operation_listview_item, null);
				mHolder_thi.mTextView = (TextView)convertView.findViewById(R.id.ringagain);
				mHolder_thi.mCheckBox = (CheckBox)convertView.findViewById(R.id.CheckBox);
				
				mHolder_thi.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							checkableStatu.clear();
							checkableStatu.put(position, "TEST");
							notifyDataSetChanged();
							
							if(mDataList.get(position).get("name").equals("Monkey随机性测试")){
								MonkeyTestType = 1;
								
							}else if(mDataList.get(position).get("name").equals("Monkey脚本测试")){
								MonkeyTestType = 2;
								
							}else if(mDataList.get(position).get("name").equals("Cpu和内存监控")){
								MonitorType.MONITOR_FLAG_CPUMEM = true;
								PerfomanceTestType += 1;
							}else if(mDataList.get(position).get("name").equals("电量监控")){
								MonitorType.MONITOR_FLAG_BATTERY = true;
								PerfomanceTestType += 1;
							}else if(mDataList.get(position).get("name").equals("IO监控")){
								MonitorType.MONITOR_FLAG_IOW = true;
								PerfomanceTestType += 1;
							}else if(mDataList.get(position).get("name").equals("流量监控")){
								MonitorType.MONITOR_FLAG_NET = true;
								PerfomanceTestType += 1;
							}
							
						}else{
							
							if(mDataList.get(position).get("name").equals("Monkey随机性测试")){
								MonkeyTestType = 0;
								
							}else if(mDataList.get(position).get("name").equals("Monkey脚本测试")){
								MonkeyTestType = 0;
								
							}else if(mDataList.get(position).get("name").equals("Cpu和内存监控")){
								MonitorType.MONITOR_FLAG_CPUMEM = false;
								PerfomanceTestType -= 1;
							}else if(mDataList.get(position).get("name").equals("电量监控")){
								MonitorType.MONITOR_FLAG_BATTERY = false;
								PerfomanceTestType -= 1;
							}else if(mDataList.get(position).get("name").equals("IO监控")){
								MonitorType.MONITOR_FLAG_IOW = false;
								PerfomanceTestType -= 1;
							}else if(mDataList.get(position).get("name").equals("流量监控")){
								MonitorType.MONITOR_FLAG_NET = false;
								PerfomanceTestType -= 1;
							}
							
							for(int i = 0;i<mDataList.size();i++){
								checkableStatu.put(i, "TEST");
							}
							notifyDataSetChanged();
						}
					}
				});
				convertView.setTag(mHolder_thi);
			}else{
				mHolder_thi = (ViewHolder)convertView.getTag();
			}
			
			mHolder_thi.mTextView.setText(mDataList.get(position).get("name").toString());
			
			if(mDataList.size() < 4 || mDataList.size() > 4){
				mHolder_thi.mCheckBox.setEnabled(checkableStatu.get(position) == null ? false : true);
				
			}else if(mDataList.size() == 4){
				mHolder_thi.mCheckBox.setChecked(true);
			}
			return convertView;
		}
    }
    
    private class ViewHolder {
    	
    	public TextView mTextView = null;
    	
    	public CheckBox mCheckBox = null;
    	
    }
    
	private class DialogAdapter extends BaseAdapter{

		private LayoutInflater inflater = null;
		private ArrayList<HashMap<String, Object>> mDataList = null;
		private HashMap<Integer, String> checkableStatu = null;
		
		public DialogAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}
		
		@SuppressLint("UseSparseArrays")
		public void onDataChangedListener(ArrayList<HashMap<String, Object>> dataList){
			mDataList = dataList;
			
			checkableStatu = new HashMap<Integer,String>(mDataList.size());
			
			for(int i = 0;i<mDataList.size();i++){
				checkableStatu.put(i, "TEST");
			}
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
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder_dia holder_dia = null;
			if(convertView == null){
				holder_dia = new ViewHolder_dia();
				
				convertView = inflater.inflate(R.layout.dialog_list_item, null);
				
				holder_dia.mTextView = (TextView)convertView.findViewById(R.id.MonkeyScriptName);
				holder_dia.mCheckBox = (CheckBox)convertView.findViewById(R.id.MonkeyScriptCheckStatu);
				
				holder_dia.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							checkableStatu.clear();
							checkableStatu.put(position, "TEST");
							notifyDataSetChanged();
							
							checkedMonkeyScriptFileName = mDataList.get(position).get(Config.FILETYPE).toString();
							dialogCheckBoxPosition = position;
							
						}else{
							
							checkedMonkeyScriptFileName = null;
							
							for(int i = 0;i<mDataList.size();i++){
								checkableStatu.put(i, "TEST");
							}
							notifyDataSetChanged();
						}
					}
				});
				
				convertView.setTag(holder_dia);
			}else{
				holder_dia = (ViewHolder_dia)convertView.getTag();
				
			}
			holder_dia.mTextView.setText(mDataList.get(position).get(Config.FILETYPE).toString());
			holder_dia.mCheckBox.setEnabled(checkableStatu.get(position) == null ? false : true);
			
			return convertView;
		}
	}
    
	private class ViewHolder_dia{
		
		public TextView mTextView = null;
    	
    	public CheckBox mCheckBox = null;
	}
    
    @SuppressLint("HandlerLeak") 
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
		}
	};
    
	private void showProgressDialog(){
		if(mProgressDialog == null){
			mProgressDialog = UcwebProgressDialog.createDialog(OperationActivity.this);
			mProgressDialog.setMessage("");
			mProgressDialog.setCancelable(true);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
		mProgressDialog.show();
	}
	
	private void destroyProgressDialog(){
		if(mProgressDialog != null){
			handler.sendEmptyMessage(0);
		}
	}
    
    @SuppressLint("InflateParams")
	private View inflateDialogView(){
    	LayoutInflater inflate = LayoutInflater.from(OperationActivity.this.getApplicationContext());
		View view = inflate.inflate(R.layout.dialog_layout_other, null);
		monkeyScriptList = (ListView)view.findViewById(R.id.MonkeyScriptList);
		monkeyScriptTestTimes = (EditText)view.findViewById(R.id.MonkeyScriptTestTimes);
		
		return view;
    }
    
    private String assUrl(String url,int type,String typename){
		return url + "?" + "type=" + type + "&typename=" + typename;
	}
    
	private String assUrl(String url,int type ,String md5,String[] cordination){
		return url + "?" + "type=" + type + "&md5=" + md5 + "&x=" + cordination[0] + 
				"&y=" + cordination[1];
	}
    
    @SuppressLint("HandlerLeak") 
	public class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			if(bundle.getString("msg").equals("getdata_success")){
				Log.d("Tag" ,"FIlENAMELIST size:" + FIlENAMELIST.size());
				DialogAdapter dialogAdapter = new DialogAdapter(OperationActivity.this);
				dialogAdapter.onDataChangedListener(FIlENAMELIST);
				monkeyScriptList.setAdapter(dialogAdapter);
				
				destroyProgressDialog();
				
				dialog = builder.show();
				
			}else if(bundle.getString("msg").equals("getdata_failed")){
				Toast.makeText(OperationActivity.this.getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
				destroyProgressDialog();
			}else if(bundle.getString("msg").equals("download_success")){
				Toast.makeText(OperationActivity.this.getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
				
			}else{
				Toast.makeText(OperationActivity.this.getApplicationContext(), "", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
