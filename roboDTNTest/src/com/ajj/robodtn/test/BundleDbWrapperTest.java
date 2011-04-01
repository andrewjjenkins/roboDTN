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

import android.test.AndroidTestCase;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.db.BundleAlreadyInDbException;
import com.ajj.robodtn.db.BundleDbWrapper;
import com.ajj.robodtn.db.DbOpener;
import com.ajj.robodtn.db.NotFoundInDbException;

public class BundleDbWrapperTest extends AndroidTestCase {

	public void testBundleDbWrapper () throws BundleAlreadyInDbException {
		DbOpener dbOpener = new DbOpener(getContext(), true);
		BundleDbWrapper db = dbOpener.bundleDbWrapper;
		
		/* Verify that these bundles are not already in the database. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle b = BpAcquisitionStreamTest.testpairs[i].bundle;
			assertFalse(db.isBundleInserted(b.src, b.createTimestamp, b.createSeq));
		}
		
		/* Insert bundles. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle insertedBundle = BpAcquisitionStreamTest.testpairs[i].bundle;
			db.insertBundle(insertedBundle);
		}
		
		/* Check that these bundles are inserted. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle b = BpAcquisitionStreamTest.testpairs[i].bundle;
			assertTrue(db.isBundleInserted(b.src, b.createTimestamp, b.createSeq));
		}		
		
		/* Verify that we extract matching bundles. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle expected = BpAcquisitionStreamTest.testpairs[i].bundle;
			Bundle retrieved = null; 
			
			
			/* Retrieve the bundle from the database. */
			try {
				retrieved = db.retrieveBundle(expected.src,
											  expected.createTimestamp,
											  expected.createSeq);
			} catch (NotFoundInDbException e) {
				fail("Couldn't find bundle #" + i + " (" + expected.src
						+ ", " + expected.createTimestamp 
						+ ", " + expected.createSeq + "): " + e.toString());
			}
			
			/* Compare the retrieved bundle to what we expect to be stored. */
			assertEquals(expected.procFlags, retrieved.procFlags);
			assertEquals(expected.dst, retrieved.dst);
			assertEquals(expected.src, retrieved.src);
			assertEquals(expected.rptto, retrieved.rptto);
			assertEquals(expected.cust, retrieved.cust);
			assertEquals(expected.createTimestamp, retrieved.createTimestamp);
			assertEquals(expected.createSeq, retrieved.createSeq);
			assertEquals(expected.lifetime, retrieved.lifetime);
			assertEquals(expected.fragOffset, retrieved.fragOffset);
			assertEquals(expected.aduLength, retrieved.aduLength);
		}
		
		/* Verify that we can't insert these bundles again. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle dupInsert = BpAcquisitionStreamTest.testpairs[i].bundle;
			
			/* Retrieve the bundle from the database. */
			try {
				db.insertBundle(dupInsert);
			} catch (BundleAlreadyInDbException e) {
				continue;
			}
			fail("Could insert more than one copy of bundle #" + i 
					+ " (" + dupInsert.src
					+ ", " + dupInsert.createTimestamp 
					+ ", " + dupInsert.createSeq + ")");
		}
	}
}
