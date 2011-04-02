package com.ajj.robodtn.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.acquire.BpBlockAcquisitionStream;
import com.ajj.robodtn.acquire.MalformedBundleException;

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
