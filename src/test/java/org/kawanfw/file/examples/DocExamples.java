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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteInputStream;
import org.kawanfw.file.api.client.RemoteOutputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;

/**
 * 
 * This example:
 * <ul>
 * <li>Creates a remote directory.</li>
 * <li>Uploads two files to the remote directory.</li>
 * <li>Lists the content of the remote directory.</li>
 * <li>Displays some info on a remote file.</li>
 * <li>Renames a remote file.</li>
 * <li>Downloads the files from the remote directory.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */
public class DocExamples {

    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator");

    /** The session to the remote Awake File server */
    private RemoteSession remoteSession = null;

    /**
     * Constructor
     * 
     * @param remoteSession
     *            the Awake File Session to use for this session
     */
    private DocExamples(RemoteSession remoteSession) {
	this.remoteSession = remoteSession;
    }

    /**
     * Main
     * 
     * @param args
     *            not used
     */
    public static void main(String[] args) throws IOException {

	RemoteSession remoteSession = DocExamples.remoteSessionBuilder();
	DocExamples sessionExample = new DocExamples(remoteSession);
	sessionExample.doIt();

	sessionExample.remoteFiles();
	sessionExample.uploadFile();

	sessionExample.uploadFileRaw();
	sessionExample.downloadFileRaw();
	sessionExample.remoteCall();

    }

    private void remoteCall() throws ConnectException, IllegalArgumentException,
    InvalidLoginException, UnknownHostException, SocketException,
    RemoteException, IOException {

	String resultStr = remoteSession
		.call("com.kremotefiles.quickstart.Calculator.add", 41, 42);
	int result = Integer.parseInt(resultStr);

	System.out.println("Result: " + result);

    }

    /**
     * RemoteSession Quick Start client example. Creates an Awake RemoteSession.
     * 
     * @return the Awake Remote Session established with the remote Awake FILE
     *         server
     * @throws IOException
     *             if communication or configuration error is raised
     */
    public static RemoteSession remoteSessionBuilder() throws IOException {

	// Parameters for connection to remote servers
	String url = "http://localhost:8080/kremote-files/ServerFileManager";
	String username = "username";
	char[] password = { 'd', 'e', 'm', 'o' };

	// Establish a session with the remote server
	RemoteSession remoteSession = new RemoteSession(url, username,
		password);
	return remoteSession;
    }

    private void remoteFiles() throws InvalidLoginException {

	// Display info on our remote /mydir/Koala.jpg
	RemoteFile koala = new RemoteFile(remoteSession, "/mydir/Koala.jpg");

	System.out.println("Display remote " + koala + " info...");
	System.out.println("last modified: " + new Date(koala.lastModified()));
	System.out.println("length       : " + koala.length());
	System.out.println("Parent       : " + koala.getParent());

	// List all files located in remote directory /mydir
	RemoteFile remoteDir = new RemoteFile(remoteSession, "/mydir");
	RemoteFile[] remoteFiles = remoteDir.listFiles();
	System.out.println();
	System.out.println("All files located in " + remoteDir + "     : "
		+ Arrays.asList(remoteFiles));

    }

