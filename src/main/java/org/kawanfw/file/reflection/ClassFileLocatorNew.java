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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;

/**
 * Allows to find the ".class" or ".jar" file of an instance, allows to open a InputStream on it.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ClassFileLocatorNew {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(ClassFileLocatorNew.class);
    
    /** The class to locate */
    private Class<?> clazz = null;
    
    /** The name of the class that calls RemoteFile.list(filter) or RemoteFile.listFile(filter) */
    private String callerClassName = null;

    /**
     * Constructor
     * @param clazz	The class to locate
     * @param callerClassName the name of the class that called clazz
     */
    public ClassFileLocatorNew(Class<?> clazz, String callerClassName) {
	
	if (clazz == null) {
	    throw new IllegalArgumentException("class is null!");
	}
	this.clazz = clazz;
	this.callerClassName = callerClassName;
	
    }
    
//    /**
//     * Exports the class to a temp file "./kawansoft/tmp/className.class"
//     * <br>Note: className includes package as in Class.getName().
//     * 
//     * @return the exported temp file "./kawansoft/tmp/className.class"
//     * @throws IOException
//     */
//    public File exportClassToTempFile() throws IOException {
//	
//	String tempDir = FrameworkFileUtil.getKawansoftTempDir();
//	File outFile = new File(tempDir + File.separator
//		+ clazz.getName() + ".class");
//	byte [] byteArray = null;
//	
//	if (FrameworkSystemUtil.isAndroid()) {
//	    String classname = clazz.getName();
//	    byteArray = AndroidUtil.extractClassFileBytecode(classname);
//	} else {
//	    byteArray = this.extractClassFileBytecode();
//	}
//	
//	debug("class name      : " + clazz.getName());
//	debug("byteArray.length: " + byteArray.length);
//	
//	FileUtils.writeByteArrayToFile(outFile, byteArray);
//	return outFile;
//    }
    
    /**
     * Gets the container of the class, ie the .jar file or the .class file
     * @return the container of the class, ie the .jar file or the .class file
     * @throws IOException
     */
    public File getContainerFile() throws IOException {
	
	if (isContainerJar()) {
	    
	    //url : jar:file:/I:/_dev_awake/awake-file-3.0/lib/stringtemplate.jar!/org/antlr/stringtemplate/StringTemplate.class
	    URL url = getUrl();
	    String urlString = url.toString();
	    
	    urlString = StringUtils.substringBefore(urlString, "!");
	    urlString = StringUtils.substringAfter(urlString, "jar:");
	    
	    File file = null;
	    
	    try {
		file = new File(new URL(urlString).toURI());
	    } catch (URISyntaxException e) {
		throw new IOException(e);
	    }
	    return file;
	}
	else {
	    return getDotClassFile();
	}
    }
    
    /**
     * Returns a byte array of the class container (.class or .jar container)
     * @return a byte array of the class container (.class or .jar container)
     * @throws IOException
     */
    public byte [] extractClassFileBytecode() throws IOException {
	
	InputStream in = null;

	try {
	    if (isContainerJar()) {
	        in = getUrl().openStream();
	    } else {
	        File file = getDotClassFile();
	        in = new BufferedInputStream(new FileInputStream(file));
	    }
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    IOUtils.copy(in, out);
	    return out.toByteArray();
	    
	} finally {
	    //IOUtils.closeQuietly(in);
	    if (in != null) {
		try {
		    in.close();
		}
		catch (Exception e) { } // Ignore
	    }
	    
	}
    }
    
    /**
     * Returns true if the container is jar file
     * @return true if the container is jar file
     */
    public boolean isContainerJar() {
	URL url = getUrl();
	
	if (url == null) {
	    return false;
	}
	
	if (url.toString().startsWith("jar:")) {
	    return true;
	}
	else {
	    return false;
	}
    }
    
    /**
     * Returns true if the container is .class file
     * @return true if the container is .class file
     */
    public boolean isContainerDotClass() {
	URL url = getUrl();
	
	if (url == null) {
	    return false;
	}
	
	if (url.toString().startsWith("file:")) {
	    return true;
	}
	else {
	    return false;
	}
    }
    
    /**
     * Get the file corresponding to our class stored in .class file
     * @return	file corresponding to our class stored in .class file
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public File getDotClassFile() throws FileNotFoundException, IOException  {
	
	if (! isContainerDotClass()) {
	    throw new IllegalArgumentException("class is stored in a jar file. No \".class\" file exists!");
	}
	
	File file = null;
	try {
	    file = new File(getUrl().toURI());
	} catch (URISyntaxException e) {
	    throw new IOException(e);
	}
	
	debug("file: " + file);
	
	if (file.isFile()) {
	    return file;
	}
	
	debug("dir: " + file);
	
	if (! file.exists() ) {
	    throw new FileNotFoundException("Class file does nots exists: " + file);
	}
	
	// It is a directory ==> Find all classe that matches name
	
	// Its not a regular class but a private static inner class whose name
	// ends with $
	// Get the first one we find in the directory

	FilenameFilter filenameFilter = new FilenameFilter() {

	    @Override
	    public boolean accept(File dir, String name) {
		if (name.endsWith("$" + clazz.getSimpleName() + ".class"))
		    return true;
		else
		    return false;
	    }
	};
	
	File[] files = file.listFiles(filenameFilter);
	if (files == null || files.length == 0) {
	    throw new FileNotFoundException(Tag.PRODUCT
		    + " Impossible to find the static inner class file for "
		    + clazz.getSimpleName());
	}

	for (File file2 : files) {
	    debug("file2 : " + file2);
	}
	
	// Easy case
	if (files.length == 1) {
	    debug("OK: returning " + files[0]);
	    return files[0];
	}

	if (callerClassName == null) {
	    throw new FileNotFoundException(Tag.PRODUCT + " More than one inner class matching inner class name. "
	    	+ "Impossible to find the correct static inner class file for " + clazz.getSimpleName() + " without the caller class name.");
	}
	
	// More that one class and we know the caller
	if (callerClassName.contains(".")) {
	    callerClassName = StringUtils.substringAfterLast(callerClassName,
		    ".");
	}

	for (File theFile : files) {
	    String filename = theFile.getName();
	    if (filename.startsWith(callerClassName)) {
		debug("OK: returning " + theFile);
		return theFile;
	    }
	}
	    
	throw new FileNotFoundException(Tag.PRODUCT + " More than one inner class matching inner class name. Impossible to find the correct static inner class file for " + clazz.getSimpleName());

    }
    
    /**
     * Returns the URL of the class. If the class is an inner class, will return the directory
     * @return the URL of the class. If the class is an inner class, will return the directory 
     */
    public URL getUrl() {
	URL url = clazz.getResource(clazz.getSimpleName() + ".class");

	// URL null ==> inner class ==> Return the directory location
	if (url == null) {
	    url = clazz.getResource("");
	}
	
	return url;
    }
        
    /**
     * Returns the pathname with "/" file separator notation corresponding to the package organization.
     * <br>
     * Example : for class org.kawanfw.file.reflection.ClassFileLocator, will
     * return /org/kawanfw/file/reflection/ 
     * @return the pathname corresponding to the package organization
     */
    public static String getClassSubdirectories(String className) {
	
	if (className == null) {
	    throw new IllegalArgumentException("className is null!");
	}
	
	
	String directoryPath = className;
	if (! directoryPath.contains(".")) {
	    return ""; // No subdirectories. it's a default package class
	}
	
	directoryPath = StringUtils.substringBeforeLast(directoryPath, ".");
	directoryPath = directoryPath.replace('.', '/');
	directoryPath = "/" + directoryPath + "/";
	return directoryPath;
    }
    
    /**
     * debug tool
     */
    private void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
