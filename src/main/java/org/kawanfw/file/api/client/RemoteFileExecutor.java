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
package org.kawanfw.file.api.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.kawanfw.commons.client.http.HttpTransfer;
import org.kawanfw.commons.client.http.SimpleNameValuePair;
import org.kawanfw.commons.json.ListOfStringTransport;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.util.parms.Action;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * 
 * Executes RemoteFile operations on all methods except list() and filesList() methods.
 * 
 * @author Nicolas de Pomereu
 *
 */
class RemoteFileExecutor {

    protected static final String FILE_SESSION_IS_CLOSED = "RemoteSession is closed.";

    /** For debug info */
    protected static boolean DEBUG = FrameworkDebug.isSet(RemoteFileExecutor.class);

    /** The Remote File on which to execute list actions */
    protected RemoteFile remoteFile = null;

    /** The remote session */
    protected RemoteSession remoteSession = null;

    /**
     * The username is stored in static memory to be passed to upload file
     * servlet
     */
    protected String username = null;

    /**
     * Token is stored in static to be available during all session and contains
     * SHA-1(userId + ServerClientLogin.SECRET_FOR_LOGIN) computed by server.
     * Token is re-send and checked at each send or recv command to be sure user
     * is authenticated.
     */
    protected String authenticationToken = null;

    /** The http transfer instance */
    protected HttpTransfer httpTransfer = null;

    /**
     * Constructor
     * 
     * @param remoteFile
     *            The Remote File on which to execute list actions
     */
    public RemoteFileExecutor(RemoteFile remoteFile) {
	this.remoteFile = remoteFile;
	this.remoteSession = remoteFile.getRemoteSession();

	this.username = this.remoteSession.getUsername();
	this.authenticationToken = this.remoteSession.getAuthenticationToken();
	this.httpTransfer = this.remoteSession.getHttpTransfer();
    }

    /**
     * Calls a remote File method and (eventually) pass some parameters to it.
     * The File method called returns a unique object, not a list
     * 
     * @param methodName
     *            the {@code File} method name to call on server in the format
     *            <code>canRead</code>, without parenthisiss
     * @param params
     *            the array of parameters passed to the method
     * 
     * @return the result of the File method call as {@code String}
     * 
     * @throws IllegalArgumentException
     *             if methodName is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * 
     * @throws UnknownHostException
     *             if host URL (http://www.acme.org) does not exists or no
     *             Internet Connection.
     * @throws ConnectException
     *             if the Host is correct but the {@code ServerFileManager}
     *             Servlet is not reachable
     *             (http://www.acme.org/ServerFileManager) and access failed
     *             with a status != OK (200). (If the host is incorrect, or is
     *             impossible to connect to - Tomcat down - the
     *             {@code ConnectException} will be the sub exception
     *             {@code HttpHostConnectException}.)
     * @throws SocketException
     *             if network failure during transmission
     * @throws RemoteException
     *             an exception has been thrown on the server side
     * @throws IOException
     *             for all other IO / Network / System Error
     * 
     */

    public String fileMethodOneReturn(String pathname, String methodName,
	    Object... params) throws IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException {

	if (pathname == null) {
	    throw new IllegalArgumentException("remoteFile can not be null!");
	}

	// Class and method name can not be null
	if (methodName == null) {
	    throw new IllegalArgumentException("methodName can not be null!");
	}

	if (username == null || authenticationToken == null) {
	    throw new InvalidLoginException(FILE_SESSION_IS_CLOSED);
	}

	// Trap locally the File methods that throw NullPointerException
	if ((methodName.equals("renameTo") || methodName.equals("compareTo"))
		&& (params == null || params.length == 0)) {
	    throw new NullPointerException();
	}

	// Trap locally the File methods that throw IllegalArgumentException
	if (methodName.equals("setLastModified")) {
	    String value = params[0].toString();
	    long longValue = Long.parseLong(value);
	    if (longValue < 0) {
		throw new IllegalArgumentException("Negative time");
	    }
	}

	// Build the params types
	List<String> paramsTypes = new Vector<String>();

	// Build the params values
	List<String> paramsValues = new Vector<String>();

	debug("");

	for (int i = 0; i < params.length; i++) {
	    if (params[i] == null) {
		throw new IllegalArgumentException(
			Tag.PRODUCT
				+ " null values are not supported. Please provide a value for all parameters.");
	    } else {
		String classType = params[i].getClass().getName();

		// NO! can alter class name if value is obsfucated
		// classType = StringUtils.substringAfterLast(classType, ".");
		paramsTypes.add(classType);

		String value = params[i].toString();

		debug("");
		debug("classType: " + classType);
		debug("value    : " + value);

		paramsValues.add(value);
	    }
	}

	String jsonParamTypes = ListOfStringTransport.toJson(paramsTypes);
	String jsonParamValues = ListOfStringTransport.toJson(paramsValues);

	debug("methodName     : " + methodName);
	debug("jsonParamTypes : " + jsonParamTypes);
	debug("jsonParamValues: " + jsonParamValues);

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.FILE_METHOD_ONE_RETURN_ACTION));
	requestParams.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(new SimpleNameValuePair(Parameter.TOKEN,
		authenticationToken));
	requestParams
		.add(new SimpleNameValuePair(Parameter.FILENAME, pathname));
	requestParams.add(new SimpleNameValuePair(Parameter.METHOD_NAME,
		methodName));
	requestParams.add(new SimpleNameValuePair(Parameter.PARAMS_TYPES,
		jsonParamTypes));
	requestParams.add(new SimpleNameValuePair(Parameter.PARAMS_VALUES,
		jsonParamValues));

	httpTransfer.send(requestParams);

	// Return the answer
	String response = httpTransfer.recv();

	debug("response: " + response);

	// Content is OK
	if (response.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    throw new InvalidLoginException(FILE_SESSION_IS_CLOSED);
	}

	// The response is in Html encode:
	if (!response.isEmpty()) {
	    response = HtmlConverter.fromHtml(response);
	}

	if (response.equals("null")) {
	    return null;
	}

	return response;
    }

    /**
     * debug tool
     */
    private void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
