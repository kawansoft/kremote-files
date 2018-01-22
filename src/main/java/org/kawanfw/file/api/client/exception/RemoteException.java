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
 * Signals that an Exception has been thrown by a remote method on the server.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class RemoteException extends IOException {

    private static final long serialVersionUID = -5604183624284785327L;

    private String remoteStackTrace = null;

    /**
     * Constructs a new RemoteException with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for exceptions that are little more than wrappers
     * for other throwables (for example,
     * {@link java.security.PrivilegedActionException}).
     * 
     * @param message
     *            The detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method)
     * 
     * @param cause
     *            The cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     * 
     * @param remoteStackTrace
     *            the remote stack trace as string (null if none)
     */
    public RemoteException(String message, Throwable cause,
	    String remoteStackTrace) {
	super(message, cause);
	this.remoteStackTrace = remoteStackTrace;
    }

    /**
     * Returns the remote stack trace as a display string.
     * 
     * @return the remote Stack Trace as a display string
     */
    public String getRemoteStackTrace() {
	return this.remoteStackTrace;
    }

}
