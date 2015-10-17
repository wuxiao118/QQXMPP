package com.zyxb.qqxmpp.engine;

import android.content.Context;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DAOFactory;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;

public class DBUserEngine {
	private DBUserDAO dao;

	public DBUserEngine(Context context) {
		dao = DAOFactory.getDB3UserDAO(context);
	}

	public DBUser getByName(String name) {
		DBUser user = dao.findByName(name);
		dao.close();
		return user;
	}

	public DBUser login(String username, String pwd) {
		DBUser user = dao.login(username, pwd);
		dao.close();
		return user;
	}

	public DBUser getUser(String account) {
		DBUser user = dao.findByAccount(account);
		dao.close();
		
		return user;
	}

	public DBUser find(XMPPUser ur) {
		DBUser user = dao.find(ur);
		dao.close();
		
		return user;
	}

	public DBUser find(String loginJid) {
		DBUser user = dao.findByName(loginJid);
		dao.close();
		
		return user;
	}
}
