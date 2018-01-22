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
package org.kawanfw.file.version;
/**
 * Displays the KRemote Files product Version
 */

public class FileVersion {
    public static final String getVersion() {
	return "" + new PRODUCT();
    }
    public static final String getFullVersion() {
	String CR_LF = System.getProperty("line.separator");
	return PRODUCT.DESCRIPTION + CR_LF + getVersion() + CR_LF + "by : "
		+ new VENDOR();
    }
    public String toString() {
	return getVersion();
    }
    public static final class PRODUCT {
	public static final String VERSION = FileVersionValues.VERSION;
	public static final String NAME = "KRemote Files";
	public static final String DESCRIPTION = "File upload/download and RPC over HTTP in Java";
	public static final String DATE = FileVersionValues.DATE;
	public String toString() {
	    return NAME + " " + VERSION + " - " + DATE;
	}
    }
    public static final class VENDOR {
	public static final String NAME = "KawanSoft S.A.S";
	public static final String WEB = "http://www.kawansoft.com";
	public static final String COPYRIGHT = "Copyright &copy; 2017";
	public static final String EMAIL = "contact@kawansoft.com";
	public String toString() {
	    return VENDOR.NAME + " - " + VENDOR.WEB;
	}
    }
    /**
     * MAIN
     */
    public static void main(String[] args) {
	System.out.println(getFullVersion());
	
	System.out.println();
    }
}
