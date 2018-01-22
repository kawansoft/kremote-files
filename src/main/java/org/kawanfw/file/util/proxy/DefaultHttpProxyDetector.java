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
package org.kawanfw.file.util.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * Proxy Detector. Used mainly for automated tests.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class DefaultHttpProxyDetector implements HttpProxyDetector {

    private String type = null;
    private String hostname = null;
    private int port = -1;

    /**
     * Constructor
     * 
     * @throws Exception
     */
    public DefaultHttpProxyDetector() {

	System.setProperty("java.net.useSystemProxies", "true");

	URI uri = null;

	try {
	    uri = new URI("http://www.google.com/");
	} catch (URISyntaxException e) {
	    throw new IllegalArgumentException(e);
	}

	List<Proxy> listProxy = ProxySelector.getDefault().select(uri);

	for (Iterator<Proxy> iter = listProxy.iterator(); iter.hasNext();) {

	    Proxy proxy = (Proxy) iter.next();
	    type = proxy.type().toString();

	    InetSocketAddress addr = (InetSocketAddress) proxy.address();

	    if (addr == null) {
		hostname = null;
	    } else {

		hostname = addr.getHostName();
		port = addr.getPort();

	    }
	}
    }

    /**
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the port
     */
    public int getPort() {
	return port;
    }
    

}
