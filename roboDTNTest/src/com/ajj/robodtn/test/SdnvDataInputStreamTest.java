package com.ajj.robodtn.test;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.ajj.robodtn.acquire.SdnvDataInputStream;
import com.ajj.robodtn.test.SdnvTest;

public class SdnvDataInputStreamTest extends TestCase {
	public void testSdnvs() throws IOException {
		for(int i = 0; i < SdnvTest.testpairs.length; i++) {
			SdnvDataInputStream is = new SdnvDataInputStream(
							new ByteArrayInputStream(SdnvTest.testpairs[i].bytes));
			assertEquals(SdnvTest.testpairs[i].value, is.readSdnv());
		}
	}
}
