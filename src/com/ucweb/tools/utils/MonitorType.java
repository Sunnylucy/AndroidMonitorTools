package com.ucweb.tools.utils;

public class MonitorType {
			// monitor cpu and memory
			public final static int MONITOR_TYPE_CPUMEM = 1;
			//monitor batter
			public final static int MONITOR_TYPE_BATTER = 2;
			//monitor io
			public final static int MONITOR_TYPE_IOW = 3;
			//monitor net
			public final static int MONITOR_TYPE_NET = 4;
			//monitor MonkeyRandomTest
			public final static int MONKEY_RANDOM_TEST = 5;
			//monitor MonkeyScriptTest
			public final static int MONKEY_SCRIPT_TEST = 6;
			//monitor RecordMonkeyScript
			public final static int RECORD_MONKEY_SCRIPT = 7;
			
			
			//cpu&mem monitor can startable flag
			public static boolean MONITOR_FLAG_CPUMEM = true;
			//battery monitor can startable flag
			public static boolean MONITOR_FLAG_BATTERY = true;
			//iow monitor can startable flag
			public static boolean MONITOR_FLAG_IOW = true;
			//net monitor can startable flag
			public static boolean MONITOR_FLAG_NET = true;
			
			//monitor cpu and memory data collect frequency
			public static String COLLECT_FREQUENCY_CPUMEM = "10";
			//monitor IOW data collect frequency
			public static String COLLECT_FREQUENCY_IOW = "10";
			//monitor net data collect frequency
			public static String COLLECT_FREQUENCY_NET = "10";
			
			//monitor monkey script test params
			public static String Monkey_Script_SavePath = "";
			
			public static int Monkey_Script_RunTimes = 1;
			
			public static String Monkey_RScript_SavePath = "";
}
