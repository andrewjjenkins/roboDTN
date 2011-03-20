package com.ajj.robodtn;

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
