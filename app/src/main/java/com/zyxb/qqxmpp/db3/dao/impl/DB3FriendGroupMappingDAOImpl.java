package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroup;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DB3FriendGroupMappingDAOImpl extends DB3FriendGroupMappingDAO {
	private DB3UserDAO userDao;
	private DB3FriendGroupDAO fgDao;
	private static final Object synchObj = new Object();

	public DB3FriendGroupMappingDAOImpl(Context context) {
		super(context);

		userDao = new DB3UserDAOImpl(context);
		fgDao = new DB3FriendGroupDAOImpl(context);
	}

	@Override
	public int add(DB3FriendGroupMapping mapping) {
		db = getTransactionDB();

		// 查找user
		DB3User user = userDao.findByAccount(mapping.getUser().getAccount());
		if (user == null) {
			return DB3Columns.ERROR_USER_NOT_FOUND;
		}

		// 查找group
		DB3FriendGroup fg = fgDao.findByAccount(mapping.getFriendGroup()
				.getAccount());
		if (fg == null) {
			fgDao.add(mapping.getFriendGroup());
		}

		// 是否已经存在
		if (isExists(mapping.getUser().getAccount(), mapping.getFriendGroup()
				.getAccount())) {
			return DB3Columns.ERROR_FRIEND_STATE_EXISTS;
		}

		// 添加
		db.execSQL("INSERT INTO " + DB3Columns.FRIEND_STATE_TABLE_NAME + "("
				+ DB3Columns.FRIEND_STATE_ACCOUNT + ","
				+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + ","
				+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + ","
				+ DB3Columns.FRIEND_STATE_LOGIN_STATE + ","
				+ DB3Columns.FRIEND_STATE_LOGIN_CHANNEL + ","
				+ DB3Columns.FRIEND_STATE_POSITION + ","
				+ DB3Columns.FRIEND_STATE_REMARK + ") VALUES(?,?,?,?,?,?,?)"

				, new String[] { mapping.getAccount(), mapping.getUser().getAccount(),
				mapping.getFriendGroup().getAccount(),
				mapping.getLoginState() + "", mapping.getLoginChannel() + "",
				mapping.getPosition() + "", mapping.getRemark() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3FriendGroupMapping findByAccount(String account) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.FRIEND_STATE_ACCOUNT + "=?",
				new String[] { account });
		DB3FriendGroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			// 查找user
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.FRIEND_STATE_USER_ACCOUNT));
			DB3User user = userDao.findByAccount(userAccount);

			// 查找friendgroup
			String friendGroupAccount = cursor
					.getString(cursor
							.getColumnIndex(DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT));
			DB3FriendGroup friendGroup = fgDao
					.findByAccount(friendGroupAccount);

			mapping = DB3ObjectHelper.getDB3FriendGroupMapping(user,
					friendGroup, cursor);
		}
		cursor.close();

		return mapping;
	}

	private boolean isExists(String userAccount, String friendGroupAccount) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=? AND "
						+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { userAccount, friendGroupAccount });
		if (cursor.moveToFirst()) {
			cursor.close();

			return true;
		}
		cursor.close();

		return false;
	}

	@Override
	public int update(DB3FriendGroupMapping mapping) {
		db = getTransactionDB();

		// 查找是否存在
		DB3FriendGroupMapping mp = findByAccount(mapping.getAccount());
		if (mp == null) {
			return DB3Columns.ERROR_FRIEND_STATE_NOT_FOUND;
		}

		// 更新
		db.execSQL(
				"UPDATE " + DB3Columns.FRIEND_STATE_TABLE_NAME + " SET "
						+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=?,"
						+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?,"
						+ DB3Columns.FRIEND_STATE_LOGIN_STATE + "=?,"
						+ DB3Columns.FRIEND_STATE_LOGIN_CHANNEL + "=?,"
						+ DB3Columns.FRIEND_STATE_POSITION + "=?,"
						+ DB3Columns.FRIEND_STATE_REMARK + "=? WHERE "
						+ DB3Columns.FRIEND_STATE_ACCOUNT + "=?",
				new Object[] { mapping.getUser().getAccount(),
						mapping.getFriendGroup().getAccount(),
						mapping.getLoginState(), mapping.getLoginChannel(),
						mapping.getPosition(), mapping.getRemark(),
						mapping.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3FriendGroupMapping mapping) {
		db = getTransactionDB();

		DB3FriendGroupMapping mp = findByAccount(mapping.getAccount());
		if (mp == null) {
			return DB3Columns.ERROR_FRIEND_STATE_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DB3Columns.FRIEND_STATE_TABLE_NAME
						+ " WHERE " + DB3Columns.FRIEND_STATE_ACCOUNT + "=?",
				new Object[] { mapping.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3User user) {
		db = getTransactionDB();

		DB3User u = userDao.findByAccount(user.getAccount());
		if (u == null) {
			return DB3Columns.ERROR_USER_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DB3Columns.FRIEND_STATE_TABLE_NAME
						+ " WHERE " + DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=?",
				new Object[] { user.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DB3Columns.FRIEND_STATE_TABLE_NAME
				+ " WHERE 1=1");

		return DB3Columns.RESULT_OK;
	}

	@Override
	public List<DB3User> findByFriendGroup(DB3FriendGroup friendGroup) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT "
						+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + " FROM "
						+ DB3Columns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { friendGroup.getAccount() });
		int i = 0;
		String[] us = new String[cursor.getCount()];
		while (cursor.moveToNext()) {
			us[i++] = cursor.getString(0);
		}
		cursor.close();

		List<DB3User> users = new ArrayList<DB3User>();
		for (int j = 0; j < us.length; j++) {
			users.add(userDao.findByAccount(us[j]));
		}

		return users;
	}

	@Override
	public List<DB3FriendGroupMapping> findMappingByFriendGroup(
			DB3FriendGroup friendGroup) {
		// friendgroup中分组中包含的user,及user状态
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.FRIEND_STATE_TABLE_NAME + " WHERE "
						+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { friendGroup.getAccount() });
		List<DB3FriendGroupMapping> mappings = new ArrayList<DB3FriendGroupMapping>();
		while (cursor.moveToNext()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.FRIEND_STATE_USER_ACCOUNT));
			DB3User user = userDao.findByAccount(userAccount);
			mappings.add(DB3ObjectHelper.getDB3FriendGroupMapping(user,
					friendGroup, cursor));
		}
		cursor.close();

		return mappings;
	}

	@Override
	public String getRemark(DB3User user, DB3User contact) {
		// db = getTransactionDB();
		//
		// // select fs.[remark] from friend_state fs,friend_group fg where
		// // fs.user_account='100009' and fg.[user_account]='100000' and
		// // fs.[friend_group_account] = fg.[account];
		// Cursor cursor = db.rawQuery("SELECT fs."
		// + DB3Columns.FRIEND_STATE_REMARK + " FROM "
		// + DB3Columns.FRIEND_STATE_TABLE_NAME + " fs,"
		// + DB3Columns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
		// + DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=? AND fg."
		// + DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=? AND fs."
		// + DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
		// + DB3Columns.FRIEND_GROUP_ACCOUNT,
		// new String[] { contact.getAccount(), user.getAccount() });
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
				+ DB3Columns.FRIEND_STATE_REMARK + " FROM "
				+ DB3Columns.FRIEND_STATE_TABLE_NAME + " fs,"
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
				+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=? AND fg."
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=? AND fs."
				+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
				+ DB3Columns.FRIEND_GROUP_ACCOUNT, new String[] {
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
	public DB3FriendGroupMapping find(String userAccount, String contactAccount) {
		db = getTransactionDB();

		// 要查询的用户
		DB3User user = userDao.findByAccount(contactAccount);

		// select fs.* from friend_state fs,friend_group fg where
		// fs.[user_account]='100002' and fs.[friend_group_account]=fg.[account]
		// and fg.[user_account]='100000'
		Cursor cursor = db.rawQuery("SELECT fs.* FROM "
				+ DB3Columns.FRIEND_STATE_TABLE_NAME + " fs,"
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
				+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=? AND fs."
				+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
				+ DB3Columns.FRIEND_GROUP_ACCOUNT + " AND fg."
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
				contactAccount, userAccount });

		DB3FriendGroupMapping mapping = null;
		if (cursor.moveToFirst()) {
			String friendGroupAccount = cursor
					.getString(cursor
							.getColumnIndex(DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT));
			DB3FriendGroup friendGroup = fgDao
					.findByAccount(friendGroupAccount);
			mapping = DB3ObjectHelper.getDB3FriendGroupMapping(user,
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
				+ DB3Columns.FRIEND_STATE_REMARK + " FROM "
				+ DB3Columns.FRIEND_STATE_TABLE_NAME + " fs,"
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " fg WHERE fs."
				+ DB3Columns.FRIEND_STATE_USER_ACCOUNT + "=? AND fs."
				+ DB3Columns.FRIEND_STATE_FRIEND_GROUP_ACCOUNT + "=fg."
				+ DB3Columns.FRIEND_GROUP_ACCOUNT + " AND fg."
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
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

		DB3User contact = userDao.findByName(jid);
		if (contact == null) {
			return;
		}

		// 查找对应的account
		// select fs.[account] from friend_state fs,friend_group fg where
		// fs.[user_account]='100003' AND fs.[friend_group_account]=fg.[account]
		// and fg.[user_account]='100000'
		DB3FriendGroupMapping mapping = find(userAccount, contact.getAccount());
		delete(mapping);
		// db.execSQL("DELETE * FROM " + DB3Columns., new Object[]{});
	}

	@Override
	// public synchronized void add(String account, XMPPUser contact) {
	public void add(String account, XMPPUser contact) {
		db = getTransactionDB();

		synchronized (synchObj) {

			// 查找group
			String group = contact.getGroup();
			DB3FriendGroup fg = fgDao.find(account, group);

			// 查找contact
			String jid = contact.getJid();
			DB3User ct = userDao.findByName(jid);
			if (ct == null) {
				// 添加contact
				// 查找account
				String ua = userDao.findMaxAccount();
				ua = Integer.parseInt(ua) + 1 + "";
				ct = new DB3User();
				ct.setAccount(ua);
				ct.setNickname(jid);
				// ct.setState(contact.getStatusMode());
				ct.setDesp(contact.getStatusMessage());
				ct.setRegisterTime(new Date().getTime());
				userDao.add(ct);
			}

			// 是否已经存在,add(mapping)中会检查

			// 添加mapping
			DB3FriendGroupMapping mapping = new DB3FriendGroupMapping();
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

		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.FRIEND_STATE_ACCOUNT
				+ " FROM " + DB3Columns.FRIEND_STATE_TABLE_NAME + " WHERE "
				+ DB3Columns.FRIEND_STATE_ID + "=(SELECT max("
				+ DB3Columns.FRIEND_STATE_ID + ") FROM "
				+ DB3Columns.FRIEND_STATE_TABLE_NAME + ")", null);
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
			DB3User user = userDao.findByAccount(userAccount);
			if (user == null) {
				return;
			}

			DB3User ct = userDao.findByName(contact.getJid());
			if (ct == null) {
				add(userAccount, contact);

				return;
			}

			// 查找group,不存在会创建新的分组
			String group = contact.getGroup();
			DB3FriendGroup fg = fgDao.find(userAccount, group);

			DB3FriendGroupMapping mp = new DB3FriendGroupMapping();
			mp.setUser(ct);
			mp.setFriendGroup(fg);
			mp.setLoginState(contact.getStatusMode());
			mp.setRemark(contact.getNickname());

			// 查找mapping中是否存在
			DB3FriendGroupMapping mapping = find(userAccount, ct.getAccount());
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
	public DB3User find(XMPPUser ur) {
		return null;
	}
}
