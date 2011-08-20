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
import java.io.InputStream;
import java.util.Arrays;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import net.robodtn.BundleBlock;
import net.robodtn.acquire.BpBlockAcquisitionStream;
import net.robodtn.acquire.MalformedBundleException;

public class BpBlockAcquisitionStreamTest extends InstrumentationTestCase {
	public static class AcquisitionTestPair {
		public AcquisitionTestPair(String acqAsset, BundleBlock block) {
			this.acqAsset = acqAsset;
			this.block = block;
		}
		public String acqAsset;
		public BundleBlock block;
	}
	
	public static final AcquisitionTestPair [] testpairs = {
		new AcquisitionTestPair("testblocks/ecos",
			new BundleBlock(BundleBlock.TYPE_ECOS, BundleBlock.MUSTCOPY,
					new byte [] { 0x00, 0x64 }))
	};
	
	public void testAcquisitions() throws IOException, MalformedBundleException {
		AcquisitionTestPair tp;
		InputStream in;
		AssetManager am = getInstrumentation().getContext().getAssets();
		BundleBlock b;
		
		for(int i = 0; i < testpairs.length; i++) {
			tp = testpairs[i];
			in = am.open(tp.acqAsset);
			b = new BpBlockAcquisitionStream(in).readBundleBlock();
			
			assertEquals(tp.block.type, b.type);
			assertEquals(tp.block.flags, b.flags);
			assertEquals(tp.block.len, b.len);
			assertTrue(Arrays.equals(tp.block.payload, b.payload));
		}
	}
}
