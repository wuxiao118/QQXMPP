package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;
import com.zyxb.qqxmpp.util.MD5Encoder;

import java.util.Date;

public class DBUserDAOImpl extends DBUserDAO {

	public DBUserDAOImpl(Context context) {
		super(context);
	}

	@Override
	public int add(DBUser user) {
		db = getTransactionDB();

		DBUser u = findByAccount(user.getAccount());
		if (u != null) {
			return DBColumns.ERROR_USER_EXISTS;
		}

		db.execSQL(
				"INSERT INTO "
						+ DBColumns.USER_TABLE_NAME
						+ "( "
						+ DBColumns.USER_ACCOUNT
						+ ","
						+ DBColumns.USER_NICKNAME
						+ ","
						+ DBColumns.USER_PWD
						+ ","
						+ DBColumns.USER_ICON
						+ ","
						+ DBColumns.USER_AGE
						+ ","
						+ DBColumns.USER_GENDER
						+ ","
						+ DBColumns.USER_EMAIL
						+ ","
						+ DBColumns.USER_LOCATION
						+ ","
						+ DBColumns.USER_LOGIN_DAYS
						+ ","
						+ DBColumns.USER_LEVEL
						+ ","
						+ DBColumns.USER_REGISTER_TIME
						+ ","
						+ DBColumns.USER_EXPORT_DAYS
						+ ","
						+ DBColumns.USER_RENEW
						+ ","
						+ DBColumns.USER_STATE
						+ ","
						+ DBColumns.USER_BIRTHDAY
						+ ","
						+ DBColumns.USER_CONSTELLATION
						+ ","
						+ DBColumns.USER_OCCUPATION
						+ ","
						+ DBColumns.USER_COMPANY
						+ ","
						+ DBColumns.USER_SCHOOL
						+ ","
						+ DBColumns.USER_HOMETOWN
						+ ","
						+ DBColumns.USER_DESP
						+ ","
						+ DBColumns.USER_PERSONALITY_SIGNATURE
						+ ","
						+ DBColumns.USER_WEB_SPACE
						+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] { user.getAccount(), user.getNickname(),
						user.getPwd(), user.getIcon(), user.getAge(),
						user.getGender(), user.getEmail(), user.getLocation(),
						user.getLoginDays(), user.getLevel(),
						user.getRegisterTime(), user.getExportDays(),
						user.getRenew(), user.getState(), user.getBirthday(),
						user.getConstellation(), user.getOccupation(),
						user.getCompany(), user.getSchool(),
						user.getHometown(), user.getDesp(),
						user.getPersonalitySignature(), user.getWebSpace() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public DBUser findByAccount(String account) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.USER_TABLE_NAME + " WHERE "
				+ DBColumns.USER_ACCOUNT + "=?", new String[] { account });
		DBUser u = null;
		if (cursor.moveToFirst()) {
			u = DBObjectHelper.getDB3User(cursor);
		}
		cursor.close();

		return u;
	}

