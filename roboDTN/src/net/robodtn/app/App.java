package net.robodtn.app;

import android.app.Activity;
import android.os.Bundle;

public class App extends Activity {
	public void onCreate(Bundle androidBundle) {
		super.onCreate(androidBundle);		
	}
	
	/* Android uses "Bundle" to mean "data passed along with an intent",
	 * while it is a specific datagram in the Bundle Protocol. */
	protected static final String bundle5050Key = "5050bundle";
}
