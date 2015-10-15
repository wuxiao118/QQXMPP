package com.zyxb.qqxmpp.util;

import android.util.Log;


public class Logger {
	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
	private static final int SYSOUT = 6;
	
	private static final int LOGLEVEL = 7;
	
	private Logger(){
		
	}
	
	public static void v(String tag,String msg){
		if(LOGLEVEL>VERBOSE){
			Log.v(tag,msg);
		}
	}
	
	public static void d(String tag,String msg){
		if(LOGLEVEL>DEBUG){
			Log.d(tag,msg);
		}
	}
	
	public static void i(String tag,String msg){
		if(LOGLEVEL>INFO){
			Log.i(tag,msg);
		}
	}
	
	public static void w(String tag,String msg){
		if(LOGLEVEL>WARN){
			Log.w(tag,msg);
		}
	}
	
	public static void e(String tag,String msg){
		if(LOGLEVEL>ERROR){
			Log.e(tag,msg);
		}
	}
	
	public static void out(String msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Boolean msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Integer msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Long msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Double msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Float msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Character msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Short msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Byte msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
	
	public static void out(Object msg){
		if(LOGLEVEL>SYSOUT){
			System.out.println(msg);
		}
	}
}
