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
import java.io.PrintWriter;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.api.server.session.SessionConfigurator;
import org.kawanfw.file.util.parms.Parameter;
import org.kawanfw.file.util.parms.ReturnCode;

/**
 * @author Nicolas de Pomereu
 * 
 *         The method executeRequest() is to to be called from the
 *         ServerClientLogin Servlet and Class. <br>
 *         It will execute a client side request with a
 *         ServerCaller.httpsLogin()
 * 
 */

public class ServerLoginAction extends HttpServlet {
    private static boolean DEBUG = FrameworkDebug.isSet(ServerLoginAction.class);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // A space
    public static final String SPACE = " ";

    /**
     * Constructor
     */
    public ServerLoginAction() {

    }

    /**
     * 
     * Execute the login request asked by the main File Servlet
     * 
     * @param request
     *            the http request
     * @param response
     *            the http response
     * @param fileConfigurator
     *            the Commons Client login specific class
     * @param sessionConfigurator TODO
     * @param action
     *            the login action: BEFORE_LOGIN_ACTION or LOGIN_ACTION
     * @throws IOException
     *             if any Servlet Exception occurs
     */
    public void executeAction(HttpServletRequest request,
	    HttpServletResponse response,
	    FileConfigurator fileConfigurator, SessionConfigurator sessionConfigurator, String action)
	    throws IOException {
	PrintWriter out = response.getWriter();

	try {
	    response.setContentType("text/html");

	    debug("before request.getParameter(Parameter.LOGIN);");

	    String username = request.getParameter(Parameter.USERNAME);
	    username = username.trim();

	    String password = request.getParameter(Parameter.PASSWORD);
	    password = password.trim();

	    // User must provide a user
	    if (username.length() < 1) {
		debug("username.length() < 1!");
		// No login transmitted
		// Redirect to ClientLogin with error message.
		out.println(TransferStatus.SEND_OK);
		out.println(ReturnCode.INVALID_LOGIN_OR_PASSWORD);
		return;
	    }

	    // Check the IP. Refuse access if IP is banned/blacklisted
	    String ipAddress = request.getRemoteAddr();

	    debug("calling checkLoginAndPassword");

	    boolean isOk = fileConfigurator.login(username,
	    	    password.toCharArray(), ipAddress);

	    debug("login isOk: " + isOk + " (login: " + username + ")");

	    if (!isOk) {
		debug("login: invalid login or password");

		// Reduce the login speed
		LoginSpeedReducer loginSpeedReducer = new LoginSpeedReducer(
			username);
		loginSpeedReducer.checkAttempts();

		out.println(TransferStatus.SEND_OK);
		out.println(ReturnCode.INVALID_LOGIN_OR_PASSWORD);
		return;
	    }

	    debug("Login done!");

	    String token = sessionConfigurator.generateToken(username);

	    // out.println(HttpTransfer.SEND_OK + SPACE + ReturnCode.OK + SPACE
	    // + token);
	    out.println(TransferStatus.SEND_OK);
	    out.println(ReturnCode.OK + SPACE + token);

	} catch (Exception e) {

	    out.println(TransferStatus.SEND_FAILED);
	    out.println(e.getClass().getName());
	    out.println(ServerUserThrowable.getMessage(e));
	    out.println(ExceptionUtils.getStackTrace(e)); // stack trace

	    try {
		ServerLogger.getLogger().log(Level.WARNING, Tag.PRODUCT_EXCEPTION_RAISED + " "
			+ ServerUserThrowable.getMessage(e));
		ServerLogger.getLogger().log(Level.WARNING, Tag.PRODUCT_EXCEPTION_RAISED + " "
			+ ExceptionUtils.getStackTrace(e));
	    } catch (Exception e1) {
		e1.printStackTrace();
		e1.printStackTrace(System.out);
	    }

	}
    }

    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }
}
