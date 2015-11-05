package com.zyxb.qqxmpp.bean;

public class MessageInfo {
    private String account;
    private int type;//sys group contact
    private Information from;
    private Information to;
    private String msg;
    private long createTime;
    private int count;
    private int state;
    private String msgType;//chat,image,video,file

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Information getFrom() {
        return from;
    }

    public void setFrom(Information from) {
        this.from = from;
    }

    public Information getTo() {
        return to;
    }

    public void setTo(Information to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
