package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3SystemGroup;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3SystemGroupDAO;

public class DB3SystemGroupDAOImpl extends DB3SystemGroupDAO {

	public DB3SystemGroupDAOImpl(Context context) {
		super(context);
	}

	@Override
	public int add(DB3SystemGroup group) {
		db = getTransactionDB();

		// 是否已经存在
		if (isExist(group.getName())) {
			return DB3Columns.ERROR_SYSTEM_GROUP_EXISTS;
		}

		DB3SystemGroup sg = findByAccount(group.getAccount());
		if (sg != null) {
			return DB3Columns.ERROR_SYSTEM_GROUP_EXISTS;
		}

		// 添加
		db.execSQL("INSERT INTO " + DB3Columns.SYSTEM_GROUP_TABLE_NAME + "("
				+ DB3Columns.SYSTEM_GROUP_ACCOUNT + ","
				+ DB3Columns.SYSTEM_GROUP_NAME + ","
				+ DB3Columns.SYSTEM_GROUP_ICON + ","
				+ DB3Columns.SYSTEM_GROUP_DESP + ","
				+ DB3Columns.SYSTEM_GROUP_TYPE + ") VALUES(?,?,?,?,?)"

				, new String[] { group.getAccount(), group.getName(), group.getIcon(),
				group.getDesp(), group.getType() + "" });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3SystemGroup findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.SYSTEM_GROUP_TABLE_NAME + "  WHERE "
						+ DB3Columns.SYSTEM_GROUP_ACCOUNT + "=?",
				new String[] { account });
		DB3SystemGroup group = null;
		if (cursor.moveToFirst()) {
			group = DB3ObjectHelper.getDB3SystemGroup(cursor);
		}
		cursor.close();

		return group;
	}

	private boolean isExist(String name) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.SYSTEM_GROUP_TABLE_NAME + " WHERE "
				+ DB3Columns.SYSTEM_GROUP_NAME + "=?", new String[] { name });
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public int update(DB3SystemGroup group) {
		db = getTransactionDB();

		// 是否存在
		DB3SystemGroup sg = findByAccount(group.getAccount());
		if (sg == null) {
			return DB3Columns.ERROR_SYSTEM_GROUP_NOT_FOUND;
		}

		// 更新
		db.execSQL(
				"UPDATE " + DB3Columns.SYSTEM_GROUP_TABLE_NAME + " SET "
						+ DB3Columns.SYSTEM_GROUP_NAME + "=?,"
						+ DB3Columns.SYSTEM_GROUP_ICON + "=?,"
						+ DB3Columns.SYSTEM_GROUP_DESP + "=?,"
						+ DB3Columns.SYSTEM_GROUP_TYPE + "=? WHERE "
						+ DB3Columns.SYSTEM_GROUP_ACCOUNT + "=?",
				new Object[] { group.getName(), group.getIcon(),
						group.getDesp(), group.getType(), group.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3SystemGroup group) {
		db = getTransactionDB();

		// 是否存在
		DB3SystemGroup fg = findByAccount(group.getAccount());
		if (fg == null) {
			return DB3Columns.ERROR_SYSTEM_GROUP_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DB3Columns.SYSTEM_GROUP_TABLE_NAME
						+ " WHERE " + DB3Columns.SYSTEM_GROUP_ACCOUNT + "=?",
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

		db.execSQL("DELETE FROM " + DB3Columns.SYSTEM_GROUP_TABLE_NAME
				+ " WHERE 1=1");

		return DB3Columns.RESULT_OK;
	}

}
