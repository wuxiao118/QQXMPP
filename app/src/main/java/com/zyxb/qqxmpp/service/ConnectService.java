package com.zyxb.qqxmpp.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.zyxb.qqxmpp.engine.XMPPEngine;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.NetUtil;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ping.packet.Ping;

import java.util.concurrent.CountDownLatch;

/**
 * 负责服务器连接,断线重连
 *
 * @author 吴小雄
 *         <p/>
 *         太复杂，逻辑不清晰,重新整理
 *         1.连接及重连还有服务器连接改变，在service内部判断
 *         2.service正确响应开启和关闭服务
 *         <p/>
 *         要解决问题:
 *         如何确保关闭之后在重新开启连接
 *         思路一:xmppconnection为静态,
 *         不确保的话,会导致新开启后，如果关闭线程还在运行，NullPointerException
 *         思路二:xmppconnection普通成员变量,不好，
 *         xmppconnection初始化较复杂,占用资源多,不适合多个
 */
@SuppressLint("DefaultLocale")
public class ConnectService extends Service {
    private static final String TAG = "ConnectService";
    // 连接,用户名密码错误,服务器无应答,网络连接断开
    public static final int SERVER_CONNECTED = 0;
    public static final int SERVER_DISCONNECTED = 3;
    public static final int SERVER_DISCONNECTED_NOT_RESPONSE = 4;
    public static final int SERVER_DISCONNECTED_NET_DISCONNECTED = 5;
    public static final int SERVER_DISCONNECTED_PING_TIMEOUT = 6;
    public static final int SERVER_DISCONNECTED_CONNECTION_ERROR = 7;
    public static final int SERVER_DISCONNECTED_CONNECTION_CLOSED = 8;
    public static final int SERVER_DISCONNECTED_FAIL = 9;
    public static final int SERVER_DISCONNECTED_REMOTE_SERVER_ERROR = 10;
    public static final int SERVER_DISCONNECTED_EXCEPTION = 11;
    public static final String LOGIN_SERVER_CONNECTED = "com.zyxb.qqxmpp.SERVER_CONNECTED";
    public static final String LOGIN_SERVER_DISCONNECTED = "com.zyxb.qqxmpp.SERVER_DISCONNECTED";
    public static final String LOGIN_SERVER_RECONNECT = "com.zyxb.qqxmpp.SERVER_RECONNECT";
    private static final int PACKET_TIMEOUT = 30000;

    // 重连
    private static int RECONNECT_TIMES = 1;
    private static final int MAX_RECONNECT_TIMES = 3;
    private static final int RECONNECT_AFTER = 5;
    private static final int RECONNECT_MAXIMUM = 10 * 60;// 最大重连时间间隔
    private static final String RECONNECT_ALARM = "com.zyxb.qqxmpp.RECONNECT_ALARM";
    private int mConnectedState = SERVER_DISCONNECTED; // 是否已经连接
    private int mReconnectTimeout = RECONNECT_AFTER;
    private Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
    private PendingIntent mPAlarmIntent;
    private BroadcastReceiver mAlarmReceiver = new ReconnectAlarmReceiver();
    private Thread mConnectingThread;
    private static int SERVER_STATE_REASON = SERVER_DISCONNECTED_NET_DISCONNECTED;
    // 是否服务开启正在重连中
    private static boolean isRunning = false;
    // 是否已经连接
    private static boolean isConnected = false;

    //
    private Service mService;
    private XMPPEngine mEngine;
    private XMPPConnection mXMPPConnection;
    //private PacketListener mPacketListener;

    // 连接和关闭
    private ConnectReceiver mConnectReceiver;
    public static final String CONNECT_CLOSE = "com.zyxb.qqxmpp.CONNECT_CLOSE";
    public static final String CONNECT_OPEN = "com.zyxb.qqxmpp.CONNECT_OPEN";

    // service关闭
    public static final String CONNECT_SERVICE_CLOSE = "com.zyxb.qqxmpp.CONNECT_SERVICE_CLOSE";
    private ConnectCloseReceiver mCloseReceiver;

    // 更换服务器地址
    private ServerChangedReceiver mServerChangedReceiver;
    public static final String SERVER_CHANGED = "com.zyxb.qqxmp.SERVER_CHANGED";

