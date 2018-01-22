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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.DefaultParms;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.DefaultFileConfigurator;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.api.server.session.DefaultJwtSessionConfigurator;
import org.kawanfw.file.api.server.session.JwtSessionStore;
import org.kawanfw.file.api.server.session.SessionConfigurator;
import org.kawanfw.file.reflection.ClassPathUtil;
import org.kawanfw.file.servlet.convert.HttpServletRequestConvertor;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;
import org.kawanfw.file.version.FileVersion;

/**
 * The main KRemote Files Manager Servlet <br>
 * 
 * @author Nicolas de Pomereu
 */

@SuppressWarnings("serial")
public class ServerFileManager extends HttpServlet {

    private static final String SPACES_3 = "   ";

    public static String CR_LF = System.getProperty("line.separator");

    public static final String FILE_CONFIGURATOR_CLASS_NAME = "fileConfiguratorClassName";
    public static final String SESSIONS_CONFIGURATOR_CLASS_NAME = "sessionConfiguratorClassName";

    private FileConfigurator fileConfigurator = null;
    private SessionConfigurator sessionConfigurator = null;
    
    /** The init error message trapped */
    private String initErrrorMesage = null;
    
    /** The Exception thrown at init */
    private Exception exception = null;
    
    /**
     * Init.
     */

    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	// Clean the olders folders of uploader classes, can't be done when closing
	String classpathUsernames = ClassPathUtil.getUserHomeDotKawansoftDotClasspath() + File.separator + ".usernames";
	try {
	    FileUtils.deleteDirectory(new File(classpathUsernames));
	    new File(classpathUsernames).delete();
	} catch (IOException ignore) {
	    ignore.printStackTrace(System.out);
	}
	
	// Variable use to store the current name when loading, used to
	// detail
	// the exception in the catch clauses
	String classNameToLoad = null;

	String fileConfiguratorClassName = config
		.getInitParameter(FILE_CONFIGURATOR_CLASS_NAME);

	String sessionConfiguratorClassName = config
		.getInitParameter(SESSIONS_CONFIGURATOR_CLASS_NAME);
	
	try {

	    if (fileConfiguratorClassName == null
		    || fileConfiguratorClassName.isEmpty()) {
		String capitalized = StringUtils
			.capitalize(FILE_CONFIGURATOR_CLASS_NAME);
		fileConfiguratorClassName = config
			.getInitParameter(capitalized);
	    }
	    
	    if (sessionConfiguratorClassName == null
		    || sessionConfiguratorClassName.isEmpty()) {
		String capitalized = StringUtils
			.capitalize(SESSIONS_CONFIGURATOR_CLASS_NAME);
		sessionConfiguratorClassName = config
			.getInitParameter(capitalized);
	    }
	   
	    // Load the JWT secret value if necessary
	    String jwtSecretValue = config.getInitParameter("jwt_secret_value");
	    if (jwtSecretValue != null && ! jwtSecretValue.isEmpty()) {
		JwtSessionStore.JWT_SECRET_VALUE = jwtSecretValue;
	    }
	   
	    // Call the specific FILE Configurator class to use

	    classNameToLoad = fileConfiguratorClassName;
	    if (fileConfiguratorClassName != null
		    && !fileConfiguratorClassName.isEmpty()) {
		Class<?> c = Class.forName(fileConfiguratorClassName);
		
		//fileConfigurator = (FileConfigurator) c.newInstance();
		Constructor<?> constructor = c.getConstructor();
		fileConfigurator = (FileConfigurator) constructor
			.newInstance();
		
	    } else {
		fileConfigurator = new DefaultFileConfigurator();
	    }
	    
	    classNameToLoad = sessionConfiguratorClassName;
	    if (sessionConfiguratorClassName != null
		    && !sessionConfiguratorClassName.isEmpty()) {
		Class<?> c = Class.forName(sessionConfiguratorClassName);
		
		//sessionConfigurator = (SessionConfigurator) c.newInstance();
		Constructor<?> constructor = c.getConstructor();
		sessionConfigurator = (SessionConfigurator) constructor
			.newInstance();
		
	    } else {
		sessionConfigurator = new DefaultJwtSessionConfigurator();
	    }
	    
	    // Immediately create the Logger
	    Logger logger = null;
	    try {
		logger = fileConfigurator.getLogger();
		ServerLogger.createLogger(logger);
		ServerLogger.getLogger().log(Level.WARNING, "Starting " + FileVersion.PRODUCT.NAME + "...");	
	    } catch (Exception e) {
		initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
			+ " Impossible to create the Logger: " + logger;
		exception = e;
	    }
	    

	} catch (ClassNotFoundException e) {
	    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (ClassNotFoundException) Configurator class: "
		    + classNameToLoad;
		exception = e;
	} catch (InstantiationException e) {
	    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (InstantiationException) Configurator class: "
		    + classNameToLoad;
		exception = e;
	} catch (IllegalAccessException e) {
	    initErrrorMesage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " Impossible to load (IllegalAccessException) Configurator class: "
		    + classNameToLoad;
		exception = e;
	} catch (Exception e) {
	    initErrrorMesage = Tag.PRODUCT_PRODUCT_FAIL + " Please contact support support@kawansoft.com";
	    exception = e;
	}
	
	
	if (fileConfigurator == null) {
	    fileConfiguratorClassName = FILE_CONFIGURATOR_CLASS_NAME;
	} else {
	    fileConfiguratorClassName = fileConfigurator.getClass()
		    .getName();
	}
	
