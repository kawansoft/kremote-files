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
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.StringUtil;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.api.server.session.SessionConfigurator;
import org.kawanfw.file.servlet.util.FileTransferManager;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * 
 * @author Nicolas de Pomereu
 * 
 *         Upload file name See Word Documentation for help
 */

public class ServerFileUploadAction {
    private static boolean DEBUG = FrameworkDebug
	    .isSet(ServerFileUploadAction.class);

    // Max file size
    @SuppressWarnings("unused")
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 20;

    // A space
    public static final String SPACE = " ";

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
     *            the session configuration in uses
     * @throws IOException
     */

    public void executeAction(HttpServletRequest request,
	    HttpServletResponse response, File servletContextTempDir,
	    FileConfigurator fileConfigurator,
	    SessionConfigurator sessionConfigurator) throws IOException {
	PrintWriter out = response.getWriter();

	try {
	    String username = null;
	    String token = null;
	    String filename = null;
	    long chunkLength = 0;

	    response.setContentType("text/html");
	    // Prepare the response

	    // Check that we have a file upload request
	    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	    debug("isMultipart: " + isMultipart);

	    if (!isMultipart) {
		return;
	    }

	    // Create a factory for disk-based file items
	    DiskFileItemFactory factory = new DiskFileItemFactory();
	    factory.setRepository(servletContextTempDir);

	    debug("servletContextTempDir: " + servletContextTempDir);

	    // Create a new file upload handler using the factory
	    // that define the secure temp dir
	    ServletFileUpload upload = new ServletFileUpload(factory);

	    // Parse the request
	    FileItemIterator iter = upload.getItemIterator(request);

	    // Parse the request
	    // List /* FileItem */ items = upload.parseRequest(request);
	    while (iter.hasNext()) {
		FileItemStream item = iter.next();
		String name = item.getFieldName();
		debug("name: " + name);

		// The input Stream for the File
		InputStream stream = item.openStream();

		if (item.isFormField()) {
		    if (name.equals(Parameter.USERNAME)) {
			// username = Streams.asString(stream);
			username = Streams.asString(stream);

			// Not sure it's necessary:
			username = HtmlConverter.fromHtml(username);

			debug("username: " + username);
		    } else if (name.equals(Parameter.TOKEN)) {
			// token = Streams.asString(stream);
			token = Streams.asString(stream);
			debug("token: " + token);
		    } else if (name.equals(Parameter.FILENAME)) {
			// filename = Streams.asString(stream);
			filename = Streams.asString(stream);
			debug("filename: " + filename);
		    } else if (name.equals(Parameter.CHUNKLENGTH)) {
			String chunklengthStr = Streams.asString(stream);
			chunkLength = Long.parseLong(chunklengthStr);
			debug("chunklengthStr: " + chunklengthStr);
		    }
		} else {

		    // JWT auth
		    token = StringUtil.getTrimValue(token);
		    boolean isVerified = sessionConfigurator.verifyToken(token);

		    if (! isVerified) {
			debug("invalid token!");
			debug("username: " + username);
			debug("token   : " + token);

			out.println(TransferStatus.SEND_OK);
			out.println(ReturnCode.INVALID_LOGIN_OR_PASSWORD);

			return;
		    }

		    // Not sure it's necessary:
		    filename = HtmlConverter.fromHtml(filename);

		    debug("");
		    debug("File field " + name + " with file name "
			    + item.getName() + " detected.");
		    debug("filename: " + filename);

		    new FileTransferManager().upload(fileConfigurator, stream,
			    username, filename, chunkLength);

		    out.println(TransferStatus.SEND_OK);
		    out.println("OK");

		    return;
		}
	    }
	} catch (Throwable throwable) {

	    Throwable finalThrowable = ServerFileDispatch
		    .getFinalThrowable(throwable);

	    out.println(TransferStatus.SEND_FAILED);
	    out.println(finalThrowable.getClass().getName());
	    out.println(ServerUserThrowable.getMessage(finalThrowable));
	    out.println(ExceptionUtils.getStackTrace(finalThrowable)); // stack
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
     * return the filename without the trailing / or \\
     * 
     * @param filename
     *            the filename to remove the trailing file separator
     * @return
     */
    public static String removeTrailingFileSep(String filename) {
	if (filename == null) {
	    return null;
	}

	if (filename.startsWith("" + File.separatorChar)) {
	    filename = filename.substring(1);
	}

	return filename;
    }

    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
