package com.zyxb.qqxmpp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.receiver.NetChangedReceiver3;
import com.zyxb.qqxmpp.ui.LoginActivity;
import com.zyxb.qqxmpp.util.UIAnimUtils;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private List<Activity> mActivities;
    private static App mInstance = null;
    // 保存数据
    private DB3User user = null;
    private String userType;

    // //ping service
    // private Intent pingService;
    // //连接服务器广播
    // private PingReceiver pingReceiver;

    // 连接服务器
    private boolean isConnected = false;
    private boolean isXmppLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivities = new ArrayList<Activity>();
        mInstance = this;

        // 开启ping service
        // Intent pingService = new Intent(this, PingService.class);
        // startService(pingService);

        // 注册receiver
        // pingReceiver = new PingReceiver();
        // registerReceiver(pingReceiver,
        // new IntentFilter(PingService.PING_ACTION));

        // 注册screen_on/screen_off receiver,这两个receiver只能动态注册才能获得
        NetChangedReceiver3 receiver = new NetChangedReceiver3();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
        // registerReceiver(receiver, new
        // IntentFilter(Intent.ACTION_SCREEN_ON));
        // registerReceiver(receiver, new
        // IntentFilter(Intent.ACTION_SCREEN_OFF));
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
        System.out.println("start activity:" + res.getClass().getSimpleName());

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

    public void setUser(DB3User user) {

        this.user = user;
    }

    public DB3User getUser() {
        return user;
    }

    public boolean isLogin() {
        if (user == null) {
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

    // private class PingReceiver extends BroadcastReceiver {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // boolean canConnected = intent.getBooleanExtra(
    // "isConnectedToServer", false);
    // if (canConnected) {
    // // 服务器连接上,自动登录
    // } else {
    // // 服务器未连接上
    // }
    // }
    //
    // }

}
