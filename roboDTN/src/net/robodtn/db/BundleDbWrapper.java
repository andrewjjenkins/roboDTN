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
package net.robodtn.db;

import java.util.Date;

import net.robodtn.Bundle;
import net.robodtn.sdnvlib.dtnUtil;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.Cursor;

public class BundleDbWrapper {
	public static final String BUNDLE_TABLE_NAME = "bundles";
	public static final String ID_COL = "_id";
	public static final String PROCFLAGS_COL = "procFlags";
	public static final String DST_COL = "dst";
	public static final String SRC_COL = "src";
	public static final String RPTTO_COL = "rptto";
	public static final String CUST_COL = "cust";
	public static final String CREATETIMESTAMP_COL = "createTimestamp";
	public static final String CREATESEQ_COL = "createSeq";
	public static final String LIFETIME_COL = "lifetime";
	public static final String DICTIONARYBLOB_COL = "dictionaryBlob";
	public static final String FRAGOFFSET_COL = "fragOffset";
	public static final String ADULENGTH_COL = "aduLength";
	public static final String PAYLOADFILENAME_COL = "payloadFilename";
	
	public static final String BUNDLE_TABLE_CREATE =
		"CREATE TABLE " + BUNDLE_TABLE_NAME + " (" +
		 ID_COL + " INTEGER PRIMARY KEY, " +
		 PROCFLAGS_COL + " INTEGER, " +
		 DST_COL + " TEXT, " +
		 SRC_COL + " TEXT, " +
		 RPTTO_COL + " TEXT, " +
		 CUST_COL + " TEXT, " +
		 CREATETIMESTAMP_COL + " INTEGER, " +
		 CREATESEQ_COL + " INTEGER, " +
		 LIFETIME_COL + " INTEGER, " +
		 DICTIONARYBLOB_COL + " BLOB, " +
		 FRAGOFFSET_COL + " INTEGER, " +
		 ADULENGTH_COL + " INTEGER, " +
		 PAYLOADFILENAME_COL + " TEXT);";
	public static final String BUNDLE_TABLE_DROP =
		"DROP TABLE IF EXISTS " + BUNDLE_TABLE_NAME;
	
	private static final String [] RETRIEVEBUNDLE_PROJECTION = new String [] {
		ID_COL,
		PROCFLAGS_COL,
		DST_COL,
		SRC_COL,
		RPTTO_COL,
		CUST_COL,
		CREATETIMESTAMP_COL,
		CREATESEQ_COL,
		LIFETIME_COL,
		DICTIONARYBLOB_COL,
		FRAGOFFSET_COL,
		ADULENGTH_COL,
		PAYLOADFILENAME_COL
	};
	private static final int ID_COL_INDEX = 0;
	private static final int PROCFLAGS_COL_INDEX = 1;
	private static final int DST_COL_INDEX = 2;
	private static final int SRC_COL_INDEX = 3;
	private static final int RPTTO_COL_INDEX = 4;
	private static final int CUST_COL_INDEX = 5;
	private static final int CREATETIMESTAMP_COL_INDEX = 6;
	private static final int CREATESEQ_COL_INDEX = 7;
	private static final int LIFETIME_COL_INDEX = 8;
	private static final int DICTIONARYBLOB_COL_INDEX = 9;
	private static final int FRAGOFFSET_COL_INDEX = 10;
	private static final int ADULENGTH_COL_INDEX = 11;
	private static final int PAYLOADFILENAME_COL_INDEX = 12;
		
	private DbOpener mDbOpener;
	
	protected BundleDbWrapper(DbOpener dbOpener) {
		mDbOpener = dbOpener;
	}
		
