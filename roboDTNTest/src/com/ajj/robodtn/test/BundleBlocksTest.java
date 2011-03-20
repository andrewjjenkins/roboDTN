package com.ajj.robodtn.test;

import java.util.ArrayList;

import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.BundleBlocks;
import android.test.InstrumentationTestCase;

public class BundleBlocksTest extends InstrumentationTestCase {
	
	public void testBundleBlocks() {
		BundleBlocks bb = new BundleBlocks();
		
		assertEquals(false, bb.hasBlock(BundleBlock.TYPE_PAYLOAD));
		bb.addBlock(new BundleBlock(BundleBlock.TYPE_PAYLOAD));
		assertEquals(true, bb.hasBlock(BundleBlock.TYPE_PAYLOAD));
		
		assertEquals(false, bb.hasBlock(3));
		bb.addBlock(new BundleBlock(3, 1, 0, null));
		assertEquals(true, bb.hasBlock(3));
		ArrayList<BundleBlock> al = bb.get(3);
		assertEquals(1, al.size());
		assertEquals(1, al.get(0).flags);
		
		bb.addBlock(new BundleBlock(3, 2, 0, null));
		assertEquals(true, bb.hasBlock(3));
		al = bb.get(3);
		assertEquals(2, al.size());
		assertEquals(2, al.get(1).flags);		
	}
}
