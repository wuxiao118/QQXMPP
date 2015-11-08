package com.zyxb.qqxmpp.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class XMPPMessage implements Parcelable {
	private int id;
	private String account;
	private String from;
	private String to;
	private String msg;
	private long createTime;
	private int msgType;
	private int state;
	private String data1;
	private String data2;

	public static final Parcelable.Creator<XMPPMessage> CREATOR = new Creator<XMPPMessage>() {

		@Override
		public XMPPMessage[] newArray(int size) {
			return new XMPPMessage[size];
		}

		@Override
		public XMPPMessage createFromParcel(Parcel source) {
			XMPPMessage message = new XMPPMessage();
			message.id = source.readInt();
			message.account = source.readString();
			message.from = source.readString();
			message.to = source.readString();
			message.msg = source.readString();
			message.createTime = source.readLong();
			message.msgType = source.readInt();
			message.state = source.readInt();
			message.data1 = source.readString();
			message.data2 = source.readString();

			return message;
		}
	};

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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(account);
		dest.writeString(from);
		dest.writeString(to);
		dest.writeString(msg);
		dest.writeLong(createTime);
		dest.writeInt(msgType);
		dest.writeInt(state);
		dest.writeString(data1);
		dest.writeString(data2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMPPMessage other = (XMPPMessage) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		return true;
	}

}
