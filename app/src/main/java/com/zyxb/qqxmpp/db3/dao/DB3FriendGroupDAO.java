package com.zyxb.qqxmpp.db3.dao;

import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroup;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.dao.impl.AbstractDB3BaseDao;

public abstract class DB3FriendGroupDAO extends
		AbstractDB3BaseDao<DB3FriendGroup> {

	public DB3FriendGroupDAO(Context context) {
		super(context);
	}

	public abstract List<DB3FriendGroup> findByUser(DB3User user);

	public abstract DB3FriendGroup find(String account, String group) ;

	public abstract DB3User find(XMPPUser ur);

	public abstract String getMaxAccount() ;
}
