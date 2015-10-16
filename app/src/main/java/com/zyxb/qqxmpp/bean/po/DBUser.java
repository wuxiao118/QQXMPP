package com.zyxb.qqxmpp.bean.po;

import java.util.List;

/**
 * 用户(好友)
 *
 * @author 吴小雄
 *
 */
public class DBUser {
	private int id;
	private String account;
	private String nickname;
	private String pwd;
	private String icon;
	private int age;
	private String gender;
	private String email;
	private String location;
	private int loginDays;
	private int level;
	private long registerTime;
	private int exportDays;
	private String renew;
	private int state;
	private long birthday;
	private int constellation;
	private String occupation;// 职业
	private String company;
	private String school;
	private String hometown;
	private String desp;
	private String personalitySignature;
	private String webSpace;

	// 好友分组列表
	private List<DBFriendGroup> friendGroups;
	// 群列表
	private List<DBGroup> groups;
	// 系统消息组列表
	private List<DBSystemGroup> sysgroups;
	// 最新消息
	private List<DBMessage> messages;

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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getLoginDays() {
		return loginDays;
	}

	public void setLoginDays(int loginDays) {
		this.loginDays = loginDays;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public int getExportDays() {
		return exportDays;
	}

	public void setExportDays(int exportDays) {
		this.exportDays = exportDays;
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

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public int getConstellation() {
		return constellation;
	}

	public void setConstellation(int constellation) {
		this.constellation = constellation;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getPersonalitySignature() {
		return personalitySignature;
	}

	public void setPersonalitySignature(String personalitySignature) {
		this.personalitySignature = personalitySignature;
	}

	public String getWebSpace() {
		return webSpace;
	}

	public void setWebSpace(String webSpace) {
		this.webSpace = webSpace;
	}

	public List<DBFriendGroup> getFriendGroups() {
		return friendGroups;
	}

	public void setFriendGroups(List<DBFriendGroup> friendGroups) {
		this.friendGroups = friendGroups;
	}

	public List<DBGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<DBGroup> groups) {
		this.groups = groups;
	}

	public List<DBSystemGroup> getSysgroups() {
		return sysgroups;
	}

	public void setSysgroups(List<DBSystemGroup> sysgroups) {
		this.sysgroups = sysgroups;
	}

	public List<DBMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<DBMessage> messages) {
		this.messages = messages;
	}

}
