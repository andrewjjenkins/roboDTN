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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	
	private static class BlockPositionComparator implements Comparator<BundleBlock> {
		public int compare(BundleBlock lhs, BundleBlock rhs) {
			return (lhs.position > rhs.position) ? 1 : 
				   (lhs.position < rhs.position) ? -1 : 0;
		}
	}
	
	public List<BundleBlock> getBlocksInOrder() {
		LinkedList<BundleBlock> l = new LinkedList<BundleBlock>();
		
		for(Iterator<ArrayList <BundleBlock>> i = values().iterator(); i.hasNext(); ) {
			ArrayList<BundleBlock> al = i.next();
			l.addAll(al);
		}
		
		Collections.sort(l, new BlockPositionComparator());
		
		return l;
	}
}
