package com.zyxb.qqxmpp.bean.po;

/**
 * 分组中的好友及其相关信息
 * @author 吴小雄
 *
 */
public class DBFriendGroupMapping {
	private int id;
	private String account;
	private DBUser user;
	private DBFriendGroup friendGroup;
	private int loginState;
	private int loginChannel;
	private int position;
	private String remark;

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

	public DBUser getUser() {
		return user;
	}

	public void setUser(DBUser user) {
		this.user = user;
	}

	public DBFriendGroup getFriendGroup() {
		return friendGroup;
	}

	public void setFriendGroup(DBFriendGroup friendGroup) {
		this.friendGroup = friendGroup;
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
