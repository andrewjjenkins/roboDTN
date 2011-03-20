package com.ajj.robodtn.acquire;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.Malformity;

public class BpBlockAcquisitionStream extends SdnvDataInputStream {
	public BpBlockAcquisitionStream(InputStream in) {
		super(in);
	}
	
	public BundleBlock readBundleBlock() throws IOException, MalformedBundleException {
		BundleBlock b = new BundleBlock();
		
		/* Read the block header */
		try {
			b.type = readUnsignedByte();
		} catch (EOFException e) {
			/* If we can't read the block type because EOF, this is most likely
			 * because the last block doesn't have the last block flag set. */
			throw new MalformedBundleException(Malformity.NOLASTBLOCK,
					"Last block flag not set on the last block");
		}
		
		try{
			b.flags = readSdnv();
			b.len = readSdnv();
			if(b.len > Integer.MAX_VALUE) {
				throw new MalformedBundleException(Malformity.TOOBIG, "Block " + b.type 
						+ " was too big (" + b.len + ") for this BP implementation");
			}
			
			/* Read the payload. */
			/* FIXME: Handle really big payloads and efficiency improvements. */
			b.payload = new byte[(int) b.len];
			readFully(b.payload, 0, (int) (b.len));
		} catch (EOFException e) {
			throw new MalformedBundleException(Malformity.TOOSHORT, 
					"EOF while reading bundle block");
		}
		
		return b;
	}
}
