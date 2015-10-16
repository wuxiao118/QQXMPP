package com.zyxb.qqxmpp.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

public abstract class DBUserDAO extends AbstractDBBaseDao<DBUser> {
	private List<OnUserChangeListener> listeners;

	public DBUserDAO(Context context) {
		super(context);

		listeners = new ArrayList<OnUserChangeListener>();
	}

	public abstract DBUser findByName(String name);
	
	//public abstract boolean login(String name,String pwd);
	public abstract DBUser login(String username,String pwd);
	
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

	public abstract DBUser find(XMPPUser ur);

	public abstract String getMaxAccount();
}
