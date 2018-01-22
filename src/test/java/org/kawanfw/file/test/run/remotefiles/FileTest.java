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
package org.kawanfw.file.test.run.remotefiles;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class FileTest {

    private File file = null;
    
    private boolean readable = true;
    private long time = System.currentTimeMillis();
    private File dest = null;
    private boolean writable = true;
    private boolean executable = true;
    private File pathname = null;
    private Object obj = null;
    private boolean ownerOnly = false;
    //private FilenameFilter filenameFilter = null;
    //private FileFilter filter = null;

    /**
     * @param file
     */
    public FileTest(File file) {
	this.file = file;
	pathname = new File(file.toString());
	dest = new File(file.toString());
	obj = (Object)dest;
    }
    
    /**
     * TestReload the methods that apply to files that are not directory
     * 
     * @throws Exception
     */
    public void testFileMethods() throws Exception {
	System.out.println();
	System.out.println("--------------------------- " + file.toString());		
	System.out.println("canExecute: " + file.canExecute());
	System.out.println("canRead: " + file.canRead());
	System.out.println("canWrite: " + file.canWrite());
	System.out.println("compareTo: " + file.compareTo(pathname));
	try {
	    System.out.println("createNewFile: " + file.createNewFile());
	} catch (Exception e) {
	    System.out.println("createNewFile: " + e.toString());
	}
	// System.out.println("delete: " + file.delete());
	System.out.println("equals: " + file.equals(obj));
	System.out.println("exists: " + file.exists());
	System.out.println("getAbsoluteFile: " + file.getAbsoluteFile());
	System.out.println("getAbsolutePath: " + file.getAbsolutePath());
	System.out.println("getCanonicalFile: " + file.getCanonicalFile());
	System.out.println("getCanonicalPath: " + file.getCanonicalPath());
	System.out.println("getFreeSpace: " + file.getFreeSpace());
	System.out.println("getName: " + file.getName());
	System.out.println("getParent: " + file.getParent());
	System.out.println("getParentFile: " + file.getParentFile());
	System.out.println("getPath: " + file.getPath());
	System.out.println("getTotalSpace: " + file.getTotalSpace());
	System.out.println("getUsableSpace: " + file.getUsableSpace());
	System.out.println("hashCode: " + file.hashCode());
	System.out.println("isAbsolute: " + file.isAbsolute());
	System.out.println("isDirectory: " + file.isDirectory());
	System.out.println("isFile: " + file.isFile());
	System.out.println("isHidden: " + file.isHidden());
	System.out.println("lastModified: " + file.lastModified());
	System.out.println("length: " + file.length());
	System.out.println("renameTo: " + file.renameTo(dest));
	System.out.println("setExecutable: " + file.setExecutable(executable));
	System.out.println("setExecutable: "
		+ file.setExecutable(executable, ownerOnly));
	System.out.println("setLastModified: " + file.setLastModified(time));
	//System.out.println("setReadOnly: " + file.setReadOnly());
	System.out.println("setReadable: " + file.setReadable(readable));
	System.out.println("setReadable: "
		+ file.setReadable(readable, ownerOnly));
	System.out.println("setWritable: " + file.setWritable(writable));
//	System.out.println("setWritable: "
//		+ file.setWritable(writable, ownerOnly));
	System.out.println("toString: " + file.toString());
	//System.out.println("toURI: " + file.toURI());
	//System.out.println("toURL: " + file.toURL());
    }

    /**
     * TestReload the methods that apply to directories only
     * 
     * @throws Exception
     */
    public void testMkdirMethods() {
	System.out.println("mkdir: " + file.mkdir());
	System.out.println("mkdirs: " + file.mkdirs());
    }
    
    public static class TheFilenameFilter implements FilenameFilter {

	//@Override
	public boolean accept(File dir, String name) {
	   
	    if (name.contains("Mb"))
		return true;
	    else
		return false;
	}
    }
    
    public static class TheFileFilter implements FileFilter  {

	//@Override
	public boolean accept(File pathname) {
	    if (pathname.toString().contains("Mb"))
		return true;
	    else
		return false;
	}
    }
    
    /**
     * TestReload the methods that apply to directories only
     * @throws IOException 
     * @throws ClassNotFoundException 
     * 
     * @throws Exception
     */
    public void testListMethods() throws IOException, ClassNotFoundException {
	
	System.out.println("");	
	
	FilenameFilter theFilenameFilter = new TheFilenameFilter() ;
	FileFilter theFileFilter = new TheFileFilter() ;
	
	String [] filenames = file.list();
	System.out.println();
	System.out.println("file.list() " + file);
	if (filenames == null) {
	    System.out.println("directory is empty: " + file);
	}
	else {
	    System.out.println("filenames.length: " + filenames.length);
	    for (int i = 0; i < filenames.length; i++) {
		System.out.println(filenames[i]);
	    }
	}
	
	filenames = file.list(theFilenameFilter);
	System.out.println();
	System.out.println("file.list(theFilenameFilter) " + file);
	if (filenames == null) {
	    System.out.println("directory is empty: " + file);
	} else {
	    System.out.println("filenames.length: " + filenames.length);
	    for (int i = 0; i < filenames.length; i++) {
		System.out.println(filenames[i]);
	    }
	}
	
	File [] files = file.listFiles();
	System.out.println();
	System.out.println("file.listFiles() " + file);
	if (files == null) {
	    System.out.println("directory is empty: " + file);
	}
	else {
	    System.out.println("File [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		System.out.println(files[i]);
	    }
	}
	
	files = file.listFiles(theFilenameFilter);
	System.out.println();
	System.out.println("file.listFiles(theFilenameFilter) " + file);
	if (files == null) {
	    System.out.println("directory is empty: " + file);
	}
	else {
	    System.out.println("File [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		System.out.println(files[i]);
	    }
	}

	files = file.listFiles(theFileFilter);
	System.out.println();
	System.out.println("file.listFiles(theFileFilter) " + file);
	if (files == null) {
	    System.out.println("directory is empty: " + file);
	}
	else {
	    System.out.println("File [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		System.out.println(files[i]);
	    }
	}
	
	MyFileFilter myFileFilter = new MyFileFilter();
	
	files = file.listFiles(myFileFilter);
	System.out.println();
	System.out.println("file.listFiles(MyFileFilter) " + file);
	if (files == null) {
	    System.out.println("directory is empty: " + file);
	}
	else {
	    System.out.println("File [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		System.out.println(files[i]);
	    }
	}
    }
	
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	String root ="c:\\";
	String koalaInRoot = "C:\\.kremote-server-root-file\\username\\koala.jpg";
	String koalaInMyRep = "C:\\.kremote-server-root-file\\username\\my_rép1\\koala.jpg";
	
	koalaInRoot = "C:\\koala.jpg";
	koalaInMyRep = "C:\\my_rép1\\koala.jpg";
	
	String [] filesToTest = new String [] {root, koalaInRoot, koalaInMyRep};
	
	for (int i = 0; i < filesToTest.length; i++) {
	    FileTest fileTest = new FileTest(new File(filesToTest[i]));
	    fileTest.testFileMethods();
	}
	
	String [] dirsToTest = new String [] {root, "C:\\.kremote-server-root-file\\username", "c:\\notexists"};
	for (int i = 0; i < dirsToTest.length; i++) {
	    FileTest fileTest = new FileTest(new File(dirsToTest[i]));
	    fileTest.testListMethods();
	}	
    }

}
