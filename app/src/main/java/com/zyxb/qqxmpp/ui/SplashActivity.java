package com.zyxb.qqxmpp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.zyxb.qqxmpp.App;
import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBInit;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.MD5Encoder;
import com.zyxb.qqxmpp.util.NetUtil;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;

/*
 * 检查网络状况,后台用户登陆,产品更新
 * 不继承baseactivity:BaseActivity会自动检测用户是否登陆,之后自动切换到登陆界面
 * 					  而splash界面需控制切换
 * 不需要绑定chat service, chatservice中监听connected,连接后自动完  成登录
 *
 * 下一步：当前流程太复杂，需简化登陆流程
 * splash界面完成最初数据初始化,根据本地保存数据登陆或跳转到login界面
 * 不再处理服务器连接与登陆
 */
public class SplashActivity extends Activity {
    private ImageView ivSplash;
    private Context mContext;
    private App mApp;

    // 是否已经登录
    private boolean isLogin = false;
    private ConnectReceiver mConnectReceiver;
    //private LoginReceiver mLoginReceiver;
    private String username;
    private String pwd;

    // 账号类型
    private String userType;

    // 绑定服务
    // private ChatServiceConnection chatConn;
    // private ChatService chatService;

    //线程同步,会导致draw不能及时绘制(draw还未及时绘制图像已经暂停执行main线程)
    //private CountDownLatch mCountDownLatch;

