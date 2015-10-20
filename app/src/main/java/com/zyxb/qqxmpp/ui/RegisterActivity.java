package com.zyxb.qqxmpp.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zyxb.qqxmpp.App;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.service.ConnectService;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.SharedPreferencesUtils;
import com.zyxb.qqxmpp.util.UIAnimUtils;

/**
 * @author 吴小雄
 *         <p/>
 *         注册界面,还未做
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private LinearLayout llBack;
    private EditText etHost, etAccount, etPassword, etConfirmPassword;
    private Button btRegister;

    //private App mApp;
    //private Context mContext;

    private String host, username, password, confirmPassword;

    //
    private ConnectReceiver mConnectReceiver;

    //
    private ProgressDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        Logger.d(TAG, "register start");

        initUI();
        initData();
        initEvent();
    }

    @Override
    protected boolean checkUser() {
        //阻止父类检查用户是否登陆，从未跳转到LoginAcitivity

        return true;
    }

    private void initUI() {
        llBack = findView(R.id.llRegisterBack);
        etHost = findView(R.id.etRegisterHost);
        etAccount = findView(R.id.etRegisterAccount);
        etPassword = findView(R.id.etRegisterPassword);
        etConfirmPassword = findView(R.id.etRegisterConfirmPassword);
        btRegister = findView(R.id.btRegister);

        mLoading = new ProgressDialog(mContext);
        mLoading.setTitle("连接服务器");
        mLoading.setCancelable(false);
    }

    private void initData() {
        mApp = (App) getApplication();
        mContext = this;
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
        btRegister.setOnClickListener(this);

        mConnectReceiver = new ConnectReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG, "register resume");

        IntentFilter registerFilter = new IntentFilter();
        registerFilter.addAction(ConnectService.LOGIN_SERVER_CONNECTED);
        registerFilter.addAction(ConnectService.LOGIN_SERVER_RECONNECT);
        registerFilter.addAction(ChatService.REGISTER_FAILED);
        registerFilter.addAction(ChatService.REGISTER_SUCCESS);
        registerFilter.addAction(ChatService.REGISTER_NO_RESPONSE);
        registerFilter.addAction(ChatService.REGISTER_USER_EXISTS);
        registerReceiver(mConnectReceiver, registerFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG, "register pause");

        unregisterReceiver(mConnectReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "register destory");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btRegister:
                mLoading.show();

                username = etAccount.getText().toString();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();
                if (username == null || username.equals("")) {
                    Toast.makeText(mContext, "账号不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password == null || password.equals("")) {
                    Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (confirmPassword == null || confirmPassword.equals("") || !password.equals(confirmPassword)) {
                    Toast.makeText(mContext, "两次密码不相同", Toast.LENGTH_LONG).show();
                    return;
                }

                host = etHost.getText().toString();
                if (host == null || host.equals("")) {
                    host = "192.168.1.101";
                }

                // 注册,先连接服务器，然后注册
                // 连接xmpp服务器
                SharedPreferencesUtils.setString(mContext, Const.SP_REGISTER_USERNAME,
                        username + "@" + host);
                SharedPreferencesUtils.setString(mContext, Const.SP_REGISTER_PWD, password);
                SharedPreferencesUtils.setString(mContext, Const.SP_REGISTER_USER_TYPE,
                        Const.USER_TYPE_XMPP);
                //SharedPreferencesUtils.setString(mContext, Const.XMPP_REGISTER_HOST, host);
                SharedPreferencesUtils.setString(mContext, Const.XMPP_HOST, host);

                // 服务器改变重新连接
                Intent serverChangeIntent = new Intent(
                        ConnectService.SERVER_CHANGED);
                sendBroadcast(serverChangeIntent);

                break;
            case R.id.llRegisterBack:
                mApp.back();
                break;
        }
    }

    private class ConnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //int reason = intent.getIntExtra("reason", -1);

            String action = intent.getAction();
            Logger.d(TAG, "register action:" + action);

            if (action.equals(ConnectService.LOGIN_SERVER_CONNECTED)) {
                // 注册
                mLoading.setTitle("已连接服务器，注册中");
                Intent registerIntent = new Intent(ChatService.REGISTER);
                sendBroadcast(registerIntent);

                return;
            } else if (action.equals(ConnectService.LOGIN_SERVER_RECONNECT)) {
               mLoading.dismiss();

                // 服务器连接失败
                Toast.makeText(mContext, "服务器连接失败,请稍后重试", Toast.LENGTH_LONG)
                        .show();

                // 断开连接
                Intent closeConnIntent = new Intent(
                        ConnectService.CONNECT_CLOSE);
                sendBroadcast(closeConnIntent);

                return;
            }

            mLoading.dismiss();
            if (action.equals(ChatService.REGISTER_FAILED)) {
                Toast.makeText(mContext, "注册失败，请重试", Toast.LENGTH_LONG).show();
            } else if (action.equals(ChatService.REGISTER_NO_RESPONSE)) {
                Toast.makeText(mContext, "服务器没有响应", Toast.LENGTH_LONG).show();
            } else if (action.equals(ChatService.REGISTER_SUCCESS)) {
                Toast.makeText(mContext, "注册成功,请登陆", Toast.LENGTH_LONG).show();
                RegisterActivity.this.finish();
                UIAnimUtils.sildRightOut(RegisterActivity.this);
            } else if (action.equals(ChatService.REGISTER_USER_EXISTS)) {
                Toast.makeText(mContext, "用户名存在", Toast.LENGTH_LONG).show();
            }
        }

    }
}