    // ping-pong
    private String mPingID;
    private long mPingTimestamp;
    private PendingIntent mPingAlarmPendIntent;
    private PendingIntent mPongTimeoutAlarmPendIntent;
    private static final String PING_ALARM = "com.zyxb.qqxmpp.PING_ALARM";
    private static final String PONG_TIMEOUT_ALARM = "com.zyxb.qqxmpp.PONG_TIMEOUT_ALARM";
    private Intent mPingAlarmIntent = new Intent(PING_ALARM);
    private Intent mPongTimeoutAlarmIntent = new Intent(PONG_TIMEOUT_ALARM);
    private PongTimeoutAlarmReceiver mPongTimeoutAlarmReceiver = new PongTimeoutAlarmReceiver();
    private BroadcastReceiver mPingAlarmReceiver = new PingAlarmReceiver();
    private PacketListener mPongListener;
    private boolean isPingPingRegistered = false;
    private static final int PING_INTERVAL_TIMES = 30000;

    // 服务器是否已经开启
    private static boolean isConnectServiceRunning = false;
    //重连是否关闭
    private static boolean isReconnectRunning = false;
    //Ping
    private static boolean isPingRunning = false;
    //Pong time out
    private static boolean isPongRunning = false;

    //确保关闭
    private CountDownLatch mCloseLatch;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化xmpp
        Logger.d(TAG, "connect service oncreate");
        mService = this;

