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
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.FrameworkFileUtil;
import org.kawanfw.commons.util.Sha1Util;
import org.kawanfw.commons.util.StringUtil;

/**
 * 
 * Allows to create a unique file  in function of (username, remote file name).
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class UniqueFileCreator {

    /**
     * Protected
     */
    protected UniqueFileCreator() {

    }

    /**
     * Create our own unique file and always the same temp file in function of (username, remote file name).
     * Must be always the same because of recovery mechanism.
     * 
     * @param username	the username of client 
     * @param remoreFile	the remote filename with full path 
     * 
     * @return the tempfile to create
     */
    public static synchronized File createUnique(String username, String remoteFile) throws IOException {
        
        Sha1Util sha1Util = new Sha1Util();
        String hexId = null;
        
        try {
            hexId = sha1Util.getHexHash((username + remoteFile).getBytes());
        } catch (Exception e) {
            throw new IOException(e);
        } 
        
        String uniqueFilename =  remoteFile;
        
        if (uniqueFilename.contains("/")) {
            uniqueFilename = StringUtils.substringAfterLast(uniqueFilename, "/");
        }
        
        uniqueFilename += "-" + username + "-" + hexId;
        
        String tempDir = FrameworkFileUtil.getKawansoftTempDir();
        String tempFile = tempDir + File.separator + "remote-" + uniqueFilename + ".kawanfw";
        
        // Beware of resutling length because of OS limit length...
        tempFile = StringUtil.cut(tempFile, 255);
        
        return new File(tempFile);
    }

}
