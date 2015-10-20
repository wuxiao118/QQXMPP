package com.zyxb.qqxmpp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.zyxb.qqxmpp.engine.XMPPEngine;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;

/**
 * 登陆，登出，注册及XMPP相关功能
 *
 */
public class ChatService extends Service {
    private static final String TAG = "ChatService";
    private ChatService mService;
    private XMPPEngine mEngine;

    public static final String USER_LOCAL_ADD_COMPLETE = "com.zyxb.qqxmpp.USER_LOCAL_ADD_COMPLETE";
    public static final String LOGIN = "com.zyxb.qqxmpp.SERVER_LOGIN";
    public static final String LOGOUT = "com.zyxb.qqxmpp.SERVER_LOGOUT";
    public static final String CHAT_SERVICE_CLOSE = "com.zyxb.qqxmpp.CHAT_SERVICE_CLOSE";
    public static final String AUTO_LOGIN = "com.zyxb.qqxmpp.AUTO_LOGIN";
    //注册
    public static final String REGISTER = "com.zyxb.qqxmpp.REGISTER";
    public static final String REGISTER_SUCCESS = "com.zyxb.qqxmpp.REGISTER_SUCCESS";
    public static final String REGISTER_FAILED = "com.zyxb.qqxmpp.REGISTER_FAILED";
    public static final String REGISTER_USER_EXISTS = "com.zyxb.qqxmpp.REGISTER_USER_EXISTS";
    public static final String REGISTER_NO_RESPONSE = "com.zyxb.qqxmpp.REGISTER_NO_RESPONSE";

    //数据改变
    public static final String USER_DATA_CHANGED = "com.zyxb.qqxmpp.USER_DATA_CHANGED";
    public static final String MESSAGE_DATA_CHANGED = "com.zyxb.qqxmpp.MESSAGE_DATA_CHANGED";

    public static final int SERVER_CONNECTED_USER_LOGIN = 1;
    public static final int SERVER_CONNECTED_USER_REJECTED = 2;
    public static final int SERVER_CONNECTED_USER_LOGOUT = 12;

    // 连接
    private ConnectReceiver mConnReceiver;
    private boolean isLogin = false;

    // 登陆用户信息保存完成
    private UserSaveReceiver mUserSaveReceiver;

    // 关闭自己
    private CloseReceiver mCloseReceiver;

    //自动登陆
    private AutoLoginReceiver mAutoLoginReceiver;

