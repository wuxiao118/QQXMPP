package com.zyxb.qqxmpp.test;

import java.util.List;

import com.zyxb.qqxmpp.bean.po.DBMessage;
import com.zyxb.qqxmpp.db.DBInit;

import android.test.AndroidTestCase;

public class DB3Test extends AndroidTestCase {

	public void testAddUser() {
		DBInit.addUser(getContext());
	}

	public void testAddFriendGroup() {
		DBInit.addFriendGroup(getContext());
	}

	public void testAddFriendState() {
		DBInit.addFriendState(getContext());
	}

	public void testAddGroup() {
		DBInit.addGroup(getContext());
	}

	public void testAddGroupState() {
		DBInit.addGroupState(getContext());
	}

	public void testAddSystemGroup() {
		DBInit.addSystemGroup(getContext());
	}

	public void testAddMessage() {
		DBInit.addMessage(getContext());
	}

	public void create() {
		DBInit.create(getContext());
	}

	public void clear() {
		DBInit.clear(getContext());
	}

	public void testFindGroupMessage() {
		List<DBMessage> messages = DBInit.getGroupMessages(getContext());
		for (DBMessage m : messages) {
			System.out.println(m);
		}
	}

	public void testFindContactMessage() {
		List<DBMessage> messages = DBInit.getContactMessages(getContext());
		for (DBMessage m : messages) {
			System.out.println(m);
		}
	}

	public void testFindSystemMessage() {
		List<DBMessage> messages = DBInit.getSystemMessages(getContext());
		for (DBMessage m : messages) {
			System.out.println(m);
		}
	}

	public void testFindNewestMessage() {
		List<DBMessage> messages = DBInit.getNewest(getContext());
		for (DBMessage m : messages) {
			System.out.println(m);
		}
	}
}