	System.out.println();
	System.out.println(Tag.PRODUCT_START + " "
		+ org.kawanfw.file.version.FileVersion.getVersion());
	System.out.println(Tag.PRODUCT_START + " "+ this.getServletName()
		+ " Servlet:");
	System.out.println(Tag.PRODUCT_START
		+ " - Init Parameter fileConfiguratorClassName   : "  + CR_LF + Tag.PRODUCT_START  + SPACES_3
		+ fileConfiguratorClassName);
	System.out.println(Tag.PRODUCT_START
		+ " - Init Parameter sessionConfiguratorClassName: "  + CR_LF + Tag.PRODUCT_START  + SPACES_3
		+ sessionConfiguratorClassName);

	if (exception == null) {
	    System.out.println(Tag.PRODUCT_START + " " + FileVersion.PRODUCT.NAME +  " Configurator Status: OK.");
	    System.out.println();
	}
	else {
	    System.out.println(Tag.PRODUCT_START + " " + FileVersion.PRODUCT.NAME  +  " Configurator Status: KO.");
	    System.out.println(initErrrorMesage);
	    System.out.println( ExceptionUtils.getStackTrace(exception));	    
	}
		
    }

    /**
     * TestReload the configurators main methods to see if they throw Exceptions
     */
    private void testConfiguratorMethods() {
	if (exception == null) {
	    // TestReload that the login method does not throw an Exception
	    @SuppressWarnings("unused")
	    boolean isOk = false;

	    try {
		isOk = fileConfigurator.login("dummy",
			"dummy".toCharArray(), "127.0.0.1");
	    } catch (Exception e) {
		initErrrorMesage = ServerUserThrowable.getErrorMessage(fileConfigurator, "login");
		exception = e;
	    }
	}
		
	if (exception == null) {
	    try {
		File homeDir = fileConfigurator.getHomeDir("username");
		HttpConfigurationUtil.testHomeDirValidity(homeDir);
	    } catch (Exception e) {
		initErrrorMesage = ServerUserThrowable.getErrorMessage(
			fileConfigurator, "getServerRoot");
		exception = e;
	    }	    
	}
	
    }

    /**
     * Post request.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws IOException {
	
	request.setCharacterEncoding("UTF-8");
	
	// If init fail, say it cleanly client, instead of bad 500 Servlet Error
	if (exception != null) {
	    OutputStream out = response.getOutputStream();

	    writeLine(out, TransferStatus.SEND_FAILED);
	    writeLine(out, exception.getClass().getName()); // Exception class name
	    writeLine(out, initErrrorMesage + " Reason: " + exception.getMessage()); // Exception message
	    writeLine(out, ExceptionUtils.getStackTrace(exception)); // stack trace
	    return;

	}
	
	// Configure a repository (to ensure a secure temp location is used)
	ServletContext servletContext = this.getServletConfig()
		.getServletContext();
	File servletContextTempDir = (File) servletContext
		.getAttribute("javax.servlet.context.tempdir");
	    	
	// Wrap the HttpServletRequest with HttpServletRequestEncrypted for
	// parameters HTML conversion (& futur decryption)
	HttpServletRequestConvertor requestConverted = new HttpServletRequestConvertor(
		request, fileConfigurator);	

	ServerFileDispatch dispatch = new ServerFileDispatch();
	dispatch.executeRequest(requestConverted, response,
		servletContextTempDir, sessionConfigurator,
		fileConfigurator);
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
     * Get request.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException {
	
	//request.setCharacterEncoding("UTF-8");
	
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	String status = "</font><font face=\"Arial\" color=\"green\">"
		+ "OK & Running.";

	// TestReload all methods
	testConfiguratorMethods();
	
	if (exception != null) {
	    
	    String stackTrace = ExceptionUtils.getStackTrace(exception);
	    
	    BufferedReader bufferedReader = new BufferedReader(
		    new StringReader(stackTrace));
	    StringBuffer sb = new StringBuffer();

	    String line = null;
	    while ((line = bufferedReader.readLine()) != null) {
		// All subsequent lines contain the result
		sb.append(line);
		sb.append("<br>");
	    }
	    	    	    
	    status = "</font><font face=\"Arial\" color=\"red\">"
		    + initErrrorMesage + "<br>"
		    + sb.toString();
	}


	String fileConfiguratorClassName = null;
	if (fileConfigurator == null) {
	    fileConfiguratorClassName = FILE_CONFIGURATOR_CLASS_NAME;
	} else {
	    fileConfiguratorClassName = fileConfigurator.getClass()
		    .getName();
	}
	
	String sessionConfiguratorClassName = null;
	if (sessionConfigurator == null) {
	    sessionConfiguratorClassName = SESSIONS_CONFIGURATOR_CLASS_NAME;
	} else {
	    sessionConfiguratorClassName = sessionConfigurator.getClass()
		    .getName();
	}
	
	out.println("<font face=\"Arial\">");
	out.println("<b>");
	out.println("<font color=\"#" + DefaultParms.KAWANSOFT_COLOR +"\">" + FileVersion.getVersion() + "</font>");
	out.println("<br>");
	out.println("<br>");	
	out.println(this.getServletName() + " Servlet Configuration");
	out.println("</b>");
	

	out.println("<br><br>");
	out.println("<table cellpadding=\"3\" border=\"1\">");

	out.println("<tr>");
	out.println("<td align=\"center\"> <b>Configurator Parameter</b> </td>");
	out.println("<td align=\"center\"> <b>Configurator Value</b> </td>");
	out.println("</tr>");

	out.println("<tr>");
	out.println("<td> " + FILE_CONFIGURATOR_CLASS_NAME + "</td>");
	out.println("<td> " + fileConfiguratorClassName + "</td>");
	out.println("</tr>");
	
	out.println("<tr>");
	out.println("<td> " + SESSIONS_CONFIGURATOR_CLASS_NAME + "</td>");
	out.println("<td> " + sessionConfiguratorClassName + "</td>");
	out.println("</tr>");

	out.println("</table>");

	out.println("<br><br>");
	out.println("<table cellpadding=\"3\" border=\"1\">");
	out.println("<tr>");
	out.println("<td align=\"center\"> <b>" + FileVersion.PRODUCT.NAME + " Configuration Status</b> </td>");
	out.println("</tr>");
	out.println("<tr>");
	out.println("<td> " + status + "</td>");
	out.println("</tr>");
	out.println("</table>");
	out.println("</font>");

    }

}
