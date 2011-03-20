package com.ajj.robodtn.acquire;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.BundleBlock;
import com.ajj.robodtn.Malformity;

public class BpAcquisitionStream extends SdnvDataInputStream {

	public BpAcquisitionStream(InputStream in) {
		super(in);
	}

	public Bundle readBundle() throws IOException, MalformedBundleException {
		Bundle b = new Bundle();
		boolean lastBlockRead = false;
		long dst_so, dst_sspo, src_so, src_sspo, 
				rptto_so, rptto_sspo, cust_so, cust_sspo;
		
		/* Read from the beginning of the bundle */
		b.version = readUnsignedByte();
		if(b.version != Bundle.VERSION_RFC5050) {
			throw new MalformedBundleException(Malformity.INVALIDVERSION,
					"Bundle version was " + b.version + 
					" not expected " + Bundle.VERSION_RFC5050);
		}
		try {
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
			b.dict      = new AcquireBundleDict(this);
			b.dict.setEids(b, dst_so, dst_sspo, src_so, src_sspo, rptto_so, rptto_sspo, cust_so, cust_sspo);
			
			/* If payload is a frag, read these two fields. */
			if ((b.procFlags & Bundle.FRAG) != 0) {
				b.fragOffset = readSdnv();
				b.aduLength = readSdnv();
			}
		} catch (EOFException e) {
			throw new MalformedBundleException(Malformity.TOOSHORT, 
					"Bundle too short while acquiring primary block");
		}
		
		/* Acquire bundle blocks. */
		BpBlockAcquisitionStream bb_acq = new BpBlockAcquisitionStream(in);
		while (!lastBlockRead) {
			BundleBlock bb = bb_acq.readBundleBlock();
			
			/* There can be at most one payload block per bundle. */
			if(bb.type == BundleBlock.TYPE_PAYLOAD && b.blocks.hasBlock(BundleBlock.TYPE_PAYLOAD)) {
				throw new MalformedBundleException(Malformity.TOOMANYPAYLOADS, "Too many payload blocks.");
			}
			
			b.blocks.addBlock(bb);
			
			if((bb.flags & BundleBlock.LAST) != 0) {
				lastBlockRead = true;
			}
		}
		
		return b;
	}

}
