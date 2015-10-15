package com.zyxb.qqxmpp.bean3.vo;

/**
 * group列表信息(TITLE分组信息,ITEM群信息)
 *
 * @author 吴小雄
 *
 */
public class GroupInfo {
	private String account;
	private String icon;
	private String name;
	private int level;
	private int type;

	public static final int GROUP_TYPE_TITLE = 0;
	public static final int GROUP_TYPE_ITEM = 1;

	//public static final int GROUP_LEVEL_CREATOR = DB3Columns.GROUP_LEVEL_CREATOR;

	public static final String[] GROUP_TITLES = { "我加入的群", "我加入的群", "我管理的群",
			"我创建的群" };

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (type == GROUP_TYPE_TITLE) {
			sb.append(name + "," + level);
		} else {
			sb.append(name);
		}

		return sb.toString();
	}

}
