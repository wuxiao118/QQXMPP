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
	 * @param listener 数据改变监听器
	 * @return 最新消息列表
	 */
	public List<MessageInfo> getNewest(OnMessageChangeListener listener) {

		return DBEngineFactory.getDBMessageEngine(mContext).getNewest(mUser);
	}

	/**
	 * 好友列表
	 *
	 * @param listener 数据改变监听器
	 * @return 所有分组信息
	 */
	public List<FriendGroupInfo> getFriends(OnUserChangeListener listener) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).getFriends(
				mUser);
	}

	/**
	 * 用户好友分组,不需要,可以服用getFriends
	 *
	 * @return 分组信息列表
	 */
//	public List<GroupInfo> getUserFriendGroups(){
//
//		return null;
//	}

	/**
	 *
	 * @param friendGroupName 分组名称
	 */
	public void addNewFriendGroup(String friendGroupName){
		DBEngineFactory.getDBFriendGroupEngine(mContext).addNewFriendGroup(mUser.getAccount(),friendGroupName);
	}

	/**
	 * 群聊
	 *
	 * @param groupAccount 群账号
	 * @param listener 改变监听器,未使用,使用receiver代替
	 * @return 群消息列表
	 */
	public List<MessageInfo> getGroupMessages(String groupAccount,
											  OnMessageChangeListener listener) {

		return DBEngineFactory.getDBMessageEngine(mContext).getGroupMessage(
				mUser.getAccount(), groupAccount, listener);
	}

	/**
	 * 好友聊天
	 *
	 * @param contactAccount 好友账号
	 * @param listener 改变监听器,未使用,使用receiver代替
	 * @return 好友聊天消息列表
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
	 * @param sysAccount 系统消息账号
	 * @param listener 改变监听器,未使用,使用receiver代替
	 * @return 系统消息列表
	 */
	public List<MessageInfo> getSystemMessages(String sysAccount,
											   OnMessageChangeListener listener) {

		return DBEngineFactory.getDBMessageEngine(mContext).getSystemMessage(
				mUser.getAccount(), sysAccount, listener);
	}

	/**
	 * 群列表
	 *
	 * @return 群类表
	 */
	public List<GroupInfo> getGroups() {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroups(mUser);
	}

	/**
	 * 群用户列表
	 *
	 * @return 群用户列表
	 */
	public List<Information> getGroupFriends(String groupAccount) {

		return DBEngineFactory.getDBGroupEngine(mContext).getFriends(
				groupAccount);
	}

	/**
	 * 获取好友信息
	 *
	 * @param account 好友账号
	 * @return 好友信息
	 */
	public DBUser getUserInfo(String account) {
		return DBEngineFactory.getDBUserEngine(mContext).getUser(account);

	}

	/**
	 * 获取群信息
	 *
	 * @param account 群账号
	 * @return 群信息
	 */
	public DBGroup getGroupInfo(String account) {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroup(account);
	}

	/**
	 * 登陆
	 *
	 * @param username 用户名
	 * @param pwd 密码
	 * @return 登陆用户
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
	 * @param fromAccount 消息放松方
	 * @param toAccount 消息接收方
	 * @param type 消息状态
	 */
	public void setReadedMessage(String fromAccount, String toAccount, int type) {
		DBMessageEngine engine = DBEngineFactory.getDBMessageEngine(mContext);
		engine.updateReadedMessage(fromAccount, toAccount, type);
	}

	/**
	 * 查找用户未读信息数目
	 *
	 * @param userAccount 要查找用户
	 * @return 该用户未读消息数
	 */
	public int getUnReadedMessage(String userAccount) {
		return DBEngineFactory.getDBMessageEngine(mContext)
				.getUnReadedMessages(userAccount);
	}

	public int getUnReadedMessage(){
		return DBEngineFactory.getDBMessageEngine(mContext).getUnReadedMessages(mUser.getAccount());
	}

	/**
	 * 指定属性查找用户，返回未读信息数目
	 *
	 * @param attr 用户属性名
	 * @param value 用户属性名对应的属性值
	 * @return 该用户未读消息数
	 */
	public int getUnReadedMessage(String attr, String value) {
		return DBEngineFactory.getDBMessageEngine(mContext)
				.getUnReadedMessages(attr, value);
	}

	/**
	 * 查找用户在群里面的备注
	 *
	 * @param groupAccount 群账号
	 * @param userAccount 群好友账号
	 * @return 群好友在群中的备注
	 */
	public String getGroupRemark(String groupAccount, String userAccount) {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroupRemark(
				groupAccount, userAccount);
	}

	/**
	 * 查找群人数
	 *
	 * @param groupAccount 群账号
	 * @return 群总人数
	 */
	public int getGroupNum(String groupAccount) {

		return DBEngineFactory.getDBGroupEngine(mContext).getGroupNum(
				groupAccount);
	}

	/**
	 * 当前好友信息(包括remark,channel等信息) 不包含自己
	 *
	 * @param contactAccount 好友账号
	 * @return 好友信息
	 */
	public Contact getUserFriend(String contactAccount) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).getFriend(
				mUser.getAccount(), contactAccount);
	}

	/**
	 * 群好友信息 包含自己
	 *
	 * @param contactAccount 群好友账号
	 * @param groupAccount 群账号
	 * @return 群好友信息
	 */
	public Contact getGroupFriend(String groupAccount, String contactAccount) {
		return DBEngineFactory.getDBGroupEngine(mContext).getFriend(
				groupAccount, contactAccount);
	}

	/**
	 * 好友备注
	 *
	 * @param userAccount 登陆用户账号
	 * @param contactAccount 好友账号
	 * @return 好友备注
	 */
	public String getRemark(String userAccount, String contactAccount) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).getRemark(
				userAccount, contactAccount);
	}

	/**
	 * 判断是否为自己好友
	 *
	 * @param contactAccount 好友账号
	 * @return 是否为自己好友
	 */
	public boolean isMyFriend(String contactAccount) {
		return DBEngineFactory.getDBFriendGroupEngine(mContext).checkFriend(
				mUser.getAccount(), contactAccount);
	}

	/**
	 * 添加消息
	 *
	 * @param message xmpp消息
	 * @return 添加message的id
	 */
	public String addMessage(XMPPMessage message) {
		// message.setTo(mUser.getAccount());
		return DBEngineFactory.getDBMessageEngine(mContext).add(message);
	}

	/**
	 * 添加收到的xmpp新消息
	 *
	 * @param message xmpp消息
	 */
	public void addNewXMPPMessge(XMPPMessage message) {
		message.setTo(mUser.getAccount());
		DBEngineFactory.getDBMessageEngine(mContext)
				.addNewXMPPMessage(message);
	}

	/**
	 * 删除jid用户
	 *
	 * @param jid xmpp好友jid
	 */
	public void deleteXMPPUser(String jid) {
		DBEngineFactory.getDBFriendGroupEngine(mContext).delete(
				mUser.getAccount(), jid);
	}

	/**
	 * 添加xmpp好友
	 *
	 * @param contact xmpp好友
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
	 * @param contact xmpp好友
	 */
	public void updateXMPPUser(XMPPUser contact) {
		DBEngineFactory.getDBFriendGroupEngine(mContext).update(
				mUser.getAccount(), contact);
	}

	//更新好友状态
	public void updateXMPPFriendState(String jid,int status){
		DBEngineFactory.getDBFriendGroupEngine(mContext).updateXMPPFriendState(mUser.getAccount(),jid,status);
		//DBEngineFactory.getDBFriendGroupEngine(mContext).updateXMPPFriendState(jid,status);
	}

	/**
	 * 获取登陆xmpp用户,不存在则添加,并添加默认分组[我的设备,我的好友]
	 *
	 * @param ur xmpp好友
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
