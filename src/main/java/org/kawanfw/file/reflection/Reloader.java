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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;

/*
 * Tributes:
 * http://stackoverflow.com/questions/3971534/how-to-force-java-to-reload-class-upon-instantiation/3971771#3971771
 * and 
 * https://github.com/evacchi/class-reloader
 */
public class Reloader extends ClassLoader {

    private static boolean DEBUG = FrameworkDebug.isSet(Reloader.class);

    private static URL url;

    ClassLoader orig;

    public Reloader(ClassLoader orig) {
	this.orig = orig;
    }

    @Override
    public Class<?> loadClass(String s) {
	return findClass(s);
    }

    @Override
    public Class<?> findClass(String s) {
	try {
	    byte[] bytes = loadClassData(s);
	    return defineClass(s, bytes, 0, bytes.length);
	} catch (IOException ioe) {
	    if (DEBUG) ioe.printStackTrace(System.out);
	    try {
		return super.loadClass(s);
	    } catch (ClassNotFoundException ignore) {
		if (DEBUG) ioe.printStackTrace(System.out);
	    }
	    //ioe.printStackTrace(System.out);
	    return null;
	} catch (ClassNotFoundException e) {
	    throw new IllegalArgumentException(e);
	}
    }

    private byte[] loadClassData(String className) throws IOException, ClassNotFoundException {

	DataInputStream dis = null;

	try {

	    debug("className: " + className);

	    /*
	     * get the actual path using the original class loader
	     */
	    Class<?> clazz = orig.loadClass(className);

	    String simpleName = StringUtils.substringAfterLast(className, ".");
	    debug("clazz                : " + clazz);
	    debug("clazz.getSimpleName(): " + simpleName);

	    url = clazz.getResource(simpleName + ".class");
	    debug("url: " + url);

	    /*
	     * force reload
	     */
	    File file = new File(url.toURI());
	    int size = (int) file.length();
	    byte buff[] = new byte[size];
	    dis = new DataInputStream(new FileInputStream(file));
	    dis.readFully(buff);

	    return buff;
	} catch (ClassNotFoundException ex) {
	    throw ex;
	} catch (Exception ex) {
	    throw new IOException(ex);
	} finally {
	    if (dis != null) {
		dis.close();
	    }
	}
    }

    private static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }
}
