package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean3.XMPPMessage;
import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3Message;
import com.zyxb.qqxmpp.bean3.po.DB3ObjectHelper;
import com.zyxb.qqxmpp.bean3.po.DB3SystemGroup;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3GroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO;
import com.zyxb.qqxmpp.db3.dao.DB3SystemGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB3MessageDAOImpl extends DB3MessageDAO {
	private DB3UserDAO userDao;
	private DB3GroupDAO groupDao;
	private DB3SystemGroupDAO sysDao;
	private DB3GroupMappingDAO gmDao;

	public DB3MessageDAOImpl(Context context) {
		super(context);

		userDao = DAOFactory.getDB3UserDAO(context);
		groupDao = DAOFactory.getDB3GroupDAO(context);
		sysDao = DAOFactory.getDB3SystemGroupDAO(context);
		gmDao = DAOFactory.getDB3GroupMappingDAO(context);
	}

	@Override
	public int add(DB3Message message) {
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

		db.execSQL("INSERT INTO " + DB3Columns.MESSAGE_TABLE_NAME + "("
				+ DB3Columns.MESSAGE_ACCOUNT + "," + DB3Columns.MESSAGE_FROM
				+ "," + DB3Columns.MESSAGE_TO + "," + DB3Columns.MESSAGE_MSG
				+ "," + DB3Columns.MESSAGE_CREATE_TIME + ","
				+ DB3Columns.MESSAGE_TYPE + "," + DB3Columns.MESSAGE_STATE
				+ "," + DB3Columns.MESSAGE_DATA1 + ","
				+ DB3Columns.MESSAGE_DATA2 + "," + DB3Columns.MESSAGE_DATA3
				+ "," + DB3Columns.MESSAGE_DATA4 + ","
				+ DB3Columns.MESSAGE_DATA5 + "," + DB3Columns.MESSAGE_DATA6
				+ "," + DB3Columns.MESSAGE_DATA7
				+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[] {
				message.getAccount(), fromAccount, toAccount, message.getMsg(),
				message.getCreateTime(), message.getType(), message.getState(),
				message.getData1(), message.getData2(), message.getData3(),
				message.getData4(), message.getData5(), message.getData6(),
				message.getData7() });

		// dataChanged(message.getType());
		notifyDataChanged(message.getType());

		return DB3Columns.RESULT_OK;
	}

	private String[] getAccount(DB3Message message) {
		String[] temp = new String[2];

		// 需要根据不同类型,判断from,to
		String fromAccount = null;
		String toAccount = null;
		switch (message.getType()) {
			case DB3Columns.MESSAGE_TYPE_GROUP:
				fromAccount = message.getFrom().getAccount();
				toAccount = message.getToGroup().getAccount();
				break;
			case DB3Columns.MESSAGE_TYPE_CONTACT:
				fromAccount = message.getFrom().getAccount();
				toAccount = message.getTo().getAccount();
				break;
			case DB3Columns.MESSAGE_TYPE_SYS:
				fromAccount = message.getFromGroup().getAccount();
				toAccount = message.getTo().getAccount();
				break;
		}
		temp[0] = fromAccount;
		temp[1] = toAccount;

		return temp;
	}

	@Override
	public DB3Message findByAccount(String account) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_ACCOUNT + "=?", new String[] { account });
		DB3Message message = null;
		if (cursor.moveToFirst()) {
			int type = cursor.getInt(cursor
					.getColumnIndex(DB3Columns.MESSAGE_TYPE));
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			String toAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_TO));

			DB3User from, to;
			DB3Group toGroup;
			DB3SystemGroup fromGroup;
			switch (type) {
				case DB3Columns.MESSAGE_TYPE_GROUP:
					// 群消息 from=消息发送方 to=群
					// 查询from
					from = userDao.findByAccount(fromAccount);
					toGroup = groupDao.findByAccount(toAccount);
					message = DB3ObjectHelper.getDB3GroupMessage(from, toGroup,
							cursor);

					break;
				case DB3Columns.MESSAGE_TYPE_CONTACT:
					// 联系人聊天 from=消息发送方 to=消息接收方
					from = userDao.findByAccount(fromAccount);
					to = userDao.findByAccount(toAccount);
					message = DB3ObjectHelper
							.getDB3ContactMessage(from, to, cursor);

					break;
				case DB3Columns.MESSAGE_TYPE_SYS:
					// 系统消息 from=系统 to=消息接收方
					fromGroup = sysDao.findByAccount(fromAccount);
					to = userDao.findByAccount(toAccount);
					message = DB3ObjectHelper.getDB3SystemMessage(to, fromGroup,
							cursor);

					break;
			}
		}
		cursor.close();

		return message;
	}

	@Override
	public int update(DB3Message message) {
		db = getTransactionDB();

		// 是否存在
		DB3Message m = findByAccount(message.getAccount());
		if (m == null) {
			return DB3Columns.ERROR_MESSAGE_NOT_FOUND;
		}

		// 获取from/to account
		String[] accounts = getAccount(message);
		String fromAccount = accounts[0];
		String toAccount = accounts[1];

		// 更新
		db.execSQL(
				"UPDATE " + DB3Columns.MESSAGE_TABLE_NAME + " SET "
						+ DB3Columns.MESSAGE_FROM + "=?,"
						+ DB3Columns.MESSAGE_MSG + "=?,"
						+ DB3Columns.MESSAGE_TO + "=?,"
						+ DB3Columns.MESSAGE_CREATE_TIME + "=?,"
						+ DB3Columns.MESSAGE_TYPE + "=?,"
						+ DB3Columns.MESSAGE_STATE + "=?,"
						+ DB3Columns.MESSAGE_DATA1 + "=?,"
						+ DB3Columns.MESSAGE_DATA2 + "=?,"
						+ DB3Columns.MESSAGE_DATA3 + "=?,"
						+ DB3Columns.MESSAGE_DATA4 + "=?,"
						+ DB3Columns.MESSAGE_DATA5 + "=?,"
						+ DB3Columns.MESSAGE_DATA6 + "=?,"
						+ DB3Columns.MESSAGE_DATA7 + "=? WHERE "
						+ DB3Columns.MESSAGE_ACCOUNT + "=?",
				new Object[] { fromAccount, message.getMsg(), toAccount,
						message.getCreateTime(), message.getType(),
						message.getState(), message.getData1(),
						message.getData2(), message.getData3(),
						message.getData4(), message.getData5(),
						message.getData6(), message.getData7(),
						message.getAccount() });

		// dataChanged(message.getType());
		notifyDataChanged(message.getType());

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3Message message) {
		db = getTransactionDB();

		DB3Message m = findByAccount(message.getAccount());
		if (m == null) {
			return DB3Columns.ERROR_MESSAGE_NOT_FOUND;
		}

		// 删除
		db.execSQL("DELETE FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
						+ DB3Columns.MESSAGE_ACCOUNT + "=?",
				new Object[] { message.getAccount() });

		// dataChanged(message.getType());
		notifyDataChanged(message.getType());

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int delete(DB3User user) {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_FROM + "=? OR " + DB3Columns.MESSAGE_TO
				+ "=?", new Object[] { user.getAccount(), user.getAccount() });

		// dataChanged(DB3Columns.MESSAGE_TYPE_ALL);
		notifyDataChanged(DB3Columns.MESSAGE_TYPE_ALL);

		return DB3Columns.RESULT_OK;
	}

	@Override
	public int clear() {
		db = getTransactionDB();

		db.execSQL("DELETE FROM " + DB3Columns.MESSAGE_TABLE_NAME
				+ " WHERE 1=1");

		// dataChanged(DB3Columns.MESSAGE_TYPE_ALL);
		notifyDataChanged(DB3Columns.MESSAGE_TYPE_ALL);

		return DB3Columns.RESULT_OK;
	}

	@Override
	public List<DB3Message> findNewest(DB3User user) {
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
				+ DB3Columns.MESSAGE_CREATE_TIME + ") t FROM "
				+ DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_TYPE + "=? AND " + DB3Columns.MESSAGE_TO
				+ "=? GROUP BY " + DB3Columns.MESSAGE_FROM + " HAVING "
				+ DB3Columns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DB3Columns.MESSAGE_TYPE_SYS + "", user.getAccount() });
		List<DB3Message> messages = new ArrayList<DB3Message>();
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			DB3SystemGroup group = sysDao.findByAccount(fromAccount);
			messages.add(DB3ObjectHelper.getDB3SystemMessage(user, group,
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
		cursor = db.rawQuery("SELECT m.*,max(" + DB3Columns.MESSAGE_CREATE_TIME
				+ ") t FROM " + DB3Columns.MESSAGE_TABLE_NAME + " m,"
				+ DB3Columns.GROUP_STATE_TABLE_NAME + " g WHERE m."
				+ DB3Columns.MESSAGE_TYPE + "=? AND m." + DB3Columns.MESSAGE_TO
				+ "=g." + DB3Columns.GROUP_STATE_GROUP_ACCOUNT + " AND g."
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=? GROUP BY m."
				+ DB3Columns.MESSAGE_TO + " HAVING m."
				+ DB3Columns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DB3Columns.MESSAGE_TYPE_GROUP + "", user.getAccount() });
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			String groupAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_TO));
			DB3Group group = groupDao.findByAccount(groupAccount);
			DB3User u = userDao.findByAccount(fromAccount);
			messages.add(DB3ObjectHelper.getDB3GroupMessage(u, group, cursor));
		}
		cursor.close();

		// 添加contact message
		// select *,max(create_time) from message where msg_type='2' and
		// (msg_from='100000' or msg_to='100000') group by msg_from+msg_to
		// 不行没选中max(create_time)
		// cursor = db.rawQuery("SELECT *,max(" + DB3Columns.MESSAGE_CREATE_TIME
		// + ") FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
		// + DB3Columns.MESSAGE_TYPE + "=? AND ("
		// + DB3Columns.MESSAGE_FROM + "=? OR " + DB3Columns.MESSAGE_TO
		// + "=?) GROUP BY msg_from,msg_to",

		// SQLite OK,运行不行
		// select *,max(create_time) time from message where msg_from='100000'
		// or msg_to='100000' group by msg_from,msg_to having create_time = time
		// order by create_time desc;

		// cursor = db.rawQuery(
		// "SELECT *,max(" + DB3Columns.MESSAGE_CREATE_TIME + ") t FROM "
		// + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
		// + DB3Columns.MESSAGE_TYPE + "=? AND ("
		// + DB3Columns.MESSAGE_FROM + "=? OR "
		// + DB3Columns.MESSAGE_TO + "=?) GROUP BY "
		// + DB3Columns.MESSAGE_FROM + "," + DB3Columns.MESSAGE_TO
		// + " HAVING " + DB3Columns.MESSAGE_CREATE_TIME + "=t",
		// new String[] { DB3Columns.MESSAGE_TYPE_CONTACT + "",
		// user.getAccount(), user.getAccount() });

		// select *,max(create_time) t from message where msg_type=2 and
		// msg_from='100000' group by msg_to having create_time=t
		// union
		// select *,max(create_time) t from message where msg_type=2 and
		// msg_to='100000' group by msg_from having create_time=t

		// cursor = db.rawQuery("SELECT *,max(" + DB3Columns.MESSAGE_CREATE_TIME
		// + ") t FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
		// + DB3Columns.MESSAGE_TYPE + "=? AND " + DB3Columns.MESSAGE_FROM
		// + "=? " + "GROUP BY " + DB3Columns.MESSAGE_TO + " HAVING "
		// + DB3Columns.MESSAGE_CREATE_TIME + "=t UNION ALL SELECT *,max("
		// + DB3Columns.MESSAGE_CREATE_TIME + ") t FROM "
		// + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
		// + DB3Columns.MESSAGE_TYPE + "=? AND " + DB3Columns.MESSAGE_TO
		// + "=? " + "GROUP BY " + DB3Columns.MESSAGE_FROM + " HAVING "
		// + DB3Columns.MESSAGE_CREATE_TIME + "=t", new String[] {
		// DB3Columns.MESSAGE_TYPE_CONTACT + "", user.getAccount(),
		// DB3Columns.MESSAGE_TYPE_CONTACT + "", user.getAccount() });

		// 发送的消息
		cursor = db.rawQuery("SELECT *,max(" + DB3Columns.MESSAGE_CREATE_TIME
				+ ") t FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_TYPE + "=? AND " + DB3Columns.MESSAGE_FROM
				+ "=? " + "GROUP BY " + DB3Columns.MESSAGE_TO + " HAVING "
				+ DB3Columns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DB3Columns.MESSAGE_TYPE_CONTACT + "", user.getAccount() });

		// cursor = db
		// .rawQuery(
		// "select *,max(create_time) time from message where msg_type=? and (msg_from=?	or msg_to=?) group by msg_from,msg_to having create_time = time order by create_time desc",
		// new String[] { DB3Columns.MESSAGE_TYPE_CONTACT + "",
		// user.getAccount(), user.getAccount() });

		// System.out.println("from count:" + cursor.getCount());// 2??
		// System.out.println("user:" + user.getAccount());

		// cursor.moveToPrevious();
		List<DB3Message> ms = new ArrayList<DB3Message>();
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			String toAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_TO));
			DB3User fu = userDao.findByAccount(fromAccount);
			DB3User tu = userDao.findByAccount(toAccount);
			ms.add(DB3ObjectHelper.getDB3ContactMessage(fu, tu, cursor));
		}
		cursor.close();

		// 接收的消息
		cursor = db.rawQuery("SELECT *,max(" + DB3Columns.MESSAGE_CREATE_TIME
				+ ") t FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_TYPE + "=? AND " + DB3Columns.MESSAGE_TO
				+ "=? " + "GROUP BY " + DB3Columns.MESSAGE_FROM + " HAVING "
				+ DB3Columns.MESSAGE_CREATE_TIME + "=t", new String[] {
				DB3Columns.MESSAGE_TYPE_CONTACT + "", user.getAccount() });
		// System.out.println("to count:" + cursor.getCount());
		while (cursor.moveToNext()) {
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			String toAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_TO));
			DB3User fu = userDao.findByAccount(fromAccount);
			DB3User tu = userDao.findByAccount(toAccount);
			ms.add(DB3ObjectHelper.getDB3ContactMessage(fu, tu, cursor));
		}
		cursor.close();

		// 去掉重复
		if (ms.size() > 0) {
			Map<String, DB3Message> mzs = new HashMap<String, DB3Message>();
			for (int i = ms.size() - 1; i >= 0; i--) {
				DB3Message mm = ms.get(i);
				String tmp1 = mm.getFrom().getAccount() + ""
						+ mm.getTo().getAccount();
				if (mzs.containsKey(tmp1)) {
					// 比较时间
					DB3Message tmp = mzs.get(tmp1);
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
	public List<DB3Message> findContact(DB3User current, DB3User contact) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery("SELECT * FROM "
						+ DB3Columns.MESSAGE_TABLE_NAME + " WHERE ("
						+ DB3Columns.MESSAGE_FROM + "=? AND " + DB3Columns.MESSAGE_TO
						+ "=?) OR (" + DB3Columns.MESSAGE_FROM + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=?) AND " + DB3Columns.MESSAGE_TYPE
						+ "=?" + " ORDER BY " + DB3Columns.MESSAGE_CREATE_TIME,
				new String[] { current.getAccount(), contact.getAccount(),
						contact.getAccount(), current.getAccount(),
						DB3Columns.MESSAGE_TYPE_CONTACT + "" });
		return DB3ObjectHelper.getDB3ContactMessages(current, contact, cursor);
	}

	@Override
	public List<DB3Message> findGroup(DB3User current, DB3Group group) {
		// db = getTransactionDB();
		//
		// // 用户是否属于该群,不属于,无权查看消息
		// boolean isContain = gmDao.isExistUser(group, current);
		// // System.out.println("user:" + current.getAccount() + ",group:" +
		// // group.getAccount() + "  " + isContain);
		// if (!isContain) {
		// return null;
		// }
		//
		// Cursor cursor = db.rawQuery(
		// "SELECT * FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
		// + DB3Columns.MESSAGE_TYPE + "=? AND "
		// + DB3Columns.MESSAGE_TO + "=? ORDER BY "
		// + DB3Columns.MESSAGE_CREATE_TIME,
		// new String[] { DB3Columns.MESSAGE_TYPE_GROUP + "",
		// group.getAccount() });
		// List<DB3Message> messages = new ArrayList<DB3Message>();
		// while (cursor.moveToNext()) {
		// // 查找消息发送者
		// String fromAccount = cursor.getString(cursor
		// .getColumnIndex(DB3Columns.MESSAGE_FROM));
		// DB3User fromUser = userDao.findByAccount(fromAccount);
		// messages.add(DB3ObjectHelper.getDB3GroupMessage(fromUser, group,
		// cursor));
		// }
		// cursor.close();
		//
		// return messages;

		return findGroup(current.getAccount(), group.getAccount());
	}

	@Override
	public List<DB3Message> findSystemGroup(DB3User current,
											DB3SystemGroup group) {
		db = getTransactionDB();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
						+ DB3Columns.MESSAGE_TYPE + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_FROM + "=?",
				new String[] { DB3Columns.MESSAGE_TYPE_SYS + "",
						current.getAccount(), group.getAccount() });

		return DB3ObjectHelper.getDB3SystemMessages(current, group, cursor);
	}

	// private void dataChanged(int type) {
	// if (isMessageListening) {
	// messageListener.onMessageChanged(type);
	// }
	// }

	@Override
	public List<DB3Message> findSystemMessage(DB3User user) {
		db = getTransactionDB();
		Cursor cursor = db.rawQuery(
				"SELECT * FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
						+ DB3Columns.MESSAGE_TYPE + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=? ",
				new String[] { DB3Columns.MESSAGE_TYPE_SYS + "",
						user.getAccount() });
		List<DB3Message> messages = new ArrayList<DB3Message>();
		while (cursor.moveToNext()) {
			String groupAccout = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			DB3SystemGroup group = sysDao.findByAccount(groupAccout);
			messages.add(DB3ObjectHelper.getDB3SystemMessage(user, group,
					cursor));
		}
		cursor.close();

		return messages;

	}

	@Override
	public int getUnreadedCount(DB3Message message) {
		db = getTransactionDB();

		String sql = null;
		String[] args = null;
		switch (message.getType()) {
			case DB3Columns.MESSAGE_TYPE_GROUP:
				sql = "SELECT count(*) FROM " + DB3Columns.MESSAGE_TABLE_NAME
						+ " WHERE " + DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_STATE + "=? AND "
						+ DB3Columns.MESSAGE_TYPE + "=?";
				args = new String[] { message.getToGroup().getAccount(),
						DB3Columns.MESSAGE_STATE_RECEIVED + "",
						DB3Columns.MESSAGE_TYPE_GROUP + "" };
				break;
			case DB3Columns.MESSAGE_TYPE_CONTACT:
				// (()or())必须加括号,否则()or(())
				sql = "SELECT count(*) FROM " + DB3Columns.MESSAGE_TABLE_NAME
						+ " WHERE ((" + DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_FROM + "=?) OR ("
						+ DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_FROM + "=?)) AND "
						+ DB3Columns.MESSAGE_STATE + "=? AND "
						+ DB3Columns.MESSAGE_TYPE + "=?";
				args = new String[] { message.getFrom().getAccount(),
						message.getTo().getAccount(), message.getTo().getAccount(),
						message.getFrom().getAccount(),
						DB3Columns.MESSAGE_STATE_RECEIVED + "",
						DB3Columns.MESSAGE_TYPE_CONTACT + "" };
				break;
			case DB3Columns.MESSAGE_TYPE_SYS:
				sql = "SELECT count(*) FROM " + DB3Columns.MESSAGE_TABLE_NAME
						+ " WHERE " + DB3Columns.MESSAGE_FROM + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_STATE + "=? AND "
						+ DB3Columns.MESSAGE_TYPE + "=?";
				args = new String[] { message.getFromGroup().getAccount(),
						message.getTo().getAccount(),
						DB3Columns.MESSAGE_STATE_RECEIVED + "",
						DB3Columns.MESSAGE_TYPE_SYS + "" };
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
			case DB3Columns.MESSAGE_TYPE_CONTACT:
				sql = "UPDATE " + DB3Columns.MESSAGE_TABLE_NAME + " SET "
						+ DB3Columns.MESSAGE_STATE + "=? WHERE (("
						+ DB3Columns.MESSAGE_FROM + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=?) OR ("
						+ DB3Columns.MESSAGE_FROM + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=?)) AND "
						+ DB3Columns.MESSAGE_TYPE + "=? AND "
						+ DB3Columns.MESSAGE_STATE + "=?";
				args = new Object[] { DB3Columns.MESSAGE_STATE_READED, fromAccount,
						toAccount, toAccount, fromAccount, type,
						DB3Columns.MESSAGE_STATE_RECEIVED };
				break;
			case DB3Columns.MESSAGE_TYPE_GROUP:
				sql = "UPDATE " + DB3Columns.MESSAGE_TABLE_NAME + " SET "
						+ DB3Columns.MESSAGE_STATE + "=? WHERE "
						+ DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_TYPE + "=? AND "
						+ DB3Columns.MESSAGE_STATE + "=?";
				args = new Object[] { DB3Columns.MESSAGE_STATE_READED, toAccount,
						type, DB3Columns.MESSAGE_STATE_RECEIVED };
				break;
			case DB3Columns.MESSAGE_TYPE_SYS:
				sql = "UPDATE " + DB3Columns.MESSAGE_TABLE_NAME + " SET "
						+ DB3Columns.MESSAGE_STATE + "=? WHERE "
						+ DB3Columns.MESSAGE_FROM + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=? AND "
						+ DB3Columns.MESSAGE_TYPE + "=? AND "
						+ DB3Columns.MESSAGE_STATE + "=?";
				args = new Object[] { DB3Columns.MESSAGE_STATE_READED, fromAccount,
						toAccount, type, DB3Columns.MESSAGE_STATE_RECEIVED };
				break;
		}

		db.execSQL(sql, args);
	}

	@Override
	public List<DB3Message> findContact(String currentAccount,
										String contactAccount) {
		db = getTransactionDB();

		DB3User current = userDao.findByAccount(currentAccount);
		DB3User contact = userDao.findByAccount(contactAccount);

		return findContact(current, contact);
	}

	@Override
	public List<DB3Message> findSystemMessage(String userAccount,
											  String sysAccount) {
		db = getTransactionDB();
		DB3User user = userDao.findByAccount(userAccount);
		DB3SystemGroup group = sysDao.findByAccount(sysAccount);

		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_TYPE + "=? AND " + DB3Columns.MESSAGE_TO
				+ "=? AND " + DB3Columns.MESSAGE_FROM + "=?", new String[] {
				DB3Columns.MESSAGE_TYPE_SYS + "", userAccount, sysAccount });
		List<DB3Message> messages = new ArrayList<DB3Message>();
		while (cursor.moveToNext()) {
			messages.add(DB3ObjectHelper.getDB3SystemMessage(user, group,
					cursor));
		}
		cursor.close();

		return messages;
	}

	@Override
	public List<DB3Message> findGroup(String userAccount, String groupAccount) {
		db = getTransactionDB();

		// 用户是否属于该群,不属于,无权查看消息
		List<DB3Message> messages = new ArrayList<DB3Message>();
		boolean isContain = gmDao.isExistUser(groupAccount, userAccount);
		if (!isContain) {
			return messages;
		}

		DB3Group group = groupDao.findByAccount(groupAccount);

		Cursor cursor = db
				.rawQuery("SELECT * FROM " + DB3Columns.MESSAGE_TABLE_NAME
						+ " WHERE " + DB3Columns.MESSAGE_TYPE + "=? AND "
						+ DB3Columns.MESSAGE_TO + "=? ORDER BY "
						+ DB3Columns.MESSAGE_CREATE_TIME, new String[] {
						DB3Columns.MESSAGE_TYPE_GROUP + "", groupAccount });

		while (cursor.moveToNext()) {
			// 查找消息发送者
			String fromAccount = cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_FROM));
			DB3User fromUser = userDao.findByAccount(fromAccount);
			messages.add(DB3ObjectHelper.getDB3GroupMessage(fromUser, group,
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
				+ DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_TO + "=? AND " + DB3Columns.MESSAGE_STATE
				+ "=? AND " + DB3Columns.MESSAGE_TYPE + "!=?", new String[] {
				userAccount, DB3Columns.MESSAGE_STATE_RECEIVED + "",
				DB3Columns.MESSAGE_TYPE_GROUP + "" });
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();

		// 查询群未读消息
		// select count(*) from message m,group_state gs
		// where m.[msg_type]=1 and m.[state]=3 and
		// m.[msg_to]=gs.[group_account] and gs.[user_account]='100000'
		cursor = db.rawQuery("SELECT count(*) FROM "
				+ DB3Columns.MESSAGE_TABLE_NAME + " m,"
				+ DB3Columns.GROUP_STATE_TABLE_NAME + " gs WHERE m."
				+ DB3Columns.MESSAGE_TYPE + "=? AND m."
				+ DB3Columns.MESSAGE_STATE + "=? AND m."
				+ DB3Columns.MESSAGE_TO + "=gs."
				+ DB3Columns.GROUP_STATE_GROUP_ACCOUNT + " AND gs."
				+ DB3Columns.GROUP_STATE_USER_ACCOUNT + "=?", new String[] {
				DB3Columns.MESSAGE_TYPE_GROUP + "",
				DB3Columns.MESSAGE_STATE_RECEIVED + "", userAccount });
		if (cursor.moveToFirst()) {
			count += cursor.getInt(0);
		}
		cursor.close();

		return count;
	}

	public String getMaxAccount() {
		db = getTransactionDB();

		// select account from message where _id=(select max(_id) from message)
		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.MESSAGE_ACCOUNT
				+ " FROM " + DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_ID + "=(SELECT max("
				+ DB3Columns.MESSAGE_ID + ") FROM "
				+ DB3Columns.MESSAGE_TABLE_NAME + ")", null);
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
			case DB3Columns.MESSAGE_TYPE_CONTACT:
				account = "cot-" + num;
				break;
			case DB3Columns.MESSAGE_TYPE_GROUP:
				account = "grp-" + num;
				break;
			case DB3Columns.MESSAGE_TYPE_SYS:
				account = "sys-" + num;
				break;
		}

		db = getTransactionDB();

		// 获取msg from
		DB3User contact = userDao.findByName(message.getFrom());
		if (contact == null) {
			// 好友不存在,添加
			contact = new DB3User();
			String acc = userDao.getMaxAccount();
			contact.setAccount(Integer.parseInt(acc) + 1 + "");
			contact.setNickname(message.getFrom());
			contact.setRegisterTime(new Date().getTime());
			userDao.add(contact);

			// 添加好友对应的好友分组??
		}

		db.execSQL(
				"INSERT INTO " + DB3Columns.MESSAGE_TABLE_NAME + "("
						+ DB3Columns.MESSAGE_ACCOUNT + ","
						+ DB3Columns.MESSAGE_FROM + "," + DB3Columns.MESSAGE_TO
						+ "," + DB3Columns.MESSAGE_MSG + ","
						+ DB3Columns.MESSAGE_CREATE_TIME + ","
						+ DB3Columns.MESSAGE_TYPE + ","
						+ DB3Columns.MESSAGE_STATE + ") VALUES(?,?,?,?,?,?,?)",
				new Object[] { account, contact.getAccount(), message.getTo(),
						message.getMsg(), message.getCreateTime(),
						message.getMsgType(), message.getState() });

	}

	@Override
	public int getUnreadedCount(String attr, String value) {
		int count = 0;
		db = getTransactionDB();

		// 查询account
		Cursor cursor = db.rawQuery("SELECT " + DB3Columns.USER_ACCOUNT
				+ " FROM " + DB3Columns.USER_TABLE_NAME + " WHERE " + attr
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
		// DB3User user = userDao.findByAccount(userAccount);
		// if(user == null)
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DB3Columns.MESSAGE_TABLE_NAME + " WHERE "
				+ DB3Columns.MESSAGE_FROM + "=? AND "
				+ DB3Columns.MESSAGE_STATE + "=? AND " + DB3Columns.MESSAGE_TO
				+ "=?", new String[] { userAccount,
				DB3Columns.MESSAGE_STATE_SENDING + "",
				DB3Columns.MESSAGE_TYPE_CONTACT + "" });
		XMPPMessage message = null;
		DB3User to = null;
		while (cursor.moveToNext()) {
			message = new XMPPMessage();
			message.setAccount(cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_ACCOUNT)));
			to = userDao.findByAccount(cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_TO)));
			message.setTo(to.getNickname());
			message.setMsg(cursor.getString(cursor
					.getColumnIndex(DB3Columns.MESSAGE_MSG)));
			message.setCreateTime(cursor.getLong(cursor
					.getColumnIndex(DB3Columns.MESSAGE_CREATE_TIME)));
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

		db.execSQL("UPDATE " + DB3Columns.MESSAGE_TABLE_NAME + " SET "
				+ DB3Columns.MESSAGE_STATE + "=? WHERE "
				+ DB3Columns.MESSAGE_ACCOUNT + "=?", new String[] {
				messageState + "", messageAccount });
	}
}
