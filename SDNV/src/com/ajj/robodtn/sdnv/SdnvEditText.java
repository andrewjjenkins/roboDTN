package com.ajj.robodtn.sdnv;

import com.ajj.robodtn.sdnvlib.Sdnv;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class SdnvEditText extends EditText {
	public enum Type {
		NONE,
		SDNV_HEX,
		SDNV_DEC,
		INTEGER_HEX,
		INTEGER_DEC
	};
	
	public SdnvEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SdnvEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SdnvEditText(Context context) {
		super(context);
	}
	
	private class SdnvEditWatcher implements TextWatcher {
		private SdnvApp sdnvApp;
		private Type type;
		
		public SdnvEditWatcher(SdnvApp sdnvApp, Type type) {
			this.sdnvApp = sdnvApp;
			this.type = type;
		}
		
		public void afterTextChanged(Editable s) {
			sdnvApp.update(type);
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {		
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}

	public void setupSdnv(Type type, Sdnv sdnv, SdnvApp parent) {
		this.type = type;
		this.sdnv = sdnv;
		this.parent = parent;
		
		this.addTextChangedListener(new SdnvEditWatcher(parent, type));
	}
	
	private Type type = null;
	private Sdnv sdnv = null;
	private SdnvApp parent = null;
}
