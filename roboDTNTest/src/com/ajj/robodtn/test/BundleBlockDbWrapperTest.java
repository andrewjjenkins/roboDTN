package com.ajj.robodtn.test;

import java.util.Arrays;

import android.test.AndroidTestCase;

import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.db.BundleBlockDbWrapper;
import com.ajj.robodtn.db.DbOpener;
import com.ajj.robodtn.db.NotFoundInDbException;

public class BundleBlockDbWrapperTest extends AndroidTestCase {

	public void testBundleBlockDbWrapper () {
		DbOpener dbOpener = new DbOpener(getContext(), true);
		BundleBlockDbWrapper db = dbOpener.bundleBlockDbWrapper;
		
		/* Verify that these blocks are not already in the database. */
		for(int i = 0; i < BpBlockAcquisitionStreamTest.testpairs.length; i++) {
			BundleBlock block = BpBlockAcquisitionStreamTest.testpairs[i].block;
			assertFalse(db.isBlockInserted(1, block.type, block.position));
		}
		
		/* Insert blocks. */
		for(int i = 0; i < BpBlockAcquisitionStreamTest.testpairs.length; i++) {
			BundleBlock block = BpBlockAcquisitionStreamTest.testpairs[i].block;
			db.insertBlock(block, 1);
		}
		
		/* Check that these blocks are inserted. */
		for(int i = 0; i < BpBlockAcquisitionStreamTest.testpairs.length; i++) {
			BundleBlock block = BpBlockAcquisitionStreamTest.testpairs[i].block;
			assertTrue(db.isBlockInserted(1, block.type, block.position));
		}
		
		/* Verify that we extract matching blocks. */
		for(int i = 0; i < BpBlockAcquisitionStreamTest.testpairs.length; i++) {
			BundleBlock expected = BpBlockAcquisitionStreamTest.testpairs[i].block;
			BundleBlock retrieved = null;
			
			try {
				retrieved = db.retrieveBundleBlock(1, 
												   expected.type,
												   expected.position);
			} catch (NotFoundInDbException e) {
				fail("Couldn't find bundle block #" + i + " (" + 1 + ", "
						+ expected.type + ", " + expected.position + "): "
						+ e.toString());
			}
			
			/* Compare the retrieved block to what we expect to be stored. */
			assertEquals(expected.type, retrieved.type);
			assertEquals(expected.position, retrieved.position);
			assertEquals(expected.flags, retrieved.flags);
			assertEquals(expected.len, retrieved.len);
			assertTrue(Arrays.equals(expected.payload, retrieved.payload));
		}
	}
}
