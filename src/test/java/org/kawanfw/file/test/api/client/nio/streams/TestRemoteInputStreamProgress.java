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
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteInputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.SessionParameters;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.api.util.client.ChunkUtil;
import org.kawanfw.file.test.parms.ProxyLoader;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.Chrono;
import org.kawanfw.file.test.util.ProgressUtil;

/**
 * 
 * TestReload the download of remote files and that the hash values match hash values
 * of the original local files
 * 
 * @author Nicolas de Pomereu
 */

public class TestRemoteInputStreamProgress {

    public static void main(String[] args) throws Exception {
	new TestRemoteInputStreamProgress().test();
    }

    
    @Test
    public void test() throws Exception {
	test(null);
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

	if (remoteSession == null) {

	    ProxyLoader proxyLoader = new ProxyLoader();
	    Proxy proxy = proxyLoader.getProxy();
	    PasswordAuthentication passwordAuthentication = proxyLoader.getPasswordAuthentication();
	    
	    SessionParameters sessionParameters = new SessionParameters();
	    sessionParameters.setDownloadChunkLength(RemoteSession.MB * 3);
	    
	    remoteSession = new RemoteSession(TestParms.KREMOTE_FILES_URL_LOCAL,
		    TestParms.REMOTE_USER,
		    TestParms.REMOTE_PASSWORD.toCharArray(), proxy, passwordAuthentication, sessionParameters);
	}
	
	InputStream in = null;
	OutputStream out = null;

	String remoteFile = null;
	File file = null;
	
	//remoteFile = "/image_1_1.jpg";
	//file = TestParms.getFileFromUserHome("image_1_1.jpg");
		 
	//remoteFile = "/javadoc-java6.zip";
	//file = TestParms.getFileFromUserHome("javadoc-java6_2.zip");
	
	remoteFile = "/empty.txt";
	file = TestParms.getFileFromUserHome("empty.txt");
	
	System.out.println(this.getClass().getSimpleName());
	System.out.println(new Date() + " Chunk Length: " + ChunkUtil.getDownloadChunkLength(remoteSession) / RemoteSession.MB + " Mb.");
	System.out.println(new Date() + " Getting remote file length...");
	long remoteFileLength = new RemoteFile(remoteSession, remoteFile).length();
	System.out.println(new Date() + remoteFile + " " + remoteFileLength / RemoteSession.MB + " Mb.");
	
	Chrono chrono = new Chrono(new Date());
	file.delete();
	
	try {
	    
	    // Get an InputStreams from the file located on our servers
	    in = new RemoteInputStream(remoteSession, remoteFile);
	    out = new BufferedOutputStream(new FileOutputStream(file));
	    	
	    int cpt = 1;
	    int tempLen = 0;
	    byte [] buffer = new byte[1024 * 4];
	    int n = 0;
	    System.out.println();
	    
	    while ((n = in.read(buffer)) != -1) {
		tempLen += n;

		if (remoteFileLength > 0
			&& tempLen > remoteFileLength / 100) {
		    tempLen = 0;
		    ProgressUtil.percentPrintl(cpt++, System.out);
		}
		out.write(buffer, 0, n);
	    }
	    
	} finally {
	    IOUtils.closeQuietly(in);
	    IOUtils.closeQuietly(out);
	}

	System.out.println();
	
	if (file.exists()) {
	    chrono.end();
	    System.out.println("File created: " + file);
	} else {
	    System.err.println("Impossible to create " + file
		    + " from remote file " + remoteFile);
	}
		
    }

}
