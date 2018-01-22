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
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;

/**
 * 
 * Format the produced File for return on client side:
 * <ul>
 * <li>Pass from Windows to Unix notation.</li>
 * <li>Remove username from name.</li>
 * </ul>
 * @author Nicolas de Pomereu
 *
 */
public class ReturnFileFormatter {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileMethodOneReturnAction.class);
    
    protected ReturnFileFormatter() {

    }

    /**
     * format the filename of a File returned by the File method
     * 
     * @param fileConfigurator
     * @param username
     * @param result
     * @return
     */
    public static String format(FileConfigurator fileConfigurator,
	    String username, String result) {
	
	if (result == null) {
	    return null;
	}
	    
	File homeDirFile = fileConfigurator.getHomeDir(username);
	
	// Direct access to file system
	if (HttpConfigurationUtil.isFileDirSystemRootDir(homeDirFile)) {
	    result = removeWindowsRoot(result);

	    if (!result.startsWith("/")) {
		result = "/" + result;
	    }
	    result = result.replace("\\", "/");
	    return result;
	}
	

	// We have a server root
	String homeDirStr = fileConfigurator.getHomeDir(username).toString();

//      NO! can happen if parent asked!
	
//	if (!result.startsWith(headerServeroot)) {
//	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL
//		    + " file name should start with  " + headerServeroot
//		    + " but does not: " + result);
//	}

	// Remove the header
	result = StringUtils.substringAfter(result, homeDirStr);

	debug("Before replace: " + result);

	// Replace all \ by /
	result = result.replace("\\", "/");

	debug("After replace \\ by /: " + result);

	/*
	if (fileConfigurator.useOneRootPerUsername()) {
	    // /username/
	    if (result.contains("/" + username)) {
		result = StringUtils.replace(result, "/" + username, "");
	    }
	}
	*/

	if (!result.startsWith("/")) {
	    result = "/" + result;
	}
	
	debug("After remove username and / add at head: " + result);

	return result;
    }    
    
    /**
     * Remove the c:\\ or a,b,.. z:\\ root from a Windows filename
     * 
     * @param filename
     * @return
     */
    public static String removeWindowsRoot(String filename) {
	if (SystemUtils.IS_OS_WINDOWS) {

	    if (filename.contains(":\\") && filename.length() >= 3) {
		filename = filename.substring(3);
	    }

	}
	return filename;
    }
    
    private static void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }
    
}
