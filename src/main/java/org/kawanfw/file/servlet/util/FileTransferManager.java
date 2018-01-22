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
package org.kawanfw.file.servlet.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.server.util.ServerLogger;
import org.kawanfw.commons.util.DefaultParms;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.server.FileConfigurator;


/**
 * The Action Manager for files transfer: define how all concrete operations will be done
 * on files.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class FileTransferManager {

    private static boolean DEBUG = FrameworkDebug
	    .isSet(FileTransferManager.class);

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Constructor.
     */
    public FileTransferManager() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kawanfw.file.api.server.fileaction.FileActionManager#
     * actionDownloadFile(java.io.OutputStream,
     * org.kawanfw.file.api.server.FileConfigurator, java.lang.String,
     * java.lang.String)
     */
 
    public boolean download(OutputStream out,
	    FileConfigurator fileConfigurator, String username,
	    String filename, long chunkLength) throws FileNotFoundException,
	    IOException {
	InputStream in = null;

	debug(new Date() + " DOWNLOAD SESSION BEGIN ");

	try {
	    filename = HttpConfigurationUtil.addUserHomePath(fileConfigurator,
		    username, filename);

	    debug(new Date() + " DOWNLOAD CHUNK");

	    // Do we must download a chunk only ? We will then seek the
	    // Random access file and read only one chunk length and send it
	    // back to client
	    if (filename.endsWith(".kawanfw.chunk")) {

		// We are now in chunk case
		String rawFilename = StringUtils.substringBeforeLast(filename,
			".kawanfw.chunk");
		String indexStr = StringUtils.substringAfterLast(rawFilename,
			".");
		int index = Integer.parseInt(indexStr);

		// Remove the number
		rawFilename = StringUtils.substringBeforeLast(rawFilename, ".");

		// We seek the total length of previous files, because client
		// method
		// is idempotent and may be replayed
		long lengthToSeek = (index - 1) * chunkLength;

		// debug("index       : " + index);
		// debug("chunkLength : " + chunkLength);
		// debug("lengthToSeek: " + lengthToSeek);

		debug("");
		debug(new Date() + " SESSION " + " " + index);

		RandomAccessFile raf = null;

		try {

		    File file = new File(rawFilename);

		    if (!file.exists()) {
			debug("File does not exists: " + file);
			return false;
		    }

		    debug(new Date() + " BEFORE SEEK ");

		    debug(new Date() + " BEFORE CREATE RAF");
		    raf = new RandomAccessFile(file, "rw");
		    debug(new Date() + " AFTER  CREATE RAF");

		    raf.seek(lengthToSeek);

		    debug(new Date() + " BEFORE COPY ");
		    long totalRead = copy(raf, out, chunkLength);
		    debug(new Date() + " AFTER COPY " + totalRead);

		    IOUtils.closeQuietly(raf);

		    if (lengthToSeek + totalRead >= file.length()) {
			// End of operations
			// Nothing yo do with Random Access File
		    }

		} finally {
		    IOUtils.closeQuietly(raf);
		}

		return true;

	    } else {

		debug(new Date() + " DOWNLOAD FULL FILE");

		File file = new File(filename);

		if (!file.exists()) {
		    debug("File does not exists: " + file);
		    return false;
		}

		in = new BufferedInputStream(new FileInputStream(file));
		IOUtils.copy(in, out);
	    }

	    return true;

	} finally {
	    IOUtils.closeQuietly(in);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kawanfw.file.api.server.fileaction.FileActionManager#
     * actionUploadFile(org.kawanfw.file.api.server.FileConfigurator,
     * java.io.InputStream, java.lang.String, java.lang.String)
     */
    public void upload(FileConfigurator fileConfigurator,
	    InputStream inputStream, String username, String filename,
	    long chunkLength) throws IOException {

	debug(new Date() + " UPLOAD SESSION BEGIN ");

	filename = HttpConfigurationUtil.addUserHomePath(fileConfigurator,
		username, filename);

	// is it a file chunk? If yes append to filename
	if (filename.endsWith(".kawanfw.chunk")
		|| filename.endsWith(".kawanfw.chunk.LASTCHUNK")) {

	    RandomAccessFile raf = null;

	    try {

		boolean lastChunk = false;

		if (filename.endsWith(".LASTCHUNK")) {
		    debug(new Date() + " RENAME DONE");
		    filename = StringUtils.substringBeforeLast(filename,
			    ".LASTCHUNK");
		    lastChunk = true;
		}

		initFileIfFirstChunk(username, filename);

		String rawFilename = StringUtils.substringBeforeLast(filename,
			".kawanfw.chunk");
		String indexStr = StringUtils.substringAfterLast(rawFilename,
			".");

		// Remove the number
		rawFilename = StringUtils.substringBeforeLast(rawFilename, ".");

		int index = Integer.parseInt(indexStr);

		File file = new File(rawFilename);

		debug(new Date() + " SESSION INDEX" + " " + index);

		// We must create, if necessary, the path to the file
		createParentDir(file);

		debug(new Date() + " BEFORE CREATE RAF");
		raf = new RandomAccessFile(file, "rw");
		debug(new Date() + " AFTER CREATE RAF");

		// We seek the total length of previous files, because client
		// method
		// is idempotent and may be replayed
		long lengthToSeek = (index - 1) * chunkLength;

		// debug("index       : " + index);
		// debug("chunkLength : " + chunkLength);
		// debug("lengthToSeek: " + lengthToSeek);

		debug(new Date() + " BEFORE SEEK ");
		raf.seek(lengthToSeek);

		debug(new Date() + " BEFORE COPY ");
		copy(inputStream, raf, new byte[DEFAULT_BUFFER_SIZE]);
		debug(new Date() + " AFTER COPY ");

		IOUtils.closeQuietly(raf);

		if (lastChunk) {
		    // End of operations
		    // Do nothing with Random Access Files
		}

	    } finally {
		IOUtils.closeQuietly(raf);
	    }

	} else {

	    OutputStream out = null;

	    try {
		File file = new File(filename);

		// We must create, if necessary, the path to the file
		createParentDir(file);

		out = new BufferedOutputStream(new FileOutputStream(file));
		IOUtils.copy(inputStream, out);

		debug("file created : " + file);
		debug("file.length(): " + file.length());
	    } finally {
		IOUtils.closeQuietly(out);
	    }
	}

    }

    /**
     * Copy the content of the random access file to the output stream up to a
     * chubk length
     * 
     * @param raf
     * @param out
     * @param chunkLength
     * @return the total amount read
     * 
     * @throws IOException
     */
    private long copy(RandomAccessFile raf, OutputStream out, long chunkLength)
	    throws IOException {
	int writeBufferSize = DefaultParms.DEFAULT_WRITE_BUFFER_SIZE;

	// For Chunk Length comparison. If buffer read > chunk
	// length ==> exit
	long totalRead = 0;

	byte[] tmp = new byte[writeBufferSize];
	int len;

	while ((len = raf.read(tmp)) >= 0) {
	    totalRead += len;
	    out.write(tmp, 0, len);

	    // Exit if chunk length is reached. We will just send
	    // the chunck,InputStream will be reused
	    if (chunkLength > 0 && totalRead > chunkLength - writeBufferSize) {
		return totalRead;
	    }
	}

	return totalRead;
    }

    /**
     * Copy the input stream into the raf
     * 
     * @param input
     * @param output
     * @param buffer
     * @return the lrngth written into the reaf
     * @throws IOException
     */
    private long copy(InputStream input, RandomAccessFile output, byte[] buffer)
	    throws IOException {
	long count = 0;
	int n = 0;
	while (EOF != (n = input.read(buffer))) {
	    output.write(buffer, 0, n);
	    count += n;
	}
	return count;
    }

    /**
     * Create the parent directory of file, if necessary
     * 
     * @param file
     *            the file to create for the parent directories if necessary
     */
    public void createParentDir(File file) {
	// We must create, if necessary, the path to the file
	File parentDir = file.getParentFile();
	if (parentDir != null) {
	    parentDir.mkdirs();
	}
    }

    /**
     * Delete the raw file if first chunk
     * 
     * @param username
     *            the client username
     * @param filename
     *            the chunk file name
     */
    private void initFileIfFirstChunk(String username, String filename)
	    throws IOException {

	if (filename.endsWith(".1.kawanfw.chunk")) {
	    String rawFilename = StringUtils.substringBeforeLast(filename,
		    ".kawanfw.chunk");
	    rawFilename = StringUtils.substringBeforeLast(rawFilename, "."); // Remove
									     // the
									     // number

	    File file = new File(rawFilename);

	    if (file.exists()) {
		boolean deleted = file.delete();
		if (!deleted) {
		    throw new IOException(
			    "File delete required because of upload of first chunk. Impossible to delete file: "
				    + file);
		}
	    }
	}

    }

   
    private void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

 
}
