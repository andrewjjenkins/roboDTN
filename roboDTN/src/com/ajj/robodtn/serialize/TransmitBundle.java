package com.ajj.robodtn.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.Sdnv;
import com.ajj.robodtn.dtnUtil;

public class TransmitBundle extends SdnvDataOutputStream {
	
	public TransmitBundle(OutputStream out) {
		super(out);
	}

	public void transmit(Bundle b) throws IOException, MalformedEidException {
		/* The version and procFlags fields are not counted in blockLength,
		 * so we can write them without counting their length. */
		writeByte(Bundle.VERSION_RFC5050);
		writeSdnv(b.procFlags);
		
		/* blockLength = sum of all PBB fields after blockLength, so we 
		 * serialize these fields first, counting their length. */
		int blockLength = 0;
		ArrayList<byte []> fields = new ArrayList<byte []>();
		SerializedDictionary sd = new SerializedDictionary(b);
		fields.add(new Sdnv(sd.dst_so.get()).getBytes());
		fields.add(new Sdnv(sd.dst_sspo.get()).getBytes());
		fields.add(new Sdnv(sd.src_so.get()).getBytes());
		fields.add(new Sdnv(sd.src_sspo.get()).getBytes());
		fields.add(new Sdnv(sd.rptto_so.get()).getBytes());
		fields.add(new Sdnv(sd.rptto_sspo.get()).getBytes());
		fields.add(new Sdnv(sd.cust_so.get()).getBytes());
		fields.add(new Sdnv(sd.cust_sspo.get()).getBytes());
		fields.add(new Sdnv(dtnUtil.DateToDtnShortDate(b.createTimestamp)).getBytes());
		fields.add(new Sdnv(b.createSeq).getBytes());
		fields.add(new Sdnv(b.lifetime).getBytes());
		fields.add(new Sdnv(sd.bytes.length).getBytes());
		fields.add(sd.bytes);
		
		if(b.aduLength != 0) {
			fields.add(new Sdnv(b.fragOffset).getBytes());
			fields.add(new Sdnv(b.aduLength).getBytes());
		}
		
		/* Count the length of all the fields we added. */
		for(Iterator<byte []> i = fields.iterator(); i.hasNext(); ) {
			byte [] bytes = i.next();
			blockLength += bytes.length;
		}
		
		/* Write the block length. */
		writeSdnv(new Sdnv(blockLength));
		
		/* Write the rest of the fields in the primary bundle block. */
		for(Iterator<byte []> i = fields.iterator(); i.hasNext(); ) {
			byte [] bytes = i.next();
			write(bytes, 0, bytes.length);
		}
		
		/* FIXME: Write the rest of the blocks. */
	}

}
