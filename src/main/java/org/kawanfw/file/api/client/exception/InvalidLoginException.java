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
package org.kawanfw.file.api.client.exception;

import java.io.IOException;


/**
 * Signals that the Server Manager refused the (username, password) login or that
 * the user is not any more logged.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class InvalidLoginException extends IOException {

    private static final long serialVersionUID = 4694166724488975743L;

    /**
     * Constructs an {@code InvalidLoginException} with {@code null} as its
     * error detail message.
     */
    public InvalidLoginException() {
	super();
    }

    /**
     * Constructs an {@code InvalidLoginException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public InvalidLoginException(String message) {
	super(message);
	
    }

    
    

}
