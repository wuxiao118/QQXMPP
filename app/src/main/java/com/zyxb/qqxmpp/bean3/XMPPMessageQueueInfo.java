package com.zyxb.qqxmpp.bean3;

import java.util.Collection;

import org.jivesoftware.smack.packet.Presence;

import android.os.Parcel;
import android.os.Parcelable;

public class XMPPMessageQueueInfo implements Parcelable {
	private int type;
	private int classType;
	private Collection<String> rosters;
	private Presence presence;
	private XMPPMessage message;
	private String account;

	public static final Parcelable.Creator<XMPPMessageQueueInfo> CREATOR = new Creator<XMPPMessageQueueInfo>() {

		@Override
		public XMPPMessageQueueInfo[] newArray(int size) {
			return null;
		}

		@Override
		public XMPPMessageQueueInfo createFromParcel(Parcel source) {
			return null;
		}
	};

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getClassType() {
		return classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public Collection<String> getRosters() {
		return rosters;
	}

	public void setRosters(Collection<String> rosters) {
		this.rosters = rosters;
	}

	public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}

	public XMPPMessage getMessage() {
		return message;
	}

	public void setMessage(XMPPMessage message) {
		this.message = message;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

}
