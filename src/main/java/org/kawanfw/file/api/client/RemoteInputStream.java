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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.kawanfw.commons.client.http.HttpTransferUtil;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.DefaultParms;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.api.util.client.ApiInputStreamDownloader;
import org.kawanfw.file.api.util.client.ChunkUtil;
import org.kawanfw.file.api.util.client.ExceptionThrower;
import org.kawanfw.file.api.util.client.FileChunkStore;
import org.kawanfw.file.api.util.client.UniqueFileCreator;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * 
 * A <code>RemoteInputStream</code> obtains input bytes from a remote file.
 * <p>
 * The remote file bytes are read with standards {@code InputStream} read
 * methods and can thus be downloaded into a local file. <br>
 * <br>
 * Large streams are split in chunks that are downloaded in sequence. The
 * default chunk length is 10Mb. You can change the default value with
 * {@link SessionParameters#setDownloadChunkLength(long)} before passing
 * {@code SessionParameters} to {@code RemoteSession} constructor. <br>
 * Note that streams chunking requires all chunks to be downloaded from to the
 * same web server. Thus, file chunking does not support true stateless
 * architecture with multiple identical web servers. If you want to set a full
 * stateless architecture with multiple identical web servers, you must disable
 * file chunking. This is done by setting a 0 download chunk length value using
 * {@link SessionParameters#setDownloadChunkLength(long)}. <br>
 * <br>
 * A recovery mechanism allows - in case of failure - to start again in the same
 * JVM run the download from the last non-downloaded chunk. <br>
 * See User Guide for more information. <br>
 * <br>
 * Note that {@code read} methods throw following subclasses of
 * {@code IOException}:
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
 * // Get an InputStream from the file located on our server and
 * // an OutputSream from our local file
 * try (InputStream in = new RemoteInputStream(remoteSession, pathname);
 * 	OutputStream out = new FileOutputStream(file);) {
 * 
 *     // Download the remote file reading
 *     // the InpuStream and save it to our local file
 *     byte[] buffer = new byte[1024 * 4];
 *     int n = 0;
 *     while ((n = in.read(buffer)) != -1) {
 * 	out.write(buffer, 0, n);
 *     }
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @see org.kawanfw.file.api.client.RemoteFile
 * @see org.kawanfw.file.api.client.RemoteOutputStream
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class RemoteInputStream extends InputStream {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(RemoteInputStream.class);

    /** The file session in use */
    private RemoteSession remoteSession = null;

    /** The remote file's pathname */
    private String pathname = null;

    /** The stream containing the datas downloaded */
    private InputStream in = null;

    /** The remote file length */
    private long remoteFileLength = -1;

    /** The unique file used as container and reference for download */
    private File fileUnique = null;

    /** The counter to use use for file chunks */
    private int cpt = 0;

    /** Total length for a download */
    private long totalLength = 0;

    /** The beginning String, used to analyse the response of the server. */
    private String beginString = "";

    /** If true, continue input stream beginning content analysis */
    private boolean continueInputStreamStartAnalysis = true;

    /**
     * Creates a <code>RemoteInputStream</code> by opening a connection to an
     * actual remote file, the file named by the path name <code>pathname</code>
     * in the remote file system.
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
     *             if remoteSession or pathname is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws IOException
     *             if an I/O error occurs.
     * 
     * @since 1.0
     */

    public RemoteInputStream(RemoteSession remoteSession, String pathname)
	    throws IOException {

	initConstructor(remoteSession, pathname);
    }

    /**
     * Creates a <code>RemoteInputStream</code> by opening a connection to an
     * actual remote file, the file named by the <code>RemoteFile</code> object
     * <code>file</code> in the remote file system.
     * 
     * @param remoteFile
     *            the remote file
     * 
     * @throws IllegalArgumentException
     *             if remoteSession or remoteFile is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws IOException
     *             if an I/O error occurs.
     * 
     * @since 1.0
     */

    public RemoteInputStream(RemoteFile remoteFile) throws IOException {

	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile is null!");
	}

	initConstructor(remoteFile.getRemoteSession(), remoteFile.getPath());
    }

    /**
     * Init done in constructors.
     * 
     * @param remoteSession
     * @param pathname
     * @throws InvalidLoginException
     * @throws IOException
     */
    private void initConstructor(RemoteSession remoteSession, String pathname)
	    throws InvalidLoginException, IOException {

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

	this.remoteSession = remoteSession;
	this.pathname = pathname;

	// Create the unique filename corresponding to username & pathname
	// name Must be done in constructor because close() uses fileUnique
	fileUnique = UniqueFileCreator
		.createUnique(this.remoteSession.getUsername(), pathname);
    }

    /**
     * init is done once inside read()
     * 
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws RemoteException
     * @throws IOException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     */
    private void init()
	    throws UnknownHostException, ConnectException, RemoteException,
	    IOException, InvalidLoginException, FileNotFoundException {

	if (in != null) {
	    throw new IllegalStateException(
		    "init can be called only if in InputStream is null!");
	}

	// Get the remote file length
	this.remoteFileLength = this.length();

	// Read up to chunk length and put it in a buffer
	debug("chunkLength     : "
		+ ChunkUtil.getDownloadChunkLength(this.remoteSession));
	debug("remoteFileLength: " + remoteFileLength);

	cpt++;
	in = downloadAndCreateInputStream(this.remoteSession, pathname,
		remoteFileLength, fileUnique, cpt);
    }

    /**
     * Returns the length of this remote input stream (which is the underlying
     * remote file length)
     * 
     * @return the remote input stream length in bytes
     * 
     * @throws FileNotFoundException
     *             if the remote file is not found on server
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
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
     */
    public long length() throws ConnectException, IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, SocketException,
	    RemoteException, IOException {

	// Do not do it twice
	if (this.remoteFileLength != -1) {
	    return this.remoteFileLength;
	}

	RemoteFile remoteFile = new RemoteFile(remoteSession, pathname);

	boolean remoteFileExists = remoteFile.exists();
	if (!remoteFileExists) {
	    throw new FileNotFoundException(
		    "Remote file does not exists: " + pathname);
	}

	// Read up to chunk length and put it in a buffer
	// Get the remote file length

	this.remoteFileLength = remoteFile.length();

	return remoteFileLength;
    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into an
     * array of bytes. An attempt is made to read as many as <code>len</code>
     * bytes, but a smaller number may be read. The number of bytes actually
     * read is returned as an integer.
     *
     * <p>
     * This method blocks until input data is available, end of file is
     * detected, or an exception is thrown.
     *
     * <p>
     * If <code>len</code> is zero, then no bytes are read and <code>0</code> is
     * returned; otherwise, there is an attempt to read at least one byte. If no
     * byte is available because the stream is at end of file, the value
     * <code>-1</code> is returned; otherwise, at least one byte is read and
     * stored into <code>b</code>.
     *
     * <p>
     * The first byte read is stored into element <code>b[off]</code>, the next
     * one into <code>b[off+1]</code>, and so on. The number of bytes read is,
     * at most, equal to <code>len</code>. Let <i>k</i> be the number of bytes
     * actually read; these bytes will be stored in elements <code>b[off]</code>
     * through <code>b[off+</code><i>k</i><code>-1]</code>, leaving elements
     * <code>b[off+</code><i>k</i><code>]</code> through
     * <code>b[off+len-1]</code> unaffected.
     *
     * <p>
     * In every case, elements <code>b[0]</code> through <code>b[off]</code> and
     * elements <code>b[off+len]</code> through <code>b[b.length-1]</code> are
     * unaffected.
     *
     * <p>
     * The <code>read(b,</code> <code>off,</code> <code>len)</code> method for
     * class <code>InputStream</code> simply calls the method
     * <code>read()</code> repeatedly. If the first such call results in an
     * <code>IOException</code>, that exception is returned from the call to the
     * <code>read(b,</code> <code>off,</code> <code>len)</code> method. If any
     * subsequent call to <code>read()</code> results in a
     * <code>IOException</code>, the exception is caught and treated as if it
     * were end of file; the bytes read up to that point are stored into
     * <code>b</code> and the number of bytes read before the exception occurred
     * is returned. The implementation of this method blocks until the requested
     * amount of input data <code>len</code> has been read, end of file is
     * detected, or an exception is thrown.
     *
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset in array <code>b</code> at which the data is
     *            written.
     * @param len
     *            the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of the
     *         stream has been reached.
     * @exception IOException
     *                If the first byte cannot be read for any reason other than
     *                end of file, or if the input stream has been closed, or if
     *                some other I/O error occurs.
     * @exception NullPointerException
     *                If <code>b</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException
     *                If <code>off</code> is negative, <code>len</code> is
     *                negative, or <code>len</code> is greater than
     *                <code>b.length - off</code>
     * @see java.io.InputStream#read()
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {

	// First read: we create the input stream
	if (in == null) {
	    init();
	}

	int intRead = in.read(b, off, len);

	if (intRead != -1) {

	    if (continueInputStreamStartAnalysis) {
		analyseInputStreamStart(b);
	    }

	    totalLength += intRead;
	    return intRead;
	}

	// So intRead = -1 ==> No more to read on *this* InputStream.
	// Check if must read from a new on or stop it all.

	if (totalLength >= remoteFileLength) {
	    // We are done, all is read from remote server.
	    in.close();
	    return -1;
	} else {
	    // Read next file chunk
	    in.close();
	    cpt++;
	    // debug("cpt " + cpt + " " + new Date());
	    in = downloadAndCreateInputStream(remoteSession, pathname,
		    remoteFileLength, fileUnique, cpt);

	    intRead = in.read(b, off, len);
	    totalLength += intRead;

	    if (continueInputStreamStartAnalysis) {
		analyseInputStreamStart(b);
	    }
	}

	return intRead;
    }

    //
    // This is the only private method using global variables
    //

    /**
     * Analyses the InputStream beginning content to check for Exceptions.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file, or if the input stream has been closed, or if
     *             some other I/O error occurs.
     */
    private void analyseInputStreamStart(byte[] b) throws IOException {

	byte[] bytes = new byte[b.length];
	for (int i = 0; i < b.length; i++) {
	    bytes[i] = b[i];
	}

	String string = new String(bytes);
	beginString += string;

	// debug("beginString: " + beginString);

	if (totalLength > TransferStatus.SEND_FAILED.length()) {

	    // SEND_OK may happen if: 1) Invalid Login 2) FileNotFound
	    if (beginString.startsWith(TransferStatus.SEND_OK)) {
		String content = getContentAsString(in, beginString);

		StringReader stringReader = new StringReader(content);
		BufferedReader bufferedReader = new BufferedReader(
			stringReader);
		bufferedReader.readLine(); // Read The status line

		String receive = bufferedReader.readLine();

		if (receive.length() > 1) {
		    if (receive
			    .startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
			throw new InvalidLoginException(
				Tag.PRODUCT + " File Session is closed.");
		    }

		    if (receive.startsWith(Tag.FileNotFoundException)) {
			throw new FileNotFoundException(
				"Remote file does not exists: " + pathname);
		    }

		    // Should never happen
		    throw new IOException(Tag.PRODUCT_PRODUCT_FAIL
			    + " Invalid received buffer: " + receive);
		}

	    } else if (beginString.startsWith(TransferStatus.SEND_FAILED)) {
		String content = getContentAsString(in, beginString);

		StringReader stringReader = new StringReader(content);
		HttpTransferUtil.throwTheRemoteException(
			new BufferedReader(stringReader));
	    } else {
		continueInputStreamStartAnalysis = false;
	    }
	}
    }

    //
    // All following private methods do *not* use global variables.
    // Keept it clean this way.
    //

    /**
     * Returns the input stream created from the download.
     * 
     * @param remoteSession
     *            the file session ins use
     * @param pathname
     *            the remote file path
     * @param remoteFileLength
     *            ghe remote file length
     * @param fileUnique
     *            the unique file identifier
     * @param cpt
     *            the counter for file chunks
     * 
     * @return the input stream created from the download
     * 
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws RemoteException
     * @throws IOException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     */
    private InputStream downloadAndCreateInputStream(
	    RemoteSession remoteSession, String remoteFile,
	    long remoteFileLength, File fileUnique, int cpt)
	    throws UnknownHostException, ConnectException, RemoteException,
	    IOException, InvalidLoginException, FileNotFoundException {

	long chunkLength = ChunkUtil.getDownloadChunkLength(remoteSession);

	InputStream in = null;
	File fileChunk = null;

	ApiInputStreamDownloader apiInputStreamDownloader = new ApiInputStreamDownloader(
		remoteSession.getUsername(),
		remoteSession.getAuthenticationToken(),
		remoteSession.getHttpTransfer());

	if (remoteFileLength <= chunkLength) {
	    in = apiInputStreamDownloader.downloadOneChunk(fileChunk,
		    remoteFile, chunkLength);
	} else {
	    FileChunkStore fileChunkStore = new FileChunkStore(
		    remoteSession.getUsername(), fileUnique, remoteFile);

	    String remoteFileChunk = remoteFile + "." + cpt + ".kawanfw.chunk";
	    String fileChunkStr = fileUnique.toString() + "." + cpt
		    + ".kawanfw.chunk";

	    ExceptionThrower.throwSocketExceptionIfFlagFileExists();

	    fileChunk = new File(fileChunkStr);

	    // No re-download if file chunk exists and is complete
	    if (fileChunkStore.alreadyDownloaded(fileChunk)) {
		debug("fileChunk exists, no download: " + fileChunk);
		in = new BufferedInputStream(new FileInputStream(fileChunk));
	    } else {
		debug("downloadOneChunk " + remoteFileChunk + " " + fileChunk);

		in = apiInputStreamDownloader.downloadOneChunk(fileChunk,
			remoteFileChunk, chunkLength);
		fileChunkStore.add(fileChunk);
	    }

	    // debug("fileChunk: " + fileChunk.toString());
	    return in;
	}

	return in;
    }

    /**
     * Returns full content as String and thus reads Input Stream until end.
     * 
     * @param in
     *            the Input Stream to read
     * @param beginString
     *            the beginning of input stream as string
     * 
     * @return the full content as String
     * @throws IOException
     */
    private String getContentAsString(InputStream in, String beginString)
	    throws IOException {

	int len;
	int bufferSize = DefaultParms.DEFAULT_WRITE_BUFFER_SIZE;

	// Get all content into a string and throw an analyzed exception
	// Exception
	byte[] buf = new byte[bufferSize];

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	while ((len = this.in.read(buf)) > 0) {
	    baos.write(buf, 0, len);
	}

	String content = beginString + new String(baos.toByteArray());
	baos.close();
	return content;
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
     * Reads some number of bytes from the input stream and stores them into the
     * buffer array <code>b</code>. The number of bytes actually read is
     * returned as an integer. This method blocks until input data is available,
     * end of file is detected, or an exception is thrown.
     *
     * <p>
     * If the length of <code>b</code> is zero, then no bytes are read and
     * <code>0</code> is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at the end
     * of the file, the value <code>-1</code> is returned; otherwise, at least
     * one byte is read and stored into <code>b</code>.
     *
     * <p>
     * The first byte read is stored into element <code>b[0]</code>, the next
     * one into <code>b[1]</code>, and so on. The number of bytes read is, at
     * most, equal to the length of <code>b</code>. Let <i>k</i> be the number
     * of bytes actually read; these bytes will be stored in elements
     * <code>b[0]</code> through <code>b[</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[</code><i>k</i><code>]</code> through
     * <code>b[b.length-1]</code> unaffected.
     *
     * <p>
     * The <code>read(b)</code> method for class <code>InputStream</code> has
     * the same effect as:
     * 
     * <blockquote>
     * 
     * <pre>
     * <code> read(b, 0, b.length) </code>
     * </pre>
     * 
     * </blockquote>
     *
     * @param b
     *            the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> is there is no more data because the end of the
     *         stream has been reached.
     * @exception IOException
     *                If the first byte cannot be read for any reason other than
     *                the end of the file, if the input stream has been closed,
     *                or if some other I/O error occurs.
     * @exception NullPointerException
     *                if <code>b</code> is <code>null</code>.
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b) throws IOException {
	return read(b, 0, b.length);
    }

    /**
     * The method is not implemented in this KRemote Files version and will
     * throw an {@code IOException}.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream is reached.
     * @exception IOException
     *                if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
	throw new IOException(Tag.PRODUCT + " read method is not supported.");
    }

    /**
     * The method is not implemented in this KRemote Files version and will
     * throw an {@code IOException}.
     *
     * @param n
     *            the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @exception IOException
     *                if the stream does not support seek, or if some other I/O
     *                error occurs.
     */
    @Override
    public long skip(long n) throws IOException {
	throw new IOException(Tag.PRODUCT + " skip method is not supported.");
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking by the next invocation of a
     * method for this input stream. The next invocation might be the same
     * thread or another thread. A single read or skip of this many bytes will
     * not block, but may read or skip fewer bytes.
     *
     * @return an estimate of the number of bytes that can be read (or skipped
     *         over) from this input stream without blocking or {@code 0} when
     *         it reaches the end of the input stream.
     * @exception IOException
     *                if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
	return in.available();
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     * @exception IOException
     *                if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {

	// if (in == null) : it has been closed already so escape now
	if (in == null) {
	    return;
	}

	in.close();

	// We immediately set out to null to avoid recall of this method
	in = null;

	debug("totalLength     : " + totalLength);
	debug("remoteFileLength: " + remoteFileLength);

	// Delete temp storage only if all is done
	if (totalLength >= remoteFileLength) {

	    // Delete the temporary files downloaded/created, if any
	    FileChunkStore fileChunkStore = new FileChunkStore(
		    remoteSession.getUsername(), fileUnique, pathname);
	    fileChunkStore.remove();

	    // Delete the file unique downloaded/created, if any
	    FileUtils.deleteQuietly(fileUnique);
	}

    }

    /**
     * Calls {@code InputStream} implementation, so does nothing.
     *
     * @param readlimit
     *            the maximum limit of bytes that can be read before the mark
     *            position becomes invalid.
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void mark(int readlimit) {
	super.mark(readlimit);
    }

    /**
     * The method is not implemented in this KRemote Files version and will
     * throw an {@code IOException}.
     */
    @Override
    public synchronized void reset() throws IOException {
	throw new IOException(Tag.PRODUCT + " reset method is not supported.");
    }

    /**
     * Returns <code>false</code>.
     *
     * @return <code>true</code> if this stream instance supports the mark and
     *         reset methods; <code>false</code> otherwise.
     * @see java.io.InputStream#mark(int)
     * @see java.io.InputStream#reset()
     */
    @Override
    public boolean markSupported() {
	return false;
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
