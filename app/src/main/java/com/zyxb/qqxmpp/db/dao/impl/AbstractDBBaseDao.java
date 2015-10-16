package com.zyxb.qqxmpp.db.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zyxb.qqxmpp.db.DBHelper;
import com.zyxb.qqxmpp.db.dao.DBBaseDAO;

public abstract class AbstractDBBaseDao<T> implements DBBaseDAO<T> {
	protected ThreadLocal<SQLiteDatabase> local = new ThreadLocal<SQLiteDatabase>();
	protected DBHelper helper;
	protected SQLiteDatabase db;
	protected OnDataChangListener listener;
	protected boolean isListening = false;
	private Object obj = new Object();

	public AbstractDBBaseDao(Context context) {
		helper = new DBHelper(context);
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
