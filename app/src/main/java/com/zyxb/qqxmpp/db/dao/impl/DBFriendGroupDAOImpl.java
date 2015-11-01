package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.Cursor;

import com.zyxb.qqxmpp.bean.XMPPUser;
import com.zyxb.qqxmpp.bean.po.DBFriendGroup;
import com.zyxb.qqxmpp.bean.po.DBObjectHelper;
import com.zyxb.qqxmpp.bean.po.DBUser;
import com.zyxb.qqxmpp.db.DAOFactory;
import com.zyxb.qqxmpp.db.DBColumns;
import com.zyxb.qqxmpp.db.dao.DBFriendGroupDAO;
import com.zyxb.qqxmpp.db.dao.DBUserDAO;
import com.zyxb.qqxmpp.util.MD5Encoder;

import java.util.List;

public class DBFriendGroupDAOImpl extends DBFriendGroupDAO {
    private DBUserDAO dao;

    public DBFriendGroupDAOImpl(Context context) {
        super(context);

        dao = DAOFactory.getDB3UserDAO(context);
    }

    @Override
    public int add(DBFriendGroup friendGroup) {
        db = getTransactionDB();

        // 查找user
        DBUser user = dao.findByAccount(friendGroup.getUser().getAccount());
        if (user == null) {
            dao.add(friendGroup.getUser());
        }

        // 查找是否有相同名称的分组
        // DBFriendGroup fg = findByName(friendGroup.getmUser().getAccount(),
        // friendGroup.getAccount());
        // if (fg != null) {
        // return DBColumns.ERROR_FRIEND_GROUP_EXISTS;
        // }
        if (isExists(friendGroup.getUser().getAccount(), friendGroup.getName())) {
            return DBColumns.ERROR_FRIEND_GROUP_EXISTS;
        }

        // 添加friendgroup
        db.execSQL(
                "INSERT INTO " + DBColumns.FRIEND_GROUP_TABLE_NAME + "("
                        + DBColumns.FRIEND_GROUP_ACCOUNT + ","
                        + DBColumns.FRIEND_GROUP_USER_ACCOUNT + ","
                        + DBColumns.FRIEND_GROUP_NAME + ","
                        + DBColumns.FRIEND_GROUP_POSITION
                        + ") VALUES(?,?,?,?)",
                new String[]{friendGroup.getAccount(),
                        friendGroup.getUser().getAccount(),
                        friendGroup.getName(), friendGroup.getPosition() + ""});

        return DBColumns.RESULT_OK;
    }

    @Override
    public DBFriendGroup findByAccount(String account) {
        db = getTransactionDB();

        Cursor cursor = db.rawQuery("SELECT * FROM "
                        + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                        + DBColumns.FRIEND_GROUP_ACCOUNT + "=?",
                new String[]{account});
        DBFriendGroup fg = null;
        if (cursor.moveToFirst()) {
            String userAccount = cursor.getString(cursor
                    .getColumnIndex(DBColumns.FRIEND_GROUP_USER_ACCOUNT));
            fg = DBObjectHelper.getDB3FriendGroup(
                    dao.findByAccount(userAccount), cursor);
        }
        cursor.close();

        return fg;
    }

    private boolean isExists(String userAccount, String groupName) {
        db = getTransactionDB();

        Cursor cursor = db.rawQuery("SELECT * FROM "
                + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                + DBColumns.FRIEND_GROUP_NAME + "=? AND "
                + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[]{
                groupName, userAccount});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();

        return false;
    }

    protected DBFriendGroup findByName(String userAccount, String name) {
        db = getTransactionDB();

        Cursor cursor = db.rawQuery("SELECT * FROM "
                + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                + DBColumns.FRIEND_GROUP_NAME + "=? AND "
                + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[]{
                name, userAccount});
        DBFriendGroup fg = null;
        if (cursor.moveToFirst()) {
            fg = DBObjectHelper.getDB3FriendGroup(
                    dao.findByAccount(userAccount), cursor);
        }
        cursor.close();

        return fg;
    }

