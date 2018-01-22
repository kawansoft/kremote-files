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

import java.lang.reflect.Type;

import org.kawanfw.file.api.client.SessionParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * Class to format a SessionParameter to a JSON String and vice versa using
 * Gson.
 * 
 * @author Nicolas de Pomereu
 */

public class SessionParametersGson {

    /**
     * Protected
     */
    protected SessionParametersGson() {

    }

    /**
     * Format a SessionParameters instance into a JSON String
     * 
     * @param sessionParameters
     *            a SessionParameters instance
     * @return the JSON string
     */
    public static String toJson(SessionParameters sessionParameters) {

	Gson gson = new Gson();
	Type type = new TypeToken<SessionParameters>() {
	}.getType();
	String jsonString = gson.toJson(sessionParameters, type);

	return jsonString;
    }

    /**
     * Format a JSON String back to a SessionParameters instance
     * 
     * @param jsonString
     *            The JSON string containing the HttpProxy
     * @return the SessionParameters instance
     */
    public static SessionParameters fromJson(String jsonString) {
	Gson gson = new Gson();
	Type type = new TypeToken<SessionParameters>() {
	}.getType();

	SessionParameters sessionParameters = gson.fromJson(
		jsonString, type);
	return sessionParameters;
    }

}
