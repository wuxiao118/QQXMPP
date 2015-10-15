package com.zyxb.qqxmpp.bean3.po;

import java.util.List;

/**
 * 好友分组
 * @author 吴小雄
 *
 */
public class DB3FriendGroup {
	private int id;
	private String account;
	private DB3User user;
	private String name;
	private int position;

	//分组中好友列表
	private List<DB3FriendGroupMapping> userMappings;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public List<DB3FriendGroupMapping> getUserMappings() {
		return userMappings;
	}

	public void setUserMappings(List<DB3FriendGroupMapping> userMappings) {
		this.userMappings = userMappings;
	}

}
