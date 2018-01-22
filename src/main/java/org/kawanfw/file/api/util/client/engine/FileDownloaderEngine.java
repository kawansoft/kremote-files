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
 * A Downloader Engine allows to download files from the server using progress indicator.
 */

public class FileDownloaderEngine extends Thread {

    /** The error return code */
    public static final int RC_ERROR = -1;
    
    /** The OK error code thats says that engine terminated normally */
    public static final int RC_OK = 1;

    /** The debug flag */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileDownloaderEngine.class);

    /** The return code */
    private int returnCode = RC_ERROR;

    /** The Exception thrown if something *realy* bad happened */
    private Exception exception = null;

    /** Te remote session (already logged user) */
    private RemoteSession remoteSession = null;

    /** The name of the file on the host */
    private List<String> pathnames = null;

    /** The file to download */
    private List<File> files = null;

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
     * @param pathnames
     *            The file path names on the remote host
     * @param files
     *            The files to create on the Client side
     * @param progress
     *            Progress value between 0 and 100. Will be used by progress
     *            indicators
     * @param cancelled
     *            Says if user has cancelled the file download
     */
    public FileDownloaderEngine(RemoteSession remoteSession,
	    List<String> pathnames, List<File> files, AtomicInteger progress,
	    AtomicBoolean cancelled) {

	if (remoteSession == null) {
	    throw new IllegalArgumentException("remoteSession can not be null!");
	}

	if (pathnames == null) {
	    throw new IllegalArgumentException(
		    "pathnames can not be null!");
	}

	if (files == null) {
	    throw new IllegalArgumentException("files can not be null!");
	}

	if (progress == null) {
	    throw new IllegalArgumentException("progress can not be null!");
	}

	if (cancelled == null) {
	    throw new IllegalArgumentException("cancelled can not be null!");
	}

	this.remoteSession = remoteSession;
	this.pathnames = pathnames;
	this.files = files;
	this.progress = progress;
	this.cancelled = cancelled;

    }

    public void run() {
	try {
	    debug("FileDownloaderEngine Begin");

	    // Get the total files length
	    long filesLength = remoteSession.length(pathnames);

	    filesTransferWithProgress = new FilesTransferWithProgress(
		    remoteSession, progress, cancelled);
	    filesTransferWithProgress.download(pathnames, files, filesLength);

	    returnCode = RC_OK;
	    debug("FileDownloaderEngine End");
	} catch (Exception e) {
	    e.printStackTrace();
	    debug("FileDownloaderEngine Exception thrown: " + e);
	    exception = e;
	} finally {
	    progress.set(100);
	}

    }

    /**
     * Returns the current remote file in download
     * 
     * @return the current remote file in download
     */
    public String getCurrentPathname() {
	if (filesTransferWithProgress == null) {
	    return null;
	} else {
	    return filesTransferWithProgress.getCurrentPathnameDownload();
	}
    }

    /**
     * Returns the RC_ERROR or RC_OK return code 
     * @return the RC_ERROR or RC_OK return code 
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
     * Returns the file list
     * @return the file list
     */
    public List<File> getFiles() {
	return files;
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
