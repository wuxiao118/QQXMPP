package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBGroupMapping;
import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DAOFactory;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBGroupMappingDAO;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;

import java.util.ArrayList;
import java.util.List;

public class DBGroupMappingDAOImpl extends DBGroupMappingDAO {
	private DBUserDAO userDao;
	private DBGroupDAO groupDao;

	public DBGroupMappingDAOImpl(Context context) {
		super(context);

		userDao = DAOFactory.getDB3UserDAO(context);
		groupDao = DAOFactory.getDB3GroupDAO(context);
	}

	@Override
	public int add(DBGroupMapping mapping) {
		db = getTransactionDB();

		// group是否存在
		DBGroup group = groupDao
				.findByAccount(mapping.getGroup().getAccount());
		if (group == null) {
			return DBColumns.ERROR_GROUP_NOT_FOUND;
		}

		// user是否存在,不存在就添加
		DBUser user = userDao.findByAccount(mapping.getUser().getAccount());
		if (user == null) {
			userDao.add(mapping.getUser());
		}

		// 是否已经存在(群中成员不能重复)
		if (isExistUser(mapping.getGroup(), mapping.getUser())) {
			return DBColumns.ERROR_GROUP_STATE_EXISTS;
		}

		// 添加
		db.execSQL("INSERT INTO " + DBColumns.GROUP_STATE_TABLE_NAME + "("
						+ DBColumns.GROUP_STATE_ACCOUNT + ","
						+ DBColumns.GROUP_STATE_LOGIN_STATE + ","
						+ DBColumns.GROUP_STATE_LOGIN_CHANNEL + ","
						+ DBColumns.GROUP_STATE_INTER_TIME + ","
						+ DBColumns.GROUP_STATE_GROUP_TITLE + ","
						+ DBColumns.GROUP_STATE_MSG_SETTING + ","
						+ DBColumns.GROUP_STATE_LEVEL + ","
						+ DBColumns.GROUP_STATE_REMARK + ","
						+ DBColumns.GROUP_STATE_USER_ACCOUNT + ","
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT
						+ ") VALUES(?,?,?,?,?,?,?,?,?,?)",
				new Object[] { mapping.getAccount(), mapping.getLoginState(),
						mapping.getLoginChannel(), mapping.getInterTime(),
						mapping.getGroupTitle(), mapping.getMsgSetting(),
						mapping.getLevel(), mapping.getRemark(),
						mapping.getUser().getAccount(),
						mapping.getGroup().getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int update(DBGroupMapping mapping) {
		db = getTransactionDB();

		// 是否存在
		DBGroupMapping m = findByAccount(mapping.getAccount());
		if (m == null) {
			return DBColumns.ERROR_GROUP_STATE_NOT_FOUND;
		}

		// 是否需要查找user和group是否存在,在业务中查找,当需要的时候调用userDao,groupDao

		// 更新
		db.execSQL(
				"UPDATE " + DBColumns.GROUP_STATE_TABLE_NAME + " SET "
						+ DBColumns.GROUP_STATE_LOGIN_STATE + "=?,"
						+ DBColumns.GROUP_STATE_LOGIN_CHANNEL + "=?,"
						+ DBColumns.GROUP_STATE_INTER_TIME + "=?,"
						+ DBColumns.GROUP_STATE_GROUP_TITLE + "=?,"
						+ DBColumns.GROUP_STATE_MSG_SETTING + "=?,"
						+ DBColumns.GROUP_STATE_LEVEL + "=?,"
						+ DBColumns.GROUP_STATE_REMARK + "=?,"
						+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=?,"
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT
						+ "=? VALUES(?,?,?,?,?,?,?,?,?)",
				new Object[] { mapping.getLoginState(),
						mapping.getLoginChannel(), mapping.getInterTime(),
						mapping.getGroupTitle(), mapping.getMsgSetting(),
						mapping.getLevel(), mapping.getRemark(),
						mapping.getUser().getAccount(),
						mapping.getGroup().getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBGroupMapping mapping) {
		db = getTransactionDB();

		// 是否存在
		DBGroupMapping m = findByAccount(mapping.getAccount());
		if (m == null) {
			return DBColumns.ERROR_GROUP_STATE_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DBColumns.GROUP_STATE_TABLE_NAME
						+ " WHERE " + DBColumns.GROUP_STATE_ACCOUNT + "=?",
				new String[] { mapping.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public DBGroupMapping findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_STATE_ACCOUNT + "=?",
				new String[] { account });
		DBGroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			// 查找user
			DBUser user = userDao.findByAccount(cursor.getString(cursor
					.getColumnIndex(DBColumns.GROUP_STATE_USER_ACCOUNT)));
			DBGroup group = groupDao.findByAccount(cursor.getString(cursor
					.getColumnIndex(DBColumns.GROUP_STATE_GROUP_ACCOUNT)));

			mapping = DBObjectHelper.getDB3GroupMapping(user, group, cursor);
		}
		cursor.close();

		return mapping;
	}

	@Override
	public int delete(DBUser user) {
		db = getTransactionDB();

		// user不存在
		DBUser u = userDao.findByAccount(user.getAccount());
		if (u == null) {
			return DBColumns.ERROR_USER_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DBColumns.GROUP_STATE_TABLE_NAME
						+ " WHERE " + DBColumns.GROUP_STATE_USER_ACCOUNT + "=?",
				new String[] { user.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DBColumns.GROUP_STATE_TABLE_NAME
				+ " WHERE 1=1");

		return DBColumns.RESULT_OK;
	}

	@Override
	public List<DBUser> findByGroup(DBGroup group) {
		db = getTransactionDB();

		List<DBUser> users = new ArrayList<DBUser>();

		// group是否存在
		DBGroup g = groupDao.findByAccount(group.getAccount());
		if (g == null) {
			return users;
		}

		// 查找account
		Cursor cursor = db.rawQuery("SELECT "
						+ DBColumns.GROUP_STATE_USER_ACCOUNT + " FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { group.getAccount() });
		String[] userAccounts = new String[cursor.getCount()];
		int i = 0;
		while (cursor.moveToFirst()) {
			userAccounts[i++] = cursor.getString(0);
		}
		cursor.close();

		// 查找user
		for (int j = 0; j < userAccounts.length; j++) {
			users.add(userDao.findByAccount(userAccounts[i]));
		}

		return users;
	}

	@Override
	public boolean isExistUser(DBGroup group, DBUser user) {
		// db = getTransactionDB();
		//
		// Cursor cursor = db.rawQuery("SELECT * FROM "
		// + DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
		// + DBColumns.GROUP_STATE_USER_ACCOUNT + "=? AND "
		// + DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
		// mUser.getAccount(), group.getAccount() });
		// // System.out.println("mUser:" + mUser.getAccount() + ",group:" +
		// // group.getAccount() + "  " + cursor.getCount());
		// if (cursor.moveToFirst()) {
		// cursor.close();
		// return true;
		// }
		// cursor.close();
		//
		// return false;

		return isExistUser(group.getAccount(), user.getAccount());
	}

	@Override
	public boolean isExistUser(String groupAccount, String userAccount) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=? AND "
				+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
				userAccount, groupAccount });
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public List<DBGroup> findByUser(DBUser user) {
		// select q.* from group_state g,qqgroup q where user_account='100000'
		// and g.group_account = q.account;

		db = getTransactionDB();
		// select group_account from group_state where user_account='100000';
		// 查找user存在的群
		Cursor cursor = db.rawQuery("SELECT "
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + " WHERE "
						+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=?",
				new String[] { user.getAccount() });
		String[] groupAccount = new String[cursor.getCount()];
		int i = 0;
		while (cursor.moveToNext()) {
			groupAccount[i++] = cursor.getString(0);
		}
		cursor.close();

		// 根据group account查找group
		List<DBGroup> groups = new ArrayList<DBGroup>();
		for (int j = 0; j < groupAccount.length; j++) {
			groups.add(groupDao.findByAccount(groupAccount[i]));
		}

		return groups;
	}

	@Override
	public List<DBGroup> findSortedByUser(DBUser user) {
		// user查找群,并按照我创建的群,我管理的群,我加入的群排序
		// select g.*,s.level from group_state s,qqgroup g where
		// s.[group_account]=g.[account] and s.[user_account]='100000' order by
		// s.level desc;
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT g.*,s."
						+ DBColumns.GROUP_STATE_LEVEL + " FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " s,"
						+ DBColumns.GROUP_TABLE_NAME + " g WHERE s."
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=g."
						+ DBColumns.GROUP_ACCOUNT + " AND s."
						+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=? ORDER BY s."
						+ DBColumns.GROUP_STATE_LEVEL + " DESC",
				new String[] { user.getAccount() });

		return DBObjectHelper.getDB3Groups(cursor);
	}

	@Override
	public List<DBGroupMapping> findMappingByGroup(DBGroup group) {
		// 查找群相关的mapping(group拥有的user,user在group中的状态)
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { group.getAccount() });
		List<DBGroupMapping> mappings = new ArrayList<DBGroupMapping>();
		while (cursor.moveToNext()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.GROUP_STATE_USER_ACCOUNT));
			DBUser user = userDao.findByAccount(userAccount);
			mappings.add(DBObjectHelper
					.getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public List<DBGroupMapping> findMappingByUser(DBUser user) {
		// 查找相关的mapping (user拥有的group,user在群中的状态属性)
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=? ORDER BY "
						+ DBColumns.GROUP_STATE_LEVEL + " DESC",
				new String[] { user.getAccount() });
		List<DBGroupMapping> mappings = new ArrayList<DBGroupMapping>();
		while (cursor.moveToNext()) {
			String groupAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.GROUP_STATE_GROUP_ACCOUNT));
			DBGroup group = groupDao.findByAccount(groupAccount);
			mappings.add(DBObjectHelper
					.getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public String getRemark(String userAccount, String groupAccount) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT " + DBColumns.GROUP_STATE_REMARK
				+ " FROM " + DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=? AND "
				+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
				userAccount, groupAccount });
		String remark = null;
		if (cursor.moveToFirst()) {
			remark = cursor.getString(0);
		}
		cursor.close();

		if(remark == null){
			remark = userDao.findByAccount(userAccount).getNickname();
		}

		return remark;
	}

	@Override
	public void close() {
		super.close();
		userDao.close();
		groupDao.close();
	}

	@Override
	public int getGroupTitle(String userAccount, String groupAccount) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT "
				+ DBColumns.GROUP_STATE_GROUP_TITLE + " FROM "
				+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=? AND "
				+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
				userAccount, groupAccount });
		int groupTitle = -1;
		if (cursor.moveToFirst()) {
			groupTitle = cursor.getInt(0);
		}
		cursor.close();

		return groupTitle;
	}

	@Override
	public int getGroupNum(String groupAccount) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT count(*) FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { groupAccount });
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();

		return count;
	}

	@Override
	public List<DBGroupMapping> findByGroup(String groupAccount) {
		db = getTransactionDB();

		DBGroup group = groupDao.findByAccount(groupAccount);
		// 查找
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { groupAccount });
		List<DBGroupMapping> mappings = new ArrayList<DBGroupMapping>();
		while (cursor.moveToNext()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.GROUP_STATE_USER_ACCOUNT));
			DBUser user = userDao.findByAccount(userAccount);
			mappings.add(DBObjectHelper
					.getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public DBGroupMapping getFriend(String groupAccount, String contactAccount) {
		db = getTransactionDB();

		// 查找group,mUser
		DBUser user = userDao.findByAccount(contactAccount);
		DBGroup group = groupDao.findByAccount(groupAccount);

		// 查询
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=?" + " AND "
				+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
				contactAccount, groupAccount });

		DBGroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			mapping = DBObjectHelper.getDB3GroupMapping(user, group, cursor);
		}
		cursor.close();

		return mapping;
	}

	@Override
	public String getMaxAccount() {


		return null;
	}
}
