package com.zyxb.qqxmpp.db.dao;

import android.content.Context;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBFriendGroup;
import com.zyxb.qqxmpp.bean.po.DBFriendGroupMapping;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

import java.util.List;

public abstract class DBFriendGroupMappingDAO extends
		AbstractDBBaseDao<DBFriendGroupMapping> {

	public DBFriendGroupMappingDAO(Context context) {
		super(context);
	}

	public abstract List<DBUser> findByFriendGroup(DBFriendGroup friendGroup);

	// public abstract List<DBUser> findDB3UserByFriendGroup(DBFriendGroup
	// friendGroup);

	// public abstract List<DBFriendGroup> findDB3FriendGroupByUser(DBUser
	// mUser);

	public abstract List<DBFriendGroupMapping> findMappingByFriendGroup(
			DBFriendGroup friendGroup);

	public abstract String getRemark(DBUser user, DBUser u1);

	public abstract String getRemark(String userAccount, String contactAccount);

	public abstract DBFriendGroupMapping find(String userAccount,
			String contactAccount);

	public abstract boolean checkFriend(String userAccount,
			String contactAccount);

	public abstract void delete(String userAccount, String jid) ;

	public abstract void add(String account, XMPPUser contact);
	
	public abstract String getMaxAccount();

	public abstract void update(String userAccount, XMPPUser contact);

	public abstract void updateXMPPFriendState(String userAccount,String jid,int status);
	//public abstract void updateXMPPFriendState(String jid,int status);

	public abstract DBUser find(XMPPUser ur) ;
	// public abstract List<DBFriendGroupMapping> findMappingByUser(DBUser
	// mUser);

}
