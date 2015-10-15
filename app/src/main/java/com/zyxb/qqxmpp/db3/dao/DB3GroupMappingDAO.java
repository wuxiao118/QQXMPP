package com.zyxb.qqxmpp.db3.dao;

import java.util.List;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3GroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.dao.impl.AbstractDB3BaseDao;

public abstract class DB3GroupMappingDAO extends
		AbstractDB3BaseDao<DB3GroupMapping> {

	public DB3GroupMappingDAO(Context context) {
		super(context);
	}

	public abstract List<DB3User> findByGroup(DB3Group group);

	public abstract List<DB3Group> findByUser(DB3User user);

	public abstract List<DB3Group> findSortedByUser(DB3User user);

	public abstract List<DB3GroupMapping> findMappingByGroup(DB3Group group);

	public abstract List<DB3GroupMapping> findMappingByUser(DB3User user);

	public abstract boolean isExistUser(DB3Group group, DB3User user);

	public abstract boolean isExistUser(String groupAccount, String userAccount);

	public abstract String getRemark(String userAccount, String groupAccount);

	public abstract int getGroupTitle(String userAccount, String groupAccount);

	public abstract int getGroupNum(String groupAccount);

	public abstract List<DB3GroupMapping> findByGroup(String groupAccount);

	public abstract DB3GroupMapping getFriend(String groupAccount,
			String contactAccount);
	
	public abstract String getMaxAccount();

}
