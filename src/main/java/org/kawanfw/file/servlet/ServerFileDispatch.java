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
package org.kawanfw.file.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.commons.json.ListOfStringTransport;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.StringUtil;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.api.server.session.SessionConfigurator;
import org.kawanfw.file.reflection.ClassPathUtil;
import org.kawanfw.file.servlet.nio.FileListAction;
import org.kawanfw.file.servlet.nio.FileListFilesAction;
import org.kawanfw.file.servlet.nio.FileMethodOneReturnAction;
import org.kawanfw.file.servlet.nio.KawanfwSecurityManager;
import org.kawanfw.file.servlet.util.CallUtil;
import org.kawanfw.file.servlet.util.FileTransferManager;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;
import org.kawanfw.file.util.parms.Action;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * @author Nicolas de Pomereu
 * 
 *         The method executeRequest() is to to be called from the
 *         ServerCallerRecv Servlet and Class. <br>
 *         It will execute a client side request with a ServerCaller.call()
 *         instance.
 * 
 */
public class ServerFileDispatch {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(ServerFileDispatch.class);

    // A space
    public static final String SPACE = " ";

    public static String CR_LF = System.getProperty("line.separator");

    public static KawanfwSecurityManager securityManager = null;

    /**
     * Constructor
     */
    public ServerFileDispatch() {

    }

    /**
     * 
     * Execute the dispatched request
     * 
     * @param request
     *            the http request
     * @param response
     *            the http response
     * @param servletContextTempDir
     *            The temp dir used by Servlets
     * @param sessionConfigurator
     * @param fileConfigurator
     *            the client configurator for files
     * @throws IOException
     *             if any Servlet Exception occurs
     */
    public void executeRequest(HttpServletRequest request,
	    HttpServletResponse response, File servletContextTempDir,
	    SessionConfigurator sessionConfigurator,
	    FileConfigurator fileConfigurator) throws IOException {
	OutputStream out = null;

	try {

	    // Immediate catch if we are asking a file upload, because
	    // parameters are
	    // in unknown sequence. We know it's a upload action if it's mime
	    // multipart
	    if (ServletFileUpload.isMultipartContent(request)) {
		ServerFileUploadAction serverFileUploadAction = new ServerFileUploadAction();
		serverFileUploadAction.executeAction(request, response,
			servletContextTempDir, fileConfigurator,
			sessionConfigurator);
		return;
	    }

	    debug("ServerFileDispatch begin 2");

	    // The action & filename (for file size ask)
	    String action = null;

	    // We must trap the IllegalArgumentException to rethrow properly to
	    // client
	    // This happens if there is an encryption problem
	    try {
		action = request.getParameter(Parameter.ACTION);
	    } catch (IllegalArgumentException e) {
		out = response.getOutputStream();
		throw e;
	    }

	    action = StringUtil.getTrimValue(action);

	    debug("ACTION : " + action);

	    // Special action for Login, because Token does not exists and must
	    // be built
	    if (action.equals(Action.LOGIN_ACTION)
		    || action.equals(Action.BEFORE_LOGIN_ACTION)) {

		ServerLoginAction serverLoginAction = new ServerLoginAction();
		serverLoginAction.executeAction(request, response,
			fileConfigurator, sessionConfigurator, action);
		return;
	    }

	    // The username (used for token re-compilation)
	    String username = request.getParameter(Parameter.USERNAME);
	    username = StringUtil.getTrimValue(username);
	    debug("username : " + username);

	    out = response.getOutputStream();

	    // Only if there is a call action, we may execute authorized classes
	    // without authentication/login
	    if (action.equals(Action.CALL_ACTION)
		    || action.equals(Action.CALL_ACTION_HTML_ENCODED)) {
		// The class name
		String methodName = request.getParameter(Parameter.METHOD_NAME);
		methodName = StringUtil.getTrimValue(methodName);

		String className = StringUtils.substringBeforeLast(methodName,
			".");
		Class<?> c = Class.forName(className);
		CallUtil callUtil = new CallUtil(c, fileConfigurator, username);
		boolean callAllowed = callUtil.isCallableNotAuthenticated();

		if (callAllowed) {
		    if (action.equals(Action.CALL_ACTION)
			    || action.equals(Action.CALL_ACTION_HTML_ENCODED)) {

			ServerCallAction serverCallAction = new ServerCallAction();
			serverCallAction.call(request, fileConfigurator, out,
				null);
		    }

		    return;
		}
	    }

	    // For all other actions, we check the parameters

	    // JWT auth
	    String token = request.getParameter(Parameter.TOKEN);
	    token = StringUtil.getTrimValue(token);

	    boolean isVerified = sessionConfigurator.verifyToken(token);

	    if (! isVerified) {
		debug("invalid token!");
		debug("username: " + username);
		debug("token   : " + token);

		writeLine(out, TransferStatus.SEND_OK);
		writeLine(out, ReturnCode.INVALID_LOGIN_OR_PASSWORD);

		return;
	    }

	    // Ok, install our security manager
	    // installSecurityManager(fileConfigurator);

	    // Displays class path
	    if (DEBUG)
		ClassPathUtil.displayClasspath();

	    // The filename
	    String filename = request.getParameter(Parameter.FILENAME);
	    filename = StringUtil.getTrimValue(filename);

	    // Call to a File method that returns one result (no list return)
	    if (action.equals(Action.FILE_METHOD_ONE_RETURN_ACTION)) {
		FileMethodOneReturnAction fileMethodOneReturnAction = new FileMethodOneReturnAction();
		fileMethodOneReturnAction.call(request, fileConfigurator, out,
			username, filename);

		return;
	    }
	    // Call to a File.list() or File.list(FilenameFilter)
	    else if (action.equals(Action.FILE_LIST_ACTION)) {

		FileListAction fileListAction = new FileListAction();
		fileListAction.list(request, fileConfigurator, out, username,
			filename);
		return;
	    }
	    // Call to a File.listFiles() or File.listFiles(FileFilter)
	    // or File.listFiles(FilenameFilter)
	    else if (action.equals(Action.FILE_LIST_FILES_ACTION)) {

		FileListFilesAction fileListFilesAction = new FileListFilesAction();
		fileListFilesAction.listFiles(request, fileConfigurator, out,
			username, filename);
		return;
	    } else if (action.equals(Action.CALL_ACTION)
		    || action.equals(Action.CALL_ACTION_HTML_ENCODED)) {
		ServerCallAction serverCallAction = new ServerCallAction();
		serverCallAction.call(request, fileConfigurator, out, username);
		return;
	    } else if (action.equals(Action.GET_FILE_LENGTH_ACTION)) {
		long result = actionGetListFileLength(fileConfigurator,
			username, filename);

		writeLine(out, TransferStatus.SEND_OK);
		writeLine(out, Long.toString(result));
	    } else if (action.equals(Action.GET_JAVA_VERSION)) {
		String javaVersion = System.getProperty("java.version");
		writeLine(out, TransferStatus.SEND_OK);
		writeLine(out, javaVersion);
	    } else if (action.equals(Action.DOWNLOAD_FILE_ACTION)) {

		String chunkLengtgStr = request
			.getParameter(Parameter.CHUNKLENGTH);
		long chunkLength = Long.parseLong(chunkLengtgStr);

		boolean result = new FileTransferManager().download(out,
			fileConfigurator, username, filename, chunkLength);

		if (!result) {
		    // Impossible to find the file on server
		    writeLine(out, TransferStatus.SEND_OK);
		    writeLine(out, Tag.FileNotFoundException);
		    // throw new FileNotFoundException(
		    // "File not found on remote server: " + filename);
		}
	    } else {
		throw new IllegalArgumentException(
			"Invalid Client Action: " + action);
	    }

	    return;
	} catch (Throwable throwable) {

	    if (DEBUG)
		throwable.printStackTrace(System.out);

	    Throwable finalThrowable = getFinalThrowable(throwable);

	    writeLine(out, TransferStatus.SEND_FAILED);
	    writeLine(out, finalThrowable.getClass().getName()); // Exception
								 // class name
	    writeLine(out, ServerUserThrowable.getMessage(finalThrowable)); // Exception
	    // message
	    writeLine(out, ExceptionUtils.getStackTrace(finalThrowable)); // stack
									  // trace

	    try {
		ServerLogger.getLogger().log(Level.WARNING,
			Tag.PRODUCT_EXCEPTION_RAISED + " " + ServerUserThrowable
				.getMessage(finalThrowable));
		ServerLogger.getLogger().log(Level.WARNING,
			Tag.PRODUCT_EXCEPTION_RAISED + " "
				+ ExceptionUtils.getStackTrace(finalThrowable));
	    } catch (Exception e1) {
		e1.printStackTrace();
		e1.printStackTrace(System.out);
	    }

	}
    }


