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
import java.io.UnsupportedEncodingException;
import java.lang.String;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.Malformity;

public class AcquireBundleDict {
	public AcquireBundleDict(SdnvDataInputStream stream) throws IOException, MalformedBundleException {
		/* Read the length of the dictionary and check for sanity. */
		long longLen = stream.readSdnv();
		if(longLen > Integer.MAX_VALUE) throw new IOException("Dictionary " + longLen + " bytes; too long");
		if(longLen > 0 && longLen < 3) {
			throw new MalformedBundleException(Malformity.DICTTOOSHORT, 
					"Dictionary " + longLen + " bytes; too short");
		}
		len = (int) longLen;
		
		/* Read the dictionary. */
		if (len != 0) {
			bytes = new byte[len];
			try {
				stream.readFully(bytes, 0, len);
			} catch (EOFException e) {
				throw new MalformedBundleException(Malformity.TOOSHORT,
						"EOF before finished reading " + len + " bytes of dictionary");
			}
		} else {
			bytes = null;
		}
	}
	
	public int getLength() { return len; }
	public byte [] getBytes() { return bytes; }

	
	private String cbheEidString(long so, long sspo) {
		if(so == 0 && sspo == 0) { return "dtn:none"; }
		else { return "ipn:" + so + "." + sspo; }
	}
	
	private String dtnEidString(long so, long sspo) 
			throws MalformedBundleException {
		
		int s_len = 0;
		int ssp_len = 0;
		
		if (so > Integer.MAX_VALUE || sspo > Integer.MAX_VALUE) {
			throw new MalformedBundleException(Malformity.EIDREFNOTINDICT, "EID offsets too big");
		}
		if (so >= bytes.length) {
			throw new MalformedBundleException(Malformity.EIDREFNOTINDICT,
					"EID scheme offset " + so + " outside dictionary (" + bytes.length + ")");
		}
		if (sspo >= bytes.length) {
			throw new MalformedBundleException(Malformity.EIDREFNOTINDICT,
					"EID scheme specific part offset " + sspo + " outside dictionary (" + bytes.length + ")");
		}
		
		for(s_len = 0; so + s_len < bytes.length && bytes[(int) so + s_len] != 0; s_len++);
		for(ssp_len = 0; sspo + ssp_len < bytes.length && bytes[(int) sspo + ssp_len] != 0; ssp_len++);
		
		try {
			String eid = new String(bytes, (int) so, s_len, "US-ASCII") + ":" +
						 new String(bytes, (int) sspo, ssp_len, "US-ASCII");
			return eid;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Can't use US-ASCII encoding");
		}
	}
	
	public void setEids(Bundle b, long dst_so, long dst_sspo, long src_so, long src_sspo,
			long rptto_so, long rptto_sspo, long cust_so, long cust_sspo) 
			throws MalformedBundleException, UnsupportedEncodingException {

		if(len == 0) {
			/* CBHE */
			b.dst   = cbheEidString(dst_so, dst_sspo);
			b.src   = cbheEidString(src_so, src_sspo);
			b.rptto = cbheEidString(rptto_so, rptto_sspo);
			b.cust  = cbheEidString(cust_so, cust_sspo);
		} else {
			/* DTN2-style EIDs */
			b.dst   = dtnEidString(dst_so, dst_sspo);
			b.src   = dtnEidString(src_so, src_sspo);
			b.rptto = dtnEidString(rptto_so, rptto_sspo);
			b.cust  = dtnEidString(cust_so, cust_sspo);
		}
	}
	
	private int len;
	private byte [] bytes;
}
