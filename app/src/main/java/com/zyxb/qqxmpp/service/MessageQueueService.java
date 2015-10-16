package com.zyxb.qqxmpp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.XMPPMessageInfo;
import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.engine.XMPPEngine;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 负责将数据保存到数据库 消息实现Parceable,可直接通过系统传递 可以直接通过intent传递,不使用messenger
 *
 * @author 吴小雄
 */
public class MessageQueueService extends Service {
    private static final String TAG = "MessageQueueService";
    private static int MAX_SIZE = 50;
    private BlockingQueue<XMPPMessageInfo> queue;
    private boolean isRunning = true;
    private Handler handler;
    private Messenger messenger;
    private Service mService;
    private DataEngine engine;

    private MessageReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mService = this;
        queue = new ArrayBlockingQueue<XMPPMessageInfo>(MAX_SIZE);
        isRunning = true;

        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper(), new MyHandlerCallback());
        messenger = new Messenger(handler);

        engine = new DataEngine(mService);

        // 注册receiver,消息通过receiver传递
        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(XMPPEngine.XMPP_QUEUE_MESSAGE);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        XMPPMessageInfo info = queue.take();
                        switch (info.getMessageState()) {
                            case XMPPMessageInfo.USER_ADD:
                                engine.addXMPPUser(getXMPPUser(info));
                                userChangedIntent();
                                break;
                            case XMPPMessageInfo.USER_DELETE:
                                engine.deleteXMPPUser(info.getJid());
                                userChangedIntent();
                                break;
                            case XMPPMessageInfo.USER_UPDATE:
                                engine.updateXMPPUser(getXMPPUser(info));
                                userChangedIntent();
                                break;
                            case XMPPMessageInfo.MESSAGE_ADD:
                                engine.addMessage(getXMPPMessage(info));
                                messageChangedIntent();
                                break;
                            case XMPPMessageInfo.MESSAGE_DELETE:
                                // TODO 删除消息
                                messageChangedIntent();
                                break;
                            case XMPPMessageInfo.MESSAGE_UPDATE:
                                // TODO 更新消息

                                messageChangedIntent();
                                break;
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();


        return START_NOT_STICKY;
    }

    private XMPPUser getXMPPUser(XMPPMessageInfo info) {
        XMPPUser user = new XMPPUser();
        user.setGroup(info.getFriendGroup());
        user.setJid(info.getJid());
        user.setNickname(info.getNickname());
        user.setStatusMessage(info.getStatusMessage());
        user.setStatusMode(info.getStatusMode());

        return user;
    }

    private XMPPMessage getXMPPMessage(XMPPMessageInfo info) {
        XMPPMessage message = new XMPPMessage();
        message.setCreateTime(info.getCreateTime());
        message.setFrom(info.getFrom());
        message.setMsgType(info.getMsgType());
        message.setMsg(info.getMsg());
        message.setState(info.getState());
        message.setTo(info.getTo());

        return message;
    }

    private void userChangedIntent() {
        Intent intent = new Intent(ChatService.USER_DATA_CHANGED);
        mService.sendBroadcast(intent);
    }

    private void messageChangedIntent() {
        Intent intent = new Intent(ChatService.MESSAGE_DATA_CHANGED);
        mService.sendBroadcast(intent);
    }

    /**
     * 接收到消息,放入blocking queue
     *
     * @author Administrator
     */
    private class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            XMPPMessageInfo info = intent.getParcelableExtra("message_info");
            try {
                queue.put(info);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用messenger时,处理消息
     *
     * @author Administrator
     */
    private class MyHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }

    }
}
