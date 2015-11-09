package com.zyxb.qqxmpp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyxb.qqxmpp.R;
import com.zyxb.qqxmpp.bean.MessageInfo;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.ui.ChatActivity;
import com.zyxb.qqxmpp.ui.FriendDtailActivity;
import com.zyxb.qqxmpp.ui.GroupFriendCardActivity;
import com.zyxb.qqxmpp.ui.LoginUserDetail;
import com.zyxb.qqxmpp.util.DateUtils;
import com.zyxb.qqxmpp.util.ImgUtil;
import com.zyxb.qqxmpp.util.MyExpressionUtil;
import com.zyxb.qqxmpp.util.PopupUtils;
import com.zyxb.qqxmpp.util.UIAnimUtils;
import com.zyxb.qqxmpp.view.CircleImageView2;
import com.zyxb.qqxmpp.view.QuickAction;
import com.zyxb.qqxmpp.view.QuickAction.OnActionItemClickListener;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;

public class ChatAdapter extends BaseAdapter implements OnClickListener,
        OnLongClickListener {
    private List<MessageInfo> messages;
    private DBUser mUser;
    private LayoutInflater mInflater;
    private ChatActivity mContext;
    private int type;
    private String account;
    private String groupAccount;
    private int mMinItemWidth; //最小的item宽度
    private int mMaxItemWidth; //最大的item宽度

    public ChatAdapter(ChatActivity context, DBUser mUser,
                       List<MessageInfo> messages) {
        this.mContext = context;
        this.messages = messages;
        this.mUser = mUser;
        this.mInflater = LayoutInflater.from(context);

        //获取屏幕的宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    public void setMessages(List<MessageInfo> messages) {
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.chat_list_item, null);

            holder = new ViewHolder();
            holder.tvTime = findView(convertView, R.id.tvChatTime);
            // left
            holder.llLeft = findView(convertView, R.id.llChatLeft);
            holder.ivLeftIcon = findView(convertView, R.id.ciChatLeftIcon);
            holder.tvLeftName = findView(convertView, R.id.tvChatLeftName);
            holder.tvLeftContent = findView(convertView, R.id.tvChatLeftContent);
            holder.ivLeftImage = findView(convertView, R.id.ivChatLeftImage);
            holder.ivLeftLoc = findView(convertView, R.id.ivChatLeftLoc);
            // holder.ivLeftLoading = findView(convertView,
            // R.id.ivChatLeftLoading);
            holder.tvLeftGroupTitle = findView(convertView,
                    R.id.tvChatLeftGroupTitle);
            holder.pbLeftLoading = findView(convertView, R.id.pbChatLeftLoading);
            //语音
            holder.tvLeftVoiceTime = findView(convertView, R.id.tvChatLeftVoiceTime);
            holder.vLeftVoice = findView(convertView, R.id.vChatLeftVoice);
            holder.flLeftVoice = findView(convertView, R.id.flChatLeftVoice);
            //文件
            holder.rlLeftFile = findView(convertView, R.id.rlChatLeftFile);
            holder.ivLeftFileIcon = findView(convertView, R.id.ivChatLeftFileIcon);
            holder.tvLeftFileName = findView(convertView, R.id.tvChatLeftFileName);
            holder.tvLeftFileSize = findView(convertView, R.id.tvChatLeftFileSize);
            holder.tvLeftFileAccept = findView(convertView, R.id.tvChatLeftFileAccept);
            holder.tvLeftFileRefuse = findView(convertView, R.id.tvChatLeftFileRefuse);
            holder.tvLeftFileState = findView(convertView, R.id.tvChatLeftFileState);
            //图片
            holder.flLeftImage = findView(convertView,R.id.flChatLeftImage);

            // right
            holder.llRight = findView(convertView, R.id.llChatRight);
            holder.ivRightIcon = findView(convertView, R.id.ciChatRightIcon);
            holder.tvRightName = findView(convertView, R.id.tvChatRightName);
            holder.tvRightContent = findView(convertView,
                    R.id.tvChatRightContent);
            holder.ivRightImage = findView(convertView, R.id.ivChatRightImage);
            holder.ivRightLoc = findView(convertView, R.id.ivChatRightLoc);
            // holder.ivRightLoading = findView(convertView,
            // R.id.ivChatRightLoading);
            holder.ivRightMessageState = findView(convertView, R.id.ivChatRightMessageState);
            holder.tvRightGroupTitle = findView(convertView,
                    R.id.tvChatRightGroupTitle);
            holder.pbRightLoading = findView(convertView,
                    R.id.pbChatRightLoading);
            //语音
            holder.tvRightVoiceTime = findView(convertView, R.id.tvChatRightVoiceTime);
            holder.vRightVoice = findView(convertView, R.id.vChatRightVoice);
            holder.flRightVoice = findView(convertView, R.id.flChatRightVoice);
            //文件
            holder.rlRightFile = findView(convertView, R.id.rlChatRightFile);
            holder.ivRightFileIcon = findView(convertView, R.id.ivChatRightFileIcon);
            holder.tvRightFileName = findView(convertView, R.id.tvChatRightFileName);
            holder.tvRightFileSize = findView(convertView, R.id.tvChatRightFileSize);
            holder.tvRightFileCancel = findView(convertView, R.id.tvChatRightFileCancel);
            holder.tvRightFileState = findView(convertView, R.id.tvChatRightFileState);
            //图片
            holder.flRightImage = findView(convertView,R.id.flChatRightImage);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        MessageInfo message = messages.get(position);
        // System.out.println("type:" + message.getType());
        type = message.getType();
        account = message.getFrom().getAccount();
        if (mUser.getAccount().equals(account)) {
            account = message.getTo().getAccount();
        }
        if (type == DBColumns.MESSAGE_TYPE_GROUP) {
            groupAccount = message.getTo().getAccount();
        }

        holder.tvTime.setText(DateUtils.getMsgDate(message.getCreateTime()));

        // 自己发送的消息
        if (mUser.getAccount().equals(message.getFrom().getAccount())) {
            // 右边
            holder.llLeft.setVisibility(View.GONE);
            holder.llRight.setVisibility(View.VISIBLE);

            if (message.getFrom().getIcon() == null
                    || message.getFrom().getIcon().trim().equals("")) {
                // 默认
                holder.ivRightIcon
                        .setBackgroundResource(R.drawable.ic_launcher);
            } else {
                // 加载图片
                // TODO
            }

            holder.tvRightName.setVisibility(View.GONE);
            //holder.tvRightContent.setText(MyExpressionUtil.prase(mContext,
            //		holder.tvRightContent, message.getMsg()));
            //holder.ivRightImage.setVisibility(View.GONE);
            holder.ivRightLoc.setVisibility(View.GONE);
            holder.tvRightGroupTitle.setVisibility(View.GONE);
            // holder.pbLeftLoading.setVisibility(View.GONE);
            if (message.getState() == DBColumns.MESSAGE_STATE_SENDING) {
                holder.ivRightMessageState.setVisibility(View.GONE);
                holder.pbRightLoading.setVisibility(View.VISIBLE);
            } else if (message.getState() == DBColumns.MESSAGE_STATE_FAIL) {
                holder.pbRightLoading.setVisibility(View.GONE);
                holder.ivRightMessageState.setVisibility(View.VISIBLE);
            } else {
                holder.pbRightLoading.setVisibility(View.GONE);
                holder.ivRightMessageState.setVisibility(View.GONE);
            }

            //发送图片
            String msgType = message.getMsgType();
            //if(msgType != null && message.getMsgType().equals(DBColumns.MESSAGE_MSG_TYPE_IMAGE)){
            if (msgType != null) {
                holder.tvRightContent.setVisibility(View.GONE);
                if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_IMAGE)) {
                    //语音不显示
                    holder.flRightVoice.setVisibility(View.GONE);
                    holder.tvRightVoiceTime.setVisibility(View.GONE);
                    //文字不显示
                    //holder.tvRightContent.setVisibility(View.GONE);
                    holder.rlRightFile.setVisibility(View.GONE);
                    //显示图片
                    //holder.ivRightImage.setVisibility(View.VISIBLE);
                    holder.flRightImage.setVisibility(View.VISIBLE);
                    holder.ivRightImage.setImageBitmap(ImgUtil.getInstance().loadBitmapFromLocal(message.getMsg(), 200, 200));
                } else if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_VOICE)) {
                    //文字，图片不显示
                    //holder.tvRightContent.setVisibility(View.GONE);
                    //holder.ivRightImage.setVisibility(View.GONE);
                    holder.flRightImage.setVisibility(View.GONE);
                    holder.rlRightFile.setVisibility(View.GONE);
                    holder.flRightVoice.setVisibility(View.VISIBLE);
                    holder.tvRightVoiceTime.setVisibility(View.VISIBLE);

                    //设置数据
                    String t = message.getMediaTime();
                    float time = 0;
                    if (t != null) {
                        time = Float.parseFloat(t);
                    }
                    //holder.tvRightVoiceTime.setText(message.getMediaTime());
                    holder.tvRightVoiceTime.setText(Math.round(time) + "\"");

                    //按时间设置长度
                    ViewGroup.LayoutParams lp = holder.flRightVoice.getLayoutParams();
                    lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f) * time);
                } else if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_LOCATION)) {
                    //TODO 发送位置信息
                } else if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_FILE)) {
                    //文件
                    holder.flRightVoice.setVisibility(View.GONE);
                    holder.tvRightVoiceTime.setVisibility(View.GONE);
                    //holder.ivRightImage.setVisibility(View.GONE);
                    holder.flRightImage.setVisibility(View.GONE);
                    holder.rlRightFile.setVisibility(View.VISIBLE);

                    //根据后缀加载文件图标
                    String filename = message.getMsg();
                    holder.ivRightFileIcon.setImageResource(getIconRes(filename));
                    holder.tvRightFileName.setText(filename.substring(filename.lastIndexOf(File.separator) + 1, filename.lastIndexOf(".")));
                    holder.tvRightFileSize.setText(getSize(filename));

                    //显示文件已经接收还是对方取消
                    if(message.getState() == DBColumns.MESSAGE_STATE_CANCELED){
                        //holder.tvLeftFileAccept.setVisibility(View.GONE);
                        //holder.tvLeftFileRefuse.setVisibility(View.VISIBLE);
                        //holder.tvLeftFileRefuse.setText("对方已拒绝");
                        holder.tvRightFileCancel.setVisibility(View.VISIBLE);
                        holder.tvRightFileCancel.setText("对方拒绝接收");
//                    }else if(message.getState() == DBColumns.MESSAGE_STATE_RECEIVED){
                    }else if(message.getState() == DBColumns.MESSAGE_STATE_SENDED){
                        //holder.tvLeftFileAccept.setVisibility(View.VISIBLE);
                        //holder.tvLeftFileRefuse.setVisibility(View.VISIBLE);
                        //holder.tvLeftFileAccept.setText("打开");
                        //holder.tvLeftFileRefuse.setText("打开文件夹");
                        holder.tvRightFileCancel.setVisibility(View.VISIBLE);
                        holder.tvRightFileCancel.setText("对方已接收");
                    }else if(message.getState() == DBColumns.MESSAGE_STATE_SENDING){
                        //holder.tvLeftFileAccept.setVisibility(View.VISIBLE);
                        //holder.tvLeftFileRefuse.setVisibility(View.GONE);
                        //holder.tvLeftFileAccept.setText("取消");
                        holder.tvRightFileCancel.setVisibility(View.VISIBLE);
                        holder.tvRightFileCancel.setText("取消");
                    }else{
                        //holder.tvLeftFileAccept.setVisibility(View.GONE);
                        //holder.tvLeftFileRefuse.setVisibility(View.GONE);
                        holder.tvRightFileCancel.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.tvRightContent.setVisibility(View.VISIBLE);
                //holder.ivRightImage.setVisibility(View.GONE);
                holder.flRightImage.setVisibility(View.GONE);
                holder.rlRightFile.setVisibility(View.GONE);
                holder.tvRightVoiceTime.setVisibility(View.GONE);
                holder.flRightVoice.setVisibility(View.GONE);
                holder.tvRightContent.setText(MyExpressionUtil.prase(mContext,
                        holder.tvRightContent, message.getMsg()));
            }

            //Logger.d("ChatActivity","msgType:" + msgType);

            //发送语音
//			if(msgType != null && message.getMsgType().equals(DBColumns.MESSAGE_MSG_TYPE_VOICE)){
//				//文字，图片不显示
//				holder.tvRightContent.setVisibility(View.GONE);
//				holder.ivRightImage.setVisibility(View.GONE);
//				holder.flRightVoice.setVisibility(View.VISIBLE);
//				holder.tvRightVoiceTime.setVisibility(View.VISIBLE);
//
//				//设置view的长度
//			}

        } else {
            // 左边
            holder.llRight.setVisibility(View.GONE);
            holder.llLeft.setVisibility(View.VISIBLE);

            String icon = null;
            switch (message.getType()) {
                case DBColumns.MESSAGE_TYPE_CONTACT:
                    icon = message.getFrom().getIcon();
                    break;
                case DBColumns.MESSAGE_TYPE_GROUP:
                    icon = message.getTo().getIcon();
                    break;
                case DBColumns.MESSAGE_TYPE_SYS:
                    icon = message.getFrom().getIcon();
                    break;
            }

            if (icon == null || icon.trim().equals("")) {
                // 默认
                holder.ivLeftIcon.setBackgroundResource(R.drawable.ic_launcher);
            } else {
                // 加载图片
                // TODO
            }

            // System.out.println("type:" + message.getType() + ",name:"
            // + message.getName());
            if (message.getType() == DBColumns.MESSAGE_TYPE_CONTACT
                    || message.getType() == DBColumns.MESSAGE_TYPE_SYS) {
                holder.tvLeftName.setVisibility(View.GONE);
                holder.tvLeftGroupTitle.setVisibility(View.GONE);
            } else {
                // 群
                holder.tvLeftName.setVisibility(View.VISIBLE);
                holder.tvLeftName
                        .setText(message.getFrom().getComments() + ":");

                holder.tvLeftGroupTitle.setVisibility(View.VISIBLE);
                holder.tvLeftGroupTitle.setText(DBColumns.GROUP_TITLES[message
                        .getFrom().getGroupTitle()]);
                // System.out.println("group title:" + message.getGroupTitle());
            }

            // holder.tvLeftContent.setText(message.getMsg());
            //holder.tvLeftContent.setText(MyExpressionUtil.prase(mContext,
            //		holder.tvLeftContent, message.getMsg()));
            holder.ivLeftImage.setVisibility(View.GONE);
            holder.ivLeftLoc.setVisibility(View.GONE);
            holder.pbLeftLoading.setVisibility(View.GONE);

            //发送图片
            String msgType = message.getMsgType();
            //if (msgType != null && message.getMsgType().equals(DBColumns.MESSAGE_MSG_TYPE_IMAGE)) {
            if (msgType != null) {
                holder.tvLeftContent.setVisibility(View.GONE);
                if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_IMAGE)) {
                    //图片
                    holder.flLeftVoice.setVisibility(View.GONE);
                    holder.tvLeftVoiceTime.setVisibility(View.GONE);
                    holder.rlLeftFile.setVisibility(View.GONE);
                    //holder.tvLeftContent.setVisibility(View.GONE);
                    //holder.ivLeftImage.setVisibility(View.VISIBLE);
                    holder.flLeftImage.setVisibility(View.VISIBLE);
                    holder.ivLeftImage.setImageBitmap(ImgUtil.getInstance().loadBitmapFromLocal(message.getMsg(), 200, 200));
                } else if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_VOICE)) {
                    //语音
                    holder.flLeftVoice.setVisibility(View.VISIBLE);
                    holder.tvLeftVoiceTime.setVisibility(View.VISIBLE);
                    holder.rlLeftFile.setVisibility(View.GONE);
                    //holder.tvLeftContent.setVisibility(View.GONE);
                    //holder.ivLeftImage.setVisibility(View.GONE);
                    holder.flLeftImage.setVisibility(View.GONE);

                    float time = Float.parseFloat(message.getMediaTime());
                    //holder.tvLeftVoiceTime.setText(Math.round(Double.parseDouble(message.getMediaTime())) + "\"");
                    holder.tvLeftVoiceTime.setText(Math.round(time) + "\"");
                    //设置长度
                    ViewGroup.LayoutParams lp = holder.flLeftVoice.getLayoutParams();
                    lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f) * time);
                } else if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_LOCATION)) {
                    //TODO 位置
                } else if (msgType.equals(DBColumns.MESSAGE_MSG_TYPE_VOICE)) {
                    //文件
                    holder.flLeftVoice.setVisibility(View.GONE);
                    holder.tvLeftVoiceTime.setVisibility(View.GONE);
                    //holder.ivLeftImage.setVisibility(View.GONE);
                    holder.flLeftImage.setVisibility(View.GONE);
                    holder.rlLeftFile.setVisibility(View.VISIBLE);

                    //根据后缀加载文件图标
                    String filename = message.getMsg();
                    holder.ivLeftFileIcon.setImageResource(getIconRes(filename));
                    holder.tvLeftFileName.setText(filename.substring(filename.lastIndexOf(File.separator) + 1, filename.lastIndexOf(".")));
                    holder.tvLeftFileSize.setText(getSize(filename));
                }
            } else {
                holder.tvLeftContent.setVisibility(View.VISIBLE);
                //holder.ivLeftImage.setVisibility(View.GONE);
                holder.flLeftImage.setVisibility(View.GONE);
                holder.flLeftVoice.setVisibility(View.GONE);
                holder.tvLeftVoiceTime.setVisibility(View.GONE);
                holder.rlLeftFile.setVisibility(View.GONE);
                holder.tvLeftContent.setText(MyExpressionUtil.prase(mContext,
                        holder.tvLeftContent, message.getMsg()));
            }
        }

        holder.ivLeftIcon.setOnClickListener(this);
        holder.tvLeftContent.setOnLongClickListener(this);
        holder.tvRightContent.setOnLongClickListener(this);
        holder.ivRightIcon.setOnClickListener(this);

        holder.ivLeftIcon.setTag(account);
        holder.tvLeftContent.setTag(account);
        holder.tvRightContent.setTag(account);
        holder.ivRightIcon.setTag(account);

        return convertView;
    }

    private String getSize(String filePath) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        long fileS = new File(filePath).length();
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }

        return fileSizeString;
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

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findView(View v, int id) {
        return (T) v.findViewById(id);
    }

    private static class ViewHolder {
        public TextView tvTime, tvLeftName, tvRightName, tvLeftContent,
                tvRightContent, tvLeftGroupTitle, tvRightGroupTitle,
                tvLeftVoiceTime, tvRightVoiceTime, tvLeftFileName,
                tvRightFileName, tvLeftFileSize, tvRightFileSize,
                tvLeftFileAccept, tvLeftFileRefuse, tvRightFileCancel,
                tvLeftFileState, tvRightFileState;
        public LinearLayout llLeft;
        public RelativeLayout llRight;
        public CircleImageView2 ivLeftIcon, ivRightIcon;
        public ImageView ivLeftImage, ivRightImage, ivLeftLoc, ivRightLoc, ivRightMessageState,
                ivLeftFileIcon, ivRightFileIcon;
        // ivLeftLoading, ivRightLoading;
        public ProgressBar pbLeftLoading, pbRightLoading;
        public View vLeftVoice, vRightVoice;
        public FrameLayout flLeftVoice, flRightVoice,flLeftImage,flRightImage;
        public RelativeLayout rlLeftFile, rlRightFile;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        String account = (String) v.getTag();

        switch (v.getId()) {
            case R.id.tvChatLeftContent:
            case R.id.tvChatRightContent:
                // PopupUtils.showPop(mContext, v, new String[] { "复制", "转发", "收藏",
                // "转发多条", "删除" });

                break;
            case R.id.ciChatRightIcon:
                if (type == DBColumns.MESSAGE_TYPE_CONTACT) {
                    // TODO 显示自己
                    intent = new Intent(mContext, LoginUserDetail.class);
                    mContext.startActivity(intent);
                    UIAnimUtils.sildLeftIn(mContext);
                    return;
                }
            case R.id.ciChatLeftIcon:
                if (type == DBColumns.MESSAGE_TYPE_GROUP) {
                    // intent = new Intent(mContext, GroupDetailActivity.class);
                    intent = new Intent(mContext, GroupFriendCardActivity.class);
                    intent.putExtra("groupAccount", groupAccount);
                } else if (type == DBColumns.MESSAGE_TYPE_CONTACT) {
                    intent = new Intent(mContext, FriendDtailActivity.class);
                } else {
                    return;
                }

                intent.putExtra("account", account);
                intent.putExtra("isFromChat", true);
                mContext.startActivity(intent);
                // mContext.overridePendingTransition(R.anim.slide_left,
                // R.anim.slide_right);
                UIAnimUtils.sildLeftIn(mContext);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tvChatLeftContent:
            case R.id.tvChatRightContent:
                // int[] arrayOfInt = new int[2];
                // v.getLocationInWindow(arrayOfInt);
                // int i = arrayOfInt[0];
                // int j = arrayOfInt[1];
                // // System.out.println("i=" + i + ",j=" + j);
                // int[] location = new int[2];
                // v.getLocationOnScreen(location);
                // System.out.println("x=" + location[0] + ",y=" + location[1]);
                // PopupUtils.showPop(mContext, v, new String[] { "复制", "转发", "收藏",
                // "转发多条", "删除" }, i, j);
                final String[] menus = new String[]{"复制", "转发", "收藏",
                        "转发多条", "删除"};
                PopupUtils.showQuickAction(mContext, v, menus, new OnActionItemClickListener() {

                    @Override
                    public void onItemClick(QuickAction source, int pos, int actionId) {
                        Toast.makeText(mContext, menus[pos], Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

        return true;
    }
}
