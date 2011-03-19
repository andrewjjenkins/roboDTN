package com.ajj.robodtn.test;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.ajj.robodtn.SdnvDataInputStream;
import com.ajj.robodtn.test.SdnvTest;

public class SdnvDataInputStreamTest extends TestCase {
	public void testSdnvs() throws IOException {
		SdnvTest st = new SdnvTest();
		
		for(int i = 0; i < st.testpairs.length; i++) {
			SdnvDataInputStream is = new SdnvDataInputStream(
							new ByteArrayInputStream(st.testpairs[i].bytes));
			assert(is.readSdnv() == st.testpairs[i].value);
		}
	}
}
