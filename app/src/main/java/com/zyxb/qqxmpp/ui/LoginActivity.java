package com.zyxb.qqxmpp.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zyxb.qqxmpp.App;
import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.MD5Encoder;
import com.zyxb.qqxmpp.util.NetUtil;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;
import com.zyxb.qqxmpp.util.ToastUtil;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.LoadingDialog;
import com.zyxb.qqxmpp.view.TextURLView;

/**
 * @author 吴小雄
 *         <p/>
 *         如果是本地测试数据，直接登陆
 *         如果是网络数据，检测本地是否登陆过，登陆过，直接使用本地登陆，后台连接服务器，更新数据
 *         未登陆过，等待登陆，成功进入Main，失败，提示错误
 */
public class LoginActivity extends Activity {
    private Context mContext;
    private RelativeLayout rlUser;
    private Button btLogin;
    private Button btRegister;
    private TextURLView mTextViewURL;

    private EditText etAccount, etPwd;
    private LoadingDialog ldLoading;
    // private boolean isConnectedToServer = true;
    private ConnectReceiver mConnectReceiver;
    private IntentFilter mConnectFilter;

    private String username;// 用户名
    private String pwd;// 密码
    private String userType;
    private String host;

    private App mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mApp = (App) getApplication();
        mContext = this;
        ldLoading = new LoadingDialog(this);
        ldLoading.setCancelable(false);
        ldLoading.setTitle("正在登录...");

