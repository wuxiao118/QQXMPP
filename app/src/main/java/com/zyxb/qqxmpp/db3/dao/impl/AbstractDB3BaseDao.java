package com.zyxb.qqxmpp.db3.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zyxb.qqxmpp.db3.DB3Helper;
import com.zyxb.qqxmpp.db3.dao.DB3BaseDAO;

public abstract class AbstractDB3BaseDao<T> implements DB3BaseDAO<T> {
	protected ThreadLocal<SQLiteDatabase> local = new ThreadLocal<SQLiteDatabase>();
	protected DB3Helper helper;
	protected SQLiteDatabase db;
	protected OnDataChangListener listener;
	protected boolean isListening = false;
	private Object obj = new Object();

	public AbstractDB3BaseDao(Context context) {
		helper = new DB3Helper(context);
	}

	protected SQLiteDatabase getTransactionDB() {
		SQLiteDatabase db = local.get();
		if (db == null) {
			synchronized (obj) {
				if (db == null) {
					db = helper.getWritableDatabase();
					local.set(db);
				}
			}
		}

		return db;
	}

	public void beginTransaction() {
		db = getTransactionDB();
		db.beginTransaction();
	}

	public void endTransaction() {
		db = getTransactionDB();
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void rollback() {
		db = getTransactionDB();
		db.endTransaction();
	}

	public void close() {
		SQLiteDatabase db = local.get();
		if (db != null) {
			synchronized (obj) {
				if (db != null) {
					local.set(null);
					db.close();
					db = null;
				}
			}
		}
	}
	
	public interface OnDataChangListener{
		void onPreDataChange();
		void onDataChanged();
	}
	
	public void setOnDataChangListener(OnDataChangListener listener){
		this.listener = listener;
		this.isListening = true;
	}
	
	public void removeOnDataChangeListener(){
		this.listener = null;
		this.isListening = false;
	}

}
