package com.zyxb.qqxmpp.db;

public class DBColumns {
	// TODO friend和user应该合并,在服务器上本身只有一张user表

	// 登陆用户信息表
	public static final String USER_TABLE_NAME = "user";
	public static final String USER_ID = "_id";
	public static final String USER_ACCOUNT = "account";
	public static final String USER_NICKNAME = "nickname";
	public static final String USER_PWD = "pwd";
	public static final String USER_ICON = "icon";
	public static final String USER_AGE = "age";
	public static final String USER_GENDER = "gender";
	public static final String USER_EMAIL = "email";
	public static final String USER_LOCATION = "location";
	public static final String USER_LOGIN_DAYS = "login_days";
	public static final String USER_LEVEL = "level";
	public static final String USER_REGISTER_TIME = "register_time";
	public static final String USER_EXPORT_DAYS = "expert_days";
	public static final String USER_RENEW = "renew";
	public static final String USER_STATE = "state";
	public static final String USER_BIRTHDAY = "birthday";
	public static final String USER_CONSTELLATION = "constellation";
	public static final String USER_OCCUPATION = "occupation";
	public static final String USER_COMPANY = "company";
	public static final String USER_SCHOOL = "school";
	public static final String USER_HOMETOWN = "hometown";
	public static final String USER_DESP = "desp";
	public static final String USER_PERSONALITY_SIGNATURE = "personality_signature";
	public static final String USER_WEB_SPACE = "web_space";

	// user状态
	public static final String[] USER_STATES = { "在线", "隐身" };
	public static final int USER_STATE_ONLINE = 0;
	public static final int USER_STATE_HIDDEN = 1;

	// 星座
	public static final String[] CONSTELLATION = {};

	// 好友分组
	public static final String FRIEND_GROUP_TABLE_NAME = "friend_group";
	public static final String FRIEND_GROUP_ID = "_id";
	public static final String FRIEND_GROUP_ACCOUNT = "account";
	public static final String FRIEND_GROUP_USER_ACCOUNT = "user_account";
	public static final String FRIEND_GROUP_NAME = "name";
	public static final String FRIEND_GROUP_POSITION = "position";// 分组列表排序

	// 用户-好友信息(在线状态,登陆方式)
	public static final String FRIEND_STATE_TABLE_NAME = "friend_state";
	public static final String FRIEND_STATE_ID = "_id";
	public static final String FRIEND_STATE_ACCOUNT = "account";
	public static final String FRIEND_STATE_USER_ACCOUNT = "user_account";
	public static final String FRIEND_STATE_FRIEND_GROUP_ACCOUNT = "friend_group_account";
	public static final String FRIEND_STATE_LOGIN_STATE = "login_state";
	public static final String FRIEND_STATE_LOGIN_CHANNEL = "login_channel";
	public static final String FRIEND_STATE_POSITION = "position";// 分组中位置
	public static final String FRIEND_STATE_REMARK = "remark";// 备注

	// 好友登陆状态
	public static final String[] LOGIN_STATES = { "离线", "离线请留言", "离开", "隐身",
			"繁忙", "在线" };
	public static final int LOGIN_STATE_OFFLINE = 0;
	public static final int LOGIN_STATE_LEAVE_MESSAGE = 1;
	public static final int LOGIN_STATE_ALWAY = 2;
	public static final int LOGIN_STATE_HIDDEN = 3;
	public static final int LOGIN_STATE_BUSY = 4;
	public static final int LOGIN_STATE_ONLINE = 5;
	// 好友登陆方式
	public static final String[] LOGIN_CHANNELS = { "未知", "2G", "3G", "4G",
			"WIFI", "computer", "phone", "iphone" };
	public static final int LOGIN_CHANNEL_UNKNOWN = 0;
	public static final int LOGIN_CHANNEL_2G = 1;
	public static final int LOGIN_CHANNEL_3G = 2;
	public static final int LOGIN_CHANNEL_4G = 3;
	public static final int LOGIN_CHANNEL_WIFI = 4;
	public static final int LOGIN_CHANNEL_COMPUTER = 5;
	public static final int LOGIN_CHANNEL_PHONE = 6;
	public static final int LOGIN_CHANNEL_IPHONE = 7;

	// 群基本信息
	public static final String GROUP_TABLE_NAME = "qqgroup";
	public static final String GROUP_ID = "_id";
	public static final String GROUP_ACCOUNT = "account";
	public static final String GROUP_NAME = "name";
	public static final String GROUP_ICON = "icon";
	public static final String GROUP_DESP = "desp";
	public static final String GROUP_CREATE_TIME = "create_time";
	public static final String GROUP_CLASSIFICATION = "classification";

	// 群头衔
	public static final String[] GROUP_TITLES = { "菜鸟", "潜水", "冒泡", "吐槽", "活跃",
			"话唠", "传说" };
	public static final int GROUP_TITLE_ROOKIE = 0;
	public static final int GROUP_TITLE_DIVE = 1;
	public static final int GROUP_TITLE_BUBBLE = 2;
	public static final int GROUP_TITLE_TUCAO = 3;
	public static final int GROUP_TITLE_ACTIVE = 4;
	public static final int GROUP_TITLE_CHATTERBOX = 5;
	public static final int GROUP_TITLE_LEGEND = 6;
	public static final int GROUP_TITLE_NONE = -1;
	// 群消息设置
	public static final String[] GROUP_SETTINGS = { "接收并显示所有消息", "接收所有消息",
			"不接收消息" };
	public static final int GROUP_SETTING_ALL = 0;
	public static final int GROUP_SETTING_ONLY_RECEIVED = 1;
	public static final int GROUP_SETTING_NOT_RECEIVE = 2;
	// 群成员级别
	public static final String[] GROUP_LEVELS = { "普通", "精英", "管理员", "创建者" };
	public static final int GROUP_LEVEL_COMMON = 0;
	public static final int GROUP_LEVEL_ELITE = 1;
	public static final int GROUP_LEVEL_MASTER = 2;
	public static final int GROUP_LEVEL_CREATOR = 3;

