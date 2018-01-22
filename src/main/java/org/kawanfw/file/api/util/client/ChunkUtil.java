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

import org.kawanfw.commons.util.DefaultParms;
import org.kawanfw.file.api.client.RemoteSession;

/**
 * 
 * Misc utilitiy methods for chunking.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ChunkUtil {

    /**
     * 
     */
    protected ChunkUtil() {
	
    }

    /**
     * Returns the chun length to use for downloads
     * 
     * @param remoteSession
     * @return the chunk length to use for downloads.
     */
    public static long getDownloadChunkLength(RemoteSession remoteSession) {
        long chunkLength = DefaultParms.DEFAULT_DOWNLOAD_CHUNK_LENGTH;
    
        if (remoteSession.getSessionParameters() != null) {
            chunkLength = remoteSession.getSessionParameters()
        	    .getDownloadChunkLength();
        }
        return chunkLength;
    }

    /**
     * Returns the chunk length to use for uploads.
     * 
     * @param remoteSession
     * @return the chunk length to use for uploads
     */
    public static long getUploadChunkLength(RemoteSession remoteSession) {
        long chunkLength = DefaultParms.DEFAULT_UPLOAD_CHUNK_LENGTH;
    
        if (remoteSession.getSessionParameters() != null) {
            chunkLength = remoteSession.getSessionParameters()
        	    .getUploadChunkLength();
        }
        return chunkLength;
    }
    
    

}
