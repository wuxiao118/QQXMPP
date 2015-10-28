package com.zyxb.qqxmpp.db.dao;

import android.content.Context;

import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBMessage;
import com.zyxb.qqxmpp.bean.po.DBSystemGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DBMessageDAO extends AbstractDBBaseDao<DBMessage> {
	protected List<OnMessageChangeListener> listeners;
	// protected boolean isMessageListening = false;
	protected Map<OnMessageChangeListener, Integer> states;

	public DBMessageDAO(Context context) {
		super(context);

		listeners = new ArrayList<OnMessageChangeListener>();
		states = new HashMap<OnMessageChangeListener, Integer>();
	}

	public abstract List<DBMessage> findNewest(DBUser user);

	public abstract List<DBMessage> findSystemMessage(DBUser user);

	public abstract List<DBMessage> findContact(DBUser current,
			DBUser contact);

	public abstract List<DBMessage> findContact(String currentAccount,
			String contactAccount);

	public abstract List<DBMessage> findGroup(DBUser current, DBGroup group);

	public abstract List<DBMessage> findGroup(String userAccount,
			String groupAccount);

	public abstract List<DBMessage> findSystemGroup(DBUser current,
			DBSystemGroup group);

	public abstract List<DBMessage> findSystemMessage(String userAccount,
			String sysAccount);

	public interface OnMessageChangeListener {
		void onPreMessageChange(int type);

		void onMessageChanged(int type);

		void onError(int type);
	}

	public void setOnMessageChangeListener(OnMessageChangeListener listener) {
		// this.messageListener = listener;
		// this.isMessageListening = true;
		listeners.add(listener);
		states.put(listener, STATE_NO_NEW_MESSAGE);
	}

	public void removeOnMessageChangeListener(OnMessageChangeListener listener) {
		// this.messageListener = null;
		// this.isMessageListening = false;
		listeners.remove(listener);
		states.remove(listener);
	}

	protected void notifyDataChanged(int type) {
		for (OnMessageChangeListener listener : listeners) {
			states.put(listener, STATE_NEW_MESSAGE);
			listener.onMessageChanged(type);
		}
	}

	protected void notifyPreChange(int type) {
		for (OnMessageChangeListener listener : listeners) {
			// states.put(listener, STATE_NEW_MESSAGE);
			listener.onPreMessageChange(type);
		}
	}

	protected void notifyError(int type) {
		for (OnMessageChangeListener listener : listeners) {
			states.put(listener, STATE_ERROR);
			listener.onError(type);
		}
	}

	public abstract int getUnreadedCount(DBMessage message);

	public abstract int getUnreadedCount(String userAccount);

	public abstract int getUnreadedCount(String attr, String value);

	public abstract void updateReadedMessage(String fromAccount,
			String toAccount, int type);

	public abstract String add(XMPPMessage message);

	public abstract List<XMPPMessage> findOffLineMessages(String userAccount);

	public abstract void updateXMPPMessageState(String messageAccount,
			int messageState);

	//public abstract void changeXMPPMessageState(String account,int messageState);

}
