package com.zyxb.qqxmpp.db3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB3Helper extends SQLiteOpenHelper {

	public DB3Helper(Context context) {
		super(context, "xmpp3.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 用户
		String user_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.USER_TABLE_NAME + "(" + DB3Columns.USER_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.USER_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.USER_NICKNAME + " char(20) not null,"
				+ DB3Columns.USER_PWD + " char(32)," + DB3Columns.USER_ICON
				+ " char(100)," + DB3Columns.USER_AGE + " integer,"
				+ DB3Columns.USER_GENDER + " char(1) default 'M',"
				+ DB3Columns.USER_EMAIL + " char(40),"
				+ DB3Columns.USER_LOCATION + " char(20),"
				+ DB3Columns.USER_LOGIN_DAYS + " integer default 0,"
				+ DB3Columns.USER_LEVEL
				+ " integer default 16,"
				+ DB3Columns.USER_REGISTER_TIME
				+ " long not null,"
				+ DB3Columns.USER_EXPORT_DAYS
				+ " integer default 1,"
				+ DB3Columns.USER_RENEW
				+ " text,"
				+ DB3Columns.USER_STATE
				+ " integer default 1,"
				+ DB3Columns.USER_BIRTHDAY
				+ " long,"
				+ DB3Columns.USER_CONSTELLATION
				+ " integer,"// 星座
				+ DB3Columns.USER_OCCUPATION
				+ " varchar(20),"// 职业
				+ DB3Columns.USER_COMPANY + " varchar(20),"
				+ DB3Columns.USER_SCHOOL + " varchar(20),"
				+ DB3Columns.USER_HOMETOWN + " varchar(20),"
				+ DB3Columns.USER_DESP + " text,"
				+ DB3Columns.USER_PERSONALITY_SIGNATURE + " text,"
				+ DB3Columns.USER_WEB_SPACE + " varchar(20));";

		String friend_group_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + "(" + DB3Columns.USER_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.FRIEND_GROUP_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.FRIEND_GROUP_NAME + " char(20) not null,"
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT
				+ " varchar(40) not null," + DB3Columns.FRIEND_GROUP_POSITION
				+ " integer not null);";

		String friend_state_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.FRIEND_STATE_TABLE_NAME + "("
				+ DB3Columns.FRIEND_STATE_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.FRIEND_STATE_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.FRIEND_STATE_USER_ACCOUNT
				+ " varchar(40) not null,"
				+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT
				+ " varchar(40) not null," + DB3Columns.FRIEND_STATE_POSITION
				+ " integer," + DB3Columns.FRIEND_STATE_LOGIN_CHANNEL
				+ " integer default 0," + DB3Columns.FRIEND_STATE_LOGIN_STATE
				+ " integer default 0," + DB3Columns.FRIEND_STATE_REMARK
				+ " varchar(20));";

		String qqgroup_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.GROUP_TABLE_NAME + "(" + DB3Columns.GROUP_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.GROUP_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.GROUP_NAME + " varchar(40) not null,"
				+ DB3Columns.GROUP_ICON + " varchar(100),"
				+ DB3Columns.GROUP_DESP + " text,"
				+ DB3Columns.GROUP_CREATE_TIME + " long not null,"
				+ DB3Columns.GROUP_CLASSIFICATION + " varchar(20));";

		String group_state_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.GROUP_STATE_TABLE_NAME + "("
				+ DB3Columns.GROUP_STATE_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.GROUP_STATE_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.GROUP_STATE_LOGIN_STATE + " integer default 0,"
				+ DB3Columns.GROUP_STATE_LOGIN_CHANNEL + " integer default 0,"
				+ DB3Columns.GROUP_STATE_INTER_TIME + " long not null,"
				+ DB3Columns.GROUP_STATE_GROUP_TITLE + " integer default 0,"
				+ DB3Columns.GROUP_STATE_MSG_SETTING + " integer default 0,"
				+ DB3Columns.GROUP_STATE_LEVEL + " integer default 0,"
				+ DB3Columns.GROUP_STATE_REMARK + " varchar(20),"
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT
				+ " varchar(40) not null,"
				+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT
				+ " varchar(40) not null);";

		String sys_group_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.SYSTEM_GROUP_TABLE_NAME + "("
				+ DB3Columns.SYSTEM_GROUP_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.SYSTEM_GROUP_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.SYSTEM_GROUP_NAME + " varchar(40) not null,"
				+ DB3Columns.SYSTEM_GROUP_ICON + " varchar(100),"
				+ DB3Columns.SYSTEM_GROUP_DESP + " text,"
				+ DB3Columns.SYSTEM_GROUP_TYPE + " integer default 0);";

		String message_sql = "CREATE TABLE IF NOT EXISTS "
				+ DB3Columns.MESSAGE_TABLE_NAME + "(" + DB3Columns.MESSAGE_ID
				+ " integer primary key autoincrement,"
				+ DB3Columns.MESSAGE_ACCOUNT + " varchar(40) not null,"
				+ DB3Columns.MESSAGE_FROM + " varchar(40) not null,"
				+ DB3Columns.MESSAGE_MSG + " text," + DB3Columns.MESSAGE_TO
				+ " varchar(40) not null," + DB3Columns.MESSAGE_CREATE_TIME
				+ " long not null," + DB3Columns.MESSAGE_STATE
				+ " integer default 0," + DB3Columns.MESSAGE_TYPE
				+ " integer not null," + DB3Columns.MESSAGE_DATA1
				+ " char(20)," + DB3Columns.MESSAGE_DATA2 + " char(20),"
				+ DB3Columns.MESSAGE_DATA3 + " char(20),"
				+ DB3Columns.MESSAGE_DATA4 + " char(20),"
				+ DB3Columns.MESSAGE_DATA5 + " char(20),"
				+ DB3Columns.MESSAGE_DATA6 + " char(20),"
				+ DB3Columns.MESSAGE_DATA7 + " char(20));";

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
