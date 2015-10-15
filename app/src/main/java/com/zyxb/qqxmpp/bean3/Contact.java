package com.zyxb.qqxmpp.bean3;

/**
 * user信息和状态信息
 *
 * @author Administrator
 *
 */
public class Contact {
	// user信息
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

	// 状态信息(群、好友)
	private int loginState;
	private int loginChannel;
	private String remark;

	//好友特有 分组名称
	private String groupName;

	// 群特有
	private int groupTitle;
	private int groupLevel;
	private long interTime;
	private int msgSetting;


	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

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

	public int getLoginState() {
		return loginState;
	}

	public void setLoginState(int loginState) {
		this.loginState = loginState;
	}

	public int getLoginChannel() {
		return loginChannel;
	}

	public void setLoginChannel(int loginChannel) {
		this.loginChannel = loginChannel;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(int groupTitle) {
		this.groupTitle = groupTitle;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	public long getInterTime() {
		return interTime;
	}

	public void setInterTime(long interTime) {
		this.interTime = interTime;
	}

	public int getMsgSetting() {
		return msgSetting;
	}

	public void setMsgSetting(int msgSetting) {
		this.msgSetting = msgSetting;
	}

}
