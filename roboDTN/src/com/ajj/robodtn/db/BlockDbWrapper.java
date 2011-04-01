package com.ajj.robodtn.db;

public class BlockDbWrapper {
	public static final String BLOCK_TABLE_NAME = "blocks";
	public static final String ID_COL = "_id";
	public static final String BUNDLE_ID_COL = "bundleId";
	public static final String TYPE_COL = "type";
	public static final String BLOCKFLAGS_COL = "blockFlags";
	public static final String LENGTH_COL = "length";
	public static final String DATA_COL = "data";
	
	public static final String BLOCK_TABLE_CREATE =
		"CREATE TABLE " + BLOCK_TABLE_NAME + " (" +
		ID_COL + " INTEGER PRIMARY KEY, " +
		BUNDLE_ID_COL + " INTEGER, " +
		TYPE_COL + " INTEGER, " +
		BLOCKFLAGS_COL + " INTEGER, " +
		LENGTH_COL + " INTEGER, " +
		DATA_COL + " BLOB);";
	public static final String BLOCK_TABLE_DROP =
		"DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME;
	
	private DbOpener mDbOpener;
	
	protected BlockDbWrapper(DbOpener dbOpener) {
		mDbOpener = dbOpener;
	}
}
