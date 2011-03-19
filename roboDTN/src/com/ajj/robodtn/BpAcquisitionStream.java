package com.ajj.robodtn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class BpAcquisitionStream extends SdnvDataInputStream {

	public BpAcquisitionStream(InputStream in) {
		super(in);
	}

	public Bundle readBundle() throws IOException, MalformedBundleException {
		Bundle b = new Bundle();
		long dst_so, dst_sspo, src_so, src_sspo, 
				rptto_so, rptto_sspo, cust_so, cust_sspo;
		
		/* Read from the beginning of the bundle */
		b.version = read();
		b.procFlags = readSdnv();
		b.blockLength = readSdnv();
		
		/* Read the PBB EID offsets; can't interpret until dictionary. */
		dst_so     = readSdnv();
		dst_sspo   = readSdnv();
		src_so     = readSdnv();
		src_sspo   = readSdnv();
		rptto_so   = readSdnv();
		rptto_sspo = readSdnv();
		cust_so    = readSdnv();
		cust_sspo  = readSdnv();
		
		/* Read the creation timestamp and convert into a date.
		 * arg to Date() is milliseconds since 1970 */
		b.createTimestamp = new Date((readSdnv() + Bundle.DTNEPOCH) * 1000);
		
		/* Read more of the PBB */
		b.createSeq = readSdnv();
		b.lifetime  = readSdnv();
		b.dict      = new BundleDict(this);
		b.dict.setEids(b, dst_so, dst_sspo, src_so, src_sspo, rptto_so, rptto_sspo, cust_so, cust_sspo);
		
		/* If payload is a frag, read these two fields. */
		if ((b.procFlags & Bundle.FRAG) != 0) {
			b.fragOffset = readSdnv();
			b.aduLength = readSdnv();
		}
		
		/* FIXME: Read the rest of the bundle. */
		return b;
	}

}