    //注册
    private RegisterReceiver mRegisterReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "chat service created");
        mService = this;
        // mEngine = XMPPEngine.getInstance(mService);
        // mEngine = new XMPPEngine(mService);
        // mEngine = XMPPEngine.getmEngine();

        // 连接receiver
        mConnReceiver = new ConnectReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
        filter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
        registerReceiver(mConnReceiver, filter);

        // 注册用户添加完成receiver,添加完成后可启动线程添加好友
        mUserSaveReceiver = new UserSaveReceiver();
        IntentFilter userSaveFilter = new IntentFilter();
        userSaveFilter.addAction(USER_LOCAL_ADD_COMPLETE);
        registerReceiver(mUserSaveReceiver, userSaveFilter);

        // 关闭
        mCloseReceiver = new CloseReceiver();
        IntentFilter closeFilter = new IntentFilter();
        closeFilter.addAction(CHAT_SERVICE_CLOSE);
        registerReceiver(mCloseReceiver, closeFilter);

        //自动登陆
        mAutoLoginReceiver = new AutoLoginReceiver();
        IntentFilter autoLoginFilter = new IntentFilter();
        autoLoginFilter.addAction(ChatService.AUTO_LOGIN);
        registerReceiver(mAutoLoginReceiver, autoLoginFilter);

        //注册
        mRegisterReceiver = new RegisterReceiver();
        IntentFilter registerFilter = new IntentFilter();
        registerFilter.addAction(ChatService.REGISTER);
        registerReceiver(mRegisterReceiver, registerFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mConnReceiver);
        unregisterReceiver(mUserSaveReceiver);
        unregisterReceiver(mCloseReceiver);
        unregisterReceiver(mAutoLoginReceiver);
        unregisterReceiver(mRegisterReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private class MyBinder extends Binder implements IChatService {
        @Override
        public ChatService getService() {
            return mService;
        }
    }

    // 设置engine
    public void setmEngine(XMPPEngine mEngine) {
        this.mEngine = mEngine;
    }

    public XMPPEngine getmEngine() {
        if (mEngine == null) {
            mEngine = XMPPEngine.getmEngine();
        }

        return mEngine;
    }

    /**
     * 登陆
     *
     * @return
     */
    public void login(String account, String pwd, String ressource) {
        getmEngine();

        Logger.d(TAG, "mUser login:" + account + "," + ressource);
        Intent intent = new Intent(LOGIN);
        boolean isLogin = mEngine.login(account, pwd, ressource);
        if (isLogin) {
            intent.putExtra("reason", SERVER_CONNECTED_USER_LOGIN);
        } else {
            intent.putExtra("reason", SERVER_CONNECTED_USER_REJECTED);
        }

        sendBroadcast(intent);
        // return mEngine.login(account, pwd, ressource);
    }

    /**
     * 注销
     *
     * @return
     */
    public boolean logout() {
        getmEngine();

        return mEngine.logout();
    }

    public int register(String account, String password) {
        getmEngine();
        int result = mEngine.regist(account, password);

        return result;
    }

    /**
     * 发送消息
     *
     * @param toJid
     * @param msg
     */
    public void sendMessage(String account, String toJid, String msg) {
        getmEngine();

        mEngine.sendMessage(account, toJid, msg);
    }

    /**
     * 网络连接成功后自动登录,网络断开,自动注销
     *
     * @author Administrator
     */
    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ConnectService.LOGIN_SERVER_CONNECTED)) {
                Logger.d(TAG, "用户自动登录");

                // 获取用户名密码
                String username = SharedPreferencesUtils.getString(mService,
                        Const.SP_USERNAME, "");
                String pwd = SharedPreferencesUtils.getString(mService,
                        Const.SP_PWD, "");
                String ressource = SharedPreferencesUtils.getString(mService,
                        Const.XMPP_RESSOURCE, "qqxmpp");

                if (username.equals("") || pwd.equals("")) {
                    // 用户名或密码为空,不连接
                    return;
                }

                login(username.split("@")[0], pwd, ressource);
                isLogin = true;
            } else if (action.equals(ConnectService.LOGIN_SERVER_DISCONNECTED)) {
                Logger.d(TAG, "用户自动注销");
                // 停止消息队列
                //mEngine.stopMessageQueue();

                if (isLogin) {
                    logout();
                    isLogin = false;
                }
            }
        }
    }

    /**
     * 登陆用户信息保存完成,启动好友和更新线程,更新数据库
     *
     * @author Administrator
     */
    private class UserSaveReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d(TAG, "login mUser added.");
            // 开启线程,添加数据
            //mEngine.startMessageQueue();
        }

    }

    /**
     * 关闭服务
     */
    private class CloseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mService.stopSelf();
        }
    }

    /**
     * 自动登陆
     */
    private class AutoLoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取用户信息,自动登陆
            String username = SharedPreferencesUtils.getString(mService, Const.SP_USERNAME, "").split("@")[0];
            String pwd = SharedPreferencesUtils.getString(mService, Const.SP_PWD, "");
            login(username.split("@")[0], pwd, "qqxmpp");
        }
    }

    /**
     * 注册
     */
    private class RegisterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String username = SharedPreferencesUtils.getString(mService,
                    Const.SP_REGISTER_USERNAME, "");
            String pwd = SharedPreferencesUtils.getString(mService,
                    Const.SP_REGISTER_PWD, "");
            getmEngine();
            int type = mEngine.regist(username.split("@")[0], pwd);

            Intent resultIntent;
            switch (type) {
                case XMPPEngine.REGISTER_FAILED:
                    resultIntent = new Intent(ChatService.REGISTER_FAILED);
                    break;
                case XMPPEngine.REGISTER_NO_RESULT:
                    resultIntent = new Intent(ChatService.REGISTER_NO_RESPONSE);
                    break;
                case XMPPEngine.REGISTER_SUCCESS:
                    resultIntent = new Intent(ChatService.REGISTER_SUCCESS);
                    break;
                case XMPPEngine.REGISTER_USER_EXISTS:
                    resultIntent = new Intent(ChatService.REGISTER_USER_EXISTS);
                    break;
                default:
                    resultIntent = new Intent(ChatService.REGISTER_FAILED);
                    break;
            }
            sendBroadcast(resultIntent);
        }
    }
}
