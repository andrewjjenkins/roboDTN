package com.ajj.robodtn;

import java.util.ArrayList;
import java.util.TreeMap;

public class BundleBlocks extends TreeMap<Integer, ArrayList<BundleBlock>> {
	private static final long serialVersionUID = -5543849577575291221L;

	public void addBlock(BundleBlock bb) {
		if (containsKey(bb.type) == false) {
			ArrayList<BundleBlock> al = new ArrayList<BundleBlock>();
			al.add(bb);
			put(bb.type, al);
		} else {
			ArrayList<BundleBlock> al = get(bb.type);
			al.add(bb);
		}
	}
	
	public boolean hasBlock(int type) {
		if (containsKey(type) == false) {
			return false;
		}
		if (get(type).isEmpty() == true) {
			return false;
		}
		return true;
	}
}
