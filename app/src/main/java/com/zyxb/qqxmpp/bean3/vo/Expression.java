package com.zyxb.qqxmpp.bean3.vo;

public class Expression {
	private String path;
	private String name;

	public Expression(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public Expression() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
