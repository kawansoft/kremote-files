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
package org.kawanfw.file.api.server.util;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.kawanfw.commons.util.Sha1Util;

/**
 * SHA-1 hash functions on strings and files with default provider.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class Sha1 {


    private Sha1Util sha1Util = null;
    
    /**
     * Constructor.
     */
    public Sha1() {
	sha1Util = new Sha1Util();
    }

    
    /**
     * Computes the SHA-1 hash code of a byte array as a hex string.
     * 
     * @param b
     *            the bytes to hash
     * @return the hash value
     * @exception NoSuchAlgorithmException
     *                if the algorithm is not available from the provide
     * @exception NoSuchProviderException
     *                if the provider is not available in the environment
     */
    public byte[] getHash(byte[] b) throws NoSuchAlgorithmException,
	    NoSuchProviderException {
	return sha1Util.getHash(b);
    }

    /**
     * 
     * Computes the hash code of a byte array as a hex string.
     * 
     * @param b
     *            the bytes to hash <br>
     * @return the hash value in hex String
     * @exception NoSuchAlgorithmException
     *                if the algorithm is not available from the provider
     * @exception NoSuchProviderException
     *                if the provider is not available in the environment
     */
    public String getHexHash(byte[] b) throws NoSuchAlgorithmException,
	    NoSuchProviderException {
	return sha1Util.getHexHash(b);
    }

    /**
     * Gets the file hash value.
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
	return sha1Util.getFileHash(file);
    }

    /**
     * Gets the file hash as a hex string value.
     * 
     * @param file the file to hash
     * 
     * @return the file hash as a hex string
     * 
     * @exception IOException
     *                an I/O error occurred
     * @exception NoSuchAlgorithmException
     *                hash algorithm not founds
     * @exception NoSuchProviderException
     *                hash provider not found
     */

    public String getHexFileHash(File file) throws IOException,
	    NoSuchAlgorithmException, NoSuchProviderException {
	return sha1Util.getHexFileHash(file);
    }

}
