package com.ajj.robodtn.db;

public class NotFoundInDbException extends Exception {
	private static final long serialVersionUID = -4192175520701341220L;

	NotFoundInDbException(String s) {
		super(s);
	}
}
