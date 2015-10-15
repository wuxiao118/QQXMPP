package com.zyxb.qqxmpp.engine;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.Information;
import com.zyxb.qqxmpp.bean3.MessageInfo;
import com.zyxb.qqxmpp.bean3.XMPPMessage;
import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3Message;
import com.zyxb.qqxmpp.bean3.po.DB3SystemGroup;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO.OnMessageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class DB3MessageEngine {
	private DB3MessageDAO dao;
	private DB3GroupMappingDAO gmDao;
	private DB3FriendGroupMappingDAO fgDao;

	public DB3MessageEngine(Context context) {
		dao = DAOFactory.getDB3MessageDAO(context);
		gmDao = DAOFactory.getDB3GroupMappingDAO(context);
		fgDao = DAOFactory.getDB3FriendGroupMappingDAO(context);
	}

	public List<MessageInfo> getNewest(DB3User user) {
		List<MessageInfo> messages = new ArrayList<MessageInfo>();
		List<DB3Message> ms = dao.findNewest(user);

		// 将DB3Message转换为MessageInfo
		MessageInfo minfo = null;
		for (int i = 0; i < ms.size(); i++) {
			DB3Message mm = ms.get(i);

			minfo = new MessageInfo();
			minfo.setAccount(mm.getAccount());
			minfo.setCount(dao.getUnreadedCount(mm));
			minfo.setCreateTime(mm.getCreateTime());
			minfo.setMsg(mm.getMsg());
			minfo.setState(mm.getState());
			minfo.setType(mm.getType());
			// 设置Information from
			Information from = new Information();
			Information to = new Information();
			switch (mm.getType()) {
				case DB3Columns.MESSAGE_TYPE_GROUP:
					DB3User u = mm.getFrom();
					DB3Group g = mm.getToGroup();
					from.setAccount(u.getAccount());
					from.setName(u.getNickname());
					from.setComments(gmDao.getRemark(u.getAccount(), g.getAccount()));
					to.setAccount(g.getAccount());
					to.setIcon(g.getIcon());
					to.setName(g.getName());
					u = null;
					g = null;
					break;
				case DB3Columns.MESSAGE_TYPE_CONTACT:
					DB3User u1 = mm.getFrom();
					DB3User u2 = mm.getTo();

					from.setAccount(u1.getAccount());
					from.setName(u1.getNickname());
					from.setIcon(u1.getIcon());
					if (u1.getAccount().equals(user.getAccount())) {
						// 为自己0
						from.setType(Information.TYPE_LOGIN_USER);
						to.setType(Information.TYPE_CONTACT_USER);
						to.setComments(fgDao.getRemark(user, u2));
					} else {
						from.setType(Information.TYPE_CONTACT_USER);
						to.setType(Information.TYPE_LOGIN_USER);
						from.setComments(fgDao.getRemark(user, u1));
					}

					to.setAccount(u2.getAccount());
					to.setName(u2.getNickname());
					to.setIcon(u2.getIcon());

					u1 = null;
					u2 = null;

					break;
				case DB3Columns.MESSAGE_TYPE_SYS:
					DB3SystemGroup sys = mm.getFromGroup();
					DB3User u3 = mm.getTo();

					from.setAccount(sys.getAccount());
					from.setIcon(sys.getIcon());
					from.setName(sys.getName());

					to.setAccount(u3.getAccount());
					to.setName(u3.getNickname());
					to.setIcon(u3.getIcon());

					break;
			}
			minfo.setFrom(from);
			minfo.setTo(to);
			from = null;
			to = null;

			messages.add(minfo);
			minfo = null;
		}

		dao.close();
		gmDao.close();
		fgDao.close();

		return messages;
	}

	public void updateReadedMessage(String fromAccount, String toAccount,
									int type) {
		dao.updateReadedMessage(fromAccount, toAccount, type);
		dao.close();
	}

	public List<MessageInfo> getContactMessage(String currentAccount,
											   String contactAccount, OnMessageChangeListener listener) {
		dao.setOnMessageChangeListener(listener);
		List<DB3Message> ms = dao.findContact(currentAccount, contactAccount);

		List<MessageInfo> infos = new ArrayList<MessageInfo>();
		MessageInfo info = null;
		for (int i = 0; i < ms.size(); i++) {
			DB3Message m = ms.get(i);
			info = new MessageInfo();

			info.setAccount(m.getAccount());
			info.setCreateTime(m.getCreateTime());
			info.setMsg(m.getMsg());
			info.setState(m.getState());
			info.setType(m.getType());

			// from
			DB3User u = m.getFrom();
			Information ifrom = new Information();
			ifrom.setAccount(u.getAccount());
			ifrom.setName(u.getNickname());
			ifrom.setIcon(u.getIcon());
			info.setFrom(ifrom);
			if (u.getAccount().equals(currentAccount)) {
				// 为当前用户
			} else {
				// 为好友
				ifrom.setComments(fgDao.getRemark(currentAccount,
						u.getAccount()));
			}
			ifrom = null;

			// to
			u = m.getTo();
			ifrom = new Information();
			ifrom.setAccount(u.getAccount());
			ifrom.setName(u.getNickname());
			ifrom.setIcon(u.getIcon());
			info.setTo(ifrom);
			if (u.getAccount().equals(currentAccount)) {
				// 为当前用户
			} else {
				// 为好友
				ifrom.setComments(fgDao.getRemark(currentAccount,
						u.getAccount()));
			}
			ifrom = null;

			infos.add(info);
			info = null;
		}
		ms = null;
		dao.close();
		fgDao.close();

		return infos;
	}

	public List<MessageInfo> getSystemMessage(String userAccount,
											  String sysAccount, OnMessageChangeListener listener) {
		dao.setOnMessageChangeListener(listener);
		List<DB3Message> ms = dao.findSystemMessage(userAccount, sysAccount);

		List<MessageInfo> infos = new ArrayList<MessageInfo>();
		MessageInfo info = null;
		for (int i = 0; i < ms.size(); i++) {
			DB3Message m = ms.get(i);
			info = new MessageInfo();

			info.setAccount(m.getAccount());
			info.setCreateTime(m.getCreateTime());
			info.setMsg(m.getMsg());
			info.setState(m.getState());
			info.setType(m.getType());

			// from
			DB3SystemGroup sg = m.getFromGroup();
			Information ifrom = new Information();
			ifrom.setAccount(sg.getAccount());
			ifrom.setName(sg.getName());
			ifrom.setIcon(sg.getIcon());
			info.setFrom(ifrom);
			ifrom = null;

			infos.add(info);
			info = null;
		}
		ms = null;
		dao.close();

		return infos;

	}

	public List<MessageInfo> getGroupMessage(String userAccount,
											 String groupAccount, OnMessageChangeListener listener) {
		dao.setOnMessageChangeListener(listener);
		List<DB3Message> ms = dao.findGroup(userAccount, groupAccount);

		List<MessageInfo> infos = new ArrayList<MessageInfo>();
		MessageInfo info = null;
		for (int i = 0; i < ms.size(); i++) {
			DB3Message m = ms.get(i);
			info = new MessageInfo();

			info.setAccount(m.getAccount());
			info.setCreateTime(m.getCreateTime());
			info.setMsg(m.getMsg());
			info.setState(m.getState());
			info.setType(m.getType());

			// from
			DB3User u = m.getFrom();
			Information ifrom = new Information();
			ifrom.setAccount(u.getAccount());
			ifrom.setName(u.getNickname());
			ifrom.setIcon(u.getIcon());
			ifrom.setComments(gmDao.getRemark(u.getAccount(), m.getToGroup()
					.getAccount()));
			ifrom.setGroupTitle(gmDao.getGroupTitle(u.getAccount(), m
					.getToGroup().getAccount()));
			info.setFrom(ifrom);
			ifrom = null;

			// to
			DB3Group g = m.getToGroup();
			ifrom = new Information();
			ifrom.setAccount(g.getAccount());
			ifrom.setName(g.getName());
			ifrom.setIcon(g.getIcon());
			info.setTo(ifrom);
			ifrom = null;

			infos.add(info);
			info = null;

		}
		ms = null;
		dao.close();
		gmDao.close();

		return infos;
	}

	public int getUnReadedMessages(String userAccount) {
		int count = dao.getUnreadedCount(userAccount);
		dao.close();

		return count;
	}

	public int getUnReadedMessages(String attr, String value) {
		int count = dao.getUnreadedCount(attr, value);
		dao.close();

		return count;
	}

	public void add(XMPPMessage message) {
		// message.setMsgType(DB3Columns.MESSAGE_TYPE_CONTACT);
		// message.setState(DB3Columns.MESSAGE_STATE_RECEIVED);
		dao.add(message);
		dao.close();
	}

	public void addNewXMPPMessage(XMPPMessage message) {
		message.setMsgType(DB3Columns.MESSAGE_TYPE_CONTACT);
		message.setState(DB3Columns.MESSAGE_STATE_RECEIVED);
		dao.add(message);
		dao.close();
	}

	public List<XMPPMessage> getOffLineMessages(String account) {
		List<XMPPMessage> messages = dao.findOffLineMessages(account);
		dao.close();

		return messages;
	}

	public void updateXMPPMessageState(String messageAccount, int messageState) {
		dao.updateXMPPMessageState(messageAccount, messageState);
		dao.close();

	}
}
