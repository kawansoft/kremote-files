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
package org.kawanfw.commons.util;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * 
 * Logger class.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class ClientLogger {

    /** The Java Logger */
    private static Logger CLIENT_LOGGER = null;

    /**
     * Invisible constructor
     */
    protected ClientLogger() {

    }

    /**
     * Returns the Logger to use on client side
     * 
     * @return Logger to use on client side
     * @throws IllegalArgumentException
     */
    public static Logger getLogger() throws IllegalArgumentException {
	createLoggerIfNotExists();
	return CLIENT_LOGGER;
    }

    /**
     * @throws IllegalArgumentException
     */
    private static void createLoggerIfNotExists()
	    throws IllegalArgumentException {
	
	// Nothing to do if already exists
	if (CLIENT_LOGGER != null) {
	    return;
	}

	try {

	    File logDir = new File(FrameworkFileUtil.getUserHomeDotKawansoftDir() + File.separator
		    + "log");
	    logDir.mkdirs();

	    String logFilePattern = logDir.toString() + File.separator
		    + "kremote_files_client.log";

	    CLIENT_LOGGER = Logger.getLogger("ClientLogger");
	    int limit = 50 * 1024 * 1024;
	    Handler fh = new FileHandler(logFilePattern, limit, 4, true);
	    fh.setFormatter(new SingleLineFormatterUtil(false));
	    CLIENT_LOGGER.addHandler(fh);
	} catch (Exception e) {
	    throw new IllegalArgumentException(
		    "Impossible to create the CLIENT_LOGGER logger", e);
	}

    }
}
