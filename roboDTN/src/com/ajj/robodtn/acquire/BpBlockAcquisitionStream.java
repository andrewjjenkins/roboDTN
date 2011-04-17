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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.Malformity;
import com.ajj.robodtn.sdnvlib.SdnvDataInputStream;

public class BpBlockAcquisitionStream extends SdnvDataInputStream {
	public BpBlockAcquisitionStream(InputStream in) {
		super(in);
	}
	
	public BundleBlock readBundleBlock(int position) throws IOException, MalformedBundleException {
		BundleBlock b = new BundleBlock();
		b.position = position;
		
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
	
	public BundleBlock readBundleBlock() throws IOException, MalformedBundleException {
		return readBundleBlock(BundleBlock.POSITION_UNDEFINED);
	}
}
