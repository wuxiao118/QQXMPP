package com.zyxb.qqxmpp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "xmpp3.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 用户
		String user_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.USER_TABLE_NAME + "(" + DBColumns.USER_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.USER_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.USER_NICKNAME + " char(20) not null,"
				+ DBColumns.USER_PWD + " char(32)," + DBColumns.USER_ICON
				+ " char(100)," + DBColumns.USER_AGE + " integer,"
				+ DBColumns.USER_GENDER + " char(1) default 'M',"
				+ DBColumns.USER_EMAIL + " char(40),"
				+ DBColumns.USER_LOCATION + " char(20),"
				+ DBColumns.USER_LOGIN_DAYS + " integer default 0,"
				+ DBColumns.USER_LEVEL
				+ " integer default 16,"
				+ DBColumns.USER_REGISTER_TIME
				+ " long not null,"
				+ DBColumns.USER_EXPORT_DAYS
				+ " integer default 1,"
				+ DBColumns.USER_RENEW
				+ " text,"
				+ DBColumns.USER_STATE
				+ " integer default 1,"
				+ DBColumns.USER_BIRTHDAY
				+ " long,"
				+ DBColumns.USER_CONSTELLATION
				+ " integer,"// 星座
				+ DBColumns.USER_OCCUPATION
				+ " varchar(20),"// 职业
				+ DBColumns.USER_COMPANY + " varchar(20),"
				+ DBColumns.USER_SCHOOL + " varchar(20),"
				+ DBColumns.USER_HOMETOWN + " varchar(20),"
				+ DBColumns.USER_DESP + " text,"
				+ DBColumns.USER_PERSONALITY_SIGNATURE + " text,"
				+ DBColumns.USER_WEB_SPACE + " varchar(20));";

		String friend_group_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.FRIEND_GROUP_TABLE_NAME + "(" + DBColumns.USER_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.FRIEND_GROUP_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.FRIEND_GROUP_NAME + " char(20) not null,"
				+ DBColumns.FRIEND_GROUP_USER_ACCOUNT
				+ " varchar(40) not null," + DBColumns.FRIEND_GROUP_POSITION
				+ " integer not null);";

		String friend_state_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.FRIEND_STATE_TABLE_NAME + "("
				+ DBColumns.FRIEND_STATE_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.FRIEND_STATE_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.FRIEND_STATE_USER_ACCOUNT
				+ " varchar(40) not null,"
				+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT
				+ " varchar(40) not null," + DBColumns.FRIEND_STATE_POSITION
				+ " integer," + DBColumns.FRIEND_STATE_LOGIN_CHANNEL
				+ " integer default 0," + DBColumns.FRIEND_STATE_LOGIN_STATE
				+ " integer default 0," + DBColumns.FRIEND_STATE_REMARK
				+ " varchar(20));";

		String qqgroup_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.GROUP_TABLE_NAME + "(" + DBColumns.GROUP_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.GROUP_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.GROUP_NAME + " varchar(40) not null,"
				+ DBColumns.GROUP_ICON + " varchar(100),"
				+ DBColumns.GROUP_DESP + " text,"
				+ DBColumns.GROUP_CREATE_TIME + " long not null,"
				+ DBColumns.GROUP_CLASSIFICATION + " varchar(20));";

		String group_state_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.GROUP_STATE_TABLE_NAME + "("
				+ DBColumns.GROUP_STATE_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.GROUP_STATE_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.GROUP_STATE_LOGIN_STATE + " integer default 0,"
				+ DBColumns.GROUP_STATE_LOGIN_CHANNEL + " integer default 0,"
				+ DBColumns.GROUP_STATE_INTER_TIME + " long not null,"
				+ DBColumns.GROUP_STATE_GROUP_TITLE + " integer default 0,"
				+ DBColumns.GROUP_STATE_MSG_SETTING + " integer default 0,"
				+ DBColumns.GROUP_STATE_LEVEL + " integer default 0,"
				+ DBColumns.GROUP_STATE_REMARK + " varchar(20),"
				+ DBColumns.GROUP_STATE_USER_ACCOUNT
				+ " varchar(40) not null,"
				+ DBColumns.GROUP_STATE_GROUP_ACCOUNT
				+ " varchar(40) not null);";

		String sys_group_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.SYSTEM_GROUP_TABLE_NAME + "("
				+ DBColumns.SYSTEM_GROUP_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.SYSTEM_GROUP_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.SYSTEM_GROUP_NAME + " varchar(40) not null,"
				+ DBColumns.SYSTEM_GROUP_ICON + " varchar(100),"
				+ DBColumns.SYSTEM_GROUP_DESP + " text,"
				+ DBColumns.SYSTEM_GROUP_TYPE + " integer default 0);";

		String message_sql = "CREATE TABLE IF NOT EXISTS "
				+ DBColumns.MESSAGE_TABLE_NAME + "(" + DBColumns.MESSAGE_ID
				+ " integer primary key autoincrement,"
				+ DBColumns.MESSAGE_ACCOUNT + " varchar(40) not null,"
				+ DBColumns.MESSAGE_FROM + " varchar(40) not null,"
				+ DBColumns.MESSAGE_MSG + " text," + DBColumns.MESSAGE_TO
				+ " varchar(40) not null," + DBColumns.MESSAGE_CREATE_TIME
				+ " long not null," + DBColumns.MESSAGE_STATE
				+ " integer default 0," + DBColumns.MESSAGE_TYPE
				+ " integer not null," + DBColumns.MESSAGE_DATA1
				+ " char(20)," + DBColumns.MESSAGE_DATA2 + " char(20),"
				+ DBColumns.MESSAGE_DATA3 + " char(20),"
				+ DBColumns.MESSAGE_DATA4 + " char(20),"
				+ DBColumns.MESSAGE_DATA5 + " char(20),"
				+ DBColumns.MESSAGE_DATA6 + " char(20),"
				+ DBColumns.MESSAGE_DATA7 + " char(20));";

		db.execSQL(user_sql);
		db.execSQL(friend_group_sql);
		db.execSQL(friend_state_sql);
		db.execSQL(qqgroup_sql);
		db.execSQL(group_state_sql);
		db.execSQL(sys_group_sql);
		db.execSQL(message_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
