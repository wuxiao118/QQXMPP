package com.zyxb.qqxmpp.bean;

import java.util.List;

public class FriendGroupInfo {
	public static final int TYPE_USER = 0;
	public static final int TYPE_GROUP = 1;
	public static final int TYPE_SYS = 2;
	public static final String[] TYPES = {"用户","群","系统"};
	
	private String account;
	private String name;
	private List<Information> friends;
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Information> getFriends() {
		return friends;
	}

	public void setFriends(List<Information> friends) {
		this.friends = friends;
	}

}
