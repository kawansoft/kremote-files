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


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import org.junit.Test;
import org.kawanfw.file.api.client.RemoteOutputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.api.util.client.ChunkUtil;
import org.kawanfw.file.test.api.client.nio.streams.ShaComparator;
import org.kawanfw.file.test.parms.FileGenerator;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.Chrono;
import org.kawanfw.file.test.util.MessageDisplayer;
import org.kawanfw.file.test.util.ProgressUtil;

/**
 * 
 * TestReload that big file can be uploaded from remote server if interrupted
 * 
 * @author Nicolas de Pomereu
 */

public class InterruptRemoteOutputStreamOneBigFile {

    public static void main(String[] args) throws Exception {
	new InterruptRemoteOutputStreamOneBigFile().test();
    }

    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES_URL_LOCAL,
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
     * @throws IOException
     */
    public void test(RemoteSession remoteSession) throws IllegalArgumentException,
	    InvalidLoginException, FileNotFoundException, UnknownHostException,
	    ConnectException, InterruptedException, RemoteException,
	    IOException, Exception {

	File bigFileClient = new File(FileGenerator.getHomeDirectory()
		.toString() + File.separator + "333Mb.txt");
	File bigFileServer = new File(
		TestParms.C_KREMOTE_SERVER_ROOT_FILE_USERNAME + File.separator
			+ "333Mb.txt");
	
	boolean delete = bigFileServer.delete();
	if (delete) {
	    MessageDisplayer.display(bigFileServer.getName() + " deleted on server.");
	}
	
	MessageDisplayer.display("Upload in "
		+ ChunkUtil.getDownloadChunkLength(remoteSession)
		/ RemoteSession.MB + " Mb Chunks a BIG file " + bigFileClient
		+ " " + bigFileClient.length() / RemoteSession.MB + " Mb");
	MessageDisplayer.display(new Date() + " Begin Upload");
	Chrono chrono = new Chrono(new Date());

	// Dwonload with 10 Mb Chunks
	boolean continueDownload = true;

	InputStream in = null;
	OutputStream out = null;

	while (continueDownload) {
	    try {

		in = new BufferedInputStream(new FileInputStream(bigFileClient));
		out = new RemoteOutputStream(remoteSession, bigFileClient.getName(), bigFileClient.length());

		long remoteFileLength = bigFileClient.length();

		int cpt = 1;
		int tempLen = 0;
		byte[] buffer = new byte[1024 * 4];
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

		continueDownload = false;
	    } catch (SocketException se) { // Includes ConnectException

		String text = "File upload interrupted because of network error. "
			+ "Do you want to retry and continue upload?";
		String title = "warning";

		int result = JOptionPane.showConfirmDialog(null, text, title,
			JOptionPane.YES_NO_OPTION);
		if (result != JOptionPane.YES_OPTION) {
		    continueDownload = false;
		}
		continue;
	    } finally {
		//IOUtils.closeQuietly(in);
		//IOUtils.closeQuietly(out);
		
		if (in != null) {
		    try {
			in.close();
		    } catch (Exception e) {
		    }
		}

		if (out != null) {
		    try {
			out.close();
		    } catch (Exception e) {
		    }
		}
	    }
	}

	MessageDisplayer.display(new Date() + " End Upload");
	chrono.end();

	ShaComparator.compare(bigFileClient, bigFileServer);

	remoteSession.logoff();
	MessageDisplayer.display("Done.");
    }

}
