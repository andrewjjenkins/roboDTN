/*******************************************************************************
 * Copyright 2011 Andrew Jenkins
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
		this(context, false);
	}
	
	public DbOpener(Context context, boolean wipe) {
		super(context, BUNDLE_DATABASE_NAME, null, BUNDLE_DATABASE_VERSION);
		
		if(wipe == true) {
			SQLiteDatabase db = getWritableDatabase();
			dropAll(db);
			onCreate(db);
		}
		
		bundleDbWrapper = new BundleDbWrapper(this);
		bundleBlockDbWrapper = new BundleBlockDbWrapper(this);
	}
	
	public void dropAll(SQLiteDatabase db) {
		db.execSQL(BundleDbWrapper.BUNDLE_TABLE_DROP);
		db.execSQL(BundleBlockDbWrapper.BLOCK_TABLE_DROP);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BundleDbWrapper.BUNDLE_TABLE_CREATE);
		db.execSQL(BundleBlockDbWrapper.BLOCK_TABLE_CREATE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "roboDTN doesn't update db gracefully (" + oldVersion + 
				" to " + newVersion + "), recreating db.");
		
		dropAll(db);
		onCreate(db);
	}
	
	public BundleDbWrapper bundleDbWrapper;
	public BundleBlockDbWrapper bundleBlockDbWrapper;

}
