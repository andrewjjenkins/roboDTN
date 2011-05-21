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
package com.ajj.robodtn.sdnvlib;

import java.util.Date;

import android.text.format.Time;

public final class dtnUtil {
	/* Number of seconds difference between the DTN epoch (1/1/2000) and the
	 * UNIX epoch (1/1/1970) */
	public static final long DTNEPOCH = 	 946684800;
	
	public static Date iso8601ToDate(String isoDate) {
		Time t = new Time();
		t.parse3339(isoDate);
		return new Date(t.toMillis(true));
	}
	
	public static long DateToDtnShortDate(Date normalDate) {
		return normalDate.getTime()/1000 - DTNEPOCH;
	}
	
	public static Date DtnShortDateToDate(long dtnShortDate) {
		return new Date((dtnShortDate + DTNEPOCH) * 1000);
	}
}
