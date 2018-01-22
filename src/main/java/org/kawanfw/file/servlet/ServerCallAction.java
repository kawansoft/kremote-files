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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.json.ListOfStringTransport;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.JavaValueBuilder;
import org.kawanfw.commons.util.StringUtil;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.servlet.util.CallUtil;
import org.kawanfw.file.util.parms.Action;
import org.kawanfw.file.util.parms.Parameter;

/**
 * @author Nicolas de Pomereu
 * 
 *         Executes the client call() action.
 */
public class ServerCallAction {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerCallAction.class);

    /**
     * Constructor
     */
    public ServerCallAction() {
    }

    /**
     * 
     * Calls a remote method from the client side <br>
     * Please note that all invocation are trapped and routed as code string to
     * the client side.
     * 
     * @param request
     *            the http request
     * @param fileConfigurator
     *            the file configurator defined by the user
     * @param out
     *            the servlet output stream
     * @param username
     *            the client login (for security check)
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
	    OutputStream out, String username) throws SQLException, IOException,
	    ClassNotFoundException, InstantiationException,
	    IllegalAccessException, NoSuchMethodException,
	    IllegalArgumentException, InvocationTargetException, Exception {

	// Connection connection = null;

	debug("in actionCall");

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

	String className = StringUtils.substringBeforeLast(methodName, ".");
	Class<?> c = Class.forName(className);
	CallUtil callUtil = new CallUtil(c, fileConfigurator, username);
	boolean callAllowed = callUtil.isCallable();

	if (!callAllowed) {
	    throw new SecurityException(Tag.PRODUCT_SECURITY
		    + " Class is forbiden for remote call: " + className);
	}

	String action = request.getParameter(Parameter.ACTION);

	// Legacy Action.CALL_ACTION call with Base64 conversion
	// Corresponds to RemoteSession.setUseBase64EncodingForCall()
	// setting
	// on client side
	if (action.equals(Action.CALL_ACTION)) {
	    paramsTypes = StringUtil.fromBase64(paramsTypes);
	    paramsValues = StringUtil.fromBase64(paramsValues);
	}

	debug("paramsTypes     : " + paramsTypes);
	debug("paramsValues    : " + paramsValues);

	List<String> listParamsTypes = ListOfStringTransport
		.fromJson(paramsTypes);
	List<String> listParamsValues = ListOfStringTransport
		.fromJson(paramsValues);

	debug("actionInvokeRemoteMethod:listParamsTypes      : "
		+ listParamsTypes);
	debug("actionInvokeRemoteMethod:listParamsValues     : "
		+ listParamsValues);

	Class<?>[] argTypes = new Class[listParamsTypes.size()];
	Object[] values = new Object[listParamsValues.size()];

	List<Object> valuesList = new Vector<Object>();
	for (int i = 0; i < listParamsTypes.size(); i++) {

	    String value = listParamsValues.get(i);
	    String javaType = listParamsTypes.get(i);

	    JavaValueBuilder javaValueBuilder = new JavaValueBuilder(javaType,
		    value, request);
	    argTypes[i] = javaValueBuilder.getClassOfValue();
	    values[i] = javaValueBuilder.getValue();

	    // // Special treatment if argTypes[i] is a Connection
	    // if (argTypes[i] == Connection.class) {
	    // connection = commonsConfigurator.getConnection();
	    // values[i] = connection;
	    // }

	    // Special treatment if argTypes[i] is a LoggedUsername
//	    if (argTypes[i] == org.kawanfw.file.api.server.LoggedUsername.class) {
//		LoggedUsername usernameObject = new LoggedUsername(username);
//		values[i] = usernameObject;
//	    }

	    valuesList.add(values[i]);
	}

	// // Try to get A connection. Will be null if user has not configured a
	// Connection
	// try {
	// if (connection == null) {
	// connection = commonsConfigurator.getConnection();
	// }
	//
	// } catch (Exception e) {
	// debug("commonsConfigurator.getConnection() exception: " +
	// e.toString());
	// if (connection != null) connection.close();
	// connection = null;
	// }

	/*
	 * boolean isAllowed = fileConfigurator.allowCallAfterAnalysis(
	 * username, connection, methodName, valuesList);
	 * 
	 * if (!isAllowed) {
	 * 
	 * String ipAddress = request.getRemoteAddr();
	 * 
	 * // Run the runIfCallDisallowed() configured by the user
	 * fileConfigurator.runIfCallRefused(username, connection, ipAddress,
	 * methodName, valuesList);
	 * 
	 * throw new SecurityException( Tag.PRODUCT_SECURITY +
	 * " Method not authorized for execution by Security Checker: " +
	 * methodName + " parameters: " + valuesList.toString()); }
	 */

	String rawMethodName = StringUtils.substringAfterLast(methodName, ".");

	// Invoke the method
	Object resultObj = null;

	debug("Before  Object theObject = c.newInstance()");
	
	//Object theObject = c.newInstance();
	Constructor<?> constructor = c.getConstructor();
	Object theObject = constructor.newInstance();

	debug("Before  c.getDeclaredMethod(rawMethodName, argTypes)");
	Method main = c.getDeclaredMethod(rawMethodName, argTypes);

	debug("Before  main.invoke(theObject, values)");
	resultObj = main.invoke(theObject, values);

	String result = null;
	if (resultObj != null)
	    result = resultObj.toString();

	debug("result before conversion: " + result);

	if (result != null) {

	    // Legacy Action.CALL_ACTION call with Base64 conversion
	    // Corresponds to RemoteSession.setUseBase64EncodingForCall()
	    // setting on client side
	    if (action.equals(Action.CALL_ACTION)) {
		result = StringUtil.toBase64(result);
	    } else if (action.equals(Action.CALL_ACTION_HTML_ENCODED)) {
		result = HtmlConverter.toHtml(result);
	    } else {
		throw new IllegalArgumentException(
			"call action is invalid: " + action);
	    }
	}

	debug("actionInvokeRemoteMethod:result: " + result);

	writeLine(out, TransferStatus.SEND_OK);
	writeLine(out, result);
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

    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
