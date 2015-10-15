package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3GroupDAO;

public class DB3GroupDAOImpl extends DB3GroupDAO {

	public DB3GroupDAOImpl(Context context) {
		super(context);
	}

	@Override
	public int add(DB3Group group) {
		db = getTransactionDB();

		// 是否已经存在
		DB3Group g = findByAccount(group.getAccount());
		if (g != null) {
			return DB3Columns.ERROR_GROUP_EXISTS;
		}

		// 添加
		db.execSQL(
				"INSERT INTO " + DB3Columns.GROUP_TABLE_NAME + "("
						+ DB3Columns.GROUP_ACCOUNT + ","
						+ DB3Columns.GROUP_NAME + "," + DB3Columns.GROUP_ICON
						+ "," + DB3Columns.GROUP_DESP + ","
						+ DB3Columns.GROUP_CREATE_TIME + ","
						+ DB3Columns.GROUP_CLASSIFICATION
						+ ") VALUES(?,?,?,?,?,?)",
				new String[] { group.getAccount(), group.getName(),
						group.getIcon(), group.getDesp(),
						group.getCreateTime() + "", group.getClassification() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3Group findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.GROUP_TABLE_NAME + " WHERE "
				+ DB3Columns.GROUP_ACCOUNT + "=?", new String[] { account });
		DB3Group group = null;
		if (cursor.moveToFirst()) {
			group = DB3ObjectHelper.getDB3Group(cursor);
		}
		cursor.close();

		return group;
	}

	@Override
	public int update(DB3Group group) {
		db = getTransactionDB();

		// 是否存在
		DB3Group g = findByAccount(group.getAccount());
		if (g == null) {
			return DB3Columns.ERROR_GROUP_NOT_FOUND;
		}

		// 更新
		db.execSQL("UPDATE " + DB3Columns.GROUP_TABLE_NAME + " SET "
				+ DB3Columns.GROUP_CLASSIFICATION + "=?,"
				+ DB3Columns.GROUP_NAME + "=?," + DB3Columns.GROUP_ICON + "=?,"
				+ DB3Columns.GROUP_DESP + "=?," + DB3Columns.GROUP_CREATE_TIME
				+ "=? WHERE " + DB3Columns.GROUP_ACCOUNT + "=?", new Object[] {
				group.getClassification(), group.getName(), group.getIcon(),
				group.getDesp(), group.getCreateTime(), group.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3Group group) {
		db = getTransactionDB();

		// 是否存在
		DB3Group g = findByAccount(group.getAccount());
		if (g == null) {
			return DB3Columns.ERROR_GROUP_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DB3Columns.GROUP_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_ACCOUNT + "=?",
				new String[] { group.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3User user) {
		throw new UnsupportedOperationException("不支持该方法");
	}

	@Override
	public int clear() {
		db = getTransactionDB();
		db.execSQL("DELETE FROM " + DB3Columns.GROUP_TABLE_NAME + " WHERE 1=1");

		return DB3Columns.RESULT_OK;
	}

}
