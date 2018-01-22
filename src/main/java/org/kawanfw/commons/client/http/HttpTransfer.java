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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.kawanfw.file.api.client.exception.RemoteException;

/**
 * HttpTransfer - Simple send() and recv() commands on PC side. <br>
 * Purpose of this interface is to wrap the underlying HTTP tool in use in
 * implementation. Default implementation will use HttpClient
 * (http://jakarta.apache.org/commons/httpclient) <br>
 * Note: <br>
 * - This interface is not *usable* on HTTP Server side <br>
 * - Design of server will use a Server Program. The Server Program will
 * retrieve string with GET. <br>
 * - The Server Manager will send back string using a Serlvet that writes an
 * output on output stream. <br>
 * - The Server Program may be written in any language : Java/Servlet, PHP5. <br>
 * <br>
 * Usage: <br>
 * - Eeach send() must be followed by: <br>
 * -- A isSendOk() to test is all operations are Ok. <br>
 * -- A recv() to get the result as a String() <br>
 */
public interface HttpTransfer {
    
    public static final int MAXIMUM_PROGRESS_100 = 100;

    /**
     * @return the http status code
     */
    public int getHttpStatusCode();

    /**
     * Send a String to the HTTP server.
     * 
     * @param requestParams
     *            the request parameters list with (parameter, value)
     * 
     * @throws UnknownHostException
     *             Host url (http://www.acme.org) does not exists or no Internet
     *             Connection.
     * @throws ConnectException
     *             The Host is correct but the Servlet
     *             (http://www.acme.org/Servlet) failed with a status <> OK
     *             (200). (if the host is incorrect, or is impossible to connect
     *             to- Tomcat down - will throw a sub exception
     *             HttpHostConnectException)
     * @throws IOException
     *             For all other IO / Network / System Error
     */
    public void send(List<SimpleNameValuePair> requestParams)
	    throws UnknownHostException, ConnectException,
	    RemoteException, IOException;

    
    /**
     * Send a String to the HTTP server and upload a file
     * 
     * @param requestParams
     *            the request parameters list with (parameter, value)
     * @param file
     *            the file to upload
     * 
     * @throws UnknownHostException
     *             Host url (http://www.acme.org) does not exists or no Internet
     *             Connection.
     * @throws ConnectException
     *             The Host is correct but the Servlet
     *             (http://www.acme.org/Servlet) failed with a status <> OK
     *             (200). (if the host is incorrect, or is impossible to connect
     *             to- Tomcat down - will throw a sub exception
     *             HttpHostConnectException)
     * @throws IOException
     *             For all other IO / Network / System Error
     * @throws SecurityException
     *             if a security exception is raised
     */    
    public void send(List<SimpleNameValuePair> requestParams, File file)
	    throws UnknownHostException, ConnectException,
	    RemoteException, IOException;    
        
        
    /**
     * Send a String to the HTTP server using servlet defined by url and return
     * the corresponding input stream
     * 
     * 
     * @param requestParams
     *            the request parameters list with (parameter, value)
     * @param fileLength
     *            the file length (for the progress indicator). If 0, will not
     *            be used
     * @param file
     *            the file to create on the client side (PC)
     * 
     * @throws IllegalArgumentException
     *             if the file to download is null
     * @throws UnknownHostException
     *             Host url (http://www.acme.org) does not exists or no Internet
     *             Connection.
     * @throws ConnectException
     *             The Host is correct but the Servlet
     *             (http://www.acme.org/Servlet) failed with a status <> OK
     *             (200).
     * @throws IOException
     *             For all other IO / Network / System Error
     */
    public InputStream getInputStream(List<SimpleNameValuePair> requestParams)
	    throws IllegalArgumentException, UnknownHostException,
	    ConnectException, RemoteException, IOException;
	

    /**
     * Create a File from an URL.
     * 
     * @param url
     *            the url of the site. Example http://www.yahoo.com
     * @param file
     *            the file to create from the download.
     * 
     * @throws IllegalArgumentException
     *             if the url or the file is null
     * @throws UnknownHostException
     *             Host url (http://www.acme.org) does not exists or no Internet
     *             Connection.
     * @throws FileNotFoundException
     *             Impossible to connect to the Host. May appear if, for
     *             example, the Web server is down. (Tomcat down ,etc.)
     * @throws SecurityException
     *             if a security exception is raised
     * @throws IOException
     *             For all other IO / Network / System Error
     */
    public void downloadUrl(URL url, File file)
	    throws IllegalArgumentException, UnknownHostException,
	    FileNotFoundException, IOException;

//    /**
//     * Creates a String from a remote URL.
//     * 
//     * @param url
//     *            the url of the site. Example http://www.yahoo.com
//     * 
//     * @return the content of the url
//     * 
//     * @throws IllegalArgumentException
//     *             if url or file is null
//     * @throws UnknownHostException
//     *             if host URL (http://www.acme.org) does not exists or no
//     *             Internet Connection.
//     * @throws IOException
//     *             For all other IO / Network / System Error
//     * 
//     */
//    public String getUrlContent(URL url) throws IllegalArgumentException,
//	    UnknownHostException, IOException;

    /**
     * Closes the http client used for getInputStream
     */
    

    public void close(); 

    
    
    // /**
    // * @return true is the last send() command successfully executed
    // */
    // public boolean isSendOk();

    /**
     * Receive a String from the HTTP Server. String is never null. If empty ==>
     * ""
     * 
     * @return the received string from the HTTP Server
     */

    public String recv();

    /**
     * Defines if the result is to be received into a text file <br>
     * Call getReceiveFile() to get the file name <br>
     * Defaults to false.
     * 
     * @param receiveInFile
     *            if true, the result will be defined in a file
     */
    public void setReceiveInFile(boolean receiveInFile);

    /**
     * @return the receiveFile
     */
    public File getReceiveFile();

   

    // /**
    // * return true if Yahoo is Reachable ==> connected to the Internet.
    // * @param httpAddress the Http Address to test. Ex: http://www.google.com
    // */
    // public boolean isHttpReachable(String httpAddress);

    // /**
    // * SqlExecutorTestLocal if there is a System Proxy defined in Windows
    // (Internet Explorer)
    // * @param httpAddress the Http Address to test. Ex: http://www.google.com
    // * @return 0: if there is no System Proxy in use
    // * 1: there is a System Proxy in use
    // * -1: It's impossible to know because there is no Internet Connection!
    // */

    // public int diagnoseIEProxySetting(String httpAddress);

}
