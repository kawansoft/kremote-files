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
package org.kawanfw.file.api.util.client;

import java.io.File;
import java.util.logging.Level;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;

/**
 * Utilities to rewrite path from Windows to Unix style
 * 
 * @author Nicolas de Pomereu
 */
public class PathUtil {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(PathUtil.class);
	    
    /**
     * Protected constructor
     */
    public PathUtil() {
	
    }
   
    /**
     * Transfrom the Windows syntax to Unix and force absolute pathname
     * with leading /.
     * 
     * @param remotePathname
     *            the remote pathname to tranform to Unix
     * @return the Unix remote pathname
     */
    public static String rewriteToUnixSyntax(String remotePathname) {
	
	debug("remotePathname in : " + remotePathname);
	    
        if (remotePathname == null) {
            return null; // Exception delegated to RemoteFile creation
        }
    
        if (remotePathname.contains("\t")) {
            throw new IllegalArgumentException("pathname contains Tab characters.");
        }
        
        if (!SystemUtils.IS_OS_WINDOWS) {
            if (!remotePathname.startsWith("/")) {
        	remotePathname = "/" + remotePathname;
            }
            return remotePathname;
        }
    
        // Windows specific
        if (remotePathname.length() > 2) {
            String unit = remotePathname.substring(0, 3);
            if (unit.endsWith(":\\")) {
        	remotePathname = remotePathname.substring(2);
            }
        }
    
        remotePathname = remotePathname.replace(File.separator, "/");
    
        if (!remotePathname.startsWith("/")) {
            remotePathname = "/" + remotePathname;
        }
        
        debug("remotePathname out: " + remotePathname);
	
        return remotePathname;
    }

    /**
     * debug tool
     */
    
    private static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }  
    
}
