package com.zyxb.qqxmpp.db.dao;

import android.content.Context;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBFriendGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

import java.util.List;

public abstract class DBFriendGroupDAO extends
		AbstractDBBaseDao<DBFriendGroup> {

	public DBFriendGroupDAO(Context context) {
		super(context);
	}

	public abstract List<DBFriendGroup> findByUser(DBUser user);

	public abstract DBFriendGroup find(String account, String group) ;

	public abstract DBUser find(XMPPUser ur);

	public abstract String getMaxAccount() ;

	public abstract void add(String userAccount,String friendGroupName);
}
