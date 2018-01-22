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
package org.kawanfw.commons.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Aas it says, class to join file that have been splitted by FileSplitter.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class FileJoiner {
    
    public static final int EOF = -1;
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    
    public static final String DOT_LAST = ".LAST";
    public static final String DOT_AYSNC_DOT_LAST = ".AYSNC.LAST";    
    
    /**
     * Constructor
     */
    public FileJoiner() {
    }


    /**
     * Join the parts of the designated file that have been created by a previous split
     * 
     * @param file
     *            the file to join from it's parts
     * @throws IOException
     */
    public void join(File file) throws IOException {

	int countFile = 1;
	int n = 0;
	byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

	OutputStream output = null;
	InputStream input = null;

	try {
	    output = new BufferedOutputStream(new FileOutputStream(file));

	    while (true) {
		File filePart = new File(getFilePart(file, countFile));

		if (filePart.exists()) {

		    try {
			System.out.println(new Date() + " Joining part " + filePart);
			input = new BufferedInputStream(new FileInputStream(
				filePart));

			while (EOF != (n = input.read(buffer))) {
			    output.write(buffer, 0, n);
			}
		    } finally {
			IOUtils.closeQuietly(input);
		    }
		    countFile++;
		} else {
		    break;
		}
	    }

	} finally {
	    IOUtils.closeQuietly(output);
	}
    }

    

    /**
     * Deletes all the parts files of the designated file
     * 
     * @param file
     *            the file to delete the parts of
     * @throws IOException
     */
    public void deleteParts(File file) throws IOException {
	int countFile = 1;

	while (true) {
	    File filePart = new File(getFilePart(file, countFile));

	    if (filePart.exists()) {
		FileUtils.deleteQuietly(filePart);
		countFile++;
	    } else {
		break;
	    }
	}
    }    
    
    
    /**
     * Return the file part name from the file
     * 
     * @param file
     *            the original file path
     * @param countFile
     *            the number of the file
     * @return the full file part in form /dir/filename.1.kawanfw.chunk, /dir/filename.2.kawanfw.chunk, etc...
     */
    public String getFilePart(File file, int countFile) {
	return file.toString() + "." + countFile + ".kawanfw.chunk";
    }    

    /**
     * Return the file part name from the file
     * 
     * @param filePath
     *            the original file path
     * @param countFile
     *            the number of the file
     * @return the full file part in form /dir/filename.1.kawanfw.chunk, /dir/filename.kawanfw.chunk, etc...
     */
    public String getFilePart(String filename, int countFile) {
	return filename  + "." + countFile + ".kawanfw.chunk";
    }       
    
    /**
     * Get the original file name from the file chunkc name
     * @param filePart	the file part name
     * @return	the file name with path 
     */
    public String getFilePathFromPart(String filePart) {
	
	// Remove generic ext
	String filePath = StringUtils.substringBeforeLast(filePart, ".kawanfw.chunk");
	// Remove file number .1 or .2, etc.
	filePath = StringUtils.substringBeforeLast(filePath, ".");
	return filePath;
    }
    
    /**
     * Says is this file a is the fist chunk of a series of chunks
     * @param filePart
     * @return
     */
    private boolean isFirstChunk(String filePart) {
	if (filePart.endsWith(".1.kawanfw.chunk")) {
	    return true;
	}
	else {
	    return false;
	}
    }

    /**
     * Delete all previous existing chunks starting at 2.
     * @param filePart	the chunk that may be a fist chunk
     */
    public void deleteExistingChunks(String filePart) {

	if (!isFirstChunk(filePart)) {
	    return;
	}
	
	// Delete all chunks
	String rawFilename = StringUtils.substringBefore(filePart, ".1.kawanfw.chunk");
	
	int count = 2;
	while (true) {
	    File chunk = new File(getFilePart(rawFilename, count));
	    if (chunk.exists()) {
		chunk.delete();
	    }
	    else {
		break;
	    }
	    
	    count++;
	}	
    }    
}
