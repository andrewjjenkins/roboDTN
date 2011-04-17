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
import java.util.Arrays;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.sdnvlib.dtnUtil;
import com.ajj.robodtn.serialize.MalformedEidException;
import com.ajj.robodtn.serialize.SerializedDictionary;

import junit.framework.TestCase;

public class SerializedDictionaryTest extends TestCase {
	public void testSerializingDtn2Dictionary() throws MalformedEidException, UnsupportedEncodingException {
		Bundle b = new Bundle(0, "dtn://dest.dtn", "dtn://source.dtn", 
				"dtn://source.dtn/rpt-to", "dtn:none",
				dtnUtil.iso8601ToDate("2009-04-27T00:05:47Z"),
				1, 300, 0, 0, null);
		byte [] dictBytes = ("dtn\0//dest.dtn\0//source.dtn\0//source.dtn/rpt-to\0none\0").getBytes("US-ASCII");
		SerializedDictionary sd = new SerializedDictionary(b);
		
		assertEquals(0, sd.dst_so.get());
		assertEquals(4, sd.dst_sspo.get());
		assertEquals(0, sd.src_so.get());
		assertEquals(15, sd.src_sspo.get());
		assertEquals(0, sd.rptto_so.get());
		assertEquals(28, sd.rptto_sspo.get());
		assertEquals(0, sd.cust_so.get());
		assertEquals(48, sd.cust_sspo.get());
		
		assertTrue(Arrays.equals(sd.bytes, dictBytes));
	}
	
	public void testSerializingCbheDictionary() throws MalformedEidException, UnsupportedEncodingException {
		Bundle b = new Bundle(0, "ipn:13.2", "ipn:71.23", 
				"dtn:none", "ipn:2.0",
				dtnUtil.iso8601ToDate("2009-04-27T00:05:47Z"),
				1, 300, 0, 0, null);
		byte [] dictBytes = new byte[0];
		SerializedDictionary sd = new SerializedDictionary(b);
		
		assertEquals(13, sd.dst_so.get());
		assertEquals(2, sd.dst_sspo.get());
		assertEquals(71, sd.src_so.get());
		assertEquals(23, sd.src_sspo.get());
		assertEquals(0, sd.rptto_so.get());
		assertEquals(0, sd.rptto_sspo.get());
		assertEquals(2, sd.cust_so.get());
		assertEquals(0, sd.cust_sspo.get());
		
		assertTrue(Arrays.equals(sd.bytes, dictBytes));
	}
}
