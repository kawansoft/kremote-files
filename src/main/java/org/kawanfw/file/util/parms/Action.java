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

public class Action {
    public static final String BEFORE_LOGIN_ACTION = "BEFORE_LOGIN_ACTION";
    public static final String LOGIN_ACTION = "LOGIN_ACTION";
    public static final String CALL_ACTION = "CALL_ACTION";
    public static final String CALL_ACTION_HTML_ENCODED ="CALL_ACTION_HTML_ENCODED";
    public static final String GET_FILE_LENGTH_ACTION = "GET_FILE_LENGTH_ACTION";
    public static final String DELETE_FILE_ACTION = "DELETE_FILE_ACTION";
    public static final String EXISTS_ACTION = "EXISTS_ACTION"; 
    public static final String MKDIR_ACTION = "MKDIR_ACTION";    
    public static final String MKDIRS_ACTION = "MKDIRS_ACTION";
    public static final String DOWNLOAD_FILE_ACTION = "DOWNLOAD_FILE_ACTION";
    public static final String UPLOAD_FILE_ACTION = "UPLOAD_FILE_ACTION";
    public static final String LIST_DIRS_IN_DIR_ACTION = "LIST_DIRS_IN_DIR_ACTION";
    public static final String LIST_FILES_IN_DIR_ACTION = "LIST_FILES_IN_DIR_ACTION";
    public static final String RENAME_FILE_ACTION = "RENAME_FILE_ACTION";
    
    // For NIO
    public static final String FILE_METHOD_ONE_RETURN_ACTION = "FILE_METHOD_ONE_RETURN_ACTION";
    public static final String FILE_LIST_ACTION = "FILE_LIST_ACTION";
    public static final String FILE_LIST_FILES_ACTION = "FILE_LIST_FILES_ACTION";
    public static final String CLEAN_HOST_TEMP_FILES = "CLEAN_HOST_TEMP_FILES";
    public static final String GET_JAVA_VERSION = "GET_JAVA_VERSION";
    
    /**
     * Protected constructor
     */
    protected Action() {
    }
}
