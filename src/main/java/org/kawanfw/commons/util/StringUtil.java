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

import java.util.logging.Level;


/**
 * Misc utilites Strings
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class StringUtil {
    
    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(StringUtil.class);

    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator");

    /**
     * Convert a String to a Base64 String
     * 
     * @param s
     *            the String to convert
     * @return The converted String converted in Base64
     */
    public static String toBase64(String s) {
	if (s == null) {
	    throw new IllegalArgumentException("input string can not be null!");
	}

	return Base64.byteArrayToBase64(s.getBytes());
	//return Base64.encodeBase64String(s.getBytes());
    }

    /**
     * return a "" empty string if null or a trimed stgring if not null
     * 
     * @param action
     *            the input string
     * @return the empty stgring or trimed string
     */
    public static String getTrimValue(String string) {
	if (string == null) {
	    return "";
	} else {
	    return string.trim();
	}
    }
    
    /**
     * Convert a String to a Base64 String
     * 
     * @param s
     *            the String to convert
     * @return The converted String converted in Base64
     */
    public static String fromBase64(String s) {
	if (s == null) {
	    throw new IllegalArgumentException("input string can not be null!");
	}

	return new String(Base64.base64ToByteArray(s));
	//return new String(Base64.decodeBase64(s));
    }

    /**
     * Cut the String to 64 chars max
     * 
     * @param in
     *            the string to cut
     * @param maxLength
     *            the string max length
     * @return the cut string at maxLength chars
     */
    public static String cut(String in, int maxLength) {
	if (in == null) {
	    return null;
	}

	if (in.length() <= maxLength) {
	    return in;
	}

	// Ok, cut it to maxLength chars!
	return in.substring(0, maxLength);

    }

    /*
     * Cut the String to 64 chars max
     * 
     * @param in the string to cut
     * 
     * @return the cut string at 64chars
     */
    public static String cut64(String in) {
	if (in == null) {
	    return null;
	}

	if (in.length() <= 64) {
	    return in;
	}

	// Ok, cut it to 64 chars!
	return in.substring(0, 64);

    }

    /**
     * Return true if the filename is a Window possible Filename
     * 
     * @param filename
     *            the filename to test
     * @return true if the filename is a Window Filename
     */
    public static boolean isPossibleWindowFilename(String filename) {
	if (filename == null) {
	    throw new IllegalArgumentException(
		    "input filename can not be null!");
	}

	if (filename.indexOf("\\") != -1 || filename.indexOf("/") != -1
		|| filename.indexOf(":") != -1 || filename.indexOf("*") != -1
		|| filename.indexOf("?") != -1 || filename.indexOf("\"") != -1
		|| filename.indexOf("\"") != -1 || filename.indexOf("<") != -1
		|| filename.indexOf(">") != -1 || filename.indexOf("|") != -1) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * debug tool
     */
    @SuppressWarnings("unused")
    private static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
