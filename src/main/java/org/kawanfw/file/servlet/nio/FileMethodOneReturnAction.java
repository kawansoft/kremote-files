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
package org.kawanfw.file.servlet.nio;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.commons.json.ListOfStringTransport;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.JavaValueBuilder;
import org.kawanfw.commons.util.StringUtil;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.reflection.Invoker;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;
import org.kawanfw.file.util.parms.Parameter;

/**
 * @author Nicolas de Pomereu
 * 
 *         Executes the client fileMethodOneReturn() action.
 */
public class FileMethodOneReturnAction {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileMethodOneReturnAction.class);

    /**
     * Constructor
     */
    public FileMethodOneReturnAction() {
    }

    /**
     * Calls a File remote method from the client side. <br>
     * 
     * Please note that invocation result is trapped and routed as code string
     * to the client side.
     * 
     * @param request
     *            the http request
     * @param fileConfigurator
     *            the file configurator defined by the user
     * @param out
     *            the servlet output stream
     * @param username
     *            the client login (for security check)
     * @param filename
     *            the filename to call
     * @throws IOException
     *             all framework, network, etc. errors
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    public void call(HttpServletRequest request,
	    FileConfigurator fileConfigurator,
	    OutputStream out, String username,
	    String filename) throws SQLException, IOException,
	    ClassNotFoundException, InstantiationException,
	    IllegalAccessException, NoSuchMethodException,
	    IllegalArgumentException, InvocationTargetException {

	debug("in call");

	// The method name
	String methodName = request.getParameter(Parameter.METHOD_NAME);

	// The parms name
	String paramsTypes = request.getParameter(Parameter.PARAMS_TYPES);
	String paramsValues = request.getParameter(Parameter.PARAMS_VALUES);

	// Make sure all values are not null and trimed

	methodName = StringUtil.getTrimValue(methodName);
	paramsTypes = StringUtil.getTrimValue(paramsTypes);
	paramsValues = StringUtil.getTrimValue(paramsValues);

	debug("methodName: " + methodName);
	debug("username  : " + username);
	debug("paramsTypes     : " + paramsTypes);
	debug("paramsValues    : " + paramsValues);

	List<String> listParamsTypes = ListOfStringTransport
		.fromJson(paramsTypes);
	List<String> listParamsValues = ListOfStringTransport
		.fromJson(paramsValues);

	debug("listParamsTypes      : " + listParamsTypes);
	debug("listParamsValues     : " + listParamsValues);

	Class<?>[] argTypes = new Class[listParamsTypes.size()];
	Object[] values = new Object[listParamsValues.size()];

	for (int i = 0; i < listParamsTypes.size(); i++) {
	    String value = listParamsValues.get(i);
	    String javaType = listParamsTypes.get(i);

	    JavaValueBuilder javaValueBuilder = new JavaValueBuilder(javaType,
		    value, request);
	    argTypes[i] = javaValueBuilder.getClassOfValue();
	    values[i] = javaValueBuilder.getValue();

	    // Special treatment to Files
	    if (values[i] instanceof File) {
		String valueFile = HttpConfigurationUtil.addUserHomePath(
			fileConfigurator, username, values[i].toString());
		values[i] = new File(valueFile);
	    }
	}

	String clientFilename = filename;
	
	debug("filename: " + filename);
	
	if (values.length >= 1) {
	    debug("values[0]: " + values[0]) ;
	}

	// Add the root path, if necessary, to the filenames
	filename = HttpConfigurationUtil.addUserHomePath(fileConfigurator,
		username, filename);

	debug("filename: " + filename);
	debug("values: " + values);

	// Create the File and call the method asked by client side
	File file = new File(filename);
	Object resultObj = null;

	// Special case for equal(Object ob) that can not be called with reflection 
	// because requires as parameter an object instead of a file
	try {
	    if (methodName.equals("equals")) {
	        resultObj = file.equals(values[0]);
	    }
	    else if (listParamsTypes.size() == 0) {
	        resultObj = Invoker.getMethodResult(file, methodName);
	    } else {
	        resultObj = Invoker.getMethodResult(file, methodName, values);
	    }
	} catch (InvocationTargetException e) {
	    if (e.getCause() != null) {
		String message = null;
		message = e.getCause().getMessage();
		if (message == null) {
			throw new IOException(e.getCause());
		}
		else {
		    throw new IOException(e.getCause().getMessage());
		}
	    }
	    else {
		throw new IOException(e.getMessage());
	    }
	}

	String result = null;
	if (resultObj != null) {
	    result = resultObj.toString();
	}

	debug("result before HTML conversion: " + result);

	if (result != null) {

	    if (isFileResult(methodName)) {
		result = ReturnFileFormatter.format(fileConfigurator, username,
			result);
	    }

	    result = HtmlConverter.toHtml(result);
	}
	
	// Special cases for root access
	result = treatRootCase(methodName, result, clientFilename);

	writeLine(out, TransferStatus.SEND_OK);
	writeLine(out, result);
    }

    /**
     * Treat special cases if client filename is "/"
     * @param methodName 	
     * @param result 
     * @param clientFilename
     */
    private String treatRootCase(String methodName, String result,
	    String clientFilename) {
	// getName:
	// getParent: null
	// getParentFile: null
	
	if (clientFilename.equals("/")) {
	    if (methodName.equals("getName"))
		result = "";
	    if (methodName.equals("getParent"))
		result = null;
	    if (methodName.equals("getParentFile"))
		result = null;
	}

	return result;
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
	out.write((s + StringUtil.CR_LF).getBytes());
    }

    /**
     * Says if a method returns a File name or path
     * 
     * @param method
     * @return if the method returns a File name or path
     */
    private boolean isFileResult(String method) {
	if (method.startsWith("getAbsolute"))
	    return true;
	else if (method.startsWith("getCanonical"))
	    return true;
	else if (method.startsWith("getParent"))
	    return true;
	else if (method.startsWith("getPath"))
	    return true;
	else if (method.startsWith("toURI")) // Not used anymore
	    return true;
	else if (method.startsWith("toURL")) // Not used anymore
	    return true;
	else
	    return false;
    }

    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
