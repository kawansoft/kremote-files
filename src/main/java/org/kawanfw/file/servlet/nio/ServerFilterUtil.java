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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.reflection.ClassSerializer;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;
import org.kawanfw.file.util.parms.Parameter;

/**
 * Server methods to rebuild FilenameFilter & FileFilter from transported class
 * name
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class ServerFilterUtil {

    private static boolean DEBUG = FrameworkDebug.isSet(ServerFilterUtil.class);

    /**
     * No instance creation
     */
    protected ServerFilterUtil() {

    }

    /**
     * Rebuid the FilenameFilter from uploaded serialized FilenameFilter
     * 
     * @param request
     * @param fileConfigurator
     * @param username
     * @return the rebuilt FilenameFilter
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static FilenameFilter buildFilenameFilter(
	    HttpServletRequest request, FileConfigurator fileConfigurator,
	    String username) throws IOException, ClassNotFoundException,
	    InstantiationException, IllegalAccessException, Exception {

	FilenameFilter filenameFilter = null;

	String filenameFilterClassname = request
		.getParameter(Parameter.FILENAME_FILTER_CLASSNAME);
	
	String base64SerialFilenameFilter = request
		.getParameter(Parameter.BASE64_SERIAL_FILENAME_FILTER);
	
	String filenameFilterFilename = request
		.getParameter(Parameter.FILENAME_FILTER_FILENAME);
		
	if (base64SerialFilenameFilter == null || base64SerialFilenameFilter.isEmpty()) {
	    
	    if (filenameFilterFilename != null
		    && !filenameFilterFilename.isEmpty()) {

		filenameFilterFilename = HttpConfigurationUtil.addUserHomePath(
			fileConfigurator, username, filenameFilterFilename);		
		File file = null;

		try {
		    file = new File(filenameFilterFilename);
		    base64SerialFilenameFilter = FileUtils.readFileToString(file, Charset.defaultCharset());
		} finally {
		    FileUtils.deleteQuietly(file);
		}
	    }
	}
	
	debug("filenameFilterClassname: " + filenameFilterClassname);

	if (filenameFilterClassname == null || filenameFilterClassname.isEmpty()) {
	    return filenameFilter;
	}
	
	ClassSerializer<FilenameFilter> classSerializer = new ClassSerializer<FilenameFilter>();
	filenameFilter = classSerializer.fromBase64(base64SerialFilenameFilter);
		    
	return filenameFilter;
    }

    /**
     * Rebuid the FileFilter from uploaded from serialized FileFilter
     * 
     * @param request
     * @param fileConfigurator
     * @param username
     * @return
     * @throws Exception
     */
    public static FileFilter buidFileFilter(HttpServletRequest request,
	    FileConfigurator fileConfigurator, String username)
	    throws Exception {

	FileFilter fileFilter = null;

	String fileFilterClassname = request
		.getParameter(Parameter.FILE_FILTER_CLASSNAME);
	
	String base64SerialFileFilter = request
		.getParameter(Parameter.BASE64_SERIAL_FILE_FILTER);
	
	String fileFilterFilename = request
		.getParameter(Parameter.FILE_FILTER_FILENAME);
		
	if (base64SerialFileFilter == null || base64SerialFileFilter.isEmpty()) {

	    if (fileFilterClassname != null && !fileFilterClassname.isEmpty()) {
		fileFilterFilename = HttpConfigurationUtil.addUserHomePath(
			fileConfigurator, username, fileFilterFilename);

		File file = null;

		try {
		    file = new File(fileFilterFilename);
		    base64SerialFileFilter = FileUtils.readFileToString(file, Charset.defaultCharset());
		} finally {
		    FileUtils.deleteQuietly(file);
		}

	    }
	}
		
	debug("fileFilterClassname: " + fileFilterClassname);

	if (fileFilterClassname == null || fileFilterClassname.isEmpty()) {
	    return fileFilter;
	}
	
	ClassSerializer<FileFilter> classSerializer = new ClassSerializer<FileFilter>();
	fileFilter = classSerializer.fromBase64(base64SerialFileFilter);

	return fileFilter;
    }

    private static void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
