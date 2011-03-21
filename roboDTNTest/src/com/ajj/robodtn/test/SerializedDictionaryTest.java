package com.ajj.robodtn.test;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.dtnUtil;
import com.ajj.robodtn.serialize.MalformedEidException;
import com.ajj.robodtn.serialize.SerializedDictionary;

import junit.framework.TestCase;

public class SerializedDictionaryTest extends TestCase {
	
	public void testSerializingDictionary() throws MalformedEidException {
		Bundle b = new Bundle(0, "dtn://dest.dtn", "dtn://source.dtn", 
				"dtn://source.dtn/rpt-to", "dtn:none",
				dtnUtil.iso8601ToDate("2009-04-27T00:05:47Z"),
				1, 300, 0, 0);
		SerializedDictionary sd = new SerializedDictionary();
		sd.serialize(b);
		
		assertEquals(0, sd.dst_so.get());
		assertEquals(4, sd.dst_sspo.get());
		assertEquals(0, sd.src_so.get());
		assertEquals(15, sd.src_sspo.get());
		assertEquals(0, sd.rptto_so.get());
		assertEquals(28, sd.rptto_sspo.get());
		assertEquals(0, sd.cust_so.get());
		assertEquals(48, sd.cust_sspo.get());
	}
}