package com.zyxb.qqxmpp.db3.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.XMPPMessage;
import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3Message;
import com.zyxb.qqxmpp.bean3.po.DB3SystemGroup;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.dao.impl.AbstractDB3BaseDao;

public abstract class DB3MessageDAO extends AbstractDB3BaseDao<DB3Message> {
	protected List<OnMessageChangeListener> listeners;
	// protected boolean isMessageListening = false;
	protected Map<OnMessageChangeListener, Integer> states;

	public DB3MessageDAO(Context context) {
		super(context);

		listeners = new ArrayList<OnMessageChangeListener>();
		states = new HashMap<OnMessageChangeListener, Integer>();
	}

	public abstract List<DB3Message> findNewest(DB3User user);

	public abstract List<DB3Message> findSystemMessage(DB3User user);

	public abstract List<DB3Message> findContact(DB3User current,
			DB3User contact);

	public abstract List<DB3Message> findContact(String currentAccount,
			String contactAccount);

	public abstract List<DB3Message> findGroup(DB3User current, DB3Group group);

	public abstract List<DB3Message> findGroup(String userAccount,
			String groupAccount);

	public abstract List<DB3Message> findSystemGroup(DB3User current,
			DB3SystemGroup group);

	public abstract List<DB3Message> findSystemMessage(String userAccount,
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

	public abstract int getUnreadedCount(DB3Message message);

	public abstract int getUnreadedCount(String userAccount);

	public abstract int getUnreadedCount(String attr, String value);

	public abstract void updateReadedMessage(String fromAccount,
			String toAccount, int type);

	public abstract void add(XMPPMessage message);

	public abstract List<XMPPMessage> findOffLineMessages(String userAccount);

	public abstract void updateXMPPMessageState(String messageAccount,
			int messageState);

}
