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
import java.io.OutputStream;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.StringUtil;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;

/**
 * Calls File.listFiles() or File.listFiles(FileFilter)
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class FileListFilesAction {
    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileListFilesAction.class);

    /**
     * Constructor.
     */
    public FileListFilesAction() {

    }

    /**
     * Calls File.listFiles() or File.listFiles(FileFilter) 
     * File.listFiles(FilenameFilter) remote method from
     * the client side. <br>
     * 
     * @param request
     *            the http request
     * @param fileConfigurator
     *            the file configurator defined by the user
     * @param out
     *            the servlet output stream
     * @param username
     *            the client login (for security check)
     * @param filename
     *            the filename to call
     * @throws Exception 
     */
    public void listFiles(HttpServletRequest request,
	    FileConfigurator fileConfigurator,
	    OutputStream out, String username,
	    String filename) throws Exception {

	debug("in listFiles()");
	
	FileFilter fileFilter =null;
	FilenameFilter filenameFilter =null;
	
	fileFilter = ServerFilterUtil.buidFileFilter(request, fileConfigurator,
		username);
	filenameFilter = ServerFilterUtil.buildFilenameFilter(request,
		fileConfigurator, username);

	debug("After ServerFilterUtil.buidFileFilter");
	
	filename = HttpConfigurationUtil.addUserHomePath(fileConfigurator,
		username, filename);

	File file = new File(filename);
	File[] files = null;

	try {
	    if (fileFilter == null && filenameFilter == null) {
	        files = file.listFiles();
	    } else if (fileFilter != null) {
	        files = file.listFiles(fileFilter);
	    }
	    else if (filenameFilter != null) {
	        files = file.listFiles(filenameFilter);
	    }
	    else {
	        // Can not happen
	        throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL + " Impossible else condition.");
	    }
	} catch (IllegalArgumentException e) {
	    // Necessary because Reloader wich extends ClassLoader can nor throw ClassNotFoundException
	    if (e.getCause() != null && e.getCause() instanceof ClassNotFoundException) {
		throw new ClassNotFoundException(e.getCause().getMessage());
	    }
	}

	if (files == null) {
	    writeLine(out, TransferStatus.SEND_OK);
	    writeLine(out, null);
	    return;
	}

	if (files.length == 0) {
	    writeLine(out, TransferStatus.SEND_OK);
	    writeLine(out, "[]");
	    return;
	}

	writeLine(out, TransferStatus.SEND_OK);

	for (File theFile : files) {
	    String fileStr = theFile.toString();
	    
	    fileStr = ReturnFileFormatter.format(fileConfigurator, username,
			 fileStr);
	    
	    fileStr = HtmlConverter.toHtml(fileStr);
	    writeLine(out, fileStr);
	}

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
	out.write((s + StringUtil.CR_LF).getBytes());
    }

    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
