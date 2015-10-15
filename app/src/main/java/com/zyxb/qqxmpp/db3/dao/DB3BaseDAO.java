package com.zyxb.qqxmpp.db3.dao;

import com.zyxb.qqxmpp.bean3.po.DB3User;

public interface DB3BaseDAO<T> {
	int STATE_NEW_MESSAGE = 0;
	int STATE_NO_NEW_MESSAGE = 1;
	int STATE_ERROR = 2;
	int STATE_UPDATE = 3;
	int add(T t);
	T findByAccount(String account);
	int update(T t);
	int delete(T t);
	int delete(DB3User user);
	int clear();
}
