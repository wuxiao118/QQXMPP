package com.zyxb.qqxmpp.util;

public class Const {

	// connect
	public static final String XMPP_HOST = "xmpp_host";
	public static final String XMPP_PORT = "xmpp_port";
	public static final String XMPP_HOST_DEFAULT = "192.168.1.100";
	public static final int XMPP_PORT_DEFAULT = 5222;
	public static final String XMPP_SMACKDEBUG = "xmpp_smackdebug";
	public static final String XMPP_REQUIRE_TLS = "xmpp_require_tls";
	public static final String XMPP_CUSTOM_SERVER = "xmpp_custom_server";
	public static final String XMPP_RESSOURCE = "xmpp_ressource";

	// message
	public static final String XMPP_MESSAGE_CARBONS = "xmpp_message_carbons";
	public static final String XMPP_MESSAGE_STATUS_MODE = "xmpp_message_status_mode";
	//mUser
	public static final String XMPP_USER_STATUS_MESSAGE = "在线";
	//public static final String XMPP_USER_AVALIABLE = "0";
	public static final String XMPP_USER_PRIORITY = "xmpp_user_priority";
	//mUser status
	public final static String USER_STATE_OFFLINE = "offline";
	public final static String USER_STATE_DND = "dnd";
	public final static String USER_STATE_XA = "xa";
	public final static String USER_STATE_AWAY = "away";
	public final static String USER_STATE_AVAILABLE = "available";
	public final static String USER_STATE_CHAT = "chat";

	/**
	 * 登录状态广播
	 */
	public static final String ACTION_IS_LOGIN_SUCCESS = "com.android.qq.is_login_success";
	/**
	 * 消息记录操作广播
	 */
	public static final String ACTION_MSG_OPER = "com.android.qq.msgoper";
	/**
	 * 添加好友请求广播
	 */
	public static final String ACTION_ADDFRIEND = "com.android.qq.addfriend";
	/**
	 * 新消息广播
	 */
	public static final String ACTION_NEW_MSG = "com.android.qq.newmsg";
	/**
	 * 好友在线状态更新广播
	 */
	public static final String ACTION_FRIENDS_ONLINE_STATUS_CHANGE = "com.android.qq.friends_online_status_change";

	// 静态地图API
	public static final String LOCATION_URL_S = "http://api.map.baidu.com/staticimage?width=320&height=240&zoom=17&center=";
	public static final String LOCATION_URL_L = "http://api.map.baidu.com/staticimage?width=480&height=800&zoom=17&center=";

	public static final String MSG_TYPE_TEXT = "msg_type_text";// 文本消息
	public static final String MSG_TYPE_IMG = "msg_type_img";// 图片
	public static final String MSG_TYPE_VOICE = "msg_type_voice";// 语音
	public static final String MSG_TYPE_LOCATION = "msg_type_location";// 位置

	public static final String MSG_TYPE_ADD_FRIEND = "msg_type_add_friend";// 添加好友
	public static final String MSG_TYPE_ADD_FRIEND_SUCCESS = "msg_type_add_friend_success";// 同意添加好友

	public static final int NOTIFY_ID = 0x90;

	/**
	 * 是否开启声音
	 */
	public static final String MSG_IS_VOICE = "msg_is_voice";
	/**
	 * 是否开启振动
	 */
	public static final String MSG_IS_VIBRATE = "msg_is_vibrate";

	// 主界面
	public static final int FRAGMENT_STATE_MESSAGE = 1;
	public static final int FRAGMENT_STATE_CONTACT = 2;
	public static final int FRAGMENT_STATE_NEWS = 3;

	// sharedpreferences
	public static final String SP_USERNAME = "username";
	public static final String SP_PWD = "pwd";
	public static final String SP_MAIN_FRAGMENT = "mainState";
	public static final String USER_TYPE_LOCAL = "local";
	public static final String USER_TYPE_XMPP = "xmpp";
	public static final String SP_USER_TYPE = "user_type";

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

	// 消息类型
	public static final String[] MESSAGE_TYPES = { "group", "contact", "sys" };
	public static final String MESSAGE_TYPE = "message_type";
	// groupId or msg_from
	public static final String MESSAGE_ID = "message_id";
	public static final String MESSAGE_TITLE = "message_title";
	public static final String MESSAGE_STATE = "message_state";
	public static final String MESSAGE_OTHER_ID = "message_otherId";
	public static final String MESSAGE_COMMENTS = "message_comments";

	public static final int MESSAGE_TYPE_NONE = -1;
	public static final int MESSAGE_TYPE_GROUP = 0;
	public static final int MESSAGE_TYPE_CONTACT = 1;
	public static final int MESSAGE_TYPE_SYS = 2;
	public static final int MESSAGE_TYPE_ALL = 10;

	// 消息发送接收状态
	public static final String[] MESSAGE_STATES = { "CREATED", "SENDING",
			"SENDED", "RECEIVED", "READED" };
	public static final int MESSAGE_STATE_CREATED = 0;
	public static final int MESSAGE_STATE_SENDING = 1;
	public static final int MESSAGE_STATE_SENDED = 2;
	public static final int MESSAGE_STATE_RECEIVED = 3;
	public static final int MESSAGE_STATE_READED = 4;

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

	// 网络
	public static String PROXY_IP = "";
	public static int PROXY_PORT = 0;
}
