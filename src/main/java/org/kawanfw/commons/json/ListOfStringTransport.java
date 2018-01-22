/*
 * This file is part of KRemote Files. 
 * KRemote Files: Easy file upload & download over HTTP with Java.                                    
 * Copyright (C) 2015,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * KRemote Files is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * KRemote Files is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
 */
package org.kawanfw.commons.json;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;

/**
 * @author Nicolas de Pomereu
 * 
 *         Class to transport a list of strings with JSON from the PC side.
 *         Because obsfucation creates toubles, this class must never obsfucated
 */

public class ListOfStringTransport {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(ListOfStringTransport.class);

    /**
     * Format for JSON String a list of strings
     * 
     * @param list
     *            a list of strings
     * 
     * @return the formated JSON string ready for transport
     */

    public static String toJson(List<String> list ) {

	if (list == null) {
	    throw new IllegalArgumentException("list is null!");
	}
		
	String jsonString = JSONValue.toJSONString(list);
	// free resultSetLine
	list = null;
	return jsonString;
	
    }


    /**
     * Format from JSON string a list of strings - TO BE USED ONLY ON THE SERVER
     * SIDE;
     * 
     * @param jsonString
     *            formated JSON string containing a list of strings
     * 
     * @return a list of strings
     */
    public static List<String> fromJson(String jsonString) {
        if (jsonString == null) {
            throw new IllegalArgumentException("jsonString is null!");
        }
    
        debug("List jsonString: " + jsonString);
            
	Object obj =JSONValue.parse(jsonString);
	JSONArray jsonArray=(JSONArray)obj;
	
	List<String> values = new Vector<String>();
	
	for (int i = 0; i < jsonArray.size(); i++) {
	    values.add((String) jsonArray.get(i));
	}
	
	// free JSONArray
	obj = null;
	jsonArray = null;
	
	
	return values;
	
    }

    private static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

    
}
