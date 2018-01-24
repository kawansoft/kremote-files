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
package org.kawanfw.file.test.api.client.nio;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.test.parms.TestParms;

/**
 * 
 * TestReload that a re mote method is callable.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class CallTestServletRequest {

    public static void main(String[] args) throws Exception {
	new CallTestServletRequest().test();
    }

    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES_URL_LOCAL,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());

	test(remoteSession);

    }

    /**
     * @param remoteSession
     *            the KRemote Files Session
     * @throws Exception
     */
    public void test(RemoteSession remoteSession) throws Exception {

	String resultStr = null;
	
	try {
	    resultStr = remoteSession.call(
		    "org.kawanfw.file.test.api.server.ServletRequestQuery.getIPAddr", "message-" + new Date(), HttpServletRequest.class);
	    System.out.println("getIPAddr: " + resultStr);
	} catch (Exception e) {
	    //e.printStackTrace();
	    System.out
		    .println("org.kawanfw.file.test.api.server.ServletRequestQuery.getIPAddr exception: "
			    + e.getCause());
	}

    }

}
