package com.zyxb.qqxmpp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class NewMessageProvider extends ContentProvider {
	public static final String AUTHORITY = "com.zyxb.qqxmpp.provider.Chat";
	public static final String TABLE_NAME = "session";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);

	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final int SESSIONS = 1;
	private static final int SESSION_ID = 2;

	static {
		MATCHER.addURI(AUTHORITY, "session", SESSIONS);
		MATCHER.addURI(AUTHORITY, "session/#", SESSION_ID);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		
		
		getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		return null;
	}

	@Override
	public boolean onCreate() {
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		
		return 0;
	}

}
