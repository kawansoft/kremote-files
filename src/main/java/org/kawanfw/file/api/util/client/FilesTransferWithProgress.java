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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.client.RemoteInputStream;
import org.kawanfw.file.api.client.RemoteOutputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;

/**
 * Wrapper for files transfer with progress indicator management.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class FilesTransferWithProgress {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(FilesTransferWithProgress.class);

    /** The current RemoteSession instance used for file transfers */
    private RemoteSession remoteSession = null;

    // progress & cancelled are Shareable variable between threads

    /** Progress value between 0 and 100. Will be used by progress indicators. */
    private AtomicInteger progress = new AtomicInteger();

    /** Says if user has cancelled the file pload or download */
    private AtomicBoolean cancelled = new AtomicBoolean();

    /** Use global var for StreamsTransferWithProgress in order to extract the current uploaded/download file */
    private StreamsTransferWithProgress streamsTransferWithProgressDownload;

    private StreamsTransferWithProgress streamsTransferWithProgressUpload;

    /**
     * Constructor
     * 
     * @param remoteSession
     *            The current RemoteSession instance used for file transfers
     * @param progress
     *            Progress value between 0 and 100. Will be used by progress
     *            indicators.
     * @param cancelled
     *            Says if user has cancelled the file pload or download
     */
    public FilesTransferWithProgress(RemoteSession remoteSession,
	    AtomicInteger progress, AtomicBoolean cancelled) {
	this.remoteSession = remoteSession;
	this.progress = progress;
	this.cancelled = cancelled;
    }

    /**
     * Returns the current remote file in download
     * @return the current remote file in download
     */
    public String getCurrentPathnameDownload() {
	if (streamsTransferWithProgressDownload != null) {
	    return streamsTransferWithProgressDownload.getCurrentPathnameDownload();
	}
	else  {
	    return null;
	}
    }

    /**
     * Returns the current remote file in upload
     * @return the current remote file in upload
     */
    public String getCurrentPathnameUpload() {
	if (streamsTransferWithProgressUpload != null) {
	    return streamsTransferWithProgressUpload.getCurrentPathnameUpload();
	}
	else  {
	    return null;
	}
    }

    /**
     * Downloads the list of remote files. <br>
     * Will set the progress indicators shareable variable
     * {@link ConnectionHttp#getProgress()}. <br>
     * Will also test the value of {@link ConnectionHttp#getCancelled())} to
     * throw an {@code InterruptedException} if necessary. <br>
     * 
     * @param remoteFiles
     *            the corresponding server file names
     * @param files
     *            the files to upload
     * @param totalLength
     *            the total lenth of files.
     * 
     * 
     * @throws IOException
     * @throws RemoteException
     * @throws SocketException
     * @throws UnknownHostException
     * @throws InvalidLoginException
     * @throws IllegalArgumentException
     * @throws ConnectException
     */
    public void download(List<String> remoteFiles, List<File> files,
	    long totalLength) throws ConnectException,
	    IllegalArgumentException, InvalidLoginException,
	    UnknownHostException, SocketException, RemoteException,
	    IOException, InterruptedException {

	if (remoteFiles == null) {
	    throw new IllegalArgumentException("remoteFiles can not be null!");
	}

	if (files == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	List<InputStream> inStreams = new ArrayList<InputStream>();

	try {
	    for (int i = 0; i < remoteFiles.size(); i++) {
		InputStream in = new RemoteInputStream(remoteSession, remoteFiles
			.get(i));
		inStreams.add(in);
	    }

	    streamsTransferWithProgressDownload = new StreamsTransferWithProgress(
		    progress, cancelled);
	    streamsTransferWithProgressDownload.download(inStreams, files, totalLength);
	} finally {

	    // Clean all InputStream in case of Exception inside download
	    if (inStreams != null) {
		for (InputStream in : inStreams) {
		    IOUtils.closeQuietly(in);
		}
	    }
	}
    }

    /**
     * 
     * Uploads a list of streams on the remote server. If a stream is closed, we bypass it
     * 
     * @param inStreams
     *            the inut stream to upload
     * @param inStreamlengths
     * 			the lengt hof each input stream
     * @param remoteFiles
     *            the corresponding server file names
     * @param totalLength
     *            the total lenth of files.
     * 
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws RemoteException
     * @throws IOException
     * @throws InterruptedException
     */
    public void upload(List<InputStream> inStreams, List<Long> inStreamlengths, List<String> remoteFiles,
	    long totalLength) throws IllegalArgumentException,
	    InvalidLoginException, FileNotFoundException, UnknownHostException,
	    ConnectException, SocketException, RemoteException, IOException,
	    InterruptedException {
	
	if (inStreams == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	if (remoteFiles == null) {
	    throw new IllegalArgumentException("remoteFiles can not be null!");
	}

	// Inside InputStream in inStreams may be null, so no test
	
	List<OutputStream> outStreams = new ArrayList<OutputStream>();

	try {
	    for (int i = 0; i < remoteFiles.size(); i++) {
		OutputStream out = new RemoteOutputStream(remoteSession, 
			remoteFiles.get(i), inStreamlengths.get(i));
		outStreams.add(out);
	    }

	    streamsTransferWithProgressUpload = new StreamsTransferWithProgress(
		    progress, cancelled);
	    streamsTransferWithProgressUpload.upload(inStreams, inStreamlengths, outStreams, totalLength);
	} finally {

	    // Clean all InputStream in case of Exception inside upload
	    if (outStreams != null) {
		for (OutputStream out : outStreams) {
		    IOUtils.closeQuietly(out);
		}
	    }
	}
    }
    
    
    /**
     * 
     * Uploads a list of files on the remote server.
     * 
     * @param files
     *            the files to upload
     * @param remoteFiles
     *            the corresponding server file names
     * @param totalLength
     *            the total lenth of files.
     * 
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws RemoteException
     * @throws IOException
     * @throws InterruptedException
     */
    public void upload(List<File> files, List<String> remoteFiles,
	    long totalLength) throws IllegalArgumentException,
	    InvalidLoginException, FileNotFoundException, UnknownHostException,
	    ConnectException, SocketException, RemoteException, IOException,
	    InterruptedException {

	if (files == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	if (remoteFiles == null) {
	    throw new IllegalArgumentException("remoteFiles can not be null!");
	}

	for (File file : files) {

	    if (file == null) {
		throw new IllegalArgumentException("file can not be null!");
	    }

	    if (!file.exists()) {
		throw new FileNotFoundException("File does not exists: " + file);
	    }
	}

	List<OutputStream> outStreams = new ArrayList<OutputStream>();

	try {
	    for (int i = 0; i < remoteFiles.size(); i++) {
		OutputStream out = new RemoteOutputStream(remoteSession, 
			remoteFiles.get(i), files.get(i).length());
		outStreams.add(out);
	    }

	    streamsTransferWithProgressUpload = new StreamsTransferWithProgress(
		    progress, cancelled);
	    streamsTransferWithProgressUpload.upload(files, outStreams, totalLength);
	} finally {

	    // Clean all InputStream in case of Exception inside upload
	    if (outStreams != null) {
		for (OutputStream out : outStreams) {
		    IOUtils.closeQuietly(out);
		}
	    }
	}
    }

    /**
     * 
     * Uploads a file on the remote server.s
     * 
     * @param file
     *            the files to upload
     * @param remoteFile
     *            the corresponding server file names
     * @param length
     *            the total lenth of files.s
     * 
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws RemoteException
     * @throws IOException
     * @throws InterruptedException
     */
    public void upload(File file, String remoteFile, long length)
	    throws IllegalArgumentException, InvalidLoginException,
	    FileNotFoundException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException, InterruptedException {

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (!file.exists()) {
	    throw new FileNotFoundException("file does not exists: " + file);
	}

	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFiles can not be null!");
	}

	List<File> files = new ArrayList<File>();
	List<String> remoteFiles = new ArrayList<String>();

	files.add(file);
	remoteFiles.add(remoteFile);

	this.upload(files, remoteFiles, length);

    }

    /**
     * Downloads the remote file. <br>
     * Will set the progress indicators shareable variable
     * {@link ConnectionHttp#getProgress()}. <br>
     * Will also test the value of {@link ConnectionHttp#getCancelled())} to
     * throw an {@code InterruptedException} if necessary. <br>
     * 
     * @param remoteFile
     *            the file name on the host
     * @param file
     *            the file to create
     * @param remoteFileLength
     *            the length of the remote file
     * 
     * @throws IOException
     * @throws RemoteException
     * @throws SocketException
     * @throws UnknownHostException
     * @throws InvalidLoginException
     * @throws IllegalArgumentException
     * @throws ConnectException
     */
    public void download(String remoteFile, File file, long remoteFileLength)
	    throws ConnectException, IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, SocketException,
	    RemoteException, IOException, InterruptedException {

	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFiles can not be null!");
	}

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	List<File> files = new ArrayList<File>();
	List<String> remoteFiles = new ArrayList<String>();

	files.add(file);
	remoteFiles.add(remoteFile);

	this.download(remoteFiles, files, remoteFileLength);
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
