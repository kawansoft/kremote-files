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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.kawanfw.commons.util.FrameworkSystemUtil;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * 
 * TestReload that a local files can be uploaded to the remote server. Then test the
 * remote list, the remot sizes and the remote delete
 * 
 * @author Nicolas de Pomereu
 */

public class UploadFilesNio {

    public static void main(String[] args) throws Exception {
	new UploadFilesNio().test();
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
	File blob3 = TestParms.getFileFromUserHome(TestParms.BLOB_FILE_RUSSIAN);
		
	// Create a remote directory;
	MessageDisplayer.display("");
	MessageDisplayer.display("uploading " + blob1);
	
	RemoteFile remoteBlob1 = new RemoteFile(remoteSession, "/" + TestParms.MYDIR1 + "/" + blob1.getName());
	RemoteFile remoteBlob2 = new RemoteFile(remoteSession, "/" + TestParms.MYDIR1 + "/" + blob2.getName());
	RemoteFile remoteBlob3 = new RemoteFile(remoteSession, "/" + TestParms.MYDIR1 + "/" + blob3.getName());
	RemoteFile remoteBlob4 = new RemoteFile(remoteSession, "/" + TestParms.MYDIR1 + "/" + "file-does-not-exists");
	
	//remoteSession.upload(blob1, "/" + TestParms.MYDIR1 + "/" + blob1.getName());
	FileTransfer.upload(remoteSession, blob1, "/" + TestParms.MYDIR1 + "/" + blob1.getName());
		
	MessageDisplayer.display("uploading " + blob2);

	RemoteSession remoteSessionClone = remoteSession.clone();
	//remoteSessionClone.upload(blob2, "/" + TestParms.MYDIR1 + "/" + blob2.getName());
	FileTransfer.upload(remoteSessionClone, blob2, "/" + TestParms.MYDIR1 + "/" + blob2.getName());
		
	remoteSessionClone.logoff();

	if (!FrameworkSystemUtil.isAndroid()) {
	    MessageDisplayer.display("uploading " + blob3);
	    //remoteSession.upload(blob3, "/" + TestParms.MYDIR1 + "/" + blob3.getName());
	    FileTransfer.upload(remoteSession, blob3, "/" + TestParms.MYDIR1 + "/" + blob3.getName());
	}
	
	boolean existsBlob1 = remoteBlob1.exists();
	Assert.assertTrue("exists blob1", existsBlob1);

	boolean existsBlob2 = remoteBlob2.exists();
	Assert.assertTrue("exists blob2", existsBlob2);

	if (!FrameworkSystemUtil.isAndroid()) {
	    boolean existsBlob3 = remoteBlob3.exists();
	    Assert.assertTrue("exists blob3", existsBlob3);
	}

	boolean existsNone = remoteBlob4.exists();
	Assert.assertTrue("exists blob2", !existsNone);

	// TestReload if the directory really exists
	MessageDisplayer
		.display("Testing remote files list with RemoteFiles.listFiles()...");
	String [] directories = new RemoteFile(remoteSession, "/"+ TestParms.MYDIR1).list();
	List<String> remotefiles = Arrays.asList((String[]) directories);

	Assert.assertEquals("remotefiles contains " + TestParms.BLOB_FILE_TULIPS,
		remotefiles.contains(TestParms.BLOB_FILE_TULIPS), true);
	Assert.assertEquals("remotefiles contains " + TestParms.BLOB_FILE_KOALA,
		remotefiles.contains(TestParms.BLOB_FILE_KOALA), true);

	MessageDisplayer.display("remotefiles: " + remotefiles);
	MessageDisplayer.display("");

	MessageDisplayer
		.display("Testing remote files size with RemoteFile.length()...");
	long remoteBlob1Length = remoteBlob1.length();
	long remoteBlob2Length = remoteBlob2.length();
	
	Assert.assertEquals("remote length is ok for " + TestParms.BLOB_FILE_TULIPS,
		blob1.length(), remoteBlob1Length);
	Assert.assertEquals("remote length is ok for " + TestParms.BLOB_FILE_KOALA,
		blob2.length(), remoteBlob2Length);

	MessageDisplayer
		.display("Testing remote files size comparison with RemoteSession.length(files)...");
	List<String> files = new Vector<String>();
	files.add("/" + TestParms.MYDIR1 + "/" + blob1.getName());
	files.add("/" + TestParms.MYDIR1 + "/" + blob2.getName());
	long totalRemotelength = remoteSession.length(files);

	Assert.assertEquals("total remote length is ok for two files",
		remoteBlob1Length + remoteBlob2Length, totalRemotelength);

	MessageDisplayer
		.display("Testing deleting remote files with delete()...");
	remoteBlob1.delete();

	MessageDisplayer
		.display("Testing if file has been deleted...");
	boolean remoteBlob1exists = remoteBlob1.exists();
	Assert.assertFalse(remoteBlob1.toString() + " exists: ", remoteBlob1exists);

	MessageDisplayer.display("ReUpload of deleted files...");	
	//remoteSession.upload(blob1, "/" + TestParms.MYDIR1 + "/" + blob1.getName());	
	FileTransfer.upload(remoteSession, blob1, "/" + TestParms.MYDIR1 + "/" + blob1.getName());
		
	MessageDisplayer.display("Done.");
    }

}
