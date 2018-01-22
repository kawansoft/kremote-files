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
package org.kawanfw.file.api.util.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;

/**
 * Stores the remote file parts already sent/created
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class FileChunkStore {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileChunkStore.class);

    private static final String KAWANFW_SEP = "**!kawanfw-sep**!";

    /** The Map of (username, set(FileChunk) */
    private static Map<String, Set<File>> mapFiles = new Hashtable<String, Set<File>>();

    /** The map key username + file.toString() */
    private String key = null;

    /**
     * Constructor
     * 
     * @param username
     *            owner of the file
     * @param file
     *            the file to to create from download
     * @param remoteFile
     *            the remote filr to download
     * @throws FileNotFoundException
     *             if file does not exist
     */
    public FileChunkStore(String username, File file, String remoteFile)
	    throws FileNotFoundException {

	if (username == null) {
	    throw new IllegalArgumentException("username is null!");
	}

	if (file == null) {
	    throw new IllegalArgumentException("file is null!");
	}

	if (remoteFile == null) {
	    throw new IllegalArgumentException("remoteFile is null!");
	}

	// Assure unicity of download action
	key = username + KAWANFW_SEP + file.toString() + "-" + remoteFile;
    }

    /**
     * Store the file chunk
     * 
     * @param fileChunk
     *            the remote file chunk to store
     */
    public void add(File fileChunk) {

	if (!mapFiles.containsKey(key)) {
	    Set<File> fileChunks = new HashSet<File>();
	    fileChunks.add(fileChunk);
	    mapFiles.put(key, fileChunks);
	} else {
	    Set<File> fileChunks = mapFiles.get(key);
	    fileChunks.add(fileChunk);
	    mapFiles.put(key, fileChunks);
	}
    }

    /**
     * Remove the file chunk and delete the asscoiated file
     * 
     * @param fileChunk
     *            the file chunk to remove
     */
    public boolean alreadyDownloaded(File fileChunk) {

	if (fileChunk == null) {
	    throw new IllegalArgumentException("fileChunk is null");
	}

	if (!mapFiles.containsKey(key)) {
	    return false;
	}

	Set<File> fileChunks = mapFiles.get(key);

	if (fileChunks == null || fileChunks.isEmpty()) {
	    return false;
	}

	return fileChunks.contains(fileChunk);

    }

    /**
     * Remove the map key
     */
    public void remove() {

	Set<File> fileChunks = mapFiles.get(key);

	if (fileChunks != null && !fileChunks.isEmpty()) {
	    for (Iterator<File> iterator = fileChunks.iterator(); iterator
		    .hasNext();) {
		File file = iterator.next();
		boolean deleted = FileUtils.deleteQuietly(file);
		debug(deleted + " " + file);
	    }
	}

	mapFiles.remove(key);
    }

    /**
     * Cleans all references of username
     * 
     * @param username
     *            the username to clean all references for
     */
    public static void clean(String username) {

	if (username == null) {
	    throw new IllegalArgumentException("username is null!");
	}

	if (mapFiles == null || mapFiles.isEmpty()) {
	    return;
	}

	Set<String> keys = mapFiles.keySet();

	// Duplicate the Set otherwise we will have a
	// java.util.ConcurrentModificationException...
	Set<String> keysDuplicate = new HashSet<String>();
	keysDuplicate.addAll(keys);

	for (Iterator<String> iterator = keysDuplicate.iterator(); iterator
		.hasNext();) {
	    String key = iterator.next();
	    String usernameKey = StringUtils.substringBefore(key, KAWANFW_SEP);

	    if (username.equals(usernameKey)) {
		mapFiles.remove(username);
	    }

	}
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
