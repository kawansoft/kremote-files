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
package org.kawanfw.file.api.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * 
 * Interface that defines the User Configuration for the KRemote Files
 * Framework.
 * <p>
 * All the implemented methods will be called by the KRemote Files Server
 * programs when a client program asks for a file operation from the client, or
 * when a client program asks for a RPC call of a Java.
 * <p>
 * A concrete implementation should be developed on the server side in order to:
 * <br>
 * <ul>
 * <li>Define how to authenticate the remote (username, password) couple sent by
 * the client side.</li>
 * <li>Define for each username the home directory of files on the server.</li>
 * <li>Define the {@code Logger} for internal logging.</li>
 * </ul>
 * <p>
 * Please note that KRemote Files comes with a default File Configurator
 * implementation that is *not* secured and should be extended:
 * {@link DefaultFileConfigurator}.
 * <p>
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public interface FileConfigurator {

    /**
     * Allows to authenticate the remote {@code (usernname, password)} couple
     * sent by the client side.
     * <p>
     * The KRemote Files Server will call the method in order to grant or not
     * client access.
     * <p>
     * Typical usage would be to check the (username, password) couple against a
     * table in a SQL database or against a LDAP, etc.
     * 
     * @param username
     *            the client username
     * @param password
     *            the password to connect to the server
     * @param ipAddress
     *            the IP address of the client user
     * @return <code>true</code> if the (login, password) couple is
     *         correct/valid. If false, the client side will not be authorized
     *         to send any command.
     * @throws IOException
     *             if an IOException occurs
     * @throws SQLException
     *             if a SQLException occurs
     */
    public boolean login(String username, char[] password, String ipAddress)
	    throws IOException, SQLException;

    /**
     * Defines, for each client user, the server absolute home directory for
     * file storage and upload/downloads. <br>
     * <br>
     * On Linux, the returned file must start with "/". <br>
     * On Windows, the returned file must start with at least 3 characters:
     * windows unit and file separator. (Example: {@code "D:\"}).
     * 
     * @param username
     *            the client username
     * @return the home directory for file storage of the client user on the
     *         server.
     */
    public File getHomeDir(String username);

    /**
     * Returns the {@link Logger} that will be used by for logging:
     * <ul>
     * <li>All Exceptions thrown by server side will be logged.</li>
     * <li>Exceptions thrown are logged with <code>Level.WARNING</code>.</li>
     * </ul>
     * <p>
     * It is not necessary nor recommended to implement this method; do it only
     * if you want take control of the logging to modify the default
     * characteristics of {@link #getLogger()}.
     * 
     * @return the java.util.logging.Logger that will be used for logging
     * @throws IOException
     *             if an IOException occurs
     */
    public Logger getLogger() throws IOException;

}
