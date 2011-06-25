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
		INTEGER_DEC,
		DATE
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

	public void setupSdnv(Type type, SdnvApp parent) {
		this.addTextChangedListener(new SdnvEditWatcher(parent, type));
	}
}
