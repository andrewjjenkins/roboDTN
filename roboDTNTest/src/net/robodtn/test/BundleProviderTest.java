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
package net.robodtn.test;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.net.Uri;
import android.test.ProviderTestCase2;

import net.robodtn.Bundle;
import net.robodtn.db.BundleAlreadyInDbException;
import net.robodtn.db.BundleProvider;

import net.robodtn.db.NotFoundInDbException;
import net.robodtn.sdnvlib.dtnUtil;

public class BundleProviderTest extends ProviderTestCase2<BundleProvider> {

	public BundleProviderTest() {
		super(BundleProvider.class, BundleProvider.authority);
	}
	
	private static class uriMatcherTestPair {
		public uriMatcherTestPair(String uri, int uriMatches) {
			this.uri = Uri.parse(uri);
			this.uriMatches = uriMatches;
		}
		public uriMatcherTestPair(Uri uri, int uriMatches) {
			this.uri = uri;
			this.uriMatches = uriMatches;
		}
		public Uri uri;
		public int uriMatches;
	}
	
	private static final uriMatcherTestPair [] testpairs;
	
	static {
		testpairs = new uriMatcherTestPair [] {
			new uriMatcherTestPair("content://" + BundleProvider.authority + "/bydbrow/1", BundleProvider.BY_BUNDLEROW_URI),
			new uriMatcherTestPair("content://" + BundleProvider.authority + "/bydbrow/1000", BundleProvider.BY_BUNDLEROW_URI),
			new uriMatcherTestPair("content://" + BundleProvider.authority + "/bydbrow/1234", BundleProvider.BY_BUNDLEROW_URI),
			new uriMatcherTestPair(BundleProvider.uriFromRow(1), BundleProvider.BY_BUNDLEROW_URI),
			new uriMatcherTestPair("content://" + BundleProvider.authority + "/nonfrag/" + 
							Uri.encode("ipn:1.0") + "/" + 
							dtnUtil.DateToDtnShortDate(BpAcquisitionStreamTest.testpairs[0].bundle.createTimestamp) + "/" +
							BpAcquisitionStreamTest.testpairs[0].bundle.createSeq, BundleProvider.BY_BUNDLE_URI),
			new uriMatcherTestPair(BundleProvider.uriFromId(BpAcquisitionStreamTest.testpairs[0].bundle.src,
															dtnUtil.DateToDtnShortDate(BpAcquisitionStreamTest.testpairs[0].bundle.createTimestamp), 
															BpAcquisitionStreamTest.testpairs[0].bundle.createSeq),
								   BundleProvider.BY_BUNDLE_URI)	
		};
	}
	
	public void testBundleProvider () throws BundleAlreadyInDbException {
		
		for(int i = 0; i < testpairs.length; i++)
		{
			assertEquals("Case " + i, testpairs[i].uriMatches, BundleProvider.uriMatcher.match(testpairs[i].uri));
		}
		
		ContentResolver cr = getMockContentResolver();
		
		/* Verify that these bundles are not already in the database. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle b = BpAcquisitionStreamTest.testpairs[i].bundle;
			assertFalse(BundleProvider.isBundleInserted(cr, b.src, b.createTimestamp, b.createSeq));
		}
		
		/* Insert bundles. */
		List<Long> rowIds = new LinkedList<Long>();
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle insertedBundle = BpAcquisitionStreamTest.testpairs[i].bundle;
			rowIds.add(BundleProvider.insertBundle(cr, insertedBundle));
			
		}
		
		/* Check that these bundles are inserted. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle b = BpAcquisitionStreamTest.testpairs[i].bundle;
			assertTrue("Case " + i, BundleProvider.isBundleInserted(cr, b.src, b.createTimestamp, b.createSeq));
		}
		
		/* Verify that we extract matching bundles. */
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle expected = BpAcquisitionStreamTest.testpairs[i].bundle;
			Bundle retrieved = null; 
			
			
			/* Retrieve the bundle from the database. */
			try {
				retrieved = BundleProvider.queryBundle(cr, expected.src,
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
		
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			Bundle expected = BpAcquisitionStreamTest.testpairs[i].bundle;
			Bundle retrieved = null;
			
			/* Retrieve the bundle from the database by ID. */
			try {
				retrieved = BundleProvider.queryBundle(cr, rowIds.get(i));
			} catch (NotFoundInDbException e) {
				fail ("Couldn't find bundle by ID " + rowIds.get(i) 
						+ " (" + expected.src + ", " + expected.createTimestamp
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
				BundleProvider.insertBundle(cr, dupInsert);
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
