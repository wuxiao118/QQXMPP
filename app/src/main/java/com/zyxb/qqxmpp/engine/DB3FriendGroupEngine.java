package com.zyxb.qqxmpp.engine;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.Contact;
import com.zyxb.qqxmpp.bean3.FriendGroupInfo;
import com.zyxb.qqxmpp.bean3.Information;
import com.zyxb.qqxmpp.bean3.XMPPUser;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroup;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupMappingDAO;

import java.util.ArrayList;
import java.util.List;

public class DB3FriendGroupEngine {
	private DB3FriendGroupMappingDAO dao;
	private DB3FriendGroupDAO fgDao;

	public DB3FriendGroupEngine(Context context) {
		dao = DAOFactory.getDB3FriendGroupMappingDAO(context);
		fgDao = DAOFactory.getDB3FriendGroupDAO(context);
	}

	public List<FriendGroupInfo> getFriends(DB3User user) {
		List<FriendGroupInfo> infos = new ArrayList<FriendGroupInfo>();

		// 查找当前用户所有的分组
		List<DB3FriendGroup> fgroups = fgDao.findByUser(user);
		FriendGroupInfo info = null;
		for (int i = 0; i < fgroups.size(); i++) {
			DB3FriendGroup fg = fgroups.get(i);
			info = new FriendGroupInfo();
			info.setAccount(fg.getAccount());
			info.setName(fg.getName());

			// 查找当前分组user
			List<Information> users = new ArrayList<Information>();
			info.setFriends(users);
			List<DB3FriendGroupMapping> mappings = dao
					.findMappingByFriendGroup(fg);
			int k = 0;
			Information us = null;
			for (int j = 0; j < mappings.size(); j++) {
				DB3FriendGroupMapping mapping = mappings.get(j);
				us = new Information();
				us.setAccount(mapping.getUser().getAccount());
				us.setChannel(mapping.getLoginChannel());
				us.setComments(mapping.getRemark());
				// us.setGroupTitle(mapping.get)//群用户才有
				us.setIcon(mapping.getUser().getIcon());
				us.setName(mapping.getUser().getNickname());
				us.setRenew(mapping.getUser().getRenew());
				us.setState(mapping.getLoginState());
				us.setType(FriendGroupInfo.TYPE_USER);
				users.add(us);
				us = null;

				// 判断是否登陆
				if (mapping.getLoginState() != DB3Columns.LOGIN_STATE_OFFLINE
						&& mapping.getLoginState() != DB3Columns.LOGIN_STATE_LEAVE_MESSAGE) {
					k++;
				}
			}
			info.setCount(k);

			infos.add(info);
			info = null;
		}

		fgDao.close();
		dao.close();

		return infos;
	}

	public Contact getFriend(String userAccount,String contactAccount) {
		DB3FriendGroupMapping mapping = dao.find(userAccount,contactAccount);
		dao.close();

		DB3User user = mapping.getUser();
		Contact cont = new Contact();
		cont.setAccount(user.getAccount());
		cont.setAge(user.getAge());
		cont.setBirthday(user.getBirthday());
		cont.setCompany(user.getCompany());
		cont.setConstellation(user.getConstellation());
		cont.setDesp(user.getDesp());
		cont.setEmail(user.getEmail());
		cont.setExportDays(user.getExportDays());
		cont.setGender(user.getGender());
		cont.setHometown(user.getHometown());
		cont.setIcon(user.getIcon());
		cont.setId(user.getId());
		cont.setLevel(user.getLevel());
		cont.setLocation(user.getLocation());
		cont.setLoginDays(user.getLoginDays());
		cont.setNickname(user.getNickname());
		cont.setOccupation(user.getOccupation());
		cont.setPersonalitySignature(user.getPersonalitySignature());
		cont.setPwd(user.getPwd());
		cont.setRegisterTime(user.getRegisterTime());
		cont.setRenew(user.getRenew());
		cont.setSchool(user.getSchool());
		cont.setState(user.getState());
		cont.setWebSpace(user.getWebSpace());
		//状态
		cont.setLoginChannel(mapping.getLoginChannel());
		cont.setLoginState(mapping.getLoginState());
		cont.setRemark(mapping.getRemark());
		//分组名称
		cont.setGroupName(mapping.getFriendGroup().getName());

		mapping = null;
		user = null;

		return cont;
	}

	public String getRemark(String userAccount, String contactAccount) {
		String remark = dao.getRemark(userAccount, contactAccount);
		dao.close();

		return remark;
	}

	public boolean checkFriend(String userAccount, String contactAccount) {
		boolean isFriend = dao.checkFriend(userAccount,contactAccount);
		dao.close();

		return isFriend;
	}

	public void delete(String userAccount, String jid) {
		dao.delete(userAccount,jid);
		dao.close();
	}

	public void add(String account, XMPPUser contact) {
		dao.add(account,contact);
		dao.close();
	}

	public void update(String userAccount, XMPPUser contact){
		dao.update(userAccount,contact);
		dao.close();
	}

	public DB3User find(XMPPUser ur) {
		DB3User user = fgDao.find(ur);
		fgDao.close();

		return user;
	}
}
