package com.ajj.robodtn.sdnv;


import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ajj.robodtn.sdnv.SdnvEditText.Type;
import com.ajj.robodtn.sdnvlib.Sdnv;
import com.ajj.robodtn.sdnvlib.dtnUtil;

public class SdnvApp extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sdnv = new Sdnv(0xABC);
        updatingFrom = Type.NONE;
        
		SdnvEditText sdnvHexText    = (SdnvEditText) findViewById(R.id.sdnv_hex);
    	SdnvEditText sdnvDecText    = (SdnvEditText) findViewById(R.id.sdnv_dec);
    	SdnvEditText integerHexText = (SdnvEditText) findViewById(R.id.integer_hex);
    	SdnvEditText integerDecText = (SdnvEditText) findViewById(R.id.integer_dec);
    	
    	sdnvHexText.setupSdnv(Type.SDNV_HEX, sdnv, this);
    	sdnvDecText.setupSdnv(Type.SDNV_DEC, sdnv, this);
    	integerHexText.setupSdnv(Type.INTEGER_HEX, sdnv, this);
    	integerDecText.setupSdnv(Type.INTEGER_DEC, sdnv, this);
    	updateDates();
    }
    
    public void update(Type updatedFrom) {
    	if (updatingFrom != Type.NONE) { return; }
    	
		SdnvEditText sdnvHexText    = (SdnvEditText) findViewById(R.id.sdnv_hex);
    	SdnvEditText sdnvDecText    = (SdnvEditText) findViewById(R.id.sdnv_dec);
    	SdnvEditText integerHexText = (SdnvEditText) findViewById(R.id.integer_hex);
    	SdnvEditText integerDecText = (SdnvEditText) findViewById(R.id.integer_dec);


    	/* See if the new text requires updating the other fields.
    	 * If mustUpdate is still false after this block, then the new text
    	 * didn't change the SDNV, so we don't update the SDNV or any other widgets.
    	 */
    	boolean mustUpdate = false;
    	try {
    	if (updatedFrom == Type.INTEGER_DEC) {
    		long newValue = Long.parseLong(integerDecText.getText().toString(), 10);
    		if (newValue != sdnv.getValue()) {
    			sdnv.setByValue(newValue);
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.INTEGER_HEX) {
    		long newValue = Long.parseLong(integerHexText.getText().toString(), 16);
    		if (newValue != sdnv.getValue()) {
    			sdnv.setByValue(newValue);
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.SDNV_HEX) {
    		Sdnv newSdnv = new Sdnv(sdnvHexText.getText().toString(), 16);
    		if (newSdnv.equals(sdnv) == false) {
    			sdnv = newSdnv;
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.SDNV_DEC) {
    		Sdnv newSdnv = new Sdnv(sdnvDecText.getText().toString(), 10);
    		if (newSdnv.equals(sdnv) == false) {
    			sdnv = newSdnv;
    			mustUpdate = true;
    		}
    	}
    	} catch (NumberFormatException e) {
    		/* FIXME: Mark somehow that we're not parsing this. */
    	}
    	
    	if (mustUpdate == false) { return; }

    	/* Prevent recursion: set updatingFrom to updatedFrom and then update
    	 * EditTexts; when their textChangedListeners call back to this
    	 * function, we'll see that updatingFrom != NONE, and short-circuit. */
    	updatingFrom = updatedFrom;
       	if (updatingFrom != Type.INTEGER_DEC) { integerDecText.setText(Long.toString(sdnv.getValue())); }
    	if (updatingFrom != Type.INTEGER_HEX) { integerHexText.setText(Long.toHexString(sdnv.getValue())); }
    	if (updatingFrom != Type.SDNV_DEC) { sdnvDecText.setText(sdnv.getBytesAsString()); }
    	if (updatingFrom != Type.SDNV_HEX) { sdnvHexText.setText(sdnv.getBytesAsHexString()); }
    	updateDates();
    	updatingFrom = Type.NONE;
    }
    
    private void updateDates() {
    	TextView	 gmtDateText	= (TextView) findViewById(R.id.gmtdate);
    	TextView	 localDateText  = (TextView) findViewById(R.id.localdate);
    	
    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG);
    	Date d = dtnUtil.DtnShortDateToDate(sdnv.getValue());
    	localDateText.setText(df.format(d));
    	df.setTimeZone(TimeZone.getTimeZone("GMT"));
    	gmtDateText.setText(df.format(d));

    }
    
    private Sdnv sdnv;
    private Type updatingFrom;
}