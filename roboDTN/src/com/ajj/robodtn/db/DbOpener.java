package com.ajj.robodtn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpener extends SQLiteOpenHelper {

	private static final String BUNDLE_DATABASE_NAME = "roboDTNbundles";
	private static final int BUNDLE_DATABASE_VERSION = 1;

	private static final String TAG = "roboDTN DbOpener";
	
	public DbOpener(Context context) {
		super(context, BUNDLE_DATABASE_NAME, null, BUNDLE_DATABASE_VERSION);
	}
	
	public DbOpener(Context context, boolean wipe) {
		super(context, BUNDLE_DATABASE_NAME, null, BUNDLE_DATABASE_VERSION);
		
		if(wipe == true) {
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL(BundleDbWrapper.BUNDLE_TABLE_DROP);
			onCreate(db);
		}
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BundleDbWrapper.BUNDLE_TABLE_CREATE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "roboDTN doesn't update db gracefully (" + oldVersion + 
				" to " + newVersion + "), recreating db.");
		
		db.execSQL("DROP TABLE IF EXISTS " + BundleDbWrapper.BUNDLE_TABLE_NAME + ";");
		onCreate(db);
	}

}
