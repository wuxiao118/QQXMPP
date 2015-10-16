package com.zyxb.qqxmpp.bean.po;

/**
 * 群中好友
 * @author 吴小雄
 *
 */
public class DBGroupMapping {
	private int id;
	private String account;
	private int loginState;
	private int loginChannel;
	private long interTime;
	private int groupTitle;
	private int msgSetting;
	private int level;
	private String remark;
	private DBUser user;
	private DBGroup group;

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

	public DBUser getUser() {
		return user;
	}

	public void setUser(DBUser user) {
		this.user = user;
	}

	public DBGroup getGroup() {
		return group;
	}

	public void setGroup(DBGroup group) {
		this.group = group;
	}

}
