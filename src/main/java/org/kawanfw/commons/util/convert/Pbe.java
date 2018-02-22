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
package org.kawanfw.commons.util.convert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.kawanfw.commons.codec.binary.CodecHex;

public class Pbe {

    public static final String KAWANFW_ENCRYPTED = "*!aw!*";

    /**
     * 
     * Encrypt a string into a an hexa format
     * 
     * @param in
     *            the string to encrypt or Decrypt. if to decrypt: string must
     *            be Hex encoded
     * @param password
     *            the password to use
     * @return the encrypted string in hexa format.
     * 
     * @throws Exception
     */
    public String encryptToHexa(String in, char[] password) throws Exception {
	return cipher(Cipher.ENCRYPT_MODE, in, password);
    }

    /**
     * 
     * Decrypt an hexa string created/encrypted with encryptToHexa
     * 
     * @param in
     *            the string to encrypt or Decrypt. if to decrypt: string must
     *            be Hex encoded
     * @param password
     *            the password to use
     * @return the decrypted string in clear readable format
     * 
     * @throws Exception
     */
    public String decryptFromHexa(String in, char[] password) throws Exception {
	return cipher(Cipher.DECRYPT_MODE, in, password);
    }

    /**
     * Encrypt or decrypt a string using a password
     * 
     * @param mode
     *            Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param in
     *            the string to encrypt or Decrypt. if to decrypt: string must
     *            be Hex encoded
     * @param password
     *            the password to use
     * @return if Cipher.ENCRYPT_MODE: the encrypted string in hexadecimal if
     *         Cipher.DECRYPT_MODE: the decrypted string in clear readable
     *         format
     * 
     * @throws Exception
     */
    private String cipher(int mode, String in, char[] password)
	    throws Exception {
	if (mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE) {
	    throw new IllegalArgumentException(
		    "mode is not Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE!");
	}

	if (in == null) {
	    throw new IllegalArgumentException("in string can not be null!");
	}

	if (password == null) {
	    throw new IllegalArgumentException("password can not be null!");
	}

	PBEKeySpec pbeKeySpec;
	PBEParameterSpec pbeParamSpec;
	SecretKeyFactory keyFac;

	// Salt
	byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
		(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

	// Iteration count
	int count = 20;

	// Create PBE parameter set
	pbeParamSpec = new PBEParameterSpec(salt, count);

	pbeKeySpec = new PBEKeySpec(password);
	keyFac = SecretKeyFactory.getInstance("Blowfish");
	SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

	// Create PBE Cipher
	Cipher pbeCipher = Cipher.getInstance("Blowfish");

	// Initialize PBE Cipher with key and parameters
	pbeCipher.init(mode, pbeKey, pbeParamSpec);

	// Our cleartext
	byte[] inText = null;

	if (mode == Cipher.ENCRYPT_MODE) {
	    inText = in.getBytes();
	} else {
	    inText = CodecHex.decodeHex(in.toCharArray());
	}

	// Encrypt the cleartext
	byte[] ciphertext = pbeCipher.doFinal(inText);

	if (mode == Cipher.ENCRYPT_MODE) {
	    return new String(CodecHex.encodeHex(ciphertext));
	} else {
	    return new String(ciphertext);
	}
    }

    /**
     * Encrypt a file
     * 
     * @param fileIn
     *            the file to encrypt
     * @param fileOut
     *            the encrypted file after operation
     * @param password
     *            the password to use
     */
    public void encryptFile(File fileIn, File fileOut, char[] password)
	    throws Exception {
	cipher(Cipher.ENCRYPT_MODE, fileIn, fileOut, password);
    }

    /**
     * Decrypt a file
     * 
     * @param fileIn
     *            the file to decrypt
     * @param fileOut
     *            the decrypted file after operation
     * @param password
     *            the password to use
     */
    public void decryptFile(File fileIn, File fileOut, char[] password)
	    throws Exception {
	cipher(Cipher.DECRYPT_MODE, fileIn, fileOut, password);
    }

    /**
     * Encrypt or decrypt a file using a password
     * 
     * @param mode
     *            Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param fileIn
     *            the file to encrypt or Decrypt.
     * @param fileOut
     *            the resulting encrypted/decrypted file
     * @param password
     *            the password to use
     * 
     * @throws Exception
     */
    private void cipher(int mode, File fileIn, File fileOut, char[] password)
	    throws Exception {
	if (mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE) {
	    throw new IllegalArgumentException(
		    "mode is not Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE!");
	}

	if (fileIn == null) {
	    throw new IllegalArgumentException("in File can not be null!");
	}

	if (fileOut == null) {
	    throw new IllegalArgumentException("out File can not be null!");
	}

	if (password == null) {
	    throw new IllegalArgumentException("password can not be null!");
	}

	PBEKeySpec pbeKeySpec;
	PBEParameterSpec pbeParamSpec;
	SecretKeyFactory keyFac;

	// Salt
	byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
		(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

	// Iteration count
	int count = 1;

	// Create PBE parameter set
	pbeParamSpec = new PBEParameterSpec(salt, count);

	pbeKeySpec = new PBEKeySpec(password);
	keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

	// Create PBE Cipher
	Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

	// Initialize PBE Cipher with key and parameters
	pbeCipher.init(mode, pbeKey, pbeParamSpec);

	// InputStream in = null;
	// OutputStream out = null;

	try (InputStream in = new BufferedInputStream(
		new FileInputStream(fileIn));
		OutputStream out = new BufferedOutputStream(
			new FileOutputStream(fileOut));) {

	    byte[] input = new byte[2048 * 10];
	    int bytesRead;
	    while ((bytesRead = in.read(input)) != -1) {
		byte[] output = pbeCipher.update(input, 0, bytesRead);
		if (output != null)
		    out.write(output);
	    }

	    byte[] output = pbeCipher.doFinal();
	    if (output != null)
		out.write(output);

	    out.flush();
	} finally {
	    // IOUtils.closeQuietly(in);
	    // IOUtils.closeQuietly(out);
	}

    }

}
