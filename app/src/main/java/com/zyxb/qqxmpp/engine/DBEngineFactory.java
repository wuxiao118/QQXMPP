package com.zyxb.qqxmpp.engine;

import android.content.Context;

public class DBEngineFactory {
	private DBEngineFactory(){
		
	}
	
	public static DBFriendGroupEngine getDBFriendGroupEngine(Context context){
		return new DBFriendGroupEngine(context);
	}
	
	public static DBMessageEngine getDBMessageEngine(Context context){
		return new DBMessageEngine(context);
	}
	
	public static DBGroupEngine getDBGroupEngine(Context context){
		return new DBGroupEngine(context);
	}
	
	public static DBUserEngine getDBUserEngine(Context context){
		return new DBUserEngine(context);
	}
}
