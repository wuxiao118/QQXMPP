package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;
import com.zyxb.qqxmpp.util.MD5Encoder;

import java.util.Date;

public class DB3UserDAOImpl extends DB3UserDAO {

	public DB3UserDAOImpl(Context context) {
		super(context);
	}

	@Override
	public int add(DB3User user) {
		db = getTransactionDB();

		DB3User u = findByAccount(user.getAccount());
		if (u != null) {
			return DB3Columns.ERROR_USER_EXISTS;
		}

		db.execSQL(
				"INSERT INTO "
						+ DB3Columns.USER_TABLE_NAME
						+ "( "
						+ DB3Columns.USER_ACCOUNT
						+ ","
						+ DB3Columns.USER_NICKNAME
						+ ","
						+ DB3Columns.USER_PWD
						+ ","
						+ DB3Columns.USER_ICON
						+ ","
						+ DB3Columns.USER_AGE
						+ ","
						+ DB3Columns.USER_GENDER
						+ ","
						+ DB3Columns.USER_EMAIL
						+ ","
						+ DB3Columns.USER_LOCATION
						+ ","
						+ DB3Columns.USER_LOGIN_DAYS
						+ ","
						+ DB3Columns.USER_LEVEL
						+ ","
						+ DB3Columns.USER_REGISTER_TIME
						+ ","
						+ DB3Columns.USER_EXPORT_DAYS
						+ ","
						+ DB3Columns.USER_RENEW
						+ ","
						+ DB3Columns.USER_STATE
						+ ","
						+ DB3Columns.USER_BIRTHDAY
						+ ","
						+ DB3Columns.USER_CONSTELLATION
						+ ","
						+ DB3Columns.USER_OCCUPATION
						+ ","
						+ DB3Columns.USER_COMPANY
						+ ","
						+ DB3Columns.USER_SCHOOL
						+ ","
						+ DB3Columns.USER_HOMETOWN
						+ ","
						+ DB3Columns.USER_DESP
						+ ","
						+ DB3Columns.USER_PERSONALITY_SIGNATURE
						+ ","
						+ DB3Columns.USER_WEB_SPACE
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

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3User findByAccount(String account) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.USER_TABLE_NAME + " WHERE "
				+ DB3Columns.USER_ACCOUNT + "=?", new String[] { account });
		DB3User u = null;
		if (cursor.moveToFirst()) {
			u = DB3ObjectHelper.getDB3User(cursor);
		}
		cursor.close();

		return u;
	}

	@Override
	public int update(DB3User user) {
		db = getTransactionDB();

		// 查找
		DB3User u = findByAccount(user.getAccount());
		if (u == null) {
			return DB3Columns.ERROR_USER_NOT_FOUND;
		}

		db.execSQL(
				"UPDATE "
						+ DB3Columns.USER_TABLE_NAME
						+ " SET "
						// + DB3Columns.USER_ACCOUNT + "=?,"
						+ DB3Columns.USER_NICKNAME + "=?,"
						+ DB3Columns.USER_PWD + "=?," + DB3Columns.USER_ICON
						+ "=?," + DB3Columns.USER_AGE + "=?,"
						+ DB3Columns.USER_GENDER + "=?,"
						+ DB3Columns.USER_EMAIL + "=?,"
						+ DB3Columns.USER_LOCATION + "=?,"
						+ DB3Columns.USER_LOGIN_DAYS + "=?,"
						+ DB3Columns.USER_LEVEL + "=?,"
						+ DB3Columns.USER_REGISTER_TIME + "=?,"
						+ DB3Columns.USER_EXPORT_DAYS + "=?,"
						+ DB3Columns.USER_RENEW + "=?," + DB3Columns.USER_STATE
						+ "=?," + DB3Columns.USER_BIRTHDAY + "=?,"
						+ DB3Columns.USER_CONSTELLATION + "=?,"
						+ DB3Columns.USER_OCCUPATION + "=?,"
						+ DB3Columns.USER_COMPANY + "=?,"
						+ DB3Columns.USER_SCHOOL + "=?,"
						+ DB3Columns.USER_HOMETOWN + "=?,"
						+ DB3Columns.USER_DESP + "=?,"
						+ DB3Columns.USER_PERSONALITY_SIGNATURE + "=?,"
						+ DB3Columns.USER_WEB_SPACE + "=? WHERE "
						+ DB3Columns.USER_ACCOUNT + "=?",
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

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3User user) {
		db = getTransactionDB();

		// 查找
		DB3User u = findByAccount(user.getAccount());
		if (u == null) {
			return DB3Columns.ERROR_USER_NOT_FOUND;
		}
		db.execSQL("DELETE FROM " + DB3Columns.USER_TABLE_NAME + " WHERE "
						+ DB3Columns.USER_ACCOUNT + "=?",
				new String[] { user.getAccount() + "" });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();
		db.execSQL("DELETE FROM " + DB3Columns.USER_TABLE_NAME + " WHERE 1=1");

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3User findByName(String name) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.USER_TABLE_NAME + " WHERE "
				+ DB3Columns.USER_NICKNAME + "=?", new String[] { name });
		DB3User u = null;
		if (cursor.moveToFirst()) {
			u = DB3ObjectHelper.getDB3User(cursor);
		}
		cursor.close();

		return u;
	}

	@Override
	public DB3User login(String name, String pwd) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.USER_TABLE_NAME + " WHERE "
				+ DB3Columns.USER_NICKNAME + "=? AND " + DB3Columns.USER_PWD
				+ "=?", new String[] { name, pwd });
		DB3User u = null;
		if (cursor.moveToFirst()) {
			// cursor.close();
			// return true;
			u = DB3ObjectHelper.getDB3User(cursor);
		}
		cursor.close();

		// return false;
		return u;
	}

	@Override
	public boolean isEmpty() {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.USER_TABLE_NAME, null);
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
		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.USER_ACCOUNT
				+ " FROM " + DB3Columns.USER_TABLE_NAME + " WHERE "
				+ DB3Columns.USER_ID + "=(SELECT max(" + DB3Columns.USER_ID
				+ ") FROM " + DB3Columns.USER_TABLE_NAME + ")", null);
		String account = "100000";
		if (cursor.moveToFirst()) {
			account = cursor.getString(0);
		}
		cursor.close();

		return account;
	}

	@Override
	public DB3User find(XMPPUser ur) {
		DB3User u = findByName(ur.getJid());
		if (u == null) {
			db = getTransactionDB();
			// 添加
			u = new DB3User();
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

		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.USER_ACCOUNT
				+ " FROM " + DB3Columns.USER_TABLE_NAME + " WHERE "
				+ DB3Columns.USER_ID + "=(SELECT max(" + DB3Columns.USER_ID
				+ ") FROM " + DB3Columns.USER_TABLE_NAME + ")", null);
		if (cursor.moveToFirst()) {
			acc = cursor.getString(0);
		}
		cursor.close();

		return acc;
	}

}
