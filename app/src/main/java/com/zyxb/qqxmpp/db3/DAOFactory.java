package com.zyxb.qqxmpp.db3;

import android.content.Context;

import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO;
import com.zyxb.qqxmpp.db3.dao.DB3SystemGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;
import com.zyxb.qqxmpp.db3.dao.impl.DB3FriendGroupDAOImpl;
import com.zyxb.qqxmpp.db3.dao.impl.DB3FriendGroupMappingDAOImpl;
import com.zyxb.qqxmpp.db3.dao.impl.DB3GroupDAOImpl;
import com.zyxb.qqxmpp.db3.dao.impl.DB3GroupMappingDAOImpl;
import com.zyxb.qqxmpp.db3.dao.impl.DB3MessageDAOImpl;
import com.zyxb.qqxmpp.db3.dao.impl.DB3SystemGroupDAOImpl;
import com.zyxb.qqxmpp.db3.dao.impl.DB3UserDAOImpl;

public class DAOFactory {

	public static DB3UserDAO getDB3UserDAO(Context context) {
		return new DB3UserDAOImpl(context);
	}

	public static DB3FriendGroupDAO getDB3FriendGroupDAO(Context context) {
		return new DB3FriendGroupDAOImpl(context);
	}

	public static DB3FriendGroupMappingDAO getDB3FriendGroupMappingDAO(
			Context context) {
		return new DB3FriendGroupMappingDAOImpl(context);
	}

	public static DB3GroupDAO getDB3GroupDAO(Context context) {
		return new DB3GroupDAOImpl(context);
	}

	public static DB3GroupMappingDAO getDB3GroupMappingDAO(Context context) {
		return new DB3GroupMappingDAOImpl(context);
	}

	public static DB3SystemGroupDAO getDB3SystemGroupDAO(Context context) {
		return new DB3SystemGroupDAOImpl(context);
	}

	public static DB3MessageDAO getDB3MessageDAO(Context context) {
		return new DB3MessageDAOImpl(context);
	}
}
