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

package org.kawanfw.commons.client.http;

/**
 * See http://java-monitor.com/forum/showthread.php?t=4090
 */

import static java.lang.System.currentTimeMillis;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.util.logging.Logger.getLogger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.kawanfw.commons.util.DefaultParms;
import org.kawanfw.file.api.client.SessionParameters;

public class MultipartUtility {
    
    @SuppressWarnings("unused")
    private static final Logger log = getLogger(MultipartUtility.class
	    .getName());

    // Keep this! No System.getProperty("line.separator") that fails on Android
    private static final String CRLF = "\r\n"; 
    
    private static final String CHARSET = "UTF-8";

    private HttpURLConnection connection;

    private final OutputStream outputStream;
    private final Writer writer;
    private final String boundary;

    // for log formatting only
    @SuppressWarnings("unused")
    private final URL url;
    
    @SuppressWarnings("unused")
    private final long start;

    public MultipartUtility(final URL url, HttpURLConnection connection, SessionParameters sessionParameters) throws IOException {
	start = currentTimeMillis();
	
	if (url == null) {
	    throw new IllegalArgumentException("url is null!");
	}
	
	if (connection == null) {
	    throw new IllegalArgumentException("connection is null!");
	}
	
	this.url = url;
	this.connection = connection;
	
	boundary = "---------------------------" + currentTimeMillis();
	
	this.connection.setRequestProperty("Accept-Charset", CHARSET);
	this.connection.setRequestProperty("Content-Type",
		"multipart/form-data; boundary=" + boundary);
	
	int connectTimeout = 0;
	if (sessionParameters != null) {
	    connectTimeout = sessionParameters.getConnectTimeout();
	}
	
	TimeoutConnector timeoutConnector = new TimeoutConnector(connection,
		connectTimeout);
	outputStream = timeoutConnector.getOutputStream();
	    
	writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET));
    }

    public void addFormField(final String name, final String value) throws IOException {
	writer.append("--").append(boundary).append(CRLF)
		.append("Content-Disposition: form-data; name=\"").append(name)
		.append("\"").append(CRLF)
		.append("Content-Type: text/plain; charset=").append(CHARSET)
		.append(CRLF).append(CRLF).append(value).append(CRLF);
    }

    public void addFilePart(final String fieldName, final File uploadFile)
	    throws IOException {
	final String fileName = uploadFile.getName();
	writer.append("--").append(boundary).append(CRLF)
		.append("Content-Disposition: form-data; name=\"")
		.append(fieldName).append("\"; filename=\"").append(fileName)
		.append("\"").append(CRLF).append("Content-Type: ")
		.append(guessContentTypeFromName(fileName)).append(CRLF)
		.append("Content-Transfer-Encoding: binary").append(CRLF)
		.append(CRLF);

	writer.flush();
	//outputStream.flush();

	//InputStream inputStream = null;

	try (InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadFile));){
	    
	    int readBufferSize = DefaultParms.DEFAULT_READ_BUFFER_SIZE;	    
	    
	    final byte[] buffer = new byte[readBufferSize];
	    int bytesRead;
	    while ((bytesRead = inputStream.read(buffer)) != -1) {
		outputStream.write(buffer, 0, bytesRead);
	    }
	    //outputStream.flush();
	    
	    //writer.append(CRLF); // No! will fail by adding it to the uploaded file
	} finally {
	    //IOUtils.closeQuietly(inputStream);
	}

    }

    public void addHeaderField(String name, String value) throws IOException{
	writer.append(name).append(": ").append(value).append(CRLF);
    }

    public void finish() throws IOException {
	writer.append(CRLF).append("--").append(boundary).append("--")
		.append(CRLF);
	writer.close();

	/*
	final int status = connection.getResponseCode();
	if (status != HTTP_OK) {
	    throw new IOException(format("{0} failed with HTTP status: {1}",
		    url, status));
	}

	InputStream is = null;

	try {
	    is = connection.getInputStream();
	    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    final byte[] buffer = new byte[4096];
	    int bytesRead;
	    while ((bytesRead = is.read(buffer)) != -1) {
		bytes.write(buffer, 0, bytesRead);
	    }

//	    log.log(INFO,
//		    format("{0} took {4} ms", url,
//			    (currentTimeMillis() - start)));
	    return bytes.toByteArray();
	} finally {
	    connection.disconnect();
	}
	*/
	 
    }

    /**
     * Returns the current HttpUrlConnection in use.
     * @return the current HttpUrlConnection in use
     */
    public HttpURLConnection getConnection() {
	return connection;
    }
}
