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
package com.ajj.robodtn.sdnvlibTest;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.ajj.robodtn.sdnvlib.SdnvDataInputStream;
import com.ajj.robodtn.sdnvlibTest.SdnvTest;

public class SdnvDataInputStreamTest extends TestCase {
	public void testSdnvs() throws IOException {
		for(int i = 0; i < SdnvTest.testpairs.length; i++) {
			SdnvDataInputStream is = new SdnvDataInputStream(
							new ByteArrayInputStream(SdnvTest.testpairs[i].bytes));
			assertEquals(SdnvTest.testpairs[i].value, is.readSdnv());
		}
	}
}
