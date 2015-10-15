package com.zyxb.qqxmpp.db3.dao;

import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroup;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.dao.impl.AbstractDB3BaseDao;

public abstract class DB3FriendGroupMappingDAO extends
		AbstractDB3BaseDao<DB3FriendGroupMapping> {

	public DB3FriendGroupMappingDAO(Context context) {
		super(context);
	}

	public abstract List<DB3User> findByFriendGroup(DB3FriendGroup friendGroup);

	// public abstract List<DB3User> findDB3UserByFriendGroup(DB3FriendGroup
	// friendGroup);

	// public abstract List<DB3FriendGroup> findDB3FriendGroupByUser(DB3User
	// user);

	public abstract List<DB3FriendGroupMapping> findMappingByFriendGroup(
			DB3FriendGroup friendGroup);

	public abstract String getRemark(DB3User user, DB3User u1);

	public abstract String getRemark(String userAccount, String contactAccount);

	public abstract DB3FriendGroupMapping find(String userAccount,
			String contactAccount);

	public abstract boolean checkFriend(String userAccount,
			String contactAccount);

	public abstract void delete(String userAccount, String jid) ;

	public abstract void add(String account, XMPPUser contact);
	
	public abstract String getMaxAccount();

	public abstract void update(String userAccount, XMPPUser contact);

	public abstract DB3User find(XMPPUser ur) ;
	// public abstract List<DB3FriendGroupMapping> findMappingByUser(DB3User
	// user);

}
