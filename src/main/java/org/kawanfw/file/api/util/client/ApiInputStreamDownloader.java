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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.apache.commons.io.input.TeeInputStream;
import org.kawanfw.commons.client.http.HttpTransfer;
import org.kawanfw.commons.client.http.SimpleNameValuePair;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.util.parms.Action;
import org.kawanfw.file.util.parms.Parameter;

/**
 * 
 * Methods for file download per unit or per chunks
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ApiInputStreamDownloader {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(ApiInputStreamDownloader.class);
        
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
    
    public ApiInputStreamDownloader(String username, String authenticationToken,
	    HttpTransfer httpTransfer) {
	this.username = username;
	this.authenticationToken = authenticationToken;
	this.httpTransfer = httpTransfer;
    }
     
    /**
     * Creates an input stream that maps the remote file chunk
     * 
     * @param remoteSession
     *            the current file session in use
     * @param fileChunk
     *            the fileChunk to store the downlad in
     * @param remoteFile
     *            the remote file name with path
     * @return the input stream that maps the remote file
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws RemoteException
     * @throws IOException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     */
    public InputStream downloadOneChunk(
	    File fileChunk, String remoteFile, long chunkLength) throws UnknownHostException,
	    ConnectException, RemoteException, IOException,
	    InvalidLoginException, FileNotFoundException {
	// debug("downloadFile Begin");

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.DOWNLOAD_FILE_ACTION));
	requestParams.add(new SimpleNameValuePair(Parameter.USERNAME,
		username));
	requestParams.add(new SimpleNameValuePair(Parameter.TOKEN, authenticationToken));
	requestParams
		.add(new SimpleNameValuePair(Parameter.FILENAME, remoteFile));
	requestParams.add(new SimpleNameValuePair(Parameter.CHUNKLENGTH, ""
		+ chunkLength));

	InputStream in = httpTransfer.getInputStream(requestParams);

	// If there is a non null FileChunk, save content of stream for reuse
	// in case of download interruptions
	if (fileChunk != null) {
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(
		    fileChunk));
	    TeeInputStream teeIn = new TeeInputStream(in, out, true);
	    return teeIn;
	} else {
	    return in;
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
