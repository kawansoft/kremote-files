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
package org.kawanfw.commons.client.http;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.convert.Pbe;
import org.kawanfw.file.api.client.SessionParameters;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.version.FileVersionValues;

public class SimpleNameValuePairConvertor {
    /** Debug flag */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(SimpleNameValuePairConvertor.class);

    /** The request params */
    private List<SimpleNameValuePair> requestParams = null;

    /** The http protocol parameters */
    private SessionParameters sessionParameters = null;

    /**
     * Constructor
     * 
     * @param requestParams
     *            the request parameters list of SimpleNameValuePair
     */
    public SimpleNameValuePairConvertor(List<SimpleNameValuePair> requestParams,
	    SessionParameters sessionParameters) {
	this.requestParams = requestParams;
	this.sessionParameters = sessionParameters;
    }

    /**
     * @return the convert & may be encrypted parameters
     */
    public List<SimpleNameValuePair> convert() throws IOException {
	List<SimpleNameValuePair> requestParamsConverted = new Vector<SimpleNameValuePair>();

	for (SimpleNameValuePair simpleNameValuePair : requestParams) {
	    String paramName = simpleNameValuePair.getName();
	    String paramValue = simpleNameValuePair.getValue();

	    paramValue = HtmlConverter.toHtml(paramValue);
	    
	    //Futur usage
	    //paramValue = encryptValue(paramName, paramValue);

	    debug("converted param name : " + paramName);
	    debug("converted param value: " + paramValue);

	    SimpleNameValuePair simpleNameValuePairEnc = new SimpleNameValuePair(
		    paramName, paramValue);
	    requestParamsConverted.add(simpleNameValuePairEnc);
	}

	// Add the Version
	SimpleNameValuePair basicNameValuePairEnc = new SimpleNameValuePair(
		Parameter.VERSION, FileVersionValues.VERSION);
	requestParamsConverted.add(basicNameValuePairEnc);

	return requestParamsConverted;
    }

    /**
     * Encrypt the parameter value
     * 
     * @param paramName
     *            the parameter name
     * @param paramValue
     *            the parameter value
     * 
     * @return the encrypted parameter value
     * 
     * @throws IOException
     *             if any exception occurs
     */
    @SuppressWarnings("unused")
    private String encryptValue(String paramName, String paramValue)
	    throws IOException {

	if (sessionParameters == null) {
	    return paramValue;
	}

	char[] password = null;
	// FUTUR USAGE
	//password = sessionParameters.getEncryptionPassword();

	if (password == null || password.length <= 1) {
	    return paramValue;
	}

	try {
	    paramValue = Pbe.KAWANFW_ENCRYPTED
		    + new Pbe().encryptToHexa(paramValue, password);
	} catch (Exception e) {
	    String message = "Impossible to encrypt the value of the parameter "
		    + paramName;

	    throw new IOException(message, e);
	}
	return paramValue;
    }

    /**
     * Displays the given message if DEBUG is set.
     * 
     * @param s
     *            the debug message
     */

    private static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