    /**
     * Do some KRemote Files operations. This example:
     * <ul>
     * <li>Creates a remote directory.</li>
     * <li>Uploads two files to the remote directory.</li>
     * <li>Lists the content of the remote directory.</li>
     * <li>Displays some info on a remote file.</li>
     * <li>Renames a remote file.</li>
     * <li>Downloads the files from the remote directory.</li>
     * </ul>
     * 
     * The example uses {@code RemoteFile} objects for operations on remote
     * files. The {@code RemoteFile} methods used are the same as in
     * {@code java.io.File}.
     * 
     * @throws IOException
     *             if communication or configuration error is raised
     */
    public void doIt() throws IOException {

	// Define userHome var
	String userHome = System.getProperty("user.home") + File.separator;
	System.out.println("\"user.home\" is: " + userHome);

	// Create a remote directory on the server
	// RemoteFile methods are the same as java.io.File methods
	System.out.println("Creating remote /mydir...");
	RemoteFile remoteDir = new RemoteFile(remoteSession, "/mydir");
	remoteDir.mkdirs();

	// Creating a subdirectory on the server
	System.out.println("Creating remote /mydir/subdir...");
	RemoteFile remoteSubdir = new RemoteFile(remoteSession,
		"/mydir/subdir");
	remoteSubdir.mkdir();

	// Upload two files Koala.jpg and Tulips.jpg file located in our
	// user.home directory to the remote directory /mydir
	File image1 = new File(userHome + "Koala.jpg");
	File image2 = new File(userHome + "Tulips.jpg");

	System.out.println(
		"Uploading " + userHome + " files to remote /mydir...");

	Path pathImage1 = image1.toPath();
	try (OutputStream outImage1 = new RemoteOutputStream(remoteSession,
		"/mydir/Koala.jpg");) {
	    Files.copy(pathImage1, outImage1);
	}

	Path pathImage2 = image2.toPath();
	try (OutputStream outImage2 = new RemoteOutputStream(remoteSession,
		"/mydir/Tulips.jpg");) {
	    Files.copy(pathImage2, outImage2);
	}

	// List all files located in remote directory /mydir
	RemoteFile[] remoteFiles = remoteDir.listFiles();
	System.out.println();
	System.out.println("All files located in " + remoteDir + "     : "
		+ Arrays.asList(remoteFiles));

	// List sub-directories only of remote directory /mydir
	// Uses an Apache Commons IO FilterFilter
	FileFilter fileFilter = DirectoryFileFilter.DIRECTORY;

	remoteFiles = remoteDir.listFiles(fileFilter);
	System.out.println("Subdirectories of " + remoteDir + ": "
		+ Arrays.asList(remoteFiles));

	RemoteFile koala = new RemoteFile(remoteSession, "/mydir/Koala.jpg");
	System.out.println();
	System.out.println("Display remote " + koala + " info...");
	System.out.println("last modified: " + new Date(koala.lastModified()));
	System.out.println("length       : " + koala.length());
	System.out.println("Parent       : " + koala.getParent());

	// Rename a file on server
	System.out.println();
	RemoteFile koalaRenamed = new RemoteFile(remoteSession,
		"/mydir/Koala_RENAMED.jpg");
	System.out.println("Renaming " + koala + " to " + koalaRenamed + "...");
	boolean renameDone = koala.renameTo(koalaRenamed);
	System.out.println("Rename done: " + renameDone);

	// Download the files - with a new name - in our user.home directory
	File downloadedImage1 = new File(userHome + "downloaded-Koala.jpg");
	File downloadedImage2 = new File(userHome + "downloaded-Tulips.jpg");

	System.out.println();
	System.out.println(
		"Downloading files from remote /mydir in " + userHome + "...");

	Path pathDownloadedImage1 = downloadedImage1.toPath();
	try (InputStream inImage1 = new RemoteInputStream(remoteSession,
		"/mydir/Koala_RENAMED.jpg");) {
	    Files.copy(inImage1, pathDownloadedImage1,
		    StandardCopyOption.REPLACE_EXISTING);
	}

	Path pathDownloadedImage2 = downloadedImage2.toPath();
	try (InputStream inImage2 = new RemoteInputStream(remoteSession,
		"/mydir/Tulips.jpg");) {
	    Files.copy(inImage2, pathDownloadedImage2,
		    StandardCopyOption.REPLACE_EXISTING);
	}

	System.out.println("Done!");
    }

    private void downloadFileRaw() throws IOException {

	String userHome = System.getProperty("user.home") + File.separator;

	// Download /mydir/Koala.jpg to usr.home/Koala.jpg
	File file = new File(userHome + "Tulips.jpg");

	// Get an InputStream from the file located on our server and
	// an OutputSream from our local file
	try (InputStream in = new RemoteInputStream(remoteSession,
		"/mydir/Tulips.jpg");
		OutputStream out = new BufferedOutputStream(
			new FileOutputStream(file));) {

	    // Download the remote file reading
	    // the InpuStream and save it to our local file
	    byte[] buffer = new byte[4096];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
		out.write(buffer, 0, n);
	    }
	}

	System.out.println(
		"/mydir/Tulips.jpgs uccessfully downloaded to " + file);
    }

    private void uploadFile() throws IOException {

	String userHome = System.getProperty("user.home") + File.separator;

	// Upload user.home/Koala.jpg to remote directory /mydir
	File file = new File(userHome + "Koala.jpg");

	Path path = file.toPath();
	try (OutputStream outputStream = new RemoteOutputStream(remoteSession,
		"/mydir/Koala.jpg");) {
	    Files.copy(path, outputStream);
	}

	System.out.println(file + " successfully uploaded!");
    }

    private void uploadFileRaw() throws IOException {

	String userHome = System.getProperty("user.home") + File.separator;

	// Upload user.home/Koala.jpg to remote directory /mydir
	File file = new File(userHome + "Koala.jpg");

	try (InputStream in = new BufferedInputStream(
		new FileInputStream(file));
		OutputStream out = new RemoteOutputStream(remoteSession,
			"/mydir/Koala.jpg")) {

	    // Create the remote file reading the InpuStream and writing
	    // on the OutputStream
	    byte[] buffer = new byte[4096];
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
		out.write(buffer, 0, n);
	    }
	}

	System.out.println(file + " successfully uploaded!");
    }

}
