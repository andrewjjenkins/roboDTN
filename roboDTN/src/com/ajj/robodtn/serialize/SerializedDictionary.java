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
package com.ajj.robodtn.serialize;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ajj.robodtn.Bundle;

public class SerializedDictionary {
	
	public SerializedDictionary(Bundle b) throws MalformedEidException {
		serialize(b);
	}
	
	/* Escape any characters that are specials in regular expressions. */
	private static final String cbheSchemename = "ipn";
	private static final Pattern cbheablePattern = Pattern.compile(cbheSchemename + "\\:(\\d+)\\.(\\d+)");
	
	/* Returns true if the EID can be CBHE-encoded */
	private static boolean canBeCbheEid(String eid) {
		if (eid.equals("dtn:none")) return true;
		if (cbheablePattern.matcher(eid).matches()) return true;
		return false;
	}
	
	/* Returns true if the dictionary can be CBHE-encoded */
	private static boolean canBeCbhe(Bundle b) {
		if (!canBeCbheEid(b.dst)) return false;
		if (!canBeCbheEid(b.src)) return false;
		if (!canBeCbheEid(b.rptto)) return false;
		if (!canBeCbheEid(b.cust)) return false;
		/* FIXME: if(other_eid_refs) return false; */
		return true;
	}
	
	/* Serializes an EID that is known to be CBHE-able. */
	private static void serializeCbheEid(String eid, EidPartReference so, EidPartReference sspo) 
			throws MalformedEidException
	{
		if (eid.equals("dtn:none")) {
			so.set(0);
			sspo.set(0);
			return;
		} else {
			Matcher m = cbheablePattern.matcher(eid);
			if (m.matches() == false) {
				throw new MalformedEidException("EID " + eid + " isn't CBHE-compatible");
			}
			so.set(Long.parseLong(m.group(1)));
			sspo.set(Long.parseLong(m.group(2)));
			return;
		}
	}
	
	/* Serializes a dictionary that is known to be CBHE-able. */
	private void serializeCbhe(Bundle b) throws MalformedEidException {
		serializeCbheEid(b.dst, dst_so, dst_sspo);
		serializeCbheEid(b.src, src_so, src_sspo);
		serializeCbheEid(b.rptto, rptto_so, rptto_sspo);
		serializeCbheEid(b.cust, cust_so, cust_sspo);		
		bytes = new byte[0];
	}
	
	private void serializeFullDictionary(Bundle b) throws MalformedEidException {
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
		/* FIXME: It isn't required that you null-terminate the last element of the
		 * dictionary, since the dictionary has an encoded length.  But DTN2 does,
		 * so we do, too, because maybe that helps compatibility.
		bytes_length--;
		 */
		
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
			
			/* Copy EID string as byte array into part_bytes; null terminate */
			System.arraycopy(part_bytes, 0, bytes, cursor, part_bytes.length);
			bytes[cursor + part_bytes.length] = 0;
			cursor += part_bytes.length + 1;
		}
	}
	
	private void serialize(Bundle b) throws MalformedEidException {
		/* First try to serialize using CBHE. */
		if (canBeCbhe(b)) {
			serializeCbhe(b);
			return;
		}
		
		/* Otherwise serialize as a full dictionary. */
		serializeFullDictionary(b);
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
	
	public byte [] bytes = null;
	public EidPartReference dst_so = new EidPartReference();
	public EidPartReference dst_sspo = new EidPartReference();
	public EidPartReference src_so = new EidPartReference();
	public EidPartReference src_sspo = new EidPartReference();
	public EidPartReference rptto_so = new EidPartReference();
	public EidPartReference rptto_sspo = new EidPartReference();
	public EidPartReference cust_so = new EidPartReference();
	public EidPartReference cust_sspo = new EidPartReference();	
}
