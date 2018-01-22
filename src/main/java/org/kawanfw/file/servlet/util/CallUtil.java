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
package org.kawanfw.file.servlet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.logging.Level;

import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.reflection.ClassFileLocatorNew;
import org.kawanfw.file.reflection.ClassPathUtil;

/**
 * @author Nicolas de Pomereu
 * 
 */
public class CallUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(CallUtil.class);

    /** The class to analyze */
    private Class<?> clazz = null;

    private FileConfigurator fileConfigurator = null;

    private String username;

    /**
     * Constructor
     * 
     * @param clazz
     *            the class to analyze
     * @param fileConfigurator
     *            the file configurator - to test if the class is in server root
     * @param username 
     * 		the client username
     */
    public CallUtil(Class<?> clazz, FileConfigurator fileConfigurator, String username) {
	this.clazz = clazz;
	this.fileConfigurator = fileConfigurator;
	this.username = username;
    }

    /**
     * Returns true if the class is callable with authentication
     * 
     * @return true if the class is calable with authentication from client side
     * 
     */
    public boolean isCallable() throws IOException, FileNotFoundException {

	debug("");
	debug("Before classFileLocator.getClassFile() " + clazz.getName());
	// Class must not be in user root
	ClassFileLocatorNew classFileLocator = new ClassFileLocatorNew(clazz,
		null);
	File file = classFileLocator.getContainerFile();
	debug("classFileLocator.getContainerFile(): " + file);

	debug("Before String serverRoot = fileConfigurator.getHomeDir(username).toString()");
	
	File homeDirFile = fileConfigurator.getHomeDir(username);
	
	// Case home dir is not root dir / or c:\:
	// Prevent user attack by loading is own classes.  
	if (! HttpConfigurationUtil.isFileDirSystemRootDir(homeDirFile)) {
	    String homeDirStr = homeDirFile.toString();
	    if (file.toString().toLowerCase()
		    .startsWith(homeDirStr.toLowerCase())) {
		throw new SecurityException(Tag.PRODUCT_SECURITY
			+ " Call of classes located inside the " + Tag.PRODUCT
			+ " server path (fileConfigurator.getHomeDir()) are not authorized.");
	    }

	    debug("Before String kawansoftRoot = ClassPathUtil.getUserHomeDotKawansoftDotClasspath()");
	    String kawansoftRoot = ClassPathUtil
		    .getUserHomeDotKawansoftDotClasspath();
	    if (file.toString().toLowerCase()
		    .startsWith(kawansoftRoot.toLowerCase())) {
		throw new SecurityException(Tag.PRODUCT_SECURITY
			+ " Call of classes located in user.home/.kawansoft/.classpath path are not authorized.");
	    }
	}

	debug("Before isCallableNotAuthenticated");

	// If it's callable without any authentication, it's OK:
	if (isCallableNotAuthenticated()) {
	    return true;
	}

	boolean callAllowed = false;

	debug("Before Type[] interfaces = clazz.getGenericInterfaces()");

	Type[] interfaces = clazz.getGenericInterfaces();
	if (interfaces.length != 0) {
	    for (Type theInterface : interfaces) {
		// Call is allowed if the class implements the
		// org.kawanfw.file.api.server.ClientCallable
		if (theInterface.toString().endsWith(
			org.kawanfw.file.api.server.ClientCallable.class
				.getName())) {
		    callAllowed = true;
		    break;
		}
	    }
	}

	debug("Before return callAllowed");
	return callAllowed;
    }

    /**
     * Returns true if the class is callable with authentication
     * 
     * @return true if the class is callable with authentication from client
     *         side
     * 
     */
    public boolean isCallableNotAuthenticated() {
	boolean callAllowed = false;

	Type[] interfaces = clazz.getGenericInterfaces();
	if (interfaces.length != 0) {
	    for (Type theInterface : interfaces) {
		// Call is allowed if the class implements the
		// org.kawanfw.file.api.server.ClientCallableNotAuthenticated
		// interface
		if (theInterface.toString().endsWith(
			org.kawanfw.file.api.server.ClientCallableNoAuth.class
				.getName())) {
		    callAllowed = true;
		}
	    }
	}
	return callAllowed;
    }

    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
