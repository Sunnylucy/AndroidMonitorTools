//package com.ucweb.tools.utils;
//
//import android.content.Context;
//import android.view.WindowManager;
//
//public class UcwebWManager {
//	
//	private UcwebFWindow ucwebFWindow = null;
//	private static WindowManager mWindowManager;
//	private WindowManager.LayoutParams mLayoutParams;
//	
//	private Context context = null;
//	private int statu = -1;
//	private int[] resolution = null;
//	private String pkgName = null;
//	private int times = 1;
//	
//	private UcwebWManager(Builder builder){
//		this.context = builder.context;
//		this.statu = builder.statu;
//		this.resolution = builder.resolution;
//		this.pkgName = builder.pkgName;
//		this.times = builder.times;
//	}
//	
//	private void createFloatWindow(){
//		if(ucwebFWindow == null){
//			
//		}
//	}
//	
//	
//    private static WindowManager getWindowManager(Context context) {  
//        if (mWindowManager == null) {  
//            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
//        }  
//        return mWindowManager;  
//    } 
//	
//	public static class Builder{
//		private Context context = null;
//		private int statu = -1;
//		private int[] resolution = null;
//		private String pkgName = null;
//		private int times = 1;
//		
//		public Builder(Context context){
//			this.context = context;
//		}
//		
//		public Builder setStatu(int statu){
//			this.statu = statu;
//			return this;
//		}
//		
//		public Builder setResolution(int[] resolution){
//			this.resolution = resolution;
//			return this;
//		}
//		
//		public Builder setPKGName(String pkgName){
//			this.pkgName = pkgName;
//			return this;
//		}
//		
//		public Builder setTimes(int times){
//			this.times = times;
//			return this;
//		}
//		
//		public UcwebWManager build(){
//			return new UcwebWManager(this);
//		}
//	}
//	
//}
