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
package org.kawanfw.file.test.api.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.kawanfw.file.api.server.ClientCallable;


/**
 * 
 * Tests that loading HttpServletRequest works correctly.
 * Requires the client to be authenticated.
 */

// org.kawanfw.file.test.api.server.ServletRequestQuery.getIPAddr
public class ServletRequestQuery implements ClientCallable {

    /**
     * Constructor
     */
    public ServletRequestQuery() throws IOException {

    }

    /**
     * Returns the remote addr + a message. Tests that loading HttpServletRequest works correctly.
     * @param message
     * @param request
     * @return
     */
    public String getIPAddr(String message, HttpServletRequest request) {
	System.out.println("request.getRemoteAddr(): " + request.getRemoteAddr());
	return "IPAddr: " + request.getRemoteAddr() + " message: " + message;
    }
    
}
