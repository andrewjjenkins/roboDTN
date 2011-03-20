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
