package com.zyxb.qqxmpp.db.dao;

import android.content.Context;

import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.db.dao.impl.AbstractDBBaseDao;

public abstract class DBGroupDAO extends AbstractDBBaseDao<DBGroup> {

	public DBGroupDAO(Context context) {
		super(context);
	}

}
