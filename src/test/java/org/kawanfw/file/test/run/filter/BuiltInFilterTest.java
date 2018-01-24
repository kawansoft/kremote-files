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
package org.kawanfw.file.test.run.filter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Assert;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.reflection.ClassSerializer;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BuiltInFilterTest {

    /**
     * 
     */
    public BuiltInFilterTest() {

    }

    public void test(RemoteSession remoteSession) throws Exception {
	doIt(remoteSession);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES_URL_LOCAL,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());
	new BuiltInFilterTest().doIt(remoteSession);
    }

    public static class TheRemoteFilenameFilter implements FilenameFilter {
	//@Override
	public boolean accept(File dir, String name) {
	    return true;
	}
    }

    public static class TheRemoteFileFilter implements FileFilter {
	//@Override
	public boolean accept(File pahtname) {
	    return true;
	}
    }

    /**
     * @throws MalformedURLException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws InvalidLoginException
     * @throws RemoteException
     * @throws IOException
     * @throws SecurityException
     */
    public void doIt(RemoteSession remoteSession) throws MalformedURLException,
	    UnknownHostException, ConnectException, SocketException,
	    InvalidLoginException, RemoteException, IOException,
	    SecurityException {
	
	RemoteFile file = new RemoteFile(remoteSession, "/");

	// List all text files in out root directory
	// using an Apache Commons FileFiter

	AndFileFilter andFileFilter = new AndFileFilter();
	andFileFilter.addFileFilter(new SuffixFileFilter(".txt"));
	
	RemoteFile [] files = file.listFiles((FileFilter)andFileFilter);
	for (RemoteFile remoteFile : files) {
	    MessageDisplayer.display("Remote text file: " + remoteFile);
	}
		
	MessageDisplayer.display();
	MessageDisplayer
		.display("file.listFiles( new SuffixFileFilter(\".txt\") "
			+ file);
	MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	for (int i = 0; i < files.length; i++) {
	    Assert.assertTrue("file is file", files[i].isFile());
	    Assert.assertTrue("file is a text file", files[i].toString()
		    .endsWith(".txt"));
	    MessageDisplayer.display(files[i]);

	}
	
	FileFilter fileFilter= FileFileFilter.FILE;

	files = file.listFiles(fileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(FileFileFilter.FILE) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	} else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
		Assert.assertTrue("file is non-directory", files[i].isFile());
	    }
	}
	
	fileFilter = DirectoryFileFilter.DIRECTORY;

	files = file.listFiles(fileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(DirectoryFileFilter.DIRECTORY) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	} else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		Assert.assertTrue("file is directory", files[i].isDirectory());
		MessageDisplayer.display(files[i]);
	    }
	}

	
	 MessageDisplayer.display();
	 
	// Force huge filter test
	andFileFilter = new AndFileFilter();
	int cpt = 0;
	while (cpt < 500) {
	    cpt++;
	    andFileFilter.addFileFilter(new SuffixFileFilter(".txt"));
	}
	
	String serialFilterBase64 = new ClassSerializer<FileFilter>().toBase64(andFileFilter);
	System.out.println("serialFilterBase64 size: " + (serialFilterBase64.length() / 1024) + " Kb.");
	
	files = file.listFiles((FileFilter)andFileFilter);
	for (RemoteFile remoteFile : files) {
	    MessageDisplayer.display("Remote text file with huge FileFilter: " + remoteFile);
	}
		
	MessageDisplayer.display();
	MessageDisplayer
		.display("file.listFiles( new SuffixFileFilter(\".txt\") "
			+ file);
	MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	for (int i = 0; i < files.length; i++) {
	    Assert.assertTrue("file is file", files[i].isFile());
	    Assert.assertTrue("file is a text file", files[i].toString()
		    .endsWith(".txt"));
	    MessageDisplayer.display(files[i]);

	}
	
	MessageDisplayer.display();
	System.out.println("serialFilterBase64 size: " + (serialFilterBase64.length() / 1024) + " Kb.");
	
	String [] filesStr = file.list((FilenameFilter)andFileFilter);
	for (String fileStr : filesStr) {
	    MessageDisplayer.display("Remote text file with huge FilenameFilter: " + fileStr);
	}
		
	MessageDisplayer.display();
	MessageDisplayer
		.display("file.list( new SuffixFileFilter(\".txt\") "
			+ file);
	MessageDisplayer.display("String [] filesStr: " + filesStr.length);
	for (int i = 0; i < filesStr.length; i++) {
	    Assert.assertTrue("file is a text file", filesStr[i]
		    .endsWith(".txt"));
	    MessageDisplayer.display(filesStr[i]);

	}
	
	
	
    }


}
