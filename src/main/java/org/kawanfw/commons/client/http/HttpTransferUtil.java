/**
 * 
 */
package org.kawanfw.commons.client.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.FrameworkFileUtil;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.client.SessionParameters;
import org.kawanfw.file.api.client.exception.RemoteException;

/**
 * @author Nicolas de Pomereu
 * 
 *         Static methods fo HttpTransfe instances
 */
public class HttpTransferUtil {

    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug.isSet(HttpTransferUtil.class);

    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator");

    /**
     * Not instanciable
     */
    protected HttpTransferUtil() {

    }

    /**
     * Factory to create an HttpTransfer instance
     * 
     * @param url
     *            the URL path to the SQL Manager Servlet
     * @param proxy
     *            the proxy to use, may be null for direct access
     * @param passwordAuthentication
     *            the proxy credentials, null if no proxy or if the proxy does not require
     *            authentication
     * @param sessionParameters
     *            the http protocol supplementary parameters (may be null for
     *            default settings)
     * @return the HttpTransfer instance
     */
    public static HttpTransfer HttpTransferFactory(String url, Proxy proxy,
	    PasswordAuthentication passwordAuthentication,
	    SessionParameters sessionParameters) {

	HttpTransfer httpTransfer = new HttpTransferOne(url, proxy,
		passwordAuthentication, sessionParameters);
	debug("HttpTransfer using: " + httpTransfer.getClass().getSimpleName());
	DEBUG = false;
	return httpTransfer;
    }

    /**
     * Factory to create an HttpTransfer instance.
     * 
     * @param proxy
     *            the proxy to use, may be null for direct access
     * @param passwordAuthentication
     *            the proxy credentials, null if no proxy or if the proxy does not require authentication
     * @param sessionParameters
     *            the http protocol supplementary parameters (may be null for
     *            default settings)
     * @return the HttpTransfer instance
     */
    public static HttpTransfer HttpTransferFactory(Proxy proxy,
	    PasswordAuthentication passwordAuthentication,
	    SessionParameters sessionParameters) {

	return new HttpTransferOne(proxy,
		passwordAuthentication, sessionParameters);
    }

    /**
     * Create our own Kawansoft temp file
     * 
     * @return the tempfile to create
     */
    public static synchronized File createKawansoftTempFile() {
	String unique = FrameworkFileUtil.getUniqueId();
	String tempDir = FrameworkFileUtil.getKawansoftTempDir();
	String tempFile = tempDir + File.separator + "http-transfer-one-"
		+ unique + ".kawanfw.txt";

	return new File(tempFile);
    }

    /**
     * 
     * Throws an Exception
     * 
     * @param bufferedReader
     *            the reader that contains the remote thrown exception
     * 
     * @throws IOException
     * @throws RemoteException
     * @throws SecurityException
     */
    public static void throwTheRemoteException(BufferedReader bufferedReader)
	    throws RemoteException, IOException {

	String exceptionName = bufferedReader.readLine();

	if (exceptionName.equals("null")) {
	    exceptionName = null;
	}

	if (exceptionName == null) {
	    throw new IOException(
		    Tag.PRODUCT_PRODUCT_FAIL
			    + "Remote Exception type/name not found in servlet output stream");
	}

	String message = bufferedReader.readLine();

	if (message.equals("null")) {
	    message = null;
	}

	StringBuffer sb = new StringBuffer();

	String line = null;
	while ((line = bufferedReader.readLine()) != null) {
	    // All subsequent lines contain the result
	    sb.append(line);
	    sb.append(CR_LF);
	}

	String remoteStackTrace = null;

	if (sb.length() > 0) {
	    remoteStackTrace = sb.toString();
	}

	// System.err.println("exceptionName: " + exceptionName);

	// Ok, build the authorized Exception
	if (exceptionName.contains(Tag.ClassNotFoundException)) {
	    throw new RemoteException(message, new ClassNotFoundException(
		    message), remoteStackTrace);
	} else if (exceptionName.contains(Tag.InstantiationException)) {
	    throw new RemoteException(message, new InstantiationException(
		    message), remoteStackTrace);
	} else if (exceptionName.contains(Tag.NoSuchMethodException)) {
	    throw new RemoteException(message, new NoSuchMethodException(
		    message), remoteStackTrace);
	} else if (exceptionName.contains(Tag.InvocationTargetException)) {
	    throw new RemoteException(message, new InvocationTargetException(
		    new Exception(message)), remoteStackTrace);
	}

	// NIO case the uploaded .class file java version is incompatible with
	// server java version
	else if (exceptionName.contains(Tag.UnsupportedClassVersionError)) {
	    throw new RemoteException(message,
		    new UnsupportedClassVersionError(message), remoteStackTrace);
	}

	//
	// SQL Exceptions
	//
	else if (exceptionName.contains(Tag.SQLException)) {
	    throw new RemoteException(message, new SQLException(message),
		    remoteStackTrace);
	} else if (exceptionName.contains(Tag.BatchUpdateException)) {
	    throw new RemoteException(message, new BatchUpdateException(),
		    remoteStackTrace);
	}

	//
	// Security Failure
	//
	else if (exceptionName.contains(Tag.SecurityException)) {
	    // throw new RemoteException(message, new
	    // SecurityException(message), remoteStackTrace);
	    throw new SecurityException(message);
	}

	//
	// IOExceptions
	//
	else if (exceptionName.contains(Tag.FileNotFoundException)) {
	    throw new RemoteException(message, new FileNotFoundException(
		    message), remoteStackTrace);
	} else if (exceptionName.contains(Tag.IOException)) {
	    throw new RemoteException(message, new IOException(message),
		    remoteStackTrace);
	}

	//
	// Server Failure: these errors should never be thrown by server :
	// - NullPointerException
	// - IllegalArgumentException
	//
	else if (exceptionName.contains(Tag.NullPointerException)) {
	    throw new RemoteException(message,
		    new NullPointerException(message), remoteStackTrace);
	} else if (exceptionName.contains(Tag.IllegalArgumentException)) {
	    throw new RemoteException(message, new IllegalArgumentException(
		    message), remoteStackTrace);
	} else {
	    // All other cases ==> IOException with no cause
	    throw new RemoteException("Remote " + exceptionName + ": "
		    + message, new IOException(message), remoteStackTrace);
	}

    }

    /**
     * debug tool
     */
    private static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