	// 群-用户关系
	public static final String GROUP_STATE_TABLE_NAME = "group_state";
	public static final String GROUP_STATE_ID = "_id";
	public static final String GROUP_STATE_ACCOUNT = "account";
	public static final String GROUP_STATE_LOGIN_STATE = "login_state";
	public static final String GROUP_STATE_LOGIN_CHANNEL = "login_channel";
	public static final String GROUP_STATE_INTER_TIME = "inter_time";
	public static final String GROUP_STATE_GROUP_TITLE = "group_title";
	public static final String GROUP_STATE_MSG_SETTING = "msg_setting";
	public static final String GROUP_STATE_LEVEL = "level";
	public static final String GROUP_STATE_REMARK = "remark";
	public static final String GROUP_STATE_USER_ACCOUNT = "user_account";
	public static final String GROUP_STATE_GROUP_ACCOUNT = "group_account";

	// 系统消息基本信息
	public static final String SYSTEM_GROUP_TABLE_NAME = "sysgroup";
	public static final String SYSTEM_GROUP_ID = "_id";
	public static final String SYSTEM_GROUP_ACCOUNT = "account";
	public static final String SYSTEM_GROUP_NAME = "name";
	public static final String SYSTEM_GROUP_ICON = "icon";
	public static final String SYSTEM_GROUP_DESP = "desp";
	public static final String SYSTEM_GROUP_TYPE = "type";

	// 系统类别
	public static final String[] SYSTEM_TYPES = { "ALL", "USER" };
	public static final int SYSTEM_TYPE_ALL = 0;
	public static final int SYSTEM_TYPE_USER = 1;

	// 系统-用户 (不需要)
	// public static final String SYSTEM_STATE_TABLE_NAME = "sysstate";
	// public static final String SYSTEM_STATE_ID = "_id";
	// public static final String SYSTEM_STATE_ACCOUNT = "account";
	// public static final String SYSTEM_STATE_USER_ACCOUNT = "user_account";
	// public static final String SYSTEM_STATE_SYSTEM_ACCOUNT =
	// "system_account";

	// 系统消息类型
	public static final String[] SYSTEM_GROUP_TYPES = { "all", "mUser" };
	public static final int SYSTEM_GROUP_TYPE_ALL = 0;
	public static final int SYSTEM_GROUP_TYPES_USER = 1;

	// 消息
	public static final String MESSAGE_TABLE_NAME = "message";
	public static final String MESSAGE_ID = "_id";
	public static final String MESSAGE_ACCOUNT = "account";
	public static final String MESSAGE_FROM = "msg_from";
	public static final String MESSAGE_MSG = "msg";
	public static final String MESSAGE_TO = "msg_to";
	public static final String MESSAGE_CREATE_TIME = "create_time";
	public static final String MESSAGE_TYPE = "msg_type";
	public static final String MESSAGE_STATE = "state";
	public static final String MESSAGE_DATA1 = "data1";
	public static final String MESSAGE_DATA2 = "data2";
	public static final String MESSAGE_DATA3 = "data3";
	public static final String MESSAGE_DATA4 = "data4";
	public static final String MESSAGE_DATA5 = "data5";
	public static final String MESSAGE_DATA6 = "data6";
	public static final String MESSAGE_DATA7 = "data7";

	// 消息类型
	public static final String[] MESSAGE_TYPES = { "SYS", "GROUP", "CONTACT",
			"ALL" };
	public static final int MESSAGE_TYPE_SYS = 0;
	public static final int MESSAGE_TYPE_GROUP = 1;
	public static final int MESSAGE_TYPE_CONTACT = 2;
	public static final int MESSAGE_TYPE_ALL = 3;

	// 消息状态
	public static final String[] MESSAGE_STATES = { "CREATED", "SENDING",
			"SENDED", "RECEIVED", "READED", "FAIL" };
	public static final int MESSAGE_STATE_CREATED = 0;
	public static final int MESSAGE_STATE_SENDING = 1;
	public static final int MESSAGE_STATE_SENDED = 2;
	public static final int MESSAGE_STATE_RECEIVED = 3;
	public static final int MESSAGE_STATE_READED = 4;
	public static final int MESSAGE_STATE_FAIL = 5;

	// 正常信息
	public static final int RESULT_OK = 1;

	// 错误信息
	public static final int ERROR_USER_NOT_FOUND = -1;
	public static final int ERROR_FRIEND_GROUP_NOT_FOUND = -2;
	public static final int ERROR_FRIEND_STATE_NOT_FOUND = -3;
	public static final int ERROR_GROUP_NOT_FOUND = -4;
	public static final int ERROR_GROUP_STATE_NOT_FOUND = -5;
	public static final int ERROR_SYSTEM_GROUP_NOT_FOUND = -6;
	public static final int ERROR_MESSAGE_NOT_FOUND = -7;

	public static final int ERROR_USER_EXISTS = -21;
	public static final int ERROR_FRIEND_GROUP_EXISTS = -22;
	public static final int ERROR_FRIEND_STATE_EXISTS = -23;
	public static final int ERROR_GROUP_EXISTS = -24;
	public static final int ERROR_GROUP_STATE_EXISTS = -25;
	public static final int ERROR_SYSTEM_GROUP_EXISTS = -26;
	public static final int ERROR_MESSAGE_EXISTS = -27;
}