        // 重连
        mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM));

        // 连接和关闭连接
        mConnectReceiver = new ConnectReceiver();
        IntentFilter connectFilter = new IntentFilter();
        connectFilter.addAction(CONNECT_CLOSE);
        connectFilter.addAction(CONNECT_OPEN);
        registerReceiver(mConnectReceiver, connectFilter);

        // 服务器更改
        mServerChangedReceiver = new ServerChangedReceiver();
        IntentFilter serverFilter = new IntentFilter();
        serverFilter.addAction(SERVER_CHANGED);
        registerReceiver(mServerChangedReceiver, serverFilter);

        // 服务关闭
        mCloseReceiver = new ConnectCloseReceiver();
        IntentFilter closeFilter = new IntentFilter();
        closeFilter.addAction(CONNECT_SERVICE_CLOSE);
        registerReceiver(mCloseReceiver, closeFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isConnectServiceRunning) {
            // 第一次开启,自动连接
            // 获取配置
            String userType = SharedPreferencesUtils.getString(mService,
                    Const.SP_USER_TYPE, Const.USER_TYPE_LOCAL);
            // 使用本地测试数据或者无网络情况下,不连接服务器
            if (userType.equals(Const.USER_TYPE_LOCAL)
                    || !NetUtil.checkNet(mService)) {
                // 使用本地数据库,不连接服务器
            } else {
                connect();
            }
        }

        return START_NOT_STICKY;
    }

    /**
     * 关闭连接
     *
     * @return 是否断开连接成功
     */
    public boolean disconnect() {
        //如果登陆，登出

        Logger.d(TAG, "关闭连接");
        isConnected = false;

        RECONNECT_TIMES = 1;
        mReconnectTimeout = RECONNECT_AFTER;

        // 取消重连闹钟
        //if (mPAlarmIntent != null && isReconnectRunning) {
        //如果重连闹钟还未执行，取消
        if (isReconnectRunning) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPAlarmIntent);
            //mPAlarmIntent = null;
        }

        // 如果连接thread还在运行,则关闭
        if (mConnectingThread != null) {
            synchronized (mConnectingThread) {
                try {
                    mConnectingThread.interrupt();
                    mConnectingThread.join(50);
                } catch (InterruptedException e) {
                    Logger.d(TAG,
                            "doDisconnect: failed catching connecting thread");
                } finally {
                    mConnectingThread = null;
                }
            }
        }

        //将异常分开，否则出现异常，后面的不会执行

        //取消连接成功后注册的listener,receiver
        if (isPingRunning) {
            //取消listener
            if (mXMPPConnection != null) {
                //mXMPPConnection.removePacketListener(mPacketListener);
                mXMPPConnection.removePacketListener(mPongListener);
            }

            //取消Ping-pong闹钟
            ((AlarmManager) mService.getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPingAlarmPendIntent);

            //注销receiver
            mService.unregisterReceiver(mPingAlarmReceiver);
            mService.unregisterReceiver(mPongTimeoutAlarmReceiver);

            isPingRunning = false;
        }

        if (isPongRunning) {
            ((AlarmManager) mService.getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPongTimeoutAlarmPendIntent);
            isPongRunning = false;
        }

        Logger.d(TAG, "mXMPPConnection=" + mXMPPConnection);
        if (mXMPPConnection != null && mXMPPConnection.isConnected()) {
            mCloseLatch = new CountDownLatch(1);

            new Thread() {
                public void run() {
                    Logger.d(TAG, "shutDown thread started");
                    mXMPPConnection.disconnect();
                    Logger.d(TAG, "shutDown thread finished");

                    mCloseLatch.countDown();
                }
            }.start();
        }

        return true;
    }

    /**
     * 连接
     */
    private void connect() {
        Logger.d(TAG, "连接服务器");
        if (mConnectingThread != null) {
            Logger.i(TAG, "a connection is still going on!");
            return;
        }
        mConnectingThread = new Thread() {

            @Override
            public void run() {
                isRunning = true;

                // 初始化xmpp
                if (mEngine == null) {
                    // mEngine = XMPPEngine.getInstance(mService);
                    mEngine = new XMPPEngine(mService);
                    XMPPEngine.setmEngine(mEngine);
                    mXMPPConnection = mEngine.getConn();
                }

                try {
                    if (mXMPPConnection.isConnected()) {
                        try {
                            mXMPPConnection.disconnect();
                        } catch (Exception e) {
                            Logger.d(TAG, "conn.disconnect() failed: " + e);
                        }
                    }
                    SmackConfiguration.setPacketReplyTimeout(PACKET_TIMEOUT);
                    SmackConfiguration.setKeepAliveInterval(-1);
                    SmackConfiguration.setDefaultPingInterval(0);

                    mXMPPConnection.connect();
                    if (!mXMPPConnection.isConnected()) {
                        connectionFailed(SERVER_DISCONNECTED_FAIL);
                        return;
                    }
                    mXMPPConnection
                            .addConnectionListener(new ConnectionListener() {
                                public void connectionClosedOnError(Exception e) {
                                    connectionFailed(SERVER_DISCONNECTED_CONNECTION_ERROR);
                                }

                                public void connectionClosed() {
                                    // 正常注销
                                    // connectionFailed(SERVER_DISCONNECTED_CONNECTION_CLOSED);

                                }

                                public void reconnectingIn(int seconds) {
                                }

                                public void reconnectionFailed(Exception e) {
                                }

                                public void reconnectionSuccessful() {
                                }

                            });

                    connectionScuessed();
                    // 连接成功,注册ping-pong listener
                    registerPongListener();
                } catch (XMPPException e) {
                    String es = Log.getStackTraceString(e).toString();
                    Logger.d(TAG, "connect(): XMPPException:" + es);
                    connectionFailed(SERVER_DISCONNECTED_REMOTE_SERVER_ERROR);
                } catch (Exception e) {
                    String es = Log.getStackTraceString(e).toString();
                    Logger.d(TAG, "connect(): Exception:" + es);
                    connectionFailed(SERVER_DISCONNECTED_EXCEPTION);
                } finally {
                    if (mConnectingThread != null)
                        synchronized (mConnectingThread) {
                            mConnectingThread = null;
                        }
                    RECONNECT_TIMES++;
                }
            }

        };
        mConnectingThread.start();
    }

    /**
     * 连接成功
     */
    private void connectionScuessed() {
        Logger.d(TAG, "connect service connected:");
        isConnected = true;

        mConnectedState = SERVER_CONNECTED;// 已经连接上
        mReconnectTimeout = RECONNECT_AFTER;// 重置重连的时间

        // 关闭重连
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .cancel(mPAlarmIntent);// 取消重连闹钟
        isRunning = false;
        isReconnectRunning = false;

        // 重置重连次数
        RECONNECT_TIMES = 1;

        // 连接成功,开启Chat Service
        connectedReceiver();
    }

    /**
     * UI线程反馈连接失败
     *
     * @param reason
     */
    private void connectionFailed(int reason) {
        Logger.i(TAG, "connectionFailed: " + reason);
        isConnected = false;
        isReconnectRunning = false;

        mConnectedState = SERVER_DISCONNECTED;// 更新当前连接状态
        SERVER_STATE_REASON = reason;

        // 无网络连接时,直接返回
        if (NetUtil.getNetworkState(mService) == NetUtil.NETWORN_NONE) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPAlarmIntent);
            SERVER_STATE_REASON = SERVER_DISCONNECTED_NET_DISCONNECTED;
            disconnectedReceiver(SERVER_STATE_REASON);
            isRunning = false;
            //isReconnectRunning = false;

            return;
        }

        // 小于最大重连次数,重连
        if (RECONNECT_TIMES <= MAX_RECONNECT_TIMES) {
            Logger.d(TAG, "reconnect server");
            // 开启重连闹钟
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(
                    AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                            + mReconnectTimeout * 1000, mPAlarmIntent);
            mReconnectTimeout = mReconnectTimeout * 2;
            if (mReconnectTimeout > RECONNECT_MAXIMUM)
                mReconnectTimeout = RECONNECT_MAXIMUM;
            isRunning = true;
            isReconnectRunning = true;
        } else {
            // 关闭连接
            disconnect();

            // 发送广播
            disconnectedReceiver(SERVER_STATE_REASON);
            isRunning = false;
            isConnected = false;
        }

    }

    private void registerPongListener() {
        mPingID = null;

        if (mPongListener != null) {
            mXMPPConnection.removePacketListener(mPongListener);
            mPongListener = null;
        }

        mPongListener = new PacketListener() {

            @Override
            public void processPacket(Packet packet) {
                if (packet == null)
                    return;

                if (packet.getPacketID().equals(mPingID)) {
                    Logger.i(
                            TAG,
                            String.format(
                                    "Ping: server latency %1.3fs",
                                    (System.currentTimeMillis() - mPingTimestamp) / 1000.));
                    mPingID = null;
                    ((AlarmManager) mService
                            .getSystemService(Context.ALARM_SERVICE))
                            .cancel(mPongTimeoutAlarmPendIntent);

                    // 服务器依然保持连接,donothing
                    // connectedReceiver();
                }
            }

        };

        mXMPPConnection.addPacketListener(mPongListener, new PacketTypeFilter(
                IQ.class));
        mPingAlarmPendIntent = PendingIntent.getBroadcast(
                mService.getApplicationContext(), 0, mPingAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mPongTimeoutAlarmPendIntent = PendingIntent.getBroadcast(
                mService.getApplicationContext(), 0, mPongTimeoutAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mService.registerReceiver(mPingAlarmReceiver, new IntentFilter(
                PING_ALARM));
        mService.registerReceiver(mPongTimeoutAlarmReceiver, new IntentFilter(
                PONG_TIMEOUT_ALARM));
        isPingPingRegistered = true;

        ((AlarmManager) mService.getSystemService(Context.ALARM_SERVICE))
                .setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + PING_INTERVAL_TIMES,
                        PING_INTERVAL_TIMES, mPingAlarmPendIntent);
        isPingRunning = true;
    }

    public void sendServerPing() {
        if (mPingID != null) {
            Logger.d(TAG, "Ping: requested, but still waiting for " + mPingID);
            return; // a ping is still on its way
        }
        Ping ping = new Ping();
        ping.setType(Type.GET);
        ping.setTo(SharedPreferencesUtils.getString(mService, Const.XMPP_HOST,
                ""));
        mPingID = ping.getPacketID();
        mPingTimestamp = System.currentTimeMillis();
        Logger.d(TAG, "Ping: sending ping " + mPingID);
        mXMPPConnection.sendPacket(ping);

        ((AlarmManager) mService.getSystemService(Context.ALARM_SERVICE)).set(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + PACKET_TIMEOUT + 3000, mPongTimeoutAlarmPendIntent);
        isPongRunning = true;
    }

    /**
     * BroadcastReceiver to trigger reconnect on pong timeout.
     */
    private class PongTimeoutAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent i) {
            Logger.d(TAG, "Ping: timeout for " + mPingID);

            isPongRunning = false;
            disconnectedReceiver(SERVER_DISCONNECTED_PING_TIMEOUT);
        }
    }

    /**
     * BroadcastReceiver to trigger sending pings to the server
     */
    private class PingAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent i) {
            if (mXMPPConnection.isAuthenticated()) {
                sendServerPing();
            } else {
                Logger.d(TAG,
                        "Ping: alarm received, but not connected to server.");
                // disconnectedReceiver(SERVER_DISCONNECTED_NOT_RESPONSE);

                // 收到ping消息,但是未登陆
            }
        }
    }

    /**
     * 发送断开连接receiver
     *
     * @param type
     */
    private void disconnectedReceiver(int type) {
        Intent intent = new Intent();
        intent.putExtra("reason", type);
        intent.setAction(LOGIN_SERVER_DISCONNECTED);
        mService.sendBroadcast(intent);
        Logger.d(TAG, "login service: disconnected :" + type);
    }

    /**
     * 发送连接成功receiver
     */
    private void connectedReceiver() {
        Intent intent = new Intent();
        intent.setAction(LOGIN_SERVER_CONNECTED);
        mService.sendBroadcast(intent);
        // Logger.d(TAG, "login service: connected :");
    }

    // 自动重连广播
    private class ReconnectAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent i) {
            Logger.d(TAG, "Alarm received.");

            if (mConnectedState != SERVER_DISCONNECTED) {
                Logger.d(TAG,
                        "Reconnect attempt aborted: we are connected again!");
                return;
            }

            //连接
            connect();

            // 发送重连广播
            Intent intent = new Intent(LOGIN_SERVER_RECONNECT);
            mService.sendBroadcast(intent);
        }
    }

    /**
     * 连接和关闭连接
     *
     * @author Administrator
     */
    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CONNECT_CLOSE)) {
                if (isRunning || isConnected) {
                    disconnect();

                    //必须,否则mXMPPConnection先设置为空了,导致mXMPPConnection.disconnect()空指针
                    if (mCloseLatch != null) {
                        try {
                            mCloseLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                mCloseLatch = null;
                isRunning = false;
                isConnected = false;
                mXMPPConnection = null;
                //清空后,chat service中任然有值，why?????
                //chat中仅仅清空了本身的引用,并不是对象本身被清空了
                mEngine.clear();
                mEngine = null;
                RECONNECT_TIMES = 1;

                // 发送广播
                disconnectedReceiver(SERVER_DISCONNECTED_CONNECTION_CLOSED);
            } else if (!isRunning && !isConnected) {
                connect();
            }
        }

    }

    /**
     * 服务器地址改变
     *
     * @author Administrator
     */
    private class ServerChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO 判断服务器地址是否真正改变

            // 未改变且未连接，连接
            // 未改变，但是连接成功,发布logout广播(登陆就登出，未登陆，do nothing)

            // 改变了，发送logout广播，断开连接，再重新连接

            if (isRunning || isConnected) {
                disconnect();

                if (mCloseLatch != null) {
                    try {
                        mCloseLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            mCloseLatch = null;
            isRunning = false;
            isConnected = false;
            mXMPPConnection = null;
            //清空后,chat service中任然有值，why?????
            if (mEngine != null) {
                mEngine.clear();
                mEngine = null;
            }
            RECONNECT_TIMES = 1;

            connect();
        }

    }

    private class ConnectCloseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRunning || isConnected) {
                disconnect();
            }
            isRunning = false;
            isConnected = false;
            isConnectServiceRunning = false;

            mService.stopSelf();

            // TODO 发送chat service stop
            Intent chatCloseIntent = new Intent(ChatService.CHAT_SERVICE_CLOSE);
            sendBroadcast(chatCloseIntent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mConnectReceiver);
        unregisterReceiver(mCloseReceiver);
        unregisterReceiver(mServerChangedReceiver);
        unregisterReceiver(mAlarmReceiver);// 注销广播监听

        if (isReconnectRunning) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPAlarmIntent);// 取消重连闹钟
        }

        if (isPingRunning) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPingAlarmPendIntent);
        }

        if (isPongRunning) {
            ((AlarmManager) mService.getSystemService(Context.ALARM_SERVICE))
                    .cancel(mPongTimeoutAlarmPendIntent);
        }

        if (mPongTimeoutAlarmReceiver != null && isPingPingRegistered) {
            unregisterReceiver(mPongTimeoutAlarmReceiver);
        }
        if (mPingAlarmReceiver != null && isPingPingRegistered) {
            unregisterReceiver(mPingAlarmReceiver);
        }

        isRunning = false;
        isConnectServiceRunning = false;
        isConnected = false;
    }
}