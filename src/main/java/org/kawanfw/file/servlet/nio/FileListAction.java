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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.HtmlConverter;
import org.kawanfw.commons.util.StringUtil;
import org.kawanfw.commons.util.TransferStatus;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.servlet.util.HttpConfigurationUtil;

/**
 * Calls File.list() or File.list(FilenameFilter)
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class FileListAction {
    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileListAction.class);

    /**
     * Constructor.
     */
    public FileListAction() {

    }
    
    /**
     * Calls File.list() or File.list(FilenameFilter) remote method from the client side. 
     * <br>
     * s
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
    public void list(HttpServletRequest request,
	    FileConfigurator fileConfigurator,
	    OutputStream out, String username,
	    String filename) throws Exception {

	debug("in call");

	FilenameFilter filenameFilter = ServerFilterUtil.buildFilenameFilter(request, fileConfigurator,
		username);
	filename = HttpConfigurationUtil.addUserHomePath(fileConfigurator,
		username, filename);

	File file = new File(filename);
	String[] files = null;

	if (filenameFilter == null) {
	    files = file.list();
	} else {
	    files = file.list(filenameFilter);
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

	for (String theFile : files) {
	    theFile = HtmlConverter.toHtml(theFile);
	    writeLine(out, theFile);
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
