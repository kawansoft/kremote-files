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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

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
 * TestReload that a remote file can be renamed on the remote server.
 * 
 * @author Nicolas de Pomereu
 */

public class RenameFilesNio {

    public static void main(String[] args) throws Exception {
	new RenameFilesNio().test();
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
	    InterruptedException, RemoteException, IOException, Exception {
	
	File blob1 = TestParms.getFileFromUserHome(TestParms.BLOB_FILE_TULIPS);
	File blob2 = TestParms.getFileFromUserHome(TestParms.BLOB_FILE_KOALA);
	
	// Create a remote directory;
	MessageDisplayer.display("");
	MessageDisplayer.display("Uploading " + blob1);
	//remoteSession.upload(blob1, "/" + blob1.getName());
	FileTransfer.upload(remoteSession, blob1, "/" + blob1.getName());
	
	RemoteFile remoteBlob1 = new RemoteFile(remoteSession, "/" + blob1.getName()); 
	RemoteFile remoteBlob2 = new RemoteFile(remoteSession, "/" + blob2.getName()); 
	
	boolean existsBlob1 = remoteBlob1.exists();
	Assert.assertTrue("exists blob1", existsBlob1);

	MessageDisplayer.display("uploading " + blob2);
	//remoteSession.upload(blob2, "/" + blob2.getName());
	FileTransfer.upload(remoteSession, blob2, "/" + blob2.getName());
	
	boolean existsBlob2 = remoteBlob1.exists();
	Assert.assertTrue("exists blob2", existsBlob2);
		
	MessageDisplayer.display("");
	MessageDisplayer.display("Trying to rename remote " + blob1.getName() + " to " + blob2.getName() + ". Dest file exists.");
	boolean renameFail = remoteBlob1.renameTo(remoteBlob2);
	MessageDisplayer.display("==> " + renameFail);
	
	// No more text on Linux
	if (TestParms.KREMOTE_FILES.equals("http://www.awake-file.org/kremote-files/ServerFileManager")) {
	    return;
	}
	
	//Assert.assertFalse("renameFail", renameFail);
	
	MessageDisplayer.display("");
	MessageDisplayer.display("Done.");
    }

}
