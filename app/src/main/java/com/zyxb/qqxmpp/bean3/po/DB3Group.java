package com.zyxb.qqxmpp.bean3.po;

import java.util.List;

/**
 * 群
 * @author 吴小雄
 *
 */
public class DB3Group {
	private int id;
	private String account;
	private String name;
	private String icon;
	private String desp;
	private long createTime;
	private String classification;

	//群好友列表
	private List<DB3GroupMapping> groupMappings;

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

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public List<DB3GroupMapping> getGroupMappings() {
		return groupMappings;
	}

	public void setGroupMappings(List<DB3GroupMapping> groupMappings) {
		this.groupMappings = groupMappings;
	}

}
