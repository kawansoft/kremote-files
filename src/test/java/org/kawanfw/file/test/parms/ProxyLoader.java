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
package org.kawanfw.file.test.parms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.FrameworkSystemUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ProxyLoader {

    private static final String NEOTUNNEL_TXT = "i:\\neotunnel.txt";

    /** Proxy to use with HttpUrlConnection */
    private Proxy proxy = null;

    /** For authenticated proxy */
    private PasswordAuthentication passwordAuthentication = null;

    /**
     * 
     */
    public ProxyLoader() {

    }

    public Proxy getProxy() throws IOException, URISyntaxException {
	if (FrameworkSystemUtil.isAndroid()) {
	    return null;
	}

	System.setProperty("java.net.useSystemProxies", "true");
	List<Proxy> proxies = ProxySelector.getDefault().select(
		new URI("http://www.google.com/"));
	       
	if (proxies != null && proxies.size() >= 1) {
	    
	    System.out.println("Proxy in use: " + proxies.get(0));
	    
	    if (proxies.get(0).type().equals(Proxy.Type.DIRECT)) {
		return null;
	    }
	    
	    System.out.println("Loading proxy file info...");
	    // System.setProperty("java.net.useSystemProxies", "false");
	    File file = new File(NEOTUNNEL_TXT);
	    if (file.exists()) {
		String proxyValues = FileUtils.readFileToString(file, Charset.defaultCharset());
		String username = StringUtils.substringBefore(proxyValues, " ");
		String password = StringUtils.substringAfter(proxyValues, " ");

//		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
//			"127.0.0.1", 8080));
		proxy = proxies.get(0);

		passwordAuthentication = new PasswordAuthentication(username,
			password.toCharArray());

		System.out.println("USING PROXY WITH AUTHENTICATION: " + proxy
			+ " / " + username + "-" + password);
	    } else {
		throw new FileNotFoundException(
			"proxy values not found. No file " + file);
	    }
	}

	return proxy;
    }

    /**
     * @return the passwordAuthentication
     */
    public PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication;
    }
    
    

}
