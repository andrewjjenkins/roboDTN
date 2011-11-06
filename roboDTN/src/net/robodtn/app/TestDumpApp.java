package net.robodtn.app;

import java.io.UnsupportedEncodingException;
import net.robodtn.Bundle;
import net.robodtn.BundleBlock;
import net.robodtn.sdnvlib.dtnUtil;
import android.app.Activity;
import android.content.Intent;

public class TestDumpApp extends Activity {

	public static final Bundle testBundle;
	static {
		try {
			testBundle = new Bundle(
					Bundle.DESTSINGLETON | Bundle.COS_NORMAL,
					"dtn:any",
					"dtn:none",
					"dtn:none",
					"dtn:none",
					dtnUtil.iso8601ToDate("2011-08-20T17:12:41Z"), 1, 300, 0, 0,
					new BundleBlock [] {
						new BundleBlock(BundleBlock.TYPE_PAYLOAD,
										BundleBlock.POSITION_PAYLOAD,
										BundleBlock.MUSTCOPY | BundleBlock.LAST,
										"Hello roboDTN!".getBytes("US-ASCII"))
					}
			);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = new Intent(this, DumpApp.class);
	}
}