    //使用handler处理
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_PROGRESS_DISMISS:
                    mProgressDialog.dismiss();
                    startLogin();
                    break;
            }
        }
    };

    private static final int INIT_PROGRESS_DISMISS = 0;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        ivSplash = (ImageView) findViewById(R.id.ivSplash);
        mContext = this;
        mApp = (App) getApplication();

        // 检测账号类型
        userType = SharedPreferencesUtils.getString(mContext,
                Const.SP_USER_TYPE, Const.USER_TYPE_LOCAL);
        mApp.setUserType(userType);

        if (userType.equals(Const.USER_TYPE_XMPP)) {
            // 注册广播接收者
            mConnectReceiver = new ConnectReceiver();
            IntentFilter connectFilter = new IntentFilter();
            connectFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
            connectFilter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
            connectFilter.addAction(ChatService.LOGIN);
            registerReceiver(mConnectReceiver, connectFilter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("数据初始化...");
        mProgressDialog.show();

        new Thread() {
            @Override
            public void run() {
                // 检查数据库,导入数据
                if (DBInit.isEmpty(SplashActivity.this)) {
                    DBInit.create(SplashActivity.this);
                }

                Message msg = Message.obtain();
                msg.what = INIT_PROGRESS_DISMISS;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void startLogin() {
        // 开启服务
        Intent service = new Intent(this, ConnectService.class);
        startService(service);

        Intent chatService = new Intent(this, ChatService.class);
        startService(chatService);

        // 过于复杂,没必要
        // Intent messageService = new Intent(this,MessageQueueService.class);
        // startService(messageService);

        if (userType.equals(Const.USER_TYPE_LOCAL)) {
            SharedPreferencesUtils.setString(mContext, Const.SP_USER_TYPE,
                    Const.USER_TYPE_LOCAL);
            testLogin();

            return;
        }

        // 检查网络连接
        boolean isNetConnected = NetUtil.checkNet(this);
        if (!isNetConnected) {
            noNet();
        } else {
            connectXMPPServer();
        }
    }

    private void testLogin() {
        ivSplash.postDelayed(new Runnable() {
            @Override
            public void run() {
                localLogin("使用测试数据登陆");
            }
        }, 2000);
    }

    private void noNet() {
        ivSplash.postDelayed(new Runnable() {
            @Override
            public void run() {
                localLogin("当前网络不可用");
            }
        }, 2000);
    }

    private void localLogin(String msg) {
        Intent intent;

        // 检测sp查询数据库user是否存在
        String username = SharedPreferencesUtils.getString(mContext,
                Const.SP_USERNAME, "");
        String pwd = SharedPreferencesUtils.getString(mContext, Const.SP_PWD,
                "");

        if (!username.equals("") && !pwd.equals("")) {
            // 从user表查询
            DataEngine engine = new DataEngine(mContext);
            DBUser user = engine.login(username, MD5Encoder.encode(pwd));
            if (user != null) {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

                mApp.setmUser(user);
                intent = new Intent(mContext, MainActivity.class);
            } else {
                Toast.makeText(mContext, "用户名或密码不对", Toast.LENGTH_SHORT).show();

                SharedPreferencesUtils.setString(mContext, Const.SP_USERNAME,
                        "");
                SharedPreferencesUtils.setString(mContext, Const.SP_PWD, "");

                intent = new Intent(mContext, LoginActivity.class);
            }

        } else {
            Toast.makeText(mContext, "未保存用户名密码,请登录", Toast.LENGTH_LONG).show();
            intent = new Intent(mContext, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void connectXMPPServer() {
        // 连接网络
        username = SharedPreferencesUtils.getString(mContext,
                Const.SP_USERNAME, "");
        pwd = SharedPreferencesUtils.getString(mContext, Const.SP_PWD, "");

        ivSplash.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 如果2秒内未登录成功,使用本地登陆
                if (!isLogin) {
                    unregisterReceiver(mConnectReceiver);
                    mConnectReceiver = null;
                    //unregisterReceiver(mLoginReceiver);
                    //mLoginReceiver = null;
                    localLogin("网络连接中,使用本地数据库登陆");
                }
            }

        }, 2000);
    }

    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int reason = intent.getIntExtra("reason", -1);
            String action = intent.getAction();

            Logger.d("Splash", "action:" + action + ",reason:" + reason);
            //Logger.d("Splash", "action:" + action);

            //ANR?????使用同一个receiver,当保存的用户名和密码变更后，并且服务器开启，网络开启，打开就连接上网络
            //接收到连接成功receiver后,主程序不执行，ANR??? why????(后台service导致)

            //chatservice中会接收login_server_connected，然后自动登陆
            if (action.equals(ConnectService.LOGIN_SERVER_CONNECTED)) {
//                Intent autoLoginIntent = new Intent(ChatService.AUTO_LOGIN);
//                sendBroadcast(autoLoginIntent);
//
                return;
            }

            Logger.d("Splash", "connection receiver OK");

            if (reason == ChatService.SERVER_CONNECTED_USER_LOGIN) {
                isLogin = true;

                // 设置user信息,进入main
                // 查找本地用户是否存在,不存在添加
                DataEngine engine = new DataEngine(SplashActivity.this);
                XMPPUser ur = new XMPPUser();
                ur.setJid(username);
                ur.setNickname(username.split("@")[0]);
                ur.setStatusMessage(pwd);
                DBUser u = engine.getXMPPUser(ur);
                mApp.setmUser(u);
                mApp.setConnected(true);

                // 发送用户消息保存完成广播
                Intent userAddIntent = new Intent(
                        ChatService.USER_LOCAL_ADD_COMPLETE);
                sendBroadcast(userAddIntent);

                // 进入主界面
                Intent main = new Intent(context, MainActivity.class);
                startActivity(main);
                SplashActivity.this.finish();

            } else if (reason == ChatService.SERVER_CONNECTED_USER_REJECTED) {
                Toast.makeText(mContext, "用户名或密码错误,请重新登录", Toast.LENGTH_SHORT)
                        .show();
                isLogin = false;
                // 进入登陆界面
                Intent lt = new Intent(mContext, LoginActivity.class);
                startActivity(lt);
                SplashActivity.this.finish();

            } else {
                Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_SHORT).show();
                isLogin = false;
                // 进入登陆界面
                Intent lt = new Intent(mContext, LoginActivity.class);
                startActivity(lt);
                SplashActivity.this.finish();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mConnectReceiver != null) {
            unregisterReceiver(mConnectReceiver);
            mConnectReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mConnectReceiver != null) {
            unregisterReceiver(mConnectReceiver);
            mConnectReceiver = null;
        }
    }

}
