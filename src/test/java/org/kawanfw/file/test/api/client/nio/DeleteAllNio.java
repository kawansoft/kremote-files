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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.junit.Test;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * 
 * Delete all remote files and directories
 * 
 * @author Nicolas de Pomereu
 */

public class DeleteAllNio {

    public static void main(String[] args) throws Exception {
	new DeleteAllNio().test();
    }
   
    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(
		TestParms.KREMOTE_FILES, TestParms.REMOTE_USER,
		TestParms.REMOTE_PASSWORD.toCharArray());

	test(remoteSession);

    }

    /**
     * @param remoteSession
     *            the KRemote Files Session
     * 
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws InterruptedException
     * @throws RemoteException
     * @throws IOException
     */
    public void test(RemoteSession remoteSession)
	    throws IllegalArgumentException, InvalidLoginException,
	    FileNotFoundException, UnknownHostException, ConnectException,
	    InterruptedException, RemoteException,
	    IOException {
	
	// Create a remote directory;
	MessageDisplayer.display("");
	System.out
		.println("Deleting remote files list in /" + TestParms.MYDIR1 + " with RemoteFiles.listFiles()...");
	
	FileFilter fileFilter = DirectoryFileFilter.DIRECTORY;
	RemoteFile[] remoteFileArray = new RemoteFile(remoteSession, "/" + TestParms.MYDIR1).listFiles(fileFilter);
	
	if (remoteFileArray != null) {
	    for (RemoteFile remoteFile : remoteFileArray) {
		remoteFile.delete();
		MessageDisplayer.display(remoteFile + " deleted.");
	    }
	}

	MessageDisplayer.display("");
	System.out
		.println("Deleting remote files list in /" + TestParms.MYDIR3 + " with RemoteFiles.listFiles()...");

	fileFilter = DirectoryFileFilter.DIRECTORY;
	remoteFileArray = new RemoteFile(remoteSession, "/" + TestParms.MYDIR3).listFiles(fileFilter);

	if (remoteFileArray != null) {
	    for (RemoteFile remoteFile : remoteFileArray) {
		remoteFile.delete();
		MessageDisplayer.display(remoteFile + " deleted.");
	    }
	}

	boolean deleted = new RemoteFile(remoteSession, "/" + TestParms.MYDIR1).delete();
	boolean deleted3 = new RemoteFile(remoteSession, "/" + TestParms.MYDIR3).delete();

	if (deleted) {
	    MessageDisplayer.display(TestParms.MYDIR1 + " deleted : " + deleted);
	}

	if (deleted3) {
	    MessageDisplayer.display(TestParms.MYDIR3 + " deleted : " + deleted);    
	}

    }
   
}
