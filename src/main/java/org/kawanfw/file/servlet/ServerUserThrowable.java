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

import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.server.FileConfigurator;

/**
 * @author Nicolas de Pomereu
 * 
 *         Universal Exception message when a use class (configurator) throws an
 *         Exception
 */

public class ServerUserThrowable {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerUserThrowable.class);

    /**
     * Protected constructor
     */
    protected ServerUserThrowable() {

    }

    /**
     * Generate the error message.
     * 
     * @param fileConfigurator
     *            the user File Configurator
     * @param method
     *            the method concerned
     * @return the error message
     */
    public static String getErrorMessage(
	    FileConfigurator fileConfigurator, String method) {
	String key = fileConfigurator.getClass().getName();

	String errorMessage = Tag.PRODUCT_USER_CONFIG_FAIL + " The " + key + "."
		+ method + "() method threw an Exception.";
	return errorMessage;
    }

    /**
     * Return Exception.getMessage() if the Exception is not thrown by a
     * Configurator, else return Tag.PRODUCT_USER_CONFIG_FAIL +
     * Exception.getMessage()
     * 
     * @param throwable
     *            the Throwable thrown
     * @return the wrapped/modifier exception
     */
    public static String getMessage(Throwable throwable) {

	String UserConfigFailureMessage = "";
	if (ServerUserThrowable.isThrowingClassConfigurator(throwable)) {
	    UserConfigFailureMessage = Tag.PRODUCT_USER_CONFIG_FAIL;

	    Class<?> theClass = ServerUserThrowable
		    .extractThrowingClassFromThrowable(throwable);

	    if (theClass != null) {
		UserConfigFailureMessage += " in " + theClass.getName();
		String methodName = ServerUserThrowable
			.extractThrowingMethodNameFromException(throwable);
		UserConfigFailureMessage += "." + methodName + ": ";
	    }

	}

	return UserConfigFailureMessage + throwable.getMessage();

    }

    /**
     * Says, for an Exception thrown, if the class that throws the Exception is
     * an Kawansoft framework Configurator
     * 
     * @param e
     *            the Exception thrown
     * @return thrue if the class that throws the Exception is a Kawansoft framework
     *         Configurator
     */
    private static boolean isThrowingClassConfigurator(Throwable e) {

	debug("e.toString():  " + e.toString());

	Class<?> theClass = ServerUserThrowable
		.extractThrowingClassFromThrowable(e);

	if (theClass != null) {
	    debug("theClass: " + theClass.getName());
	}

	if (theClass == null) {
	    return false;
	}

	boolean isAConfigurator = DoesClassImplementsAConfigurator(theClass);

	if (isAConfigurator) {
	    return true;
	}

	// maybe there is Default parent Configurator. TestReload if it's implement a
	// Configurator
	Class<?> cSuper = theClass.getSuperclass();

	isAConfigurator = DoesClassImplementsAConfigurator(cSuper);
	return isAConfigurator;

    }

    /**
     * Says if a class is a Configurator
     * 
     * @param theClass
     * @return
     */
    private static boolean DoesClassImplementsAConfigurator(Class<?> theClass) {
	boolean isAConfigurator = false;

	if (theClass != null) {
	    Class<?>[] interfaces = theClass.getInterfaces();

	    if (interfaces == null) {
		return isAConfigurator;
	    }

	    for (int i = 0; i < interfaces.length; i++) {
		Class<?> theInterface = interfaces[i];
		String interfaceName = theInterface.getName();

		debug("interfaceName: " + interfaceName);

		if (interfaceName
			.equals("org.kawanfw.file.api.server.SessionConfigurator")
			|| interfaceName
				.equals("org.kawanfw.file.api.server.FileConfigurator")
		) {
		    isAConfigurator = true;
		    break;
		}
	    }
	}

	return isAConfigurator;
    }

    /**
     * Return the class where the Exception has been thrown
     * 
     * @param throwable
     *            the exception thrown
     * @return the class that throws the Exception
     */
    private static Class<?> extractThrowingClassFromThrowable(Throwable throwable) {

	try {
	    String statckTrace = ExceptionUtils.getStackTrace(throwable);
	    String className = StringUtils.substringAfter(statckTrace, "at ");
	    className = StringUtils.substringBefore(className, "(");
	    className = StringUtils.substringBeforeLast(className, ".");

	    debug("className: " + ":" + className + ":");

	    Class<?> theClass = Class.forName(className);
	    return theClass;
	} catch (ClassNotFoundException e1) {
	    e1.printStackTrace(System.out);
	    return null;
	}

    }

    /**
     * Return the class where the Exception has been thrown
     * 
     * @param e
     *            the exception thrown
     * @return the class that throws the Exception
     */
    private static String extractThrowingMethodNameFromException(Throwable e) {

	String statckTrace = ExceptionUtils.getStackTrace(e);
	String className = StringUtils.substringAfter(statckTrace, "at ");
	className = StringUtils.substringBefore(className, "(");

	String methodName = StringUtils.substringAfterLast(className, ".");

	debug("className : " + ":" + className + ":");
	debug("methodName: " + ":" + methodName + ":");

	return methodName;

    }

    private static void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
