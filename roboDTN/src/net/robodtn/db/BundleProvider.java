package net.robodtn.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.robodtn.Bundle;
import net.robodtn.sdnvlib.dtnUtil;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import junit.framework.Assert;

public class BundleProvider extends ContentProvider {

	public static final String authority = "net.robodtn.db.BundleProvider";
	public static final int BY_BUNDLE_URI = 1;
	public static final int BY_BUNDLEROW_URI = 2;
	public static final Uri BUNDLEROW_URI = Uri.parse("content://" + authority + "/row");
	
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
	
	private static final String [] CHECKFORBUNDLE_PROJECTION = new String [] { ID_COL }; 
	
	private static final String [] REQUIREDINSERTVALUES = new String [] {
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
	
	private static final HashMap<String,String> bundleProjectionMap;
	
	
	public static Uri uriFromId(String src, long createTimestamp, long createSeq) {
		return Uri.parse("content://" + authority + 
				"/" + Uri.encode(src) + 
				"/" + createTimestamp +
				"/" + createSeq);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Required by the Android API, but for most bundle insertions it is
	 * easier to use insertBundle()
	 */
	public Uri insert(Uri uri, ContentValues values) {
		if (uriMatcher.match(uri) != BY_BUNDLE_URI) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		for(int i = 0; i < REQUIREDINSERTVALUES.length; i++) {
			if(values.containsKey(REQUIREDINSERTVALUES[i]) == false) {
				throw new IllegalArgumentException("Can't insert bundle without " +
						REQUIREDINSERTVALUES[i]);
			}
		}
		
		SQLiteDatabase db = dbOpener.getWritableDatabase();
		long rowId = db.insert(BUNDLE_TABLE_NAME, null, values);
		
		if (rowId > 0) {
			Log.d("BundleProvider", "inserted bundle " + uri.toString());
			Uri bundleUri = ContentUris.withAppendedId(BUNDLEROW_URI, rowId);
			getContext().getContentResolver().notifyChange(bundleUri, null);
			return bundleUri;
		}
		
		throw new SQLException("Failed to insert new row " + uri);
	}
	
	public static boolean isBundleInserted(ContentResolver cr, String srcEid, Date createTimestamp, long createSeq)
	{
		return isBundleInserted(cr, srcEid, dtnUtil.DateToDtnShortDate(createTimestamp), createSeq);
	}
	
	public static boolean isBundleInserted(ContentResolver cr, String srcEid, long dtnShortDate, long createSeq)
	{	
		Cursor c = cr.query(uriFromId(srcEid, dtnShortDate, createSeq), 
						 CHECKFORBUNDLE_PROJECTION, 
						 null, null, null);
		
		if (c == null || !c.moveToFirst()) {
			return false;
		}
		return true;  
	}
	
	/**
	 * Wrapper around insert() (from the ContentProvider interface) that takes
	 * a Bundle as input, and maps it to values on your behalf, and inserts.
	 * 
	 * @param b  A Bundle to be inserted into the database.
	 * @return The rowId of the bundle inserted into the database.
	 * @throws BundleAlreadyInDbException
	 */
	public static long insertBundle(ContentResolver cr, Bundle b) throws BundleAlreadyInDbException {
		Log.d("BundleProvider", "About to try inserting (" + b.src + ", " + b.createTimestamp + ", " + b.createSeq + ")");
		if(isBundleInserted(cr, b.src, b.createTimestamp, b.createSeq) == true) {
			throw new BundleAlreadyInDbException("Can't insert (" + b.src + 
					", " + b.createTimestamp + ", " + b.createSeq + "), it's already in DB");
		}
		
		long createTimestampAsLong = dtnUtil.DateToDtnShortDate(b.createTimestamp);
		
		ContentValues vals = new ContentValues();
		
		vals.put(PROCFLAGS_COL, b.procFlags);
		vals.put(DST_COL, b.dst);
		vals.put(SRC_COL, b.src);
		vals.put(RPTTO_COL, b.rptto);
		vals.put(CUST_COL, b.cust);
		vals.put(CREATETIMESTAMP_COL, createTimestampAsLong);
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
		

		Uri insertedUri = cr.insert(uriFromId(b.src, createTimestampAsLong, b.createSeq), vals);
		return ContentUris.parseId(insertedUri);
	}

	@Override
	public boolean onCreate() {
		dbOpener = new DbOpener(getContext());
		Assert.assertNotNull(dbOpener);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		String whereClause = null;
		String [] whereArgs = null;
		
		switch (uriMatcher.match(uri)) {
		case BY_BUNDLE_URI:
			List<String> pathSegments = uri.getPathSegments();
			whereClause = SRC_COL + "=? AND " + CREATETIMESTAMP_COL + "=? AND " + CREATESEQ_COL + "=?";
			whereArgs = new String [] { pathSegments.get(0), pathSegments.get(1), pathSegments.get(2) };
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		//Log.d("BundleProvider", "Finding bundle with WHERE: " + whereClause + " and args " + whereArgs.toString());
		
		// FIXME: orderBy for multiple bundle queries.
		Assert.assertNotNull(dbOpener);
		SQLiteDatabase db = dbOpener.getReadableDatabase();
		Assert.assertNotNull(db);
		
		Cursor c = db.query(BUNDLE_TABLE_NAME, projection,
							whereClause,
							whereArgs,
							null,
							null,
							null,
							"1");		/* FIXME: multiple bundle queries? */
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	public static Bundle queryBundle(ContentResolver cr, String srcEid, long dtnShortDate, long createSeq)
		throws NotFoundInDbException
	{
		Cursor c = cr.query(uriFromId(srcEid, dtnShortDate, createSeq),
						    RETRIEVEBUNDLE_PROJECTION,
						    null, null, null);
		
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
	
	public static Bundle queryBundle(ContentResolver cr, String src, Date createTimestamp, long createSeq) 
			throws NotFoundInDbException
	{
		return queryBundle(cr, src, dtnUtil.DateToDtnShortDate(createTimestamp), createSeq);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static final UriMatcher uriMatcher;
	private DbOpener dbOpener;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(authority, "*/*/*", BY_BUNDLE_URI);
		uriMatcher.addURI(authority, "row/#", BY_BUNDLEROW_URI);
		
		bundleProjectionMap = new HashMap<String,String>();
		bundleProjectionMap.put(ID_COL, ID_COL);
		bundleProjectionMap.put(PROCFLAGS_COL, PROCFLAGS_COL);
		bundleProjectionMap.put(DST_COL, DST_COL);
		bundleProjectionMap.put(SRC_COL, SRC_COL);
		bundleProjectionMap.put(RPTTO_COL, RPTTO_COL);
		bundleProjectionMap.put(CUST_COL, CUST_COL);
		bundleProjectionMap.put(CREATETIMESTAMP_COL, CREATETIMESTAMP_COL);
		bundleProjectionMap.put(CREATESEQ_COL, CREATESEQ_COL);
		bundleProjectionMap.put(LIFETIME_COL, LIFETIME_COL);
		bundleProjectionMap.put(DICTIONARYBLOB_COL, DICTIONARYBLOB_COL);
		bundleProjectionMap.put(FRAGOFFSET_COL, FRAGOFFSET_COL);
		bundleProjectionMap.put(ADULENGTH_COL, ADULENGTH_COL);
		bundleProjectionMap.put(PAYLOADFILENAME_COL, PAYLOADFILENAME_COL);
	}



}
