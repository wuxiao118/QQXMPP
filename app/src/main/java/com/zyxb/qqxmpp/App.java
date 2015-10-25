package com.zyxb.qqxmpp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.receiver.NetChangedReceiver;
import com.zyxb.qqxmpp.ui.LoginActivity;
import com.zyxb.qqxmpp.util.UIAnimUtils;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private List<Activity> mActivities;
    private static App mInstance = null;
    // 保存数据
    private DBUser mUser = null;
    private String userType;

    // 连接服务器
    private boolean isConnected = false;
    private boolean isXmppLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivities = new ArrayList<Activity>();
        mInstance = this;

        // 注册screen_on/screen_off receiver,这两个receiver只能动态注册才能获得
        NetChangedReceiver receiver = new NetChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }

    public static App getInstance() {
        return mInstance;
    }

    public Boolean contains(Activity act) {
        return mActivities.contains(act);
    }

    public void add(Activity act) {
        mActivities.add(act);
    }

    public void remove(Activity act) {
        mActivities.remove(act);
    }

    public void back() {
        Activity res = mActivities.remove(mActivities.size() - 1);
        res.finish();
        UIAnimUtils.sildRightOut(res);
    }

    public void login() {
        Activity res = mActivities.remove(mActivities.size() - 1);
        System.out.println(res.getClass().getSimpleName());
        Intent intent = new Intent(res, LoginActivity.class);

        String packageName = res.getPackageName();
        String className = res.getClass().getName();
        intent.putExtra("packageName", packageName);
        intent.putExtra("className", className);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        res.startActivity(intent);

        res.finish();
    }

    public void finish() {
        for (Activity act : mActivities) {
            act.finish();
        }
    }

    public void restart() {
        for (int i = 0; i < mActivities.size() - 1; i++) {
            mActivities.get(i).finish();
            mActivities.remove(i);
            i--;
        }
    }

    public void setmUser(DBUser mUser) {

        this.mUser = mUser;
    }

    public DBUser getmUser() {
        return mUser;
    }

    public boolean isLogin() {
        if (mUser == null) {
            return false;
        }

        return true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isXmppLogin() {
        return isXmppLogin;
    }

    public void setXmppLogin(boolean isXmppLogin) {
        this.isXmppLogin = isXmppLogin;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
