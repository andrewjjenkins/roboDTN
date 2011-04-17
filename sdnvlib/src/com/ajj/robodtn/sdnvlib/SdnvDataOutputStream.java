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
package com.ajj.robodtn.sdnvlib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


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
