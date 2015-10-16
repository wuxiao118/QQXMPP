package com.zyxb.qqxmpp.bean;

public class Information {
	private String account;
	private int type;
	private String name;
	private String comments;
	private String icon;
	private int groupTitle;
	private String renew;
	private int state;
	private int channel;
	private int level;
	
	public static final int TYPE_LOGIN_USER = 0;
	public static final int TYPE_CONTACT_USER = 1;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(int groupTitle) {
		this.groupTitle = groupTitle;
	}

	public String getRenew() {
		return renew;
	}

	public void setRenew(String renew) {
		this.renew = renew;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

}
