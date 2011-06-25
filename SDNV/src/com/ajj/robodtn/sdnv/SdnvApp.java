/*******************************************************************************
 * Copyright 2011 Andrew Jenkins
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ajj.robodtn.sdnv;


import java.util.Date;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

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
        gmtTime = new Time("GMT");
        localTime = new Time();
        
        // Find widgets
		mSdnvHexText    = (SdnvEditText) findViewById(R.id.sdnv_hex);
    	mSdnvDecText    = (SdnvEditText) findViewById(R.id.sdnv_dec);
    	mIntegerHexText = (SdnvEditText) findViewById(R.id.integer_hex);
    	mIntegerDecText = (SdnvEditText) findViewById(R.id.integer_dec);
    	mPickGmtDate    = (Button) findViewById(R.id.pickgmtdate);
    	mPickGmtTime    = (Button) findViewById(R.id.pickgmttime);
    	mPickLocalDate  = (Button) findViewById(R.id.picklocaldate);
    	mPickLocalTime  = (Button) findViewById(R.id.picklocaltime);
    	
    	// Set up edit boxes to call this.update when edited.
    	mSdnvHexText.setupSdnv(Type.SDNV_HEX, this);
    	mSdnvDecText.setupSdnv(Type.SDNV_DEC, this);
    	mIntegerHexText.setupSdnv(Type.INTEGER_HEX, this);
    	mIntegerDecText.setupSdnv(Type.INTEGER_DEC, this);
    	updateDatesFromNumbers();

    	// Set up buttons for picking the date and associate handlers.
    	mPickGmtDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(ID_DIALOG_GMTDATEPICK);
			}
		});
    	mPickGmtTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(ID_DIALOG_GMTTIMEPICK);
			}
		});
    	mPickLocalDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(ID_DIALOG_LOCALDATEPICK);
			}
		});
    	mPickLocalTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(ID_DIALOG_LOCALTIMEPICK);
			}
		});
    }
    
    public void update(Type updatedFrom) {
    	if (updatingFrom != Type.NONE) { return; }

    	/* See if the new text requires updating the other fields.
    	 * If mustUpdate is still false after this block, then the new text
    	 * didn't change the SDNV, so we don't update the SDNV or any other widgets.
    	 */
    	boolean mustUpdate = false;
    	try {
    	if (updatedFrom == Type.INTEGER_DEC) {
    		long newValue = Long.parseLong(mIntegerDecText.getText().toString(), 10);
    		if (newValue != sdnv.getValue()) {
    			sdnv.setByValue(newValue);
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.INTEGER_HEX) {
    		long newValue = Long.parseLong(mIntegerHexText.getText().toString(), 16);
    		if (newValue != sdnv.getValue()) {
    			sdnv.setByValue(newValue);
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.SDNV_HEX) {
    		Sdnv newSdnv = new Sdnv(mSdnvHexText.getText().toString(), 16);
    		if (newSdnv.equals(sdnv) == false) {
    			sdnv = newSdnv;
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.SDNV_DEC) {
    		Sdnv newSdnv = new Sdnv(mSdnvDecText.getText().toString(), 10);
    		if (newSdnv.equals(sdnv) == false) {
    			sdnv = newSdnv;
    			mustUpdate = true;
    		}
    	} else if (updatedFrom == Type.DATE) {
    		mustUpdate = true;
    	}
    	} catch (NumberFormatException e) {
    		/* FIXME: Mark somehow that we're not parsing this. */
    	}
    	
    	if (mustUpdate == false) { return; }

    	/* Prevent recursion: set updatingFrom to updatedFrom and then update
    	 * EditTexts; when their textChangedListeners call back to this
    	 * function, we'll see that updatingFrom != NONE, and short-circuit. */
    	updatingFrom = updatedFrom;
       	if (updatingFrom != Type.INTEGER_DEC) { mIntegerDecText.setText(Long.toString(sdnv.getValue())); }
    	if (updatingFrom != Type.INTEGER_HEX) { mIntegerHexText.setText(Long.toHexString(sdnv.getValue())); }
    	if (updatingFrom != Type.SDNV_DEC) { mSdnvDecText.setText(sdnv.getBytesAsString()); }
    	if (updatingFrom != Type.SDNV_HEX) { mSdnvHexText.setText(sdnv.getBytesAsHexString()); }
    	updateDatesFromNumbers();
    	updatingFrom = Type.NONE;
    }
    
    private void updateDatesFromNumbers() {
    	// Formats are the same as the Android calendar "EditEvent"
    	final int dateFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR |
			DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH | 
			DateUtils.FORMAT_ABBREV_WEEKDAY;
    	final int timeFlags = DateUtils.FORMAT_SHOW_TIME;
    	
    	Button pickGmtDate = (Button) findViewById(R.id.pickgmtdate);
    	Button pickGmtTime = (Button) findViewById(R.id.pickgmttime);
    	Button pickLocalDate = (Button) findViewById(R.id.picklocaldate);
    	Button pickLocalTime = (Button) findViewById(R.id.picklocaltime);
    	
    	// Set our representations of time.
    	gmtTime.set(dtnUtil.DtnShortDateToDate(sdnv.getValue()).getTime());
    	localTime.set(gmtTime);
    	localTime.switchTimezone(Time.getCurrentTimezone());

    	// Set the labels on the date/time picker buttons.
    	pickGmtDate.setText(DateUtils.formatDateTime(this, gmtTime.toMillis(false), dateFlags | DateUtils.FORMAT_UTC));
    	pickGmtTime.setText(DateUtils.formatDateTime(this, gmtTime.toMillis(false), timeFlags | DateUtils.FORMAT_UTC));
    	pickLocalDate.setText(DateUtils.formatDateTime(this, localTime.toMillis(false), dateFlags));
    	pickLocalTime.setText(DateUtils.formatDateTime(this, localTime.toMillis(false), timeFlags));
    }
    
    @Override
    protected Dialog onCreateDialog(int id)	{    	
    	switch(id) {
    	case ID_DIALOG_GMTDATEPICK:
    			return new DatePickerDialog(this, gmtdate_callback, 
    						gmtTime.year, gmtTime.month, gmtTime.monthDay);
		case ID_DIALOG_GMTTIMEPICK:
    			return new TimePickerDialog(this, gmttime_callback,
    						gmtTime.hour, gmtTime.minute, false);
    	case ID_DIALOG_LOCALDATEPICK:
    			return new DatePickerDialog(this, localdate_callback,
    						localTime.year, localTime.month, localTime.monthDay);
    	case ID_DIALOG_LOCALTIMEPICK:
    			return new TimePickerDialog(this, localtime_callback,
    						localTime.hour, localTime.minute, false);
    	default:
    			throw new RuntimeException("Unknown dialog id " + id + " requested");
    	}
    }
    
    protected DatePickerDialog.OnDateSetListener gmtdate_callback = 
    	new DatePickerDialog.OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			gmtTime.set(dayOfMonth, monthOfYear, year);
			setSdnvFromTime(gmtTime);
		}
	};
	
	protected TimePickerDialog.OnTimeSetListener gmttime_callback =
		new TimePickerDialog.OnTimeSetListener() {
			
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			gmtTime.set(0, minute, hourOfDay, gmtTime.monthDay, gmtTime.month, gmtTime.year);
			setSdnvFromTime(gmtTime);
		}
	};
	
    protected DatePickerDialog.OnDateSetListener localdate_callback = 
    	new DatePickerDialog.OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			localTime.set(dayOfMonth, monthOfYear, year);
			setSdnvFromTime(localTime);
		}
	};
	
	protected TimePickerDialog.OnTimeSetListener localtime_callback =
		new TimePickerDialog.OnTimeSetListener() {
			
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			localTime.set(0, minute, hourOfDay, localTime.monthDay, localTime.month, localTime.year);
			setSdnvFromTime(localTime);
		}
	};
	
	
	protected void setSdnvFromTime(Time time) {
		long newValue = dtnUtil.DateToDtnShortDate(new Date(time.toMillis(false)));
		sdnv.setByValue(newValue);
		update(Type.DATE);
		updateDatesFromNumbers();
	}
    
    private Sdnv sdnv;
    private Type updatingFrom;
    private Time gmtTime;
    private Time localTime;
    
    // Widgets
	SdnvEditText mSdnvHexText;
	SdnvEditText mSdnvDecText;
	SdnvEditText mIntegerHexText;
	SdnvEditText mIntegerDecText;
	Button		 mPickGmtDate;
	Button		 mPickGmtTime;
	Button		 mPickLocalDate;
	Button		 mPickLocalTime;
    
    
    // The date/time picker dialog IDs.
    private static final int ID_DIALOG_GMTDATEPICK = 1;
    private static final int ID_DIALOG_GMTTIMEPICK = 2;
    private static final int ID_DIALOG_LOCALDATEPICK = 3;
    private static final int ID_DIALOG_LOCALTIMEPICK = 4;   
}