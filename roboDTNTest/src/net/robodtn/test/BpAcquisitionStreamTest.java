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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.util.List;

import net.robodtn.BundleBlock;
import net.robodtn.sdnvlib.dtnUtil;
import net.robodtn.Bundle;
import net.robodtn.Malformity;
import net.robodtn.acquire.BpAcquisitionStream;
import net.robodtn.acquire.MalformedBundleException;

import android.content.res.*;
import android.test.InstrumentationTestCase;

public class BpAcquisitionStreamTest extends InstrumentationTestCase {
	public static class AcquisitionTestPair {
		public AcquisitionTestPair(String acqAsset, Bundle bundle, String bundleToString) {
			this.acqAsset = acqAsset;
			this.bundle = bundle;
			this.bundleToString = bundleToString;
		}
		public String acqAsset;
		public Bundle bundle;
		public String bundleToString;
	}
	
	public static final AcquisitionTestPair [] testpairs;
	
	static {
	try {
	testpairs = new AcquisitionTestPair [] {
		new AcquisitionTestPair("testbundles/ionbundle", 
			new Bundle(0x94, "ipn:1.1", "ipn:1.0", "dtn:none", "dtn:none", 
					dtnUtil.iso8601ToDate("2009-04-27T00:05:47Z"), 1, 300, 0, 0,
					new BundleBlock [] {
					new BundleBlock(BundleBlock.TYPE_PAYLOAD,
									BundleBlock.POSITION_PAYLOAD,
									BundleBlock.MUSTCOPY | BundleBlock.LAST,
									"here is a test bundle".getBytes("US-ASCII"))}),
			"Bundle (ipn:1.0, 27 Apr 2009 00:05:47 GMT, 1, 21)"),
		new AcquisitionTestPair("testbundles/dtn2bundle",
			new Bundle(0x90, "dtn://destination/app", "dtn://syme.dtn/source", 
					"dtn://syme.dtn/source", "dtn:none", 
					dtnUtil.iso8601ToDate("2011-01-30T02:24:16Z"),
					2, 60, 0, 0, new BundleBlock [] {
					new BundleBlock(BundleBlock.TYPE_PAYLOAD,
									BundleBlock.POSITION_PAYLOAD,
									BundleBlock.LAST,
									"here is a bundle from DTN2".getBytes("US-ASCII"))}),
			"Bundle (dtn://syme.dtn/source, 30 Jan 2011 02:24:16 GMT, 2, 26)"),
		new AcquisitionTestPair("testbundles/ionbundle-with-ecos",
			new Bundle(0x110, "ipn:1.1", "ipn:1.2", "dtn:none", "dtn:none",
					dtnUtil.iso8601ToDate("2011-04-01T21:36:18Z"),
					1, 3600, 0, 0, new BundleBlock [] {
					new BundleBlock(BundleBlock.TYPE_ECOS,
									BundleBlock.POSITION_FIRST,
									BundleBlock.MUSTCOPY,
									new byte [] { 0x00, 0x64 }),
					new BundleBlock(BundleBlock.TYPE_PAYLOAD,
									BundleBlock.POSITION_PAYLOAD,
									BundleBlock.MUSTCOPY | BundleBlock.LAST,
									"Hey hey here's a bundle\0".getBytes("US-ASCII"))}),
			"Bundle (ipn:1.2, 1 Apr 2011 21:36:18 GMT, 1, 24)")
	};
	} catch (UnsupportedEncodingException e) {
		throw new RuntimeException(e);
	}
	}
	
	public void testAcquisitions() throws IOException, MalformedBundleException {
		AcquisitionTestPair tp;
		InputStream in;
		AssetManager am = getInstrumentation().getContext().getAssets();
		Bundle b;

		for(int i = 0; i < testpairs.length; i++) {
			tp = testpairs[i];
			in = am.open(tp.acqAsset);
			b = new BpAcquisitionStream(in).readBundle();

			assertEquals(b.procFlags, tp.bundle.procFlags);
			assertEquals(b.src, tp.bundle.src);
			assertEquals(b.dst, tp.bundle.dst);
			assertEquals(b.rptto, tp.bundle.rptto);
			assertEquals(b.cust, tp.bundle.cust);
			assertEquals(b.createTimestamp, tp.bundle.createTimestamp);
			assertEquals(b.createSeq, tp.bundle.createSeq);
			assertEquals(b.lifetime, tp.bundle.lifetime);
			assertEquals(b.toString(), tp.bundleToString);
			
			List<BundleBlock> bbs = b.blocks.getBlocksInOrder();
			List<BundleBlock> tpbs = tp.bundle.blocks.getBlocksInOrder();
			assertTrue(tpbs.equals(bbs));

			if ((b.procFlags & Bundle.FRAG) != 0) {
				assertEquals(b.fragOffset, tp.bundle.fragOffset);
				assertEquals(b.aduLength, tp.bundle.aduLength);
			}
		}
	}
	
	private class MalformedAcquisitionTestPair {
		public String acqAsset;
		public Malformity expectedMalformity;

		public MalformedAcquisitionTestPair(String acqAsset, Malformity expectedMalformity) {
			this.acqAsset = acqAsset;
			this.expectedMalformity = expectedMalformity;
		}
	}
	
	private MalformedAcquisitionTestPair [] malformedtestpairs = {
		new MalformedAcquisitionTestPair("testbundles/dicttooshort", Malformity.DICTTOOSHORT),
		new MalformedAcquisitionTestPair("testbundles/eidrefnotindict", Malformity.EIDREFNOTINDICT),
		new MalformedAcquisitionTestPair("testbundles/invalid-version", Malformity.INVALIDVERSION),
		new MalformedAcquisitionTestPair("testbundles/nolastblock", Malformity.NOLASTBLOCK),
		new MalformedAcquisitionTestPair("testbundles/toomanypayloads", Malformity.TOOMANYPAYLOADS),
		new MalformedAcquisitionTestPair("testbundles/tooshort", Malformity.TOOSHORT),
		new MalformedAcquisitionTestPair("testbundles/tooshort2", Malformity.TOOSHORT),
		new MalformedAcquisitionTestPair("testbundles/tooshort3", Malformity.TOOSHORT)
	};
	
	public void testMalformedAcquisitions() throws IOException {
		MalformedAcquisitionTestPair tp;
		InputStream in;
		AssetManager am = getInstrumentation().getContext().getAssets();
		
		for(int i = 7; i < malformedtestpairs.length; i++) {
			tp = malformedtestpairs[i];
			in = am.open(tp.acqAsset);

			try {
				new BpAcquisitionStream(in).readBundle();

				/* If we get here, no exception, which means the parsing didn't
				 * detect the malformed bundle. */
				fail("Did not get expected exception " + tp.expectedMalformity.toString()
						+ " from test bundle " + tp.acqAsset);
			} catch (MalformedBundleException mbe) {
				assertEquals(tp.expectedMalformity, mbe.getMalformity());
			}
		}
	}
}
