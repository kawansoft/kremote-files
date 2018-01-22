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

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Classes that allow to extract parts from a HTTP Host in the format 
 *  "http://www.site.com:8080" 
 * <br>
 *  
 * @author Nicolas de Pomereu
 *
 */
public class HttpHostPartsExtractor {

    public static final int DEFAULT_PORT_HTTP = 80;
    public static final int DEFAULT_PORT_HTTPS = 443;    
    
    /** A HTTP Host in the format "http://www.site.com:8080" */
    private String httpHost = null;
    
    /**
     * Constructor
     * @param httpHost A HTTP Host in the format "http://www.site.com:8080" 
     */
    public HttpHostPartsExtractor(String httpHost) {
	
	if (httpHost == null) {
	    throw new IllegalArgumentException("httpHost can not be null!");
	}
	
	if (! httpHost.startsWith("http://") && ! httpHost.startsWith("https://")) {
	    throw new IllegalArgumentException("httpHost must start with \"http://\" or \"https://\". passed httpUrl is invalid: " + this.httpHost);	    
	}
	
	String hostname = StringUtils.substringAfter(httpHost, "://");
	
	if (hostname.contains("/")) {
	    throw new IllegalArgumentException("httpHost can not contain / separator");
	}
	
	if (hostname.contains(":")) {
	    String portStr= StringUtils.substringAfter(hostname, ":");
	    if (! StringUtils.isNumeric(portStr)) {
		throw new IllegalArgumentException("port is not numeric: " + httpHost);
	    }
	}	
	
	this.httpHost = httpHost;
		
    }

    /**
     * Returns the host name.
     *
     * @return the host name (IP or DNS name)
     */
    public String getHostName() {
	
	String hostname = StringUtils.substringAfter(httpHost, "://");
	
	if (hostname.contains(":")) {
	    hostname = StringUtils.substringBefore(hostname, ":");
	}
	
        return hostname;
    }

    /**
     * Returns the port.
     *
     * @return the host port, or <code>80</code> if not set for http scheme or 
     * or <code>443</code> if not set for https scheme. 
     */
    public int getPort() {
	
	String hostname = StringUtils.substringAfter(httpHost, "://");
	
	if (hostname.contains(":")) {
	    String portStr= StringUtils.substringAfter(hostname, ":");
	    int port = Integer.parseInt(portStr);
	    return port;
	}
	
	if (getSchemeName().equals("http")) {
	    return DEFAULT_PORT_HTTP;
	}
	else if (getSchemeName().equals("https")) {
	    return DEFAULT_PORT_HTTPS;
	}
	else {
	    throw new IllegalArgumentException("invalid scheme. Must be http or https. value is: " + getSchemeName());
	}
	
    }

    /**
     * Returns the scheme name ("http" or "https")
     *
     * @return "http" or "https"
     */
    public String getSchemeName() {
	String shemeName = StringUtils.substringBefore(httpHost, "://");
	return shemeName;
    }    
    

}
