package com.zyxb.qqxmpp.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.carbons.Carbon;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.forward.Forwarded;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.provider.DelayInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * smack xmpp功能
 *
 * @author 吴小雄
 *
 * 使用MessageQueen进行用户合消息的CRUD操作
 *
 * 更改:
 * MessageQueen大材小用，没必要，去掉
 * 再需要对数据库操作的地方，直接增加数据操作
 *
 * 发送消息不正确原因:
 * 添加测试好友,jid name@domain
 * ip貌似不行,因为ip只是内网ip且没有配置映射
 * 手机登陆需使用ip,domain不能正确解析
 *
 */
@SuppressLint("DefaultLocale")
public class XMPPEngine {
	private static final String TAG = "XMPPEngine";
	public static final String XMPP_IDENTITY_NAME = "XMPP";
	public static final String XMPP_IDENTITY_TYPE = "phone";

	//将消息发送给MessageQueueService
	public static final String XMPP_QUEUE_MESSAGE = "com.zyxb.qqxmpp.XMPP_QUEUE_MESSAGE";

	// receiver
	// 成功登陆
	public static final String XMPP_USER_LOGIN = "com.zyxb.qqxmpp.USER_LOGIN";
	// 用户名或密码错误
	public static final String XMPP_USER_REJECT = "com.zyxb.qqxmpp.USER_REJECT";
	// 注销成功
	public static final String XMPP_USER_LOGOUT = "com.zyxb.qqxmpp.USER_LOGOUT";

	// register
	public static final int REGISTER_NO_RESULT = 0;
	public static final int REGISTER_SUCCESS = 1;
	public static final int REGISTER_USER_EXISTS = 2;
	public static final int REGISTER_FAILED = 3;

	// roster
	private Roster mRoster;
	private RosterListener mRosterListener;
	//
	private PacketListener mPacketListener;
	private PacketListener mSendFailureListener;
	//
	// 接受语音文件监听
	private FileTransferListener mfiletransfransferlistener;
	// 语音存放位置
	private static final String RECORD_ROOT_PATH = Environment
			.getExternalStorageDirectory() + File.separator + "/testxmpp";

	// bad address family, android2.2bug不能启用IPV6协议
	static {
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
	}

	static {
		registerSmackProviders();
	}

	static void registerSmackProviders() {
		ProviderManager pm = ProviderManager.getInstance();
		// add IQ handling
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// add delayed delivery notifications
		pm.addExtensionProvider("delay", "urn:xmpp:delay",
				new DelayInfoProvider());
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInfoProvider());
		// add carbons and forwarding
		pm.addExtensionProvider("forwarded", Forwarded.NAMESPACE,
				new Forwarded.Provider());
		pm.addExtensionProvider("sent", Carbon.NAMESPACE, new Carbon.Provider());
		pm.addExtensionProvider("received", Carbon.NAMESPACE,
				new Carbon.Provider());
		// add delivery receipts
		pm.addExtensionProvider(DeliveryReceipt.ELEMENT,
				DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
		pm.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,
				DeliveryReceipt.NAMESPACE,
				new DeliveryReceiptRequest.Provider());
		// add XMPP Ping (XEP-0199)
		pm.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());

		ServiceDiscoveryManager.setIdentityName(XMPP_IDENTITY_NAME);
		ServiceDiscoveryManager.setIdentityType(XMPP_IDENTITY_TYPE);
	}

	// 联系人
	// private BlockingQueue<Collection<String>> rosters;
	// private List<Collection<String>> rosters;
	// 消息队列
//	private BlockingQueue<XMPPMessageQueueInfo> queue = new ArrayBlockingQueue<XMPPMessageQueueInfo>(
//			50);
//	private Thread queueThread;
	private DataEngine mDataEngine;
