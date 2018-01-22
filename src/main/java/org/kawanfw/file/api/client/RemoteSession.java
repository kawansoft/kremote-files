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
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.kawanfw.commons.client.http.HttpTransfer;
import org.kawanfw.commons.client.http.HttpTransferUtil;
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
import org.kawanfw.file.version.FileVersion;

/**
 * Main class for establishing an HTTP session with a remote host. <br>
 * <br>
 * Main operations on remote files are done passing a {@code RemoteSession}
 * instance to {@link RemoteFile}, {@link RemoteInputStream} and
 * {@link RemoteOutputStream}.<br>
 * <br>
 * {@code RemoteSession} allows also the execution of some basic operations:
 * <br>
 * <br>
 * <ul>
 * <li>Get the Java version of the servlet container on the remote server.</li>
 * <li>Call remote Java methods through the integrated RPC server.</li>
 * <li>Returns with one call the length of a list of files located on the remote
 * server.</li>
 * </ul>
 * <br>
 * Example: <blockquote>
 * 
 * <pre>
 * // Define URL of the path to the {@code ServerFileManager} servlet
 * String url = &quot;https://www.acme.org/ServerFileManager&quot;;
 * 
 * // The login info for strong authentication on server side:
 * String username = &quot;myUsername&quot;;
 * char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };
 * 
 * // Establish a session with the remote server
 * RemoteSession remoteSession = new RemoteSession(url, username, password);
 * 
 * // Create a new RemoteFile that maps a file on remote server
 * RemoteFile remoteFile = new RemoteFile(remoteSession, &quot;/Koala.jpg&quot;);
 * 
 * // We can use all the familiar java.io.File methods on our RemoteFile
 * if (remoteFile.exists()) {
 *     System.out.println(
 * 	    remoteFile.getName() + &quot; length  : &quot; + remoteFile.length());
 *     System.out.println(
 * 	    remoteFile.getName() + &quot; canWrite: &quot; + remoteFile.canWrite());
 * }
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Communication via a proxy server is done using {@code java.net.Proxy} and
 * {@code java.net.PasswordAuthentication} for authentication.
 * 
 * <blockquote>
 * 
 * <pre>
 * String url = "http://www.acme.org/ServerFileManager";
 * String username = "myUsername";
 * char[] password = "myPassword".toCharArray();
 * 
 * Proxy proxy = new Proxy(Proxy.Type.HTTP,
 * 	new InetSocketAddress("proxyHostname", 8080));
 * 
 * PasswordAuthentication passwordAuthentication = null;
 * 
 * // If proxy requires authentication:
 * passwordAuthentication = new PasswordAuthentication("proxyUsername",
 * 	"proxyPassword".toCharArray());
 * 
 * RemoteSession remoteSession = new RemoteSession(url, username, password,
 * 	proxy, passwordAuthentication);
 * // Etc.
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * NTLM authentication is done using {@code PasswordAuthentication}:
 * 
 * <blockquote>
 * 
 * <pre>
 * String url = "http://www.acme.org/ServerFileManager";
 * String username = "myUsername";
 * char[] password = "myPassword".toCharArray();
 * 
 * Proxy proxy = Proxy.NO_PROXY;
 * 
 * // DOMAIN is passed along username:
 * PasswordAuthentication passwordAuthentication = new PasswordAuthentication(
 * 	"DOMAIN\\username", "password".toCharArray());
 * 
 * RemoteSession remoteSession = new RemoteSession(url, username, password,
 * 	proxy, passwordAuthentication);
 * // Etc.
 * </pre>
 * 
 * </blockquote>
 * 
 * @see org.kawanfw.file.api.client.RemoteFile
 * @see org.kawanfw.file.api.client.RemoteInputStream
 * @see org.kawanfw.file.api.client.RemoteOutputStream
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class RemoteSession implements Cloneable {

    static final String REMOTE_SESSION_IS_CLOSED = "RemoteSession is closed.";

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(RemoteSession.class);

    /** Defines 1 kilobyte */
    public static final int KB = 1024;

    /** Defines 1 megabyte */
    public static final int MB = 1024 * KB;

    /** The url to use to connect to the KRemote Files Server */
    private String url = null;

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

    /** Proxy to use with HttpUrlConnection */
    private Proxy proxy = null;

    /** For authenticated proxy */
    private PasswordAuthentication passwordAuthentication = null;

    /** The Http Parameters instance */
    private SessionParameters sessionParameters = null;

    /** The http transfer instance */
    private HttpTransfer httpTransfer = null;

    /** The remote Java version */
    private String remoteJavaVersion = null;

    /**
     * Private constructor for clone().
     * 
     * @param url
     *            the URL of the path to the {@code ServerFileManager} Servlet
     * @param username
     *            the username for authentication on the KRemote Files Server
     *            (may be null for call() or downloadUrl())
     * @param authenticationToken
     *            the actual token of the KRemote Files session to clone
     * @param proxy
     *            the proxy to use, may be null for direct access
     * @param passwordAuthentication
     *            the proxy credentials, null if no proxy or if the proxy does
     *            not require authentication
     * @param sessionParameters
     *            the http parameters to use
     * @param remoteJavaVersion
     *            the Java version on remote server
     */
    private RemoteSession(String url, String username,
	    String authenticationToken, Proxy proxy,
	    PasswordAuthentication passwordAuthentication,
	    SessionParameters sessionParameters, String remoteJavaVersion) {
	this.url = url;
	this.username = username;
	this.authenticationToken = authenticationToken;
	this.proxy = proxy;
	this.passwordAuthentication = passwordAuthentication;
	this.sessionParameters = sessionParameters;
	this.remoteJavaVersion = remoteJavaVersion;

	httpTransfer = HttpTransferUtil.HttpTransferFactory(url, proxy,
		passwordAuthentication, sessionParameters);
    }

    /**
     * Creates an KRemote Files session with a proxy and protocol parameters.
     * 
     * @param url
     *            the URL of the path to the {@code ServerFileManager} Servlet
     * @param username
     *            the username for authentication on the KRemote Files Server
     *            (may be null for <code>call()</code>
     * @param password
     *            the user password for authentication on the KRemote Files
     *            Server (may be null)
     * @param proxy
     *            the proxy to use, may be null for direct access
     * @param passwordAuthentication
     *            the proxy credentials, null if no proxy or if the proxy does
     *            not require authentication
     * @param sessionParameters
     *            the session parameters to use (may be null)
     * 
     * @throws MalformedURLException
     *             if the url is malformed
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
     * @throws InvalidLoginException
     *             the username or password is invalid
     * @throws SecurityException
     *             Scheme is required to be https (SSL/TLS)
     * @throws RemoteException
     *             an exception has been thrown on the server side. This traps a
     *             product failure and should not happen.
     * @throws IOException
     *             for all other IO / Network / System Error
     */
    public RemoteSession(String url, String username, char[] password,
	    Proxy proxy, PasswordAuthentication passwordAuthentication,
	    SessionParameters sessionParameters)
	    throws MalformedURLException, UnknownHostException,
	    ConnectException, SocketException, InvalidLoginException,
	    RemoteException, SecurityException, IOException {

	if (url == null) {
	    throw new MalformedURLException("url is null!");
	}

	@SuppressWarnings("unused")
	URL asUrl = new URL(url); // Try to raise a MalformedURLException;

	this.username = username;
	this.url = url;

	this.proxy = proxy;
	this.passwordAuthentication = passwordAuthentication;
	this.sessionParameters = sessionParameters;

	// Launch the Servlet
	httpTransfer = HttpTransferUtil.HttpTransferFactory(url, proxy,
		passwordAuthentication, sessionParameters);

	// username & password may be null: for call()
	if (username == null) {
	    return;
	}

	// TestReload if SSL required by host
	// if (this.url.toLowerCase().startsWith("http://") && isForceHttps()) {
	// throw new SecurityException(
	// Tag.PRODUCT_SECURITY
	// + " Remote Host requires a SSL url that starts with \"https\"
	// scheme");
	// }

	String passwordStr = new String(password);

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.TEST_CRYPTO,
		Parameter.TEST_CRYPTO));
	requestParams.add(
		new SimpleNameValuePair(Parameter.ACTION, Action.LOGIN_ACTION));
	requestParams
		.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams
		.add(new SimpleNameValuePair(Parameter.PASSWORD, passwordStr));

	httpTransfer.send(requestParams);

	// If everything is OK, we have in our protocol a response that
	// 1) starts with "OK". 2) Is followed by the Authentication Token
	// else: response starts with "INVALID_LOGIN_OR_PASSWORD".

	String receive = httpTransfer.recv();

	debug("receive: " + receive);

	if (receive.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    throw new InvalidLoginException("Invalid username or password.");
	} else if (receive.startsWith(ReturnCode.OK)) {
	    // OK! We are logged in & and correctly authenticated
	    // Keep in static memory the Authentication Token for next api
	    // commands (First 20 chars)
	    String theToken = receive.substring(ReturnCode.OK.length() + 1);

	    // authenticationToken = StringUtils.left(theToken,
	    // Parameter.TOKEN_LEFT_SIZE);

	    authenticationToken = theToken;

	} else {
	    this.username = null;
	    // Should never happen
	    throw new InvalidLoginException(
		    Tag.PRODUCT_PRODUCT_FAIL + " Please contact support.");
	}

    }

    /**
     * Creates an KRemote Files session with a proxy.
     * 
     * @param url
     *            the URL of the path to the {@code ServerFileManager} Servlet
     * @param username
     *            the username for authentication on the KRemote Files Server
     *            (may be null for <code>call()</code>
     * @param password
     *            the user password for authentication on the KRemote Files
     *            Server (may be null)
     * @param proxy
     *            the proxy to use, may be null for direct access
     * @param passwordAuthentication
     *            the proxy credentials, null if no proxy or if the proxy does
     *            not require authentication
     * 
     * @throws MalformedURLException
     *             if the url is malformed
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
     * @throws InvalidLoginException
     *             the username or password is invalid
     * @throws SecurityException
     *             scheme is required to be https (SSL/TLS)
     * @throws RemoteException
     *             an exception has been thrown on the server side
     * @throws IOException
     *             for all other IO / Network / System Error
     */
    public RemoteSession(String url, String username, char[] password,
	    Proxy proxy, PasswordAuthentication passwordAuthentication)
	    throws MalformedURLException, UnknownHostException,
	    ConnectException, SocketException, InvalidLoginException,
	    RemoteException, SecurityException, IOException {
	this(url, username, password, proxy, passwordAuthentication, null);
    }

    /**
     * Creates an KRemote Files session.
     * 
     * @param url
     *            the URL of the path to the {@code ServerFileManager} Servlet
     * @param username
     *            the username for authentication on the KRemote Files Server
     *            (may be null for <code>call()</code>
     * @param password
     *            the user password for authentication on the KRemote Files
     *            Server (may be null)
     * 
     * @throws MalformedURLException
     *             if the url is malformed
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
     * @throws InvalidLoginException
     *             the username or password is invalid
     * @throws SecurityException
     *             scheme is required to be https (SSL/TLS)
     * @throws RemoteException
     *             an exception has been thrown on the server side
     * @throws IOException
     *             for all other IO / Network / System Error
     */

    public RemoteSession(String url, String username, char[] password)
	    throws MalformedURLException, UnknownHostException,
	    ConnectException, SocketException, InvalidLoginException,
	    RemoteException, IOException, SecurityException {
	this(url, username, password, null, null);
    }

    /**
     * Returns the username of this KRemote Files session
     * 
     * @return the username of this KRemote Files session
     */
    public String getUsername() {
	return this.username;
    }

    /**
     * Returns the {@code SessionParameters} instance in use for the KRemote
     * Files session.
     * 
     * @return the {@code SessionParameters} instance in use for the KRemote
     *         Files session
     */
    public SessionParameters getSessionParameters() {
	return this.sessionParameters;
    }

    /**
     * Returns the URL of the path to the <code>ServerFileManager</code> Servlet
     * (or <code>ServerSqlManager</code> Servlet if session has been initiated
     * by a <code>RemoteConnection</code>).
     * 
     * @return the URL of the path to the <code>ServerFileManager</code> Servlet
     */
    public String getUrl() {
	return url;
    }

    /**
     * Returns the {@code Proxy} instance in use for this File Session.
     * 
     * @return the {@code Proxy} instance in use for this File Session
     */
    public Proxy getProxy() {
	return this.proxy;
    }

    /**
     * Returns the proxy credentials
     * 
     * @return the proxy credentials
     */
    public PasswordAuthentication getPasswordAuthentication() {
	return passwordAuthentication;
    }

    /**
     * Returns the http status code of the last executed verb
     * 
     * @return the http status code of the last executed verb
     */
    public int getHttpStatusCode() {
	if (httpTransfer != null) {
	    return httpTransfer.getHttpStatusCode();
	} else {
	    return 0;
	}
    }

    /**
     * Returns the Authentication Token. This method is used by the Kawansoft
     * frameworks.
     * 
     * @return the Authentication Token
     */
    public String getAuthenticationToken() {
	return this.authenticationToken;
    }

    /**
     * Calls a remote Java method and (eventually) pass some parameters to it.
     * 
     * @param methodName
     *            the full method name to call in the format
     *            <code>org.acme.config.package.MyClass.myMethod</code>
     * @param params
     *            the array of parameters passed to the method
     * 
     * @return the result of the Java call as {@code String}
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

    public String call(String methodName, Object... params)
	    throws IllegalArgumentException, InvalidLoginException,
	    UnknownHostException, ConnectException, SocketException,
	    RemoteException, IOException {

	// Class and method name can not be null
	if (methodName == null) {
	    throw new IllegalArgumentException("methodName can not be null!");
	}

	// username & Authentication Token may be null
	// because some methods can be called freely

	if (username == null) {
	    username = "null";
	}

	if (authenticationToken == null) {
	    authenticationToken = "null";
	}

	// Build the params types
	List<String> paramsTypes = new Vector<String>();

	// Build the params values
	List<String> paramsValues = new Vector<String>();

	debug("");

	for (int i = 0; i < params.length; i++) {
	    if (params[i] == null) {
		throw new IllegalArgumentException(Tag.PRODUCT
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

	// ListHolder listHolderTypes = new ListHolder();
	// listHolderTypes.setList(paramsTypes);
	String jsonParamTypes = ListOfStringTransport.toJson(paramsTypes);

	// ListHolder listHolderValues = new ListHolder();
	// listHolderValues.setList(paramsValues);
	String jsonParamValues = ListOfStringTransport.toJson(paramsValues);

	debug("methodName     : " + methodName);
	debug("jsonParamTypes : " + jsonParamTypes);
	debug("jsonParamValues: " + jsonParamValues);

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.CALL_ACTION_HTML_ENCODED));
	requestParams
		.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(
		new SimpleNameValuePair(Parameter.TOKEN, authenticationToken));
	requestParams.add(
		new SimpleNameValuePair(Parameter.METHOD_NAME, methodName));
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
	    throw new InvalidLoginException(REMOTE_SESSION_IS_CLOSED);
	}

	// The response is in Html encode:
	if (!response.isEmpty()) {
	    response = HtmlConverter.fromHtml(response);
	}

	return response;

    }

    /**
     * Returns with one call the length of a list of files located on the remote
     * host.
     * <p>
     * This convenient methods is provided for fast compute of the total length
     * of a list of files to download, without contacting the server for each
     * file result. (Case using a progress monitor).
     * <p>
     * The real paths of the remote files depend on the KRemote Files
     * configuration on the server. See User Documentation.
     * 
     * @param pathnames
     *            the list of pathnames on host with "/" as file separator. Must
     *            be absolute.
     * 
     * @return the total length in bytes of the files located on the remote
     *         host.
     * 
     * @throws IllegalArgumentException
     *             if pathnames is null
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * 
     * @throws UnknownHostException
     *             if the host URL (http://www.acme.org) does not exists or no
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
    public long length(List<String> pathnames) throws IllegalArgumentException,
	    InvalidLoginException, UnknownHostException, ConnectException,
	    SocketException, RemoteException, IOException {

	if (pathnames == null) {
	    throw new IllegalArgumentException("pathnames can not be null!");
	}

	if (username == null || authenticationToken == null) {
	    throw new InvalidLoginException(REMOTE_SESSION_IS_CLOSED);
	}

	pathnames = HtmlConverter.toHtml(pathnames);
	String jsonString = ListOfStringTransport.toJson(pathnames);

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.GET_FILE_LENGTH_ACTION));
	requestParams
		.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(
		new SimpleNameValuePair(Parameter.TOKEN, authenticationToken));
	requestParams
		.add(new SimpleNameValuePair(Parameter.FILENAME, jsonString));

	httpTransfer.send(requestParams);

	// If everything is OK, we have in our protocol a response that
	// 1) starts with "OK". 2) Is followed by the Authentication Token
	// else: response starts with "INVALID_LOGIN_OR_PASSWORD".

	String response = httpTransfer.recv();

	if (response.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    throw new InvalidLoginException(REMOTE_SESSION_IS_CLOSED);
	} else {
	    try {
		long fileLength = Long.parseLong(response);
		return fileLength;
	    } catch (NumberFormatException nfe) {
		// Build a product Exception with the content of the recv stream
		throw new IOException(
			Tag.PRODUCT_PRODUCT_FAIL + " " + nfe.getMessage(), nfe);
	    }
	}
    }

    /**
     * Returns the Java version of the the servlet container on the remote
     * server <br>
     * (The value of {@code System.getProperty("java.version")}.
     * 
     * @return the Java version of the the servlet container on the remote
     *         server
     * 
     * @throws InvalidLoginException
     *             the session has been closed by a {@code logoff()}
     * 
     * @throws UnknownHostException
     *             if the host URL (http://www.acme.org) does not exists or no
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
    public String getRemoteJavaVersion()
	    throws InvalidLoginException, UnknownHostException,
	    ConnectException, SocketException, RemoteException, IOException {

	if (username == null || authenticationToken == null) {
	    throw new InvalidLoginException(REMOTE_SESSION_IS_CLOSED);
	}

	// Remote Java verdion is cached
	if (remoteJavaVersion != null) {
	    return remoteJavaVersion;
	}

	// Prepare the request parameters
	List<SimpleNameValuePair> requestParams = new Vector<SimpleNameValuePair>();
	requestParams.add(new SimpleNameValuePair(Parameter.ACTION,
		Action.GET_JAVA_VERSION));
	requestParams
		.add(new SimpleNameValuePair(Parameter.USERNAME, username));
	requestParams.add(
		new SimpleNameValuePair(Parameter.TOKEN, authenticationToken));

	httpTransfer.send(requestParams);

	// If everything is OK, we have in our protocol a response that
	// 1) starts with "OK". 2) Is followed by the Authentication Token
	// else: response starts with "INVALID_LOGIN_OR_PASSWORD".

	String response = httpTransfer.recv();

	if (response.startsWith(ReturnCode.INVALID_LOGIN_OR_PASSWORD)) {
	    throw new InvalidLoginException(REMOTE_SESSION_IS_CLOSED);
	} else {
	    remoteJavaVersion = response;
	    return remoteJavaVersion;
	}
    }

    /**
     * Allows to get a copy of the current <code>RemoteSession</code>: use it to
     * do some simultaneous operations in a different thread (in order to avoid
     * conflicts).
     */
    @Override
    public RemoteSession clone() {
	RemoteSession remoteSession = new RemoteSession(this.url, this.username,
		this.authenticationToken, this.proxy,
		this.passwordAuthentication, this.sessionParameters,
		this.remoteJavaVersion);
	return remoteSession;
    }

    /**
     * Returns the KRemote Files Version.
     * 
     * @return the KRemote Files Version
     */
    public String getVersion() {
	return FileVersion.getVersion();
    }

    /**
     * Logs off from the remote host.&nbsp; This will purge the authentication
     * values necessary for method calls.
     * <p>
     * <b>Method should be called at the closure of the Client application</b>.
     */
    public void logoff() {
	username = null; 
	authenticationToken = null;

	proxy = null;
	passwordAuthentication = null;
	sessionParameters = null;
	remoteJavaVersion = null;

	if (httpTransfer != null) {
	    httpTransfer.close();
	    httpTransfer = null;
	}
    }

    /**
     * Returns the HttpTransfer instance in use
     * 
     * @return the httpTransfer instance
     */
    HttpTransfer getHttpTransfer() {
	return httpTransfer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((authenticationToken == null) ? 0
		: authenticationToken.hashCode());
	result = prime * result + ((url == null) ? 0 : url.hashCode());
	result = prime * result
		+ ((username == null) ? 0 : username.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RemoteSession other = (RemoteSession) obj;
	if (authenticationToken == null) {
	    if (other.authenticationToken != null)
		return false;
	} else if (!authenticationToken.equals(other.authenticationToken))
	    return false;
	if (url == null) {
	    if (other.url != null)
		return false;
	} else if (!url.equals(other.url))
	    return false;
	if (username == null) {
	    if (other.username != null)
		return false;
	} else if (!username.equals(other.username))
	    return false;
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "RemoteSession [url=" + url + ", username=" + username
		+ ", proxy=" + proxy + ", passwordAuthentication="
		+ passwordAuthentication + ", SessionParameters="
		+ sessionParameters + "]";
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

// End
