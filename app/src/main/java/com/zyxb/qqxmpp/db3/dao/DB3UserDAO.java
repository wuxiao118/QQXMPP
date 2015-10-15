package com.zyxb.qqxmpp.db3.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.dao.impl.AbstractDB3BaseDao;

public abstract class DB3UserDAO extends AbstractDB3BaseDao<DB3User> {
	private List<OnUserChangeListener> listeners;

	public DB3UserDAO(Context context) {
		super(context);

		listeners = new ArrayList<OnUserChangeListener>();
	}

	public abstract DB3User findByName(String name);
	
	//public abstract boolean login(String name,String pwd);
	public abstract DB3User login(String username,String pwd);
	
	public abstract boolean isEmpty();

	public interface OnUserChangeListener {
		public void onChanged();

		public void onError(int type);
	}

	public void setOnUserChangeListener(OnUserChangeListener listener) {
		listeners.add(listener);
	}

	public void removeOnUserChangeListener(OnUserChangeListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyDataChanged(int type){
		for(OnUserChangeListener listener:listeners){
			listener.onChanged();
		}
	}
	
	protected void notifyError(int type){
		for(OnUserChangeListener listener:listeners){
			listener.onError(type);
		}
	}

	public abstract String findMaxAccount() ;

	public abstract DB3User find(XMPPUser ur);

	public abstract String getMaxAccount();
}
