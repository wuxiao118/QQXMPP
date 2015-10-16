package com.zyxb.qqxmpp.db.dao;

import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBFriendGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

public abstract class DBFriendGroupDAO extends
		AbstractDBBaseDao<DBFriendGroup> {

	public DBFriendGroupDAO(Context context) {
		super(context);
	}

	public abstract List<DBFriendGroup> findByUser(DBUser user);

	public abstract DBFriendGroup find(String account, String group) ;

	public abstract DBUser find(XMPPUser ur);

	public abstract String getMaxAccount() ;
}
