/**
 * 
 */
package org.kawanfw.file.test.api.client.nio;

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

import org.apache.commons.io.IOUtils;
import org.kawanfw.file.api.client.RemoteInputStream;
import org.kawanfw.file.api.client.RemoteOutputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.SessionParameters;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class FileTransfer {

    /**
     * 
     */
    public FileTransfer() {
	// TODO Auto-generated constructor stub
    }
    
    /**
     * Downloads a file from the remote server. <br>
     * This method simply wraps bytes copy from a {@link RemoteInputStream} to a
     * {@code FileOutputStream}.
     * <p>
     * The real path of the remote file depends on the KRemote Files configuration
     * on the server. See User Documentation.
     * <p>
     * Large files are split in chunks that are downloaded in sequence. The
     * default chunk length is 10Mb. You can change the default value with
     * {@link SessionParameters#setDownloadChunkLength(long)} before passing
     * {@code SessionParameters} to this class constructor.
     * <p>
     * Note that file chunking requires that all chunks be downloaded from to
     * the same web server. Thus, file chunking does not support true stateless
     * architecture with multiple identical web servers. If you want to set a
     * full stateless architecture with multiple identical web servers, you must
     * disable file chunking. This is done by setting a 0 download chunk length
     * value using {@link SessionParameters#setDownloadChunkLength(long)}. <br>
     * <br>
     * A recovery mechanism allows - in case of failure - to start again in the
     * same JVM run the file download from the last non-downloaded chunk. See
     * User Guide for more information. <br>
     * <br>
     * Note that this method can not be used with a progress indicator/monitor
     * and so does not implement any increment mechanism. The reason is dual:
     * <ul>
     * <li>Implementing an increment mechanism would require to add cumbersome
     * API.</li>
     * <li>Wrapped classes {@link RemoteInputStream} and
     * {@code FileOutputStream} allow easy implementation of progress
     * indicators. See Tutorial and included examples.</li>
     * </ul>
     * 
     * @param pathname
     *            the pathname on host with "/" as file separator. Must be
     *            absolute.
     * @param file
     *            the file to create on the client side
     * @throws IllegalArgumentException
     *             if file or pathname is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws FileNotFoundException
     *             if the remote file is not found on server
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
     */
    public static void download(RemoteSession remoteSession, String pathname, File file)
	    throws IllegalArgumentException, InvalidLoginException,
	    FileNotFoundException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException {

	if (pathname == null) {
	    throw new IllegalArgumentException("pathname can not be null!");
	}

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

//	if (getUsername() == null || getAuthenticationToken() == null) {
//	    throw new InvalidLoginException(
//		    RemoteSession.REMOTE_SESSION_IS_CLOSED);
//	}

	//InputStream in = null;
	//OutputStream out = null;

	// (IOUtils is a general IO stream manipulation utilities
	// provided by Apache Commons IO)

	try ( InputStream in = new RemoteInputStream(remoteSession, pathname);
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));){
	   
	    IOUtils.copy(in, out);
	    // Cleaner to close in here so that no Exception is thrown in
	    // finally clause
	    in.close();
	} finally {
	    //IOUtils.closeQuietly(in);
	    //IOUtils.closeQuietly(out);
	}
    }
    
    /**
     * Uploads a file on the server. <br>
     * This method simply wraps bytes copy from a {@code FileInputStream} to a
     * {@link RemoteOutputStream}.
     * <p>
     * The real path of the remote file depends on the KRemote Files configuration
     * on the server. See User Documentation.
     * <p>
     * Large files are split in chunks that are uploaded in sequence. The
     * default chunk length is 10Mb. You can change the default value with
     * {@link SessionParameters#setUploadChunkLength(long)} before passing
     * {@code SessionParameters} to this class constructor.
     * <p>
     * Note that file chunking requires all chunks to be sent to the same web
     * server that will aggregate the chunks after the last send. Thus, file
     * chunking does not support true stateless architecture with multiple
     * identical web servers. If you want to set a full stateless architecture
     * with multiple identical web servers, you must disable file chunking. This
     * is done by setting a 0 upload chunk length value using
     * {@link SessionParameters#setUploadChunkLength(long)}. <br>
     * <br>
     * A recovery mechanism allows - in case of failure - to start again in the
     * same JVM run the file upload from the last non-uploaded chunk. See User
     * Guide for more information. <br>
     * <br>
     * Note that this method can not be used with a progress indicator/monitor
     * and so does not implement any increment mechanism. The reason is dual:
     * <ul>
     * <li>Implementing an increment mechanism would require to add cumbersome
     * API.</li>
     * <li>Wrapped classes {@code FileInputStream} and
     * {@link RemoteOutputStream} allow easy implementation of progress
     * indicators. See Tutorial and included examples.</li>
     * </ul>
     * 
     * @param file
     *            the file to upload
     * @param pathname
     *            the pathname on host with "/" as file separator. Must be
     *            absolute.
     * @throws IllegalArgumentException
     *             if file or pathname is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * @throws FileNotFoundException
     *             if the file to upload is not found
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
    public static void upload(RemoteSession remoteSession, File file, String pathname)
	    throws IllegalArgumentException, InvalidLoginException,
	    FileNotFoundException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException {

	if (pathname == null) {
	    throw new IllegalArgumentException("pathname can not be null!");
	}

	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (!file.exists()) {
	    throw new FileNotFoundException("File does not exists: " + file);
	}


	//InputStream in = null;
	//OutputStream out = null;

	// (IOUtils is a general IO stream manipulation utilities
	// provided by Apache Commons IO)

	try (InputStream in = new BufferedInputStream(
		new FileInputStream(file));
		OutputStream out = new RemoteOutputStream(remoteSession,
			pathname, file.length());) {

	    IOUtils.copy(in, out);
	    // Cleaner to close out here so that no Exception is thrown in
	    // finally clause
	    out.close();
	} finally {
	    // IOUtils.closeQuietly(in);
	    // IOUtils.closeQuietly(out);
	}
    }

}
