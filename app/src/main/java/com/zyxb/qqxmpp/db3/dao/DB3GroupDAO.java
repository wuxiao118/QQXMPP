package com.zyxb.qqxmpp.db3.dao;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.db3.dao.impl.AbstractDB3BaseDao;

public abstract class DB3GroupDAO extends AbstractDB3BaseDao<DB3Group> {

	public DB3GroupDAO(Context context) {
		super(context);
	}

}
