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
package org.kawanfw.commons.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.apache.commons.io.IOUtils;
import org.kawanfw.commons.codec.binary.CodecHex;

/**
 * SHA-1 hash functions on strings and files with SUN provider.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class Sha1Util {

    // default read buffer size
    static int READ_BUFFER_SIZE = 4096;

    /**
     * Constructor.
     */
    public Sha1Util() {

    }

    /**
     * Computes the SHA-1 hash code of a byte array as an hex string.
     * 
     * @param b
     *            the bytes to hash
     * @return the hash value
     * @exception NoSuchAlgorithmException
     *                if the algorithm is not available from the provide
     * @exception NoSuchProviderException
     *                if the provider is not available in the environment
     */

    public byte[] getHash(byte b[]) throws NoSuchAlgorithmException,
	    NoSuchProviderException {
	MessageDigest mdInstance = MessageDigest.getInstance("SHA-1");

	// Bytes containing the MD5 or SHA-1 Hash Code
	byte[] bytesHash = new byte[160];

	mdInstance.update(b);

	bytesHash = mdInstance.digest();

	return bytesHash;
    }

    /**
     * 
     * Computes the hash code of a byte array as an hex string.
     * 
     * @param b
     *            the bytes to hash <br>
     * @return the hash value in hex String
     * @exception NoSuchAlgorithmException
     *                if the algorithm is not available from the provider
     * @exception NoSuchProviderException
     *                if the provider is not available in the environment
     */

    public String getHexHash(byte b[]) throws NoSuchAlgorithmException,
	    NoSuchProviderException {
	return new String(CodecHex.encodeHex(getHash(b)));
    }

    /**
     * Gets the a file hash value.
     * 
     * @param file the file to hash
     *            
     * @return the file hash as bytes
     * 
     * @exception IOException
     *                an I/O error occurred
     * @exception NoSuchAlgorithmException
     *                hash algorithm not found
     * @exception NoSuchProviderException
     *                hash provider not found
     */

    public byte[] getFileHash(File file) throws IOException,
	    NoSuchAlgorithmException, NoSuchProviderException {

	FileInputStream fisIn = new FileInputStream(file);
	BufferedInputStream bisIn = new BufferedInputStream(fisIn);

	DataInputStream disIn = null;

	try {
	    disIn = new DataInputStream(bisIn);

	    // Bytes containing the MD5 or SHA-1 Hash Code
	    byte[] bytesHash = new byte[160];

	    MessageDigest mdInstance = MessageDigest
		    .getInstance("SHA-1");

	    int bytesRead;
	    int total = disIn.available();

	    if (total == 0) {
		return null;
	    }

	    if (total > READ_BUFFER_SIZE) {
		total = READ_BUFFER_SIZE;
	    }

	    byte bRead[] = new byte[total];

	    while ((bytesRead = disIn.read(bRead)) > 0) {
		mdInstance.update(bRead, 0, bytesRead);
	    }

	    bytesHash = mdInstance.digest();

	    return bytesHash;
	} finally {
	    IOUtils.closeQuietly(disIn);
	}

    }

    /**
     * Gets the file hash as an hex string value.
     * 
     * @param file the file to hash
     * 
     * @return the file hash as an hex string
     * 
     * @exception IOException
     *                an I/O error occurred
     * @exception NoSuchAlgorithmException
     *                hash algorithm not found
     * @exception NoSuchProviderException
     *                hash provider not found
     */

    public String getHexFileHash(File file) throws IOException,
	    NoSuchAlgorithmException, NoSuchProviderException {
	String hashResult = new String();

	byte[] bHash = this.getFileHash(file);

	if (bHash == null) {
	    return "NULL";
	}

	hashResult = new String(CodecHex.encodeHex(bHash));
	return hashResult;
    }

}
