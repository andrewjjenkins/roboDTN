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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.ajj.robodtn.BundleBlock.*;
import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.BundleBlocks;
import android.test.InstrumentationTestCase;
import junit.framework.AssertionFailedError;

public class BundleBlocksTest extends InstrumentationTestCase {
	
	public void testBundleBlocks() {
		BundleBlocks bb = new BundleBlocks();
		
		assertEquals(false, bb.hasBlock(TYPE_PAYLOAD));
		bb.addBlock(new BundleBlock(TYPE_PAYLOAD));
		assertEquals(true, bb.hasBlock(TYPE_PAYLOAD));
		
		assertEquals(false, bb.hasBlock(3));
		bb.addBlock(new BundleBlock(3, 1, new byte [0]));
		assertEquals(true, bb.hasBlock(3));
		ArrayList<BundleBlock> al = bb.get(3);
		assertEquals(1, al.size());
		assertEquals(1, al.get(0).flags);
		assertEquals(0, al.get(0).len);
		
		bb.addBlock(new BundleBlock(3, 2, new byte [3]));
		assertEquals(true, bb.hasBlock(3));
		al = bb.get(3);
		assertEquals(2, al.size());
		assertEquals(2, al.get(1).flags);
		assertEquals(3, al.get(1).len);
	}
	
	public static final List<BundleBlock> orderedBlockList;
	
	static {
		try {
			orderedBlockList =
			Arrays.asList(
				new BundleBlock(TYPE_ECOS, POSITION_FIRST,
						MUSTCOPY, 2, new byte [] { 0x64, 0x00 }),
				new BundleBlock( 0x15, POSITION_FIRST + POSITION_GRANULARITY,
						MUSTCOPY | REPORTBAD, 3, new byte [] { (byte) 0xAA, 0x55, (byte) 0xAA }),
				new BundleBlock( 0x15, POSITION_FIRST + 2*POSITION_GRANULARITY,
						MUSTCOPY | REPORTBAD | FORWARDEDUNPROC, "hellotestblock".getBytes("US-ASCII")),
				new BundleBlock(TYPE_PAYLOAD, POSITION_PAYLOAD,
						MUSTCOPY, "this is the payload block".getBytes("US-ASCII")),
				new BundleBlock(0x15, POSITION_FIRST_AFTER_PAYLOAD,
						MUSTCOPY | REPORTBAD, "postpayloadtestblock".getBytes("US-ASCII")),
				new BundleBlock(0x15, POSITION_FIRST_AFTER_PAYLOAD + POSITION_GRANULARITY,
						MUSTCOPY | REPORTBAD, "anotherpostpayloadtestblock".getBytes("US-ASCII")),
				new BundleBlock(0x14, POSITION_FIRST_AFTER_PAYLOAD + 2*POSITION_GRANULARITY,
						MUSTCOPY | REPORTBAD | LAST, "lastblock".getBytes("US-ASCII"))
			);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.toString());
		}
	}
	
	
	/* Takes a list of bundle blocks and hands them to the BundleBlocks
	 * class in a random order, and then verifies that getBlocksInOrder()
	 * correctly sorts them. */
	public void testBundleBlockOrdering() {
		final int RUNS = 10;
		final int RANDSEED = 1234;
		Random random = new Random(RANDSEED);

		for (int run = 0; run < RUNS; run++) {
			BundleBlocks bb = new BundleBlocks();
			List<BundleBlock> shuffleList = new ArrayList<BundleBlock>(orderedBlockList);
			
			/* Insert each bundle block in a random order, as if we were adding
			 * these bundle blocks in extension block processors, and each
			 * extension block processor had "weird" requirements for where its
			 * blocks were in the list. */
			/* We use a predictable seed so that if it doesn't work we can repeat. */
			Collections.shuffle(shuffleList, random);
			for(Iterator<BundleBlock> i = shuffleList.iterator(); i.hasNext(); ) {
				bb.addBlock(i.next());
			}
			
			/* Verify that even though the blocks were handed to addBlock() in a
			 * random order, getBlocksInOrder() returns them in their position-order.
			 */
			List<BundleBlock> hopefullyOrderedList = bb.getBlocksInOrder();
			try {
				assertTrue(orderedBlockList.equals(hopefullyOrderedList));
			} catch (AssertionFailedError e) {
				throw new AssertionFailedError("getBlocksInOrder() mismatch on run " + run
						+ ", RANDSEED " + RANDSEED + ": " + e.toString());
			}
		}
	}
}