	@Override
	public int update(DBUser user) {
		db = getTransactionDB();

		// 查找
		DBUser u = findByAccount(user.getAccount());
		if (u == null) {
			return DBColumns.ERROR_USER_NOT_FOUND;
		}

		db.execSQL(
				"UPDATE "
						+ DBColumns.USER_TABLE_NAME
						+ " SET "
						// + DBColumns.USER_ACCOUNT + "=?,"
						+ DBColumns.USER_NICKNAME + "=?,"
						+ DBColumns.USER_PWD + "=?," + DBColumns.USER_ICON
						+ "=?," + DBColumns.USER_AGE + "=?,"
						+ DBColumns.USER_GENDER + "=?,"
						+ DBColumns.USER_EMAIL + "=?,"
						+ DBColumns.USER_LOCATION + "=?,"
						+ DBColumns.USER_LOGIN_DAYS + "=?,"
						+ DBColumns.USER_LEVEL + "=?,"
						+ DBColumns.USER_REGISTER_TIME + "=?,"
						+ DBColumns.USER_EXPORT_DAYS + "=?,"
						+ DBColumns.USER_RENEW + "=?," + DBColumns.USER_STATE
						+ "=?," + DBColumns.USER_BIRTHDAY + "=?,"
						+ DBColumns.USER_CONSTELLATION + "=?,"
						+ DBColumns.USER_OCCUPATION + "=?,"
						+ DBColumns.USER_COMPANY + "=?,"
						+ DBColumns.USER_SCHOOL + "=?,"
						+ DBColumns.USER_HOMETOWN + "=?,"
						+ DBColumns.USER_DESP + "=?,"
						+ DBColumns.USER_PERSONALITY_SIGNATURE + "=?,"
						+ DBColumns.USER_WEB_SPACE + "=? WHERE "
						+ DBColumns.USER_ACCOUNT + "=?",
				new Object[] { user.getNickname(), user.getPwd(),
						user.getIcon(), user.getAge(), user.getGender(),
						user.getEmail(), user.getLocation(),
						user.getLoginDays(), user.getLevel(),
						user.getRegisterTime(), user.getExportDays(),
						user.getRenew(), user.getState(), user.getBirthday(),
						user.getConstellation(), user.getOccupation(),
						user.getCompany(), user.getSchool(),
						user.getHometown(), user.getDesp(),
						user.getPersonalitySignature(), user.getWebSpace(),
						user.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBUser user) {
		db = getTransactionDB();

		// 查找
		DBUser u = findByAccount(user.getAccount());
		if (u == null) {
			return DBColumns.ERROR_USER_NOT_FOUND;
		}
		db.execSQL("DELETE FROM " + DBColumns.USER_TABLE_NAME + " WHERE "
						+ DBColumns.USER_ACCOUNT + "=?",
				new String[] { user.getAccount() + "" });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();
		db.execSQL("DELETE FROM " + DBColumns.USER_TABLE_NAME + " WHERE 1=1");

		return DBColumns.RESULT_OK;
	}

	@Override
	public DBUser findByName(String name) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.USER_TABLE_NAME + " WHERE "
				+ DBColumns.USER_NICKNAME + "=?", new String[] { name });
		DBUser u = null;
		if (cursor.moveToFirst()) {
			u = DBObjectHelper.getDB3User(cursor);
		}
		cursor.close();

		return u;
	}

	@Override
	public DBUser login(String name, String pwd) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.USER_TABLE_NAME + " WHERE "
				+ DBColumns.USER_NICKNAME + "=? AND " + DBColumns.USER_PWD
				+ "=?", new String[] { name, pwd });
		DBUser u = null;
		if (cursor.moveToFirst()) {
			// cursor.close();
			// return true;
			u = DBObjectHelper.getDB3User(cursor);
		}
		cursor.close();

		// return false;
		return u;
	}

	@Override
	public boolean isEmpty() {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.USER_TABLE_NAME, null);
		if (cursor.moveToFirst()) {
			cursor.close();
			return false;
		}
		cursor.close();

		return true;
	}

	@Override
	public String findMaxAccount() {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT " + DBColumns.USER_ACCOUNT
				+ " FROM " + DBColumns.USER_TABLE_NAME + " WHERE "
				+ DBColumns.USER_ID + "=(SELECT max(" + DBColumns.USER_ID
				+ ") FROM " + DBColumns.USER_TABLE_NAME + ")", null);
		String account = "100000";
		if (cursor.moveToFirst()) {
			account = cursor.getString(0);
		}
		cursor.close();

		return account;
	}

	@Override
	public DBUser find(XMPPUser ur) {
		DBUser u = findByName(ur.getJid());
		if (u == null) {
			db = getTransactionDB();
			// 添加
			u = new DBUser();
			String acc = getMaxAccount();
			u.setAccount(Integer.parseInt(acc) + 1 + "");
			u.setNickname(ur.getJid());
			u.setRegisterTime(new Date().getTime());
			u.setLocation("湖北 武汉");
			u.setPwd(MD5Encoder.encode(ur.getStatusMessage()));
			add(u);
		}

		return u;
	}

	@Override
	public String getMaxAccount() {
		db = getTransactionDB();
		String acc = "100000";

		Cursor cursor = db.rawQuery("SELECT " + DBColumns.USER_ACCOUNT
				+ " FROM " + DBColumns.USER_TABLE_NAME + " WHERE "
				+ DBColumns.USER_ID + "=(SELECT max(" + DBColumns.USER_ID
				+ ") FROM " + DBColumns.USER_TABLE_NAME + ")", null);
		if (cursor.moveToFirst()) {
			acc = cursor.getString(0);
		}
		cursor.close();

		return acc;
	}

}
