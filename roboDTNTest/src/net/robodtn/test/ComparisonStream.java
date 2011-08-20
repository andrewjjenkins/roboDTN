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
package net.robodtn.test;

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
	
	public void finish() throws IOException {
		int b = in.read();
		if (b != -1) {
			throw new IOException("InputStream is longer than OutputStream; read byte " + b);
		}
	}
}
