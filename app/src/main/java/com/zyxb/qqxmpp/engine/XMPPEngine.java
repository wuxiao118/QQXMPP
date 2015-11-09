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
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.util.AppShortCutUtil;
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
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.carbons.Carbon;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.DelayInfo;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
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
 *         <p/>
 *         使用MessageQueen进行用户合消息的CRUD操作
 *         <p/>
 *         更改:
 *         MessageQueen大材小用，没必要，去掉
 *         再需要对数据库操作的地方，直接增加数据操作
 *         <p/>
 *         发送消息不正确原因:
 *         添加测试好友,jid name@domain
 *         ip貌似不行,因为ip只是内网ip且没有配置映射??
 *         手机登陆需使用ip,domain不能正确解析??
 *         <p/>
 *         功能基本正确,存在问题,setPresence因为使用ip,本地服务器domain为电脑名,状态不能正确转换
 *         ip--->domain,能否自动转换??
 *         <p/>
 *         要解决问题:
 *         信息更新(用户、消息)  只完成新消息的添加和消息状态的改变,删除未做  OK 1/2
 *         <p/>
 *         好友状态不能更新(在线显示但是没有收到在线presence)  OK
 *         将openfire中的当前user的每一个roster的subcribtion属性改为both。
 *         其含义是：好友双方可以互相订阅对方的状态，这样当好友的一方上线之后，就会通知好友的另一方，也就可以获取到其在线状态了
 *         <p/>
 *         将耗时或者可能导致ANR的代码改为线程
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
    //public static final String XMPP_USER_LOGOUT = "com.zyxb.qqxmpp.USER_LOGOUT";

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
//        // add IQ handling
//        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
//                new DiscoverInfoProvider());
//        // add delayed delivery notifications
//        pm.addExtensionProvider("delay", "urn:xmpp:delay",
//                new DelayInfoProvider());
//        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInfoProvider());
//        // add carbons and forwarding
//        pm.addExtensionProvider("forwarded", Forwarded.NAMESPACE,
//                new Forwarded.Provider());
//        pm.addExtensionProvider("sent", Carbon.NAMESPACE, new Carbon.Provider());
//        pm.addExtensionProvider("received", Carbon.NAMESPACE,
//                new Carbon.Provider());
//        // User Search
//        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
//        // add delivery receipts
//        pm.addExtensionProvider(DeliveryReceipt.ELEMENT,
//                DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
//        pm.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,
//                DeliveryReceipt.NAMESPACE,
//                new DeliveryReceiptRequest.Provider());
//        // add XMPP Ping (XEP-0199)
//        pm.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());

        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient",
                    "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());

        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());

        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired",
                "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());


        ServiceDiscoveryManager.setIdentityName(XMPP_IDENTITY_NAME);
        ServiceDiscoveryManager.setIdentityType(XMPP_IDENTITY_TYPE);
    }

    private DataEngine mDataEngine;
    //private ConnectionConfiguration mXMPPConf;

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

    //单例,当更换服务器时,需要重新初始化mEngine
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
    public void clear() {
        mEngine = null;
        //mXMPPConnection = null;
    }

    /**
     * 连接配置
     *
     * @param context 上下文
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

//        if (customServer.length() > 0 || pt != Const.XMPP_PORT_DEFAULT)
//            this.mXMPPConf = new ConnectionConfiguration(customServer, pt,
//                    server);
//        else
//            this.mXMPPConf = new ConnectionConfiguration(server); // use SRV
//
//        this.mXMPPConf.setReconnectionAllowed(false);
//        this.mXMPPConf.setSendPresence(false);
//        this.mXMPPConf.setCompressionEnabled(false); // disable for now
//        this.mXMPPConf.setDebuggerEnabled(smackdebug);
//        if (requireSsl)
//            this.mXMPPConf
//                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required);
//
//        mXMPPConnection = new XMPPConnection(mXMPPConf);

        ConnectionConfiguration xmppConf;
        if (customServer.length() > 0 || pt != Const.XMPP_PORT_DEFAULT)
            xmppConf = new ConnectionConfiguration(customServer, pt,
                    server);
        else
            xmppConf = new ConnectionConfiguration(server); // use SRV

        xmppConf.setReconnectionAllowed(false);
        xmppConf.setSendPresence(false);
        xmppConf.setCompressionEnabled(false); // disable for now
        xmppConf.setDebuggerEnabled(smackdebug);
        if (requireSsl)
            xmppConf
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required);

        mXMPPConnection = new XMPPConnection(xmppConf);
    }

//    public XMPPEngine(Context context, String host, boolean smackDebug,
//                      boolean requireSsl) {
//        mContext = context;
//        this.mXMPPConf = new ConnectionConfiguration(host); // use SRV
//
//        this.mXMPPConf.setReconnectionAllowed(false);
//        this.mXMPPConf.setSendPresence(false);
//        this.mXMPPConf.setCompressionEnabled(false);
//        this.mXMPPConf.setDebuggerEnabled(smackDebug);
//        if (requireSsl)
//            this.mXMPPConf
//                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required);
//
//        mXMPPConnection = new XMPPConnection(mXMPPConf);
//    }

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

            //获取好友在线状态,不需要
            //setFriendStatus();

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

    //好友状态
    private void setFriendStatus() {
        Roster roster = mXMPPConnection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        //Collection<RosterGroup> entriesGroup = roster.getGroups();

        //下面通过设置sleep(1000)使程序暂定1秒钟，来获取其好友列表及好友的在线状态；
        //如果不设置sleep(1000)，则程序获取在线好友会不稳定，有时可以获取到，有时获取不到。
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (RosterEntry entry : entries) {
            //获取好友在线状态
            Presence presence = roster.getPresence(entry.getUser());
            //User user = new User();
            //user.setName(entry.getName());
            //user.setUser(entry.getUser());
            //user.setType(entry.getType());
            //user.setSize(entry.getGroups().size());
            //user.setStatus(presence.getStatus());
            //user.setFrom(presence.getFrom());
            //userInfos.add(user);
            //Toast.makeText(FriendsActivity.this, "User:" + entry.getUser(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(FriendsActivity.this, "isAvailable:" + presence.isAvailable(), Toast.LENGTH_SHORT).show();
            String jid = entry.getUser();
            if (presence.isAvailable() == true)   //判断好友是否在线
            {
                //相应的逻辑操作
            }
            Logger.d(TAG, "好友:" + jid + " " + (presence.isAvailable() ? "在线" : "不在线"));
        }

    }

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

                // 更新联系人状态
                String jabberID = getJabberID(presence.getFrom());
                //RosterEntry rosterEntry = mRoster.getEntry(jabberID);

                //获取自己jid


                //仅更新用户对应好友的状态
                //mDataEngine.updateXMPPUserState(jabberID,status);
                mDataEngine.updateXMPPFriendState(jabberID, getFriendStatus(presence));
                // 更新好友状态
                sendUserChangedIntent(ChatService.USER_UPDATE);
            }

            @Override
            public void entriesUpdated(final Collection<String> entries) {
                try {
                    mCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Logger.i(TAG, "entriesUpdated(" + entries + ")");

                // 更新联系人
                for (String entry : entries) {
                    RosterEntry rosterEntry = mRoster.getEntry(entry);
                    Logger.i(TAG, rosterEntry.getUser() + "," + rosterEntry.getName() + "," + rosterEntry.getGroups() + ","
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

                // 删除联系人
                for (String entry : entries) {
                    RosterEntry rosterEntry = mRoster.getEntry(entry);
                    Logger.i(TAG, rosterEntry.getUser() + "," + rosterEntry.getName() + "," + rosterEntry.getGroups() + ","
                            + rosterEntry.getStatus() + "," + rosterEntry.getType());
                    //仅删除用户与好友的对应关系
                    mDataEngine.deleteXMPPUser(rosterEntry.getUser());
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

                //添加联系人
                for (String entry : entries) {
                    RosterEntry rosterEntry = mRoster.getEntry(entry);
                    mDataEngine.addXMPPUser(getXMPPUser(rosterEntry));

                    //用户消息改变broadcast
//                    Intent userChangedIntent = new Intent();
//                    userChangedIntent.setAction(ChatService.USER_DATA_CHANGED);
//                    mContext.sendBroadcast(userChangedIntent);
                    sendUserChangedIntent(ChatService.USER_ADD);
                }
            }

        };
        mRoster.addRosterListener(mRosterListener);
    }

    //获取好友状态，转换为自定义的类型
    private int getFriendStatus(Presence presence) {
        Presence.Mode mode = presence.getMode();
        String status = presence.getStatus();
        if (status == null) status = "";
        Presence.Type type = presence.getType();
        Logger.d(TAG, "mode:" + mode + ",status" + status + ",type:" + type);//null 在线 avaliable

//           if (mode == Mode.available) {
//               if(mode == Mode.away){
//                   //离开
//                   return Const.LOGIN_STATE_ALWAY;
//               }else if(mode == Mode.chat){
//                   //聊天
//                   return Const.LOGIN_STATE_LEAVE_MESSAGE;
//               }else if(mode == Mode.dnd){
//                   //请勿打扰
//                   return Const.LOGIN_STATE_BUSY;
//               }else if(mode == Mode.xa){
//                   //Away for an extended period of time
//                   return Const.LOGIN_STATE_ALWAY;
//               }
//
//               return Const.LOGIN_STATE_ONLINE;
//           }
        if (type == Presence.Type.available) {
            //在线
            if (mode == Mode.away) {
                //离开
                return Const.LOGIN_STATE_ALWAY;
            } else if (mode == Mode.chat) {
                //聊天
                return Const.LOGIN_STATE_LEAVE_MESSAGE;
            } else if (mode == Mode.dnd) {
                //请勿打扰
                return Const.LOGIN_STATE_BUSY;
            } else if (mode == Mode.xa) {
                //Away for an extended period of time
                return Const.LOGIN_STATE_ALWAY;
            }

            return Const.LOGIN_STATE_ONLINE;
        } else if (type == Presence.Type.unavailable) {
            //不在线

            //TODO 获取隐身状态
        }


        return Const.LOGIN_STATE_OFFLINE;
    }

    private XMPPUser getXMPPUser(RosterEntry rs) {
        XMPPUser xmppUser = new XMPPUser();
        String jid = rs.getUser();
        xmppUser.setJid(jid);
        String nickname = rs.getName();
        xmppUser.setNickname(nickname);

        //获取domain,并设置,不行,所有好友,可能不是当前服务器的,要在状态改变中获取自己的jid才行
//        String domain = SharedPreferencesUtils.getString(mContext, Const.XMPP_DOMAIN, "");
//        String serverDomain = jid.substring(jid.indexOf("@") + 1);
//        if (!domain.equals(serverDomain)) {
//            SharedPreferencesUtils.setString(mContext, Const.XMPP_DOMAIN, serverDomain);
//        }

        //Logger.d(TAG,"jid:" + jid + ",nickname:" + nickname);
        Collection<RosterGroup> groups = rs.getGroups();
        for (RosterGroup group : groups) {
            //Logger.d(TAG,group.getName() + "");
            xmppUser.setGroup(group.getName());
            break;
        }

        //String status = rs.getStatus().toString();
        //String type = rs.getType().toString();
        //Logger.d(TAG,"status:" + status + ",type:" + type);

        RosterPacket.ItemStatus itemStatus = rs.getStatus();
        RosterPacket.ItemType itemType = rs.getType();
        Logger.d(TAG, "jid:" + jid + ",status:" + itemStatus + ",type:" + itemType);

        return xmppUser;
    }

    //private void sendUserChangedIntent(){
    private void sendUserChangedIntent(int reason) {
        //用户信息改变broadcast
        Intent userChangedIntent = new Intent();
        userChangedIntent.putExtra("reason", reason);
        userChangedIntent.setAction(ChatService.USER_DATA_CHANGED);
        mContext.sendBroadcast(userChangedIntent);
    }

    //private void sendMessageChangedIntent(){
    private void sendMessageChangedIntent(int reason) {
        //消息改变
        Intent messageChangedIntent = new Intent();
        messageChangedIntent.putExtra("reason", reason);
        messageChangedIntent.setAction(ChatService.MESSAGE_DATA_CHANGED);
        mContext.sendBroadcast(messageChangedIntent);
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

                // 修改消息状态为对方已经收到
                mDataEngine.updateXMPPMessageState(receiptId, DBColumns.MESSAGE_STATE_RECEIVED);
                sendMessageChangedIntent(ChatService.MESSAGE_UPDATE);
            }
        });
    }

    /**
     * 更新在线状态
     */
    private void setStatusFromConfig() {
        Logger.d(TAG, "Set status available");

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
            // 文件接收
            //receivedFile();
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

        Logger.d(TAG, "register message listener");

        mPacketListener = new PacketListener() {
            public void processPacket(Packet packet) {
                try {
                    if (packet instanceof Message) {
                        Message msg = (Message) packet;
                        String chatMessage = msg.getBody();

                        Logger.d(TAG, "message body:" + chatMessage);

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

                            // 发送新的消息处理(加入数据库,发送消息改变receiver),不用,发送时已经存入数据库

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

                        // 接收到新的消息处理(加入数据库,发送消息改变receiver)
                        XMPPMessage xmppMsg = new XMPPMessage();
                        xmppMsg.setCreateTime(ts);
                        xmppMsg.setFrom(fromJID);
                        xmppMsg.setMsg(chatMessage);
                        xmppMsg.setMsgType(DBColumns.MESSAGE_TYPE_CONTACT);
                        xmppMsg.setState(DBColumns.MESSAGE_STATE_RECEIVED);
                        mDataEngine.addNewXMPPMessge(xmppMsg);
                        sendMessageChangedIntent(ChatService.MESSAGE_ADD);

                        //更新图标消息数
                        AppShortCutUtil.addNumShortCut(mContext, null, true, mDataEngine.getUnReadedMessage() + "", true);

                        //发送收到新消息广播
                        //Intent newMsgIntent = new Intent();
                        //newMsgIntent.setAction(ChatService.NEW_MESSAGE);
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
                        //消息发送失败
                        //修改消息状态
                        //mDataEngine.changeXMPPMessageState(msg.getPacketID(), DBColumns.MESSAGE_STATE_FAIL);
                        mDataEngine.updateXMPPMessageState(msg.getPacketID(), DBColumns.MESSAGE_STATE_FAIL);
                        sendMessageChangedIntent(ChatService.MESSAGE_UPDATE);
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
     * @return 是否连接成功
     */
    public boolean isAuthenticated() {
//        if (mXMPPConnection != null) {
//            return (mXMPPConnection.isConnected() && mXMPPConnection
//                    .isAuthenticated());
//        }
//        return false;

//        return mXMPPConnection == null ? false : (mXMPPConnection.isConnected() && mXMPPConnection
//                .isAuthenticated());

        return mXMPPConnection != null && mXMPPConnection.isConnected() && mXMPPConnection.isAuthenticated();
    }

    /**
     * 获取jid
     *
     * @param from JID字符串
     * @return JID ID
     */
    private String getJabberID(String from) {
        String[] res = from.split("/");
        return res[0].toLowerCase();
    }

    /******************************* 登陆后变化结束 ******************************************/

    /**
     * 注销
     *
     * @return 是否成功登出
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
     * @param account  注册帐号
     * @param password 注册密码
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
     * @param toJID   消息发送对象
     * @param message 判断消息发送成功与否
     *                这里的“成功”有两个概念，一个是数据是否成功到达服务器，一个是服务器是否成功接收。
     *                <p/>
     *                一般如果消息没有到达服务器，android-xmpp的sendMessage会返回error。
     *                另一个服务器是否发送成功，则需要回执处理了，比如发送了消息<message id="a-xxx" /> ，服务器给个回执告诉客户端a-xxx已经成功接收了，
     *                这时候才是成功，如果超时未接收到回执，那就是失败了。
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

            //修改消息状态
            //mDataEngine.changeXMPPMessageState(account, DBColumns.MESSAGE_STATE_SENDED);
            mDataEngine.updateXMPPMessageState(account, DBColumns.MESSAGE_STATE_SENDED);
            sendMessageChangedIntent(ChatService.MESSAGE_UPDATE);
        } else {
            Logger.d(TAG, "send message failed,authenticate failed");
        }
    }

    /**
     * 更改用户状态
     *
     * @param code 状态
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
        List<RosterGroup> groupList = new ArrayList<RosterGroup>();
        Collection<RosterGroup> rosterGroup = mXMPPConnection.getRoster()
                .getGroups();
//        Iterator<RosterGroup> i = rosterGroup.iterator();
//        while (i.hasNext()) {
//            groupList.add(i.next());
//        }
        for (RosterGroup i : rosterGroup) {
            groupList.add(i);
        }

        return groupList;
    }

    /**
     * 获取某个组里面的所有好友
     *
     * @param groupName 组名
     * @return 组名对应的所有好友
     */

    public List<RosterEntry> getEntriesByGroup(String groupName) {
        if (mXMPPConnection == null)

            return null;
        List<RosterEntry> entriesList = new ArrayList<RosterEntry>();
        RosterGroup rosterGroup = mXMPPConnection.getRoster().getGroup(
                groupName);
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
//        Iterator<RosterEntry> i = rosterEntry.iterator();
//        while (i.hasNext()) {
//            entriesList.add(i.next());
//        }
        for (RosterEntry i : rosterEntry) {
            entriesList.add(i);
        }

        return entriesList;
    }

    /**
     * 获取所有好友信息
     *
     * @return 所有好友
     */
    public List<RosterEntry> getAllEntries() {
        if (mXMPPConnection == null)
            return null;
        List<RosterEntry> entriesList = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntry = mXMPPConnection.getRoster()
                .getEntries();
//        Iterator<RosterEntry> i = rosterEntry.iterator();
//        while (i.hasNext()) {
//            entriesList.add(i.next());
//        }
        for (RosterEntry i : rosterEntry) {
            entriesList.add(i);
        }

        return entriesList;
    }

    /**
     * 获取用户VCard信息
     *
     * @param user 用户
     * @return 用户VCard
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
     * @param user 用户
     * @return 头像drawable
     */
    public Drawable getUserImage(String user) {
        if (mXMPPConnection == null)
            return null;
        ByteArrayInputStream byteArrayInputStream;
        try {
            VCard vcard = new VCard();
            // 加入这句代码，解决No VCard for
            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
                    new org.jivesoftware.smackx.provider.VCardProvider());
            //if (user == "" || user == null || user.trim().length() <= 0) {
            if (user == null || user.equals("") || user.trim().length() <= 0) {
                return null;
            }
            vcard.load(mXMPPConnection,
                    user + "@" + mXMPPConnection.getServiceName());
//            if (vcard == null || vcard.getAvatar() == null)
            if (vcard.getAvatar() == null)
                return null;
            byteArrayInputStream = new ByteArrayInputStream(vcard.getAvatar());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // input stream to drawable
        //BitmapDrawable drawable = new BitmapDrawable(byteArrayInputStream);

        //return drawable;
        return new BitmapDrawable(byteArrayInputStream);
    }

    /**
     * 添加一个分组
     *
     * @param groupName 分组名称
     * @return 是否添加成功
     */
    public boolean addGroup(String groupName) {
        if (mXMPPConnection == null)
            return false;
        try {
            mXMPPConnection.getRoster().createGroup(groupName);
            Log.v("addGroup", groupName + "創建成功");

            // 写入本地数据库
            mDataEngine.addNewFriendGroup(groupName);
            //发送好友信息变化
            sendUserChangedIntent(ChatService.USER_UPDATE);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除分组
     *
     * @param groupName 分组名称
     * @return 是否删除分组成功
     */
    public boolean removeGroup(String groupName) {
        return true;
    }

    /**
     * 添加好友 无分组
     *
     * @param userName 好友名称
     * @param name     备注名称
     * @return 是否添加成功
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
     * @param userName  好友名称
     * @param name      备注
     * @param groupName 分组名称
     * @return 是否添加成功
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
                    new String[]{groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除好友
     *
     * @param userName 好友名称
     * @return 是否删除成功
     */
    public boolean removeUser(String userName) {
        if (mXMPPConnection == null)
            return false;
        try {
            RosterEntry entry;
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
     * @param userName 用户名称
     * @return 是否存在
     */
    //public List<HashMap<String, String>> searchUsers(String userName) {
    public ArrayList<String> searchUsers(String userName) {
        if (mXMPPConnection == null)
            return null;
        //HashMap<String, String> user;
        //List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        ArrayList<String> results = new ArrayList<String>();
        try {
//            new ServiceDiscoveryManager(mXMPPConnection);
//            UserSearchManager usm = new UserSearchManager(mXMPPConnection);
//            Form searchForm = usm.getSearchForm(mXMPPConnection
//                    .getServiceName());
//            Form answerForm = searchForm.createAnswerForm();
//            answerForm.setAnswer("userAccount", true);
//            answerForm.setAnswer("userPhote", userName);
//            ReportedData data = usm.getSearchResults(answerForm, "search"
//                    + mXMPPConnection.getServiceName());
//            Iterator<Row> it = data.getRows();
//            Row row;
//            while (it.hasNext()) {
//                user = new HashMap<String, String>();
//                row = it.next();
//                user.put("userAccount", row.getValues("userAccount").next()
//                        .toString());
//                user.put("userPhote", row.getValues("userPhote").next()
//                        .toString());
//                results.add(user);
//                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
//
//                Logger.d(TAG,"userAccount:" + user.get("userAccount") + ",userPhote:" + user.get("userPhote"));
//            }

            UserSearchManager search = new UserSearchManager(mXMPPConnection);
            //此处一定要加上 search.
            Form searchForm = search.getSearchForm("search." + mXMPPConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = search.getSearchResults(answerForm, "search." + mXMPPConnection.getServiceName());
            Iterator<Row> it = data.getRows();
            Row row;
            while (it.hasNext()) {
                //user = new HashMap<String, String>();
                row = it.next();
                //user.put("username", row.getValues("Username").next().toString() + mXMPPConnection.getHost());
                //results.add(user);
                //Logger.d(TAG, "username:" + user.get("username"));
//                String username = row.getValues("Username").next().toString() + "@" + SharedPreferencesUtils.getString(mContext, Const.XMPP_DOMAIN, "");
                String username = row.getValues("Username").next().toString() + "@" + mXMPPConnection.getServiceName();
                results.add(username);

                //Logger.d(TAG, "username:" + username);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 修改心情
     *
     * @param status 更改后的状态
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
     * @param file 图像文件
     * @return 是否更改成功
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
     * @param file 文件
     * @return 转换后的字节
     * @throws IOException IO异常
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
     * @return 是否删除成功
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
     * @param pwd 修改后的密码
     * @return 是否修改成功
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
     * @return 会议室列表
     */
    public List<HostedRoom> getHostRooms() {
        if (mXMPPConnection == null)
            return null;
        Collection<HostedRoom> hostRooms;
        List<HostedRoom> rooms = new ArrayList<HostedRoom>();
        try {
            new ServiceDiscoveryManager(mXMPPConnection);
            hostRooms = MultiUserChat.getHostedRooms(mXMPPConnection,
                    mXMPPConnection.getServiceName());
            for (HostedRoom entry : hostRooms) {
                rooms.add(entry);
                Log.i("room",
                        "名字：" + entry.getName() + " - ID:" + entry.getJid());
            }
            Log.i("room", "服务会议数量:" + rooms.size());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * 创建房间
     *
     * @param user     创建者
     * @param roomName 房间名称
     * @param password 房间密码
     * @return 创建的多人聊天实例
     */
    public MultiUserChat createRoom(String user, String roomName,
                                    String password) {
        if (mXMPPConnection == null)
            return null;
        MultiUserChat muc;
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
                    .hasNext(); ) {
                //FormField field = (FormField) fields.next();
                FormField field = fields.next();
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
     * @param user      昵称
     * @param password  会议室密码
     * @param roomsName 会议室名
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
     * @param muc 多人会议实例
     * @return 会议室成员列表
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
     * @param user     文件发送对象
     * @param filePath 文件路径
     */
    //public void sendFile(String user, String filePath) {
    public void sendFile(String id, String user, String filePath) {
        Logger.d(TAG, "send file:" + filePath + " to:" + user);
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

            //Logger.d(TAG, "send file status:" + transfer.getStatus());
            //监听文件是否接受
            long startTime = -1;
            while (!transfer.isDone()) {
                //Logger.d(TAG,"current status:" + transfer.getStatus());

                if (transfer.getStatus().equals(FileTransfer.Status.error)) {
                    Logger.d(TAG, "error!!!" + transfer.getError());
                } else {
                    double progress = transfer.getProgress();
                    if (progress > 0.0 && startTime == -1) {
                        startTime = System.currentTimeMillis();
                    }
                    progress *= 100;
                    Logger.d(TAG, "status=" + transfer.getStatus());
                    Logger.d(TAG, "progress=" + progress + "%");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //文件对方是接受还是拒绝
            //Logger.d(TAG,"done status:" + transfer.getStatus());
            if(transfer.getStatus().equals(FileTransfer.Status.refused)){
                //拒绝
                transfer.cancel();
                mDataEngine.updateXMPPMessageState(id, DBColumns.MESSAGE_STATE_CANCELED);
            }else if(transfer.getStatus().equals(FileTransfer.Status.complete)) {
                //修改文件为已经发送
                //Logger.d(TAG, "send file msg id:" + id);
                //不行,chat activity更新消息时，会将received消息修改为readed
                //mDataEngine.updateXMPPMessageState(id, DBColumns.MESSAGE_STATE_RECEIVED);
                mDataEngine.updateXMPPMessageState(id, DBColumns.MESSAGE_STATE_SENDED);
            }

            //更新消息列表
            Intent intent = new Intent();
            intent.setAction(ChatService.MESSAGE_DATA_CHANGED);
            mContext.sendBroadcast(intent);
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

                //TODO 判断文件类型，如果是普通文件,需要确认是否接收
                String fileName = request.getFileName();
                String fileSize = request.getFileSize() + "";

                // Accept it 如果是语音或者图片,直接接收
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
        Map<String, List<HashMap<String, String>>> offlineMessages = null;
        try {
            OfflineMessageManager offlineManager = new OfflineMessageManager(
                    mXMPPConnection);
            Iterator<Message> it = offlineManager.getMessages();
            int count = offlineManager.getMessageCount();
            if (count <= 0)
                return null;
            offlineMessages = new HashMap<String, List<HashMap<String, String>>>();
            while (it.hasNext()) {
                Message message = it.next();
                String fromUser = StringUtils.parseName(message.getFrom());
                HashMap<String, String> histrory = new HashMap<String, String>();
                histrory.put("useraccount",
                        StringUtils.parseName(mXMPPConnection.getUser()));
                histrory.put("friendaccount", fromUser);
                histrory.put("info", message.getBody());
                histrory.put("type", "left");
                if (offlineMessages.containsKey(fromUser)) {
                    offlineMessages.get(fromUser).add(histrory);
                } else {
                    List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
                    temp.add(histrory);
                    offlineMessages.put(fromUser, temp);
                }
            }
            offlineManager.deleteMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offlineMessages;
    }

    /**
     * 判断OpenFire用户的状态
     * <p/>
     * strUrl : url格式
     * -http://my.openfire.com:9090/plugins/presence/status?jid=user1
     *
     * @SERVER_NAME&type=xml 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
     * <p/>
     * 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
     */
    public int IsUserOnLine(String user) {
        String url = "http://"
                + SharedPreferencesUtils.getString(mContext, Const.XMPP_HOST,
                "192.168.1.101")
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
                            || strFlag.contains("id=\"")) {
                        shOnLineState = 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shOnLineState;
    }
}