package com.ajj.robodtn.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ComparisonStream extends OutputStream {
	public ComparisonStream(InputStream in) {
		this.in = in;
	}
	
	private InputStream in;
	private int bytesWritten = 0;

	public void write(int oneByte) throws IOException {
		int readByte = in.read();
		if (readByte == -1) {
			throw new IOException("Output Stream is longer than input stream");
		}
		if ((readByte & 0xFF) != (oneByte & 0xFF)) {
			throw new IOException("Output stream has " + oneByte + " but in has " + readByte + " at " + bytesWritten);
		}
		bytesWritten++;
	}
}
