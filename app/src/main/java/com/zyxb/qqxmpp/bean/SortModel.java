package com.zyxb.qqxmpp.bean;

public class SortModel {

	private String name; // 显示的数据
	private String sortLetters; // 显示数据拼音的首字母
	private Information info; //相关好友信息

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public Information getInfo() {
		return info;
	}

	public void setInfo(Information info) {
		this.info = info;
	}

}
