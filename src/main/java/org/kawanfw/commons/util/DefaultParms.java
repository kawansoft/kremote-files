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
/**
 * @author Nicolas de Pomereu
 * 
 *         Store the default values of some parameters used in Kawansoft
 *         frameworks
 *
 */

public class DefaultParms {
    
    /** Do not instantiate */
    protected DefaultParms() {
    }

    /** Defines one kilobyte */
    public static final int KB = 1024;
    /** Defines one megabyte */
    public static final int MB = 1024 * KB;
    
    // BEGIN VALUES NOT MODIFIED BY SESSION PARAMETERS
    /** The default buffer size when reading a file */
    public static final int DEFAULT_READ_BUFFER_SIZE = 4 * KB;
    
    /** The default Buffer size when writing a file */
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 4 * KB;
    
    /** The chunklen to HttpUrlConnection.setChunkedStreamingMode(int chunklen) */
    public static final int DEFAULT_STREAMING_MODE_CHUNKLEN = 1024;
    // END VALUES NOT MODIFIED BY SESSION PARAMETERS
    
    
    /**
     * The default maximum authorized length for a string for upload or download
     */
    public static final int DEFAULT_MAX_LENGTH_FOR_STRING = 2 * MB;
    
    /** The default behavior for html encoding */
    public static final boolean DEFAULT_HTML_ENCODING_ON = true;

    /** The default acceptance for self signed SSL certificates */
    public static final boolean ACCEPT_ALL_SSL_CERTIFICATES = false;

    public static final long DEFAULT_DOWNLOAD_CHUNK_LENGTH = 10 * MB;
    public static final long DEFAULT_UPLOAD_CHUNK_LENGTH = 10 * MB;
    
    /** Http content compression */
    public static final boolean DEFAULT_COMPRESSION_ON = true;
    
    /** Color used by servlet display in all KwanSoft Frameworks */
    public static final String KAWANSOFT_COLOR = "E7403E";


}
