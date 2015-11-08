package com.zyxb.qqxmpp.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.ChatAdapter;
import com.zyxb.qqxmpp.adapter.FaceVPAdapter;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.bean.XMPPMessage;
import com.zyxb.qqxmpp.bean.po.DBGroup;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBMessageDAO.OnMessageChangeListener;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.util.DateUtils;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.MyExpressionUtil;
import com.zyxb.qqxmpp.util.SDUtil;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.AudioRecorderButton;
import com.zyxb.qqxmpp.view.DropdownListView;
import com.zyxb.qqxmpp.view.DropdownListView.OnRefreshListenerHeader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 代码可读性：
 * 把表情、语音、添加抽取为单独的类
 */
public class ChatActivity extends BaseActivity implements OnClickListener,
        OnRefreshListenerHeader, OnMessageChangeListener {
    private static final String TAG = "ChatActivity";
    private static final int INTENT_PICTURE = 1;
    private static final int INTENT_CAMERA = 2;

    // title
    private TextView tvTitleBack;
    private TextView tvTitleName;
    private ImageView ivTitlePhone;
    private ImageView ivTitleContact;

    // 消息列表
    private DropdownListView dlMsgList;
    private ChatAdapter mChatAdapter;

    // 底部
    // private LinearLayout llBottom;
    private ImageView ivFace;
    private ImageView ivAdd;
    private EditText etInput;
    private TextView tvSend;

    // 弹出
    private LinearLayout llFaceContainer;
    private LinearLayout llAddContainer;
    private ViewPager vpFace;
    private LinearLayout llDot;
    private TextView tvAddPic;
    private TextView tvAddCamera;
    private TextView tvAddLoc;
    private TextView tvAddFile;
    private TextView tvAddRealTimeVideo;
    private TextView tvAddShake;
    private TextView tvAddMusic;
    //private ImageView ivAddVoice;
    //private LinearLayout llVoice;
    private ImageView ivVoice;
    //private TextView tvVoice;
    //private Button btVoice;
    private AudioRecorderButton btVoice;
    private boolean isKeyBoard = true;

    // 底部face/add是否显示
    private boolean isShowFace = false;
    private boolean isShowAdd = false;

    // 表情图标每页6列4行
    private int columns = 6;
    private int rows = 4;
    // 每页显示的表情view
    private List<View> views = new ArrayList<View>();
    // 表情列表
    private List<String> staticFacesList;

    private LayoutInflater mInflater;

    // 消息
    private List<MessageInfo> messages;
    private DBUser user;
    private int messageType;
    private String account;
    private String toJid;

    //监听新消息
    private NewMessageReceiver mNewMessageReceiver;

    //浏览文件 抽取为单独类
//    private String[] sdCards;
//    private int[] sdIcons;
//    private File[] sdFiles;
//    private FileAdapter adapter;
//    private int filePosition = -1;
//    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.msg_chat);

        tvTitleBack = findView(R.id.tvMsgTitleLeft);
        tvTitleName = findView(R.id.tvMsgTitleName);
        ivTitlePhone = findView(R.id.ivMsgTitleSecond);
        ivTitleContact = findView(R.id.ivMsgTitleRight);

        dlMsgList = findView(R.id.dlMsgList);
        // llBottom = findView(R.id.llMsgBottom);

        ivFace = findView(R.id.ivMsgChatFace);
        ivAdd = findView(R.id.ivMsgChatAdd);
        etInput = findView(R.id.etMsgInput);
        tvSend = findView(R.id.tvMsgSend);

        llFaceContainer = findView(R.id.llMsgFaceContainer);
        llAddContainer = findView(R.id.llMsgAddContainer);
        vpFace = findView(R.id.vpMsgFaceContainer);
        llDot = findView(R.id.llMsgFaceDotContainer);
        tvAddPic = findView(R.id.tvMsgAddPic);
        tvAddCamera = findView(R.id.tvMsgAddCamera);
        tvAddLoc = findView(R.id.tvMsgAddLoc);
        tvAddFile = findView(R.id.tvMsgAddFile);
        tvAddRealTimeVideo = findView(R.id.tvMsgAddRealTimeVideo);
        tvAddShake = findView(R.id.tvMsgAddShake);
        tvAddMusic = findView(R.id.tvMsgAddMusic);
        //ivAddVoice = findView(R.id.ivMsgAddVoice);
        //llVoice = findView(R.id.llMsgAddVoice);
        ivVoice = findView(R.id.ivMsgChatVoice);
        //tvVoice = findView(R.id.tvMsgVoice);
        btVoice = findView(R.id.btMsgVoice);

        initUI();
        initData();
        initEvent();
    }

    private void initUI() {
        isShowFace = false;
        isShowAdd = false;
        hide();

        mInflater = LayoutInflater.from(this);
        staticFacesList = MyExpressionUtil.initStaticFaces(this);

        // 初始化表情
        initViewPager();
    }

    private void initData() {
        // 获取信息
        user = mApp.getmUser();

        Intent intent = getIntent();
        messageType = intent.getIntExtra("type", -1);
        String fromAccount = intent.getStringExtra("fromAccount");
        String toAccount = intent.getStringExtra("toAccount");

        // System.out.println(DBColumns.MESSAGE_TYPES[messageType] + ":" +
        // fromAccount + "--->" +toAccount);
        Logger.d(TAG, DBColumns.MESSAGE_TYPES[messageType] + ":" + fromAccount
                + "--->" + toAccount + ",current user:" + user.getAccount());

        switch (messageType) {
            case DBColumns.MESSAGE_TYPE_CONTACT:
                String contactAccount = fromAccount;
                if (contactAccount.equals(user.getAccount())) {
                    contactAccount = toAccount;
                }
                account = contactAccount;
                messages = mEngine.getContactMessages(contactAccount, this);

                //设置toJid
                toJid = mEngine.getUserInfo(account).getNickname();

                //当messages为0时,toJid为null
                if (messages.size() > 0) {
                    MessageInfo info = messages.get(0);
                    String remark;
                    if (info.getFrom().getAccount().equals(user.getAccount())) {
                        remark = info.getTo().getComments() == null ? info.getTo()
                                .getName() : info.getTo().getComments();
                        //toJid = info.getTo().getName();
                    } else {
                        remark = info.getFrom().getComments() == null ? info
                                .getFrom().getName() : info.getFrom().getComments();
                        //toJid = info.getFrom().getName();
                    }
                    tvTitleName.setText(remark);
                } else {
                    // 获取用户信息
                    tvTitleName.setText(mEngine.getRemark(user.getAccount(),
                            contactAccount));

                    //需要添加toJid信息,否则为null

                }

                mEngine.setReadedMessage(account, mUser.getAccount(), messageType);

                break;
            case DBColumns.MESSAGE_TYPE_GROUP:
                account = toAccount;
                messages = mEngine.getGroupMessages(toAccount, this);
                if (messages.size() > 0) {
                    MessageInfo info = messages.get(0);
                    tvTitleName.setText(info.getTo().getName());
                } else {
                    DBGroup groupInfo = mEngine.getGroupInfo(toAccount);
                    tvTitleName.setText(groupInfo.getName());
                }

                mEngine.setReadedMessage(null, account, messageType);

                break;
            case DBColumns.MESSAGE_TYPE_SYS:
                account = fromAccount;
                messages = mEngine.getSystemMessages(fromAccount, this);
                if (messages.size() > 0) {
                    MessageInfo info = messages.get(0);
                    tvTitleName.setText(info.getFrom().getName());
                }

                mEngine.setReadedMessage(account, mUser.getAccount(), messageType);

                break;
        }

        mChatAdapter = new ChatAdapter(this, user, messages);
        dlMsgList.setAdapter(mChatAdapter);
        // 滚动到最下方 android:transcriptMode="alwaysScroll"
        // android:stackFromBottom="true"
        dlMsgList.setSelection(messages.size() - 1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        tvTitleBack.setOnClickListener(this);
        ivTitlePhone.setOnClickListener(this);
        ivTitleContact.setOnClickListener(this);

        ivFace.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        tvSend.setOnClickListener(this);

        tvAddPic.setOnClickListener(this);
        tvAddCamera.setOnClickListener(this);
        tvAddLoc.setOnClickListener(this);
        tvAddFile.setOnClickListener(this);
        tvAddRealTimeVideo.setOnClickListener(this);
        tvAddShake.setOnClickListener(this);
        tvAddMusic.setOnClickListener(this);
        //ivAddVoice.setOnClickListener(this);
        //llVoice.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        //tvVoice.setOnClickListener(this);
//        tvVoice.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return false;
//            }
//        });
        btVoice.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                // 添加数据并设置状态
                XMPPMessage message = new XMPPMessage();
                long time = new Date().getTime();
                message.setCreateTime(time);
                message.setMsg(filePath);
                message.setFrom(user.getAccount());
                message.setTo(account);
                message.setMsgType(messageType);
                message.setData1(DBColumns.MESSAGE_MSG_TYPE_VOICE);
                message.setState(DBColumns.MESSAGE_STATE_SENDING);
                message.setData2(seconds + "");
                // 数据库中添加数据 ,先由engine写入数据库
                // 连接服务器service完成后由service负责写入数据
                String id = mEngine.addMessage(message);

                // 更新数据,service完成后,由service广播,在receiver中更新
                switch (messageType) {
                    case DBColumns.MESSAGE_TYPE_CONTACT:
                        messages = mEngine.getContactMessages(account, ChatActivity.this);
                        break;
                    case DBColumns.MESSAGE_TYPE_GROUP:
                        messages = mEngine.getGroupMessages(account, ChatActivity.this);
                        break;
                    case DBColumns.MESSAGE_TYPE_SYS:
                        messages = mEngine.getSystemMessages(account, ChatActivity.this);
                        break;
                }
                mChatAdapter.setMessages(messages);
                mChatAdapter.notifyDataSetChanged();

                //滚动到最下方
                dlMsgList.setSelection(messages.size() - 1);

                // 如果网络连通,并登陆,通过smack发送
                if (mApp.isConnected()) {
                    //绑定service发送or使用sendBroadcast发送
                    //暂定使用sendBroadcast发送
                    Intent msgIntent = new Intent();
                    msgIntent.setAction(ChatService.MESSAGE_SEND_FILE);
                    msgIntent.putExtra("filePath", filePath);
                    msgIntent.putExtra("toJid", toJid);
                    sendBroadcast(msgIntent);
                }
            }
        });

        etInput.setOnClickListener(this);

        //文本改变
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    tvSend.setVisibility(View.GONE);
                    //ivAddVoice.setVisibility(View.VISIBLE);
                    //llVoice.setVisibility(View.VISIBLE);
                    ivVoice.setVisibility(View.VISIBLE);
                } else {
                    tvSend.setVisibility(View.VISIBLE);
                    //ivAddVoice.setVisibility(View.GONE);
                    //llVoice.setVisibility(View.GONE);
                    ivVoice.setVisibility(View.GONE);
                }
            }
        });

        // 初始化消息列表
        dlMsgList.setOnRefreshListenerHead(this);
        dlMsgList.setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftInputView();
                    if (isShowAdd || isShowFace) {
                        hide();
                    }
                }

                return false;
            }
        });

        vpFace.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 改变下方点
                for (int i = 0; i < llDot.getChildCount(); i++) {
                    llDot.getChildAt(i).setSelected(false);
                }
                llDot.getChildAt(position).setSelected(true);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

    }

    /**
     * 初始化表情
     */
    private void initViewPager() {
        int pagesize = MyExpressionUtil.getPagerCount(staticFacesList.size(),
                columns, rows);
        // 获取页数
        for (int i = 0; i < pagesize; i++) {
            views.add(MyExpressionUtil.viewPagerItem(this, i, staticFacesList,
                    columns, rows, etInput));
            LayoutParams params = new LayoutParams(16, 16);
            llDot.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        vpFace.setAdapter(mVpAdapter);
        llDot.getChildAt(0).setSelected(true);
    }

    /**
     * 表情页切换时，底部小圆点
     *
     * @param position
     * @return
     */
    @SuppressLint("InflateParams")
    private ImageView dotsItem(int position) {
        View layout = mInflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvMsgTitleLeft:
                // back
                mApp.back();
                break;
            case R.id.ivMsgTitleSecond:
                // phone

                break;
            //case R.id.ivMsgAddVoice:
            //case R.id.llMsgAddVoice:
            case R.id.ivMsgChatVoice:
                // 语音切换按钮
                //隐藏输入法、表情,如果弹出
                hideSoftInputView();
                if (isShowFace) {
                    hide();
                }
                if (isShowAdd) {
                    hide();
                }

                if (isKeyBoard) {
                    //输入框换成语音
//                    etInput.setVisibility(View.GONE);
//                    tvVoice.setVisibility(View.VISIBLE);
//                    ivVoice.setImageResource(R.drawable.btn_chat_add_keyboard_selector);
//                    isKeyBoard = false;
                    showVoice();
                } else {
                    //换成输入框
//                    etInput.setVisibility(View.VISIBLE);
//                    tvVoice.setVisibility(View.GONE);
//                    ivVoice.setImageResource(R.drawable.btn_chat_add_voice_selector);
//                    isKeyBoard = true;
                    showKeyBoard();
                }

                break;
//            case R.id.tvMsgVoice:
//                // 语音说话按钮
//                break;
            case R.id.ivMsgTitleRight:
                // 个人/群/系统组 信息
                Intent intent = null;
                switch (messageType) {
                    case DBColumns.MESSAGE_TYPE_CONTACT:
                        intent = new Intent(this, FriendChatSettingActivity.class);
                        // 传递好友ID
                        break;
                    case DBColumns.MESSAGE_TYPE_GROUP:
                        intent = new Intent(this, GroupDetailActivity.class);
                        // 传递群ID
                        break;
                    case DBColumns.MESSAGE_TYPE_SYS:

                        return;
                    // break;
                }
                intent.putExtra("account", account);
                startActivity(intent);
                UIAnimUtils.sildLeftIn(this);

                break;
            case R.id.ivMsgChatFace:
                // 表情
                if (isShowFace) {
                    hide();
                } else {
                    show(llFaceContainer);
                    isShowFace = true;
                }

                //语音情况下,点击时,回到键盘输入状态
                if (!isKeyBoard) {
                    showKeyBoard();
                }
                break;
            case R.id.etMsgInput:
                hide();
                break;
            case R.id.ivMsgChatAdd:
                // 添加照片位置等
                if (isShowAdd) {
                    hide();
                } else {
                    show(llAddContainer);
                    isShowAdd = true;
                }
                //语音情况下,点击时,回到键盘输入状态
                if (!isKeyBoard) {
                    showKeyBoard();
                }
                break;
            case R.id.tvMsgSend:
                // 发送文本消息
                String text = etInput.getText().toString();
                if (text.trim().equals("")) {
                    Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
                    Animation anim = AnimationUtils.loadAnimation(this,
                            R.anim.shake);
                    etInput.startAnimation(anim);
                } else {
                    // 添加数据并设置状态
                    XMPPMessage message = new XMPPMessage();
                    long time = new Date().getTime();
                    message.setCreateTime(time);
                    message.setMsg(text);
                    message.setFrom(user.getAccount());
                    message.setTo(account);
                    message.setMsgType(messageType);
                    message.setState(DBColumns.MESSAGE_STATE_SENDING);
                    // 数据库中添加数据 ,先由engine写入数据库
                    // 连接服务器service完成后由service负责写入数据
                    //mEngine.addMessage(message);
                    String id = mEngine.addMessage(message);
                    //message = null;

                    // 更新数据,service完成后,由service广播,在receiver中更新
                    switch (messageType) {
                        case DBColumns.MESSAGE_TYPE_CONTACT:
                            messages = mEngine.getContactMessages(account, this);
                            break;
                        case DBColumns.MESSAGE_TYPE_GROUP:
                            messages = mEngine.getGroupMessages(account, this);
                            break;
                        case DBColumns.MESSAGE_TYPE_SYS:
                            messages = mEngine.getSystemMessages(account, this);
                            break;
                    }
                    mChatAdapter.setMessages(messages);
                    mChatAdapter.notifyDataSetChanged();

                    etInput.setText("");

                    //滚动到最下方
                    dlMsgList.setSelection(messages.size() - 1);

                    // 关闭键盘

                    //Logger.d(TAG,"Login in：" + mApp.isConnected());
                    // 如果网络连通,并登陆,通过smack发送
                    if (mApp.isConnected()) {
                        //绑定service发送or使用sendBroadcast发送
                        //暂定使用sendBroadcast发送
                        Intent msgIntent = new Intent();
                        msgIntent.setAction(ChatService.XMPP_MESSAGE);
                        msgIntent.putExtra("xmpp_message_type", ChatService.MESSAGE_TYPE_TXT);
                        msgIntent.putExtra("id", id);
                        //msgIntent.putExtra("toJid",account + "@" + Const.XMPP_HOST);
                        msgIntent.putExtra("toJid", toJid);//有时候为null,why????没有消息的时候未设置toJid,已更正
                        msgIntent.putExtra("message", text);
                        sendBroadcast(msgIntent);
                    }
                }

                break;
            case R.id.tvMsgAddPic:
                // 发送相册照片
                Intent picIntent = new Intent();
                /* 开启Pictures画面Type设定为image */
                picIntent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                picIntent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(picIntent, INTENT_PICTURE);

                //切换效果 出去的变暗,进来的由底到上
                UIAnimUtils.sildBottomIn(this);

                //如何设置返回的切换效果?????

                break;
            case R.id.tvMsgAddCamera:
                // 发送相机拍摄图片
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, INTENT_CAMERA);
                break;
            case R.id.tvMsgAddLoc:
                // 发送位置

                break;
            case R.id.tvMsgAddFile:
                // 发送文件
                //showFileDialog();
                new FileDialog(mContext);
                break;
            case R.id.tvMsgAddRealTimeVideo:
                // 实时视频

                break;
            case R.id.tvMsgAddShake:
                // 发送抖动

                break;
            case R.id.tvMsgAddMusic:
                // 发送音乐

                break;
        }
    }

    //显示键盘输入状态
    private void showKeyBoard() {
        //换成输入框
        etInput.setVisibility(View.VISIBLE);
        //tvVoice.setVisibility(View.GONE);
        btVoice.setVisibility(View.GONE);
        ivVoice.setImageResource(R.drawable.btn_chat_add_voice_selector);
        isKeyBoard = true;
    }

    //显示语音输入状态
    private void showVoice() {
        //输入框换成语音
        etInput.setVisibility(View.GONE);
        //tvVoice.setVisibility(View.VISIBLE);
        btVoice.setVisibility(View.VISIBLE);
        ivVoice.setImageResource(R.drawable.btn_chat_add_keyboard_selector);
        isKeyBoard = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_PICTURE:
//                    Uri uri = data.getData();
//                    Logger.d(TAG + " uri ", uri.toString());
//                    ContentResolver cr = this.getContentResolver();
//                    try {
//                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//                        //显示图片
//
//                    } catch (FileNotFoundException e) {
//                        Logger.d(TAG + " Exception", e.getMessage());
//                    }

                    /*
                    try {
                        Uri originalUri = data.getData();        //获得图片的uri
                        //ContentResolver cr = this.getContentResolver();

                        Bitmap bm = MediaStore.Images.Media.getBitmap(cr, originalUri); //显得到bitmap图片
                        //这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};

                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);

                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();

                        //最后根据索引值获取图片路径
                        String path = cursor.getString(column_index);
                    }catch(FileNotFoundException e){

                    }catch(IOException e){

                    }*/

                    //获得图片的uri
                    Uri originalUri = data.getData();

                    //获取图片的路径：
                    String[] proj = {MediaStore.Images.Media.DATA};
                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    Logger.d(TAG, "image path:" + path);

                    // 添加数据并设置状态
                    XMPPMessage message = new XMPPMessage();
                    long time = new Date().getTime();
                    message.setCreateTime(time);
                    message.setMsg(path);
                    message.setFrom(user.getAccount());
                    message.setTo(account);
                    message.setMsgType(messageType);
                    message.setData1(DBColumns.MESSAGE_MSG_TYPE_IMAGE);
                    message.setState(DBColumns.MESSAGE_STATE_SENDING);
                    // 数据库中添加数据 ,先由engine写入数据库
                    // 连接服务器service完成后由service负责写入数据
                    String id = mEngine.addMessage(message);

                    // 更新数据,service完成后,由service广播,在receiver中更新
                    switch (messageType) {
                        case DBColumns.MESSAGE_TYPE_CONTACT:
                            messages = mEngine.getContactMessages(account, this);
                            break;
                        case DBColumns.MESSAGE_TYPE_GROUP:
                            messages = mEngine.getGroupMessages(account, this);
                            break;
                        case DBColumns.MESSAGE_TYPE_SYS:
                            messages = mEngine.getSystemMessages(account, this);
                            break;
                    }
                    mChatAdapter.setMessages(messages);
                    mChatAdapter.notifyDataSetChanged();

                    etInput.setText("");

                    //滚动到最下方
                    dlMsgList.setSelection(messages.size() - 1);

                    // 关闭键盘
                    hideSoftInputView();

                    // 如果网络连通,并登陆,通过smack发送
                    if (mApp.isConnected()) {
                        //绑定service发送or使用sendBroadcast发送
                        //暂定使用sendBroadcast发送
                        Intent msgIntent = new Intent();
                        //msgIntent.setAction(ChatService.XMPP_MESSAGE);
                        //msgIntent.putExtra("xmpp_message_type", ChatService.MESSAGE_TYPE_IMAGE);
                        //msgIntent.putExtra("id", id);
                        //msgIntent.putExtra("toJid", toJid);
                        //msgIntent.putExtra("message", path);
                        msgIntent.setAction(ChatService.MESSAGE_SEND_FILE);
                        msgIntent.putExtra("filePath", path);
                        msgIntent.putExtra("toJid", toJid);
                        sendBroadcast(msgIntent);
                    }

                    break;
                case INTENT_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                        Log.i("TestFile",
                                "SD card is not avaiable/writeable right now.");
                        return;
                    }
                    String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                    Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

                    FileOutputStream b = null;
                    //???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
                    File file = new File("/sdcard/myImage/");
                    file.mkdirs();// 创建文件夹
                    String fileName = "/sdcard/myImage/" + name;

                    try {
                        b = new FileOutputStream(fileName);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //修改数据库,更新数据源

                    // 添加数据并设置状态
                    XMPPMessage msg = new XMPPMessage();
                    long tm = new Date().getTime();
                    msg.setCreateTime(tm);
                    msg.setMsg(fileName);
                    msg.setFrom(user.getAccount());
                    msg.setTo(account);
                    msg.setMsgType(messageType);
                    msg.setData1(DBColumns.MESSAGE_MSG_TYPE_IMAGE);
                    msg.setState(DBColumns.MESSAGE_STATE_SENDING);
                    // 数据库中添加数据 ,先由engine写入数据库
                    // 连接服务器service完成后由service负责写入数据
                    String idd = mEngine.addMessage(msg);

                    // 更新数据,service完成后,由service广播,在receiver中更新
                    switch (messageType) {
                        case DBColumns.MESSAGE_TYPE_CONTACT:
                            messages = mEngine.getContactMessages(account, this);
                            break;
                        case DBColumns.MESSAGE_TYPE_GROUP:
                            messages = mEngine.getGroupMessages(account, this);
                            break;
                        case DBColumns.MESSAGE_TYPE_SYS:
                            messages = mEngine.getSystemMessages(account, this);
                            break;
                    }
                    mChatAdapter.setMessages(messages);
                    mChatAdapter.notifyDataSetChanged();

                    etInput.setText("");

                    //滚动到最下方
                    dlMsgList.setSelection(messages.size() - 1);

                    // 关闭键盘
                    hideSoftInputView();

                    // 如果网络连通,并登陆,通过smack发送
                    if (mApp.isConnected()) {
                        //绑定service发送or使用sendBroadcast发送
                        //暂定使用sendBroadcast发送
                        Intent msgIntent = new Intent();
                        //msgIntent.setAction(ChatService.XMPP_MESSAGE);
                        //msgIntent.putExtra("xmpp_message_type", DBColumns.MESSAGE_MSG_TYPE_IMAGE);
                        //msgIntent.putExtra("id", idd);
                        //msgIntent.putExtra("toJid", toJid);
                        //msgIntent.putExtra("message", fileName);
                        msgIntent.setAction(ChatService.MESSAGE_SEND_FILE);
                        msgIntent.putExtra("filePath", fileName);
                        msgIntent.putExtra("toJid", toJid);
                        sendBroadcast(msgIntent);
                    }

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 让输入框获取焦点
                etInput.requestFocus();
            }
        }, 100);

        //启动消息监听
        mNewMessageReceiver = new NewMessageReceiver();
        IntentFilter newMsgFilter = new IntentFilter();
        newMsgFilter.addAction(ChatService.MESSAGE_DATA_CHANGED);
        registerReceiver(mNewMessageReceiver, newMsgFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mNewMessageReceiver);
    }

    /**
     * 监听返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideSoftInputView();
            if (llFaceContainer.getVisibility() == View.VISIBLE) {
                llFaceContainer.setVisibility(View.GONE);
            } else if (llAddContainer.getVisibility() == View.VISIBLE) {
                llAddContainer.setVisibility(View.GONE);
            } else {
                finish();
                UIAnimUtils.sildRightOut(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void show(LinearLayout ll) {
        // 如果键盘弹出状态,关闭键盘
        hideSoftInputView();

        hide();
        ll.setVisibility(View.VISIBLE);
    }

    private void hide() {
        llFaceContainer.setVisibility(View.GONE);
        llAddContainer.setVisibility(View.GONE);
        isShowAdd = false;
        isShowFace = false;
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                dlMsgList.onRefreshCompleteHeader();
                Toast.makeText(mContext, "刷新完成", Toast.LENGTH_LONG).show();
            }

        }, 1000);
    }

    @Override
    public void onPreMessageChange(int type) {

    }

    @Override
    public void onMessageChanged(int type) {

    }

    @Override
    public void onError(int type) {

    }

    /**
     * 监听新消息
     */
    private class NewMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //消息状态改变,刷新
            switch (messageType) {
                case DBColumns.MESSAGE_TYPE_CONTACT:
                    messages = mEngine.getContactMessages(account, ChatActivity.this);
                    //修改消息状态为已读
                    mEngine.setReadedMessage(account, mUser.getAccount(), messageType);

                    break;
                case DBColumns.MESSAGE_TYPE_GROUP:
                    messages = mEngine.getGroupMessages(account, ChatActivity.this);
                    //修改消息状态为已读
                    mEngine.setReadedMessage(null, account, messageType);

                    break;
                case DBColumns.MESSAGE_TYPE_SYS:
                    messages = mEngine.getSystemMessages(account, ChatActivity.this);
                    //修改消息状态为已读
                    mEngine.setReadedMessage(account, mUser.getAccount(), messageType);

                    break;
            }

            mChatAdapter.setMessages(messages);
            mChatAdapter.notifyDataSetChanged();
        }
    }

    //TODO 如何得知对方确认接收还是拒绝接收?获取发送的进度?
    private class FileDialog {
        private Context mContext;
        private String phonePath;
        private String sdOutPath;
        private String sdInPath;
        private String[] sdCards;
        private int[] sdIcons;
        private File[] sdFiles;

        private int filePosition = -1;
        private FileAdapter mAdapter;
        private ExplorerFileFilter mFileFilter;
        private AlertDialog mDialog;

        private View view;
        private TextView tvPath, tvBack;
        private ListView lvFiles;

        public FileDialog(Context context) {
            mContext = context;

            initDialogData();
            ui();
            event();
            show();
        }

        private void initDialogData() {
            //手机自身内存
            phonePath = SDUtil.getPhoneCardPath();

            //sd卡路径
            sdOutPath = null;

            //判断sd卡是否能用
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                sdOutPath = SDUtil.getSDCardPath();
            }

            //获取sd卡路径
            sdInPath = SDUtil.getNormalSDCardPath();
            //String sdOutPath = SDUtil.getSDCardPath();
            //Logger.d(TAG, "手机自身内存:" + phonePath + ",内部sd卡:" + sdInPath + ",外部sd卡:" + sdOutPath);

            if (sdOutPath == null) {
                sdCards = new String[]{"Device data"};
                sdIcons = new int[]{R.drawable.dir_phone};
                sdFiles = new File[]{new File(phonePath)};
            } else if (sdInPath.equals(sdOutPath)) {
                //只有一张卡
                //初始化数据
                sdCards = new String[]{"Device data", "SD memory card"};
                sdIcons = new int[]{R.drawable.dir_phone, R.drawable.dir_sdcard};
                sdFiles = new File[]{new File(phonePath), new File(sdOutPath)};
            } else {
                //两张卡
                //初始化数据
                sdCards = new String[]{"Device data", "Device storage", "SD memory card"};
                sdIcons = new int[]{R.drawable.dir_phone, R.drawable.dir_sdcard, R.drawable.dir_sdcard};
                sdFiles = new File[]{new File(phonePath), new File(sdInPath), new File(sdOutPath)};
            }
        }

        private void ui() {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_file, null);
            tvPath = (TextView) view.findViewById(R.id.tvChatFilePath);
            lvFiles = (ListView) view.findViewById(R.id.lvChatFileList);
            tvBack = (TextView) view.findViewById(R.id.tvChatFileBack);
            tvPath.setText("root");
        }

        private void event() {
            lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    filePosition = -1;
                    File f = sdFiles[i];
                    if (f.isDirectory()) {
                        if (f.listFiles() != null && f.listFiles(mFileFilter).length > 0) {
                            String path = sdFiles[i].getAbsolutePath();
                            tvPath.setText(path);
                            tvBack.setVisibility(View.VISIBLE);

                            //更新list数据
                            sdFiles = f.listFiles(mFileFilter);
                            mAdapter.setFiles(sdFiles);
                            mAdapter.setIcons(null);
                            mAdapter.setNames(null);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "文件夹为空", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //更新选择
                        mAdapter.setSelectPosition(i);
                        mAdapter.setFiles(sdFiles);
                        mAdapter.notifyDataSetChanged();

                        filePosition = i;
                    }
                }
            });

            tvBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    filePosition = -1;
                    String path = tvPath.getText().toString();

                    //最初数据
                    initDialogData();
                    //是否回到根
                    if (path.equals(phonePath) || (sdOutPath != null && sdOutPath.equals(path)) || (sdInPath != null && sdInPath.equals(path))) {

                        //设置adapter数据
                        mAdapter.setFiles(sdFiles);
                        mAdapter.setIcons(sdIcons);
                        mAdapter.setNames(sdCards);
                        mAdapter.setSelectPosition(-1);
                        mAdapter.notifyDataSetChanged();

                        tvBack.setVisibility(View.GONE);
                        tvPath.setText("root");

                        return;
                    }

                    mFileFilter = new ExplorerFileFilter();

                    String newPath = path.substring(0, path.lastIndexOf(File.separatorChar));
                    tvPath.setText(newPath);
                    sdFiles = new File(newPath).listFiles(mFileFilter);
                    mAdapter.setFiles(sdFiles);
                    mAdapter.setSelectPosition(-1);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        private void show() {
            //设置adapter数据
            mAdapter = new FileAdapter(sdFiles);
            mAdapter.setIcons(sdIcons);
            mAdapter.setNames(sdCards);
            lvFiles.setAdapter(mAdapter);

            mDialog = new AlertDialog.Builder(mContext)
                    .setTitle("选择要发送的文件")
                    .setView(view)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (filePosition == -1) {
                                //未选择
                                Toast.makeText(mContext, "还未选择文件", Toast.LENGTH_LONG).show();
                                keepDialogOpen();
                            } else {
                                String filePath = sdFiles[filePosition].getAbsolutePath();
                                Logger.d(TAG, "send file:" + filePath);
                                //TODO 写入数据库,文件标示为待发送,若服务器未连接,可等服务器连接后,后台默默发送
                                XMPPMessage msg = new XMPPMessage();
                                long tm = new Date().getTime();
                                msg.setCreateTime(tm);
                                msg.setMsg(filePath);
                                msg.setFrom(user.getAccount());
                                msg.setTo(account);
                                msg.setMsgType(messageType);
                                msg.setData1(DBColumns.MESSAGE_MSG_TYPE_FILE);
                                msg.setState(DBColumns.MESSAGE_STATE_SENDING);
                                // 数据库中添加数据 ,先由engine写入数据库
                                // 连接服务器service完成后由service负责写入数据
                                String idd = mEngine.addMessage(msg);

                                // 更新数据,service完成后,由service广播,在receiver中更新
                                switch (messageType) {
                                    case DBColumns.MESSAGE_TYPE_CONTACT:
                                        messages = mEngine.getContactMessages(account, ChatActivity.this);
                                        break;
                                    case DBColumns.MESSAGE_TYPE_GROUP:
                                        messages = mEngine.getGroupMessages(account, ChatActivity.this);
                                        break;
                                    case DBColumns.MESSAGE_TYPE_SYS:
                                        messages = mEngine.getSystemMessages(account, ChatActivity.this);
                                        break;
                                }
                                mChatAdapter.setMessages(messages);
                                mChatAdapter.notifyDataSetChanged();

                                etInput.setText("");

                                //滚动到最下方
                                dlMsgList.setSelection(messages.size() - 1);

                                // 关闭键盘
                                hideSoftInputView();

                                if (mApp.isConnected()) {
                                    //发送文件Intent
                                    Intent sendFileIntent = new Intent();
                                    sendFileIntent.setAction(ChatService.MESSAGE_SEND_FILE);
                                    sendFileIntent.putExtra("filePath", filePath);
                                    sendFileIntent.putExtra("toJid", toJid);
                                    mContext.sendBroadcast(sendFileIntent);
                                }

                                closeDialog();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            closeDialog();
                        }
                    }).create();
            mDialog.show();
        }

        private void keepDialogOpen() {
            try {
                Field field = mDialog.getClass().getSuperclass().getDeclaredField("mShowing");//dialog可能未初始化
                field.setAccessible(true);
                field.set(mDialog, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void closeDialog() {
            try {
                java.lang.reflect.Field field = mDialog.getClass().getSuperclass().getDeclaredField("mShowing");
                field.setAccessible(true);
                field.set(mDialog, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private class FileAdapter extends BaseAdapter {
            private File[] files;
            private int[] icons;
            private String[] names;
            private LayoutInflater inflater;
            private ExplorerFileFilter fileFilter;
            private int selectPosition = -1;

            public FileAdapter(File[] files) {
                this.files = files;
                inflater = LayoutInflater.from(mContext);
                fileFilter = new ExplorerFileFilter();
            }

            public void setSelectPosition(int selectPosition) {
                this.selectPosition = selectPosition;
            }

            public void setFiles(File[] files) {
                this.files = files;
            }

            public void setNames(String[] names) {
                this.names = names;
            }

            public void setIcons(int[] icons) {
                this.icons = icons;
            }

            @Override
            public int getCount() {
                return files.length;
            }

            @Override
            public Object getItem(int i) {
                return files[i];
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                ViewHolder holder;
                if (view == null) {
                    view = inflater.inflate(R.layout.chat_file_list_item, null);
                    holder = new ViewHolder();
                    holder.ivIcon = (ImageView) view.findViewById(R.id.ivChatFileIcon);
                    holder.tvName = (TextView) view.findViewById(R.id.tvChatFileName);
                    holder.tvCreateTime = (TextView) view.findViewById(R.id.tvChatFileCreateTime);
                    holder.rbSelection = (RadioButton) view.findViewById(R.id.rbChatFileSelection);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }

                if ((i & 1) == 0) {
                    view.setBackgroundColor(getResources().getColor(R.color.bg_blue));
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.blue));
                }

                //设置值
                if (icons != null) {
                    holder.ivIcon.setImageResource(icons[i]);
                } else {
                    File f = files[i];
                    if (f.isDirectory()) {
                        if (null != f.listFiles() && f.listFiles(fileFilter).length > 0) {
                            //holder.ivIcon.setImageResource(getIconRes(f.getName()));
                            holder.ivIcon.setImageResource(R.drawable.folder_);
                        } else {
                            holder.ivIcon.setImageResource(R.drawable.folder);
                        }
                    } else {
                        holder.ivIcon.setImageResource(getIconRes(f.getName()));
                    }
                }

                if (names != null) {
                    holder.tvName.setText(names[i]);
                } else {
                    String name = files[i].getName();
                    holder.tvName.setText(name);
                }

                if (files[i].isDirectory()) {
                    holder.rbSelection.setVisibility(View.INVISIBLE);
                } else {
                    holder.rbSelection.setVisibility(View.VISIBLE);
                    //Logger.d("adapter","before");
                    if (selectPosition == i) {
                        //Logger.d("adapter","after");
//                    holder.rbSelection.setSelected(true);//不行，不是选中状态
                        holder.rbSelection.setChecked(true);
                    } else {
                        holder.rbSelection.setChecked(false);
                    }
                }

                long time = files[i].lastModified();
                holder.tvCreateTime.setText(DateUtils.format(time, "yyyy-MM-dd"));

                return view;
            }

            private int getIconRes(String filename) {

                String suffix = filename.substring(filename.lastIndexOf(".") + 1).trim();

                try {
                    Field field = Class.forName("com.zyxb.qqxmpp.R$drawable").getField(
                            suffix);
                    return field.getInt(field);
                } catch (Exception e) {
                    return R.drawable.default_fileicon;
                }

            }
        }

        private class ViewHolder {
            public ImageView ivIcon;
            public TextView tvName, tvCreateTime;
            public RadioButton rbSelection;
        }

        //文件过滤器
        private class ExplorerFileFilter implements FileFilter {

            @Override
            public boolean accept(File pathname) {
                if (!pathname.getName().startsWith("."))
                    return true;

                return false;
            }

        }
    }
}
