package com.ajj.robodtn.serialize;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.ajj.robodtn.Bundle;

public class SerializedDictionary {
	
	public void serialize(Bundle b) throws MalformedEidException {
		/* This will avoid storing duplicate EID scheme-names or scheme-specific-parts.
		 * There is an NP-hard compression scheme that isn't attempted here. */
		LinkedHashMap<String, ArrayList<EidPartReference>> dictSet = 
				new LinkedHashMap<String, ArrayList<EidPartReference>>();
		
		/* Add EID references from the Primary Bundle Block */
		addEidParts(b.dst, dictSet, dst_so, dst_sspo);
		addEidParts(b.src, dictSet, src_so, src_sspo);
		addEidParts(b.rptto, dictSet, rptto_so, rptto_sspo);
		addEidParts(b.cust, dictSet, cust_so, cust_sspo);
		
		/* FIXME: Add any EID references from any other blocks. */
		
		/* Count how many bytes we need for the dictionary. */
		int bytes_length = 0;
		for(Iterator<Entry<String, ArrayList<EidPartReference>>> i = dictSet.entrySet().iterator(); i.hasNext(); ) {
			Entry<String, ArrayList<EidPartReference>> e = i.next();
			bytes_length += e.getKey().length() + 1;
		}
		/* Don't need null terminator for the last string. */
		bytes_length--;
		
		/* Serialize the dictionary */
		bytes = new byte[bytes_length];
		int cursor = 0;
		for(Iterator<Entry<String, ArrayList<EidPartReference>>> i = dictSet.entrySet().iterator(); i.hasNext(); ) {
			Entry<String, ArrayList<EidPartReference>> e = i.next();
			
			/* Update each EidPartReference to point to this offset 
			 * in the serialized dictionary. */
			ArrayList<EidPartReference> reflist = e.getValue();
			for(Iterator<EidPartReference> j = reflist.iterator(); j.hasNext(); ) {
				j.next().set(cursor);
			}
			
			/* Convert EID string to ASCII byte array */
			byte [] part_bytes;
			try { 
				part_bytes = e.getKey().getBytes("US-ASCII"); 
			} catch (UnsupportedEncodingException exc) {
				throw new RuntimeException("Can't use US-ASCII encoding");
			}
			
			/* Copy EID string as byte array into part_bytes; null terminate
			 * if it isn't the last element of the dictionary. */
			System.arraycopy(part_bytes, 0, bytes, cursor, part_bytes.length);
			if(cursor + part_bytes.length < bytes_length) {
				bytes[cursor + part_bytes.length] = 0;
				cursor += part_bytes.length + 1;
				assert(i.hasNext() == true);
			} else {
				cursor += part_bytes.length;
				assert(i.hasNext() == false);
			}
		}
	}
	
	private static void addEidParts(String eid, LinkedHashMap<String, ArrayList<EidPartReference>> dictSet,
			EidPartReference so, EidPartReference sspo) throws MalformedEidException
	{
		String [] parts = eid.split(":", 2);
		if (parts.length != 2) {
			throw new MalformedEidException("EID " + eid + " doesn't have a colon");
		}
		
		/* Add the scheme name reference */
		if(dictSet.containsKey(parts[0]) == false) {
			ArrayList<EidPartReference> eidParts = new ArrayList<EidPartReference>();
			eidParts.add(so);
			dictSet.put(parts[0], eidParts);
		} else {
			dictSet.get(parts[0]).add(so);
		}
		
		/* Add the scheme specific part reference */
		if(dictSet.containsKey(parts[1]) == false) {
			ArrayList<EidPartReference> eidParts = new ArrayList<EidPartReference>();
			eidParts.add(sspo);
			dictSet.put(parts[1], eidParts);
		} else {
			dictSet.get(parts[1]).add(sspo);
		}
	}
	
	private byte [] bytes = null;
	public EidPartReference dst_so = new EidPartReference();
	public EidPartReference dst_sspo = new EidPartReference();
	public EidPartReference src_so = new EidPartReference();
	public EidPartReference src_sspo = new EidPartReference();
	public EidPartReference rptto_so = new EidPartReference();
	public EidPartReference rptto_sspo = new EidPartReference();
	public EidPartReference cust_so = new EidPartReference();
	public EidPartReference cust_sspo = new EidPartReference();	
}
