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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.kawanfw.commons.client.http.SimpleNameValuePair;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.FrameworkFileUtil;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.KeepTempFilePolicyParms;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.api.util.client.RemoteFileUtil;
import org.kawanfw.file.util.parms.Action;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * 
 * Executes RemoteFile list() and listFiles() operations.
 * 
 * @author Nicolas de Pomereu
 *
 */
class RemoteFileListExecutor extends RemoteFileExecutor {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(RemoteFileListExecutor.class);

    /**
     * Constructor
     * 
     * @param remoteFile
     *            The Remote File on which to execute list actions
     */
    public RemoteFileListExecutor(RemoteFile remoteFile) {
	super(remoteFile);
    }

    /**
     * 
     * Executes a File.list() or File.list(FilenameFilter) on remote host.
     * 
     * @param remoteFile
     *            the file name of the directory on the host
     * @param callerClassName
     *            the class name that calls this method
     * 
     * @return the list of files or directories in the remote directory. Will be
     *         <code>null</code> if the remote directory does not exists. Will
     *         be empty if the remote directory exists but is empty.
     * 
     * @throws IllegalArgumentException
     *             if remoteFile is null
     * @throws InvalidLoginException
     *             if the username is refused by the remote host
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
     * @throws SecurityException
     *             the url is not secured with https (SSL)
     * @throws IOException
     *             for all other IO / Network / System Error
     * @throws IllegalAccessException
     *             if class is not instanciable
     * @throws InstantiationException
     *             if class is not instanciable
     */

    public String[] list(String remoteFile, FilenameFilter filenameFilter,
	    String callerClassName) throws IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException,
	    InstantiationException, IllegalAccessException

    {
	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile can not be null!");
	}

	if (username == null || authenticationToken == null) {
	    throw new InvalidLoginException(FILE_SESSION_IS_CLOSED);
	}

	String filenameFilterFilename = null;
	String filenameFilterClassname = null;
	String base64SerialFilenameFilter = null;
	
	if (filenameFilter != null) {
	    filenameFilterClassname = filenameFilter.getClass().getName();
	    base64SerialFilenameFilter = RemoteFileUtil.SerializeBase64FilenameFilter(
		    filenameFilter, filenameFilterClassname);
	    
	    filenameFilterFilename = RemoteFileUtil.uploadFilterIfShortSize(
		    base64SerialFilenameFilter, remoteSession);

	}

