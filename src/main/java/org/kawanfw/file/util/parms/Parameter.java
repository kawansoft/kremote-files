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
package org.kawanfw.file.util.parms;
/**
 * @author Nicolas de Pomereu
 * 
 */

public class Parameter {
    public static final String ACTION = "action";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String METHOD_NAME = "method_name";
    public static final String PARAMS_TYPES = "params_types";
    public static final String PARAMS_VALUES = "params_values";
    public static final String FILENAME = "filename";
    public static final String CHUNKLENGTH = "chunkLength";
    public static final String FILENAME_DEST = "filename_dest";
    public static final String DELETE_IF_EXISTS = "delete_if_exists";
    
    public static final String  LICENSE_FILENAME = "license_filename";
    
    // NIO
    public static final String FILENAME_FILTER_CLASSNAME = "filename_filter_classname";
    public static final String FILE_FILTER_CLASSNAME = "file_filter_classname";
    
    public static final String FILENAME_FILTER_FILENAME = "filename_filter_filename";
    public static final String FILE_FILTER_FILENAME = "file_filter_filename";
    
    public static final String BASE64_SERIAL_FILENAME_FILTER = "base64_serial_filename_filter";
    public static final String BASE64_SERIAL_FILE_FILTER = "base64_serial_file_filter";
    
    // Obsolete
    
    /** To test if crypto is correctly set */
    public static final String TEST_CRYPTO = "test_crypto";
    
    /** The version for server to know if there is a protocol change */
    public static String VERSION = "version";

    
    /** The token length to use */
    public static final int TOKEN_LEFT_SIZE = 20;
    
   
    /**
     * Protected constructor
     */
    protected Parameter() {
    }
}
