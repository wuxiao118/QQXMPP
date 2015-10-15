package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3GroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3GroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;

import java.util.ArrayList;
import java.util.List;

public class DB3GroupMappingDAOImpl extends DB3GroupMappingDAO {
	private DB3UserDAO userDao;
	private DB3GroupDAO groupDao;

	public DB3GroupMappingDAOImpl(Context context) {
		super(context);

		userDao = DAOFactory.getDB3UserDAO(context);
		groupDao = DAOFactory.getDB3GroupDAO(context);
	}

	@Override
	public int add(DB3GroupMapping mapping) {
		db = getTransactionDB();

		// group是否存在
		DB3Group group = groupDao
				.findByAccount(mapping.getGroup().getAccount());
		if (group == null) {
			return DB3Columns.ERROR_GROUP_NOT_FOUND;
		}

		// user是否存在,不存在就添加
		DB3User user = userDao.findByAccount(mapping.getUser().getAccount());
		if (user == null) {
			userDao.add(mapping.getUser());
		}

		// 是否已经存在(群中成员不能重复)
		if (isExistUser(mapping.getGroup(), mapping.getUser())) {
			return DB3Columns.ERROR_GROUP_STATE_EXISTS;
		}

		// 添加
		db.execSQL("INSERT INTO " + DB3Columns.GROUP_STATE_TABLE_NAME + "("
						+ DB3Columns.GROUP_STATE_ACCOUNT + ","
						+ DB3Columns.GROUP_STATE_LOGIN_STATE + ","
						+ DB3Columns.GROUP_STATE_LOGIN_CHANNEL + ","
						+ DB3Columns.GROUP_STATE_INTER_TIME + ","
						+ DB3Columns.GROUP_STATE_GROUP_TITLE + ","
						+ DB3Columns.GROUP_STATE_MSG_SETTING + ","
						+ DB3Columns.GROUP_STATE_LEVEL + ","
						+ DB3Columns.GROUP_STATE_REMARK + ","
						+ DB3Columns.GROUP_STATE_USER_ACCOUNT + ","
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT
						+ ") VALUES(?,?,?,?,?,?,?,?,?,?)",
				new Object[] { mapping.getAccount(), mapping.getLoginState(),
						mapping.getLoginChannel(), mapping.getInterTime(),
						mapping.getGroupTitle(), mapping.getMsgSetting(),
						mapping.getLevel(), mapping.getRemark(),
						mapping.getUser().getAccount(),
						mapping.getGroup().getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int update(DB3GroupMapping mapping) {
		db = getTransactionDB();

		// 是否存在
		DB3GroupMapping m = findByAccount(mapping.getAccount());
		if (m == null) {
			return DB3Columns.ERROR_GROUP_STATE_NOT_FOUND;
		}

		// 是否需要查找user和group是否存在,在业务中查找,当需要的时候调用userDao,groupDao

		// 更新
		db.execSQL(
				"UPDATE " + DB3Columns.GROUP_STATE_TABLE_NAME + " SET "
						+ DB3Columns.GROUP_STATE_LOGIN_STATE + "=?,"
						+ DB3Columns.GROUP_STATE_LOGIN_CHANNEL + "=?,"
						+ DB3Columns.GROUP_STATE_INTER_TIME + "=?,"
						+ DB3Columns.GROUP_STATE_GROUP_TITLE + "=?,"
						+ DB3Columns.GROUP_STATE_MSG_SETTING + "=?,"
						+ DB3Columns.GROUP_STATE_LEVEL + "=?,"
						+ DB3Columns.GROUP_STATE_REMARK + "=?,"
						+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=?,"
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT
						+ "=? VALUES(?,?,?,?,?,?,?,?,?)",
				new Object[] { mapping.getLoginState(),
						mapping.getLoginChannel(), mapping.getInterTime(),
						mapping.getGroupTitle(), mapping.getMsgSetting(),
						mapping.getLevel(), mapping.getRemark(),
						mapping.getUser().getAccount(),
						mapping.getGroup().getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3GroupMapping mapping) {
		db = getTransactionDB();

		// 是否存在
		DB3GroupMapping m = findByAccount(mapping.getAccount());
		if (m == null) {
			return DB3Columns.ERROR_GROUP_STATE_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DB3Columns.GROUP_STATE_TABLE_NAME
						+ " WHERE " + DB3Columns.GROUP_STATE_ACCOUNT + "=?",
				new String[] { mapping.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3GroupMapping findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_STATE_ACCOUNT + "=?",
				new String[] { account });
		DB3GroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			// 查找user
			DB3User user = userDao.findByAccount(cursor.getString(cursor
					.getColumnIndex(DB3Columns.GROUP_STATE_USER_ACCOUNT)));
			DB3Group group = groupDao.findByAccount(cursor.getString(cursor
					.getColumnIndex(DB3Columns.GROUP_STATE_GROUP_ACCOUNT)));

			mapping = DB3ObjectHelper.getDB3GroupMapping(user, group, cursor);
		}
		cursor.close();

		return mapping;
	}

	@Override
	public int delete(DB3User user) {
		db = getTransactionDB();

		// user不存在
		DB3User u = userDao.findByAccount(user.getAccount());
		if (u == null) {
			return DB3Columns.ERROR_USER_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DB3Columns.GROUP_STATE_TABLE_NAME
						+ " WHERE " + DB3Columns.GROUP_STATE_USER_ACCOUNT + "=?",
				new String[] { user.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DB3Columns.GROUP_STATE_TABLE_NAME
				+ " WHERE 1=1");

		return DB3Columns.RESULT_OK;
	}

	@Override
	public List<DB3User> findByGroup(DB3Group group) {
		db = getTransactionDB();

		List<DB3User> users = new ArrayList<DB3User>();

		// group是否存在
		DB3Group g = groupDao.findByAccount(group.getAccount());
		if (g == null) {
			return users;
		}

		// 查找account
		Cursor cursor = db.rawQuery("SELECT "
						+ DB3Columns.GROUP_STATE_USER_ACCOUNT + " FROM "
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?",
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
	public boolean isExistUser(DB3Group group, DB3User user) {
		// db = getTransactionDB();
		//
		// Cursor cursor = db.rawQuery("SELECT * FROM "
		// + DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
		// + DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? AND "
		// + DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
		// user.getAccount(), group.getAccount() });
		// // System.out.println("user:" + user.getAccount() + ",group:" +
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
				+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? AND "
				+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
				userAccount, groupAccount });
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public List<DB3Group> findByUser(DB3User user) {
		// select q.* from group_state g,qqgroup q where user_account='100000'
		// and g.group_account = q.account;

		db = getTransactionDB();
		// select group_account from group_state where user_account='100000';
		// 查找user存在的群
		Cursor cursor = db.rawQuery("SELECT "
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + " WHERE "
						+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=?",
				new String[] { user.getAccount() });
		String[] groupAccount = new String[cursor.getCount()];
		int i = 0;
		while (cursor.moveToNext()) {
			groupAccount[i++] = cursor.getString(0);
		}
		cursor.close();

		// 根据group account查找group
		List<DB3Group> groups = new ArrayList<DB3Group>();
		for (int j = 0; j < groupAccount.length; j++) {
			groups.add(groupDao.findByAccount(groupAccount[i]));
		}

		return groups;
	}

	@Override
	public List<DB3Group> findSortedByUser(DB3User user) {
		// user查找群,并按照我创建的群,我管理的群,我加入的群排序
		// select g.*,s.level from group_state s,qqgroup g where
		// s.[group_account]=g.[account] and s.[user_account]='100000' order by
		// s.level desc;
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT g.*,s."
						+ DB3Columns.GROUP_STATE_LEVEL + " FROM "
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " s,"
						+ DB3Columns.GROUP_TABLE_NAME + " g WHERE s."
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=g."
						+ DB3Columns.GROUP_ACCOUNT + " AND s."
						+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? ORDER BY s."
						+ DB3Columns.GROUP_STATE_LEVEL + " DESC",
				new String[] { user.getAccount() });

		return DB3ObjectHelper.getDB3Groups(cursor);
	}

	@Override
	public List<DB3GroupMapping> findMappingByGroup(DB3Group group) {
		// 查找群相关的mapping(group拥有的user,user在group中的状态)
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { group.getAccount() });
		List<DB3GroupMapping> mappings = new ArrayList<DB3GroupMapping>();
		while (cursor.moveToNext()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.GROUP_STATE_USER_ACCOUNT));
			DB3User user = userDao.findByAccount(userAccount);
			mappings.add(DB3ObjectHelper
					.getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public List<DB3GroupMapping> findMappingByUser(DB3User user) {
		// 查找相关的mapping (user拥有的group,user在群中的状态属性)
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? ORDER BY "
						+ DB3Columns.GROUP_STATE_LEVEL + " DESC",
				new String[] { user.getAccount() });
		List<DB3GroupMapping> mappings = new ArrayList<DB3GroupMapping>();
		while (cursor.moveToNext()) {
			String groupAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.GROUP_STATE_GROUP_ACCOUNT));
			DB3Group group = groupDao.findByAccount(groupAccount);
			mappings.add(DB3ObjectHelper
					.getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public String getRemark(String userAccount, String groupAccount) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.GROUP_STATE_REMARK
				+ " FROM " + DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? AND "
				+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
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
				+ DB3Columns.GROUP_STATE_GROUP_TITLE + " FROM "
				+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? AND "
				+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
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
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { groupAccount });
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();

		return count;
	}

	@Override
	public List<DB3GroupMapping> findByGroup(String groupAccount) {
		db = getTransactionDB();

		DB3Group group = groupDao.findByAccount(groupAccount);
		// 查找
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?",
				new String[] { groupAccount });
		List<DB3GroupMapping> mappings = new ArrayList<DB3GroupMapping>();
		while (cursor.moveToNext()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.GROUP_STATE_USER_ACCOUNT));
			DB3User user = userDao.findByAccount(userAccount);
			mappings.add(DB3ObjectHelper
					.getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public DB3GroupMapping getFriend(String groupAccount, String contactAccount) {
		db = getTransactionDB();

		// 查找group,user
		DB3User user = userDao.findByAccount(contactAccount);
		DB3Group group = groupDao.findByAccount(groupAccount);

		// 查询
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.GROUP_STATE_TABLE_NAME + " WHERE "
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=?" + " AND "
				+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + "=?", new String[] {
				contactAccount, groupAccount });

		DB3GroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			mapping = DB3ObjectHelper.getDB3GroupMapping(user, group, cursor);
		}
		cursor.close();

		return mapping;
	}

	@Override
	public String getMaxAccount() {


		return null;
	}
}
