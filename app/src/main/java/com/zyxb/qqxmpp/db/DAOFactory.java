package com.zyxb.qqxmpp.db;

import android.content.Context;

import com.zyxb.qqxmpp.db.dao.DBFriendGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBFriendGroupMappingDAO;
import com.zyxb.qqxmpp.db.dao.DBGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBGroupMappingDAO;
import com.zyxb.qqxmpp.db.dao.DBMessageDAO;
import com.zyxb.qqxmpp.db.dao.DBSystemGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;
import com.zyxb.qqxmpp.db.dao.impl.DBFriendGroupDAOImpl;
import com.zyxb.qqxmpp.db.dao.impl.DBFriendGroupMappingDAOImpl;
import com.zyxb.qqxmpp.db.dao.impl.DBGroupDAOImpl;
import com.zyxb.qqxmpp.db.dao.impl.DBGroupMappingDAOImpl;
import com.zyxb.qqxmpp.db.dao.impl.DBMessageDAOImpl;
import com.zyxb.qqxmpp.db.dao.impl.DBSystemGroupDAOImpl;
import com.zyxb.qqxmpp.db.dao.impl.DBUserDAOImpl;

public class DAOFactory {

	public static DBUserDAO getDB3UserDAO(Context context) {
		return new DBUserDAOImpl(context);
	}

	public static DBFriendGroupDAO getDB3FriendGroupDAO(Context context) {
		return new DBFriendGroupDAOImpl(context);
	}

	public static DBFriendGroupMappingDAO getDB3FriendGroupMappingDAO(
			Context context) {
		return new DBFriendGroupMappingDAOImpl(context);
	}

	public static DBGroupDAO getDB3GroupDAO(Context context) {
		return new DBGroupDAOImpl(context);
	}

	public static DBGroupMappingDAO getDB3GroupMappingDAO(Context context) {
		return new DBGroupMappingDAOImpl(context);
	}

	public static DBSystemGroupDAO getDB3SystemGroupDAO(Context context) {
		return new DBSystemGroupDAOImpl(context);
	}

	public static DBMessageDAO getDB3MessageDAO(Context context) {
		return new DBMessageDAOImpl(context);
	}
}
