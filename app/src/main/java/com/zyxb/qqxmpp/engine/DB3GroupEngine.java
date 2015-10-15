package com.zyxb.qqxmpp.engine;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.Contact;
import com.zyxb.qqxmpp.bean3.Information;
import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3GroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.bean3.vo.GroupInfo;
import com.zyxb.qqxmpp.db3.DAOFactory;
import com.zyxb.qqxmpp.db3.DB3Columns;
import com.zyxb.qqxmpp.db3.dao.DB3GroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupMappingDAO;

import java.util.ArrayList;
import java.util.List;

public class DB3GroupEngine {
	private DB3GroupMappingDAO dao;
	private DB3GroupDAO gDao;

	public DB3GroupEngine(Context context) {
		dao = DAOFactory.getDB3GroupMappingDAO(context);
		gDao = DAOFactory.getDB3GroupDAO(context);
	}

	public List<GroupInfo> getGroups(DB3User user) {
		List<DB3GroupMapping> mappings = dao.findMappingByUser(user);
		dao.close();

		ArrayList<GroupInfo> creators = new ArrayList<GroupInfo>();
		ArrayList<GroupInfo> managers = new ArrayList<GroupInfo>();
		ArrayList<GroupInfo> members = new ArrayList<GroupInfo>();

		GroupInfo info = null;
		for (DB3GroupMapping m : mappings) {
			int level = m.getLevel();
			info = new GroupInfo();
			info.setAccount(m.getGroup().getAccount());
			info.setIcon(m.getGroup().getIcon());
			info.setLevel(level);
			info.setName(m.getGroup().getName());
			info.setType(GroupInfo.GROUP_TYPE_ITEM);

			switch (level) {
				case DB3Columns.GROUP_LEVEL_CREATOR:
					creators.add(info);
					break;
				case DB3Columns.GROUP_LEVEL_MASTER:
					managers.add(info);
					break;
				case DB3Columns.GROUP_LEVEL_ELITE:
				case DB3Columns.GROUP_LEVEL_COMMON:
					members.add(info);
					break;
			}

			info = null;
		}

		mappings = null;

		if (creators.size() > 0) {
			info = new GroupInfo();
			info.setName("我创建的群");
			info.setLevel(creators.size());
			info.setType(GroupInfo.GROUP_TYPE_TITLE);
			creators.add(0, info);
			info = null;
		}

		if (managers.size() > 0) {
			info = new GroupInfo();
			info.setName("我管理的群");
			info.setLevel(managers.size());
			info.setType(GroupInfo.GROUP_TYPE_TITLE);
			creators.add(info);
			info = null;

			creators.addAll(managers);
		}

		if (members.size() > 0) {
			info = new GroupInfo();
			info.setName("我加入的群");
			info.setLevel(members.size());
			info.setType(GroupInfo.GROUP_TYPE_TITLE);
			creators.add(info);
			info = null;

			creators.addAll(members);
		}

		return creators;
	}

	public DB3Group getGroup(String account) {
		DB3Group group = gDao.findByAccount(account);
		gDao.close();

		return group;
	}

	public String getGroupRemark(String groupAccount, String userAccount) {
		String remark = dao.getRemark(userAccount, groupAccount);
		dao.close();

		return remark;
	}

	public int getGroupNum(String groupAccount) {
		int num = dao.getGroupNum(groupAccount);
		dao.close();

		return num;
	}

	public List<Information> getFriends(String groupAccount) {
		List<DB3GroupMapping> mps = dao.findByGroup(groupAccount);
		dao.close();

		List<Information> infos = new ArrayList<Information>();
		Information info = null;
		for (int i = 0; i < mps.size(); i++) {
			DB3GroupMapping map = mps.get(i);
			info = new Information();
			info.setAccount(map.getUser().getAccount());
			info.setChannel(map.getLoginChannel());
			info.setComments(map.getRemark());
			info.setGroupTitle(map.getGroupTitle());
			info.setIcon(map.getUser().getIcon());
			info.setLevel(map.getLevel());
			info.setName(map.getUser().getNickname());
			info.setState(map.getLoginState());

			infos.add(info);
			info = null;
		}
		mps = null;

		return infos;
	}

