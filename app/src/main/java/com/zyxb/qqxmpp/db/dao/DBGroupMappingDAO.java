package com.zyxb.qqxmpp.db.dao;

import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBGroupMapping;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

public abstract class DBGroupMappingDAO extends
		AbstractDBBaseDao<DBGroupMapping> {

	public DBGroupMappingDAO(Context context) {
		super(context);
	}

	public abstract List<DBUser> findByGroup(DBGroup group);

	public abstract List<DBGroup> findByUser(DBUser user);

	public abstract List<DBGroup> findSortedByUser(DBUser user);

	public abstract List<DBGroupMapping> findMappingByGroup(DBGroup group);

	public abstract List<DBGroupMapping> findMappingByUser(DBUser user);

	public abstract boolean isExistUser(DBGroup group, DBUser user);

	public abstract boolean isExistUser(String groupAccount, String userAccount);

	public abstract String getRemark(String userAccount, String groupAccount);

	public abstract int getGroupTitle(String userAccount, String groupAccount);

	public abstract int getGroupNum(String groupAccount);

	public abstract List<DBGroupMapping> findByGroup(String groupAccount);

	public abstract DBGroupMapping getFriend(String groupAccount,
			String contactAccount);
	
	public abstract String getMaxAccount();

}
