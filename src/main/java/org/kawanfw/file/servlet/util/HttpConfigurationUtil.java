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

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.server.FileConfigurator;

/**
 * 
 * Utility class to use with <code>FileConfigurator</code> implementation
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 * 
 */
public class HttpConfigurationUtil {

    /**
     * Protected constructor
     */
    protected HttpConfigurationUtil() {

    }

    /**
     * Adds the root path to the beginning of path for a remote file.&nbsp;
     * <p>
     * If the server runs on Windows: the method will convert the "/" file
     * separator to "\" if necessary.
     * 
     * @param fileConfigurator
     *            the user http configuration
     * @param username
     *            the client username
     * @param filename
     *            the filename path
     * 
     * @return the new filename path, prefixed with the root path
     */
    public static String addUserHomePath(FileConfigurator fileConfigurator,
	    String username, String filename) throws IOException {
	if (filename == null) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT_PRODUCT_FAIL + " filename can not be null!");
	}

	File homeDirFile = fileConfigurator.getHomeDir(username);

	// TestReload the server root file is well defined and that it can be created
	testHomeDirValidity(homeDirFile);

	if (isFileDirSystemRootDir(homeDirFile)) {

	    if (SystemUtils.IS_OS_WINDOWS) {
		// Replace all "/" with "\" for Windows
		filename = filename.replace("/", File.separator);
	    }

	    return filename;
	}

	/*
	boolean doUseOneRootPerUsername = fileConfigurator
		.useOneRootPerUsername();

	if (doUseOneRootPerUsername) {
	    // Add username to the root directory
	    homeDirFile = new File(
		    homeDirFile.toString() + File.separator + username);
	}
	*/

	if (SystemUtils.IS_OS_WINDOWS) {
	    if (filename.contains(":\\") && filename.length() >= 3) {
		filename = filename.substring(3);
	    }

	    // Replace all "/" with "\" for Windows
	    filename = filename.replace("/", File.separator);

	    if (filename.startsWith(File.separator) && filename.length() >= 1) {
		filename = filename.substring(1);
	    }

	} else {
	    if (filename.startsWith("/") && filename.length() >= 1) {
		filename = filename.substring(1);
	    }
	}

	filename = homeDirFile.toString() + File.separator + filename;

	// Force path creation for filename if it not exists
	File file = new File(filename);
	if (file.getParent() != null) {
	    file.getParentFile().mkdirs();
	}

	return filename;
    }

    /**
     * Tests that the fileConfigurator.getHomeDir() is valid.
     * 
     * @param homeDirFile
     *            fileConfigurator.getHomeDir() value
     * @throws FileNotFoundException
     *             if it's impossible to create it
     */
    public static void testHomeDirValidity(File homeDirFile)
	    throws FileNotFoundException {

	if (homeDirFile == null) {
	    throw new FileNotFoundException(Tag.PRODUCT_USER_CONFIG_FAIL
		    + " FileConfigurator.getHomeDir() can not be null!");
	}

	String homeDirStr = homeDirFile.toString();

	if (SystemUtils.IS_OS_WINDOWS) {
	    String exceptionMessage = Tag.PRODUCT_USER_CONFIG_FAIL
		    + " FileConfigurator.getHomeDir() directory does not start"
		    + " with a 3 characters string containing Windows unit and file separator (like \"C:\\\"): "
		    + homeDirFile.toString();

	    if (homeDirStr.length() < 3) {
		throw new FileNotFoundException(exceptionMessage);
	    }
	    
	    String unit = homeDirStr.substring(0, 3);
	    if (!unit.endsWith(":\\")) {

		throw new FileNotFoundException(exceptionMessage);
	    }
	    
	} else {
	    if (!homeDirStr.startsWith("/")) {
		throw new FileNotFoundException(Tag.PRODUCT_USER_CONFIG_FAIL
			+ " FileConfigurator.getHomeDir() directory does not"
			+ " start with \"/\" separator: "
			+ homeDirFile.toString());
	    }
	}

	if (!homeDirFile.exists() || !homeDirFile.isDirectory()) {
	    homeDirFile.mkdirs();
	}

	if (!homeDirFile.isDirectory()) {
	    throw new FileNotFoundException(Tag.PRODUCT_USER_CONFIG_FAIL
		    + " FileConfigurator.getHomeDir() directory does not exist and can not be created: "
		    + homeDirFile.toString());
	}

    }

    /**
     * Says if a File is / or c:\\ or \
     * 
     * @param file
     * @return
     */
    public static boolean isFileDirSystemRootDir(File file) {

	if (file == null) {
	    throw new NullPointerException("file is null!");
	}
	
	if (file.toString().equals("/")
		|| file.toString().toLowerCase().equals("c:\\")
		|| file.toString().toLowerCase().equals("\\")) {
	    return true;
	} else {
	    return false;
	}
    }

}
