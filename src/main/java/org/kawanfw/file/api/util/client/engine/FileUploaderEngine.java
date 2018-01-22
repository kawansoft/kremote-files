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
package org.kawanfw.file.api.util.client.engine;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.util.client.FilesTransferWithProgress;

/**
 * An Uploader Engine allows to upload files to a remote server using progress indicator.
 */

public class FileUploaderEngine extends Thread {
    
    /** The error return code */
    public static final int RC_ERROR = -1;
    
    /** The OK error code thats says that engine terminated normally */
    public static final int RC_OK = 1;

    /** The debug flag */
    public boolean DEBUG = FrameworkDebug.isSet(FileUploaderEngine.class);

    /** The return code */
    private int returnCode = RC_ERROR;

    /** The Exception thrown if something *realy* bad happened */
    private Exception exception = null;

    /** The current Remote Session instance (already logged user) */
    private RemoteSession remoteSession = null;

    /** The file to upload */
    private List<File> files = null;

    /** The name of the file on the remote host */
    private List<String> pathnames = null;
    
    /** Progress value between 0 and 100. Will be used by progress indicators. */
    private AtomicInteger progress = new AtomicInteger();

    /** Says if user has cancelled the file pload or download */
    private AtomicBoolean cancelled = new AtomicBoolean();

    /** Used to retrieve the current file name at any moment */
    private FilesTransferWithProgress filesTransferWithProgress = null;
    
    /**
     * Constructor
     * 
     * @param remoteSession
     *            The Remote Session instance
     * @param files
     *            The files to upload on the remote server
     * @param pathnames
     *            The files path addresses on the remote host
     * @param progress
     *            Progress value between 0 and 100. Will be used by progress
     *            indicators
     * @param cancelled
     *            Says if user has cancelled the file upload
     */
    public FileUploaderEngine(RemoteSession remoteSession,
	    List<File> files, List<String> pathnames, AtomicInteger progress, AtomicBoolean cancelled) {

	if (remoteSession == null) {
	    throw new IllegalArgumentException(
		    "remoteSession can not be null!");
	}

	if (pathnames == null) {
	    throw new IllegalArgumentException(
		    "pathnames can not be null!");
	}

	if (files == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	this.remoteSession = remoteSession;
	this.files = files;
	this.pathnames = pathnames;
	this.progress = progress;
	this.cancelled = cancelled;
    }

    public void run() {
	try {
	    debug("FileUploaderEngine Begin");

	    // Get the total files length
	    long filesLength = getFilesLength();

	    filesTransferWithProgress = new FilesTransferWithProgress(
		    remoteSession, progress, cancelled);
	    filesTransferWithProgress.upload(files, pathnames, filesLength);
	    
	    returnCode = RC_OK;
	    debug("FileUploaderEngine End");
	} catch (Exception e) {
	    e.printStackTrace();
	    debug("FileUploaderEngine Exception thrown: " + e);
	    exception = e;
	}
	finally {
	    progress.set(100);
	}

    }
    
    /**
     * Returns the current remote file in upload
     * 
     * @return the current remote file in upload
     */
    public String getCurrentPathname() {
	if (filesTransferWithProgress == null) {
	    return null;
	} else {
	    return filesTransferWithProgress.getCurrentPathnameUpload();
	}
    }
    
    /**
     * @return the total files length
     */
    private long getFilesLength() {
	long filesLength = 0;
	for (File file : files) {
	    filesLength += file.length();
	}
	return filesLength;
    }

    /**
     * Returns the RC_OK or RC_ERROR return code 
     * @return the RC_OK or RC_ERROR return code 
     */
    public int getReturnCode() {
	return returnCode;
    }

    /**
     * Returns the {@code Exception} thrown by the engine. null if none.
     * @return the {@code Exception} thrown by the engine
     */
    public Exception getException() {
	return exception;
    }

    /**
     * debug tool
     */
    private void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
