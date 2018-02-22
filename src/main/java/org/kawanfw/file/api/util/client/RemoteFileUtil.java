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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.client.http.HttpTransferUtil;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.client.RemoteOutputStream;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.reflection.ClassSerializer;
import org.kawanfw.file.util.parms.Settings;

/**
 * Utility methods called by RemoteFile list()/listFiles() with filter parameter
 * 
 * @author Nicolas de Pomereu
 *
 */
public class RemoteFileUtil {

    /** For debug info */
    private static boolean DEBUG = FrameworkDebug.isSet(RemoteFileUtil.class);

    private static final String UNSUPPORTED_MAJOR_MINOR_VERSION = "Unsupported major.minor version";

    /**
     * Protected constructor
     */
    protected RemoteFileUtil() {

    }

    /**
     * Decode the Throwable and rethrow a new RuntimeException or
     * IllegalArgumentException
     * 
     * @param throwable
     *            the throwable to analyse
     * @throws IllegalArgumentException
     *             if the filter java version is > host java version
     * @throws RuntimeException
     *             the RuntimeException to rethrow
     */
    public static void decodeTrowableForFilterUsage(Throwable throwable)
	    throws IllegalArgumentException, RuntimeException {
	if (throwable instanceof RemoteException) {
	    Throwable cause = throwable.getCause();

	    // Say what verson is supported if UnsupportedClassVersionError
	    if (cause != null
		    && cause instanceof UnsupportedClassVersionError) {
		String message = cause.getMessage();
		if (message != null
			&& message.contains(UNSUPPORTED_MAJOR_MINOR_VERSION)) {
		    String classFileVersionNumber = StringUtils.substringAfter(
			    message, UNSUPPORTED_MAJOR_MINOR_VERSION);
		    classFileVersionNumber = classFileVersionNumber.trim();
		    String classFileVersion = RemoteFileUtil
			    .decodeJavaVersionFromMajorMinor(
				    classFileVersionNumber);
		    String finalMessage = "The filter .class file java version ("
			    + classFileVersion
			    + ") is unsupported on remote host. " + "("
			    + message + ")";
		    throw new IllegalArgumentException(finalMessage);
		}
	    }

	    throw new RuntimeException(cause);
	}
	throw new RuntimeException(throwable);
    }

    /**
     * Decode the Java version from the major.minor in the .class file
     * 
     * @param classFileVersionNumber
     * @return
     */
    public static String decodeJavaVersionFromMajorMinor(
	    String classFileVersionNumber) {
	// 6 (1.6) 50.0
	// 7 (1.7) 51.0
	// 8 (1.8) 52.0

	if (classFileVersionNumber.contains("50")) {
	    return "1.6";
	} else if (classFileVersionNumber.contains("51")) {
	    return "1.7";
	} else if (classFileVersionNumber.contains("52")) {
	    return "1.8";
	} else if (classFileVersionNumber.contains("52")) {
	    return "1.9";
	} else {
	    return "1.9+";
	}

    }

    /**
     * Uploads the serialized base64 String to host if size less than
     * Settings.MAX_STRING_SIZE_FOR_HTTP_REQUEST
     * 
     * @param base64SerialFilter
     *            the serialized filter
     * @param remoteSession
     *            the remote session to use for upload
     * @return the name of the file that contains the filter on host
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InvalidLoginException
     * @throws FileNotFoundException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws SocketException
     * @throws RemoteException
     */
    public static String uploadFilterIfShortSize(String base64SerialFilter,
	    RemoteSession remoteSession)
	    throws IOException, IllegalArgumentException, InvalidLoginException,
	    FileNotFoundException, UnknownHostException, ConnectException,
	    SocketException, RemoteException {

	debug("base64SerialFilter.length(): " + base64SerialFilter.length());

	String filterFilename = null;
	if (!RemoteFileUtil.isFilterShortSize(base64SerialFilter)) {
	    File file = HttpTransferUtil.createKawansoftTempFile();

	    try {
		FileUtils.writeStringToFile(file, base64SerialFilter,
			Charset.defaultCharset());

		filterFilename = file.getName();
		filterFilename = "/.classes/" + filterFilename;

		// remoteSession.upload(file, filterFilename);

		// InputStream in = null;
		// OutputStream out = null;

		// (IOUtils is a general IO stream manipulation utilities
		// provided by Apache Commons IO)

		try (InputStream in = new BufferedInputStream(
			new FileInputStream(file));
			OutputStream out = new RemoteOutputStream(remoteSession,
				filterFilename, file.length());) {

		    IOUtils.copy(in, out);
		    // Cleaner to close out here so that no Exception is thrown
		    // in
		    // finally clause
		    out.close();
		} finally {
		    // IOUtils.closeQuietly(in);
		    // IOUtils.closeQuietly(out);
		}

	    } finally {
		FileUtils.deleteQuietly(file);
	    }

	    debug("Filter uploaded with remoteSession.upload(file, filterFilename)");
	}
	return filterFilename;
    }

    /**
     * Serialize the passed FilenameFilter to base64 string
     * 
     * @param filenameFilter
     *            the FilenameFilter to serialize
     * @param filenameFilterClassname
     *            the FilenameFilter class name (for Exception)
     * @return the serialized FilenameFilter in base64 String
     * 
     * @throws IOException
     */
    public static String SerializeBase64FilenameFilter(
	    FilenameFilter filenameFilter, String filenameFilterClassname)
	    throws IOException {

	if (!(filenameFilter instanceof Serializable)) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT + " the FilenameFilter is not serializable: "
			    + filenameFilterClassname);
	}

	ClassSerializer<FilenameFilter> classSerializer = new ClassSerializer<FilenameFilter>();
	String base64SerialFilenameFilter = classSerializer
		.toBase64(filenameFilter);

	return base64SerialFilenameFilter;
    }

    /**
     * Serialize the passed FileFilter to base64 string
     * 
     * @param filenameFilter
     *            the FileFilter to serialize
     * @param filenameFilterClassname
     *            the FileFilter class name (for Exception)
     * @return the serialized FileFilter in base64 String
     * 
     * @throws IOException
     */
    public static String SerializeBase64FileFilter(FileFilter fileFilter,
	    String fileFilterClassname) throws IOException {

	if (!(fileFilter instanceof Serializable)) {
	    throw new IllegalArgumentException(
		    Tag.PRODUCT + " the FileFilter is not serializable: "
			    + fileFilterClassname);
	}

	ClassSerializer<FileFilter> classSerializer = new ClassSerializer<FileFilter>();
	String base64SerialFileFilter = classSerializer.toBase64(fileFilter);

	return base64SerialFileFilter;
    }

    /**
     * Test is filter is short size, in ordr to decide to send it in request or
     * per file upload
     * 
     * @param base64SerialFilter
     *            the serialized base64 filter
     * @return true if filter si short size
     */
    public static boolean isFilterShortSize(String base64SerialFilter) {
	if (base64SerialFilter == null) {
	    return true;
	}

	if (base64SerialFilter
		.length() <= Settings.MAX_STRING_SIZE_FOR_HTTP_REQUEST) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * debug tool
     */
    public static void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
