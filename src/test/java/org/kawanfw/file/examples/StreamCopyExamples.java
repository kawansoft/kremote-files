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

package org.kawanfw.file.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kawanfw.file.api.client.RemoteInputStream;
import org.kawanfw.file.api.client.RemoteOutputStream;
import org.kawanfw.file.api.client.RemoteSession;

/**
 * @author Nicolas de Pomereu
 *
 */
public class StreamCopyExamples {

    /**
     * 
     */
    public StreamCopyExamples() {

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	@SuppressWarnings("unused")
	String url = "http://localhost:8080/kremote-files/ServerFileManager";

	remoteInputStreamExample();
	remoteOutputStreamExample();

    }

    private static void remoteOutputStreamExample() throws IOException {

	// Define URL of the path to the ServerFileManager servlet
	String url = "https://www.acme.org/ServerFileManager";

	// The login info for strong authentication on server side:
	String username = "myUsername";
	char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };

	// Establish a session with the remote server
	RemoteSession remoteSession = new RemoteSession(url, username,
		password);

	File file = new File("C:\\Users\\Mike\\Koala.jpg");
	String pathname = "/Koala.jpg";

	// Get an InputStream from our local file and
	// create an OutputStream that maps a remote file on the host
	try (InputStream in = new BufferedInputStream(
		new FileInputStream(file));
		OutputStream out = new RemoteOutputStream(remoteSession,
			pathname, file.length());) {

	    // Create the remote file reading the InpuStream and writing
	    // on the OutputStream
	    byte[] buffer = new byte[1024 * 4];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
		out.write(buffer, 0, n);
	    }

	    // It is better to also close out before finally
	    // because RemoteOutputStream.close() sends data to the server and
	    // is thus prone to IOException
	    out.close();

	} catch (IOException e) {
	    // Treat IOException, including those thrown by out.close()
	    e.printStackTrace();
	    // Etc.
	}

    }
    
    private static void remoteInputStreamExample() throws IOException {

	// Define URL of the path to the ServerFileManager servlet
	String url = "https://www.acme.org/ServerFileManager";

	// The login info for strong authentication on server side:
	String username = "myUsername";
	char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };

	// Establish a session with the remote server
	RemoteSession remoteSession = new RemoteSession(url, username,
		password);

	File file = new File("C:\\Users\\Mike\\Koala.jpg");
	String pathname = "/Koala.jpg";

	// Get an InputStream from the file located on our server and
	// an OutputSream from our local file
	try (InputStream in = new RemoteInputStream(remoteSession, pathname);
		OutputStream out = new BufferedOutputStream(
			new FileOutputStream(file));) {

	    // Download the remote file reading
	    // the InpuStream and save it to our local file
	    byte[] buffer = new byte[1024 * 4];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
		out.write(buffer, 0, n);
	    }
	}

    }

}
