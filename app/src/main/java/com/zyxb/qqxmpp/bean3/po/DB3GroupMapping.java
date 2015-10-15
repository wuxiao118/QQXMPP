package com.zyxb.qqxmpp.bean3.po;

/**
 * 群中好友
 * @author 吴小雄
 *
 */
public class DB3GroupMapping {
	private int id;
	private String account;
	private int loginState;
	private int loginChannel;
	private long interTime;
	private int groupTitle;
	private int msgSetting;
	private int level;
	private String remark;
	private DB3User user;
	private DB3Group group;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getLoginState() {
		return loginState;
	}

	public void setLoginState(int loginState) {
		this.loginState = loginState;
	}

	public int getLoginChannel() {
		return loginChannel;
	}

	public void setLoginChannel(int loginChannel) {
		this.loginChannel = loginChannel;
	}

	public long getInterTime() {
		return interTime;
	}

	public void setInterTime(long interTime) {
		this.interTime = interTime;
	}

	public int getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(int groupTitle) {
		this.groupTitle = groupTitle;
	}

	public int getMsgSetting() {
		return msgSetting;
	}

	public void setMsgSetting(int msgSetting) {
		this.msgSetting = msgSetting;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public DB3User getUser() {
		return user;
	}

	public void setUser(DB3User user) {
		this.user = user;
	}

	public DB3Group getGroup() {
		return group;
	}

	public void setGroup(DB3Group group) {
		this.group = group;
	}

}
