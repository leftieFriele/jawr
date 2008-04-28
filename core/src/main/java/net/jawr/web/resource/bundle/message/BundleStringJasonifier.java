/**
 * Copyright 2007-2008 Jordi Hern�ndez Sell�s
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.resource.bundle.message;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Creates a Jason like structure (an object literal) from a set of message resource properties. 
 * 
 * @author Jordi Hern�ndez Sell�s
 */
public class BundleStringJasonifier {
	private Map keyMap;
	private Properties bundleValues;
	
	private static final String FUNC = "p(";
	
	

	public BundleStringJasonifier(Properties bundleValues) {
		super();
		this.bundleValues = bundleValues;
		this.keyMap = new HashMap();
		Enumeration keys = this.bundleValues.keys();
		
		// Create map tree with all the message keys. 
		while(keys.hasMoreElements())
			processKey((String)keys.nextElement());
	}

	/**
	 * Creates a tree map structure from the message bundle. 
	 * Each messages key is split by the separator (.) and used as a
	 * key in the map, for whose value a new map is created, and so forth until all 
	 * keys form the tree structure. 
	 * For instance, 
	 * com.example.message=foo 
	 * com.example.somethingelse=baz 
	 * results in 
	 * [com [example [message,somethingelse] ] ]
	 * 
	 * @param key A key in the message bundle. 
	 */
	private void processKey(String key) {
		StringTokenizer tk = new StringTokenizer(key,".");
		Map currentMap = this.keyMap;
		while(tk.hasMoreTokens()) {
			String token = tk.nextToken();
			if(!currentMap.containsKey(token))
				currentMap.put(token, new HashMap());
			currentMap = (Map) currentMap.get(token);
		}
	}
	
	/**
	 * Creates a javascript object literal representing a set of message resources. 
	 * 
	 * @return StringBuffer the object literal. 
	 */
	public StringBuffer serializeBundles() {
		StringBuffer sb = new StringBuffer("{");
		
		// Iterates over the 
		for(Iterator it = keyMap.keySet().iterator(); it.hasNext();) {
			String currentKey = (String) it.next();
			handleKey(sb,keyMap,currentKey,currentKey,!it.hasNext());
		}
		
		return sb.append("}");
	}
	
	/**
	 * Processes a leaf from the key map, adding its name and values recursively in a javascript object literal structure, 
	 * where values are invocations of a method that returns a function. 
	 * 
	 * @param sb Stringbuffer to append the javascript code. 
	 * @param currentLeaf Current Map from the keys tree. 
	 * @param currentKey Current key from the keys tree. 
	 * @param fullKey Key with ancestors as it appears in the message bundle(foo --> com.mycompany.foo)
	 * @param isLeafLast Wether this is the las item in the current leaf, to append a separator. 
	 */
	private void handleKey( final StringBuffer sb, 
							Map currentLeaf, 
							String currentKey, 
							String fullKey, 
							boolean isLeafLast) {
		
		Map newLeaf = (Map) currentLeaf.get(currentKey);
		
		if(bundleValues.containsKey(fullKey)) {
			addValuedKey(sb,currentKey,fullKey);
			if(!newLeaf.isEmpty()) {
				sb.append(",({");
				for(Iterator it = newLeaf.keySet().iterator(); it.hasNext();) {
					String newKey = (String) it.next();
					handleKey(sb,newLeaf,newKey,fullKey + "." + newKey,!it.hasNext());
				}
				sb.append("}))");
			}
			else {
				sb.append(")");
			}
		}
		else if(!newLeaf.isEmpty()) {
			sb.append(quote(currentKey))
			  .append(":{");
			for(Iterator it = newLeaf.keySet().iterator(); it.hasNext();) {
				String newKey = (String) it.next();
				handleKey(sb,newLeaf,newKey,fullKey + "." + newKey,!it.hasNext());
			}
			sb.append("}");

		}
		if(!isLeafLast)
			sb.append(",");
	}
	
	/**
	 * Add a key and its value to the object literal. 
	 * @param sb
	 * @param key
	 * @param fullKey
	 */
	private void addValuedKey(final StringBuffer sb, String key, String fullKey) {
		sb	.append(quote(key))
		.append(":")
		.append(FUNC)
		.append(quote(bundleValues.get(fullKey).toString()));
	}

	/**
	 * From JSONObject.java, from which the copyright notice is the following: 
	 * 
	 * Copyright (c) 2002 JSON.org
	 * Permission is hereby granted, free of charge, to any person obtaining a copy
	 * of this software and associated documentation files (the "Software"), to deal
	 * in the Software without restriction, including without limitation the rights
	 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	 * copies of the Software, and to permit persons to whom the Software is
	 * furnished to do so, subject to the following conditions:
	 *
	 *
	 * The above copyright notice and this permission notice shall be included in all
	 * copies or substantial portions of the Software.
	 * 
	 * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, allowing JSON
     * text to be delivered in HTML. In JSON text, a string cannot contain a
     * control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a JSON text.
	 */
	private String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
