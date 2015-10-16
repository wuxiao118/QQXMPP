package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBFriendGroup;
import com.zyxb.qqxmpp.bean.po.DBFriendGroupMapping;
import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBFriendGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBFriendGroupMappingDAO;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBFriendGroupMappingDAOImpl extends DBFriendGroupMappingDAO {
	private DBUserDAO userDao;
	private DBFriendGroupDAO fgDao;
	private static final Object synchObj = new Object();

	public DBFriendGroupMappingDAOImpl(Context context) {
		super(context);

		userDao = new DBUserDAOImpl(context);
		fgDao = new DBFriendGroupDAOImpl(context);
	}

	@Override
	public int add(DBFriendGroupMapping mapping) {
		db = getTransactionDB();

		// 查找user
		DBUser user = userDao.findByAccount(mapping.getUser().getAccount());
		if (user == null) {
			return DBColumns.ERROR_USER_NOT_FOUND;
		}

		// 查找group
		DBFriendGroup fg = fgDao.findByAccount(mapping.getFriendGroup()
				.getAccount());
		if (fg == null) {
			fgDao.add(mapping.getFriendGroup());
		}

		// 是否已经存在
		if (isExists(mapping.getUser().getAccount(), mapping.getFriendGroup()
				.getAccount())) {
			return DBColumns.ERROR_FRIEND_STATE_EXISTS;
		}

		// 添加
		db.execSQL("INSERT INTO " + DBColumns.FRIEND_STATE_TABLE_NAME + "("
				+ DBColumns.FRIEND_STATE_ACCOUNT + ","
				+ DBColumns.FRIEND_STATE_USER_ACCOUNT + ","
				+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + ","
				+ DBColumns.FRIEND_STATE_LOGIN_STATE + ","
				+ DBColumns.FRIEND_STATE_LOGIN_CHANNEL + ","
				+ DBColumns.FRIEND_STATE_POSITION + ","
				+ DBColumns.FRIEND_STATE_REMARK + ") VALUES(?,?,?,?,?,?,?)"

				, new String[] { mapping.getAccount(), mapping.getUser().getAccount(),
				mapping.getFriendGroup().getAccount(),
				mapping.getLoginState() + "", mapping.getLoginChannel() + "",
				mapping.getPosition() + "", mapping.getRemark() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public DBFriendGroupMapping findByAccount(String account) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.FRIEND_STATE_ACCOUNT + "=?",
				new String[] { account });
		DBFriendGroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			// 查找user
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.FRIEND_STATE_USER_ACCOUNT));
			DBUser user = userDao.findByAccount(userAccount);

			// 查找friendgroup
			String friendGroupAccount = cursor
					.getString(cursor
							.getColumnIndex(DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT));
			DBFriendGroup friendGroup = fgDao
					.findByAccount(friendGroupAccount);

			mapping = DBObjectHelper.getDB3FriendGroupMapping(user,
					friendGroup, cursor);
		}
		cursor.close();

		return mapping;
	}

	private boolean isExists(String userAccount, String friendGroupAccount) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.FRIEND_STATE_USER_ACCOUNT + "=? AND "
						+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { userAccount, friendGroupAccount });
		if (cursor.moveToFirst()) {
			cursor.close();

			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public int update(DBFriendGroupMapping mapping) {
		db = getTransactionDB();

		// 查找是否存在
		DBFriendGroupMapping mp = findByAccount(mapping.getAccount());
		if (mp == null) {
			return DBColumns.ERROR_FRIEND_STATE_NOT_FOUND;
		}

		// 更新
		db.execSQL(
				"UPDATE " + DBColumns.FRIEND_STATE_TABLE_NAME + " SET "
						+ DBColumns.FRIEND_STATE_USER_ACCOUNT + "=?,"
						+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?,"
						+ DBColumns.FRIEND_STATE_LOGIN_STATE + "=?,"
						+ DBColumns.FRIEND_STATE_LOGIN_CHANNEL + "=?,"
						+ DBColumns.FRIEND_STATE_POSITION + "=?,"
						+ DBColumns.FRIEND_STATE_REMARK + "=? WHERE "
						+ DBColumns.FRIEND_STATE_ACCOUNT + "=?",
				new Object[] { mapping.getUser().getAccount(),
						mapping.getFriendGroup().getAccount(),
						mapping.getLoginState(), mapping.getLoginChannel(),
						mapping.getPosition(), mapping.getRemark(),
						mapping.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBFriendGroupMapping mapping) {
		db = getTransactionDB();

		DBFriendGroupMapping mp = findByAccount(mapping.getAccount());
		if (mp == null) {
			return DBColumns.ERROR_FRIEND_STATE_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DBColumns.FRIEND_STATE_TABLE_NAME
						+ " WHERE " + DBColumns.FRIEND_STATE_ACCOUNT + "=?",
				new Object[] { mapping.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBUser user) {
		db = getTransactionDB();

		DBUser u = userDao.findByAccount(user.getAccount());
		if (u == null) {
			return DBColumns.ERROR_USER_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DBColumns.FRIEND_STATE_TABLE_NAME
						+ " WHERE " + DBColumns.FRIEND_STATE_USER_ACCOUNT + "=?",
				new Object[] { user.getAccount() });

		return DBColumns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DBColumns.FRIEND_STATE_TABLE_NAME
				+ " WHERE 1=1");

		return DBColumns.RESULT_OK;
	}

	@Override
	public List<DBUser> findByFriendGroup(DBFriendGroup friendGroup) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT "
						+ DBColumns.FRIEND_STATE_USER_ACCOUNT + " FROM "
						+ DBColumns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { friendGroup.getAccount() });
		int i = 0;
		String[] us = new String[cursor.getCount()];
		while (cursor.moveToNext()) {
			us[i++] = cursor.getString(0);
		}
		cursor.close();

		List<DBUser> users = new ArrayList<DBUser>();
		for (int j = 0; j < us.length; j++) {
			users.add(userDao.findByAccount(us[j]));
		}

		return users;
	}

	@Override
	public List<DBFriendGroupMapping> findMappingByFriendGroup(
			DBFriendGroup friendGroup) {
		// friendgroup中分组中包含的user,及user状态
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { friendGroup.getAccount() });
		List<DBFriendGroupMapping> mappings = new ArrayList<DBFriendGroupMapping>();
		while (cursor.moveToNext()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.FRIEND_STATE_USER_ACCOUNT));
			DBUser user = userDao.findByAccount(userAccount);
			mappings.add(DBObjectHelper.getDB3FriendGroupMapping(user,
					friendGroup, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public String getRemark(DBUser user, DBUser contact) {
		// db = getTransactionDB();
		//
		// // select fs.[remark] from friend_state fs,friend_group fg where
		// // fs.user_account='100009' and fg.[user_account]='100000' and
		// // fs.[friend_group_account] = fg.[account];
		// Cursor cursor = db.rawQuery("SELECT fs."
		// + DBColumns.FRIEND_STATE_REMARK + " FROM "
		// + DBColumns.FRIEND_STATE_TABLE_NAME + " fs,"
		// + DBColumns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
		// + DBColumns.FRIEND_STATE_USER_ACCOUNT + "=? AND fg."
		// + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=? AND fs."
		// + DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
		// + DBColumns.FRIEND_GROUP_ACCOUNT,
		// new String[] { contact.getAccount(), mUser.getAccount() });
		// String remark = null;
		// if (cursor.moveToFirst()) {
		// remark = cursor.getString(0);
		// }
		// cursor.close();
		//
		// return remark;
		return getRemark(user.getAccount(), contact.getAccount());
	}

	@Override
	public String getRemark(String userAccount, String contactAccount) {
		db = getTransactionDB();

		// select fs.[remark] from friend_state fs,friend_group fg where
		// fs.user_account='100009' and fg.[user_account]='100000' and
		// fs.[friend_group_account] = fg.[account];
		Cursor cursor = db.rawQuery("SELECT fs."
				+ DBColumns.FRIEND_STATE_REMARK + " FROM "
				+ DBColumns.FRIEND_STATE_TABLE_NAME + " fs,"
				+ DBColumns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
				+ DBColumns.FRIEND_STATE_USER_ACCOUNT + "=? AND fg."
				+ DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=? AND fs."
				+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
				+ DBColumns.FRIEND_GROUP_ACCOUNT, new String[] {
				contactAccount, userAccount });
		String remark = null;
		if (cursor.moveToFirst()) {
			remark = cursor.getString(0);
		}
		cursor.close();

		if (remark == null) {
			remark = userDao.findByAccount(contactAccount).getNickname();
		}

		return remark;
	}

	@Override
	public void close() {
		super.close();
		userDao.close();
		fgDao.close();
	}

	@Override
	public DBFriendGroupMapping find(String userAccount, String contactAccount) {
		db = getTransactionDB();

		// 要查询的用户
		DBUser user = userDao.findByAccount(contactAccount);

		// select fs.* from friend_state fs,friend_group fg where
		// fs.[user_account]='100002' and fs.[friend_group_account]=fg.[account]
		// and fg.[user_account]='100000'
		Cursor cursor = db.rawQuery("SELECT fs.* FROM "
				+ DBColumns.FRIEND_STATE_TABLE_NAME + " fs,"
				+ DBColumns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
				+ DBColumns.FRIEND_STATE_USER_ACCOUNT + "=? AND fs."
				+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
				+ DBColumns.FRIEND_GROUP_ACCOUNT + " AND fg."
				+ DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
				contactAccount, userAccount });

		DBFriendGroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			String friendGroupAccount = cursor
					.getString(cursor
							.getColumnIndex(DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT));
			DBFriendGroup friendGroup = fgDao
					.findByAccount(friendGroupAccount);
			mapping = DBObjectHelper.getDB3FriendGroupMapping(user,
					friendGroup, cursor);
		}
		cursor.close();

		return mapping;
	}

	@Override
	public boolean checkFriend(String userAccount, String contactAccount) {
		db = getTransactionDB();

		// select fs.[remark] from friend_state fs,friend_group fg where
		// fs.[user_account]='100009' and fs.[friend_group_account]=fg.[account]
		// and fg.[user_account]='100000'
		Cursor cursor = db.rawQuery("SELECT fs."
				+ DBColumns.FRIEND_STATE_REMARK + " FROM "
				+ DBColumns.FRIEND_STATE_TABLE_NAME + " fs,"
				+ DBColumns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
				+ DBColumns.FRIEND_STATE_USER_ACCOUNT + "=? AND fs."
				+ DBColumns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
				+ DBColumns.FRIEND_GROUP_ACCOUNT + " AND fg."
				+ DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
				contactAccount, userAccount });

		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public void delete(String userAccount, String jid) {
		db = getTransactionDB();

		DBUser contact = userDao.findByName(jid);
		if (contact == null) {
			return;
		}

		// 查找对应的account
		// select fs.[account] from friend_state fs,friend_group fg where
		// fs.[user_account]='100003' AND fs.[friend_group_account]=fg.[account]
		// and fg.[user_account]='100000'
		DBFriendGroupMapping mapping = find(userAccount, contact.getAccount());
		delete(mapping);
		// db.execSQL("DELETE * FROM " + DBColumns., new Object[]{});
	}

	@Override
	// public synchronized void add(String account, XMPPUser contact) {
	public void add(String account, XMPPUser contact) {
		db = getTransactionDB();

		synchronized (synchObj) {

			// 查找group
			String group = contact.getGroup();
			DBFriendGroup fg = fgDao.find(account, group);

			// 查找contact
			String jid = contact.getJid();
			DBUser ct = userDao.findByName(jid);
			if (ct == null) {
				// 添加contact
				// 查找account
				String ua = userDao.findMaxAccount();
				ua = Integer.parseInt(ua) + 1 + "";
				ct = new DBUser();
				ct.setAccount(ua);
				ct.setNickname(jid);
				// ct.setState(contact.getStatusMode());
				ct.setDesp(contact.getStatusMessage());
				ct.setRegisterTime(new Date().getTime());
				userDao.add(ct);
			}

			// 是否已经存在,add(mapping)中会检查

			// 添加mapping
			DBFriendGroupMapping mapping = new DBFriendGroupMapping();
			mapping.setFriendGroup(fg);
			mapping.setUser(ct);
			mapping.setRemark(contact.getNickname());
			mapping.setLoginState(contact.getStatusMode());
			String ac = Integer.parseInt(getMaxAccount()) + 1 + "";
			mapping.setAccount(ac);
			add(mapping);
		}
	}

	@Override
	public String getMaxAccount() {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT " + DBColumns.FRIEND_STATE_ACCOUNT
				+ " FROM " + DBColumns.FRIEND_STATE_TABLE_NAME + " WHERE "
				+ DBColumns.FRIEND_STATE_ID + "=(SELECT max("
				+ DBColumns.FRIEND_STATE_ID + ") FROM "
				+ DBColumns.FRIEND_STATE_TABLE_NAME + ")", null);
		String ac = "100";
		if (cursor.moveToFirst()) {
			ac = cursor.getString(0);
		}
		cursor.close();

		return ac;
	}

	@Override
	// public synchronized void update(String userAccount, XMPPUser contact) {
	public void update(String userAccount, XMPPUser contact) {
		db = getTransactionDB();

		synchronized (synchObj) {

			// 是否存在
			DBUser user = userDao.findByAccount(userAccount);
			if (user == null) {
				return;
			}

			DBUser ct = userDao.findByName(contact.getJid());
			if (ct == null) {
				add(userAccount, contact);

				return;
			}

			// 查找group,不存在会创建新的分组
			String group = contact.getGroup();
			DBFriendGroup fg = fgDao.find(userAccount, group);

			DBFriendGroupMapping mp = new DBFriendGroupMapping();
			mp.setUser(ct);
			mp.setFriendGroup(fg);
			mp.setLoginState(contact.getStatusMode());
			mp.setRemark(contact.getNickname());

			// 查找mapping中是否存在
			DBFriendGroupMapping mapping = find(userAccount, ct.getAccount());
			if (mapping != null) {
				// 更新
				mp.setAccount(mapping.getAccount());
				update(mp);
			} else {
				// 不存在,添加
				String acc = Integer.parseInt(getMaxAccount()) + 1 + "";
				mp.setAccount(acc);
				add(mp);
			}
		}
	}

	@Override
	public DBUser find(XMPPUser ur) {
		return null;
	}
}
