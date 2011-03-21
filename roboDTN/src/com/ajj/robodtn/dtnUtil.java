package com.ajj.robodtn;

import java.util.Date;

import android.text.format.Time;

public final class dtnUtil {
	public static Date iso8601ToDate(String isoDate) {
		Time t = new Time();
		t.parse3339(isoDate);
		return new Date(t.toMillis(true));
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
