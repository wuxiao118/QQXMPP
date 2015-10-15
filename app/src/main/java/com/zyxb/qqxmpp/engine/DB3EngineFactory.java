package com.zyxb.qqxmpp.engine;

import android.content.Context;

public class DB3EngineFactory {
	private DB3EngineFactory(){
		
	}
	
	public static DB3FriendGroupEngine getDB3FriendGroupEngine(Context context){
		return new DB3FriendGroupEngine(context);
	}
	
	public static DB3MessageEngine getDB3MessageEngine(Context context){
		return new DB3MessageEngine(context);
	}
	
	public static DB3GroupEngine getDB3GroupEngine(Context context){
		return new DB3GroupEngine(context);
	}
	
	public static DB3UserEngine getDB3UserEngine(Context context){
		return new DB3UserEngine(context);
	}
}
