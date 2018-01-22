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
package org.kawanfw.file.api.client;

import java.io.InputStream;
import java.io.Serializable;

import org.kawanfw.commons.json.SessionParametersGson;
import org.kawanfw.commons.util.DefaultParms;

/**
 * 
 * Allows to define some parameters for the HTTP session.
 * <br><br>
 * Parameters are:
 * <br><br>
 * <ul>
 * <li>Timeout value, in milliseconds, to be used when opening a communications link with the remote server. Defaults to 0 (no timeout).</li>
 * <li>Read timeout, in milliseconds, that specifies the timeout when reading from remote Input stream. Defaults to 0 (no timeout).</li>
 * <li>Boolean to say if Clob upload/download using character stream or ASCII
 * stream must be HTML encoded. Defaults to <code>true</code>.</li>
 * <li>Boolean to say if HTTP content must be compressed. Defaults to <code>true</code>.</li>
 * <li>Download chunk length to be used by
 * {@link RemoteInputStream}. Defaults to 10Mb. 0
 * means files are not chunked.</li>
 * <li>Upload chunk length to be used by
 * {@link RemoteOutputStream} Defaults to 10Mb. 0 means
 * files are not chunked.</li>
 * </ul>
 * <p>
 * Use this class to change the default values of the HTTP session and
 * pass the created instance to <code>RemoteSession</code>.
 * <p>
 * For example, the following change the default connection timeout to 10
 * seconds and the default read timeout to 60 seconds: 
 * 
 * <blockquote><pre>
 * String url = "https://www.acme.org/ServerFileManager";
 * String username = "myUsername";
 * char [] password = {'m', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'}; 
 * &nbsp;        
 * SessionParameters sessionParameters = new SessionParameters();
 * &nbsp;
 * // Sets the timeout until a connection is established to 10 seconds
 * sessionParameters.setConnectTimeout(10);
 * &nbsp;
 * // Sets the read timeout to 60 seconds
 * sessionParameters.setReadTimeout(60);
 * &nbsp;        
 * // We will use no proxy
 * Proxy proxy = null;
 * PasswordAuthentication passwordAuthentication = null;
 * &nbsp;        
 * RemoteSession remoteSession 
 * = new RemoteSession(url, username, password, proxy, passwordAuthentication, sessionParameters);
 * &nbsp;
 * // Etc.
 * 
 *  </pre></blockquote>
 *   
 * @author Nicolas de Pomereu
 * @since 1.0
 */

/*
* <li>Boolean to say if client sides allows HTTPS call with all SSL
* Certificates, including "invalid" or self-signed Certificates. Defaults to
* <code>false</code>.</li>
*/

public class SessionParameters implements Serializable {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 8900046208199627283L;

    ///** The maximum size of a string read from input stream. Should be &le; 2Mb */
    //private int maxLengthForString = DefaultParms.DEFAULT_MAX_LENGTH_FOR_STRING;

    /**
     * Says if we want to html Encode the Clob when using chararacter or ASCII
     * stream Default is true
     */
    private boolean htmlEncodingOn = DefaultParms.DEFAULT_HTML_ENCODING_ON;

//    /**
//     * Says if we accept all SSL Certificates (example: self signed
//     * certificates)
//     */
//    private boolean acceptAllSslCertificates = DefaultParms.ACCEPT_ALL_SSL_CERTIFICATES;

    /**
     * The password to use to encrypt all request parameter names and values.
     * null means no encryption is done
     */
    @SuppressWarnings("unused")
    private char[] encryptionPassword = null;

    /**
     * The chunk length in bytes for {@link RemoteInputStream}.
     * Defaults to 10Mb.
     */
    private long downloadChunkLength = DefaultParms.DEFAULT_DOWNLOAD_CHUNK_LENGTH;

    /**
     * The chunk length in bytes for {@link RemoteOutputStream}.
     * Defaults to 10Mb.
     */
    private long uploadChunkLength = DefaultParms.DEFAULT_UPLOAD_CHUNK_LENGTH;

    /** the HttpUrlConnection timeout. Defaults to 0. */
    private int connectTimeout = 0;
    
    /** the HttpUrlConnection read timeout. Defaults to 0. */
    private int readTimeout = 0;
    
    /** Says if we send an "Accept-Encoding" "gzip" to server */
    private boolean compressionOn = DefaultParms.DEFAULT_COMPRESSION_ON;
    
    /**
     * Constructor.
     */
    public SessionParameters() {
	
    }

//    /**
//     * Returns the maximum authorized length in bytes for a string for upload or download
//     * (in order to avoid OutOfMemoryException on client and server side).
//     * 
//     * @return the maximum authorized length for a string for upload or download
//     */
//    public int getMaxLengthForString() {
//	return maxLengthForString;
//    }
//
//    /**
//     * Sets the maximum authorized length in bytes for a string for upload or download
//     * (in order to avoid OutOfMemoryException on client and server side).
//     * 
//     * @param maxLengthForString
//     *            the maximum authorized length for a string for upload or
//     *            download
//     */
//    public void setMaxLengthForString(int maxLengthForString) {
//	this.maxLengthForString = maxLengthForString;
//    }

//	FUTUR USAGE
//    /**
//     * Returns the encryption Password that encrypts http request parameters.
//     * 
//     * @return the encryption Password that encrypts http request parameters
//     */
//    public char[] getEncryptionPassword() {
//	return this.encryptionPassword;
//    }
//
//    /**
//     * Sets the encryption Password that encrypts http request parameters.
//     * 
//     * @param encryptionPassword
//     *            the encryption Password that encrypts http request parameters
//     */
//    public void setEncryptionPassword(char[] encryptionPassword) {
//	this.encryptionPassword = encryptionPassword;
//    }

