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
package org.kawanfw.file.reflection;

import org.kawanfw.file.servlet.nio.KawanfwSecurityManager;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ReflectionUtil {

    /**
     * Protected
     */
    protected ReflectionUtil() {
	
    }

    /**
     * As is it says! 
     * @param clazz the class to test the filter/filtername type
     */
    public static boolean isClassFileFilterOrFilenameFilter(Class<?> clazz) {
        
        if (clazz == null) throw new IllegalArgumentException("clazz is null!");
    
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        if (interfaceClasses == null) {
            return false;
        }
        for (Class<?> class1 : interfaceClasses) {
            KawanfwSecurityManager.debug("class1: " + class1);
            if (class1.getName().equals("java.io.FileFilter")
        	    || class1.getName().equals("java.io.FilenameFilter")) {
        	return true;
            }
        }
        
        return false;
    }

    /**
     * As is it says! 
     * @param clazz the class to test the FilenameFilter type
     */
    public static boolean isClassFilenameFilter(Class<?> clazz) {
        if (clazz == null) throw new IllegalArgumentException("clazz is null!");
        
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        if (interfaceClasses == null) {
            return false;
        }
        
        for (Class<?> class1 : interfaceClasses) {
            KawanfwSecurityManager.debug("class1: " + class1);
            if (class1.getName().equals("java.io.FilenameFilter")) {
        	return true;
            }
        }
        
        return true;
    }
    
    /**
     * As is it says! 
     * @param clazz the class to test the FileFilter type
     */
    public static boolean isClassFileFilter(Class<?> clazz) {
        if (clazz == null) throw new IllegalArgumentException("clazz is null!");
        
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        if (interfaceClasses == null) {
            return false;
        }
        
        for (Class<?> class1 : interfaceClasses) {
            KawanfwSecurityManager.debug("class1: " + class1);
            if (class1.getName().equals("java.io.FileFilter")) {
        	return true;
            }
        }
        
        return true;
    }
    
}
