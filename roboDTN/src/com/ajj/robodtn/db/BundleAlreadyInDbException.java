package com.ajj.robodtn.db;

public class BundleAlreadyInDbException extends Exception {
	private static final long serialVersionUID = 2702170931413688727L;
	
	public BundleAlreadyInDbException(String s) {
		super(s);
	}
}
