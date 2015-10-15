package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroup;
import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;
import com.zyxb.qqxmpp.util.MD5Encoder;

import java.util.List;

public class DB3FriendGroupDAOImpl extends DB3FriendGroupDAO {
	private DB3UserDAO dao;

	public DB3FriendGroupDAOImpl(Context context) {
		super(context);

		dao = DAOFactory.getDB3UserDAO(context);
	}

	@Override
	public int add(DB3FriendGroup friendGroup) {
		db = getTransactionDB();

		// 查找user
		DB3User user = dao.findByAccount(friendGroup.getUser().getAccount());
		if (user == null) {
			dao.add(friendGroup.getUser());
		}

		// 查找是否有相同名称的分组
		// DB3FriendGroup fg = findByName(friendGroup.getUser().getAccount(),
		// friendGroup.getAccount());
		// if (fg != null) {
		// return DB3Columns.ERROR_FRIEND_GROUP_EXISTS;
		// }
		if (isExists(friendGroup.getUser().getAccount(), friendGroup.getName())) {
			return DB3Columns.ERROR_FRIEND_GROUP_EXISTS;
		}

		// 添加friendgroup
		db.execSQL(
				"INSERT INTO " + DB3Columns.FRIEND_GROUP_TABLE_NAME + "("
						+ DB3Columns.FRIEND_GROUP_ACCOUNT + ","
						+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + ","
						+ DB3Columns.FRIEND_GROUP_NAME + ","
						+ DB3Columns.FRIEND_GROUP_POSITION
						+ ") VALUES(?,?,?,?)",
				new String[] { friendGroup.getAccount(),
						friendGroup.getUser().getAccount(),
						friendGroup.getName(), friendGroup.getPosition() + "" });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public DB3FriendGroup findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
						+ DB3Columns.FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { account });
		DB3FriendGroup fg = null;
		if (cursor.moveToFirst()) {
			String userAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.FRIEND_GROUP_USER_ACCOUNT));
			fg = DB3ObjectHelper.getDB3FriendGroup(
					dao.findByAccount(userAccount), cursor);
		}
		cursor.close();

		return fg;
	}

	private boolean isExists(String userAccount, String groupName) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
				+ DB3Columns.FRIEND_GROUP_NAME + "=? AND "
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
				groupName, userAccount });
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		}
		cursor.close();

		return false;
	}

	protected DB3FriendGroup findByName(String userAccount, String name) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
				+ DB3Columns.FRIEND_GROUP_NAME + "=? AND "
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
				name, userAccount });
		DB3FriendGroup fg = null;
		if (cursor.moveToFirst()) {
			fg = DB3ObjectHelper.getDB3FriendGroup(
					dao.findByAccount(userAccount), cursor);
		}
		cursor.close();

		return fg;
	}

	@Override
	public int update(DB3FriendGroup friendGroup) {
		db = getTransactionDB();

		// 查找
		DB3FriendGroup fg = findByAccount(friendGroup.getAccount());
		if (fg == null) {
			return DB3Columns.ERROR_FRIEND_GROUP_NOT_FOUND;
		}

		// 查找user
		DB3User user = dao.findByAccount(friendGroup.getUser().getAccount());
		if (user == null) {
			dao.add(friendGroup.getUser());
		}

		// 更新
		db.execSQL("UPDATE " + DB3Columns.FRIEND_GROUP_TABLE_NAME + " SET "
				+ DB3Columns.FRIEND_GROUP_NAME + "=? "
				+ DB3Columns.FRIEND_GROUP_POSITION + "=? "
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=? ", new String[] {
				friendGroup.getName(), friendGroup.getPosition() + "",
				friendGroup.getUser().getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3FriendGroup friendGroup) {
		db = getTransactionDB();

		// 查找
		DB3FriendGroup fg = findByAccount(friendGroup.getAccount());
		if (fg == null) {
			return DB3Columns.ERROR_FRIEND_GROUP_NOT_FOUND;
		}

		db.execSQL("DELETE FROM " + DB3Columns.FRIEND_GROUP_TABLE_NAME
						+ " WHERE " + DB3Columns.FRIEND_GROUP_ACCOUNT + "=?",
				new String[] { friendGroup.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3User user) {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DB3Columns.FRIEND_GROUP_TABLE_NAME
						+ " WHERE " + DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?",
				new String[] { user.getAccount() });

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DB3Columns.FRIEND_GROUP_TABLE_NAME
				+ " WHERE 1=1");

		return DB3Columns.RESULT_OK;
	}

	@Override
	public List<DB3FriendGroup> findByUser(DB3User user) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
						+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=? ORDER BY "
						+ DB3Columns.FRIEND_GROUP_POSITION,
				new String[] { user.getAccount() });

		return DB3ObjectHelper.getDB3FriendGroups(user, cursor);
	}

	@Override
	public void close() {
		super.close();
		dao.close();
	}

	@Override
	public DB3FriendGroup find(String account, String group) {
		db = getTransactionDB();

		DB3User user = dao.findByAccount(account);
		if (user == null) {
			return null;
		}

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
				+ DB3Columns.FRIEND_GROUP_NAME + "=? AND "
				+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[] {
				group, account });
		DB3FriendGroup fg = null;
		if (cursor.moveToFirst()) {
			fg = DB3ObjectHelper.getDB3FriendGroup(user, cursor);
		}
		cursor.close();

		if (fg == null) {
			// 添加
			// 查找account
			// select account from friend_group where _id = (select max(_id)
			// from friend_group);
			cursor = db.rawQuery("SELECT " + DB3Columns.FRIEND_GROUP_ACCOUNT
					+ " FROM " + DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
					+ DB3Columns.FRIEND_GROUP_ID + "=(SELECT max("
					+ DB3Columns.FRIEND_GROUP_ID + ") FROM "
					+ DB3Columns.FRIEND_GROUP_TABLE_NAME + ")", null);
			int acc = 1;
			if (cursor.moveToFirst()) {
				acc = Integer.parseInt(cursor.getString(0)) + 1;
			}
			cursor.close();
			fg = new DB3FriendGroup();
			fg.setAccount(acc + "");
			fg.setName(group);
			fg.setUser(user);

			// 查找position
			// select max(position) from friend_group where
			// user_account='100000'
			cursor = db.rawQuery("SELECT max("
							+ DB3Columns.FRIEND_GROUP_POSITION + ") FROM "
							+ DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
							+ DB3Columns.FRIEND_GROUP_USER_ACCOUNT + "=?",
					new String[] { account });
			int position = 0;
			if (cursor.moveToFirst()) {
				position = cursor.getInt(0) + 1;
			}
			cursor.close();
			fg.setPosition(position);

			add(fg);
		}

		return fg;
	}

	@Override
	public DB3User find(XMPPUser ur) {
		db = getTransactionDB();

		// 查找用户是否存在
		DB3User user = dao.findByName(ur.getJid());
		if (user == null) {
			// 添加user
			user = dao.find(ur);

			// 添加[我的设备,我的好友]
			DB3FriendGroup fg = new DB3FriendGroup();
			fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
			fg.setPosition(0);
			fg.setName("我的设备");
			fg.setUser(user);
			add(fg);

			fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
			fg.setPosition(1);
			fg.setName("我的好友");
			add(fg);
			fg = null;
		}else{
			//如果已经存在,看看pwd是否为空,为空则是其他用户的添加好友添加,需添加[我的设备,我的好友]
			if(user.getPwd() == null){
				user.setPwd(MD5Encoder.encode(ur.getStatusMessage()));
				dao.update(user);

				// 添加[我的设备,我的好友]
				DB3FriendGroup fg = new DB3FriendGroup();
				fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
				fg.setPosition(0);
				fg.setName("我的设备");
				fg.setUser(user);
				add(fg);

				fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
				fg.setPosition(1);
				fg.setName("我的好友");
				add(fg);
				fg = null;
			}
		}

		return user;
	}

	@Override
	public String getMaxAccount() {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.FRIEND_GROUP_ACCOUNT
				+ " FROM " + DB3Columns.FRIEND_GROUP_TABLE_NAME + " WHERE "
				+ DB3Columns.FRIEND_GROUP_ID + "=(SELECT max("
				+ DB3Columns.FRIEND_GROUP_ID + ") FROM "
				+ DB3Columns.FRIEND_GROUP_TABLE_NAME + ")", null);
		String acc = "1";
		if (cursor.moveToFirst()) {
			acc = cursor.getString(0);
		}
		cursor.close();

		return acc;
	}
}
