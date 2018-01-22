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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.kawanfw.commons.util.FrameworkFileUtil;
import org.kawanfw.file.api.server.util.SingleLineFormatter;

/**
 * Default implementation of server side configuration for the KRemote Files
 * Framework. Implementation elements are:
 * <br><br>
 * <ul>
 * <li>{@link #login} method returns {@code true}. (Server access is always
 * granted.)</li>
 * <li>The home directory of files on the server is
 * {@code user.home/.kremote-server-root} for all client users.</li>
 * <li>Defines the Logger for internal logging.</li>
 * </ul>
 * <p>
 * 
 * @author Nicolas de Pomereu
 * @since 1.0 
 */
public class DefaultFileConfigurator implements FileConfigurator {

    /** The Logger to use */
    private static Logger KAWANFW_LOGGER = null;  

    /**
     * Constructor.
     */
    public DefaultFileConfigurator() {

    }

    /* (non-Javadoc)
     * @see org.kawanfw.file.api.server.FileConfigurator#login(java.lang.String, char[], java.lang.String)
     */
    
    /**
     * Returns {@code true}. (Client is always granted access).
     * @return {@code true}. 
     */
    @Override
    public boolean login(String username, char[] password, String ipAddress)
	    throws IOException, SQLException {
	return true;
    }


    /* (non-Javadoc)
     * @see org.kawanfw.file.api.server.FileConfigurator#getHomeDir(java.lang.String)
     */
    /**
     * Returns <code>user.home/.kremote-server-root</code> for all client users.
     *         ({@code user.home} is the one of the servlet container). <br>
     *         The directory is created if it does not exists.
     * @return <code>user.home/.kremote-server-root</code>.
     */
    @Override
    public File getHomeDir(String username) {

	String userHome = System.getProperty("user.home");
	if (!userHome.endsWith(File.separator)) {
	    userHome += File.separator;
	}
	userHome += ".kremote-server-root";
	File homeDir = new File(userHome);

	if (!homeDir.exists()) {
	    homeDir.mkdirs();
	}

	return homeDir;
    }

    /* (non-Javadoc)
     * @see org.kawanfw.file.api.server.FileConfigurator#getLogger()
     */
    /**
     * Returns a Logger whose pattern is
     *         <code>user.home/.kawansoft/log/kremote_files.log</code>, that
     *         uses a {@link SingleLineFormatter} and that logs 50Mb into 4
     *         rotating files.
     * @return a default Logger with  <code>user.home/.kawansoft/log/kremote_files.log</code> pattern.
     */

    @Override
    public Logger getLogger() throws IOException {

	if (KAWANFW_LOGGER == null) {

	    File logDir = new File(
		    FrameworkFileUtil.getUserHomeDotKawansoftDir()
		    + File.separator + "log");
	    logDir.mkdirs();

	    String logFilePattern = logDir.toString() + File.separator
		    + "kremote_files.log";

	    KAWANFW_LOGGER = Logger.getLogger("KawanfwLogger");
	    int limit = 50 * 1024 * 1024;
	    Handler fh = new FileHandler(logFilePattern, limit, 4, true);
	    fh.setFormatter(new SingleLineFormatter(false));
	    KAWANFW_LOGGER.addHandler(fh);
	}

	return KAWANFW_LOGGER;
    }

}
