package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBGroupDAO;

public class DBGroupDAOImpl extends DBGroupDAO {

	public DBGroupDAOImpl(Context context) {
		super(context);
	}

	@Override
	public int add(DBGroup group) {
		db = getTransactionDB();

		// 是否已经存在
		DBGroup g = findByAccount(group.getAccount());
		if (g != null) {
			return DBColumns.ERROR_GROUP_EXISTS;
		}

		// 添加
		db.execSQL(
				"INSERT INTO " + DBColumns.GROUP_TABLE_NAME + "("
						+ DBColumns.GROUP_ACCOUNT + ","
						+ DBColumns.GROUP_NAME + "," + DBColumns.GROUP_ICON
						+ "," + DBColumns.GROUP_DESP + ","
						+ DBColumns.GROUP_CREATE_TIME + ","
						+ DBColumns.GROUP_CLASSIFICATION
						+ ") VALUES(?,?,?,?,?,?)",
				new String[] { group.getAccount(), group.getName(),
						group.getIcon(), group.getDesp(),
						group.getCreateTime() + "", group.getClassification() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public DBGroup findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.GROUP_TABLE_NAME + " WHERE "
				+ DBColumns.GROUP_ACCOUNT + "=?", new String[] { account });
		DBGroup group = null;
		if (cursor.moveToFirst()) {
			group = DBObjectHelper.getDB3Group(cursor);
		}
		cursor.close();

		return group;
	}

	@Override
	public int update(DBGroup group) {
		db = getTransactionDB();

		// 是否存在
		DBGroup g = findByAccount(group.getAccount());
		if (g == null) {
			return DBColumns.ERROR_GROUP_NOT_FOUND;
		}

		// 更新
		db.execSQL("UPDATE " + DBColumns.GROUP_TABLE_NAME + " SET "
				+ DBColumns.GROUP_CLASSIFICATION + "=?,"
				+ DBColumns.GROUP_NAME + "=?," + DBColumns.GROUP_ICON + "=?,"
				+ DBColumns.GROUP_DESP + "=?," + DBColumns.GROUP_CREATE_TIME
				+ "=? WHERE " + DBColumns.GROUP_ACCOUNT + "=?", new Object[] {
				group.getClassification(), group.getName(), group.getIcon(),
				group.getDesp(), group.getCreateTime(), group.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBGroup group) {
		db = getTransactionDB();

		// 是否存在
		DBGroup g = findByAccount(group.getAccount());
		if (g == null) {
			return DBColumns.ERROR_GROUP_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DBColumns.GROUP_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_ACCOUNT + "=?",
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
		db.execSQL("DELETE FROM " + DBColumns.GROUP_TABLE_NAME + " WHERE 1=1");

		return DBColumns.RESULT_OK;
	}

}
