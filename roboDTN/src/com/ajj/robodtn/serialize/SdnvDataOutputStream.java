package com.ajj.robodtn.serialize;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.ajj.robodtn.Sdnv;

public class SdnvDataOutputStream extends DataOutputStream {
	
	public SdnvDataOutputStream (OutputStream out) {
		super(out);
	}
	
	public void writeSdnv(Sdnv s) throws IOException {
		write(s.getBytes(), 0, s.getBytes().length);
	}
	
	public void writeSdnv(long l) throws IOException {
		Sdnv s = new Sdnv(l);
		write(s.getBytes(), 0, s.getBytes().length);
	}

}
