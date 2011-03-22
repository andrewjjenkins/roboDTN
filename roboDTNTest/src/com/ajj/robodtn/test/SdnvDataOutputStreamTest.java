package com.ajj.robodtn.test;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.ajj.robodtn.serialize.SdnvDataOutputStream;

public class SdnvDataOutputStreamTest extends TestCase {
	public void testStream() {
		for(int i = 0; i < SdnvTest.testpairs.length; i++) {
			try {
				ByteArrayInputStream correctAnswers = 
					new ByteArrayInputStream(SdnvTest.testpairs[i].bytes);
				ComparisonStream cs = new ComparisonStream(correctAnswers);
				SdnvDataOutputStream out = new SdnvDataOutputStream(cs);
				out.writeSdnv(SdnvTest.testpairs[i].value);
			} catch (IOException e) {
				fail("SdnvDataOutputStreamTest mismatched for SDNV test pair " + i + ": " + e.getMessage());
			}
		}
	}
}
