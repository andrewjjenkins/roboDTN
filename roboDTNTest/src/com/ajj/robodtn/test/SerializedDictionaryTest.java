package com.ajj.robodtn.test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.dtnUtil;
import com.ajj.robodtn.serialize.MalformedEidException;
import com.ajj.robodtn.serialize.SerializedDictionary;

import junit.framework.TestCase;

public class SerializedDictionaryTest extends TestCase {
	public void testSerializingDtn2Dictionary() throws MalformedEidException, UnsupportedEncodingException {
		Bundle b = new Bundle(0, "dtn://dest.dtn", "dtn://source.dtn", 
				"dtn://source.dtn/rpt-to", "dtn:none",
				dtnUtil.iso8601ToDate("2009-04-27T00:05:47Z"),
				1, 300, 0, 0);
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
				1, 300, 0, 0);
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
