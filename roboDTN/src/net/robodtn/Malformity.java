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
package net.robodtn;

public enum Malformity {
	UNSPECIFIED,		/* Exception creator didn't specify */
	OTHER,				/* Creator was too lazy to define a new exception */
	DICTTOOSHORT,		/* Dictionary was shorter than it could possibly be. */
	EIDREFNOTINDICT,	/* The EID reference was outside the dictionary bounds. */
	INVALIDVERSION,		/* The version wasn't 0x06 (RFC5050) */
	NOLASTBLOCK,		/* There wasn't a block with the "last block" flag set. */
	TOOMANYPAYLOADS,	/* There was more than one block with the payload type 0x00. */
	TOOSHORT,			/* The bundle wasn't as long as it was supposed to be. */
	TOOBIG				/* The bundle was bigger than this implementation supports. */
}