//	private boolean isQueueThreadStarted = false;
//	private static final int QUEUE_TYPE_USER_ADD = 0;
//	private static final int QUEUE_TYPE_USER_UPDATE = 1;
//	private static final int QUEUE_TYPE_USER_DELETE = 2;
//	private static final int QUEUE_TYPE_USER_PRESENCE_CHANGED = 3;
//	private static final int QUEUE_TYPE_MESSAGE_RECEIVED = 4;
//	private static final int QUEUE_TYPE_MESSAGE_SEND_FAIL = 5;
//	private static final int QUEUE_TYPE_MESSAGE_SEND_RECEIVED = 6;
//	private static final int QUEUE_TYPE_MESSAGE_SEND = 7;
//	//
//	private static final int QUEUE_CLASS_TYPE_ROSTERS = 0;
//	private static final int QUEUE_CLASS_TYPE_PRESENCE = 1;
//	private static final int QUEUE_CLASS_TYPE_MESSAGE = 2;
//	private static final int QUEUE_CLASS_TYPE_ACCOUNT = 3;

	private ConnectionConfiguration mXMPPConf;

	//
	private Context mContext;
	private static XMPPEngine mEngine;
	private XMPPConnection mXMPPConnection;
	private Object obj = new Object();

	//
	private CountDownLatch mCountDownLatch = new CountDownLatch(1);

	public static XMPPEngine getmEngine() {
		return mEngine;
	}

	public static void setmEngine(XMPPEngine mEngine) {
		XMPPEngine.mEngine = mEngine;
	}

	public XMPPEngine getInstance(Context context) {
		if (mEngine == null) {
			synchronized (obj) {
				if (mEngine == null) {
					mEngine = new XMPPEngine(context);
				}
			}
		}

		return mEngine;
	}

	public XMPPConnection getConn() {
		return mXMPPConnection;
	}

	//清空本身
	public void clear(){
		mEngine = null;
		//mXMPPConnection = null;
	}

	/**
	 * 连接配置
	 *
	 * @param context
	 */
	public XMPPEngine(Context context) {
		mContext = context;
		String customServer = SharedPreferencesUtils.getString(context,
				Const.XMPP_CUSTOM_SERVER, "");
		int pt = SharedPreferencesUtils.getInt(context, Const.XMPP_PORT,
				Const.XMPP_PORT_DEFAULT);
		String server = SharedPreferencesUtils.getString(context,
				Const.XMPP_HOST, Const.XMPP_HOST_DEFAULT);
		boolean smackdebug = SharedPreferencesUtils.getBoolean(context,
				Const.XMPP_SMACKDEBUG, false);
		boolean requireSsl = SharedPreferencesUtils.getBoolean(context,
				Const.XMPP_REQUIRE_TLS, false);
		if (customServer.length() > 0 || pt != Const.XMPP_PORT_DEFAULT)
			this.mXMPPConf = new ConnectionConfiguration(customServer, pt,
					server);
		else
			this.mXMPPConf = new ConnectionConfiguration(server); // use SRV

		this.mXMPPConf.setReconnectionAllowed(false);
		this.mXMPPConf.setSendPresence(false);
		this.mXMPPConf.setCompressionEnabled(false); // disable for now
		this.mXMPPConf.setDebuggerEnabled(smackdebug);
		if (requireSsl)
			this.mXMPPConf
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

		mXMPPConnection = new XMPPConnection(mXMPPConf);
	}

	public XMPPEngine(Context context, String host, boolean smackDebug,
					  boolean requireSsl) {
		mContext = context;
		this.mXMPPConf = new ConnectionConfiguration(host); // use SRV

		this.mXMPPConf.setReconnectionAllowed(false);
		this.mXMPPConf.setSendPresence(false);
		this.mXMPPConf.setCompressionEnabled(false);
		this.mXMPPConf.setDebuggerEnabled(smackDebug);
		if (requireSsl)
			this.mXMPPConf
					.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

		mXMPPConnection = new XMPPConnection(mXMPPConf);
	}

	public boolean login(String account, String pwd, String ressource) {
		try {
			// 监听联系人动态变化
			registerRosterListener();

			// 与服务器交互消息监听,发送消息需要回执，判断是否发送成功
			initServiceDiscovery();

			// 登陆
			// SMACK auto-logins if we were authenticated before
			if (!mXMPPConnection.isAuthenticated()) {
				mXMPPConnection.login(account, pwd, ressource);

				//TODO 未抛出异常，登陆成功，在数据库中添加登陆用户
				mDataEngine = new DataEngine(mContext);
				XMPPUser ur = new XMPPUser();
				ur.setJid(account + "@" + SharedPreferencesUtils.getString(mContext, Const.XMPP_HOST, ""));
				ur.setNickname(account);
				ur.setStatusMessage(pwd);
				mDataEngine.setmUser(mDataEngine.getXMPPUser(ur));

				//Logger.d(TAG, "login success");
				//必须先要添加当前登陆用户，之后更新登陆用户的好友及相关信息
				mCountDownLatch.countDown();
			}

			// 更新在线状态
			setStatusFromConfig();

			// 注册监听其他的事件，比如新消息
			registerAllListener();

			// 成功登陆
			loginScuessed();
		} catch (XMPPException e) {
			String es = Log.getStackTraceString(e).toString();
			Logger.d(TAG, "connect(): XMPPException:" + es);
			loginReject();
		} catch (Exception e) {
			String es = Log.getStackTraceString(e).toString();
			Logger.d(TAG, "connect(): Exception:" + es);
			loginReject();
		}

		return mXMPPConnection.isAuthenticated();
	}

	// 开启消息队列
