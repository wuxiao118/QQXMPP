package com.zyxb.qqxmpp.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.MainActivity;
import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.adapter.ExAdapter;
import com.zyxb.qqxmpp.bean.FriendGroupInfo;
import com.zyxb.qqxmpp.bean.Information;
import com.zyxb.qqxmpp.db.dao.DBUserDAO.OnUserChangeListener;
import com.zyxb.qqxmpp.engine.DataEngine;
import com.zyxb.qqxmpp.service.ChatService;
import com.zyxb.qqxmpp.ui.FriendDtailActivity;
import com.zyxb.qqxmpp.util.Const;
import com.zyxb.qqxmpp.util.Logger;
import com.zyxb.qqxmpp.util.ToastUtil;
import com.zyxb.qqxmpp.view.XExpandableListView;
import com.zyxb.qqxmpp.view.XExpandableListView.IXExpandableListViewListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements OnItemClickListener,
        OnClickListener, IXExpandableListViewListener, OnGroupExpandListener,
        OnGroupCollapseListener, OnGroupClickListener, OnChildClickListener,
        OnUserChangeListener {
    private static final String TAG = "ContactFragment";
    private XExpandableListView lvContactList;
    private TextView tvContactAdd;

    private MainActivity mContext;
    // private DBUser mUser;

    private ExAdapter mAdapter;
    private List<FriendGroupInfo> groups;
    private DataEngine mEngine;

    //添加的位置
    private int mAddPosition = 0;
    private static final int FRIEND_POSITION = 0;
    private static final int FRIEND_GROUP_POSITION = 1;
    //dialog
    //private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 检查登陆
        mContext = (MainActivity) getActivity();
        // mUser = mContext.getmUser();

        initData();
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact, null);
        lvContactList = (XExpandableListView) view
                .findViewById(R.id.elContactList);
        tvContactAdd = (TextView) view.findViewById(R.id.tvContactAdd);

        initEvent();

        return view;
    }

    private void initEvent() {
        lvContactList.setOnItemClickListener(this);
        tvContactAdd.setOnClickListener(this);
        lvContactList.setPullRefreshEnable(true);
        lvContactList.setPullLoadEnable(false);
        lvContactList.setExpandableListViewListener(this);
        lvContactList.setOnGroupExpandListener(this);
        lvContactList.setOnGroupCollapseListener(this);
        lvContactList.setOnGroupClickListener(this);
        lvContactList.setOnChildClickListener(this);
    }

    private void initData() {
        mContext = (MainActivity) getActivity();
        mEngine = mContext.getmEngine();

        createData();

        mAdapter = new ExAdapter(mContext, groups);
        lvContactList.setAdapter(mAdapter);
    }

    private void createData() {
        groups = mEngine.getFriends(this);

        // groups中添加我的设备
        List<Information> friends = null;
        if (groups.size() == 0) {
            friends = new ArrayList<Information>();
            FriendGroupInfo info = new FriendGroupInfo();
            info.setFriends(friends);
            info.setName("我的设备");
            groups.add(info);
        } else {
            friends = groups.get(0).getFriends();
        }
        Information friend = new Information();
        friend.setName("我的电脑");
        friend.setState(Const.LOGIN_STATE_OFFLINE);
        friend.setChannel(Const.LOGIN_CHANNEL_UNKNOWN);
        friend.setRenew("无需数据线，手机轻松传文件到电脑。");
        friends.add(friend);
        friend = null;
        friend = new Information();
        friend.setName("所搜新设备");
        friend.setState(Const.LOGIN_STATE_ONLINE);
        friend.setChannel(Const.LOGIN_CHANNEL_UNKNOWN);
        friend.setRenew("搜索附近的设备，用QQ轻松连接设备。");
        friends.add(friend);
        friend = null;
        groups.get(0).setCount(1);
    }

    public void updateContact() {
        //更新好友数据
        createData();
        mAdapter.setGroups(groups);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // mAdapter.setGroupClickPosition(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvContactAdd:
                mAddPosition = 0;

                //弹出dialog
                new AlertDialog.Builder(getActivity())
                        .setTitle("选择要添加的类型")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(new String[]{"好友", "好友分组"}, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Logger.d(TAG,"single choice:" + i);
                                //position = i;
                                mAddPosition = i;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Logger.d(TAG, "selected:" + i);
                                switch (mAddPosition) {
                                    case FRIEND_POSITION:
                                        //添加好友
                                        //showFriendDialog();
                                        new AddUserDialog(mContext);
                                        break;
                                    case FRIEND_GROUP_POSITION:
                                        //添加好友分组
                                        //showFriendGroupDialog();
                                        new FriendGroupDialog(mContext);
                                        break;
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
                break;
        }
    }

    private class AddUserDialog{
        private Context mContext;
        private View view;
        private TextView tvFind ;
        private TextView tvOnline ;
        private ListView lvResult ;
        private LinearLayout llSearch ;
        private EditText etContent ;
        private TextView tvSearch ;
        private UserSearchAdapter adapter;
        private AlertDialog mDialog;

        //监听user search
        private UserSearchResultReceiver mUserSearchResultReceiver;

        public AddUserDialog(Context context){
            mContext = context;

            ui();
            event();
            //initList();
            create();
        }

        private void ui(){
            view = LayoutInflater.from(mContext).inflate(R.layout.contact_add_friend, null);
            tvFind = (TextView) view.findViewById(R.id.tvContactAddFind);
            tvOnline = (TextView) view.findViewById(R.id.tvContactAddOnline);
            lvResult = (ListView) view.findViewById(R.id.lvContactAddResultList);
            llSearch = (LinearLayout)view.findViewById(R.id.llContactAddSearch);
            etContent = (EditText)view.findViewById(R.id.etContactAddContent);
            tvSearch = (TextView)view.findViewById(R.id.tvContactAddSearch);
        }

        private void event(){
            tvSearch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tvSearch.getText().toString().equals("搜索")) {
                        String content = etContent.getText().toString();
                        if (content == null || content.trim().equals("")) {
                            ToastUtil.showLongToast(mContext, "搜索内容不能为空");
                            return;
                        }

                        tvSearch.setText("取消");

                        //根据名称或者账号查找用户
                        Intent searchUserIntent = new Intent();
                        searchUserIntent.setAction(ChatService.USER_SEARCH);
                        searchUserIntent.putExtra("username", content);
                        mContext.sendBroadcast(searchUserIntent);

                        //监听
                        mUserSearchResultReceiver = new UserSearchResultReceiver();
                        IntentFilter userSearchIntent = new IntentFilter();
                        userSearchIntent.addAction(ChatService.USER_SEARCH_RESULT);
                        mContext.registerReceiver(mUserSearchResultReceiver, userSearchIntent);
                    }else{
                        tvSearch.setText("搜索");
                        etContent.setText("");

                        //取消监听
                        mContext.unregisterReceiver(mUserSearchResultReceiver);
                        //清空list
                        //lvResult.removeAllViews();//UnsupportedOperationException
                    }
                }
            });

            tvFind.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvFind.setTextColor(getResources().getColor(R.color.setting_line_color));
                    tvFind.setBackgroundResource(R.drawable.login_btn_pressed);
                    tvOnline.setTextColor(getResources().getColor(R.color.group_color_gray2));
                    tvOnline.setBackgroundResource(R.drawable.login_btn_disabled);
                    llSearch.setVisibility(View.VISIBLE);
                }
            });

            tvOnline.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvOnline.setTextColor(getResources().getColor(R.color.setting_line_color));
                    tvOnline.setBackgroundResource(R.drawable.login_btn_pressed);
                    tvFind.setTextColor(getResources().getColor(R.color.group_color_gray2));
                    tvFind.setBackgroundResource(com.zyxb.qqxmpp.R.drawable.login_btn_disabled);
                    llSearch.setVisibility(View.GONE);

                    //TODO 根据条件查找在线用户(貌似xmpp不能直接获取服务器上所有用户)
                }
            });
        }

        private void create(){
            mDialog = new AlertDialog.Builder(mContext)
                    .setTitle("添加好友")
                    .setView(view)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).create();
            mDialog.show();
        }

        private class UserSearchResultReceiver extends BroadcastReceiver{
            @Override
            public void onReceive(Context context, Intent intent) {
                //数据写入list view
                ArrayList<String> users = intent.getStringArrayListExtra("result");
                if(users != null) {
                    adapter = new UserSearchAdapter(users);
                    lvResult.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    tvSearch.setText("搜索");
                }else{
                    //lvResult.removeAllViews();
                }
            }
        }

        //list view adapter
        private class UserSearchAdapter extends BaseAdapter{
            private List<String> users;

            public UserSearchAdapter(List<String> users){
                this.users = users;
            }

            @Override
            public int getCount() {
                return users.size();
            }

            @Override
            public Object getItem(int i) {
                return users.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                ViewHolder holder;
                if(view == null){
                    view = LayoutInflater.from(mContext).inflate(R.layout.contact_dialog_list_item,null);
                    holder = new ViewHolder();
                    holder.ivUserIcon = (ImageView)view.findViewById(R.id.ivContactDialogUserIcon);
                    holder.tvUserName = (TextView)view.findViewById(R.id.tvContactDialogUserName);
                    holder.tvServerName = (TextView)view.findViewById(R.id.tvContactDialogServerName);
                    holder.tvAdd = (TextView)view.findViewById(R.id.tvContactDialogUserAdd);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder)view.getTag();
                }

                final String jid = users.get(i);
                holder.tvUserName.setText(jid.substring(0, jid.lastIndexOf("@")));
                holder.tvServerName.setText(jid.substring(jid.lastIndexOf("@") + 1));

                //查找是否已经是好友
                boolean isFriend = false;
                for(FriendGroupInfo group:groups){
                    for(Information info:group.getFriends()){
                        if(info.getName().equals(jid)){
                            isFriend = true;
                            break;
                        }
                    }
                }

                if(isFriend){
                    holder.tvAdd.setBackgroundColor(Color.WHITE);
                    holder.tvAdd.setText("已添加");
                    holder.tvAdd.setClickable(false);
                }else{
                    holder.tvAdd.setBackgroundResource(R.drawable.contact_dialog_list_button_bg);
                    holder.tvAdd.setText("添加");
                    holder.tvAdd.setClickable(true);
                    holder.tvAdd.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO 添加好友
                            Logger.d(TAG,"添加好友:" + jid);
                        }
                    });
                }

                return view;
            }
        }

        private class ViewHolder{
            public ImageView ivUserIcon;
            public TextView tvUserName,tvServerName,tvAdd;
        }
    }

    private class FriendGroupDialog{
        private Context mContext;
        private AlertDialog mDialog;
        private EditText etInput;

        public FriendGroupDialog(Context context){
            mContext = context;
            etInput = new EditText(mContext);

            init();
        }

        private void init(){
            mDialog = new AlertDialog.Builder(mContext)
                    .setTitle("输入分组名称").setView(etInput).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String groupName = etInput.getText().toString();
                            if (groupName.trim().equals("")) {
                                Toast.makeText(mContext, "分组名称不能为空", Toast.LENGTH_LONG).show();
                                keepDialogOpen();

                                return;
                            }

                            //比较是否存在,若存在,重新输入
                            boolean isExists = false;
                            for (FriendGroupInfo info : groups) {
                                if (info.getName().equals(groupName)) {
                                    isExists = true;

                                    break;
                                }
                            }

                            if (isExists) {
                                Toast.makeText(mContext, "分组存在", Toast.LENGTH_LONG).show();
                                keepDialogOpen();

                                return;
                            }

                            //添加分组
                            Intent friendGroupIntent = new Intent();
                            friendGroupIntent.setAction(ChatService.USER_CREATE_FRIEND_GROUP);
                            friendGroupIntent.putExtra("friendGroupName", groupName);
                            mContext.sendBroadcast(friendGroupIntent);

                            closeDialog();
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
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                lvContactList.stopRefresh();
                Toast.makeText(mContext, "刷新完成", Toast.LENGTH_LONG).show();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onGroupExpand(int groupPosition) {
        RotateAnimation animation = new RotateAnimation(0.0f, 90.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        RotateAnimation animation = new RotateAnimation(90.0f, 0.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View groupView,
                                int position, long id) {

        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View childView,
                                int groupPosition, int childPosition, long id) {

        // TODO 传递数据

        if (groupPosition == 1) {
            // 我的电脑

            return true;
        }

        Intent intent = new Intent(mContext, FriendDtailActivity.class);
        String account = groups.get(groupPosition - 1).getFriends()
                .get(childPosition).getAccount();
        // System.out.println(TAG + ":account:" + account);
        Logger.d(TAG, account);
        intent.putExtra("account", account);
        startActivity(intent);
        mContext.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

        return true;
    }

    @Override
    public void onChanged() {
        // 数据库用户信息改变

    }

    @Override
    public void onError(int type) {
        // 数据库用户信息更改出错

    }
}
