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

import org.apache.commons.lang3.StringUtils;

/**
 * Stores the remote file parts already sent/created
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class RemoteFilePartStore {

    private static final String KAWANFW_SEP = "**!kawanfw-sep**!";

    /** The Map of (username + file.toString, set(remoteFilePart) */
    private static Map<String, Set<String>> mapFiles = new Hashtable<String, Set<String>>();

    /** The map key username + file.toString() */
    private String key = null;

    /**
     * Constructor
     * 
     * @param username
     *            owner of the file
     * @param file
     *            the file to upload
     * @param remoteFile 
     * 		  the remote file path
     * 
     * @throws FileNotFoundException if file does not exist
     */
    public RemoteFilePartStore(String username, File file, String remoteFile) throws FileNotFoundException {

	if (username == null) {
	    throw new IllegalArgumentException("username is null!");
	}
	
	if (file == null) {
	    throw new IllegalArgumentException("file is null!");
	}	
	
//	if (! file.exists()) {
//	    throw new FileNotFoundException("file does not exist: " + file);
//	}
	
	// Assure unicity of upload action
	key = username + KAWANFW_SEP + file.toString() + "-" + remoteFile;
	
    }

    /**
     * Says if a chunk file part has already been sent to remote file (aka
     * file.n.kawanfw.chunk)
     * 
     * @param remoteFilePartthe
     *            remote file part in file.n.kawanfw.chunk format
     * @return true if chunk file part has already been sent to remote file (aka
     *         file.n.kawanfw.chunk)
     */
    public boolean alreadyUploaded(String remoteFilePart) {

	if (!mapFiles.containsKey(key)) {
	    return false;
	}

	Set<String> remoteFileParts = mapFiles.get(key);

	if (remoteFileParts == null) {
	    return false;
	}

	return remoteFileParts.contains(remoteFilePart);
    }

    /**
     * Store the file part
     * 
     * @param remoteFilePart
     *            the remote file part to store
     */
    public void storeFilePart(String remoteFilePart) {

	if (!mapFiles.containsKey(key)) {
	    Set<String> remoteFileParts = new HashSet<String>();
	    remoteFileParts.add(remoteFilePart);
	    mapFiles.put(key, remoteFileParts);
	} else {
	    Set<String> remoteFileParts = mapFiles.get(key);
	    remoteFileParts.add(remoteFilePart);
	    mapFiles.put(key, remoteFileParts);
	}
    }

    /**
     * Remove the map key
     */
    public void remove() {
	mapFiles.remove(key);
    }
    
    /**
     * Cleans all references of username
     * @param username the username to clean all references for
     */
    public static void clean(String username) {
	
	if (username == null) {
	    throw new IllegalArgumentException("username is null!");
	}
	 
	if (mapFiles == null || mapFiles.isEmpty()) {
	    return;
	}
	
	Set<String> keys = mapFiles.keySet();
	
	// Duplicate the Set otherwise we will have a java.util.ConcurrentModificationException...
	Set<String> keysDuplicate = new HashSet<String>();
	keysDuplicate.addAll(keys);	
	
	for (Iterator<String> iterator = keysDuplicate.iterator(); iterator.hasNext();) {
	    String key = iterator.next();
	    String usernameKey = StringUtils.substringBefore(key, KAWANFW_SEP);
	    
	    if (username.equals(usernameKey)) {
		mapFiles.remove(username);
	    }
	    
	}
    }

   

}
