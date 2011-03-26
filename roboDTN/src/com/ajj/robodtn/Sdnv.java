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

public class Sdnv {
	public Sdnv() {
		value = 0;
		bytes = new byte[1];
		bytes[0] = 0x00;
	}
	
	public Sdnv(long value) {
		setByValue(value);
	}
	
	public Sdnv(byte [] bytes) {
		setByBytes(bytes);
	}
	
	public Sdnv(byte [] bytes, int index) {
		setByBytes(bytes, index);
	}
	
	public long getValue() {
		return value;
	}
	
	public void setByValue(long value) {
		if (value < 0) {
			throw new NumberFormatException("SDNVs must be non-negative");
		}
		this.value = value;
		this.valueToBytes();
	}
	
	public byte [] getBytes() {
		return bytes;
	}
	
	public void setByBytes(byte [] bytes, int index) {
		long v = 0;
		int i;
		
		for(i = index; i < bytes.length; i++)
		{
			if (v > Long.MAX_VALUE>>7) {
				throw new NumberFormatException("SDNV is bigger than " + Long.MAX_VALUE);
			}
			v <<=7;
			v |= bytes[i] & 0x7F;
			if ((bytes[i] & 0x80) == 0) break;
		}
		
		value = v;
		this.bytes = bytes;
	}
	
	public void setByBytes(byte [] bytes) {
		setByBytes(bytes, 0);
	}
	
	private void valueToBytes() {
		// No SDNV can be bigger than 10 bytes encoded.
		final int maxSdnvByteArray = 10;
		byte [] myBytes = new byte[maxSdnvByteArray];
		
		int i = 0;
		long v = value;
		
		// Encode value into myBytes scratch space.
		do {
			myBytes[maxSdnvByteArray-1-i] = (byte) ((0x7F & v) | (i > 0 ? 0x80 : 0x00));
			v >>= 7;
			i++;
		} while (v > 0);
		
		// Allocate a new correctly-sized array for bytes and copy into it.
		bytes = new byte[i];
		System.arraycopy(myBytes, maxSdnvByteArray-i, bytes, 0, i);
	}
	
	private byte [] bytes;
	private long value;
}
