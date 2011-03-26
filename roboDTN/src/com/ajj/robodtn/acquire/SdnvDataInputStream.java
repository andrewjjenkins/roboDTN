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
package com.ajj.robodtn.acquire;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SdnvDataInputStream extends DataInputStream {
	
	public SdnvDataInputStream(InputStream in) {
		super(in);
	}
	
	public long readSdnv() throws IOException {
		long v = 0;
		int i;
		int b;
		
		for(i = 0; i < 10; i++)
		{
			if (v > Long.MAX_VALUE>>7) {
				throw new NumberFormatException("SDNV is bigger than " + Long.MAX_VALUE);
			}
			b = readUnsignedByte();
			v <<=7;
			v |= b & 0x7F;
			if ((b & 0x80) == 0) return v;
		}
		throw new NumberFormatException("SDNV was too long");
	}
}
