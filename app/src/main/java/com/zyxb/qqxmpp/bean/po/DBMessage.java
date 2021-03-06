package com.zyxb.qqxmpp.bean.po;

import com.zyxb.qqxmpp.db.DBColumns;

/**
 * 信息
 *
 * @author 吴小雄
 *
 */
public class DBMessage implements Comparable<DBMessage> {
	private int id;
	private String account;
	private DBUser from;
	private DBUser to;
	private DBSystemGroup fromGroup;
	private DBGroup toGroup;
	private String msg;
	private long createTime;
	private int type;
	private int state;
	private String data1;
	private String data2;
	private String data3;
	private String data4;
	private String data5;
	private String data6;
	private String data7;

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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public DBUser getFrom() {
		return from;
	}

	public void setFrom(DBUser from) {
		this.from = from;
	}

	public DBUser getTo() {
		return to;
	}

	public void setTo(DBUser to) {
		this.to = to;
	}

	public DBSystemGroup getFromGroup() {
		return fromGroup;
	}

	public void setFromGroup(DBSystemGroup fromGroup) {
		this.fromGroup = fromGroup;
	}

	public DBGroup getToGroup() {
		return toGroup;
	}

	public void setToGroup(DBGroup toGroup) {
		this.toGroup = toGroup;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getData5() {
		return data5;
	}

	public void setData5(String data5) {
		this.data5 = data5;
	}

	public String getData6() {
		return data6;
	}

	public void setData6(String data6) {
		this.data6 = data6;
	}

	public String getData7() {
		return data7;
	}

	public void setData7(String data7) {
		this.data7 = data7;
	}

	@Override
	public int compareTo(DBMessage another) {

		return createTime > another.createTime ? 1
				: (createTime == another.createTime ? 0 : -1);
	}

	@Override
	public String toString() {
		String fromAccount = "";
		String toAccount = "";
		switch (type) {
			case DBColumns.MESSAGE_TYPE_CONTACT:
				fromAccount = from.getAccount();
				toAccount = to.getAccount();
				break;
			case DBColumns.MESSAGE_TYPE_GROUP:
				fromAccount = from.getAccount();
				toAccount = toGroup.getAccount();
				break;
			case DBColumns.MESSAGE_TYPE_SYS:
				fromAccount = fromGroup.getAccount();
				toAccount = to.getAccount();
				break;
		}

		return "[" + createTime + ":" + msg + ":"
				+ DBColumns.MESSAGE_STATES[state] + "  " + fromAccount
				+ "--->" + toAccount + "]";
	}

}
