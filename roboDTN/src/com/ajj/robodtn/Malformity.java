package com.ajj.robodtn;

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
