package com.ajj.robodtn.db;

import java.util.Date;
import java.util.HashMap;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.dtnUtil;

import android.content.ContentValues;
import android.content.Context;
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
	public static final String HACKNOTE_COL = "hacknote";
	
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
	
	private static HashMap<String, String> sBundleProjectionMap;
	
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
	
	static {
		sBundleProjectionMap = new HashMap<String, String>();
		sBundleProjectionMap.put(ID_COL, ID_COL);
		sBundleProjectionMap.put(PROCFLAGS_COL, PROCFLAGS_COL);
		sBundleProjectionMap.put(DST_COL, DST_COL);
		sBundleProjectionMap.put(SRC_COL, SRC_COL);
		sBundleProjectionMap.put(RPTTO_COL, RPTTO_COL);
		sBundleProjectionMap.put(CUST_COL, CUST_COL);
		sBundleProjectionMap.put(CREATETIMESTAMP_COL, CREATETIMESTAMP_COL);
		sBundleProjectionMap.put(CREATESEQ_COL, CREATESEQ_COL);
		sBundleProjectionMap.put(LIFETIME_COL, LIFETIME_COL);
		sBundleProjectionMap.put(DICTIONARYBLOB_COL, DICTIONARYBLOB_COL);
		sBundleProjectionMap.put(FRAGOFFSET_COL, FRAGOFFSET_COL);
		sBundleProjectionMap.put(ADULENGTH_COL, ADULENGTH_COL);
		sBundleProjectionMap.put(PAYLOADFILENAME_COL, PAYLOADFILENAME_COL);
	}
	
	private DbOpener mDbOpener;
	
	public BundleDbWrapper(Context context) {
		mDbOpener = new DbOpener(context);
	}
	
	public BundleDbWrapper(Context context, boolean wipe) {
		mDbOpener = new DbOpener(context, wipe);
	}
	
	public long insertBundle(Bundle b) {
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
		long rowId = db.insert(BUNDLE_TABLE_NAME, HACKNOTE_COL, vals);
		
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
		SQLiteQueryBuilder q = new SQLiteQueryBuilder();
		
		q.setTables(BUNDLE_TABLE_NAME);
		q.setProjectionMap(sBundleProjectionMap);
		
		q.appendWhere(SRC_COL + "=");
		q.appendWhereEscapeString(srcEid);
		q.appendWhere(" AND " + CREATETIMESTAMP_COL + "=" + dtnShortDate +
				      " AND " + CREATESEQ_COL + "=" + createSeq);
		
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
}
