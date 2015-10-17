package com.zyxb.qqxmpp.engine;

import android.content.Context;

public class DBEngineFactory {
	private DBEngineFactory(){
		
	}
	
	public static DBFriendGroupEngine getDB3FriendGroupEngine(Context context){
		return new DBFriendGroupEngine(context);
	}
	
	public static DBMessageEngine getDB3MessageEngine(Context context){
		return new DBMessageEngine(context);
	}
	
	public static DBGroupEngine getDB3GroupEngine(Context context){
		return new DBGroupEngine(context);
	}
	
	public static DBUserEngine getDB3UserEngine(Context context){
		return new DBUserEngine(context);
	}
}
