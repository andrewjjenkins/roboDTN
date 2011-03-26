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

import java.io.IOException;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.dtnUtil;
import com.ajj.robodtn.serialize.MalformedEidException;
import com.ajj.robodtn.serialize.TransmitBundle;

public class TransmitBundleTest extends InstrumentationTestCase {
	private class TransmitTestPair {
		public TransmitTestPair(String acqAsset, Bundle bundle) {
			this.acqAsset = acqAsset;
			this.bundle = bundle;
		}
		public String acqAsset;
		public Bundle bundle;
	}
	
	private final TransmitTestPair [] testpairs = {
		new TransmitTestPair("testbundles/dtn2bundle",
			new Bundle(0x90, "dtn://destination/app", "dtn://syme.dtn/source", 
					"dtn://syme.dtn/source", "dtn:none", 
					dtnUtil.iso8601ToDate("2011-01-30T02:24:16Z"),
					2, 60, 0, 0)),
		new TransmitTestPair("testbundles/ionbundle", 
			new Bundle(0x94, "ipn:1.1", "ipn:1.0", "dtn:none", "dtn:none", 
					dtnUtil.iso8601ToDate("2009-04-27T00:05:47Z"), 1, 300, 0, 0))

	};
	
	public void testTransmits() {
		TransmitTestPair tp;
		AssetManager am = getInstrumentation().getContext().getAssets();
		
		for(int i = 0; i < testpairs.length; i++) {
			tp = testpairs[i];
			try {
				ComparisonStream cmpstream = new ComparisonStream(am.open(tp.acqAsset));
				TransmitBundle tb = new TransmitBundle(cmpstream);			
				tb.transmit(tp.bundle);
			} catch (IOException exc) {
				fail("Streams not equal for bundle " + i + exc.toString());
			} catch (MalformedEidException e) {
				fail("Test bundle had malformed EID " + e.toString());
			}
		}
	}
}