//	public void startMessageQueue() {
//		mDataEngine = new DataEngine(mContext);
//		String username = SharedPreferencesUtils.getString(mContext,
//				Const.SP_USERNAME, "");
//		mDataEngine.setmUser(mDataEngine.findXMPPUserByName(username));
//		isQueueThreadStarted = true;
//
//		queueThread = new QueueThread(mDataEngine);
//		queueThread.start();
//	}
//
//	// 停止消息队列
//	public void stopMessageQueue() {
//		isQueueThreadStarted = false;
//	}

	/**************************** 登陆后联系人变化,消息变化等 ********************************/
	/**
	 *
	 */
	private void registerRosterListener() {
		mRoster = mXMPPConnection.getRoster();
		mRosterListener = new RosterListener() {

			@Override
			public void presenceChanged(final Presence presence) {
				try {
					mCountDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Logger.i(TAG, "presenceChanged(" + presence.getFrom() + "): "
						+ presence);

				// TODO 更新联系人状态
				//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
				//info.setType(QUEUE_TYPE_USER_PRESENCE_CHANGED);
				//info.setClassType(QUEUE_CLASS_TYPE_PRESENCE);
				//info.setPresence(presence);
//				try {
//					queue.put(info);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				String jabberID = getJabberID(presence.getFrom());
				RosterEntry rosterEntry = mRoster.getEntry(jabberID);
				//updateRosterEntryInDB(rosterEntry);// 更新联系人数据库
				//mService.rosterChanged();// 回调通知服务，主要是用来判断一下是否掉线
				//TODO 仅更新用户对应好友的状态

			}

			@Override
			public void entriesUpdated(final Collection<String> entries) {
				try {
					mCountDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Logger.i(TAG, "entriesUpdated(" + entries + ")");

				// TODO 更新联系人
				//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
				//info.setType(QUEUE_TYPE_USER_UPDATE);
				//info.setClassType(QUEUE_CLASS_TYPE_ROSTERS);
				//info.setRosters(entries);
//				try {
//					queue.put(info);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				for(String entry:entries){
					RosterEntry rosterEntry = mRoster.getEntry(entry);
					Logger.i(TAG,rosterEntry.getUser() + "," + rosterEntry.getName() + "," +rosterEntry.getGroups() + ","
					+ rosterEntry.getStatus() + "," + rosterEntry.getType());
					//TODO 仅修改用户与好友的对应关系
				}
			}

			@Override
			public void entriesDeleted(final Collection<String> entries) {
				try {
					mCountDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Logger.i(TAG, "entriesDeleted(" + entries + ")");

				// TODO 删除联系人
				//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
				//info.setType(QUEUE_TYPE_USER_DELETE);
				//info.setClassType(QUEUE_CLASS_TYPE_ROSTERS);
				//info.setRosters(entries);
//				try {
//					queue.put(info);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				for(String entry:entries){
					RosterEntry rosterEntry = mRoster.getEntry(entry);
					Logger.i(TAG,rosterEntry.getUser() + "," + rosterEntry.getName() + "," +rosterEntry.getGroups() + ","
							+ rosterEntry.getStatus() + "," + rosterEntry.getType());
					//TODO 仅删除用户与好友的对应关系

				}
			}

			@Override
			public void entriesAdded(final Collection<String> entries) {
				try {
					mCountDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Logger.i(TAG, "entriesAdded(" + entries + ")");

				// TODO 添加联系人
				//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
				//info.setType(QUEUE_TYPE_USER_ADD);
				//info.setClassType(QUEUE_CLASS_TYPE_ROSTERS);
				//info.setRosters(entries);
//				try {
//					queue.put(info);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}

				for(String entry:entries){
					RosterEntry rosterEntry = mRoster.getEntry(entry);
//					Logger.i(TAG,rosterEntry.getUser() + "," + rosterEntry.getName() + "," +rosterEntry.getGroups() + ","
//							+ rosterEntry.getStatus() + "," + rosterEntry.getType());

					//xmppUser.setGroup();
					mDataEngine.addXMPPUser(getXMPPUser(rosterEntry));
					//getXMPPUser(rosterEntry);

				}
			}

		};
		mRoster.addRosterListener(mRosterListener);
	}

	private XMPPUser getXMPPUser(RosterEntry rs){
		XMPPUser xmppUser = new XMPPUser();
		String jid = rs.getUser();
		xmppUser.setJid(jid);
		String nickname = rs.getName();
		xmppUser.setNickname(nickname);

		//Logger.d(TAG,"jid:" + jid + ",nickname:" + nickname);
		Collection<RosterGroup> groups = rs.getGroups();
		for(RosterGroup group:groups){
			//Logger.d(TAG,group.getName() + "");
			xmppUser.setGroup(group.getName());
			break;
		}

		//String status = rs.getStatus().toString();
		//String type = rs.getType().toString();
		//Logger.d(TAG,"status:" + status + ",type:" + type);

		RosterPacket.ItemStatus itemStatus = rs.getStatus();
		RosterPacket.ItemType itemType = rs.getType();
		Logger.d(TAG,"status:" + itemStatus + ",type:" + itemType);

		return xmppUser;
	}

	/**
	 * 与服务器交互消息监听,发送消息需要回执，判断是否成功接收
	 */
	private void initServiceDiscovery() {
		// register connection features
		ServiceDiscoveryManager sdm = ServiceDiscoveryManager
				.getInstanceFor(mXMPPConnection);
		if (sdm == null)
  			sdm = new ServiceDiscoveryManager(mXMPPConnection);

		sdm.addFeature("http://jabber.org/protocol/disco#info");

		// reference PingManager, set ping flood protection to 10s
		PingManager.getInstanceFor(mXMPPConnection).setPingMinimumInterval(
				10 * 1000);

		// reference DeliveryReceiptManager, add listener
		DeliveryReceiptManager dm = DeliveryReceiptManager
				.getInstanceFor(mXMPPConnection);
		dm.enableAutoReceipts();
		dm.registerReceiptReceivedListener(new DeliveryReceiptManager.ReceiptReceivedListener() {
			public void onReceiptReceived(String fromJid, String toJid,
										  String receiptId) {
				Logger.d(TAG, "got delivery receipt for " + receiptId);

				// TODO 修改消息状态为发送成功
				//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
				//info.setType(QUEUE_TYPE_MESSAGE_SEND_RECEIVED);
				//info.setClassType(QUEUE_CLASS_TYPE_ACCOUNT);
				//info.setAccount(receiptId);
//				try {
//					queue.put(info);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		});
	}

	/**
	 * 更新在线状态
	 */
	private void setStatusFromConfig() {
		Logger.d(TAG,"Set status available");

		boolean messageCarbons = SharedPreferencesUtils.getBoolean(mContext,
				Const.XMPP_MESSAGE_CARBONS, true);
		String statusMode = SharedPreferencesUtils.getString(mContext,
				Const.XMPP_MESSAGE_STATUS_MODE, Const.USER_STATE_AVAILABLE);
		String statusMessage = SharedPreferencesUtils.getString(mContext,
				Const.XMPP_USER_STATUS_MESSAGE, "在线");
		int priority = SharedPreferencesUtils.getInt(mContext,
				Const.XMPP_USER_PRIORITY, 0);
		if (messageCarbons)
			CarbonManager.getInstanceFor(mXMPPConnection).sendCarbonsEnabled(
					true);

		Presence presence = new Presence(Presence.Type.available);
		Mode mode = Mode.valueOf(statusMode);
		presence.setMode(mode);
		presence.setStatus(statusMessage);
		presence.setPriority(priority);
		mXMPPConnection.sendPacket(presence);
	}

	/**
	 * 注册所有的监听器
	 */
	private void registerAllListener() {
		if (isAuthenticated()) {
			registerMessageListener();
			registerMessageSendFailureListener();
			// ping-pong在连接中处理
			// registerPongListener();
			sendOfflineMessages();
			if (mContext == null) {
				mXMPPConnection.disconnect();
				//return;
			}
		}
	}

	/**
	 * 新消息处理
	 */
	private void registerMessageListener() {
		// do not register multiple packet listeners
		if (mPacketListener != null)
			mXMPPConnection.removePacketListener(mPacketListener);

		PacketTypeFilter filter = new PacketTypeFilter(Message.class);

		Logger.d(TAG,"register message listener");

		mPacketListener = new PacketListener() {
			public void processPacket(Packet packet) {
				try {
					if (packet instanceof Message) {
						Message msg = (Message) packet;
						String chatMessage = msg.getBody();

						Logger.d(TAG,"message body:" + chatMessage);

						// try to extract a carbon
						Carbon cc = CarbonManager.getCarbon(msg);
						if (cc != null
								&& cc.getDirection() == Carbon.Direction.received) {
							Logger.d(TAG, "carbon: " + cc.toXML());
							msg = (Message) cc.getForwarded()
									.getForwardedPacket();
							chatMessage = msg.getBody();
							// fall through
						} else if (cc != null
								&& cc.getDirection() == Carbon.Direction.sent) {
							Logger.d(TAG, "carbon: " + cc.toXML());

							// TODO 发送新的消息处理(加入数据库,发送消息改变receiver)
							//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
							//info.setType(QUEUE_TYPE_MESSAGE_SEND);
							//info.setClassType(QUEUE_CLASS_TYPE_ACCOUNT);
							//info.setAccount(msg.getPacketID());
//							try {
//								queue.put(info);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}

							return;
						}

						if (chatMessage == null) {
							return;
						}

						if (msg.getType() == Message.Type.error) {
							chatMessage = "<Error> " + chatMessage;
						}

						long ts;
						DelayInfo timestamp = (DelayInfo) msg.getExtension(
								"delay", "urn:xmpp:delay");
						if (timestamp == null)
							timestamp = (DelayInfo) msg.getExtension("x",
									"jabber:x:delay");
						if (timestamp != null)
							ts = timestamp.getStamp().getTime();
						else
							ts = System.currentTimeMillis();

						String fromJID = getJabberID(msg.getFrom());

						Logger.d(TAG, "receive new msg: time:" + ts + ",msg:"
								+ chatMessage + ",from:" + fromJID);

						// TODO 接收到新的消息处理(加入数据库,发送消息改变receiver)
						XMPPMessage xmppMsg = new XMPPMessage();
						xmppMsg.setCreateTime(ts);
						xmppMsg.setFrom(fromJID);
						xmppMsg.setMsg(chatMessage);
						xmppMsg.setMsgType(DBColumns.MESSAGE_TYPE_CONTACT);
						xmppMsg.setState(DBColumns.MESSAGE_STATE_RECEIVED);
						//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
						//info.setType(QUEUE_TYPE_MESSAGE_RECEIVED);
						//info.setClassType(QUEUE_CLASS_TYPE_MESSAGE);
						//info.setMessage(xmppMsg);
//						try {
//							queue.put(info);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
				} catch (Exception e) {
					Logger.e(TAG, "failed to process packet:");
					e.printStackTrace();
				}
			}
		};

		mXMPPConnection.addPacketListener(mPacketListener, filter);
	}

	/**
	 * 处理消息发送失败状态
	 */
	private void registerMessageSendFailureListener() {
		// do not register multiple packet listeners
		if (mSendFailureListener != null)
			mXMPPConnection
					.removePacketSendFailureListener(mSendFailureListener);

		PacketTypeFilter filter = new PacketTypeFilter(Message.class);

		mSendFailureListener = new PacketListener() {
			public void processPacket(Packet packet) {
				try {
					if (packet instanceof Message) {
						Message msg = (Message) packet;
						String chatMessage = msg.getBody();

						Log.d("SmackableImp",
								"message "
										+ chatMessage
										+ " could not be sent (ID:"
										+ (msg.getPacketID() == null ? "null"
										: msg.getPacketID()) + ")");
						// TODO 消息发送失败
						//XMPPMessageQueueInfo info = new XMPPMessageQueueInfo();
						//info.setType(QUEUE_TYPE_MESSAGE_SEND_FAIL);
						//info.setClassType(QUEUE_CLASS_TYPE_ACCOUNT);
						//info.setAccount(msg.getPacketID());
//						try {
//							queue.put(info);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}

					}
				} catch (Exception e) {
					Logger.e(TAG, "failed to process packet:");
					e.printStackTrace();
				}
			}
		};

		mXMPPConnection.addPacketSendFailureListener(mSendFailureListener,
				filter);
	}

	/**
	 * 发送离线消息(读取数据库中发送失败的消息发送)
	 */
	private void sendOfflineMessages() {

	}

	/**
	 * 登陆成功broadcast
	 */
	private void loginScuessed() {
		Intent intent = new Intent(XMPP_USER_LOGIN);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 登陆失败broadcast
	 */
	private void loginReject() {
		Intent intent = new Intent(XMPP_USER_REJECT);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 是否登陆成功
	 *
	 * @return
	 */
	public boolean isAuthenticated() {
		if (mXMPPConnection != null) {
			return (mXMPPConnection.isConnected() && mXMPPConnection
					.isAuthenticated());
		}
		return false;
	}

	/**
	 * 获取jid
	 *
	 * @param from
	 * @return
	 */
	private String getJabberID(String from) {
		String[] res = from.split("/");
		return res[0].toLowerCase();
	}

	/******************************* 登陆后变化结束 ******************************************/

	/**
	 * 注销
	 *
	 * @return
	 */
	public boolean logout() {
		Logger.d(TAG, "unRegisterCallback()");
		try {
			mXMPPConnection.getRoster().removeRosterListener(mRosterListener);
			mXMPPConnection.removePacketListener(mPacketListener);
			mXMPPConnection
					.removePacketSendFailureListener(mSendFailureListener);
		} catch (Exception e) {
			return false;
		}
		if (mXMPPConnection.isConnected()) {
			new Thread() {
				public void run() {
					Logger.d(TAG, "shutDown thread started");
					mXMPPConnection.disconnect();
					Logger.d(TAG, "shutDown thread finished");
				}
			}.start();
		}

		setStatusOffline();

		return true;

	}

	/**
	 * 离线后处理
	 */
	private void setStatusOffline() {

	}

	/**
	 * * 注册
	 *
	 * @param account
	 *            注册帐号
	 * @param password
	 *            注册密码
	 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
	 */
	public int regist(String account, String password) {
		if (mXMPPConnection == null)
			return REGISTER_NO_RESULT;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(mXMPPConnection.getServiceName());
		// 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。
		reg.setUsername(account);
		reg.setPassword(password);
		// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(
				reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = mXMPPConnection
				.createPacketCollector(filter);
		mXMPPConnection.sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());
		// Stop queuing results停止请求results（是否成功的结果）
		collector.cancel();
		if (result == null) {
			Log.e("regist", "No response from server.");
			return REGISTER_NO_RESULT;
		} else if (result.getType() == IQ.Type.RESULT) {
			Log.v("regist", "regist success.");
			return REGISTER_SUCCESS;
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e("regist", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return REGISTER_USER_EXISTS;
			} else {
				Log.e("regist", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return REGISTER_FAILED;
			}
		}
	}

	/**
	 * 发送消息
	 *
	 * @param toJID
	 * @param message
	 *
	 * 判断消息发送成功与否
	 * 这里的“成功”有两个概念，一个是数据是否成功到达服务器，一个是服务器是否成功接收。

	一般如果消息没有到达服务器，android-xmpp的sendMessage会返回error。
	另一个服务器是否发送成功，则需要回执处理了，比如发送了消息<message id="a-xxx" /> ，服务器给个回执告诉客户端a-xxx已经成功接收了，
	这时候才是成功，如果超时未接收到回执，那就是失败了。

	 */
	public void sendMessage(String account, String toJID, String message) {
		final Message newMessage = new Message(toJID, Message.Type.chat);
		newMessage.setBody(message);
		// 收到回执中将携带此id
		newMessage.setPacketID(account);

		// DeliveryReceiptRequest request = new DeliveryReceiptRequest();
		newMessage.addExtension(new DeliveryReceiptRequest());
		if (isAuthenticated()) {
			Logger.d(TAG, "account" + account + ",send message to:" + toJID + ",msg:" + message);
			mXMPPConnection.sendPacket(newMessage);
		} else {
			Logger.d(TAG,"send message failed,authenticate failed");
		}
	}

	/**
	 * 更改用户状态
	 *
	 * @param code
	 */
	public void setPresence(int code) {
		if (mXMPPConnection == null)
			return;
		Presence presence;
		switch (code) {
			case 0:
				presence = new Presence(Presence.Type.available);
				mXMPPConnection.sendPacket(presence);
				Logger.v(TAG, "设置在线");
				break;
			case 1:
				presence = new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.chat);
				mXMPPConnection.sendPacket(presence);
				Logger.v(TAG, "设置Q我吧");
				break;
			case 2:
				presence = new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.dnd);
				mXMPPConnection.sendPacket(presence);
				Logger.v(TAG, "设置忙碌");
				break;
			case 3:
				presence = new Presence(Presence.Type.available);
				presence.setMode(Presence.Mode.away);
				mXMPPConnection.sendPacket(presence);
				Logger.v(TAG, "设置离开");
				break;
			case 4:
				Roster roster = mXMPPConnection.getRoster();
				Collection<RosterEntry> entries = roster.getEntries();
				for (RosterEntry entry : entries) {
					presence = new Presence(Presence.Type.unavailable);
					presence.setPacketID(Packet.ID_NOT_AVAILABLE);
					presence.setFrom(mXMPPConnection.getUser());
					presence.setTo(entry.getUser());
					mXMPPConnection.sendPacket(presence);
					Logger.v(TAG, presence.toXML());
				}
				// 向同一用户的其他客户端发送隐身状态
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(mXMPPConnection.getUser());
				presence.setTo(StringUtils.parseBareAddress(mXMPPConnection
						.getUser()));
				mXMPPConnection.sendPacket(presence);
				Logger.v(TAG, "设置隐身");
				break;
			case 5:
				presence = new Presence(Presence.Type.unavailable);
				mXMPPConnection.sendPacket(presence);
				Logger.v(TAG, "设置离线");
				break;
			default:
				break;
		}
	}

	/**
	 * 获取所有组
	 *
	 * @return 所有组集合
	 */
	public List<RosterGroup> getGroups() {
		if (mXMPPConnection == null)
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = mXMPPConnection.getRoster()
				.getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext()) {
			grouplist.add(i.next());
		}
		return grouplist;
	}

	/**
	 * 获取某个组里面的所有好友
	 *
	 * @param groupName
	 *            组名
	 * @return
	 */

	public List<RosterEntry> getEntriesByGroup(String groupName) {
		if (mXMPPConnection == null)

			return null;
		List<RosterEntry> entriesList = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = mXMPPConnection.getRoster().getGroup(
				groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			entriesList.add(i.next());
		}
		return entriesList;
	}

	/**
	 * 获取所有好友信息
	 *
	 * @return
	 */
	public List<RosterEntry> getAllEntries() {
		if (mXMPPConnection == null)
			return null;
		List<RosterEntry> entriesList = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = mXMPPConnection.getRoster()
				.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext()) {
			entriesList.add(i.next());
		}
		return entriesList;
	}

	/**
	 * 获取用户VCard信息
	 *
	 * @param user
	 * @return
	 */
	public VCard getUserVCard(String user) {
		if (mXMPPConnection == null)
			return null;
		VCard vcard = new VCard();
		try {
			vcard.load(mXMPPConnection, user);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return vcard;
	}

	/**
	 * 获取用户头像信息
	 *
	 * @param user
	 * @return
	 */
	public Drawable getUserImage(String user) {
		if (mXMPPConnection == null)
			return null;
		ByteArrayInputStream bais = null;
		try {
			VCard vcard = new VCard();
			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());
			if (user == "" || user == null || user.trim().length() <= 0) {
				return null;
			}
			vcard.load(mXMPPConnection,
					user + "@" + mXMPPConnection.getServiceName());
			if (vcard == null || vcard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vcard.getAvatar());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// input stream to drawable
		BitmapDrawable drawable = new BitmapDrawable(bais);

		return drawable;
	}

	/**
	 * 添加一个分组
	 *
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(String groupName) {
		if (mXMPPConnection == null)
			return false;
		try {
			mXMPPConnection.getRoster().createGroup(groupName);
			Log.v("addGroup", groupName + "創建成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除分组
	 *
	 * @param groupName
	 * @return
	 */
	public boolean removeGroup(String groupName) {
		return true;
	}

	/**
	 * 添加好友 无分组
	 *
	 * @param userName
	 * @param name
	 * @return
	 */
	public boolean addUser(String userName, String name) {
		if (mXMPPConnection == null)
			return false;
		try {
			mXMPPConnection.getRoster().createEntry(userName, name, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 添加好友 有分组
	 *
	 * @param userName
	 * @param name
	 * @param groupName
	 * @return
	 */
	public boolean addUser(String userName, String name, String groupName) {
		if (mXMPPConnection == null)
			return false;
		try {
			Presence subscription = new Presence(Presence.Type.subscribed);
			subscription.setTo(userName);
			userName += "@" + mXMPPConnection.getServiceName();
			mXMPPConnection.sendPacket(subscription);
			mXMPPConnection.getRoster().createEntry(userName, name,
					new String[] { groupName });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除好友
	 *
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName) {
		if (mXMPPConnection == null)
			return false;
		try {
			RosterEntry entry = null;
			if (userName.contains("@"))
				entry = mXMPPConnection.getRoster().getEntry(userName);
			else
				entry = mXMPPConnection.getRoster().getEntry(
						userName + "@" + mXMPPConnection.getServiceName());
			if (entry == null)
				entry = mXMPPConnection.getRoster().getEntry(userName);
			mXMPPConnection.getRoster().removeEntry(entry);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 查询用户
	 *
	 * @param userName
	 * @return
	 */
	public List<HashMap<String, String>> searchUsers(String userName) {
		if (mXMPPConnection == null)
			return null;
		HashMap<String, String> user = null;
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		try {
			new ServiceDiscoveryManager(mXMPPConnection);
			UserSearchManager usm = new UserSearchManager(mXMPPConnection);
			Form searchForm = usm.getSearchForm(mXMPPConnection
					.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("userAccount", true);
			answerForm.setAnswer("userPhote", userName);
			ReportedData data = usm.getSearchResults(answerForm, "search"
					+ mXMPPConnection.getServiceName());
			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext()) {
				user = new HashMap<String, String>();
				row = it.next();
				user.put("userAccount", row.getValues("userAccount").next()
						.toString());
				user.put("userPhote", row.getValues("userPhote").next()
						.toString());
				results.add(user);
				// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * 修改心情
	 *
	 * @param status
	 */
	public void changeStateMessage(String status) {
		if (mXMPPConnection == null)
			return;
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		mXMPPConnection.sendPacket(presence);
	}

	/**
	 * 修改用户头像
	 *
	 * @param file
	 * @return
	 */
	public boolean changeImage(File file) {
		if (mXMPPConnection == null)
			return false;
		try {
			VCard vcard = new VCard();
			vcard.load(mXMPPConnection);
			byte[] bytes;
			bytes = getFileBytes(file);
			String encodedImage = StringUtils.encodeBase64(bytes);
			vcard.setAvatar(bytes, encodedImage);
			vcard.setEncodedImage(encodedImage);
			vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
					+ encodedImage + "</BINVAL>", true);
			// ByteArrayInputStream bais = new ByteArrayInputStream(
			// vcard.getAvatar());

			// input stream to bitmap

			vcard.save(mXMPPConnection);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 文件转字节
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	/**
	 * 删除当前用户
	 *
	 * @return
	 */
	public boolean deleteAccount() {
		if (mXMPPConnection == null)
			return false;
		try {
			mXMPPConnection.getAccountManager().deleteAccount();
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * 修改密码
	 *
	 * @param pwd
	 * @return
	 */
	public boolean changePassword(String pwd) {
		if (mXMPPConnection == null)
			return false;
		try {
			mXMPPConnection.getAccountManager().changePassword(pwd);
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	/**
	 * 初始化会议室列表
	 *
	 * @return
	 */
	public List<HostedRoom> getHostRooms() {
		if (mXMPPConnection == null)
			return null;
		Collection<HostedRoom> hostRooms = null;
		List<HostedRoom> roomInfos = new ArrayList<HostedRoom>();
		try {
			new ServiceDiscoveryManager(mXMPPConnection);
			hostRooms = MultiUserChat.getHostedRooms(mXMPPConnection,
					mXMPPConnection.getServiceName());
			for (HostedRoom entry : hostRooms) {
				roomInfos.add(entry);
				Log.i("room",
						"名字：" + entry.getName() + " - ID:" + entry.getJid());
			}
			Log.i("room", "服务会议数量:" + roomInfos.size());
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return roomInfos;
	}

	/**
	 * 创建房间
	 *
	 * @param user
	 * @param roomName
	 * @param password
	 * @return
	 */
	public MultiUserChat createRoom(String user, String roomName,
									String password) {
		if (mXMPPConnection == null)
			return null;
		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(mXMPPConnection, roomName + "@conference."
					+ mXMPPConnection.getServiceName());
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields
					.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
			List<String> owners = new ArrayList<String>();
			owners.add(mXMPPConnection.getUser());
			// 用户JID
			submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom", true);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
						true);
				// 设置进入密码
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}

	/**
	 * 加入会议室
	 *
	 * @param user
	 *            昵称
	 * @param password
	 *            会议室密码
	 * @param roomsName
	 *            会议室名
	 */
	public MultiUserChat joinMultiUserChat(String user, String roomsName,
										   String password) {
		if (mXMPPConnection == null)
			return null;
		try {
			// 使用XMPPConnection创建一个MultiUserChat窗口
			MultiUserChat muc = new MultiUserChat(mXMPPConnection, roomsName
					+ "@conference." + mXMPPConnection.getServiceName());
			// 聊天室服务将会决定要接受的历史记录数量
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxChars(0);
			// history.setSince(new Date());
			// 用户加入聊天室
			muc.join(user, password, history,
					SmackConfiguration.getPacketReplyTimeout());
			Log.i("MultiUserChat", "会议室【" + roomsName + "】加入成功........");
			return muc;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("MultiUserChat", "会议室【" + roomsName + "】加入失败........");
			return null;
		}
	}

	/**
	 * 查询会议室成员名字
	 *
	 * @param muc
	 * @return
	 */
	public List<String> findMulitUser(MultiUserChat muc) {
		if (mXMPPConnection == null)
			return null;
		List<String> listUser = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants();
		// 遍历出聊天室人员名称
		while (it.hasNext()) {
			// 聊天室成员名字
			String name = StringUtils.parseResource(it.next());
			listUser.add(name);
		}
		return listUser;
	}

	/**
	 * 发送文件
	 *
	 * @param user
	 * @param filePath
	 */
	public void sendFile(String user, String filePath) {
		if (mXMPPConnection == null)
			return;
		// 创建文件传输管理器
		FileTransferManager manager = new FileTransferManager(mXMPPConnection);
		// 创建输出的文件传输
		OutgoingFileTransfer transfer = manager
				.createOutgoingFileTransfer(user);
		// 发送文件
		try {
			transfer.sendFile(new File(filePath), "You won't believe this!");
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 语音接收文件监听接收文件
	 */
	public void receivedFile() {

		// Create the file transfer manager

		final FileTransferManager manager = new FileTransferManager(
				mXMPPConnection);

		/*
		 * System.out.println("接收语音文件开始"); if (mfiletransfransferlistener !=
		 * null){
		 * 
		 * //如果监听存在就删除监听
		 * manager.removeFileTransferListener(mfiletransfransferlistener); }
		 */

		mfiletransfransferlistener = new FileTransferListener() {
			public void fileTransferRequest(FileTransferRequest request) {
				// Check to see if the request should be accepted

				System.out.println("接收语音文件");
				// Accept it
				IncomingFileTransfer transfer = request.accept();
				try {

					File file = new File(RECORD_ROOT_PATH + "/"
							+ request.getFileName());

					System.out.println(request.getFileName() + "接收路径"
							+ file.getPath() + "接收语音文件名称" + file.exists());

					transfer.recieveFile(file);

				} catch (XMPPException e) {
					e.printStackTrace();
				}

			}
		};

		manager.addFileTransferListener(mfiletransfransferlistener);

	}

	/**
	 * 获取离线消息
	 *
	 * @return
	 */
	public Map<String, List<HashMap<String, String>>> getHisMessage() {
		if (mXMPPConnection == null)
			return null;
		Map<String, List<HashMap<String, String>>> offlineMsgs = null;
		try {
			OfflineMessageManager offlineManager = new OfflineMessageManager(
					mXMPPConnection);
			Iterator<Message> it = offlineManager.getMessages();
			int count = offlineManager.getMessageCount();
			if (count <= 0)
				return null;
			offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();
			while (it.hasNext()) {
				Message message = it.next();
				String fromUser = StringUtils.parseName(message.getFrom());
				HashMap<String, String> histrory = new HashMap<String, String>();
				histrory.put("useraccount",
						StringUtils.parseName(mXMPPConnection.getUser()));
				histrory.put("friendaccount", fromUser);
				histrory.put("info", message.getBody());
				histrory.put("type", "left");
				if (offlineMsgs.containsKey(fromUser)) {
					offlineMsgs.get(fromUser).add(histrory);
				} else {
					List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
					temp.add(histrory);
					offlineMsgs.put(fromUser, temp);
				}
			}
			offlineManager.deleteMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return offlineMsgs;
	}

	/**
	 * 判断OpenFire用户的状态
	 *
	 * strUrl : url格式
	 * -http://my.openfire.com:9090/plugins/presence/status?jid=user1
	 *
	 * @SERVER_NAME&type=xml
	 *
	 *                       返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
	 *
	 *                       说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
	 */
	public int IsUserOnLine(String user) {
		String url = "http://"
				+ SharedPreferencesUtils.getString(mContext, Const.XMPP_HOST,
				"192.168.1.100")
				+ ":9090/plugins/presence/status?"
				+ "jid="
				+ user
				+ "@"
				+ SharedPreferencesUtils.getString(mContext,
				Const.XMPP_RESSOURCE, "testxmpp") + "&type=xml";
		int shOnLineState = 0;
		// 不存在
		try {
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(
						oConn.getInputStream()));
				if (null != oIn) {
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag" + strFlag);
					if (strFlag.contains("type=\"unavailable\"")) {
						shOnLineState = 2;
					}
					if (strFlag.contains("type=\"error\"")) {
						shOnLineState = 0;
					} else if (strFlag.contains("priority")
							|| strFlag.contains("id=\"") ) {
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shOnLineState;
	}

	/**
	 * 消息线程,负责向数据库添加数据
	 *
	 * @author Administrator
	 *
	 */
//	private class QueueThread extends Thread {
//		private DataEngine mEngine;
//		private XMPPMessageQueueInfo info;
//
//		public QueueThread(DataEngine mEngine) {
//			this.mEngine = mEngine;
//		}
//
//		@Override
//		public void run() {
//			while (isQueueThreadStarted) {
//				// 从消息队列中取得消息
//				try {
//					info = queue.take();
//
//					switch (info.getClassType()) {
//						case QUEUE_CLASS_TYPE_ACCOUNT:
//							accountChange();
//							break;
//						case QUEUE_CLASS_TYPE_MESSAGE:
//							messageChange();
//							break;
//						case QUEUE_CLASS_TYPE_PRESENCE:
//							presenceChange();
//							break;
//						case QUEUE_CLASS_TYPE_ROSTERS:
//							rostersChange();
//							break;
//					}
//
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}

//		private void rostersChange() {
//			// 好友改变
//			Collection<String> rosters = info.getRosters();
//
//			switch (info.getType()) {
//				case QUEUE_TYPE_USER_ADD:
//					for (String roster : rosters) {
//						RosterEntry rosterEntry = mRoster.getEntry(roster);
//						mEngine.addXMPPUser(getXMPPUser(rosterEntry));
//					}
//					break;
//				case QUEUE_TYPE_USER_DELETE:
//					for (String roster : rosters) {
//						RosterEntry rosterEntry = mRoster.getEntry(roster);
//						mEngine.deleteXMPPUser(rosterEntry.getmUser());
//					}
//					break;
//				case QUEUE_TYPE_USER_UPDATE:
//					for (String roster : rosters) {
//						RosterEntry rosterEntry = mRoster.getEntry(roster);
//						mEngine.updateXMPPUser(getXMPPUser(rosterEntry));
//					}
//					break;
//			}
//
//			userChangedIntent();
//		}
//
//		private void presenceChange() {
//			// 好友状态改变
//			Presence presence = info.getPresence();
//			String jabberID = getJabberID(presence.getFrom());
//			RosterEntry rosterEntry = mRoster.getEntry(jabberID);
//			mEngine.updateXMPPUser(getXMPPUser(rosterEntry));
//
//			userChangedIntent();
//		}
//
//		private void messageChange() {
//			// 接收到新消息
//			XMPPMessage xmppMsg = info.getMessage();
//			mEngine.addNewXMPPMessge(xmppMsg);
//
//			messageChangedIntent();
//		}
//
//		private void accountChange() {
//			// 发送消息失败,收到消息回执
//			String account = info.getAccount();
//
//			switch (info.getType()) {
//				case QUEUE_TYPE_MESSAGE_SEND:
//					// 消息发送
//					mEngine.updateXMPPMessageState(account,
//							DBColumns.MESSAGE_STATE_SENDED);
//					break;
//				case QUEUE_TYPE_MESSAGE_SEND_FAIL:
//					// 发送失败
//					mEngine.updateXMPPMessageState(account,
//							DBColumns.MESSAGE_STATE_FAIL);
//					break;
//				case QUEUE_TYPE_MESSAGE_SEND_RECEIVED:
//					// 收到消息回执
//					mEngine.updateXMPPMessageState(account,
//							DBColumns.MESSAGE_STATE_RECEIVED);
//					break;
//			}
//
//			messageChangedIntent();
//		}
//
//		private void userChangedIntent(){
//			Intent intent = new Intent(ChatService.USER_DATA_CHANGED);
//			mContext.sendBroadcast(intent);
//		}
//
//		private void messageChangedIntent(){
//			Intent intent = new Intent(ChatService.MESSAGE_DATA_CHANGED);
//			mContext.sendBroadcast(intent);
//		}
//
//		private XMPPUser getXMPPUser(RosterEntry rosterEntry) {
//			XMPPUser mUser = new XMPPUser();
//			mUser.setJid(rosterEntry.getmUser());
//			mUser.setNickname(rosterEntry.getName());
//			mUser.setGroup(getGroup(rosterEntry.getGroups()));
//
//			Presence presence = mRoster.getPresence(rosterEntry.getmUser());
//			mUser.setStatusMode(getStatusInt(presence));
//			mUser.setStatusMessage(presence.getStatus());
//
//			return mUser;
//		}
//
//		private StatusMode getStatus(Presence presence) {
//			if (presence.getType() == Presence.Type.available) {
//				if (presence.getMode() != null) {
//					return StatusMode.valueOf(presence.getMode().name());
//				}
//				return StatusMode.available;
//			}
//			return StatusMode.offline;
//		}
//
//		private int getStatusInt(final Presence presence) {
//			return getStatus(presence).ordinal();
//		}
//
//		private String getGroup(Collection<RosterGroup> groups) {
//			for (RosterGroup group : groups) {
//				return group.getName();
//			}
//			return "我的好友";
//		}
//
//	}
}