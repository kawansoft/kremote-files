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
import org.kawanfw.commons.util.FrameworkSystemUtil;
import org.kawanfw.commons.util.Sha1Util;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.FileGenerator;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * 
 * TestReload the download of remote files and that the hash values match hash values of the original local files
 * @author Nicolas de Pomereu
 */

public class DownloadFilesNio {
    
    public static void main(String[] args) throws Exception {
	new DownloadFilesNio().test();
    }
    
    @Test
    public void test() throws Exception {
		
	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());
			
	test(remoteSession);	
	
    }

    /**
     * @param remoteSession the KRemote Files Session
     * 
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws InterruptedException
     * @throws RemoteException
     * @throws SecurityException
     * @throws IOException
     */
    public void test(RemoteSession remoteSession)
	    throws Exception {
	
	// The original local files
	File blob1 = TestParms.getFileFromUserHome(TestParms.BLOB_FILE_TULIPS);
	File blob2 = TestParms.getFileFromUserHome(TestParms.BLOB_FILE_KOALA);
	File blob3 = TestParms.getFileFromUserHome(TestParms.RUSSIAN);
	
	// The download files 
        File downloadDir = new File(FileGenerator.getHomeDirectory().toString() + File.separator + "download");
        downloadDir.mkdirs();
	File downloadedBlob1 = new File(downloadDir.toString() + File.separator + TestParms.BLOB_FILE_TULIPS);
	File downloadedBlob2 = new File(downloadDir.toString() + File.separator + TestParms.BLOB_FILE_KOALA);	
	File downloadedBlob3 = new File(downloadDir.toString() + File.separator + TestParms.RUSSIAN);	
	
	// Create a remote directory;
	MessageDisplayer.display("");
	MessageDisplayer.display("Downloading " + blob1);
	//remoteSession.download("/" + TestParms.MYDIR1 + "/" + blob1.getName(), downloadedBlob1);
	FileTransfer.download(remoteSession, "/" + TestParms.MYDIR1 + "/" + blob1.getName(), downloadedBlob1);
	
	MessageDisplayer.display("Downloading " + blob2);
	//remoteSession.download("/" + TestParms.MYDIR1 + "/" + blob2.getName(), downloadedBlob2);
	FileTransfer.download(remoteSession, "/" + TestParms.MYDIR1 + "/" + blob2.getName(), downloadedBlob2);
	
	if (!FrameworkSystemUtil.isAndroid()) {
	    MessageDisplayer.display("Downloading " + blob3);
	    //remoteSession.download("/" + TestParms.MYDIR1 + "/" + blob3.getName(), downloadedBlob3);
	    FileTransfer.download(remoteSession, "/" + TestParms.MYDIR1 + "/" + blob3.getName(), downloadedBlob3);
	}
	
	MessageDisplayer.display("Testing that hash values match...");
	Sha1Util sha1 = new Sha1Util();
	
	String sha1Blob1 = sha1.getHexFileHash(blob1);
	String sha1Blob2 = sha1.getHexFileHash(blob2);
	
	String sha1DownloadedBlob1 = sha1.getHexFileHash(downloadedBlob1);
	String sha1DownloadedBlob2 = sha1.getHexFileHash(downloadedBlob2);
		
	Assert.assertEquals("sha1Blob1 equals sha1DownloadedBlob1 ", sha1Blob1, sha1DownloadedBlob1);
	Assert.assertEquals("sha1Blob2 equals sha1DownloadedBlob2 ", sha1Blob2, sha1DownloadedBlob2);	

	if (!FrameworkSystemUtil.isAndroid()) {
	    String sha1Blob3 = sha1.getHexFileHash(blob3);
	    String sha1DownloadedBlob3 = sha1.getHexFileHash(downloadedBlob3);
	    Assert.assertEquals("sha1Blob3 equals sha1DownloadedBlob3 ",
		    sha1Blob3, sha1DownloadedBlob3);
	}

	MessageDisplayer.display("Testing Exception throw...");
	
	// TestReload a download on a file that does not exist:
	Exception exception = new Exception();
	try {
	   //remoteSession.download("/" + TestParms.MYDIR1 + "/" + "notExists", new File("c:\\test.txt"));
	    FileTransfer.download(remoteSession, "/" + TestParms.MYDIR1 + "/" + "notExists", new File("c:\\test.txt"));
	} catch (Exception e) {
	    exception = e;
	}
	
	Assert.assertTrue("Exception thrown is instance of FileNotFoundException", 
		exception instanceof FileNotFoundException);
	
	
	RemoteSession remoteSessionClone = remoteSession.clone();
	remoteSessionClone.logoff();
	try {
	    File file = new File(downloadDir.toString() + File.separator + TestParms.BLOB_FILE_TULIPS);
	    //remoteSessionClone.download("/" + TestParms.MYDIR1 + " notExists", file);
	    FileTransfer.download(remoteSessionClone, "/" + TestParms.MYDIR1 + " notExists", file);
	} catch (Exception e) {
	    exception = e;
	}
	
	Assert.assertTrue("Exception thrown is instance of InvalidLoginException", 
		exception instanceof InvalidLoginException);
	
    }
        
}
