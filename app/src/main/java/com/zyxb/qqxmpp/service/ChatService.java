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

import java.util.ArrayList;

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
    public static final int USER_ADD = 1;
    public static final int USER_DELETE = 2;
    public static final int USER_UPDATE = 3;
    public static final int MESSAGE_ADD = 10;
    public static final int MESSAGE_DELETE = 11;
    public static final int MESSAGE_UPDATE =12;

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

    //服务器改变
   // private ServerChangedReceiver mServerChangedReceiver;

    //发送消息
    private MessageReceiver mMessageReceiver;
    public static final String XMPP_MESSAGE = "com.zyxb.qqxmpp.XMPP_MESSAGE";
    public static final int MESSAGE_TYPE_TXT = 0;//文本消息
    public static final int MESSAGE_TYPE_FILE = 1;//普通文件
    public static final int MESSAGE_TYPE_VOICE = 2;//语音文件
    public static final int MESSAGE_TYPE_VIDEO = 3;//视频文件
    public static final int MESSAGE_TYPE_REALTIME_VIDEO = 4;//实时视频

    //好友
    private CreateFriendGroupReceiver mCreateFriendGroupReceiver;
    public static final String USER_CREATE_FRIEND_GROUP = "com.zyxb.qqxmpp.USER_CREATE_FRIEND_GROUP";
    //public static final String USER_CREATE_FRIEND_GROUP_SUCCESS = "com.zyxb.qqxmpp.USER_CREATE_FRIEND_GROUP_SUCCESS";
    //public static final String USER_CREATE_FRIEND_GROUP_FAILED = "com.zyxb.qqxmpp.USER_CREATE_FRIEND_GROUP_FAILED";

    //发送文件
    public static final String MESSAGE_SEND_FILE = "com.zyxb.qqxmpp.MESSAGE_SEND_FILE";
    private SendFileReceiver mSendFileReceiver;

    //搜索用户
    public static final String USER_SEARCH = "com.zyxb.qqxmpp.USER_SEARCH";
    private UserSearchReceiver mUserSearchReceiver;
    public static final String USER_SEARCH_RESULT = "com.zyxb.qqxmpp.USER_SEARCH_RESULT";

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

        mMessageReceiver = new MessageReceiver();
        IntentFilter messageFilter = new IntentFilter();
        messageFilter.addAction(XMPP_MESSAGE);
        registerReceiver(mMessageReceiver,messageFilter);

        //服务器改变

        //创建新分组
        mCreateFriendGroupReceiver = new CreateFriendGroupReceiver();
        IntentFilter friendGroupIntent = new IntentFilter();
        friendGroupIntent.addAction(USER_CREATE_FRIEND_GROUP);
        registerReceiver(mCreateFriendGroupReceiver,friendGroupIntent);

        //发送文件
        mSendFileReceiver = new SendFileReceiver();
        IntentFilter sendFileIntent = new IntentFilter();
        sendFileIntent.addAction(MESSAGE_SEND_FILE);
        registerReceiver(mSendFileReceiver,sendFileIntent);

        //搜索用户
        mUserSearchReceiver = new UserSearchReceiver();
        IntentFilter userSearchIntent = new IntentFilter();
        userSearchIntent.addAction(USER_SEARCH);
        registerReceiver(mUserSearchReceiver,userSearchIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO 网络开启情况下，查询表示为sending的message(文本/文件),发送

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
        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mCreateFriendGroupReceiver);
        unregisterReceiver(mSendFileReceiver);
        unregisterReceiver(mUserSearchReceiver);
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
        Logger.d(TAG,"ChatService Engine:" + mEngine);

        if (mEngine == null) {
            mEngine = XMPPEngine.getmEngine();
        }

        return mEngine;
    }

    public void resetEngine(){
        mEngine = XMPPEngine.getmEngine();
    }

    /**
     * 登陆
     *
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
     * @return 是否登出成功
     */
    public boolean logout() {
        getmEngine();

        return mEngine.logout();
    }

    public int register(String account, String password) {
        getmEngine();
        //int result = mEngine.regist(account, password);

        //return result;
        return mEngine.regist(account,password);
    }

    /**
     * 发送消息
     *
     * @param account 消息的id
     * @param toJid 消息接收方jid
     * @param msg 消息内容
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

                //重新获取Engine,服务器更换时，需要重新获取Engine
                //connect service中清除XMPPEngine中engine值时,chat service中获取不为空,why
                //理解 a = new XMPPEngine(...);b=a; a=null; b不等于null
                resetEngine();
                //getmEngine();//不行,理由同上
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
            //int type = mEngine.regist(username.split("@")[0], pwd);
            int type = register(username.split("@")[0], pwd);

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

    /**
     * 发送消息
     */
    private class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("xmpp_message_type",-1);
            switch(type){
                case MESSAGE_TYPE_TXT:
                    //文本消息，直接发送
                    //收到回执中携带此id
                    String id = intent.getStringExtra("id");
                    String toJid = intent.getStringExtra("toJid");
                    String message = intent.getStringExtra("message");
                    //mEngine.sendMessage(id,toJid,message);
                    sendMessage(id,toJid,message);
                    break;
            }
        }
    }

    /**
     * 创建好友分组
     */
    private class CreateFriendGroupReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String groupName = intent.getStringExtra("friendGroupName");
            mEngine.addGroup(groupName);

//            new Thread(){
//                @Override
//                public void run() {
//                    boolean isCreated = mEngine.addGroup(groupName);
//                    if(isCreated){
//                        //创建成功
//                        Intent createSuccessIntent = new Intent();
//                        createSuccessIntent.setAction(ChatService.USER_CREATE_FRIEND_GROUP_SUCCESS);
//                        mService.sendBroadcast(createSuccessIntent);
//                    }else{
//                        //创建失败
//                        Intent createFailIntent = new Intent();
//                        createFailIntent.setAction(ChatService.USER_CREATE_FRIEND_GROUP_FAILED);
//                        mService.sendBroadcast(createFailIntent);
//                    }
//                }
//            }.start();
        }
    }

    /**
     * 发送文件
     */
    private class SendFileReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filePath = intent.getStringExtra("filePath");
            final String toJid = intent.getStringExtra("toJid") + "/Spark 2.6.3";

            //向spark发送文件没问题,接收文件还未调试
            new Thread(){
                @Override
                public void run() {
                    mEngine.sendFile(toJid,filePath);
                }
            }.start();
        }
    }

    private class UserSearchReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String username = intent.getStringExtra("username");

            //如果登陆了
            if (mEngine != null) {
                //可以搜索到用户
                new Thread() {
                    @Override
                    public void run() {
                        ArrayList<String> names = mEngine.searchUsers(username);

                        //发送结果result
                        Intent searchResultIntent = new Intent();
                        searchResultIntent.setAction(ChatService.USER_SEARCH_RESULT);
                        searchResultIntent.putStringArrayListExtra("result", names);
                        mService.sendBroadcast(searchResultIntent);
                    }
                }.start();
            }
        }
    }
}
