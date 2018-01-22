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
package org.kawanfw.file.api.util.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.kawanfw.commons.client.http.HttpTransfer;
import org.kawanfw.commons.client.http.SimpleNameValuePair;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.util.parms.Action;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * 
 * Methods for file upload per unit or per chunks
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ApiOutputStreamUploader {

    public static final String SESSION_IS_CLOSED = "RemoteSession is closed.";
    
    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(ApiOutputStreamUploader.class);
        
    /**
     * The username is stored in static memory to be passed to upload file
     * servlet
     */
    private String username = null;

    /**
     * Token is stored in static to be available during all session and contains
     * SHA-1(userId + ServerClientLogin.SECRET_FOR_LOGIN) computed by server.
     * Token is re-send and checked at each send or recv command to be sure user
     * is authenticated.
     */
    private String authenticationToken = null;
    
    /** The http transfer instance */
    private HttpTransfer httpTransfer = null;
    
    public ApiOutputStreamUploader(String username, String authenticationToken,
	    HttpTransfer httpTransfer) {
	this.username = username;
	this.authenticationToken = authenticationToken;
	this.httpTransfer = httpTransfer;
    }
     
    /**
     * Uploads a file in one chunk on the server.
     * <p>
     * The path of the remote file name is relative depending on the KRemote Files
     * configuration on the server.
     * <p>
     * 
     * @param file 
     * 		the File to upload
     * @param remoteFile
     *            the file name on the host
     * @param chunkLength
     *            the chunk length to use
     * 
     * @throws IllegalArgumentException
     *             if file or remoteFile is null
     * @throws InvalidLoginException
     *             the session has been closed by a logoff()
     * @throws FileNotFoundException
     *             if the file to upload is not found
     * 
     * @throws UnknownHostException
     *             if host URL (http://www.acme.org) does not exists or no
     *             Internet Connection.
     * @throws ConnectException
     *             if the Host is correct but the ServerFileManager Servlet is
     *             not reachable (http://www.acme.org/ServerFileManager) and
     *             access failed with a status != OK (200). (If the host is
     *             incorrect, or is impossible to connect to - Tomcat down - the
     *             ConnectException will be the sub exception
     *             HttpHostConnectException.)
     * @throws SocketException
     *             if network failure during transmission.
     * @throws RemoteException
     *             an exception has been thrown on the server side
     * @throws IOException
     *             For all other IO / Network / System Error
     * 
     */
    public void uploadOneChunk(File file, String remoteFile, long chunkLength)
	    throws IllegalArgumentException, InvalidLoginException,
	    FileNotFoundException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException

    {
	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile can not be null!");
	}

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (username == null || authenticationToken == null) {
	    throw new InvalidLoginException();
	}

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.UPLOAD_FILE_ACTION));
	requestParams.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(new SimpleNameValuePair(Parameter.TOKEN,
		authenticationToken));
	requestParams
		.add(new SimpleNameValuePair(Parameter.FILENAME, remoteFile));
	requestParams.add(new SimpleNameValuePair(Parameter.CHUNKLENGTH, ""
		+ chunkLength));

	//debug("chunkLength  : " + chunkLength);
	//debug("file.length(): " + file.length());

	 httpTransfer.send(requestParams, file);

	// If everything is OK, we have in our protocol a response that
	// 1) starts with "OK". 2) Is followed by the authenticaiton token
	// else: response starts with "INVALID_LOGIN_OR_PASSWORD".

	// Return the answer
	String receive = httpTransfer.recv();

	//debug("receive: " + receive);

	if (receive.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    throw new InvalidLoginException(SESSION_IS_CLOSED);
	}

    }  
 

    
    /**
     * debug tool
     */
    
    @SuppressWarnings("unused")
    private void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }       
    
    
}
