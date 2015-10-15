package com.zyxb.qqxmpp.bean3.po;

/**
 * 分组中的好友及其相关信息
 * @author 吴小雄
 *
 */
public class DB3FriendGroupMapping {
	private int id;
	private String account;
	private DB3User user;
	private DB3FriendGroup friendGroup;
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

	public DB3User getUser() {
		return user;
	}

	public void setUser(DB3User user) {
		this.user = user;
	}

	public DB3FriendGroup getFriendGroup() {
		return friendGroup;
	}

	public void setFriendGroup(DB3FriendGroup friendGroup) {
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
