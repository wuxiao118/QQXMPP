package com.zyxb.qqxmpp.bean3.po;

import java.util.ArrayList;
import java.util.List;

import com.zyxb.qqxmpp.db3.DB3Columns;

import android.database.Cursor;

public class DB3ObjectHelper {
	/**
	 * 从cursor中获取user
	 *
	 * @param cursor
	 * @return
	 */
	public static DB3User getDB3User(Cursor cursor) {
		DB3User user = new DB3User();
		user.setId(cursor.getInt(cursor.getColumnIndex(DB3Columns.USER_ID)));
		user.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_ACCOUNT)));
		user.setNickname(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_NICKNAME)));
		user.setPwd(cursor.getString(cursor.getColumnIndex(DB3Columns.USER_PWD)));
		user.setIcon(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_ICON)));
		user.setAge(cursor.getInt(cursor.getColumnIndex(DB3Columns.USER_AGE)));
		user.setGender(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_GENDER)));
		user.setEmail(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_EMAIL)));
		user.setLocation(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_LOCATION)));
		user.setLoginDays(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.USER_LOGIN_DAYS)));
		user.setLevel(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.USER_LEVEL)));
		user.setRegisterTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.USER_REGISTER_TIME)));
		user.setExportDays(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.USER_EXPORT_DAYS)));
		user.setRenew(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_RENEW)));
		user.setState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.USER_STATE)));
		user.setBirthday(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.USER_BIRTHDAY)));
		user.setConstellation(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.USER_CONSTELLATION)));
		user.setOccupation(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_OCCUPATION)));
		user.setCompany(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_COMPANY)));
		user.setSchool(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_SCHOOL)));
		user.setHometown(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_HOMETOWN)));
		user.setDesp(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_DESP)));
		user.setPersonalitySignature(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_PERSONALITY_SIGNATURE)));
		user.setWebSpace(cursor.getString(cursor
				.getColumnIndex(DB3Columns.USER_WEB_SPACE)));

		return user;
	}

	public static List<DB3User> getDB3Users(Cursor cursor) {
		List<DB3User> users = new ArrayList<DB3User>();
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
	public static DB3FriendGroup getDB3FriendGroup(DB3User user, Cursor cursor) {
		DB3FriendGroup friendGroup = new DB3FriendGroup();
		friendGroup.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.FRIEND_GROUP_ID)));
		friendGroup.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.FRIEND_GROUP_ACCOUNT)));
		friendGroup.setName(cursor.getString(cursor
				.getColumnIndex(DB3Columns.FRIEND_GROUP_NAME)));
		friendGroup.setPosition(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.FRIEND_GROUP_POSITION)));
		friendGroup.setUser(user);

		return friendGroup;
	}

	public static List<DB3FriendGroup> getDB3FriendGroups(DB3User user,
														  Cursor cursor) {
		List<DB3FriendGroup> friendGroups = new ArrayList<DB3FriendGroup>();
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
	public static DB3FriendGroupMapping getDB3FriendGroupMapping(DB3User user,
																 DB3FriendGroup friendGroup, Cursor cursor) {
		DB3FriendGroupMapping mapping = new DB3FriendGroupMapping();
		mapping.setUser(user);
		mapping.setFriendGroup(friendGroup);
		mapping.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.FRIEND_STATE_ID)));
		mapping.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.FRIEND_STATE_ACCOUNT)));
		mapping.setLoginState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.FRIEND_STATE_LOGIN_STATE)));
		mapping.setLoginChannel(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.FRIEND_STATE_LOGIN_CHANNEL)));
		mapping.setPosition(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.FRIEND_STATE_POSITION)));
		mapping.setRemark(cursor.getString(cursor
				.getColumnIndex(DB3Columns.FRIEND_STATE_REMARK)));

		return mapping;
	}

	public static List<DB3FriendGroupMapping> getDB3FriendGroupMappings(
			DB3User user, DB3FriendGroup friendGroup, Cursor cursor) {
		List<DB3FriendGroupMapping> mappings = new ArrayList<DB3FriendGroupMapping>();
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
	public static DB3Group getDB3Group(Cursor cursor) {
		DB3Group group = new DB3Group();
		group.setId(cursor.getInt(cursor.getColumnIndex(DB3Columns.GROUP_ID)));
		group.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_ACCOUNT)));
		group.setName(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_NAME)));
		group.setIcon(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_ICON)));
		group.setDesp(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_DESP)));
		group.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.GROUP_CREATE_TIME)));
		group.setClassification(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_CLASSIFICATION)));

		return group;
	}

	public static List<DB3Group> getDB3Groups(Cursor cursor) {
		List<DB3Group> groups = new ArrayList<DB3Group>();
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
	public static DB3GroupMapping getDB3GroupMapping(DB3User user,
													 DB3Group group, Cursor cursor) {
		DB3GroupMapping mapping = new DB3GroupMapping();
		mapping.setUser(user);
		mapping.setGroup(group);
		mapping.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_ID)));
		mapping.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_ACCOUNT)));
		mapping.setLoginState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_LOGIN_STATE)));
		mapping.setLoginChannel(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_LOGIN_CHANNEL)));
		mapping.setInterTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_INTER_TIME)));
		mapping.setGroupTitle(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_GROUP_TITLE)));
		mapping.setMsgSetting(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_MSG_SETTING)));
		mapping.setLevel(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_LEVEL)));
		mapping.setRemark(cursor.getString(cursor
				.getColumnIndex(DB3Columns.GROUP_STATE_REMARK)));

		return mapping;
	}

	public static List<DB3GroupMapping> getDB3GroupMappings(DB3User user,
															DB3Group group, Cursor cursor) {
		List<DB3GroupMapping> mappings = new ArrayList<DB3GroupMapping>();
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
	public static DB3SystemGroup getDB3SystemGroup(Cursor cursor) {
		DB3SystemGroup sysgroup = new DB3SystemGroup();
		sysgroup.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.SYSTEM_GROUP_ID)));
		sysgroup.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.SYSTEM_GROUP_ACCOUNT)));
		sysgroup.setName(cursor.getString(cursor
				.getColumnIndex(DB3Columns.SYSTEM_GROUP_NAME)));
		sysgroup.setDesp(cursor.getString(cursor
				.getColumnIndex(DB3Columns.SYSTEM_GROUP_DESP)));
		sysgroup.setIcon(cursor.getString(cursor
				.getColumnIndex(DB3Columns.SYSTEM_GROUP_ICON)));
		sysgroup.setType(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.SYSTEM_GROUP_TYPE)));

		return sysgroup;
	}

	public static List<DB3SystemGroup> getDB3SystemGroups(Cursor cursor) {
		List<DB3SystemGroup> sysgroups = new ArrayList<DB3SystemGroup>();
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
	public static DB3Message getDB3Message(Cursor cursor) {
		DB3Message message = new DB3Message();

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DB3Message> getDB3Messages(Cursor cursor) {
		List<DB3Message> messages = new ArrayList<DB3Message>();
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
	public static DB3Message getDB3SystemMessage(DB3User user,
												 DB3SystemGroup group, Cursor cursor) {
		DB3Message message = new DB3Message();
		message.setFromGroup(group);
		message.setTo(user);

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DB3Message> getDB3SystemMessages(DB3User user,
														DB3SystemGroup group, Cursor cursor) {
		List<DB3Message> messages = new ArrayList<DB3Message>();
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
	public static DB3Message getDB3GroupMessage(DB3User user, DB3Group group,
												Cursor cursor) {
		DB3Message message = new DB3Message();
		message.setToGroup(group);
		message.setFrom(user);

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DB3Message> getDB3GroupMessages(DB3User user,
													   DB3Group group, Cursor cursor) {
		List<DB3Message> messages = new ArrayList<DB3Message>();
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
	public static DB3Message getDB3ContactMessage(DB3User from, DB3User to,
												  Cursor cursor) {
		DB3Message message = new DB3Message();
		String fromAccount = cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_FROM));
		if (fromAccount.equals(from.getAccount())) {
			message.setTo(to);
			message.setFrom(from);
		} else {
			message.setTo(from);
			message.setFrom(to);
		}

		message.setId(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ID)));
		message.setAccount(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_ACCOUNT)));
		message.setMsg(cursor.getString(cursor
				.getColumnIndex(DB3Columns.MESSAGE_MSG)));
		message.setType(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_TYPE)));
		message.setState(cursor.getInt(cursor
				.getColumnIndex(DB3Columns.MESSAGE_STATE)));
		message.setCreateTime(cursor.getLong(cursor
				.getColumnIndex(DB3Columns.MESSAGE_CREATE_TIME)));

		return message;
	}

	public static List<DB3Message> getDB3ContactMessages(DB3User from,
														 DB3User to, Cursor cursor) {
		List<DB3Message> messages = new ArrayList<DB3Message>();
		while (cursor.moveToNext()) {
			messages.add(getDB3ContactMessage(from, to, cursor));
		}
		cursor.close();

		return messages;
	}
}
