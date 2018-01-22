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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.api.util.client.ApiOutputStreamUploader;
import org.kawanfw.file.api.util.client.ChunkUtil;
import org.kawanfw.file.api.util.client.ExceptionThrower;
import org.kawanfw.file.api.util.client.RemoteFilePartStore;
import org.kawanfw.file.api.util.client.UniqueFileCreator;

/**
 * 
 * A remote output stream is an output stream for writing data to a remote
 * <code>File</code>. <br>
 * It allows to create a remote file by writing bytes on the
 * {@code RemoteOutputStream} with standards {@code OutputStream} write methods.
 * <br>
 * <br>
 * Large streams are split in chunks that are uploaded in sequence. The default
 * chunk length is 10Mb. You can change the default value with
 * {@link SessionParameters#setUploadChunkLength(long)} before passing
 * {@code SessionParameters} to this {@link RemoteSession} constructor.
 * <p>
 * Note that stream chunking requires all chunks to be sent to the same web
 * server that will aggregate the chunks on the same file. Thus, stream chunking
 * does not support true stateless architecture with multiple identical web
 * servers. If you want to set a full stateless architecture with multiple
 * identical web servers, you must disable file chunking. This is done by
 * setting a 0 upload chunk length value using
 * {@link SessionParameters#setUploadChunkLength(long)}. <br>
 * <br>
 * A recovery mechanism allows - in case of failure - to start again in the same
 * JVM run the data upload from the last non-uploaded chunk. The recovery
 * mechanism is enabled only if the length is known (and so is different from
 * -1) <br>
 * See User Guide for more information. <br>
 * <br>
 * Note that {@code write} and {@code close} methods throw following subclasses
 * of {@code IOException}:
 * <ul>
 * <li>{@code InvalidLoginException} the session has been closed by a
 * {@code RemoteSession.logoff()}.</li>
 * <li>{@code UnknownHostException} if host URL (http://www.acme.org) does not
 * exists or no Internet connection.</li>
 * <li>{@code ConnectException} if the Host is correct but the ServerFileManager
 * Servlet is not reachable (http://www.acme.org/ServerFileManager) and access
 * failed with a status != OK (200). (If the host is incorrect, or is impossible
 * to connect to - Tomcat down - the {@code ConnectException} will be the sub
 * exception {@code HttpHostConnectException}.)</li>
 * <li>{@code SocketException} if network failure during transmission.</li>
 * <li>{@link RemoteException} an exception has been thrown on the server
 * side.</li>
 * </ul>
 * <br>
 * Example: <blockquote>
 * 
 * <pre>
 * // Define URL of the path to the ServerFileManager servlet
 * String url = "https://www.acme.org/ServerFileManager";
 * 
 * // The login info for strong authentication on server side:
 * String username = "myUsername";
 * char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };
 * 
 * // Establish a session with the remote server
 * RemoteSession remoteSession = new RemoteSession(url, username, password);
 * 
 * File file = new File("C:\\Users\\Mike\\Koala.jpg");
 * String pathname = "/Koala.jpg";
 * 
 * // Get an InputStream from our local file and
 * // create an OutputStream that maps a remote file on the host
 * try (InputStream in = new FileInputStream(file);
 * 	OutputStream out = new RemoteOutputStream(remoteSession, pathname,
 * 		file.length());) {
 * 
 *     // Create the remote file reading the InpuStream and writing
 *     // on the OutputStream
 *     byte[] buffer = new byte[1024 * 4];
 *     int n = 0;
 *     while ((n = in.read(buffer)) != -1) {
 * 	out.write(buffer, 0, n);
 *     }
 * 
 *     // It is better to also close out before finally
 *     // because RemoteOutputStream.close() sends data to the server and
 *     // is thus prone to IOException
 *     out.close();
 * 
 * } catch (IOException e) {
 *     // Treat IOException, including those thrown by out.close()
 *     e.printStackTrace();
 *     // Etc.
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see org.kawanfw.file.api.client.RemoteFile
 * @see org.kawanfw.file.api.client.RemoteInputStream
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class RemoteOutputStream extends OutputStream {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(RemoteOutputStream.class);

    /** the file session in use */
    private RemoteSession remoteSession;

    /** The remote file's pathname */
    private String pathname = null;

    /** The stream containing the datas to upload */
    private OutputStream out = null;

    /** The unique file used as container and reference for download */
    private File fileUnique = null;

    /** The counter to use use for file chunks */
    private int cpt = 0;

    /** Total length for one upload */
    private long totalLength = 0;

    /** Temporary length for one chunk */
    private long tempLength = 0;

    /** The remote file length */
    private long remoteFileLength = -1;

    /** Compute the total length of upload files, must match totalLength */
    private long totalFileLength = 0;

    /** Happens id the lase send id exactly the size of a chunk */
    private boolean noSendInclose = false;

    /**
     * Creates an output stream to write to the remote file with the specified
     * pathname.
     * <p>
     * The real path of the remote file depends on the KRemote Files
     * configuration on the server. See User Documentation.
     * 
     * @param remoteSession
     *            the current remote session
     * @param pathname
     *            the pathname on host with "/" as file separator. Must be
     *            absolute.
     * @param length
     *            the final length of the remote file after creation, -1 if
     *            unknown
     * 
     * @throws IllegalArgumentException
     *             if remoteFile is null or length is &lt; -1
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws IOException
     *             if an I/O error occurs.
     *
     * @since 1.0
     */
    public RemoteOutputStream(RemoteSession remoteSession, String pathname,
	    long length) throws IOException {

	initConstructors(remoteSession, pathname, length);
    }
    
    /**
     * Creates an output stream to write to the remote file with the specified
     * pathname.
     * <p>
     * The real path of the remote file depends on the KRemote Files
     * configuration on the server. See User Documentation.
     * 
     * @param remoteSession
     *            the current remote session
     * @param pathname
     *            the pathname on host with "/" as file separator. Must be
     *            absolute.
     * 
     * @throws IllegalArgumentException
     *             if remoteFile is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws IOException
     *             if an I/O error occurs.
     *
     * @since 1.0
     */
    public RemoteOutputStream(RemoteSession remoteSession, String pathname) throws IOException {
	initConstructors(remoteSession, pathname, -1);
    }

    /**
     * Creates an output stream to write to the remote file represented by the
     * specified <code>RemoteFile</code> object.
     * 
     * @param remoteFile
     *            the remote file
     * @param length
     *            the final remote file length <i>after</i> creation, -1 if
     *            unknown
     * 
     * @throws IllegalArgumentException
     *             if pathname is null or length is &lt; -1
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws IOException
     *             if an I/O error occurs.
     * 
     * @since 1.0
     */
    public RemoteOutputStream(RemoteFile remoteFile, long length)
	    throws IOException {

	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile is null!");
	}

	initConstructors(remoteFile.getRemoteSession(), remoteFile.getPath(),
		length);
    }

    /**
     * Creates an output stream to write to the remote file represented by the
     * specified <code>RemoteFile</code> object.
     * 
     * @param remoteFile
     *            the remote file
     * 
     * @throws IllegalArgumentException
     *             if pathname is null or length is &lt; -1
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws IOException
     *             if an I/O error occurs.
     * 
     * @since 1.0
     */
    public RemoteOutputStream(RemoteFile remoteFile)
	    throws IOException {

	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile is null!");
	}

	initConstructors(remoteFile.getRemoteSession(), remoteFile.getPath(),
		-1);
    }
    
    /**
     * Init done in constructors.
     * 
     * @param remoteSession
     * @param pathname
     * @param length
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void initConstructors(RemoteSession remoteSession, String pathname,
	    long length) throws IllegalArgumentException, InvalidLoginException,
    IOException, FileNotFoundException {
	if (remoteSession == null) {
	    throw new IllegalArgumentException("remoteSession is null!");
	}

	if (remoteSession.getUsername() == null
		|| remoteSession.getAuthenticationToken() == null) {
	    throw new InvalidLoginException(
		    RemoteSession.REMOTE_SESSION_IS_CLOSED);
	}

	if (pathname == null) {
	    throw new IllegalArgumentException("pathname is null!");
	}

	if (!pathname.startsWith("/")) {
	    throw new IllegalArgumentException(
		    "pathname must be asbsolute and start with \"/\": "
			    + pathname);
	}

	if (length < -1) {
	    throw new IllegalArgumentException("length must be > -1.");
	}

	this.remoteSession = remoteSession;
	this.pathname = pathname;
	this.remoteFileLength = length;

	// Create the unique filename corresponding to username & pathname name
	// Must be done in constructor because close() uses fileUnique
	fileUnique = UniqueFileCreator.createUnique(remoteSession.getUsername(),
		this.pathname);
	out = new BufferedOutputStream(new FileOutputStream(fileUnique));
    }

    /**
     * Returns the remote file's pathname
     * 
     * @return the remote file's pathname
     */
    public String getPathname() {
	return pathname;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this output stream. The general contract for
     * <code>write(b, off, len)</code> is that some of the bytes in the array
     * <code>b</code> are written to the output stream in order; element
     * <code>b[off]</code> is the first byte written and
     * <code>b[off+len-1]</code> is the last byte written by this operation.
     * <p>
     * The <code>write</code> method of <code>OutputStream</code> calls the
     * write method of one argument on each of the bytes to be written out.
     * <p>
     * If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     * <p>
     * If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param b
     *            the data.
     * @param off
     *            the start offset in the data.
     * @param len
     *            the number of bytes to write.
     * @exception IOException
     *                if an I/O error occurs. In particular, an
     *                <code>IOException</code> is thrown if the output stream is
     *                closed.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
	out.write(b, off, len);

	totalLength += len;
	tempLength += len;

	// if length = exaclty only chunk==> mark it for the close
	if (tempLength == ChunkUtil.getUploadChunkLength(remoteSession)) {
	    noSendInclose = true;
	} else {
	    noSendInclose = false;
	}

	// if length >= chunkLength ==> send the chunk
	if (tempLength >= ChunkUtil.getUploadChunkLength(remoteSession)) {
	    out.close();
	    cpt++;
	    uploadPerChunks(remoteSession, fileUnique, pathname, cpt, false);

	    tempLength = 0;
	    fileUnique = UniqueFileCreator
		    .createUnique(remoteSession.getUsername(), pathname);
	    out = new BufferedOutputStream(new FileOutputStream(fileUnique));

	}
    }

    /**
     * Writes the specified byte to this output stream. The general contract for
     * <code>write</code> is that one byte is written to the output stream. The
     * byte to be written is the eight low-order bits of the argument
     * <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
     *
     * @param b
     *            the <code>byte</code>.
     * @exception IOException
     *                if an I/O error occurs. In particular, an
     *                <code>IOException</code> may be thrown if the output
     *                stream has been closed.
     */
    @Override
    public void write(int b) throws IOException {
	// out.write(b);
	throw new IOException(
		Tag.PRODUCT + " write(int b) method is not supported.");
    }

    /**
     * Writes <code>b.length</code> bytes from the specified byte array to this
     * output stream. The general contract for <code>write(b)</code> is that it
     * should have exactly the same effect as the call
     * <code>write(b, 0, b.length)</code>.
     *
     * @param b
     *            the data.
     * @exception IOException
     *                if an I/O error occurs.
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b) throws IOException {
	write(b, 0, b.length);
    }

    /**
     * Flushes this stream by writing any buffered output to the underlying
     * stream.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void flush() throws IOException {
	out.flush();
    }

    /**
     * Closes this output stream and releases any system resources associated
     * with this stream. The general contract of <code>close</code> is that it
     * closes the output stream. A closed stream cannot perform output
     * operations and cannot be reopened.
     * <p>
     * WARNING: before the output stream close, the remaining bytes are sent to
     * server.
     * <p>
     *
     * @exception IOException
     *                if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {

	// if (out == null) : it has been closed already so escape now
	// We don't want to resend datas...
	if (out == null) {
	    return;
	}

	out.close();

	// We immediately set out to null to avoid recall of this method
	out = null;

	try {
	    // If close is due to Exception throw, total written length will be
	    // < remote file length, so do nothing
	    if (remoteFileLength != -1 && totalLength < remoteFileLength) {
		return;
	    }

	    if (noSendInclose) {
		// Last send is exactly chunk length. We have nothing to do:
		return;
	    }

	    // We must upload the end of our output stream before getting out

	    if (totalLength <= ChunkUtil.getUploadChunkLength(remoteSession)) {
		ApiOutputStreamUploader apiOutputStreamUploader = new ApiOutputStreamUploader(
			remoteSession.getUsername(),
			remoteSession.getAuthenticationToken(),
			remoteSession.getHttpTransfer());

		apiOutputStreamUploader.uploadOneChunk(fileUnique, pathname,
			ChunkUtil.getUploadChunkLength(remoteSession));
	    } else {
		sendLastChunk();
	    }
	} finally {
	    FileUtils.deleteQuietly(fileUnique);
	}

    }

    /**
     * As is says.
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws RemoteException
     */
    private void sendLastChunk()
	    throws FileNotFoundException, IOException, IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, ConnectException,
	    SocketException, RemoteException {
	cpt++;
	uploadPerChunks(remoteSession, fileUnique, pathname, cpt, true);

	// 1) Always remove if we don't know the remote file length
	// 2) If remote file length is known, remove only at end of
	// Successful transfer
	if (remoteFileLength == -1 || (remoteFileLength != -1
		&& totalLength >= remoteFileLength)) {
	    RemoteFilePartStore remoteFilePartStore = new RemoteFilePartStore(
		    remoteSession.getUsername(), fileUnique, pathname);
	    remoteFilePartStore.remove();
	}
    }

    /**
     * Upload the file per chunk
     * 
     * @param file
     *            the file to upload
     * @param pathname
     *            the file name on the host
     * @param cpt
     *            the counter for file chunks
     * @param isLastChunk
     *            says if this is the lastchunk
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws RemoteException
     */
    private void uploadPerChunks(RemoteSession remoteSession, File file,
	    String remoteFile, int cpt, boolean isLastChunk)
		    throws FileNotFoundException, IOException, IllegalArgumentException,
		    InvalidLoginException, UnknownHostException, ConnectException,
		    SocketException, RemoteException {

	// Upload files in chunk, creating temporary file with default size 10Mb
	RemoteFilePartStore remoteFilePartStore = new RemoteFilePartStore(
		remoteSession.getUsername(), file, remoteFile);

	String remoteFilePart = remoteFile + "." + cpt + ".kawanfw.chunk";

	if (isLastChunk) {
	    remoteFilePart += ".LASTCHUNK";
	}

	ExceptionThrower.throwSocketExceptionIfFlagFileExists();

	// Do the upload only if it has not been done
	if (!remoteFilePartStore.alreadyUploaded(remoteFilePart)) {
	    debug(new Date() + " Uploading " + remoteFilePart + "...");

	    ApiOutputStreamUploader apiOutputStreamUploader = new ApiOutputStreamUploader(
		    remoteSession.getUsername(),
		    remoteSession.getAuthenticationToken(),
		    remoteSession.getHttpTransfer());

	    ExceptionThrower.throwSocketExceptionIfFlagFileExists();

	    apiOutputStreamUploader.uploadOneChunk(file, remoteFilePart,
		    ChunkUtil.getUploadChunkLength(remoteSession));

	    remoteFilePartStore.storeFilePart(remoteFilePart);

	    totalFileLength += file.length();

	    debug("");
	    debug("totalLength / totalFileLength : " + totalLength + " / "
		    + totalFileLength + " UPLOADED!");
	    FileUtils.deleteQuietly(file);

	} else {
	    debug(new Date() + " No Uploading of " + remoteFilePart
		    + ". Already done!");
	}

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
