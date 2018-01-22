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
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class StaticFilterTest{

    /**
     * 
     */
    public StaticFilterTest() {

    }

    public void test(RemoteSession remoteSession) throws Exception {
	doIt(remoteSession);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());
	doIt(remoteSession);
    }

    public static class ThePublicStaticFileFilter implements FileFilter  {
	    
	//@Override
	public boolean accept(File pathname) {
	    	    		
	    if (pathname.toString().contains(".jpg"))
		return true;
	    else
		return false;
	}
    }
    
    private static class ThePrivateStaticFileFilter implements FileFilter  {
	    
	//@Override
	public boolean accept(File pathname) {
	    	    		
	    if (pathname.toString().contains(".jpg"))
		return true;
	    else
		return false;
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
    public static void doIt(RemoteSession remoteSession) throws MalformedURLException,
	    UnknownHostException, ConnectException, SocketException,
	    InvalidLoginException, RemoteException, IOException,
	    SecurityException, Exception {
	
	RemoteFile file = new RemoteFile(remoteSession, "/");
	FileFilter fileFilter = new ThePublicStaticFileFilter();
	
	RemoteFile[] files = file.listFiles(fileFilter);
	MessageDisplayer.display();
	MessageDisplayer.display("file.listFiles(theFileFilter) " + file);
	if (files == null) {
	    MessageDisplayer.display("remote directory is empty: " + file);
	} else {
	    MessageDisplayer.display("RemoteFile [] files.length: " + files.length);
	    for (int i = 0; i < files.length; i++) {
		MessageDisplayer.display(files[i]);
	    }
	}
	
	Assert.assertTrue("2 files listed with JPG extension", files.length == 2);
	
	fileFilter = new ThePrivateStaticFileFilter();
	
	try {
	    files = null;
	    files = file.listFiles(fileFilter);
	} catch (Exception e) {
	    //e.printStackTrace();
	    MessageDisplayer.display(e.toString());
	    Assert.assertTrue("Exception is RuntimeException", e instanceof RuntimeException );
	    Assert.assertTrue("Exception.getCause() is IllegalArgumentException", e.getCause() instanceof IllegalArgumentException );
	}

	Assert.assertTrue("No files listed", files == null);
    }




}
