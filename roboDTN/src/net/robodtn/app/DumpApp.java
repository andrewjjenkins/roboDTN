package net.robodtn.app;

import android.widget.TextView;
import net.robodtn.Bundle;
import net.robodtn.R;

public class DumpApp extends App {

	public void onCreate(android.os.Bundle androidBundle) {
		super.onCreate(androidBundle);
		setContentView(R.layout.dumpapp);
		
		// Find widgets
		destEid = (TextView) findViewById(R.id.destEid);
		sourceEid = (TextView) findViewById(R.id.sourceEid);
		reporttoEid = (TextView) findViewById(R.id.reporttoEid);
		custodianEid = (TextView) findViewById(R.id.custodianEid);
		
		Bundle bundle = (Bundle)(androidBundle.get(App.bundle5050Key));
		onDelivery(bundle);
	}
	
	public void onDelivery(Bundle bundle) {
		destEid.setText(bundle.dst);
		sourceEid.setText(bundle.src);
		reporttoEid.setText(bundle.rptto);
		custodianEid.setText(bundle.cust);
	}
	
	private TextView destEid;
	private TextView sourceEid;
	private TextView reporttoEid;
	private TextView custodianEid;
}
