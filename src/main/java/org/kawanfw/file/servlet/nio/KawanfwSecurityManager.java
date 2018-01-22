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

import java.net.InetAddress;
import java.security.Permission;
import java.util.logging.Level;

import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.reflection.ReflectionUtil;

/**
 * 
 * Create a sandbox for file write/delete.
 * <p>
 * Necessary because of FileFilter & FilenameFilter execution that could be
 * dangerous if hacker used a regular (username, password) to add file
 * write/delete code in filters accept() method.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class KawanfwSecurityManager extends SecurityManager {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(KawanfwSecurityManager.class);

    /**
     * Constructor
     */
    public KawanfwSecurityManager() {
    }

    /**
     * Check that the file accessed fro write/delete is NOT coded in a
     * FileFilter or FilenameFilter instance.
     */
    private void refuseAccessIfFilter() {

	boolean invalidCall = false;

	debug("BEFORE isFilterInStack");

	StackTraceElement[] stackTraceElements = Thread.currentThread()
		.getStackTrace();
	String caller = "unknown";
	if (stackTraceElements.length > 2) {
	    caller = stackTraceElements[2].getMethodName();
	}

	Class<?> [] classes = super.getClassContext();
	invalidCall = isFilterInStack(classes);
	
	if (invalidCall) {
	    debug("BEFORE throw new SecurityException");
	    throw new SecurityException(
		    "Operation not authorized in FileFilter and FilenameFilter ["
			    + caller + "]");
	}
    }
    
    /**
     * Says if the stack contains a FileFilter or FilenameFilter
     * @param classes	the classes stack
     * @return true if the stack contains a FileFilter or FilenameFilter
     */
    public static boolean isFilterInStack(Class<?>[] classes) {

	for (Class<?> clazz : classes) {
	    boolean isFilter = ReflectionUtil.isClassFileFilterOrFilenameFilter(clazz);
	    if (isFilter) {
		return true;
	    }
	}
	
	return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
     */
    @Override
    public void checkPermission(Permission perm) {
	// Allow all
    }

    
    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkPermission(java.security.Permission, java.lang.Object)
     */
    @Override
    public void checkPermission(Permission perm, Object context) {
	// Allow all
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkRead(java.lang.String)
     */
    @Override
    public void checkRead(String file) {
	//super.checkRead(file);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkWrite(java.lang.String)
     */
    @Override
    public void checkWrite(String file) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkDelete(java.lang.String)
     */
    @Override
    public void checkDelete(String file) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkCreateClassLoader()
     */
    @Override
    public void checkCreateClassLoader() {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkExec(java.lang.String)
     */
    @Override
    public void checkExec(String cmd) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @Override
    public void checkLink(String lib) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkConnect(java.lang.String, int)
     */
    @Override
    public void checkConnect(String host, int port) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkConnect(java.lang.String, int,
     * java.lang.Object)
     */
    @Override
    public void checkConnect(String host, int port, Object context) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkListen(int)
     */
    @Override
    public void checkListen(int port) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkAccept(java.lang.String, int)
     */
    @Override
    public void checkAccept(String host, int port) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress)
     */
    @Override
    public void checkMulticast(InetAddress maddr) {
	refuseAccessIfFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress, byte)
     */
    @Override
    public void checkMulticast(InetAddress maddr, byte ttl) {
	refuseAccessIfFilter();
    }

    
    
    /**
     * debug tool
     */
    public static void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
