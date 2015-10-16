package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBMessage;
import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBSystemGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DAOFactory;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBGroupMappingDAO;
import com.zyxb.qqxmpp.db.dao.DBMessageDAO;
import com.zyxb.qqxmpp.db.dao.DBSystemGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBMessageDAOImpl extends DBMessageDAO {
	private DBUserDAO userDao;
	private DBGroupDAO groupDao;
	private DBSystemGroupDAO sysDao;
	private DBGroupMappingDAO gmDao;

	public DBMessageDAOImpl(Context context) {
		super(context);

		userDao = DAOFactory.getDB3UserDAO(context);
		groupDao = DAOFactory.getDB3GroupDAO(context);
		sysDao = DAOFactory.getDB3SystemGroupDAO(context);
		gmDao = DAOFactory.getDB3GroupMappingDAO(context);
	}

	@Override
	public int add(DBMessage message) {
		db = getTransactionDB();

		// 需要根据不同类型,判断from,to
		// String fromAccount = null;
		// String toAccount = null;
		// switch (message.getType()) {
		// case Const.MESSAGE_TYPE_GROUP:
		// fromAccount = message.getFrom().getAccount();
		// toAccount = message.getToGroup().getAccount();
		// break;
		// case Const.MESSAGE_TYPE_CONTACT:
		// fromAccount = message.getFrom().getAccount();
		// toAccount = message.getTo().getAccount();
		// break;
		// case Const.MESSAGE_TYPE_SYS:
		// fromAccount = message.getFromGroup().getAccount();
		// toAccount = message.getTo().getAccount();
		// break;
		// }
		String fromAccount = getAccount(message)[0];
		String toAccount = getAccount(message)[1];

		db.execSQL("INSERT INTO " + DBColumns.MESSAGE_TABLE_NAME + "("
				+ DBColumns.MESSAGE_ACCOUNT + "," + DBColumns.MESSAGE_FROM
				+ "," + DBColumns.MESSAGE_TO + "," + DBColumns.MESSAGE_MSG
				+ "," + DBColumns.MESSAGE_CREATE_TIME + ","
				+ DBColumns.MESSAGE_TYPE + "," + DBColumns.MESSAGE_STATE
				+ "," + DBColumns.MESSAGE_DATA1 + ","
				+ DBColumns.MESSAGE_DATA2 + "," + DBColumns.MESSAGE_DATA3
				+ "," + DBColumns.MESSAGE_DATA4 + ","
				+ DBColumns.MESSAGE_DATA5 + "," + DBColumns.MESSAGE_DATA6
				+ "," + DBColumns.MESSAGE_DATA7
				+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[] {
				message.getAccount(), fromAccount, toAccount, message.getMsg(),
				message.getCreateTime(), message.getType(), message.getState(),
				message.getData1(), message.getData2(), message.getData3(),
				message.getData4(), message.getData5(), message.getData6(),
				message.getData7() });

		// dataChanged(message.getType());
		notifyDataChanged(message.getType());

		return DBColumns.RESULT_OK;
	}

	private String[] getAccount(DBMessage message) {
		String[] temp = new String[2];

		// 需要根据不同类型,判断from,to
		String fromAccount = null;
		String toAccount = null;
		switch (message.getType()) {
			case DBColumns.MESSAGE_TYPE_GROUP:
				fromAccount = message.getFrom().getAccount();
				toAccount = message.getToGroup().getAccount();
				break;
			case DBColumns.MESSAGE_TYPE_CONTACT:
				fromAccount = message.getFrom().getAccount();
				toAccount = message.getTo().getAccount();
				break;
			case DBColumns.MESSAGE_TYPE_SYS:
				fromAccount = message.getFromGroup().getAccount();
				toAccount = message.getTo().getAccount();
				break;
		}
		temp[0] = fromAccount;
		temp[1] = toAccount;

		return temp;
	}

	@Override
	public DBMessage findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_ACCOUNT + "=?", new String[] { account });
		DBMessage message = null;
		if (cursor.moveToFirst()) {
			int type = cursor.getInt(cursor
					.getColumnIndex(DBColumns.MESSAGE_TYPE));
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			String toAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_TO));

			DBUser from, to;
			DBGroup toGroup;
			DBSystemGroup fromGroup;
			switch (type) {
				case DBColumns.MESSAGE_TYPE_GROUP:
					// 群消息 from=消息发送方 to=群
					// 查询from
					from = userDao.findByAccount(fromAccount);
					toGroup = groupDao.findByAccount(toAccount);
					message = DBObjectHelper.getDB3GroupMessage(from, toGroup,
							cursor);

					break;
				case DBColumns.MESSAGE_TYPE_CONTACT:
					// 联系人聊天 from=消息发送方 to=消息接收方
					from = userDao.findByAccount(fromAccount);
					to = userDao.findByAccount(toAccount);
					message = DBObjectHelper
							.getDB3ContactMessage(from, to, cursor);

					break;
				case DBColumns.MESSAGE_TYPE_SYS:
					// 系统消息 from=系统 to=消息接收方
					fromGroup = sysDao.findByAccount(fromAccount);
					to = userDao.findByAccount(toAccount);
					message = DBObjectHelper.getDB3SystemMessage(to, fromGroup,
							cursor);

					break;
			}
		}
		cursor.close();

		return message;
	}

	@Override
	public int update(DBMessage message) {
		db = getTransactionDB();

		// 是否存在
		DBMessage m = findByAccount(message.getAccount());
		if (m == null) {
			return DBColumns.ERROR_MESSAGE_NOT_FOUND;
		}

		// 获取from/to account
		String[] accounts = getAccount(message);
		String fromAccount = accounts[0];
		String toAccount = accounts[1];

		// 更新
		db.execSQL(
				"UPDATE " + DBColumns.MESSAGE_TABLE_NAME + " SET "
						+ DBColumns.MESSAGE_FROM + "=?,"
						+ DBColumns.MESSAGE_MSG + "=?,"
						+ DBColumns.MESSAGE_TO + "=?,"
						+ DBColumns.MESSAGE_CREATE_TIME + "=?,"
						+ DBColumns.MESSAGE_TYPE + "=?,"
						+ DBColumns.MESSAGE_STATE + "=?,"
						+ DBColumns.MESSAGE_DATA1 + "=?,"
						+ DBColumns.MESSAGE_DATA2 + "=?,"
						+ DBColumns.MESSAGE_DATA3 + "=?,"
						+ DBColumns.MESSAGE_DATA4 + "=?,"
						+ DBColumns.MESSAGE_DATA5 + "=?,"
						+ DBColumns.MESSAGE_DATA6 + "=?,"
						+ DBColumns.MESSAGE_DATA7 + "=? WHERE "
						+ DBColumns.MESSAGE_ACCOUNT + "=?",
				new Object[] { fromAccount, message.getMsg(), toAccount,
						message.getCreateTime(), message.getType(),
						message.getState(), message.getData1(),
						message.getData2(), message.getData3(),
						message.getData4(), message.getData5(),
						message.getData6(), message.getData7(),
						message.getAccount() });

		// dataChanged(message.getType());
		notifyDataChanged(message.getType());

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBMessage message) {
		db = getTransactionDB();

		DBMessage m = findByAccount(message.getAccount());
		if (m == null) {
			return DBColumns.ERROR_MESSAGE_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
						+ DBColumns.MESSAGE_ACCOUNT + "=?",
				new Object[] { message.getAccount() });

		// dataChanged(message.getType());
		notifyDataChanged(message.getType());

		return DBColumns.RESULT_OK;
	}

	@Override
	public int delete(DBUser user) {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_FROM + "=? OR " + DBColumns.MESSAGE_TO
				+ "=?", new Object[] { user.getAccount(), user.getAccount() });

		// dataChanged(DBColumns.MESSAGE_TYPE_ALL);
		notifyDataChanged(DBColumns.MESSAGE_TYPE_ALL);

		return DBColumns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DBColumns.MESSAGE_TABLE_NAME
				+ " WHERE 1=1");

		// dataChanged(DBColumns.MESSAGE_TYPE_ALL);
		notifyDataChanged(DBColumns.MESSAGE_TYPE_ALL);

		return DBColumns.RESULT_OK;
	}

	@Override
	public List<DBMessage> findNewest(DBUser user) {
		// 可以查询出来
		// select *,max(create_time) from message where (msg_type=0 and
		// msg_to='100000') group by msg_from union select m.*,max(create_time)
		// from message m,group_state g where msg_type=1 and
		// m.msg_to=g.group_account and g.user_account='100000' union select
		// *,max(create_time) from message where msg_type='2' and
		// (msg_from='100000' or msg_to='100000') group by msg_from+msg_to order
		// by create_time desc;

		db = getTransactionDB();
		// 查询system message (TYPE,TO)
		Cursor cursor = db.rawQuery("SELECT *,max("
				+ DBColumns.MESSAGE_CREATE_TIME + ") t FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_TYPE + "=? AND " + DBColumns.MESSAGE_TO
				+ "=? GROUP BY " + DBColumns.MESSAGE_FROM + " HAVING "
				+ DBColumns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DBColumns.MESSAGE_TYPE_SYS + "", user.getAccount() });
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			DBSystemGroup group = sysDao.findByAccount(fromAccount);
			messages.add(DBObjectHelper.getDB3SystemMessage(user, group,
					cursor));
		}
		cursor.close();

		// 添加group message (需要连接group_state表查看user是否在group中)
		// select m.*,max(create_time) from message m,group_state g where
		// m.msg_type=1 and m.msg_to=g.group_account and g.user_account='100000'
		// group by m.msg_to

		// 添加having
		// select m.*,max(create_time) t from message m,group_state g where
		// m.msg_type=1 and m.msg_to=g.group_account and g.user_account='100000'
		// group by m.msg_to having m.create_time=t
		cursor = db.rawQuery("SELECT m.*,max(" + DBColumns.MESSAGE_CREATE_TIME
				+ ") t FROM " + DBColumns.MESSAGE_TABLE_NAME + " m,"
				+ DBColumns.GROUP_STATE_TABLE_NAME + " g WHERE m."
				+ DBColumns.MESSAGE_TYPE + "=? AND m." + DBColumns.MESSAGE_TO
				+ "=g." + DBColumns.GROUP_STATE_GROUP_ACCOUNT + " AND g."
				+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=? GROUP BY m."
				+ DBColumns.MESSAGE_TO + " HAVING m."
				+ DBColumns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DBColumns.MESSAGE_TYPE_GROUP + "", user.getAccount() });
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			String groupAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_TO));
			DBGroup group = groupDao.findByAccount(groupAccount);
			DBUser u = userDao.findByAccount(fromAccount);
			messages.add(DBObjectHelper.getDB3GroupMessage(u, group, cursor));
		}
		cursor.close();

		// 添加contact message
		// select *,max(create_time) from message where msg_type='2' and
		// (msg_from='100000' or msg_to='100000') group by msg_from+msg_to
		// 不行没选中max(create_time)
		// cursor = db.rawQuery("SELECT *,max(" + DBColumns.MESSAGE_CREATE_TIME
		// + ") FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
		// + DBColumns.MESSAGE_TYPE + "=? AND ("
		// + DBColumns.MESSAGE_FROM + "=? OR " + DBColumns.MESSAGE_TO
		// + "=?) GROUP BY msg_from,msg_to",

		// SQLite OK,运行不行
		// select *,max(create_time) time from message where msg_from='100000'
		// or msg_to='100000' group by msg_from,msg_to having create_time = time
		// order by create_time desc;

		// cursor = db.rawQuery(
		// "SELECT *,max(" + DBColumns.MESSAGE_CREATE_TIME + ") t FROM "
		// + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
		// + DBColumns.MESSAGE_TYPE + "=? AND ("
		// + DBColumns.MESSAGE_FROM + "=? OR "
		// + DBColumns.MESSAGE_TO + "=?) GROUP BY "
		// + DBColumns.MESSAGE_FROM + "," + DBColumns.MESSAGE_TO
		// + " HAVING " + DBColumns.MESSAGE_CREATE_TIME + "=t",
		// new String[] { DBColumns.MESSAGE_TYPE_CONTACT + "",
		// mUser.getAccount(), mUser.getAccount() });

		// select *,max(create_time) t from message where msg_type=2 and
		// msg_from='100000' group by msg_to having create_time=t
		// union
		// select *,max(create_time) t from message where msg_type=2 and
		// msg_to='100000' group by msg_from having create_time=t

		// cursor = db.rawQuery("SELECT *,max(" + DBColumns.MESSAGE_CREATE_TIME
		// + ") t FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
		// + DBColumns.MESSAGE_TYPE + "=? AND " + DBColumns.MESSAGE_FROM
		// + "=? " + "GROUP BY " + DBColumns.MESSAGE_TO + " HAVING "
		// + DBColumns.MESSAGE_CREATE_TIME + "=t UNION ALL SELECT *,max("
		// + DBColumns.MESSAGE_CREATE_TIME + ") t FROM "
		// + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
		// + DBColumns.MESSAGE_TYPE + "=? AND " + DBColumns.MESSAGE_TO
		// + "=? " + "GROUP BY " + DBColumns.MESSAGE_FROM + " HAVING "
		// + DBColumns.MESSAGE_CREATE_TIME + "=t", new String[] {
		// DBColumns.MESSAGE_TYPE_CONTACT + "", mUser.getAccount(),
		// DBColumns.MESSAGE_TYPE_CONTACT + "", mUser.getAccount() });

		// 发送的消息
		cursor = db.rawQuery("SELECT *,max(" + DBColumns.MESSAGE_CREATE_TIME
				+ ") t FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_TYPE + "=? AND " + DBColumns.MESSAGE_FROM
				+ "=? " + "GROUP BY " + DBColumns.MESSAGE_TO + " HAVING "
				+ DBColumns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DBColumns.MESSAGE_TYPE_CONTACT + "", user.getAccount() });

		// cursor = db
		// .rawQuery(
		// "select *,max(create_time) time from message where msg_type=? and (msg_from=?	or msg_to=?) group by msg_from,msg_to having create_time = time order by create_time desc",
		// new String[] { DBColumns.MESSAGE_TYPE_CONTACT + "",
		// mUser.getAccount(), mUser.getAccount() });

		// System.out.println("from count:" + cursor.getCount());// 2??
		// System.out.println("mUser:" + mUser.getAccount());

		// cursor.moveToPrevious();
		List<DBMessage> ms = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			String toAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_TO));
			DBUser fu = userDao.findByAccount(fromAccount);
			DBUser tu = userDao.findByAccount(toAccount);
			ms.add(DBObjectHelper.getDB3ContactMessage(fu, tu, cursor));
		}
		cursor.close();

		// 接收的消息
		cursor = db.rawQuery("SELECT *,max(" + DBColumns.MESSAGE_CREATE_TIME
				+ ") t FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_TYPE + "=? AND " + DBColumns.MESSAGE_TO
				+ "=? " + "GROUP BY " + DBColumns.MESSAGE_FROM + " HAVING "
				+ DBColumns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DBColumns.MESSAGE_TYPE_CONTACT + "", user.getAccount() });
		// System.out.println("to count:" + cursor.getCount());
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			String toAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_TO));
			DBUser fu = userDao.findByAccount(fromAccount);
			DBUser tu = userDao.findByAccount(toAccount);
			ms.add(DBObjectHelper.getDB3ContactMessage(fu, tu, cursor));
		}
		cursor.close();

		// 去掉重复
		if (ms.size() > 0) {
			Map<String, DBMessage> mzs = new HashMap<String, DBMessage>();
			for (int i = ms.size() - 1; i >= 0; i--) {
				DBMessage mm = ms.get(i);
				String tmp1 = mm.getFrom().getAccount() + ""
						+ mm.getTo().getAccount();
				if (mzs.containsKey(tmp1)) {
					// 比较时间
					DBMessage tmp = mzs.get(tmp1);
					if (tmp.getCreateTime() > mm.getCreateTime()) {
						ms.remove(mm);
					} else {
						ms.remove(tmp);
						mzs.put(tmp1, mm);
						mzs.put(mm.getTo().getAccount() + ""
								+ mm.getFrom().getAccount(), mm);
					}
				} else {

					String tmp2 = mm.getTo().getAccount() + ""
							+ mm.getFrom().getAccount();
					mzs.put(tmp1, mm);
					mzs.put(tmp2, mm);
				}
			}

			messages.addAll(ms);
		}

		// 排序
		Collections.sort(messages);
		Collections.reverse(messages);

		return messages;
	}

	@Override
	public List<DBMessage> findContact(DBUser current, DBUser contact) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DBColumns.MESSAGE_TABLE_NAME + " WHERE ("
						+ DBColumns.MESSAGE_FROM + "=? AND " + DBColumns.MESSAGE_TO
						+ "=?) OR (" + DBColumns.MESSAGE_FROM + "=? AND "
						+ DBColumns.MESSAGE_TO + "=?) AND " + DBColumns.MESSAGE_TYPE
						+ "=?" + " ORDER BY " + DBColumns.MESSAGE_CREATE_TIME,
				new String[] { current.getAccount(), contact.getAccount(),
						contact.getAccount(), current.getAccount(),
						DBColumns.MESSAGE_TYPE_CONTACT + "" });
		return DBObjectHelper.getDB3ContactMessages(current, contact, cursor);
	}

	@Override
	public List<DBMessage> findGroup(DBUser current, DBGroup group) {
		// db = getTransactionDB();
		//
		// // 用户是否属于该群,不属于,无权查看消息
		// boolean isContain = gmDao.isExistUser(group, current);
		// // System.out.println("mUser:" + current.getAccount() + ",group:" +
		// // group.getAccount() + "  " + isContain);
		// if (!isContain) {
		// return null;
		// }
		//
		// Cursor cursor = db.rawQuery(
		// "SELECT * FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
		// + DBColumns.MESSAGE_TYPE + "=? AND "
		// + DBColumns.MESSAGE_TO + "=? ORDER BY "
		// + DBColumns.MESSAGE_CREATE_TIME,
		// new String[] { DBColumns.MESSAGE_TYPE_GROUP + "",
		// group.getAccount() });
		// List<DBMessage> messages = new ArrayList<DBMessage>();
		// while (cursor.moveToNext()) {
		// // 查找消息发送者
		// String fromAccount = cursor.getString(cursor
		// .getColumnIndex(DBColumns.MESSAGE_FROM));
		// DBUser fromUser = userDao.findByAccount(fromAccount);
		// messages.add(DBObjectHelper.getDB3GroupMessage(fromUser, group,
		// cursor));
		// }
		// cursor.close();
		//
		// return messages;

		return findGroup(current.getAccount(), group.getAccount());
	}

	@Override
	public List<DBMessage> findSystemGroup(DBUser current,
											DBSystemGroup group) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
						+ DBColumns.MESSAGE_TYPE + "=? AND "
						+ DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_FROM + "=?",
				new String[] { DBColumns.MESSAGE_TYPE_SYS + "",
						current.getAccount(), group.getAccount() });

		return DBObjectHelper.getDB3SystemMessages(current, group, cursor);
	}

	// private void dataChanged(int type) {
	// if (isMessageListening) {
	// messageListener.onMessageChanged(type);
	// }
	// }

	@Override
	public List<DBMessage> findSystemMessage(DBUser user) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery(
				"SELECT * FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
						+ DBColumns.MESSAGE_TYPE + "=? AND "
						+ DBColumns.MESSAGE_TO + "=? ",
				new String[] { DBColumns.MESSAGE_TYPE_SYS + "",
						user.getAccount() });
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			String groupAccout = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			DBSystemGroup group = sysDao.findByAccount(groupAccout);
			messages.add(DBObjectHelper.getDB3SystemMessage(user, group,
					cursor));
		}
		cursor.close();

		return messages;

	}

	@Override
	public int getUnreadedCount(DBMessage message) {
		db = getTransactionDB();

		String sql = null;
		String[] args = null;
		switch (message.getType()) {
			case DBColumns.MESSAGE_TYPE_GROUP:
				sql = "SELECT count(*) FROM " + DBColumns.MESSAGE_TABLE_NAME
						+ " WHERE " + DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_STATE + "=? AND "
						+ DBColumns.MESSAGE_TYPE + "=?";
				args = new String[] { message.getToGroup().getAccount(),
						DBColumns.MESSAGE_STATE_RECEIVED + "",
						DBColumns.MESSAGE_TYPE_GROUP + "" };
				break;
			case DBColumns.MESSAGE_TYPE_CONTACT:
				// (()or())必须加括号,否则()or(())
				sql = "SELECT count(*) FROM " + DBColumns.MESSAGE_TABLE_NAME
						+ " WHERE ((" + DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_FROM + "=?) OR ("
						+ DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_FROM + "=?)) AND "
						+ DBColumns.MESSAGE_STATE + "=? AND "
						+ DBColumns.MESSAGE_TYPE + "=?";
				args = new String[] { message.getFrom().getAccount(),
						message.getTo().getAccount(), message.getTo().getAccount(),
						message.getFrom().getAccount(),
						DBColumns.MESSAGE_STATE_RECEIVED + "",
						DBColumns.MESSAGE_TYPE_CONTACT + "" };
				break;
			case DBColumns.MESSAGE_TYPE_SYS:
				sql = "SELECT count(*) FROM " + DBColumns.MESSAGE_TABLE_NAME
						+ " WHERE " + DBColumns.MESSAGE_FROM + "=? AND "
						+ DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_STATE + "=? AND "
						+ DBColumns.MESSAGE_TYPE + "=?";
				args = new String[] { message.getFromGroup().getAccount(),
						message.getTo().getAccount(),
						DBColumns.MESSAGE_STATE_RECEIVED + "",
						DBColumns.MESSAGE_TYPE_SYS + "" };
				break;
		}

		Cursor cursor = db.rawQuery(sql, args);
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();

		return count;
	}

	@Override
	public void close() {
		super.close();

		userDao.close();
		groupDao.close();
		sysDao.close();
		gmDao.close();
	}

	@Override
	public void updateReadedMessage(String fromAccount, String toAccount,
									int type) {
		// 更新已读消息
		db = getTransactionDB();

		//
		String sql = null;
		Object[] args = null;

		switch (type) {
			case DBColumns.MESSAGE_TYPE_CONTACT:
				sql = "UPDATE " + DBColumns.MESSAGE_TABLE_NAME + " SET "
						+ DBColumns.MESSAGE_STATE + "=? WHERE (("
						+ DBColumns.MESSAGE_FROM + "=? AND "
						+ DBColumns.MESSAGE_TO + "=?) OR ("
						+ DBColumns.MESSAGE_FROM + "=? AND "
						+ DBColumns.MESSAGE_TO + "=?)) AND "
						+ DBColumns.MESSAGE_TYPE + "=? AND "
						+ DBColumns.MESSAGE_STATE + "=?";
				args = new Object[] { DBColumns.MESSAGE_STATE_READED, fromAccount,
						toAccount, toAccount, fromAccount, type,
						DBColumns.MESSAGE_STATE_RECEIVED };
				break;
			case DBColumns.MESSAGE_TYPE_GROUP:
				sql = "UPDATE " + DBColumns.MESSAGE_TABLE_NAME + " SET "
						+ DBColumns.MESSAGE_STATE + "=? WHERE "
						+ DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_TYPE + "=? AND "
						+ DBColumns.MESSAGE_STATE + "=?";
				args = new Object[] { DBColumns.MESSAGE_STATE_READED, toAccount,
						type, DBColumns.MESSAGE_STATE_RECEIVED };
				break;
			case DBColumns.MESSAGE_TYPE_SYS:
				sql = "UPDATE " + DBColumns.MESSAGE_TABLE_NAME + " SET "
						+ DBColumns.MESSAGE_STATE + "=? WHERE "
						+ DBColumns.MESSAGE_FROM + "=? AND "
						+ DBColumns.MESSAGE_TO + "=? AND "
						+ DBColumns.MESSAGE_TYPE + "=? AND "
						+ DBColumns.MESSAGE_STATE + "=?";
				args = new Object[] { DBColumns.MESSAGE_STATE_READED, fromAccount,
						toAccount, type, DBColumns.MESSAGE_STATE_RECEIVED };
				break;
		}

		db.execSQL(sql, args);
	}

	@Override
	public List<DBMessage> findContact(String currentAccount,
										String contactAccount) {
		db = getTransactionDB();

		DBUser current = userDao.findByAccount(currentAccount);
		DBUser contact = userDao.findByAccount(contactAccount);

		return findContact(current, contact);
	}

	@Override
	public List<DBMessage> findSystemMessage(String userAccount,
											  String sysAccount) {
		db = getTransactionDB();
		DBUser user = userDao.findByAccount(userAccount);
		DBSystemGroup group = sysDao.findByAccount(sysAccount);

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_TYPE + "=? AND " + DBColumns.MESSAGE_TO
				+ "=? AND " + DBColumns.MESSAGE_FROM + "=?", new String[] {
				DBColumns.MESSAGE_TYPE_SYS + "", userAccount, sysAccount });
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			messages.add(DBObjectHelper.getDB3SystemMessage(user, group,
					cursor));
		}
		cursor.close();

		return messages;
	}

	@Override
	public List<DBMessage> findGroup(String userAccount, String groupAccount) {
		db = getTransactionDB();

		// 用户是否属于该群,不属于,无权查看消息
		List<DBMessage> messages = new ArrayList<DBMessage>();
		boolean isContain = gmDao.isExistUser(groupAccount, userAccount);
		if (!isContain) {
			return messages;
		}

		DBGroup group = groupDao.findByAccount(groupAccount);

		Cursor cursor = db
				.rawQuery("SELECT * FROM " + DBColumns.MESSAGE_TABLE_NAME
						+ " WHERE " + DBColumns.MESSAGE_TYPE + "=? AND "
						+ DBColumns.MESSAGE_TO + "=? ORDER BY "
						+ DBColumns.MESSAGE_CREATE_TIME, new String[] {
						DBColumns.MESSAGE_TYPE_GROUP + "", groupAccount });

		while (cursor.moveToNext()) {
			// 查找消息发送者
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_FROM));
			DBUser fromUser = userDao.findByAccount(fromAccount);
			messages.add(DBObjectHelper.getDB3GroupMessage(fromUser, group,
					cursor));
		}
		cursor.close();

		return messages;
	}

	@Override
	public int getUnreadedCount(String userAccount) {
		int count = 0;
		db = getTransactionDB();

		// 查询好友和系统未读信息
		Cursor cursor = db.rawQuery("SELECT count(*) FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_TO + "=? AND " + DBColumns.MESSAGE_STATE
				+ "=? AND " + DBColumns.MESSAGE_TYPE + "!=?", new String[] {
				userAccount, DBColumns.MESSAGE_STATE_RECEIVED + "",
				DBColumns.MESSAGE_TYPE_GROUP + "" });
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();

		// 查询群未读消息
		// select count(*) from message m,group_state gs
		// where m.[msg_type]=1 and m.[state]=3 and
		// m.[msg_to]=gs.[group_account] and gs.[user_account]='100000'
		cursor = db.rawQuery("SELECT count(*) FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + " m,"
				+ DBColumns.GROUP_STATE_TABLE_NAME + " gs WHERE m."
				+ DBColumns.MESSAGE_TYPE + "=? AND m."
				+ DBColumns.MESSAGE_STATE + "=? AND m."
				+ DBColumns.MESSAGE_TO + "=gs."
				+ DBColumns.GROUP_STATE_GROUP_ACCOUNT + " AND gs."
				+ DBColumns.GROUP_STATE_USER_ACCOUNT + "=?", new String[] {
				DBColumns.MESSAGE_TYPE_GROUP + "",
				DBColumns.MESSAGE_STATE_RECEIVED + "", userAccount });
		if (cursor.moveToFirst()) {
			count += cursor.getInt(0);
		}
		cursor.close();

		return count;
	}

	public String getMaxAccount() {
		db = getTransactionDB();

		// select account from message where _id=(select max(_id) from message)
		Cursor cursor = db.rawQuery("SELECT " + DBColumns.MESSAGE_ACCOUNT
				+ " FROM " + DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_ID + "=(SELECT max("
				+ DBColumns.MESSAGE_ID + ") FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + ")", null);
		String account = "";
		if (cursor.moveToFirst()) {
			account = cursor.getString(0);
		}
		cursor.close();
		// int num = Integer.parseInt(account.split("-")[1]) + 1;

		return account;
	}

	@Override
	public void add(XMPPMessage message) {
		// 查找当前最大的account
		String account = getMaxAccount();
		int num = Integer.parseInt(account.split("-")[1]) + 1;
		switch (message.getMsgType()) {
			case DBColumns.MESSAGE_TYPE_CONTACT:
				account = "cot-" + num;
				break;
			case DBColumns.MESSAGE_TYPE_GROUP:
				account = "grp-" + num;
				break;
			case DBColumns.MESSAGE_TYPE_SYS:
				account = "sys-" + num;
				break;
		}

		db = getTransactionDB();

		// 获取msg from
		DBUser contact = userDao.findByName(message.getFrom());
		if (contact == null) {
			// 好友不存在,添加
			contact = new DBUser();
			String acc = userDao.getMaxAccount();
			contact.setAccount(Integer.parseInt(acc) + 1 + "");
			contact.setNickname(message.getFrom());
			contact.setRegisterTime(new Date().getTime());
			userDao.add(contact);

			// 添加好友对应的好友分组??
		}

		db.execSQL(
				"INSERT INTO " + DBColumns.MESSAGE_TABLE_NAME + "("
						+ DBColumns.MESSAGE_ACCOUNT + ","
						+ DBColumns.MESSAGE_FROM + "," + DBColumns.MESSAGE_TO
						+ "," + DBColumns.MESSAGE_MSG + ","
						+ DBColumns.MESSAGE_CREATE_TIME + ","
						+ DBColumns.MESSAGE_TYPE + ","
						+ DBColumns.MESSAGE_STATE + ") VALUES(?,?,?,?,?,?,?)",
				new Object[] { account, contact.getAccount(), message.getTo(),
						message.getMsg(), message.getCreateTime(),
						message.getMsgType(), message.getState() });

	}

	@Override
	public int getUnreadedCount(String attr, String value) {
		int count = 0;
		db = getTransactionDB();

		// 查询account
		Cursor cursor = db.rawQuery("SELECT " + DBColumns.USER_ACCOUNT
				+ " FROM " + DBColumns.USER_TABLE_NAME + " WHERE " + attr
				+ "=?", new String[] { value });

		if (cursor.moveToFirst()) {
			count = getUnreadedCount(cursor.getString(0));
		}
		cursor.close();

		return count;
	}

	@Override
	public List<XMPPMessage> findOffLineMessages(String userAccount) {
		db = getTransactionDB();

		List<XMPPMessage> messages = new ArrayList<XMPPMessage>();
		// DBUser mUser = userDao.findByAccount(userAccount);
		// if(mUser == null)
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBColumns.MESSAGE_TABLE_NAME + " WHERE "
				+ DBColumns.MESSAGE_FROM + "=? AND "
				+ DBColumns.MESSAGE_STATE + "=? AND " + DBColumns.MESSAGE_TO
				+ "=?", new String[] { userAccount,
				DBColumns.MESSAGE_STATE_SENDING + "",
				DBColumns.MESSAGE_TYPE_CONTACT + "" });
		XMPPMessage message = null;
		DBUser to = null;
		while (cursor.moveToNext()) {
			message = new XMPPMessage();
			message.setAccount(cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_ACCOUNT)));
			to = userDao.findByAccount(cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_TO)));
			message.setTo(to.getNickname());
			message.setMsg(cursor.getString(cursor
					.getColumnIndex(DBColumns.MESSAGE_MSG)));
			message.setCreateTime(cursor.getLong(cursor
					.getColumnIndex(DBColumns.MESSAGE_CREATE_TIME)));
			messages.add(message);
			message = null;
			to = null;
		}
		cursor.close();

		return messages;
	}

	@Override
	public void updateXMPPMessageState(String messageAccount, int messageState) {
		db = getTransactionDB();

		db.execSQL("UPDATE " + DBColumns.MESSAGE_TABLE_NAME + " SET "
				+ DBColumns.MESSAGE_STATE + "=? WHERE "
				+ DBColumns.MESSAGE_ACCOUNT + "=?", new String[] {
				messageState + "", messageAccount });
	}
}
