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

import junit.framework.TestCase;
import com.ajj.robodtn.dtnUtil;
import java.util.Arrays;

public class dtnUtilTest extends TestCase {
	public void testHexStringToByteArray() {
		for(int i = 0; i < hexStringTestPairs.length; i++) {
			byte [] hexStringToBytes = dtnUtil.hexStringToByteArray(hexStringTestPairs[i].hexString);
			assertTrue(Arrays.equals(hexStringTestPairs[i].bytes, hexStringToBytes));
		}
	}

	public void testSubArray() {
		byte [] testString = dtnUtil.hexStringToByteArray("00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f");
		byte [] subTestString1 = dtnUtil.hexStringToByteArray("01 02 03 04 05 06");
		byte [] subTestString2 = dtnUtil.hexStringToByteArray("0c 0d 0e 0f");
		assertTrue(Arrays.equals(dtnUtil.subArray(testString, 1, 6), subTestString1));
		assertTrue(Arrays.equals(dtnUtil.subArray(testString, 12, 4), subTestString2));
		assertTrue(Arrays.equals(dtnUtil.subArray(testString, 12, -1), subTestString2));

		//This is asking for an invalid subarray and should result in an exception.
		boolean exceptionCaught = false;
		try {
			assertTrue(Arrays.equals(dtnUtil.subArray(testString, 12, 6), subTestString2));
		} catch (IndexOutOfBoundsException e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}
	
	private final class hexStringTestPair {
		public hexStringTestPair(byte [] bytes, String hexString) {
			this.bytes = bytes;
			this.hexString = hexString;
		}
		public byte [] bytes;
		public String hexString;
	}
	
	private final hexStringTestPair [] hexStringTestPairs = {
		new hexStringTestPair(new byte [] {(byte) 0x00, (byte) 0xA0, (byte) 0xBF}, "00A0BF"),
		new hexStringTestPair(new byte [] {(byte) 0x00, (byte) 0xA0, (byte) 0xBF}, "00A0bf"),
		new hexStringTestPair(new byte [] {(byte) 0x00, (byte) 0xA0, (byte) 0xBF}, "00a0bf"),
		new hexStringTestPair(new byte [] {(byte) 0xA0, (byte) 0xBF}, "A0bf"), 
		new hexStringTestPair(new byte [] {(byte) 0x12, (byte) 0x34, (byte) 0xA0, (byte) 0x10, (byte) 0xFF}, "1234a010ff"),
		new hexStringTestPair(new byte [] {(byte) 0x12, (byte) 0x34, (byte) 0xA0, (byte) 0x10, (byte) 0xFF}, "12 34		a010 ff")
	};
}
