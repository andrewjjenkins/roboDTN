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
package net.robodtn.acquire;

import net.robodtn.Malformity;

public class MalformedBundleException extends Exception {
	private static final long serialVersionUID = 1870019398876452908L;
	private Malformity malformity;

	MalformedBundleException(String s) {
		super(s);
		this.malformity = Malformity.UNSPECIFIED;
	}

	MalformedBundleException(Malformity malformity, String s) {
		super(s);
		this.malformity = malformity;
	}

	public Malformity getMalformity() {
		return malformity;
	}
}
