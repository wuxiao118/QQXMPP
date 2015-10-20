package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBSystemGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBSystemGroupDAO;

public class DBSystemGroupDAOImpl extends DBSystemGroupDAO {

	public DBSystemGroupDAOImpl(Context context) {
		super(context);
	}

	@Override
	public int add(DBSystemGroup group) {
		db = getTransactionDB();

		// 是否已经存在
		if (isExist(group.getName())) {
			return DBColumns.ERROR_SYSTEM_GROUP_EXISTS;
		}

		DBSystemGroup sg = findByAccount(group.getAccount());
		if (sg != null) {
			return DBColumns.ERROR_SYSTEM_GROUP_EXISTS;
		}

		// 添加
		db.execSQL("INSERT INTO " + DBColumns.SYSTEM_GROUP_TABLE_NAME + "("
				+ DBColumns.SYSTEM_GROUP_ACCOUNT + ","
				+ DBColumns.SYSTEM_GROUP_NAME + ","
				+ DBColumns.SYSTEM_GROUP_ICON + ","
				+ DBColumns.SYSTEM_GROUP_DESP + ","
				+ DBColumns.SYSTEM_GROUP_TYPE + ") VALUES(?,?,?,?,?)"

				, new String[] { group.getAccount(), group.getName(), group.getIcon(),
				group.getDesp(), group.getType() + "" });

		return DBColumns.RESULT_OK;
	}

	@Override
	public DBSystemGroup findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.SYSTEM_GROUP_TABLE_NAME + "  WHERE "
						+ DBColumns.SYSTEM_GROUP_ACCOUNT + "=?",
				new String[] { account });
		DBSystemGroup group = null;
		if (cursor.moveToFirst()) {
			group = DBObjectHelper.getDB3SystemGroup(cursor);
		}
		cursor.close();

		return group;
	}

	private boolean isExist(String name) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.SYSTEM_GROUP_TABLE_NAME + " WHERE "
				+ DBColumns.SYSTEM_GROUP_NAME + "=?", new String[] { name });
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public int update(DBSystemGroup group) {
		db = getTransactionDB();

		// 是否存在
		DBSystemGroup sg = findByAccount(group.getAccount());
		if (sg == null) {
			return DBColumns.ERROR_SYSTEM_GROUP_NOT_FOUND;
		}

		// 更新
		db.execSQL(
				"UPDATE " + DBColumns.SYSTEM_GROUP_TABLE_NAME + " SET "
						+ DBColumns.SYSTEM_GROUP_NAME + "=?,"
						+ DBColumns.SYSTEM_GROUP_ICON + "=?,"
						+ DBColumns.SYSTEM_GROUP_DESP + "=?,"
						+ DBColumns.SYSTEM_GROUP_TYPE + "=? WHERE "
						+ DBColumns.SYSTEM_GROUP_ACCOUNT + "=?",
				new Object[] { group.getName(), group.getIcon(),
						group.getDesp(), group.getType(), group.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBSystemGroup group) {
		db = getTransactionDB();

		// 是否存在
		DBSystemGroup fg = findByAccount(group.getAccount());
		if (fg == null) {
			return DBColumns.ERROR_SYSTEM_GROUP_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DBColumns.SYSTEM_GROUP_TABLE_NAME
						+ " WHERE " + DBColumns.SYSTEM_GROUP_ACCOUNT + "=?",
				new String[] { group.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBUser user) {

		throw new UnsupportedOperationException("不支持该方法");
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DBColumns.SYSTEM_GROUP_TABLE_NAME
				+ " WHERE 1=1");

		return DBColumns.RESULT_OK;
	}

	@Override
	public String getMaxAccount() {
		String acc = "";

		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT " + DBColumns.SYSTEM_GROUP_ACCOUNT
				+ " FROM " + DBColumns.SYSTEM_GROUP_TABLE_NAME + " WHERE "
				+ DBColumns.SYSTEM_GROUP_ID + "=(SELECT max(" + DBColumns.SYSTEM_GROUP_ID
				+ ") FROM " + DBColumns.SYSTEM_GROUP_TABLE_NAME + ")", null);
		if (cursor.moveToFirst()) {
			acc = cursor.getString(0);
		}
		cursor.close();

		return acc;
	}

}
