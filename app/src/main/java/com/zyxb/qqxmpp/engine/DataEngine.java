package com.zyxb.qqxmpp.engine;

import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.Contact;
import com.zyxb.qqxmpp.bean3.FriendGroupInfo;
import com.zyxb.qqxmpp.bean3.Information;
import com.zyxb.qqxmpp.bean3.XMPPMessage;
import com.zyxb.qqxmpp.bean3.MessageInfo;
import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.bean3.vo.GroupInfo;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO.OnMessageChangeListener;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO.OnUserChangeListener;
import com.zyxb.qqxmpp.util.Logger;

/**
 * 读取数据库数据,完成业务处理,将结果返回给界面
 *
 * @author 吴小雄
 *
 */
public class DataEngine {
	private static final String TAG = "DataEngine";
	private Context context;
	private DB3User user;
	private DB3UserEngine userEngine;

	// private DB3GroupEngine groupEngine;

	public DataEngine(Context context, DB3User user) {
		this.context = context;
		this.user = user;

		userEngine = new DB3UserEngine(context);
		// groupEngine = new DB3GroupEngine(context);
	}

	public DataEngine(Context context) {
		this.context = context;
		userEngine = new DB3UserEngine(context);
	}

	public void setUser(DB3User user) {
		this.user = user;
	}

	/**
	 * 最新消息
	 *
	 * @param listener
	 * @return
	 */
	public List<MessageInfo> getNewest(OnMessageChangeListener listener) {

		return DB3EngineFactory.getDB3MessageEngine(context).getNewest(user);
	}

	/**
	 * 好友列表
	 *
	 * @param listener
	 * @return
	 */
	public List<FriendGroupInfo> getFriends(OnUserChangeListener listener) {
		return DB3EngineFactory.getDB3FriendGroupEngine(context).getFriends(
				user);
	}

	/**
	 * 群聊
	 *
	 * @param groupAccount
	 * @param listener
	 * @return
	 */
	public List<MessageInfo> getGroupMessages(String groupAccount,
											  OnMessageChangeListener listener) {

		return DB3EngineFactory.getDB3MessageEngine(context).getGroupMessage(
				user.getAccount(), groupAccount, listener);
	}

	/**
	 * 好友聊天
	 *
	 * @param contact
	 * @param listener
	 * @return
	 */
	public List<MessageInfo> getContactMessages(String contactAccount,
												OnMessageChangeListener listener) {
		DB3MessageEngine engine = DB3EngineFactory.getDB3MessageEngine(context);
		return engine.getContactMessage(user.getAccount(), contactAccount,
				listener);
	}

	/**
	 * 系统信息
	 *
	 * @param sysAccount
	 * @param listener
	 * @return
	 */
	public List<MessageInfo> getSystemMessages(String sysAccount,
											   OnMessageChangeListener listener) {

		return DB3EngineFactory.getDB3MessageEngine(context).getSystemMessage(
				user.getAccount(), sysAccount, listener);
	}

	/**
	 * 群列表
	 *
	 * @return
	 */
	public List<GroupInfo> getGroups() {

		return DB3EngineFactory.getDB3GroupEngine(context).getGroups(user);
	}

	/**
	 * 群用户列表
	 *
	 * @return
	 */
	public List<Information> getGroupFriends(String groupAccount) {

		return DB3EngineFactory.getDB3GroupEngine(context).getFriends(
				groupAccount);
	}

	/**
	 * 获取好友信息
	 *
	 * @param account
	 * @return
	 */
	public DB3User getUserInfo(String account) {
		return DB3EngineFactory.getDB3UserEngine(context).getUser(account);

	}

	/**
	 * 获取群信息
	 *
	 * @param account
	 * @return
	 */
	public DB3Group getGroupInfo(String account) {

		return DB3EngineFactory.getDB3GroupEngine(context).getGroup(account);
	}

	/**
	 * 登陆
	 *
	 * @param username
	 * @param pwd
	 * @return
	 */
	public DB3User login(String username, String pwd) {
		// DB3UserDAO dao = DAOFactory.getDB3UserDAO(context);
		// DB3User u = dao.login(username, pwd);
		// dao.close();

		return userEngine.login(username, pwd);
	}

	public int login() {

		return 0;
	}

	/**
	 * 登出
	 */
	public void logout() {

	}

	/**
	 * 更新已读信息
	 *
	 * @param account
	 * @param account2
	 */
	public void setReadedMessage(String fromAccount, String toAccount, int type) {
		DB3MessageEngine engine = DB3EngineFactory.getDB3MessageEngine(context);
		engine.updateReadedMessage(fromAccount, toAccount, type);
	}

