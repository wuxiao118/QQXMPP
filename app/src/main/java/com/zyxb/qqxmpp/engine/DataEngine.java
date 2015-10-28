package com.zyxb.qqxmpp.engine;

import android.content.Context;

import com.zyxb.qqxmpp.bean.Contact;
import com.zyxb.qqxmpp.bean.FriendGroupInfo;
import com.zyxb.qqxmpp.bean.Information;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.bean.vo.GroupInfo;
import com.zyxb.qqxmpp.db.dao.DBMessageDAO.OnMessageChangeListener;
import com.zyxb.qqxmpp.db.dao.DBUserDAO.OnUserChangeListener;
import com.zyxb.qqxmpp.util.Logger;

import java.util.List;

/**
 * 读取数据库数据,完成业务处理,将结果返回给界面
 *
 * @author 吴小雄
 *
 */
public class DataEngine {
	private static final String TAG = "DataEngine";
	private Context mContext;
	private DBUser mUser;
	private DBUserEngine mUserEngine;

	// private DBGroupEngine groupEngine;

	public DataEngine(Context context, DBUser user) {
		this.mContext = context;
		this.mUser = user;

		mUserEngine = new DBUserEngine(context);
		// groupEngine = new DBGroupEngine(mContext);
	}

	public DataEngine(Context context) {
		this.mContext = context;
		mUserEngine = new DBUserEngine(context);
	}

	public void setmUser(DBUser mUser) {
		this.mUser = mUser;
	}

	/**
	 * 最新消息
	 *
	 * @param listener
	 * @return
	 */
	public List<MessageInfo> getNewest(OnMessageChangeListener listener) {

		return DBEngineFactory.getDBMessageEngine(mContext).getNewest(mUser);
	}

