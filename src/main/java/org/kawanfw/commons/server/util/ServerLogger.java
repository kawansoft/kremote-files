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
package org.kawanfw.commons.server.util;

import java.io.IOException;
import java.util.logging.Logger;

import org.kawanfw.commons.util.Tag;

/**
 * 
 * Logger class.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class ServerLogger {

    /** The Java Logger */
    private static Logger SERVER_LOGGER = null;

    /**
     * Invisible constructor
     */
    protected ServerLogger() {

    }

    /**
     * Create the Logger.
     * 
     * @param the
     *            directory where to put the log files
     * 
     * @throws IOException
     */
    public static void createLogger(Logger logger) throws IOException {

	SERVER_LOGGER = logger;
    }
    
    /**
     * Returns the Logger to use on server side
     * 
     * @return Logger to use on client side
     * @throws IllegalArgumentException
     */
    public static Logger getLogger() throws IllegalArgumentException {

	if (SERVER_LOGGER == null) {
	    throw new IllegalArgumentException(Tag.PRODUCT_PRODUCT_FAIL + " Impossible to get the Logger. (Null value).");
	}
	
	return SERVER_LOGGER;
    }
    
}
