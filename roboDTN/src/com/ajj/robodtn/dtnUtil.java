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
package com.ajj.robodtn;

import java.util.Date;

import android.text.format.Time;

public final class dtnUtil {
	public static Date iso8601ToDate(String isoDate) {
		Time t = new Time();
		t.parse3339(isoDate);
		return new Date(t.toMillis(true));
	}
	
	public static long DateToDtnShortDate(Date normalDate) {
		return normalDate.getTime()/1000 - Bundle.DTNEPOCH;
	}
	
	public static Date DtnShortDateToDate(long dtnShortDate) {
		return new Date((dtnShortDate + Bundle.DTNEPOCH) * 1000);
	}
	
	public static byte[] hexStringToByteArray(String sWithWhitespace) {
		String s = sWithWhitespace.replaceAll("\\s", "");
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	public static byte[] subArray(byte [] bytes, int index, int length) {
		if(length == -1) {
			length = bytes.length - index;
		}
		byte [] toReturn = new byte[length];
		if (index < 0 || index + length > bytes.length) {
			throw new IndexOutOfBoundsException("Requested subarray [" + index + "," + index+length + 
							"] of bytes[" + bytes.length + "] invalid");
		}
		System.arraycopy(bytes, index, toReturn, 0, length);
		return toReturn;
	}
}
