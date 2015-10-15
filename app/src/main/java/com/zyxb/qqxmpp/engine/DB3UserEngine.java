package com.zyxb.qqxmpp.engine;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;

public class DB3UserEngine {
	private DB3UserDAO dao;

	public DB3UserEngine(Context context) {
		dao = DAOFactory.getDB3UserDAO(context);
	}

	public DB3User getByName(String name) {
		DB3User user = dao.findByName(name);
		dao.close();
		return user;
	}

	public DB3User login(String username, String pwd) {
		DB3User user = dao.login(username, pwd);
		dao.close();
		return user;
	}

	public DB3User getUser(String account) {
		DB3User user = dao.findByAccount(account);
		dao.close();
		
		return user;
	}

	public DB3User find(XMPPUser ur) {
		DB3User user = dao.find(ur);
		dao.close();
		
		return user;
	}

	public DB3User find(String loginJid) {
		DB3User user = dao.findByName(loginJid);
		dao.close();
		
		return user;
	}
}
