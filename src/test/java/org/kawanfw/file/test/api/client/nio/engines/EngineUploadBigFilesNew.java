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
package org.kawanfw.file.test.api.client.nio.engines;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.kawanfw.file.api.client.RemoteFile;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.util.client.engine.FileUploaderEngine;
import org.kawanfw.file.test.api.client.nio.streams.ShaComparator;
import org.kawanfw.file.test.parms.BigFiles;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.Chrono;

/**
 * 
 * TestReload that big files can be downloaded from remote server.
 * 
 * @author Nicolas de Pomereu
 */

public class EngineUploadBigFilesNew {

    private File[] bigFiles = BigFiles.getBig();
    private File[] bigFiles_2 = BigFiles.getBig_2();

    private List<File> files = new ArrayList<File>();
    private List<String> remoteFiles = new ArrayList<String>();

    private AtomicInteger progress = new AtomicInteger();
    private AtomicBoolean cancelled = new AtomicBoolean();

    public static void main(String[] args) throws Exception {
	new EngineUploadBigFilesNew().test();
    }

    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());
		
	test(remoteSession);
    }
    
    @Test
    public void test(RemoteSession remoteSession) throws Exception {

	System.out.println(new Date() + " Uploading big files with engine...");
	
	Long totalLength = (long) 0;

	for (int i = 0; i < bigFiles.length; i++) {
	    //totalLength += remoteSession.length(bigFiles[i].getName());
	    totalLength += new RemoteFile(remoteSession, "/" + bigFiles[i].getName()).length();
	}

	for (File remoteFile : bigFiles_2) {
	    remoteFiles.add("/" + remoteFile.getName());
	}

	files = Arrays.asList(bigFiles);

	System.out.println(new Date() + " Uploading big files with engine...");
	Chrono chrono = new Chrono(new Date());

	FileUploaderEngine fileUploaderEngine = new FileUploaderEngine(
		remoteSession, files, remoteFiles, progress, cancelled);
	fileUploaderEngine.start();

	while (fileUploaderEngine.getCurrentPathname() == null) {
	    Thread.sleep(100);
	}

	while (progress.get() < 100) {
	    System.out.println(fileUploaderEngine.getCurrentPathname() + ": "
		    + progress.get() + "%");
	    Thread.sleep(200);
	}

	chrono.end();

	if (TestParms.TEST_BIG_FILES_SHA) {
	    ShaComparator.compare(files, Arrays.asList(bigFiles_2));
	}


	System.out.println("Done.");
    }

}