	public Contact getFriend(String groupAccount, String contactAccount) {
		DB3GroupMapping mapping = dao.getFriend(groupAccount, contactAccount);
		dao.close();

		Contact cont = new Contact();
		// 设置user信息
		DB3User user = mapping.getUser();
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

		// 状态
		cont.setLoginChannel(mapping.getLoginChannel());
		cont.setLoginState(mapping.getLoginState());
		cont.setRemark(mapping.getRemark());
		//group相关信息
		cont.setInterTime(mapping.getInterTime());
		cont.setGroupTitle(mapping.getGroupTitle());
		cont.setMsgSetting(mapping.getMsgSetting());
		cont.setGroupLevel(mapping.getLevel());
		cont.setGroupName(mapping.getGroup().getName());

		mapping = null;
		user = null;

		return cont;
	}

	/**
	 * public List<GroupInfo> getGroups(DB3User user) { List<DB3GroupMapping>
	 * mappings = dao.findMappingByUser(user); dao.close();
	 *
	 * Map<Integer, List<GroupInfo>> map = new HashMap<Integer,
	 * List<GroupInfo>>(); GroupInfo info = null; List<GroupInfo> infos = null;
	 * List<Integer> keys = new ArrayList<Integer>(); for (DB3GroupMapping m :
	 * mappings) { int level = m.getLevel(); if (!map.containsKey(level)) {
	 * List<GroupInfo> l = new ArrayList<GroupInfo>(); map.put(level, l);
	 * keys.add(level); }
	 *
	 * infos = map.get(level); info = new GroupInfo();
	 * info.setAccount(m.getGroup() .getAccount());
	 * info.setIcon(m.getGroup().getIcon()); info.setLevel(level);
	 * info.setName(m.getGroup().getName());
	 * info.setType(GroupInfo.GROUP_TYPE_ITEM); infos.add(info); info = null;
	 * infos = null; }
	 *
	 * Collections.sort(keys); Collections.reverse(keys);
	 *
	 * infos = new ArrayList<GroupInfo>(); for(int i=0;i<keys.size();i++){
	 * if(map.get(keys.get(i)).size()>0){ info = new GroupInfo();
	 * info.setType(GroupInfo .GROUP_TYPE_TITLE); info.setName(GroupInfo
	 * .GROUP_TITLES[keys.get(i)]); info.setLevel (map.get(keys.get(i)).size());
	 * infos.add(info); info = null;
	 *
	 * infos.addAll(map.get(keys.get(i))); } }
	 *
	 * return infos; }
	 *
	 *
	 * 死循环
	 *
	 * @param user
	 * @return
	 *
	 *         public List<GroupInfo> getGroups(DB3User user) {
	 *         List<DB3GroupMapping> mappings = dao.findMappingByUser(user);
	 *         dao.close();
	 *
	 *         List<GroupInfo> infos = new ArrayList<GroupInfo>(); // 处理业务 int
	 *         level = mappings.get(0).getLevel(); GroupInfo title = null;
	 *         GroupInfo info = null; int tnum = 0; int num = 0; boolean isEnd =
	 *         false; for (int i = 0; i < mappings.size(); i++) { if (level ==
	 *         mappings.get(i).getLevel()) { //结束的时候增加数量 if(isEnd){
	 *         title.setLevel(num); title = null; num = 0; }
	 *
	 *         // 增加头 title = new GroupInfo();
	 *         title.setName(GroupInfo.GROUP_TITLES[level]);
	 *         title.setType(GroupInfo.GROUP_TYPE_TITLE); infos.add(title);
	 *
	 *         level--; tnum++; i--; isEnd = false; } else { // 群信息 info = new
	 *         GroupInfo(); DB3Group group = mappings.get(i-tnum).getGroup();
	 *         info.setAccount(group.getAccount());
	 *         info.setIcon(group.getIcon());
	 *         info.setLevel(mappings.get(i-tnum).getLevel());
	 *         info.setName(group.getName());
	 *         info.setType(GroupInfo.GROUP_TYPE_ITEM); infos.add(info); info =
	 *         null;
	 *
	 *         isEnd = true; num++; } }
	 *
	 *         return infos; }
	 */
}