	public long insertBundle(Bundle b) throws BundleAlreadyInDbException {
		if(isBundleInserted(b.src, b.createTimestamp, b.createSeq) == true) {
			throw new BundleAlreadyInDbException("Can't insert (" + b.src + 
					", " + b.createTimestamp + ", " + b.createSeq + "), it's already in DB");
		}
		
		
		ContentValues vals = new ContentValues();
		
		vals.put(PROCFLAGS_COL, b.procFlags);
		vals.put(DST_COL, b.dst);
		vals.put(SRC_COL, b.src);
		vals.put(RPTTO_COL, b.rptto);
		vals.put(CUST_COL, b.cust);
		vals.put(CREATETIMESTAMP_COL, dtnUtil.DateToDtnShortDate(b.createTimestamp));
		vals.put(CREATESEQ_COL, b.createSeq);
		vals.put(LIFETIME_COL, b.lifetime);
		vals.put(FRAGOFFSET_COL, b.fragOffset);
		vals.put(ADULENGTH_COL, b.aduLength);
		

		if (b.dict != null) {
			vals.put(DICTIONARYBLOB_COL, b.dict.getBytes());
		} else {
			vals.put(DICTIONARYBLOB_COL, new byte [0]);
		}
		/* FIXME */
		vals.put(PAYLOADFILENAME_COL, "/dev/null");
		
		SQLiteDatabase db = mDbOpener.getWritableDatabase();
		
		/* Insert the bundle into the database. */
		long rowId = db.insert(BUNDLE_TABLE_NAME, null, vals);
		
		if (rowId > 0) {
			return rowId;
		}
		throw new SQLException("Failed to insert new bundle ("
				+ b.src + "," + b.createTimestamp + "," + b.createSeq + ")");
	}
	
	public Bundle retrieveBundle(String srcEid, Date createTimestamp, long createSeq) 
			throws NotFoundInDbException 
	{
		return retrieveBundle(srcEid, dtnUtil.DateToDtnShortDate(createTimestamp), createSeq);
	}
	
	public Bundle retrieveBundle(String srcEid, long dtnShortDate, long createSeq) 
			throws NotFoundInDbException 
	{
		SQLiteQueryBuilder q = queryForBundle(srcEid, dtnShortDate, createSeq);
		
		SQLiteDatabase db = mDbOpener.getReadableDatabase();
		
		Cursor c  = q.query(db, 
							RETRIEVEBUNDLE_PROJECTION, 
							null, null,		/* FIXME: Get all cols. */
							null, null, 	/* No grouping */
							null,			/* No ordering */ 
							"1");			/* LIMIT 1: There's at most one bundle */
		
		if (c == null || !c.moveToFirst()) {
			throw new NotFoundInDbException("Couldn't find bundle ("
					+ srcEid + ", " + dtnShortDate + ", " + createSeq + ")");
		}
		
		Bundle b = new Bundle();
		
		b.procFlags = c.getLong(PROCFLAGS_COL_INDEX);
		b.dst = c.getString(DST_COL_INDEX);
		b.src = c.getString(SRC_COL_INDEX);
		b.rptto = c.getString(RPTTO_COL_INDEX);
		b.cust = c.getString(CUST_COL_INDEX);
		b.createTimestamp = dtnUtil.DtnShortDateToDate(c.getLong(CREATETIMESTAMP_COL_INDEX));
		b.createSeq = c.getLong(CREATESEQ_COL_INDEX);
		b.lifetime = c.getLong(LIFETIME_COL_INDEX);
		b.fragOffset = c.getLong(FRAGOFFSET_COL_INDEX);
		b.aduLength = c.getLong(ADULENGTH_COL_INDEX);
		
		byte [] dictBytes = c.getBlob(DICTIONARYBLOB_COL_INDEX);
		if(dictBytes.length != 0) {
			/* FIXME */
		}
		
		return b;
	}
	
	private static final String [] checkForBundleProjection = new String [] { ID_COL }; 
	
	public boolean isBundleInserted(String srcEid, Date createTimestamp, long createSeq)
	{
		return isBundleInserted(srcEid, dtnUtil.DateToDtnShortDate(createTimestamp), createSeq);
	}
	
	public boolean isBundleInserted(String srcEid, long dtnShortDate, long createSeq)
	{		                     
		SQLiteDatabase db = mDbOpener.getReadableDatabase();
		SQLiteQueryBuilder q = queryForBundle(srcEid, dtnShortDate, createSeq);
		
		Cursor c = q.query(db,
						   checkForBundleProjection,
						   null, null, null, null, null, "1");
		
		if (c == null || !c.moveToFirst()) {
			return false;
		}
		return true;  
	}
	
	private SQLiteQueryBuilder queryForBundle(String srcEid, long dtnShortDate, long createSeq)
	{
		SQLiteQueryBuilder q = new SQLiteQueryBuilder();
		
		q.setTables(BUNDLE_TABLE_NAME);
		
		q.appendWhere(SRC_COL + "=");
		q.appendWhereEscapeString(srcEid);
		q.appendWhere(" AND " + CREATETIMESTAMP_COL + "=" + dtnShortDate +
				      " AND " + CREATESEQ_COL + "=" + createSeq);
		
		return q;
	}
}
