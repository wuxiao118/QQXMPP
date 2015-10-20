package com.zyxb.qqxmpp.db.dao;

import android.content.Context;

import com.zyxb.qqxmpp.bean.po.DBSystemGroup;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

public abstract class DBSystemGroupDAO extends AbstractDBBaseDao<DBSystemGroup> {

	public DBSystemGroupDAO(Context context) {
		super(context);
	}

	public abstract String getMaxAccount();

}
