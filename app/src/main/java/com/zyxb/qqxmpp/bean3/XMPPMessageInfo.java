package com.zyxb.qqxmpp.bean3;

/**
 * 用户message queue传递消息
 *
 * @author Administrator
 *
 */
public class XMPPMessageInfo {
	private int id;
	private long createTime;

	// message
	private String account;
	private String from;
	private String to;
	private String msg;
	private int msgType;
	private int state;

	// contact
	private String jid;
	private String nickname;
	private String friendGroup;
	private int statusMode;
	private String statusMessage;

	// 类型
	public static final int USER_ADD = 0;
	public static final int USER_DELETE = 1;
	public static final int USER_UPDATE = 2;
	public static final int MESSAGE_ADD = 3;
	public static final int MESSAGE_DELETE = 4;
	public static final int MESSAGE_UPDATE = 5;
	private int messageState;

	public int getMessageState() {
		return messageState;
	}

	public void setMessageState(int messageState) {
		this.messageState = messageState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFriendGroup() {
		return friendGroup;
	}

	public void setFriendGroup(String friendGroup) {
		this.friendGroup = friendGroup;
	}

	public int getStatusMode() {
		return statusMode;
	}

	public void setStatusMode(int statusMode) {
		this.statusMode = statusMode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

}