    /**
     * Says if the upload/download of Clob using character stream or ASCII
     * stream is html encoded.
     * 
     * @return true if the upload/download of Clob is html encoded
     */
    public boolean isHtmlEncodingOn() {
	return this.htmlEncodingOn;
    }

    /**
     * Says if the upload/download of Clob using character stream or ASCII
     * stream must be html encoded.
     * 
     * @param htmlEncodeOn
     *            true to html encode the upload/download of Clob, else false
     */
    public void setHtmlEncodingOn(boolean htmlEncodeOn) {
	this.htmlEncodingOn = htmlEncodeOn;
    }

//    /**
//     * Says if client sides allows HTTPS call with all SSL Certificates,
//     * including "invalid" or self-signed Certificates.
//     * 
//     * @return true if client sides allows HTTPS call with all SSL Certificates
//     */
//    public boolean isAcceptAllSslCertificates() {
//	return acceptAllSslCertificates;
//    }
//
//    /**
//     * Sets if client sides must allow HTTPS call with all SSL Certificates,
//     * including "invalid" or self-signed Certificates.
//     * 
//     * @param acceptAllSslCertificates
//     *            true if we want client client sides to allow HTTPS call with
//     *            all SSL Certificates
//     */
//    public void setAcceptAllSslCertificates(boolean acceptAllSslCertificates) {
//	this.acceptAllSslCertificates = acceptAllSslCertificates;
//    }

    /**
     * Returns the chunk length in bytes used by
     * {@link RemoteOutputStream}. Defaults to 10Mb. 0
     * means files are not chunked.
     * 
     * @return the chunk length in bytes to be used for file upload
     */
    public long getUploadChunkLength() {
	return uploadChunkLength;
    }

    /**
     * Sets the chunk length in bytes to be used by
     * {@link RemoteOutputStream}. 0 means files are not
     * chunked.
     * 
     * @param chunkLength
     *            the chunk length in bytes to set for file upload
     */
    public void setUploadChunkLength(long chunkLength) {
	this.uploadChunkLength = chunkLength;
    }

    /**
     * Returns the chunk length in bytes used by
     * {@link RemoteInputStream}. Defaults to 10Mb. 0
     * means files are not chunked.
     * 
     * @return the chunk length to be used for file download
     */
    public long getDownloadChunkLength() {
	return downloadChunkLength;
    }

    /**
     * Sets the chunk length in bytes to be used by
     * {@link RemoteInputStream}. 0 means files are not
     * chunked.
     * 
     * @param chunkLength
     *            the chunk length in bytes to set for file download
     */
    public void setDownloadChunkLength(long chunkLength) {
	this.downloadChunkLength = chunkLength;
    }

    /**
     * Returns setting for connect timeout.
     * <p>
     * 0 return implies that the option is disabled
     * (i.e., timeout of infinity).
     *
     * @return an <code>int</code> that indicates the connect timeout
     *         value in milliseconds
     * @see #setConnectTimeout(int)
     * @since 1.0
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets a specified timeout value, in milliseconds, to be used when opening
     * a communications link to the remote server. If the timeout expires before
     * the connection can be established, a java.net.SocketTimeoutException is
     * raised. A timeout of zero is interpreted as an infinite timeout.
     * 
     * @param timeout
     *            an <code>int</code> that specifies the connect timeout value
     *            in milliseconds
     * @see #getConnectTimeout()
     * @since 1.0
     */
    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    
    /**
     * Returns setting for read timeout. 0 return implies that the
     * option is disabled (i.e., timeout of infinity).
     *
     * @return an <code>int</code> that indicates the read timeout
     *         value in milliseconds
     *
     * @see #setReadTimeout(int)
     * @see InputStream#read()
     * @since 1.0
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the read timeout to a specified timeout, in
     * milliseconds. A non-zero value specifies the timeout when
     * reading from Input stream when a connection is established to the remote server.
     * If the timeout expires before there is data available
     * for read, a java.net.SocketTimeoutException is raised. A
     * timeout of zero is interpreted as an infinite timeout.
     *
     * @param timeout an <code>int</code> that specifies the timeout
     * value to be used in milliseconds
     * @throws IllegalArgumentException if the timeout parameter is negative
     *
     * @see #getReadTimeout()
     * @see InputStream#read()
     * @since 1.0
     */
    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
    }

    /**
     * Says if HTTP content is compressed
     * @return {@code true} if compression is activated
     * @since 1.0
     */
    public boolean isCompressionOn() {
        return compressionOn;
    }

    /**
     * Says if http content is compressed
     * @param compressionOn {@code true} if compression is activated, else {@code false}
     * @since 1.0
     */
    public void setCompressionOn(boolean compressionOn) {
        this.compressionOn = compressionOn;
    }

    /**
     * Returns a JSon representation of the <code>SessionParameters</code>
     * instance. <br>
     * The JSon formated String can be used later to rebuild the instance from
     * the String.
     * 
     * @return a JSon representation of the <code>SessionParameters</code>
     *         instance
     */
    @Override
    public String toString() {
	return SessionParametersGson.toJson(this);
    }

}
