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

import java.util.Iterator;
import java.util.List;

import net.robodtn.Bundle;
import net.robodtn.BundleBlock;
import net.robodtn.BundleBlocks;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class BundleBlockDbWrapper {
	public static final String BLOCK_TABLE_NAME = "blocks";
	public static final String ID_COL = "_id";
	public static final String BUNDLE_ID_COL = "bundleId";
	public static final String POSITION_COL = "position";
	public static final String TYPE_COL = "type";
	public static final String BLOCKFLAGS_COL = "blockFlags";
	public static final String LEN_COL = "len";
	public static final String DATA_COL = "data";
	
	public static final String BLOCK_TABLE_CREATE =
		"CREATE TABLE " + BLOCK_TABLE_NAME + " (" +
		ID_COL + " INTEGER PRIMARY KEY, " +
		BUNDLE_ID_COL + " INTEGER, " +
		POSITION_COL + " INTEGER, " +
		TYPE_COL + " INTEGER, " +
		BLOCKFLAGS_COL + " INTEGER, " +
		LEN_COL + " INTEGER, " +
		DATA_COL + " BLOB);";
	public static final String BLOCK_TABLE_DROP =
		"DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME;
	
	private static final String [] RETRIEVEBLOCK_PROJECTION = new String [] {
		ID_COL,
		BUNDLE_ID_COL,
		POSITION_COL,
		TYPE_COL,
		BLOCKFLAGS_COL,
		LEN_COL,
		DATA_COL
	};
	private static final int ID_COL_INDEX = 0;
	private static final int BUNDLE_ID_COL_INDEX = 1;
	private static final int POSITION_COL_INDEX = 2;
	private static final int TYPE_COL_INDEX = 3;
	private static final int BLOCKFLAGS_COL_INDEX = 4;
	private static final int LEN_COL_INDEX = 5;
	private static final int DATA_COL_INDEX = 6;
	
	private DbOpener mDbOpener;
	
	protected BundleBlockDbWrapper(DbOpener dbOpener) {
		mDbOpener = dbOpener;
	}
	
	public long insertBlock(BundleBlock block, long bundleId) {		
		
		ContentValues vals = new ContentValues();
		
		vals.put(BUNDLE_ID_COL, bundleId);
		vals.put(POSITION_COL, block.position);
		vals.put(TYPE_COL, block.type);
		vals.put(BLOCKFLAGS_COL, block.flags);
		vals.put(LEN_COL, block.len);
		vals.put(DATA_COL, block.payload);
		
		SQLiteDatabase db = mDbOpener.getWritableDatabase();
		
		long rowId = db.insert(BLOCK_TABLE_NAME, null, vals);
		
		if (rowId > 0) {
			return rowId;
		}
		throw new SQLException("Failed to insert new bundle block ("
				+ block.type + ", " + block.flags + ", " + block.len + ", "
				+ block.position + ") for bundleID " + bundleId);
	}

	public BundleBlock retrieveBundleBlock(long bundleId, int type, long position) throws NotFoundInDbException {
		SQLiteQueryBuilder q = queryForBlock(bundleId, type, position);
		
		SQLiteDatabase db = mDbOpener.getReadableDatabase();
		
		Cursor c = q.query(db,
						   RETRIEVEBLOCK_PROJECTION,
						   null, null,		/* FIXME: Get all cols. */
						   null, null,		/* No grouping */
						   null,			/* No ordering */
						   "1");			/* Limit 1: There's at most one block. */
		
		if (c == null || !c.moveToFirst()) {
			throw new NotFoundInDbException("Couldn't find block ("
					+ type + ", " + position + ") for bundleId " + bundleId);
		}
		
		BundleBlock block = new BundleBlock();
		
		block.type = c.getInt(TYPE_COL_INDEX);
		block.position = c.getInt(POSITION_COL_INDEX);
		block.flags = c.getLong(BLOCKFLAGS_COL_INDEX);
		block.len = c.getLong(LEN_COL_INDEX);
		block.payload = c.getBlob(DATA_COL_INDEX);
		
		return block;
	}
	
	private static final String [] checkForBlockProjection = new String [] { ID_COL };
	
	public boolean isBlockInserted(long bundleId, int type, long position) {
		SQLiteDatabase db = mDbOpener.getReadableDatabase();
		SQLiteQueryBuilder q = queryForBlock(bundleId, type, position);
		
		Cursor c = q.query(db, checkForBlockProjection,
						   null, null, null, null, null, "1");
		
		if (c == null || !c.moveToFirst()) {
			return false;
		}
		return true;
	}
	
	private SQLiteQueryBuilder queryForBlock(long bundleId, int type, long position) {
		SQLiteQueryBuilder q = new SQLiteQueryBuilder();
		
		q.setTables(BLOCK_TABLE_NAME);
		
		q.appendWhere(BUNDLE_ID_COL + "=" + bundleId + " AND " +
				TYPE_COL + "=" + type + " AND " + POSITION_COL + "=" + position);
		
		return q;
	}
	
	public BundleBlocks retrieveBundleBlocks(long bundleId) throws NotFoundInDbException {
		SQLiteDatabase db = mDbOpener.getReadableDatabase();
		SQLiteQueryBuilder q = queryForBlocks(bundleId);
		
		Cursor c = q.query(db, RETRIEVEBLOCK_PROJECTION, 
					null, null,
					null, null, 
					POSITION_COL + " ASC");
		
		if (c == null || !c.moveToFirst()) {
			throw new NotFoundInDbException("Couldn't find any blocks for bundleId " + bundleId);
		}
		
		BundleBlocks blocks = new BundleBlocks();
		
		do {
			BundleBlock block = new BundleBlock();
			block.type = c.getInt(TYPE_COL_INDEX);
			block.position = c.getInt(POSITION_COL_INDEX);
			block.flags = c.getLong(BLOCKFLAGS_COL_INDEX);
			block.len = c.getLong(LEN_COL_INDEX);
			block.payload = c.getBlob(DATA_COL_INDEX);
			blocks.addBlock(block);
			c.moveToNext();
		} while (!c.isAfterLast());
		
		return blocks;
	}
	
	private SQLiteQueryBuilder queryForBlocks(long bundleId) {
		SQLiteQueryBuilder q = new SQLiteQueryBuilder();
		
		q.setTables(BLOCK_TABLE_NAME);
		
		q.appendWhere(BUNDLE_ID_COL + "=" + bundleId);
		
		return q;
	}
	
	public void insertBundleBlocks(long bundleId, Bundle b) {
		List<BundleBlock> blocks = b.blocks.getBlocksInOrder();
		for(Iterator<BundleBlock> i = blocks.iterator(); i.hasNext(); ) {
			BundleBlock bb = i.next();
			insertBlock(bb, bundleId);
		}
	}
}
