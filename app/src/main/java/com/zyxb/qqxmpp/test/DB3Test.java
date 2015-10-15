package com.zyxb.qqxmpp.test;

import java.util.List;

import com.zyxb.qqxmpp.bean3.po.DB3Message;
import com.zyxb.qqxmpp.db3.DB3Init;

import android.test.AndroidTestCase;

public class DB3Test extends AndroidTestCase {

	public void testAddUser() {
		DB3Init.addUser(getContext());
	}

	public void testAddFriendGroup() {
		DB3Init.addFriendGroup(getContext());
	}

	public void testAddFriendState() {
		DB3Init.addFriendState(getContext());
	}

	public void testAddGroup() {
		DB3Init.addGroup(getContext());
	}

	public void testAddGroupState() {
		DB3Init.addGroupState(getContext());
	}

	public void testAddSystemGroup() {
		DB3Init.addSystemGroup(getContext());
	}

	public void testAddMessage() {
		DB3Init.addMessage(getContext());
	}

	public void create() {
		DB3Init.create(getContext());
	}

	public void clear() {
		DB3Init.clear(getContext());
	}

	public void testFindGroupMessage() {
		List<DB3Message> messages = DB3Init.getGroupMessages(getContext());
		for (DB3Message m : messages) {
			System.out.println(m);
		}
	}

	public void testFindContactMessage() {
		List<DB3Message> messages = DB3Init.getContactMessages(getContext());
		for (DB3Message m : messages) {
			System.out.println(m);
		}
	}

	public void testFindSystemMessage() {
		List<DB3Message> messages = DB3Init.getSystemMessages(getContext());
		for (DB3Message m : messages) {
			System.out.println(m);
		}
	}

	public void testFindNewestMessage() {
		List<DB3Message> messages = DB3Init.getNewest(getContext());
		for (DB3Message m : messages) {
			System.out.println(m);
		}
	}
}