	// Launch the Servlet

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.FILE_LIST_ACTION));
	requestParams.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(new SimpleNameValuePair(Parameter.TOKEN,
		authenticationToken));
	requestParams
		.add(new SimpleNameValuePair(Parameter.FILENAME, remoteFile));

	requestParams.add(new SimpleNameValuePair(
		Parameter.FILENAME_FILTER_CLASSNAME, filenameFilterClassname));
	requestParams.add(new SimpleNameValuePair(
		Parameter.FILENAME_FILTER_FILENAME,
		filenameFilterFilename));
	
	if (RemoteFileUtil.isFilterShortSize(base64SerialFilenameFilter)) {
	    requestParams.add(new SimpleNameValuePair(
		    Parameter.BASE64_SERIAL_FILENAME_FILTER,
		    base64SerialFilenameFilter));
	}

	httpTransfer.setReceiveInFile(true); // To say we get the result into a
					     // file
	httpTransfer.send(requestParams);

	// If everything is OK, we have in our protocol a response that
	// 1) starts with "OK". 2) Is followed by the authenticaiton token
	// else: response starts with "INVALID_LOGIN_OR_PASSWORD".

	File receiveFile = httpTransfer.getReceiveFile();
	String receive = FrameworkFileUtil.getFirstLineOfFile(receiveFile);

	debug("receiveFile: " + receiveFile);

	// Content is OK
	if (receive.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    if (!DEBUG && !KeepTempFilePolicyParms.KEEP_TEMP_FILE) {
		receiveFile.delete();
	    }
	    throw new InvalidLoginException(FILE_SESSION_IS_CLOSED);
	}

	try {
	    if (receive.equals("null")) {
		return null;
	    } else if (receive.equals("[]")) {
		return new String[0];
	    } else {
		List<String> list = getFilesListFromFile(receiveFile);
		String[] array = list.toArray(new String[list.size()]);
		return array;
	    }
	} catch (Exception e) {
	    throw new IOException(e.getMessage(), e);
	} finally {
	    if (!DEBUG && !KeepTempFilePolicyParms.KEEP_TEMP_FILE) {
		receiveFile.delete();
	    }
	}
    }



    /**
     * 
     * Executes a File.listFiles() or File.listFiles(FileFilter) on remote host.
     * 
     * @param remoteFile
     *            the file name of the directory on the host
     * @param filenameFilter
     * @param callerClassName
     *            the class name that calls this method
     * @return the list of files or directories in the remote directory. Will be
     *         <code>null</code> if the remote directory does not exists. Will
     *         be empty if the remote directory exists but is empty.
     * 
     * @throws IllegalArgumentException
     *             if remoteFile is null
     * @throws InvalidLoginException
     *             if the username is refused by the remote host
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
     * @throws SecurityException
     *             the url is not secured with https (SSL)
     * @throws IOException
     *             for all other IO / Network / System Error
     * @throws IllegalAccessException
     * @throws InstantiationException
     */

    public RemoteFile[] listFiles(String remoteFile,
	    FilenameFilter filenameFilter, FileFilter fileFilter,
	    String callerClassName) throws IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException,
	    InstantiationException, IllegalAccessException

    {
	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile can not be null!");
	}

	if (username == null || authenticationToken == null) {
	    throw new InvalidLoginException(FILE_SESSION_IS_CLOSED);
	}

	String filenameFilterFilename = null;
	String filenameFilterClassname = null;
	String base64SerialFilenameFilter = null;

	if (filenameFilter != null) {
	    filenameFilterClassname = filenameFilter.getClass().getName();
	    base64SerialFilenameFilter = RemoteFileUtil.SerializeBase64FilenameFilter(
		    filenameFilter, filenameFilterClassname);
	    
	    filenameFilterFilename = RemoteFileUtil.uploadFilterIfShortSize(
		    base64SerialFilenameFilter, remoteSession);
	}

	String fileFilterFilename = null;
	String fileFilterClassname = null;
	String base64SerialFileFilter = null;

	if (fileFilter != null) {
	    debug("after fileFilter() :" + fileFilter);
	    fileFilterClassname = fileFilter.getClass().getName();
	    base64SerialFileFilter = RemoteFileUtil.SerializeBase64FileFilter(fileFilter,
		    fileFilterClassname);
	    fileFilterFilename = RemoteFileUtil.uploadFilterIfShortSize(
		    base64SerialFileFilter, remoteSession);
	    debug("after uploadFilter() :" + fileFilterFilename);
	}

	// Launch the Servlet

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.FILE_LIST_FILES_ACTION));
	requestParams.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(new SimpleNameValuePair(Parameter.TOKEN,
		authenticationToken));
	requestParams
		.add(new SimpleNameValuePair(Parameter.FILENAME, remoteFile));

	requestParams.add(new SimpleNameValuePair(
		Parameter.FILENAME_FILTER_CLASSNAME, filenameFilterClassname));
	requestParams.add(new SimpleNameValuePair(
		Parameter.FILENAME_FILTER_FILENAME,
		filenameFilterFilename));
	
	if (RemoteFileUtil.isFilterShortSize(base64SerialFileFilter)) {
	    requestParams.add(new SimpleNameValuePair(
		    Parameter.BASE64_SERIAL_FILENAME_FILTER,
		    base64SerialFilenameFilter));
	}

	requestParams.add(new SimpleNameValuePair(
		Parameter.FILE_FILTER_CLASSNAME, fileFilterClassname));
	requestParams.add(new SimpleNameValuePair(
		Parameter.FILE_FILTER_FILENAME,
		fileFilterFilename));
	
	debug("fileFilterFilename: " + fileFilterFilename);
	
	if (RemoteFileUtil.isFilterShortSize(base64SerialFileFilter)) {
	    requestParams
		    .add(new SimpleNameValuePair(
			    Parameter.BASE64_SERIAL_FILE_FILTER,
			    base64SerialFileFilter));
	}

	httpTransfer.setReceiveInFile(true); // To say we get the result into a
					     // file
	httpTransfer.send(requestParams);

	// If everything is OK, we have in our protocol a response that
	// 1) starts with "OK". 2) Is followed by the authenticaiton token
	// else: response starts with "INVALID_LOGIN_OR_PASSWORD".

	File receiveFile = httpTransfer.getReceiveFile();
	String receive = FrameworkFileUtil.getFirstLineOfFile(receiveFile);

	debug("receiveFile: " + receiveFile);

	// Content is OK
	if (receive.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    if (!DEBUG && !KeepTempFilePolicyParms.KEEP_TEMP_FILE) {
		receiveFile.delete();
	    }
	    throw new InvalidLoginException(FILE_SESSION_IS_CLOSED);
	}

	try {
	    if (receive.equals("null")) {
		return null;
	    } else if (receive.equals("[]")) {
		return new RemoteFile[0];
	    } else {
		List<String> listPathnames = getFilesListFromFile(receiveFile);
		List<RemoteFile> remoteFiles = new ArrayList<RemoteFile>();

		for (String pathname : listPathnames) {
		    // debug("pathname: " + pathname);
		    RemoteFile theRemoteFile = new RemoteFile(
			    this.remoteFile.getRemoteSession(), pathname);
		    remoteFiles.add(theRemoteFile);
		}

		RemoteFile[] remoteFilearray = remoteFiles
			.toArray(new RemoteFile[listPathnames.size()]);
		return remoteFilearray;
	    }
	} catch (Exception e) {
	    throw new IOException(e.getMessage(), e);
	} finally {
	    if (!DEBUG && !KeepTempFilePolicyParms.KEEP_TEMP_FILE) {
		receiveFile.delete();
	    }
	}

    }

    /**
     * Transforms the content of the file in Html lines into a list.
     * 
     * @param file
     *            the file containing the file names
     * 
     * @return the content of the file lines into a list
     * @throws IOException
     *             if any IO / Network / System Error occurs
     */
    private List<String> getFilesListFromFile(File file) throws IOException {

	List<String> listBase = FileUtils.readLines(file, Charset.defaultCharset());
	List<String> files = new Vector<String>();

	for (String theFileStr : listBase) {
	    theFileStr = HtmlConverter.fromHtml(theFileStr);
	    files.add(theFileStr);
	}

	return files;
    }

    /**
     * debug tool
     */
    public static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
