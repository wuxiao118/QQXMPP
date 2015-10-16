package com.zyxb.qqxmpp.bean;

public class XMPPUser {
	private int id;
	private String jid;
	private String nickname;
	private int statusMode;
	// ״̬
	private String statusMessage;
	private String group;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getGroup() {
		return group;
	}

	public void setGroup(String string) {
		this.group = string;
	}

}
