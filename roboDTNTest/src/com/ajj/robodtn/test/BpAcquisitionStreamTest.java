package com.ajj.robodtn.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.ajj.robodtn.BpAcquisitionStream;
import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.MalformedBundleException;

import android.content.res.*;
import android.test.InstrumentationTestCase;
import android.text.format.Time;

public class BpAcquisitionStreamTest extends InstrumentationTestCase {
	private static Date iso8601ToDate(String isoDate) {
		Time t = new Time();
		t.parse3339(isoDate);
		return new Date(t.toMillis(true));
	}
	
	private class AcquisitionTestPair {
		public AcquisitionTestPair(String acqAsset, Bundle bundle) {
			this.acqAsset = acqAsset;
			this.bundle = bundle;
		}
		public String acqAsset;
		public Bundle bundle;
	}
	
	private final AcquisitionTestPair [] testpairs = {
		new AcquisitionTestPair("testbundles/ionbundle", 
			new Bundle(0x94, "ipn:1.1", "ipn:1.0", "dtn:none", "dtn:none", 
					iso8601ToDate("2009-04-27T00:05:47Z"), 1, 300, 0, 0)),
		new AcquisitionTestPair("testbundles/dtn2bundle",
			new Bundle(0x90, "dtn://destination/app", "dtn://syme.dtn/source", 
					"dtn://syme.dtn/source", "dtn:none", 
					iso8601ToDate("2011-01-30T02:24:16Z"),
					2, 60, 0, 0))
	};
	
	public void testAcquisitions() throws IOException, MalformedBundleException {
		AcquisitionTestPair tp;
		InputStream in;
		AssetManager am = getInstrumentation().getContext().getAssets();
		Bundle b;
		
		for(int i = 0; i < testpairs.length; i++) {
			tp = testpairs[i];
			in = am.open(tp.acqAsset);
			b = new BpAcquisitionStream(in).readBundle();
			
			assertEquals(b.procFlags, tp.bundle.procFlags);
			assertEquals(b.src, tp.bundle.src);
			assertEquals(b.dst, tp.bundle.dst);
			assertEquals(b.rptto, tp.bundle.rptto);
			assertEquals(b.cust, tp.bundle.cust);
			assertEquals(b.createTimestamp, tp.bundle.createTimestamp);
			assertEquals(b.createSeq, tp.bundle.createSeq);
			assertEquals(b.lifetime, tp.bundle.lifetime);
			
			if ((b.procFlags & Bundle.FRAG) != 0) {
				assertEquals(b.fragOffset, tp.bundle.fragOffset);
				assertEquals(b.aduLength, tp.bundle.aduLength);
			}
		}
	}
	
	private class MalformedAcquisitionTestPair {
		public String acqAsset;
		public MalformedBundleException expectedException;
	}
	
	private MalformedAcquisitionTestPair [] malformedtestpairs = {
		new MalformedAcquisitionTestPair("testbundles/dicttooshort", )
	}
	
	public void testMalformedAcquisitions() throws IOException {
		MalformedAcquisitionTestPair tp;
		InputStream in;
		AssetManager am = getInstrumentation().getContext().getAssets();
		Bundle b;
		
		for(int i = 0; i < malformedtestpairs.length; i++) {
			tp = malformedtestpairs[i];
			tp.expectedException.
		}
	}
}