	/**
	 * 查找用户未读信息数目
	 *
	 * @param userAccount
	 * @return
	 */
	public int getUnReadedMessage(String userAccount) {
		return DB3EngineFactory.getDB3MessageEngine(context)
				.getUnReadedMessages(userAccount);
	}

	/**
	 * 指定属性查找用户，返回未读信息数目
	 *
	 * @param attr
	 * @param value
	 * @return
	 */
	public int getUnReadedMessage(String attr, String value) {
		return DB3EngineFactory.getDB3MessageEngine(context)
				.getUnReadedMessages(attr, value);
	}

	/**
	 * 查找用户在群里面的备注
	 *
	 * @param groupAccount
	 * @param userAccount
	 * @return
	 */
	public String getGroupRemark(String groupAccount, String userAccount) {

		return DB3EngineFactory.getDB3GroupEngine(context).getGroupRemark(
				groupAccount, userAccount);
	}

	/**
	 * 查找群人数
	 *
	 * @param account
	 * @return
	 */
	public int getGroupNum(String groupAccount) {

		return DB3EngineFactory.getDB3GroupEngine(context).getGroupNum(
				groupAccount);
	}

	/**
	 * 当前好友信息(包括remark,channel等信息) 不包含自己
	 *
	 * @param contactAccount
	 * @return
	 */
	public Contact getUserFriend(String contactAccount) {
		return DB3EngineFactory.getDB3FriendGroupEngine(context).getFriend(
				user.getAccount(), contactAccount);
	}

	/**
	 * 群好友信息 包含自己
	 *
	 * @param contactAccount
	 * @return
	 */
	public Contact getGroupFriend(String groupAccount, String contactAccount) {
		return DB3EngineFactory.getDB3GroupEngine(context).getFriend(
				groupAccount, contactAccount);
	}

	/**
	 * 好友备注
	 *
	 * @param account
	 * @param contactAccount
	 * @return
	 */
	public String getRemark(String userAccount, String contactAccount) {
		return DB3EngineFactory.getDB3FriendGroupEngine(context).getRemark(
				userAccount, contactAccount);
	}

	/**
	 * 判断是否为自己好友
	 *
	 * @param contactAccount
	 * @return
	 */
	public boolean isMyFriend(String contactAccount) {
		return DB3EngineFactory.getDB3FriendGroupEngine(context).checkFriend(
				user.getAccount(), contactAccount);
	}

	/**
	 * 添加消息
	 *
	 * @param message
	 */
	public void addMessage(XMPPMessage message) {
		// message.setTo(user.getAccount());
		DB3EngineFactory.getDB3MessageEngine(context).add(message);
	}

	/**
	 * 添加收到的xmpp新消息
	 *
	 * @param message
	 */
	public void addNewXMPPMessge(XMPPMessage message) {
		message.setTo(user.getAccount());
		DB3EngineFactory.getDB3MessageEngine(context)
				.addNewXMPPMessage(message);
	}

	/**
	 * 删除jid用户
	 *
	 * @param jid
	 */
	public void deleteXMPPUser(String jid) {
		DB3EngineFactory.getDB3FriendGroupEngine(context).delete(
				user.getAccount(), jid);
	}

	/**
	 * 添加xmpp好友
	 *
	 * @param contact
	 */
	public void addXMPPUser(XMPPUser contact) {
		Logger.d(
				TAG,
				"add xmpp user :" + user.getAccount() + ",contact:"
						+ contact.getJid() + "," + contact.getGroup() + ","
						+ contact.getStatusMode() + ","
						+ contact.getStatusMessage());
		DB3EngineFactory.getDB3FriendGroupEngine(context).add(
				user.getAccount(), contact);
	}

	/**
	 * 更新xmpp好友
	 *
	 * @param contact
	 */
	public void updateXMPPUser(XMPPUser contact) {
		DB3EngineFactory.getDB3FriendGroupEngine(context).update(
				user.getAccount(), contact);
	}

	/**
	 * 获取登陆xmpp用户,不存在则添加,并添加默认分组[我的设备,我的好友]
	 *
	 * @param ur
	 */
	public DB3User getXMPPUser(XMPPUser ur) {
		// return DB3EngineFactory.getDB3UserEngine(context).find(ur);
		return DB3EngineFactory.getDB3FriendGroupEngine(context).find(ur);
	}

	public DB3User findXMPPUserByName(String loginJid) {
		return DB3EngineFactory.getDB3UserEngine(context).find(loginJid);
	}

	public List<XMPPMessage> getOffLineMessages() {
		return DB3EngineFactory.getDB3MessageEngine(context)
				.getOffLineMessages(user.getAccount());
	}

	public void updateXMPPMessageState(String messageAccount, int messageState) {
		DB3EngineFactory.getDB3MessageEngine(context)
				.updateXMPPMessageState(messageAccount, messageState);
	}
}
