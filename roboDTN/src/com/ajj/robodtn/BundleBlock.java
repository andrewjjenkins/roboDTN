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
package com.ajj.robodtn;

public class BundleBlock {
	/* Block types */
	public static final int TYPE_UNDEFINED  = -1;
	public static final int TYPE_PAYLOAD	= 0x01;
	public static final int TYPE_ECOS		= 0x13;
	
	/* Block processing flags */
	public static final long MUSTCOPY			= 1 << 0;
	public static final long REPORTBAD			= 1 << 1;
	public static final long DELETEBAD			= 1 << 2;
	public static final long LAST				= 1 << 3;
	public static final long DISCARDBADBLOCK	= 1 << 4;
	public static final long FORWARDEDUNPROC	= 1 << 5;
	public static final long HASEIDREFS			= 1 << 6;
	
	/* The granularity of positions as bundle blocks are acquired.
	 * FIXME: Indicating the position by integers makes storing in database more 
	 * rapid but limits the number of blocks per bundle, a tradeoff that needs to
	 * be evaluated.
	 */
	public static final int POSITION_GRANULARITY = 100;
	
	/* Position magic numbers. */
	public static final int POSITION_FIRST      = -10000;
	public static final int	POSITION_UNDEFINED  = 0;
	public static final int POSITION_PAYLOAD	= 0;
	public static final int POSITION_FIRST_AFTER_PAYLOAD = 
								POSITION_PAYLOAD + POSITION_GRANULARITY;
	
	public int	type;
	public int  position;
	public long flags;
	public long len;
	public byte [] payload;
	
	public BundleBlock(int type, int position, long flags, long len, byte [] payload) {
		this.type = type;
		this.position = position;
		this.flags = flags;
		this.len = len;
		this.payload = payload;
	}
	
	public BundleBlock(int type, int position, long flags, byte [] payload) {
		this(type, position, flags, payload.length, payload);
	}
	
	public BundleBlock(int type, long flags, byte [] payload) {
		this(type, POSITION_UNDEFINED, flags, payload.length, payload);
	}
	
	public BundleBlock(int type) {
		this(type, POSITION_UNDEFINED, 0, 0, null);
	}
	
	public BundleBlock() {
		this(TYPE_UNDEFINED);
	}
}