    /**
     * Analyze the throwable and build the final Exception/Throwable
     * 
     * @param throwable
     *            the input throwable thrown
     * @return the new rewritten Throwable
     */
    public static Throwable getFinalThrowable(Throwable throwable) {
	Throwable finalThrowable = null;
	Throwable cause = throwable.getCause();

	if (cause != null && cause instanceof ClassNotFoundException
		|| cause != null && cause instanceof NoClassDefFoundError) {
	    finalThrowable = new ClassNotFoundException(throwable.getMessage());
	} else if (cause != null
		&& cause instanceof UnsupportedClassVersionError) {
	    finalThrowable = new UnsupportedClassVersionError(
		    throwable.getMessage());
	} else {

	    if (cause != null) {
		finalThrowable = cause;
	    } else {
		finalThrowable = throwable;
	    }
	}

	return finalThrowable;
    }

    /**
     * NOT USED ANYMORE Install the Security Manager that restricts FileFilter
     * and FilenameFilter to write/delete files.
     * 
     * @param fileConfigurator
     *            the file configurator in use
     */
    @SuppressWarnings("unused")
    private void installSecurityManager(FileConfigurator fileConfigurator) {
	// Ok, install our security manager
	if (System.getSecurityManager() == null) {
	    securityManager = new KawanfwSecurityManager();
	    System.setSecurityManager(securityManager);
	}
    }

    /**
     * Write a line of string on the servlet output stream. Will add the
     * necessary CR_LF
     * 
     * @param out
     *            the servlet output stream
     * @param s
     *            the string to write
     * @throws IOException
     */
    private void writeLine(OutputStream out, String s) throws IOException {
	out.write((s + CR_LF).getBytes());
    }

    /**
     * Action: get a file list length
     * 
     * @param fileConfigurator
     * @param filename
     *            the filelist
     * @return the length of file list
     */
    private long actionGetListFileLength(FileConfigurator fileConfigurator,
	    String username, String filename) throws IOException {
	debug("Action.GET_FILE_LENGTH_ACTION");
	long result = 0;

	// We have in fact a list of files
	List<String> files = ListOfStringTransport.fromJson(filename);

	// actionGetListFileLength: We must convert each element of List<String>
	// files from Html
	files = HtmlConverter.fromHtml(files);

	for (String theFilename : files) {

	    // result += fileActionManager.length(fileConfigurator,
	    // username, theFilename);

	    theFilename = HttpConfigurationUtil.addUserHomePath(fileConfigurator,
		    username, theFilename);

	    File file = new File(theFilename);
	    result += file.length();
	}

	return result;
    }

    private static void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }
}
