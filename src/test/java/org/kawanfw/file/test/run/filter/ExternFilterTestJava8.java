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

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.TestParms;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ExternFilterTestJava8 {

    /**
     * 
     */
    public ExternFilterTestJava8() {

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
	doIt(remoteSession);
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
    public static void doIt(RemoteSession remoteSession) throws MalformedURLException,
	    UnknownHostException, ConnectException, SocketException,
	    InvalidLoginException, RemoteException, IOException,
	    SecurityException, Exception {

	
//	RemoteFile file = new RemoteFile(remoteSession, "/");
//	FileFilter fileFilter = new ExternFileFilterJava8();
//	
//	RemoteFile[] files = file.listFiles(fileFilter);
//	System.out.println();
//	System.out.println("file.listFiles(theFileFilter) " + file);
//	if (files == null) {
//	    System.out.println("remote directory is empty: " + file);
//	} else {
//	    System.out.println("RemoteFile [] files.length: " + files.length);
//	    for (int i = 0; i < files.length; i++) {
//		System.out.println(files[i]);
//	    }
//	}
//	
//	Assert.assertTrue("2 files listed with JPG extension", files.length == 2);
	
    }




}