        findView();
        initTvUrl();
        init();
    }

    private void initReceiver() {
        mConnectReceiver = new ConnectReceiver();
        mConnectFilter = new IntentFilter();
        mConnectFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
        mConnectFilter.addAction(ConnectService.LOGIN_SERVER_DISCONNECTED);
        mConnectFilter.addAction(ChatService.LOGIN);
        registerReceiver(mConnectReceiver, mConnectFilter);
    }

    private void findView() {
        rlUser = (RelativeLayout) findViewById(R.id.rl_user);
        btLogin = (Button) findViewById(R.id.login);
        btRegister = (Button) findViewById(R.id.register);
        mTextViewURL = (TextURLView) findViewById(R.id.tv_forget_password);

        etAccount = (EditText) findViewById(R.id.account);
        etPwd = (EditText) findViewById(R.id.password);

    }

    private void init() {
        Animation anim = AnimationUtils.loadAnimation(mContext,
                R.anim.login_anim);
        anim.setFillAfter(true);
        rlUser.startAnimation(anim);
        btLogin.setOnClickListener(loginOnClickListener);
        btRegister.setOnClickListener(registerOnClickListener);
        mTextViewURL.setOnClickListener(forgetPwdOnClickListener);

        // 开启连接,则关闭
        if (SharedPreferencesUtils.getString(mContext, Const.SP_USER_TYPE,
                Const.USER_TYPE_LOCAL).equals(Const.USER_TYPE_XMPP)) {
            Intent intent = new Intent(ConnectService.CONNECT_CLOSE);
            sendBroadcast(intent);
        }
    }

    private void initTvUrl() {
        mTextViewURL.setText(R.string.forget_password);
    }

    /**
     * 登录
     */
    private OnClickListener loginOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doLogin();
        }
    };

    /**
     * 注册
     */
    private OnClickListener registerOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, RegisterActivity.class);
            startActivity(intent);
            UIAnimUtils.sildLeftIn(LoginActivity.this);
            //LoginActivity.this.finish();
        }
    };

    /**
     * 忘记密码
     */
    private OnClickListener forgetPwdOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, "Come on later", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 根据用户输入类型
     * 用户名中没有@,为本地测试数据，直接登陆
     * 用户名中包含@,为网络数据，连接服务器
     */
    private void doLogin() {

        username = etAccount.getText().toString();
        pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showShortToast(mContext, "请输入您的账号");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showShortToast(mContext, "请输入您的密码");
            return;
        }
        ldLoading.show();

        // 检测账号类型 [xxx@xx.xx.xx为xmpp,xxx为local]
        String[] un = username.split("@");
        if (un.length == 1) {
            // 本地测试账号,直接登陆
            userType = Const.USER_TYPE_LOCAL;
            host = "";

            // 关闭连接
            Intent stopConn = new Intent(ConnectService.CONNECT_CLOSE);
            sendBroadcast(stopConn);
            mApp.setUserType(userType);

            localLogin("使用测试数据登陆");

            return;
        } else {
            userType = Const.USER_TYPE_XMPP;
            host = un[1];

            // 注册receiver
            if (mConnectReceiver == null) {
                initReceiver();
            }
        }

        mApp.setUserType(userType);

        // 如果网络未开启,查询本地数据
        if (!NetUtil.checkNet(this)) {
            localLogin("当前网络不可用");

        } else {
            // 连接xmpp服务器
            SharedPreferencesUtils.setString(mContext, Const.SP_USERNAME,
                    username);
            SharedPreferencesUtils.setString(mContext, Const.SP_PWD, pwd);
            SharedPreferencesUtils.setString(mContext, Const.SP_USER_TYPE,
                    userType);
            SharedPreferencesUtils.setString(mContext, Const.XMPP_HOST, host);

            // 服务器改变重新连接
            Intent serverChangeIntent = new Intent(
                    ConnectService.SERVER_CHANGED);
            sendBroadcast(serverChangeIntent);

            // 使用本地数据库登陆
            localXMPPLogin();

        }

    }

    private void localXMPPLogin() {
        DataEngine engine = new DataEngine(this);
        DBUser user = engine.login(username, MD5Encoder.encode(pwd));
        if (user != null) {
            ldLoading.dismiss();
            // 设置user
            mApp.setmUser(user);
            mApp.setConnected(false);

            // 进入主界面
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);

            this.finish();
        } else {
            DBUser u = engine.findXMPPUserByName(username);
            if (u != null) {
                Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                ldLoading.dismiss();
                etAccount.setText("");
                etPwd.setText("");
                etAccount.requestFocus();
            } else {
                // 本地未登陆过，没有存储数据，等待服务器回应
            }
        }
    }

    private void localLogin(String msg) {
        DataEngine engine = new DataEngine(this);
        DBUser user = engine.login(username, MD5Encoder.encode(pwd));
        if (user != null) {
            ldLoading.dismiss();
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            SharedPreferencesUtils.setString(mContext, Const.SP_USERNAME,
                    username);
            SharedPreferencesUtils.setString(mContext, Const.SP_PWD, pwd);
            SharedPreferencesUtils.setString(mContext, Const.SP_USER_TYPE,
                    userType);
            SharedPreferencesUtils.setString(mContext, Const.XMPP_HOST, host);

            // 设置user
            mApp.setmUser(user);
            mApp.setConnected(false);

            // 进入主界面
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);

            this.finish();
        } else {

            Toast.makeText(this, "用户名或密码不正确,请重新输入", Toast.LENGTH_LONG).show();
            ldLoading.dismiss();
            etAccount.setText("");
            etPwd.setText("");
            etAccount.requestFocus();
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

    @Override
    protected void onPause() {
        super.onPause();

        if (mConnectReceiver != null) {
            unregisterReceiver(mConnectReceiver);
            mConnectReceiver = null;
        }
    }

    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int reason = intent.getIntExtra("reason", -1);

            String action = intent.getAction();

            if (action.equals(ConnectService.LOGIN_SERVER_CONNECTED)) {
                // 登陆
                // chatService.login(username.split("@")[0], pwd, "qqxmpp");
                // return;

                // 登陆
                //chatservice中会自动登陆
//				Intent autoLoginIntent = new Intent(ChatService.AUTO_LOGIN);
//				sendBroadcast(autoLoginIntent);

                return;
            } else if (action.equals(ConnectService.LOGIN_SERVER_RECONNECT)) {
                ldLoading.dismiss();
                // 服务器连接失败
                Toast.makeText(mContext, "服务器连接失败,请稍后重试", Toast.LENGTH_LONG)
                        .show();
                ldLoading.dismiss();
                etAccount.setText("");
                etPwd.setText("");
                etAccount.requestFocus();

                // 断开连接
                Intent closeConnIntent = new Intent(
                        ConnectService.CONNECT_CLOSE);
                sendBroadcast(closeConnIntent);

                return;
            }

            if (reason == ChatService.SERVER_CONNECTED_USER_LOGIN) {
                ldLoading.dismiss();
                // isLogin = true;

                if (username == null || username.trim().equals("")
                        || pwd == null || pwd.trim().equals("")) {
                    return;
                }
                // 设置user信息,进入main
                // 查找本地用户是否存在,不存在添加
                // 新用户添加移到login中,读取即可
                DataEngine engine = new DataEngine(LoginActivity.this);
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
                LoginActivity.this.finish();

            } else if (reason == ChatService.SERVER_CONNECTED_USER_REJECTED) {
                ldLoading.dismiss();
                Toast.makeText(LoginActivity.this, "用户名或密码错误,请重新登录",
                        Toast.LENGTH_SHORT).show();
                // isLogin = false;
                etAccount.setText("");
                etPwd.setText("");
                etAccount.requestFocus();

            } else {
                ldLoading.dismiss();
                Toast.makeText(LoginActivity.this, "网络连接错误", Toast.LENGTH_SHORT)
                        .show();

                etAccount.setText("");
                etPwd.setText("");
                etAccount.requestFocus();
            }
        }

    }

}