    @Override
    public int update(DBFriendGroup friendGroup) {
        db = getTransactionDB();

        // 查找
        DBFriendGroup fg = findByAccount(friendGroup.getAccount());
        if (fg == null) {
            return DBColumns.ERROR_FRIEND_GROUP_NOT_FOUND;
        }

        // 查找user
        DBUser user = dao.findByAccount(friendGroup.getUser().getAccount());
        if (user == null) {
            dao.add(friendGroup.getUser());
        }

        // 更新
        db.execSQL("UPDATE " + DBColumns.FRIEND_GROUP_TABLE_NAME + " SET "
                + DBColumns.FRIEND_GROUP_NAME + "=? "
                + DBColumns.FRIEND_GROUP_POSITION + "=? "
                + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=? ", new String[]{
                friendGroup.getName(), friendGroup.getPosition() + "",
                friendGroup.getUser().getAccount()});

        return DBColumns.RESULT_OK;
    }

    @Override
    public int delete(DBFriendGroup friendGroup) {
        db = getTransactionDB();

        // 查找
        DBFriendGroup fg = findByAccount(friendGroup.getAccount());
        if (fg == null) {
            return DBColumns.ERROR_FRIEND_GROUP_NOT_FOUND;
        }

        db.execSQL("DELETE FROM " + DBColumns.FRIEND_GROUP_TABLE_NAME
                        + " WHERE " + DBColumns.FRIEND_GROUP_ACCOUNT + "=?",
                new String[]{friendGroup.getAccount()});

        return DBColumns.RESULT_OK;
    }

    @Override
    public int delete(DBUser user) {
        db = getTransactionDB();

        db.execSQL("DELETE FROM " + DBColumns.FRIEND_GROUP_TABLE_NAME
                        + " WHERE " + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?",
                new String[]{user.getAccount()});

        return DBColumns.RESULT_OK;
    }

    @Override
    public int clear() {
        db = getTransactionDB();

        db.execSQL("DELETE FROM " + DBColumns.FRIEND_GROUP_TABLE_NAME
                + " WHERE 1=1");

        return DBColumns.RESULT_OK;
    }

    @Override
    public List<DBFriendGroup> findByUser(DBUser user) {
        db = getTransactionDB();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                        + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                        + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=? ORDER BY "
                        + DBColumns.FRIEND_GROUP_POSITION,
                new String[]{user.getAccount()});

        return DBObjectHelper.getDB3FriendGroups(user, cursor);
    }

    @Override
    public void close() {
        super.close();
        dao.close();
    }

    @Override
    public DBFriendGroup find(String account, String group) {
        db = getTransactionDB();

        DBUser user = dao.findByAccount(account);
        if (user == null) {
            return null;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM "
                + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                + DBColumns.FRIEND_GROUP_NAME + "=? AND "
                + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?", new String[]{
                group, account});
        DBFriendGroup fg = null;
        if (cursor.moveToFirst()) {
            fg = DBObjectHelper.getDB3FriendGroup(user, cursor);
        }
        cursor.close();

        if (fg == null) {
            // 添加
            // 查找account
            // select account from friend_group where _id = (select max(_id)
            // from friend_group);
            cursor = db.rawQuery("SELECT " + DBColumns.FRIEND_GROUP_ACCOUNT
                    + " FROM " + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                    + DBColumns.FRIEND_GROUP_ID + "=(SELECT max("
                    + DBColumns.FRIEND_GROUP_ID + ") FROM "
                    + DBColumns.FRIEND_GROUP_TABLE_NAME + ")", null);
            int acc = 1;
            if (cursor.moveToFirst()) {
                acc = Integer.parseInt(cursor.getString(0)) + 1;
            }
            cursor.close();
            fg = new DBFriendGroup();
            fg.setAccount(acc + "");
            fg.setName(group);
            fg.setUser(user);

            // 查找position
            // select max(position) from friend_group where
            // user_account='100000'
            cursor = db.rawQuery("SELECT max("
                            + DBColumns.FRIEND_GROUP_POSITION + ") FROM "
                            + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                            + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?",
                    new String[]{account});
            int position = 0;
            if (cursor.moveToFirst()) {
                position = cursor.getInt(0) + 1;
            }
            cursor.close();
            fg.setPosition(position);

            add(fg);
        }

        return fg;
    }

    @Override
    public DBUser find(XMPPUser ur) {
        db = getTransactionDB();

        // 查找用户是否存在
        DBUser user = dao.findByName(ur.getJid());
        if (user == null) {
            // 添加user
            user = dao.find(ur);

            // 添加[我的设备,我的好友]
            DBFriendGroup fg = new DBFriendGroup();
            fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
            fg.setPosition(0);
            fg.setName("我的设备");
            fg.setUser(user);
            add(fg);

            fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
            fg.setPosition(1);
            fg.setName("我的好友");
            add(fg);
            //fg = null;
        } else {
            //如果已经存在,看看pwd是否为空,为空则是其他用户的添加好友添加,需添加[我的设备,我的好友]
            if (user.getPwd() == null) {
                user.setPwd(MD5Encoder.encode(ur.getStatusMessage()));
                dao.update(user);

                // 添加[我的设备,我的好友]
                DBFriendGroup fg = new DBFriendGroup();
                fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
                fg.setPosition(0);
                fg.setName("我的设备");
                fg.setUser(user);
                add(fg);

                fg.setAccount(Integer.parseInt(getMaxAccount()) + 1 + "");
                fg.setPosition(1);
                fg.setName("我的好友");
                add(fg);
                //fg = null;
            }
        }

        return user;
    }

    @Override
    public String getMaxAccount() {
        db = getTransactionDB();

        Cursor cursor = db.rawQuery("SELECT " + DBColumns.FRIEND_GROUP_ACCOUNT
                + " FROM " + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                + DBColumns.FRIEND_GROUP_ID + "=(SELECT max("
                + DBColumns.FRIEND_GROUP_ID + ") FROM "
                + DBColumns.FRIEND_GROUP_TABLE_NAME + ")", null);
        String acc = "1";
        if (cursor.moveToFirst()) {
            acc = cursor.getString(0);
        }
        cursor.close();

        return acc;
    }

    public String getMaxPosition(String userAccount) {
        String pos = "0";
        db = getTransactionDB();
        Cursor cursor = db.rawQuery("SELECT MAX(" + DBColumns.FRIEND_GROUP_POSITION + ") FROM "
                + DBColumns.FRIEND_GROUP_TABLE_NAME + " WHERE "
                + DBColumns.FRIEND_GROUP_USER_ACCOUNT + "=?",new String[]{userAccount});
        if(cursor.moveToFirst()){
            pos = cursor.getString(0);
        }
        cursor.close();

        return pos;
    }

    @Override
    public void add(String userAccount, String friendGroupName) {
        db = getTransactionDB();

        String account = getMaxAccount();
        int acc = Integer.parseInt(account) + 1;
        String position = getMaxPosition(userAccount);
        int pos = Integer.parseInt(position) + 1;

        //db.execSQL("INSERT INTO " + DBColumns.FRIEND_GROUP_TABLE_NAME,new Object[]{});
        // 添加friendgroup
        db.execSQL(
                "INSERT INTO " + DBColumns.FRIEND_GROUP_TABLE_NAME + "("
                        + DBColumns.FRIEND_GROUP_ACCOUNT + ","
                        + DBColumns.FRIEND_GROUP_USER_ACCOUNT + ","
                        + DBColumns.FRIEND_GROUP_NAME + ","
                        + DBColumns.FRIEND_GROUP_POSITION
                        + ") VALUES(?,?,?,?)",
                new String[]{acc + "",
                        userAccount,
                        friendGroupName, pos + ""});
    }
}
