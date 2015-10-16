package com.zyxb.qqxmpp.db.dao;

import com.zyxb.qqxmpp.bean.po.DBUser;

public interface DBBaseDAO<T> {
	int STATE_NEW_MESSAGE = 0;
	int STATE_NO_NEW_MESSAGE = 1;
	int STATE_ERROR = 2;
	int STATE_UPDATE = 3;
	int add(T t);
	T findByAccount(String account);
	int update(T t);
	int delete(T t);
	int delete(DBUser user);
	int clear();
}
