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
package org.kawanfw.file.test.api.client.nio.streams;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Date;

import org.junit.Test;
import org.kawanfw.file.api.client.RemoteInputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.api.client.nio.FileTransfer;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.Chrono;

/**
 * 
 * TestReload the download of remote files and that the hash values match hash values
 * of the original local files
 * 
 * @author Nicolas de Pomereu
 */

public class TestRemoteInputStream {

    public static void main(String[] args) throws Exception {
	new TestRemoteInputStream().test();
    }

    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());

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
     * @throws SecurityException
     * @throws IOException
     */
    public void test(RemoteSession remoteSession) throws Exception {

	File file = TestParms.getFileFromUserHome("image_1_1.jpg");
	file.delete();

	String remoteFile = "/image_1_1.jpg";

	System.out.println("chrono " + new Date() + " Begin");
	Chrono chrono = new Chrono(new Date());
	
	InputStream in = null;
	OutputStream out = null;
	
	try {
	    
	    // Get an InputStream from the file located on our servers
	    in = new RemoteInputStream(remoteSession, remoteFile);
	    out = new BufferedOutputStream(new FileOutputStream(file));
	    
	    byte [] buffer = new byte[1024 *4];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
		out.write(buffer, 0, n);
	    }	    
	    
	} finally {
	    if (in != null) in.close();
	    if (out != null) out.close();
	}
	    
	chrono.end();
	
	if (file.exists()) {
	    System.out.println("chrono " + new Date() + " Done!");
	    System.out.println("File created: " + file);
	} else {
	    System.err.println("Impossible to create " + file
		    + " from remote file " + remoteFile);
	}
	
	boolean doFileDownload = false;
	if (! doFileDownload) {
	    return;
	}
	
	file = TestParms.getFileFromUserHome("Example_3.exe");
	System.out.println();
	System.out.println("chrono " + new Date() + " Begin");
	
	//remoteSession.download(remoteFile, file);
	FileTransfer.download(remoteSession, remoteFile, file);
	
	if (file.exists()) {
	    System.out.println("chrono " + new Date() + " Done!");
	    System.out.println("File created: " + file);
	} else {
	    System.err.println("Impossible to create " + file
		    + " from remote file " + remoteFile);
	}
	
    }

}
