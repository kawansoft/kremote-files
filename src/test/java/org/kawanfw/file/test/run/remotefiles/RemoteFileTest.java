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

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class RemoteFileTest {

    /** If true, the methds that modify the file are called */
    private boolean callModifierMethods = false;
    
    private RemoteFile file = null;
    
    private boolean readable = true;
    private long time = System.currentTimeMillis();
    private RemoteFile dest = null;
    private boolean writable = true;
    private boolean executable = true;
    private RemoteFile pathname = null;
    private Object obj = null;
    private boolean ownerOnly = false;
    
    /**
     * 
     */
    public RemoteFileTest() {

    }

    /**
     * @param file
     * @throws InvalidLoginException 
     */
    public RemoteFileTest(RemoteFile file) throws InvalidLoginException {
	this.file = file;
	pathname = new RemoteFile(file.getRemoteSession(), file.toString());
	dest = new RemoteFile(file.getRemoteSession(), file.toString());
	obj = (Object)dest;
    }
    
    public void test(RemoteSession remoteSession) throws Exception {
	RemoteFile file = null;
	RemoteFile dest = null;
	RemoteFileTest testFile = null;
	
	String root ="/";
	String koalaInRoot = "/Koala.jpg";
	String koalaInMyRep = "/my_rép1/Koala.jpg";
	
	// test the rename alone
	boolean doRename = false;
	
	if (doRename) {
	    file = new RemoteFile(remoteSession, koalaInRoot);
	    dest = new RemoteFile(remoteSession, koalaInRoot + ".ren");
	    boolean rename = file.renameTo(dest);
	    MessageDisplayer.display("rename 1: " + rename + " ("
		    + dest.getName() + ")");
	    MessageDisplayer.display("exists " + dest + ": " + dest.exists());
	    MessageDisplayer.display("exists " + file + ": " + file.exists());

	    file = new RemoteFile(remoteSession, koalaInRoot + ".ren");
	    dest = new RemoteFile(remoteSession, koalaInRoot);
	    rename = file.renameTo(dest);
	    MessageDisplayer.display();
	    MessageDisplayer.display("rename 2: " + rename + " ("
		    + dest.getName() + ")");
	    MessageDisplayer.display("exists " + dest + ": " + dest.exists());
	    MessageDisplayer.display("exists " + file + ": " + file.exists());
	}
	
	
	boolean doRenameOnly = false;
	if (doRenameOnly) return;
	
	String [] filesToTest = new String [] {root, koalaInRoot, koalaInMyRep};
	
	for (int i = 0; i < filesToTest.length; i++) {
	    file = new RemoteFile(remoteSession, filesToTest[i]);
	    testFile = new RemoteFileTest(file);
	    testFile.testFileMethods();   
	}
	
	String [] dirsToTest = new String [] {root, "/my_rép1", "/notexists"};
	for (int i = 0; i < dirsToTest.length; i++) {
	    file = new RemoteFile(remoteSession, dirsToTest[i]);
	    testFile = new RemoteFileTest(file);
	    testFile.testListMethods(); 
	}
	
	file = new RemoteFile(remoteSession, "/notexists");
	boolean mkdir = file.mkdir();
	MessageDisplayer.display(file.getPath() + ".mkdir(): " + mkdir + " - exists(): "+ file.exists());
	
	
	file = new RemoteFile(remoteSession, "/notexists/dir1/dir2");
	mkdir = file.mkdirs();
	MessageDisplayer.display(file.getPath() + ".mkdirs(): " + mkdir + " - exists(): " + file.exists());
	
	boolean delete = file.delete();
	MessageDisplayer.display(file.getPath() + ".delete(): " + delete + " - exists(): " + file.exists());
    }
    
    /**
     * TestReload the methods that apply to files that are not directory
     * 
     * @throws Exception
     */
    public void testFileMethods() throws Exception {
	MessageDisplayer.display();
	MessageDisplayer.display("--------------------------- " + file.toString());
	MessageDisplayer.display("canExecute: " + file.canExecute());
	MessageDisplayer.display("canRead: " + file.canRead());
	MessageDisplayer.display("canWrite: " + file.canWrite());
	MessageDisplayer.display("compareTo: " + file.compareTo(pathname));
	try {
	    MessageDisplayer.display("createNewFile: " + file.createNewFile());
	} catch (Exception e) {
	    MessageDisplayer.display("createNewFile: " + e.toString());
	}
	// MessageDisplayer.display("delete: " + file.delete());
	MessageDisplayer.display("equals: " + file.equals(obj));
	MessageDisplayer.display("exists: " + file.exists());
	MessageDisplayer.display("getAbsoluteFile: " + file.getAbsoluteFile());
	MessageDisplayer.display("getAbsolutePath: " + file.getAbsolutePath());
	MessageDisplayer.display("getCanonicalFile: " + file.getCanonicalFile());
	MessageDisplayer.display("getCanonicalPath: " + file.getCanonicalPath());
	MessageDisplayer.display("getFreeSpace: " + file.getFreeSpace());
	MessageDisplayer.display("getName: " + file.getName());
	MessageDisplayer.display("getParent: " + file.getParent());
	MessageDisplayer.display("getParentFile: " + file.getParentFile());
	MessageDisplayer.display("getPath: " + file.getPath());
	MessageDisplayer.display("getTotalSpace: " + file.getTotalSpace());
	MessageDisplayer.display("getUsableSpace: " + file.getUsableSpace());
	MessageDisplayer.display("hashCode: " + file.hashCode());
	MessageDisplayer.display("isAbsolute: " + file.isAbsolute());
	MessageDisplayer.display("isDirectory: " + file.isDirectory());
	MessageDisplayer.display("isFile: " + file.isFile());
	MessageDisplayer.display("isHidden: " + file.isHidden());
	MessageDisplayer.display("lastModified: " + file.lastModified());
	MessageDisplayer.display("length: " + file.length());
	MessageDisplayer.display("renameTo: " + file.renameTo(dest));
	
	if (callModifierMethods) {
	    MessageDisplayer.display("setExecutable: "
		    + file.setExecutable(executable));
	    MessageDisplayer.display("setExecutable: "
		    + file.setExecutable(executable, ownerOnly));
	    MessageDisplayer.display("setLastModified: "
		    + file.setLastModified(time));
	    MessageDisplayer.display("setReadOnly: " + file.setReadOnly());
	    MessageDisplayer.display("setReadable: "
		    + file.setReadable(readable));
	    MessageDisplayer.display("setReadable: "
		    + file.setReadable(readable, ownerOnly));
	    MessageDisplayer.display("setWritable: "
		    + file.setWritable(writable));
	    MessageDisplayer.display("setWritable: "
		    + file.setWritable(writable, ownerOnly));
	    MessageDisplayer.display("toString: " + file.toString());
	}

	
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
	    	    		
	    if (pathname.toString().contains(".txt"))
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
	
	MessageDisplayer.display("");	
		
	String [] filenames = file.list();
	MessageDisplayer.display();
	MessageDisplayer.display("file.list() " + file);
	if (filenames == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("filenames.length: " + filenames.length);
	    for (int i = 0; i < filenames.length; i++) {
		MessageDisplayer.display(filenames[i]);
	    }
	}
	
	RemoteFile [] files = file.listFiles();
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles() " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	
	FilenameFilter filenameFilter = DirectoryFileFilter.DIRECTORY;
	
	filenames = file.list(filenameFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.list(DirectoryFileFilter.DIRECTORY) " + file);
	if (filenames == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("filenames.length: " + filenames.length);
	    for (int i = 0; i < filenames.length; i++) {
		MessageDisplayer.display(filenames[i]);
	    }
	}
	
	FileFilter fileFilter = FileFileFilter.FILE;
	files = file.listFiles(fileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(FileFileFilter.FILE) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	
	fileFilter = DirectoryFileFilter.DIRECTORY;
	
	files = file.listFiles(fileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(DirectoryFileFilter.DIRECTORY) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	
	filenameFilter = DirectoryFileFilter.DIRECTORY;
	files = file.listFiles(filenameFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(filenameFilter) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	
	
	boolean bypass = true;
	if (bypass) {
	    System.err.println("WARNING: tests on remote file filters are bypassed!");
	    return;
	}
	
	FilenameFilter theFilenameFilter = new TheFilenameFilter() ;
	FileFilter theFileFilter = new TheFileFilter() ;
	
	filenames = file.list(theFilenameFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.list(theFilenameFilter) " + file);
	if (filenames == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	} else {
	    MessageDisplayer.display("filenames.length: " + filenames.length);
	    for (int i = 0; i < filenames.length; i++) {
		MessageDisplayer.display(filenames[i]);
	    }
	}
	
	files = file.listFiles(theFilenameFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(theFilenameFilter) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	

	files = file.listFiles(theFileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(theFileFilter) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	
	MyFileFilter myFileFilter = new MyFileFilter();
	
	files = file.listFiles(myFileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(MyFileFilter) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	}
	else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());
	new RemoteFileTest().test(remoteSession);   
    }



}
