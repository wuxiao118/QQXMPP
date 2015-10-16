package com.zyxb.qqxmpp.bean.po;

import java.util.ArrayList;
import java.util.List;

import com.zyxb.qqxmpp.db.DBColumns;

import android.database.Cursor;

public class DBObjectHelper {
	/**
	 * 从cursor中获取user
	 *
	 * @param cursor
	 * @return
	 */
	public static DBUser getDB3User(Cursor cursor) {
		DBUser user = new DBUser();
		user.setId(cursor.getInt(cursor.getColumnIndex(DBColumns.USER_ID)));
		user.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_ACCOUNT)));
		user.setNickname(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_NICKNAME)));
		user.setPwd(cursor.getString(cursor.getColumnIndex(DBColumns.USER_PWD)));
		user.setIcon(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_ICON)));
		user.setAge(cursor.getInt(cursor.getColumnIndex(DBColumns.USER_AGE)));
		user.setGender(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_GENDER)));
		user.setEmail(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_EMAIL)));
		user.setLocation(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_LOCATION)));
		user.setLoginDays(cursor.getInt(cursor
				.getColumnIndex(DBColumns.USER_LOGIN_DAYS)));
		user.setLevel(cursor.getInt(cursor
				.getColumnIndex(DBColumns.USER_LEVEL)));
		user.setRegisterTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.USER_REGISTER_TIME)));
		user.setExportDays(cursor.getInt(cursor
				.getColumnIndex(DBColumns.USER_EXPORT_DAYS)));
		user.setRenew(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_RENEW)));
		user.setState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.USER_STATE)));
		user.setBirthday(cursor.getLong(cursor
				.getColumnIndex(DBColumns.USER_BIRTHDAY)));
		user.setConstellation(cursor.getInt(cursor
				.getColumnIndex(DBColumns.USER_CONSTELLATION)));
		user.setOccupation(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_OCCUPATION)));
		user.setCompany(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_COMPANY)));
		user.setSchool(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_SCHOOL)));
		user.setHometown(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_HOMETOWN)));
		user.setDesp(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_DESP)));
		user.setPersonalitySignature(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_PERSONALITY_SIGNATURE)));
		user.setWebSpace(cursor.getString(cursor
				.getColumnIndex(DBColumns.USER_WEB_SPACE)));

		return user;
	}

	public static List<DBUser> getDB3Users(Cursor cursor) {
		List<DBUser> users = new ArrayList<DBUser>();
		while (cursor.moveToNext()) {
			users.add(getDB3User(cursor));
		}
		cursor.close();

		return users;
	}

	/**
	 * friendgroup
	 *
	 * @param cursor
	 * @return
	 */
	public static DBFriendGroup getDB3FriendGroup(DBUser user, Cursor cursor) {
		DBFriendGroup friendGroup = new DBFriendGroup();
		friendGroup.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.FRIEND_GROUP_ID)));
		friendGroup.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.FRIEND_GROUP_ACCOUNT)));
		friendGroup.setName(cursor.getString(cursor
				.getColumnIndex(DBColumns.FRIEND_GROUP_NAME)));
		friendGroup.setPosition(cursor.getInt(cursor
				.getColumnIndex(DBColumns.FRIEND_GROUP_POSITION)));
		friendGroup.setUser(user);

		return friendGroup;
	}

	public static List<DBFriendGroup> getDB3FriendGroups(DBUser user,
														  Cursor cursor) {
		List<DBFriendGroup> friendGroups = new ArrayList<DBFriendGroup>();
		while (cursor.moveToNext()) {
			friendGroups.add(getDB3FriendGroup(user, cursor));
		}
		cursor.close();

		return friendGroups;
	}

	/**
	 * friendgroupmapping
	 *
	 * @param user
	 * @param friendGroup
	 * @param cursor
	 * @return
	 */
	public static DBFriendGroupMapping getDB3FriendGroupMapping(DBUser user,
																 DBFriendGroup friendGroup, Cursor cursor) {
		DBFriendGroupMapping mapping = new DBFriendGroupMapping();
		mapping.setUser(user);
		mapping.setFriendGroup(friendGroup);
		mapping.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.FRIEND_STATE_ID)));
		mapping.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.FRIEND_STATE_ACCOUNT)));
		mapping.setLoginState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.FRIEND_STATE_LOGIN_STATE)));
		mapping.setLoginChannel(cursor.getInt(cursor
				.getColumnIndex(DBColumns.FRIEND_STATE_LOGIN_CHANNEL)));
		mapping.setPosition(cursor.getInt(cursor
				.getColumnIndex(DBColumns.FRIEND_STATE_POSITION)));
		mapping.setRemark(cursor.getString(cursor
				.getColumnIndex(DBColumns.FRIEND_STATE_REMARK)));

		return mapping;
	}

	public static List<DBFriendGroupMapping> getDB3FriendGroupMappings(
			DBUser user, DBFriendGroup friendGroup, Cursor cursor) {
		List<DBFriendGroupMapping> mappings = new ArrayList<DBFriendGroupMapping>();
		while (cursor.moveToNext()) {
			mappings.add(getDB3FriendGroupMapping(user, friendGroup, cursor));
		}
		cursor.close();

		return mappings;
	}

	/**
	 * group
	 *
	 * @param cursor
	 * @return
	 */
	public static DBGroup getDB3Group(Cursor cursor) {
		DBGroup group = new DBGroup();
		group.setId(cursor.getInt(cursor.getColumnIndex(DBColumns.GROUP_ID)));
		group.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_ACCOUNT)));
		group.setName(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_NAME)));
		group.setIcon(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_ICON)));
		group.setDesp(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_DESP)));
		group.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.GROUP_CREATE_TIME)));
		group.setClassification(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_CLASSIFICATION)));

		return group;
	}

	public static List<DBGroup> getDB3Groups(Cursor cursor) {
		List<DBGroup> groups = new ArrayList<DBGroup>();
		while (cursor.moveToNext()) {
			groups.add(getDB3Group(cursor));
		}
		cursor.close();

		return groups;
	}

	/**
	 * groupmapping
	 *
	 * @return
	 */
	public static DBGroupMapping getDB3GroupMapping(DBUser user,
													 DBGroup group, Cursor cursor) {
		DBGroupMapping mapping = new DBGroupMapping();
		mapping.setUser(user);
		mapping.setGroup(group);
		mapping.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_ID)));
		mapping.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_ACCOUNT)));
		mapping.setLoginState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_LOGIN_STATE)));
		mapping.setLoginChannel(cursor.getInt(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_LOGIN_CHANNEL)));
		mapping.setInterTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_INTER_TIME)));
		mapping.setGroupTitle(cursor.getInt(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_GROUP_TITLE)));
		mapping.setMsgSetting(cursor.getInt(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_MSG_SETTING)));
		mapping.setLevel(cursor.getInt(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_LEVEL)));
		mapping.setRemark(cursor.getString(cursor
				.getColumnIndex(DBColumns.GROUP_STATE_REMARK)));

		return mapping;
	}

	public static List<DBGroupMapping> getDB3GroupMappings(DBUser user,
															DBGroup group, Cursor cursor) {
		List<DBGroupMapping> mappings = new ArrayList<DBGroupMapping>();
		while (cursor.moveToNext()) {
			mappings.add(getDB3GroupMapping(user, group, cursor));
		}
		cursor.close();

		return mappings;
	}

	/**
	 * systemgroup
	 *
	 * @param cursor
	 * @return
	 */
	public static DBSystemGroup getDB3SystemGroup(Cursor cursor) {
		DBSystemGroup sysgroup = new DBSystemGroup();
		sysgroup.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.SYSTEM_GROUP_ID)));
		sysgroup.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.SYSTEM_GROUP_ACCOUNT)));
		sysgroup.setName(cursor.getString(cursor
				.getColumnIndex(DBColumns.SYSTEM_GROUP_NAME)));
		sysgroup.setDesp(cursor.getString(cursor
				.getColumnIndex(DBColumns.SYSTEM_GROUP_DESP)));
		sysgroup.setIcon(cursor.getString(cursor
				.getColumnIndex(DBColumns.SYSTEM_GROUP_ICON)));
		sysgroup.setType(cursor.getInt(cursor
				.getColumnIndex(DBColumns.SYSTEM_GROUP_TYPE)));

		return sysgroup;
	}

	public static List<DBSystemGroup> getDB3SystemGroups(Cursor cursor) {
		List<DBSystemGroup> sysgroups = new ArrayList<DBSystemGroup>();
		while (cursor.moveToNext()) {
			sysgroups.add(getDB3SystemGroup(cursor));
		}
		cursor.close();

		return sysgroups;
	}

	/**
	 * message
	 *
	 * @param user
	 * @param group
	 * @param cursor
	 * @return
	 */
	public static DBMessage getDB3Message(Cursor cursor) {
		DBMessage message = new DBMessage();

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DBMessage> getDB3Messages(Cursor cursor) {
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			messages.add(getDB3Message(cursor));
		}
		cursor.close();

		return messages;
	}

	/**
	 * systemmessage
	 *
	 * @param user
	 * @param group
	 * @param cursor
	 * @return
	 */
	public static DBMessage getDB3SystemMessage(DBUser user,
												 DBSystemGroup group, Cursor cursor) {
		DBMessage message = new DBMessage();
		message.setFromGroup(group);
		message.setTo(user);

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DBMessage> getDB3SystemMessages(DBUser user,
														DBSystemGroup group, Cursor cursor) {
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			messages.add(getDB3SystemMessage(user, group, cursor));
		}
		cursor.close();

		return messages;
	}

	/**
	 * group message
	 *
	 * @param user
	 * @param group
	 * @param cursor
	 * @return
	 */
	public static DBMessage getDB3GroupMessage(DBUser user, DBGroup group,
												Cursor cursor) {
		DBMessage message = new DBMessage();
		message.setToGroup(group);
		message.setFrom(user);

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DBMessage> getDB3GroupMessages(DBUser user,
													   DBGroup group, Cursor cursor) {
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			messages.add(getDB3GroupMessage(user, group, cursor));
		}
		cursor.close();

		return messages;
	}

	/**
	 * contact message
	 *
	 * @param user
	 * @param group
	 * @param cursor
	 * @return
	 */
	public static DBMessage getDB3ContactMessage(DBUser from, DBUser to,
												  Cursor cursor) {
		DBMessage message = new DBMessage();
		String fromAccount = cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_FROM));
		if (fromAccount.equals(from.getAccount())) {
			message.setTo(to);
			message.setFrom(from);
		} else {
			message.setTo(from);
			message.setFrom(to);
		}

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DBColumns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DBColumns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DBColumns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DBMessage> getDB3ContactMessages(DBUser from,
														 DBUser to, Cursor cursor) {
		List<DBMessage> messages = new ArrayList<DBMessage>();
		while (cursor.moveToNext()) {
			messages.add(getDB3ContactMessage(from, to, cursor));
		}
		cursor.close();

		return messages;
	}
}
