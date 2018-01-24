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
package org.kawanfw.file.test.api.client.nio;

import java.io.FileFilter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.Assert;
import org.junit.Test;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * 
 * TestReload that a remote directory can be created and deleted
 * @author Nicolas de Pomereu
 */

public class MkdirsRemoteNio {
            
    public static void main(String[] args) throws Exception {
	new MkdirsRemoteNio().test();
    }
    
    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES_URL_LOCAL,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());
		
	test(remoteSession);
    }

    /**
     * 
     * @param remoteSession the KRemote Files Session
     * 
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws RemoteException
     * @throws IOException
     */
    public void test(RemoteSession remoteSession)
	    throws IllegalArgumentException, InvalidLoginException,
	    UnknownHostException, ConnectException, RemoteException,
	    IOException {
		
	// Create a remote directory
	MessageDisplayer.display("");
	MessageDisplayer.display("creating 3 remote directories with RemoteFile.mkdirs()...");
	
	new RemoteFile(remoteSession, "/" + TestParms.MYDIR1).mkdirs();
	new RemoteFile(remoteSession, "/" + TestParms.MYDIR2).mkdirs();	
	new RemoteFile(remoteSession, "/" + TestParms.MYDIR3).mkdirs();
	new RemoteFile(remoteSession, "/" + TestParms.MYDIR4).mkdirs();
	
	// TestReload if the directory really exists
	MessageDisplayer.display("Testing remote directories list with RemoteFile.listFiles(DirectoryFileFilter.DIRECTOR()...");
	FileFilter fileFilter = DirectoryFileFilter.DIRECTORY;
	RemoteFile[] remoteFileArray = new RemoteFile(remoteSession, "/").listFiles(fileFilter);
			
	List<String> directories = new ArrayList<String>();
	
	for (RemoteFile remoteFile : remoteFileArray) {
	    if (remoteFile.toString().contains(TestParms.MYDIR1)) directories.add(remoteFile.getName());
	    if (remoteFile.toString().contains(TestParms.MYDIR2)) directories.add(remoteFile.getName());
	    if (remoteFile.toString().contains(TestParms.MYDIR3)) directories.add(remoteFile.getName());
	    if (remoteFile.toString().contains(TestParms.MYDIR4)) directories.add(remoteFile.getName());
	}
	
	Assert.assertTrue("directories contains " + TestParms.MYDIR1 , directories.contains(TestParms.MYDIR1));
	Assert.assertTrue("directories contains " + TestParms.MYDIR2 , directories.contains(TestParms.MYDIR2));
	Assert.assertTrue("directories contains " + TestParms.MYDIR3 , directories.contains(TestParms.MYDIR3));
	Assert.assertTrue("directories contains " + TestParms.MYDIR4 , directories.contains(TestParms.MYDIR4));
	
	MessageDisplayer.display("Remote directories: " + directories);
	MessageDisplayer.display("");
	
	// Try to delete the directory
	MessageDisplayer.display("Testing remote directory delete with deleteRemoteFile()...");
	new RemoteFile(remoteSession, "/" + TestParms.MYDIR2).delete();
	
	fileFilter = DirectoryFileFilter.DIRECTORY;
	remoteFileArray = new RemoteFile(remoteSession, "/").listFiles(fileFilter);
	
	//Assert.assertEquals("2 directories exists now", directories.size(), 2);
	
	Assert.assertTrue("directory exists " + TestParms.MYDIR1 , new RemoteFile(remoteSession, "/" + TestParms.MYDIR1).exists());
	Assert.assertFalse("directory doe NOT exists " + TestParms.MYDIR2 , new RemoteFile(remoteSession, "/" + TestParms.MYDIR2).exists());
	Assert.assertTrue("directory exists " + TestParms.MYDIR3, new RemoteFile(remoteSession, "/" + TestParms.MYDIR3).exists());
	
	MessageDisplayer.display(TestParms.MYDIR2 + " deleted!");
	for (RemoteFile remoteFile : remoteFileArray) {
		MessageDisplayer.display("Remote directory: " + remoteFile);
		if (remoteFile.toString().contains("//AVG")) {
		    Assert.assertTrue("is directory ", remoteFile.isDirectory());
		}

	}
    }

    @Test(expected = InvalidLoginException.class)
    public void test2() throws Exception {
	
	@SuppressWarnings("unused")
	RemoteSession remoteSession = new RemoteSession(
		TestParms.KREMOTE_FILES_URL_LOCAL, "xxxxxxxxxxxx",
		TestParms.REMOTE_PASSWORD.toCharArray());
	
    }

}
