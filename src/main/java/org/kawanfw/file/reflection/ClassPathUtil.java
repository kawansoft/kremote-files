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
package org.kawanfw.file.reflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.FrameworkFileUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ClassPathUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ClassPathUtil.class);

    /**
     * Protected constructor
     */
    protected ClassPathUtil() {
    }

    /**
     * Returns user.home/.kawansoft/.classpath
     * 
     * @returns user.home/.kawansoft/.classpath
     */
    public static String getUserHomeDotKawansoftDotClasspath() {

	String userHomeDotKawansoftDotClasspath = FrameworkFileUtil.getUserHomeDotKawansoftDir()+ File.separator + ".classpath"
		+ File.separator;
	return userHomeDotKawansoftDotClasspath;
    }

    /**
     * Returns user.home/.kawansoft/.classpath/.usernames/username/
     * 
     * @returns user.home/.kawansoft/.classpath/.usernames/username/
     */
    public static String getUsernameClasspath(String username) {

	if (username == null) {
	    throw new IllegalArgumentException("username is null");
	}

	return getUserHomeDotKawansoftDotClasspath() + ".usernames"
		+ File.separator + username + File.separator;
    }

    /**
     * Displays the classpath
     */
    public static void displayClasspath() {

	System.out.println();
	System.out.println(new Date());
	ClassLoader cl = ClassLoader.getSystemClassLoader();

	URL[] urls = ((URLClassLoader) cl).getURLs();

	for (URL url : urls) {
	    System.out.println(url.getFile());
	}

    }

    /**
     * Move the filter file from the upload zone to the final ou of server
     * classpath
     * 
     * @param username
     * @param filterFile
     * @param filterClassname
     * @throws IOException
     */
    public static void moveToFinalClasspath(String username, File filterFile,
	    String filterClassname) throws IOException {

	File destFile = null;

	try {
	    // Move the file to the final classpath, outside erver root
	    String usernameClasspath = getUsernameClasspath(username);

	    debug("usernameClasspath: " + usernameClasspath);

	    if (filterFile.toString().endsWith(".class")) {
		String classFolder = usernameClasspath + "classes";
		String classSubDirPath = ClassFileLocatorNew
			.getClassSubdirectories(filterClassname);
		classSubDirPath = classSubDirPath.replace("/", File.separator);
		classFolder += classSubDirPath;
		File classFolderDir = new File(classFolder);
		boolean doneMkdirs = classFolderDir.mkdirs();
		debug("doneMkdirs: " + doneMkdirs);
		destFile = new File(classFolder + filterFile.getName());
		FileUtils.copyFile(filterFile, destFile);

		debug("classFolder: " + classFolder);
		debug("destFile   : " + destFile);

	    } else {
		String libDir = usernameClasspath + "lib" + File.separator;
		destFile = new File(libDir + filterFile.getName());
		FileUtils.copyFile(filterFile, destFile);
		destFile.deleteOnExit();
	    }
	} finally {
	    // Clean upload area
	    filterFile.delete();

	    if (destFile != null) {
		destFile.deleteOnExit(); // Keep a trace for debug
	    }

	}
    }

    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
