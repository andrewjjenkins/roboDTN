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
package com.ajj.robodtn.test;

import java.util.Arrays;

import android.test.AndroidTestCase;

import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.BundleBlocks;
import com.ajj.robodtn.db.BundleBlockDbWrapper;
import com.ajj.robodtn.db.DbOpener;
import com.ajj.robodtn.db.NotFoundInDbException;
import com.ajj.robodtn.test.BpAcquisitionStreamTest.AcquisitionTestPair;

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
	
	public void testRetrieveBundleBlocks () {
		DbOpener dbOpener = new DbOpener(getContext(), true);
		BundleBlockDbWrapper db = dbOpener.bundleBlockDbWrapper;
		
		/* Insert blocks from test bundles. */
		AcquisitionTestPair [] tps = BpAcquisitionStreamTest.testpairs;
		for(int i = 0; i < tps.length; i++) {
			db.insertBundleBlocks(i, tps[i].bundle);
		}
		
		/* Retrieve blocks from test bundles. */
		for(int i = 0; i < tps.length; i++) {
			BundleBlocks expected = tps[i].bundle.blocks;
			BundleBlocks retrieved = null;
			try {
				retrieved = db.retrieveBundleBlocks(i);
			} catch (NotFoundInDbException e) {
				fail("Couldn't find blocks for test bundle #" + i + " (" 
						+ tps[i].acqAsset + ")");
			}
			assertTrue(expected.equals(retrieved));
		}
	}
}
