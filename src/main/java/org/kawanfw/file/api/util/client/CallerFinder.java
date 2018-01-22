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
package org.kawanfw.file.api.util.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;

/**
 * getCallerCallerClassName() :
 * Tribute to: http://stackoverflow.com/users/263525/dystroy in:
 * http://stackoverflow.com/questions/11306811/how-to-get-the-caller-class-in-java
 * 
 * 
 */

public class CallerFinder {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(CallerFinder.class);

    /**
     * Proctected constructor
     */
    protected CallerFinder() {
    }

    /**
     * Allows to find the name of the class that calls this method
     * 
     * @return the name of the class that calls ths method
     */
    public static String getCallerCallerClassName() {
	StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
	String callerClassName = null;
	for (int i = 1; i < stElements.length; i++) {
	    StackTraceElement ste = stElements[i];
	    if (!ste.getClassName().equals(CallerFinder.class.getName())
		    && ste.getClassName().indexOf("java.lang.Thread") != 0) {
		if (callerClassName == null) {
		    callerClassName = ste.getClassName();
		} else if (!callerClassName.equals(ste.getClassName())) {    
		    return ste.getClassName();
		}
	    }
	}
	return null;
    }

    /**
     * Returns the stack of the classes that called this method
     * 
     * @return the stack of the classes that called this method
     */
    public static List<String> getCallStack() {
	StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
	String callerClassName = null;
	List<String> stack = new ArrayList<String>();
	for (int i = 1; i < stElements.length; i++) {
	    StackTraceElement ste = stElements[i];
	    callerClassName = ste.getClassName();
	    debug("callerClassName: " + callerClassName);
	    stack.add(callerClassName);
	}
	return stack;
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