	/**
	 * 好友列表
	 *
	 * @param listener
	 * @return
	 */
	public List<FriendGroupInfo> getFriends(OnUserChangeListener listener) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).getFriends(
				mUser);
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

		return DBEngineFactory.getDBMessageEngine(mContext).getGroupMessage(
				mUser.getAccount(), groupAccount, listener);
	}

	/**
	 * 好友聊天
	 *
	 * @param contactAccount
	 * @param listener
	 * @return
	 */
	public List<MessageInfo> getContactMessages(String contactAccount,
												OnMessageChangeListener listener) {
		DBMessageEngine engine = DBEngineFactory.getDBMessageEngine(mContext);
		return engine.getContactMessage(mUser.getAccount(), contactAccount,
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

		return DBEngineFactory.getDBMessageEngine(mContext).getSystemMessage(
				mUser.getAccount(), sysAccount, listener);
	}

	/**
	 * 群列表
	 *
	 * @return
	 */
	public List<GroupInfo> getGroups() {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroups(mUser);
	}

	/**
	 * 群用户列表
	 *
	 * @return
	 */
	public List<Information> getGroupFriends(String groupAccount) {

		return DBEngineFactory.getDBGroupEngine(mContext).getFriends(
				groupAccount);
	}

	/**
	 * 获取好友信息
	 *
	 * @param account
	 * @return
	 */
	public DBUser getUserInfo(String account) {
		return DBEngineFactory.getDBUserEngine(mContext).getUser(account);

	}

	/**
	 * 获取群信息
	 *
	 * @param account
	 * @return
	 */
	public DBGroup getGroupInfo(String account) {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroup(account);
	}

	/**
	 * 登陆
	 *
	 * @param username
	 * @param pwd
	 * @return
	 */
	public DBUser login(String username, String pwd) {
		// DBUserDAO dao = DAOFactory.getDB3UserDAO(mContext);
		// DBUser u = dao.login(username, pwd);
		// dao.close();

		return mUserEngine.login(username, pwd);
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
	 * @param fromAccount
	 * @param toAccount
	 */
	public void setReadedMessage(String fromAccount, String toAccount, int type) {
		DBMessageEngine engine = DBEngineFactory.getDBMessageEngine(mContext);
		engine.updateReadedMessage(fromAccount, toAccount, type);
	}

	/**
	 * 查找用户未读信息数目
	 *
	 * @param userAccount
	 * @return
	 */
	public int getUnReadedMessage(String userAccount) {
		return DBEngineFactory.getDBMessageEngine(mContext)
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
		return DBEngineFactory.getDBMessageEngine(mContext)
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

		return DBEngineFactory.getDBGroupEngine(mContext).getGroupRemark(
				groupAccount, userAccount);
	}

	/**
	 * 查找群人数
	 *
	 * @param groupAccount
	 * @return
	 */
	public int getGroupNum(String groupAccount) {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroupNum(
				groupAccount);
	}

	/**
	 * 当前好友信息(包括remark,channel等信息) 不包含自己
	 *
	 * @param contactAccount
	 * @return
	 */
	public Contact getUserFriend(String contactAccount) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).getFriend(
				mUser.getAccount(), contactAccount);
	}

	/**
	 * 群好友信息 包含自己
	 *
	 * @param contactAccount
	 * @return
	 */
	public Contact getGroupFriend(String groupAccount, String contactAccount) {
		return DBEngineFactory.getDBGroupEngine(mContext).getFriend(
				groupAccount, contactAccount);
	}

	/**
	 * 好友备注
	 *
	 * @param userAccount
	 * @param contactAccount
	 * @return
	 */
	public String getRemark(String userAccount, String contactAccount) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).getRemark(
				userAccount, contactAccount);
	}

	/**
	 * 判断是否为自己好友
	 *
	 * @param contactAccount
	 * @return
	 */
	public boolean isMyFriend(String contactAccount) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).checkFriend(
				mUser.getAccount(), contactAccount);
	}

	/**
	 * 添加消息
	 *
	 * @param message
	 * @return 添加message的id
	 */
	public String addMessage(XMPPMessage message) {
		// message.setTo(mUser.getAccount());
		return DBEngineFactory.getDBMessageEngine(mContext).add(message);
	}

	/**
	 * 添加收到的xmpp新消息
	 *
	 * @param message
	 */
	public void addNewXMPPMessge(XMPPMessage message) {
		message.setTo(mUser.getAccount());
		DBEngineFactory.getDBMessageEngine(mContext)
				.addNewXMPPMessage(message);
	}

	/**
	 * 删除jid用户
	 *
	 * @param jid
	 */
	public void deleteXMPPUser(String jid) {
		DBEngineFactory.getDBFriendGroupEngine(mContext).delete(
				mUser.getAccount(), jid);
	}

	/**
	 * 添加xmpp好友
	 *
	 * @param contact
	 */
	public void addXMPPUser(XMPPUser contact) {
		Logger.d(
				TAG,
				"add xmpp mUser :" + mUser.getAccount() + ",contact:"
						+ contact.getJid() + "," + contact.getGroup() + ","
						+ contact.getStatusMode() + ","
						+ contact.getStatusMessage());
		DBEngineFactory.getDBFriendGroupEngine(mContext).add(
				mUser.getAccount(), contact);
	}

	/**
	 * 更新xmpp好友
	 *
	 * @param contact
	 */
	public void updateXMPPUser(XMPPUser contact) {
		DBEngineFactory.getDBFriendGroupEngine(mContext).update(
				mUser.getAccount(), contact);
	}

	/**
	 * 获取登陆xmpp用户,不存在则添加,并添加默认分组[我的设备,我的好友]
	 *
	 * @param ur
	 */
	public DBUser getXMPPUser(XMPPUser ur) {
		// return DBEngineFactory.getDBUserEngine(mContext).find(ur);
		return DBEngineFactory.getDBFriendGroupEngine(mContext).find(ur);
	}

	public DBUser findXMPPUserByName(String loginJid) {
		return DBEngineFactory.getDBUserEngine(mContext).find(loginJid);
	}

	public List<XMPPMessage> getOffLineMessages() {
		return DBEngineFactory.getDBMessageEngine(mContext)
				.getOffLineMessages(mUser.getAccount());
	}

	public void updateXMPPMessageState(String messageAccount, int messageState) {
		DBEngineFactory.getDBMessageEngine(mContext)
				.updateXMPPMessageState(messageAccount, messageState);
	}

//	public void changeXMPPMessageState(String account,int messageState){
//		//DBEngineFactory.getDBMessageEngine(mContext).changeXMPPMessageState(account, messageState);
//	}
}
