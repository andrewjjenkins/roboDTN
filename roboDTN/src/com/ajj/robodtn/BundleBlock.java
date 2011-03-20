package com.ajj.robodtn;

public class BundleBlock {
	/* Block types */
	public static final int TYPE_PAYLOAD	= 0x01;
	
	/* Block processing flags */
	public static final long MUSTCOPY			= 1 << 0;
	public static final long REPORTBAD			= 1 << 1;
	public static final long DELETEBAD			= 1 << 2;
	public static final long LAST				= 1 << 3;
	public static final long DISCARDBADBLOCK	= 1 << 4;
	public static final long FORWARDEDUNPROC	= 1 << 5;
	public static final long HASEIDREFS			= 1 << 6;
	
	public int	type;
	public long flags;
	public long len;
	public byte [] payload;
	
	public BundleBlock() {
		type = -1;
	}
	
	public BundleBlock(int type, long flags, long len, byte [] payload) {
		this.type = type;
		this.flags = flags;
		this.len = len;
		this.payload = payload;
	}
	
	public BundleBlock(int type) {
		this.type = type;
		this.flags = 0;
		this.len = 0;
		this.payload = new byte[0];
	}
}
