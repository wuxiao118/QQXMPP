package com.zyxb.qqxmpp.bean.po;

import java.util.List;

/**
 * 好友分组
 * @author 吴小雄
 *
 */
public class DBFriendGroup {
	private int id;
	private String account;
	private DBUser user;
	private String name;
	private int position;

	//分组中好友列表
	private List<DBFriendGroupMapping> userMappings;

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

	public List<DBFriendGroupMapping> getUserMappings() {
		return userMappings;
	}

	public void setUserMappings(List<DBFriendGroupMapping> userMappings) {
		this.userMappings = userMappings;
	}

}
