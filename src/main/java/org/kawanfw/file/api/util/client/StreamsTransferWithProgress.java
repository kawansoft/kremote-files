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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;

/**
 * Wrapper for streams transfer with progress indicator management.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class StreamsTransferWithProgress {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(StreamsTransferWithProgress.class);

    // progress & cancelled are Shareable variable between threads

    /** Progress value between 0 and 100. Will be used by progress indicators. */
    private AtomicInteger progress = new AtomicInteger();

    /** Says if user has cancelled the file pload or download */
    private AtomicBoolean cancelled = new AtomicBoolean();

    /** The current downloaded file name */
    private String currentPathnameDownload = null;

    /** The current downloaded file name */
    private String currentPathnameUpload = null;
    

    /**
     * Constructor
     * 
     * @param progress
     *            Progress value between 0 and 100. Will be used by progress
     *            indicators.
     * @param cancelled
     *            Says if user has cancelled the file pload or download
     */
    public StreamsTransferWithProgress(AtomicInteger progress,
	    AtomicBoolean cancelled) {
	this.progress = progress;
	this.cancelled = cancelled;
    }

    /**
     * @return the current remote file in download
     */
    public String getCurrentPathnameDownload() {
	return currentPathnameDownload;
    }

    
    /**
     * @return the current remote file in upload
     */
    String getCurrentPathnameUpload() {
        return currentPathnameUpload;
    }

    /**
     * Downloads the list of remote files using {@code RemoteInputStream} <br>
     * Will set the progress indicators shareable variable
     * {@link ConnectionHttp#getProgress()}. <br>
     * Will also test the value of {@link ConnectionHttp#getCancelled())} to
     * throw an {@code InterruptedException} if necessary. <br>
     * <br>
     * <br>
     * Warning: streams are not closed and must be closed by caller
     * 
     * @param inStreams
     *            the RemoteInputStream list to read for download
     * @param files
     *            the files to create
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

    public void download(List<InputStream> inStreams, List<File> files,
	    long totalLength) throws ConnectException,
	    IllegalArgumentException, InvalidLoginException,
	    UnknownHostException, SocketException, RemoteException,
	    IOException, InterruptedException {

	if (inStreams == null) {
	    throw new IllegalArgumentException("inStream can not be null!");
	}

	if (files == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	InputStream in = null;
	OutputStream out = null;

	for (int i = 0; i < inStreams.size(); i++) {
	    try {

		if (inStreams.get(i) == null) {
		    continue;
		}
		
		String pathname = ((RemoteInputStream) inStreams.get(i))
			.getPathname();

		debug("Downloading remoteFile with RemoteInpuStream: "
			+ pathname + " progress: " + progress.get());
		currentPathnameDownload = pathname;

		in = inStreams.get(i);
		out = new BufferedOutputStream(new FileOutputStream(
			files.get(i)));

		int tempLen = 0;
		byte[] buffer = new byte[1024 * 4];
		int n = 0;

		while ((n = in.read(buffer)) != -1) {
		    tempLen += n;

		    if (totalLength > 0 && tempLen > totalLength / 100) {
			tempLen = 0;
			int cpt = progress.get();
			cpt++;

			// Update the progress value for progress
			// indicator
			progress.set(Math.min(99, cpt));
		    }

		    // If progress indicator says that user has cancelled the
		    // download, stop now!
		    if (cancelled.get()) {
			throw new InterruptedException(
				"File download cancelled by user.");
		    }

		    out.write(buffer, 0, n);
		}

	    } finally {
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);
		inStreams.set(i, null);
	    }
	}
    }

    /**
     * 
     * Uploads a list of input stream on the remote server using
     * {@code RemoteOutputStream}.
     * 
     * @param inStreams
     *            the input stream to upload
     * @param inStreamsLength the lengths of the input streams
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
    public void upload(List<InputStream> inStreams, List<Long> inStreamsLength, List<OutputStream> outStreams,
	    long totalLength) throws IllegalArgumentException,
	    InvalidLoginException, FileNotFoundException, UnknownHostException,
	    ConnectException, SocketException, RemoteException, IOException,
	    InterruptedException {

	if (inStreams == null) {
	    throw new IllegalArgumentException("instreams can not be null!");
	}

	if (outStreams == null) {
	    throw new IllegalArgumentException("outStreams can not be null!");
	}

	// Inside InputStream in inStreams may be null, so no test

	InputStream in = null;
	OutputStream out = null;

	for (int i = 0; i < inStreams.size(); i++) {
	    
	    // Do not reupload an uploaded stream that is closed!
	    if (inStreams.get(i) == null) {
		continue;
	    }
		
	    try {

		String remoteFile = ((RemoteOutputStream) outStreams.get(i))
			.getPathname();
		debug("Uploading stream with RemoteInputStream: "
			+ remoteFile + " progress: " + progress.get());
		currentPathnameUpload = remoteFile;

		in = inStreams.get(i);
		out = outStreams.get(i);

		int tempLen = 0;
		byte[] buffer = new byte[1024 * 4];
		int n = 0;

		while ((n = in.read(buffer)) != -1) {
		    tempLen += n;

		    if (totalLength > 0 && tempLen > totalLength / 100) {
			tempLen = 0;
			int cpt = progress.get();
			cpt++;

			// Update the progress value for progress
			// indicator
			progress.set(Math.min(99, cpt));
		    }

		    // If progress indicatos says that user has cancelled the
		    // download, stop now!
		    if (cancelled.get()) {
			throw new InterruptedException(
				"File upload cancelled by user.");
		    }

		    out.write(buffer, 0, n);
		}

	    } finally {
		inStreams.set(i, null);
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);
	    }
	}
    }
    
    /**
     * 
     * Uploads a list of files on the remote server using
     * {@code RemoteOutputStream}.
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
    public void upload(List<File> files, List<OutputStream> outStreams,
	    long totalLength) throws IllegalArgumentException,
	    InvalidLoginException, FileNotFoundException, UnknownHostException,
	    ConnectException, SocketException, RemoteException, IOException,
	    InterruptedException {

	if (files == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	if (outStreams == null) {
	    throw new IllegalArgumentException("outStreams can not be null!");
	}

	List<InputStream> inStreams = new ArrayList<InputStream>();
	List<Long> inStreamsLength = new ArrayList<Long>();
	
	for (File file : files) {

	    if (file == null) {
		throw new IllegalArgumentException("file can not be null!");
	    }

	    if (!file.exists()) {
		throw new FileNotFoundException("File does not exists: " + file);
	    }
	    
	    InputStream in = new BufferedInputStream(new FileInputStream(file));
	    inStreams.add(in);
	}
	
	this.upload(inStreams, inStreamsLength, outStreams, totalLength);

    }

    /**
     * 
     * Uploads a file on the remote server.s
     * 
     * @param file
     *            the files to upload
     * @param remoteOutputStream
     *            the remote output stream
     * @param length
     *            the total lenth of files.
     * @param remoteOutputStream
     *            the remote output stream to use
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
    public void upload(File file, RemoteOutputStream remoteOutputStream,
	    long length) throws IllegalArgumentException,
	    InvalidLoginException, FileNotFoundException, UnknownHostException,
	    ConnectException, SocketException, RemoteException, IOException,
	    InterruptedException {

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (!file.exists()) {
	    throw new FileNotFoundException("File does not exists: " + file);
	}

	if (remoteOutputStream == null) {
	    throw new IllegalArgumentException(
		    "remoteOutputStream can not be null!");
	}

	List<File> files = new ArrayList<File>();
	List<OutputStream> remoteOutputStreams = new ArrayList<OutputStream>();

	files.add(file);
	remoteOutputStreams.add(remoteOutputStream);

	this.upload(files, remoteOutputStreams, length);

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
