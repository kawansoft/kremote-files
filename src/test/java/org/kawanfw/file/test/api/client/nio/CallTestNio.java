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

import org.junit.Assert;
import org.junit.Test;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * 
 * TestReload that a re mote method is callable.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class CallTestNio {

    public static void main(String[] args) throws Exception {
	new CallTestNio().test();
    }

    @Test
    public void test() throws Exception {

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());

	test(remoteSession);

    }

    /**
     * @param remoteSession
     *            the KRemote Files Session
     * @throws Exception
     */
    public void test(RemoteSession remoteSession) throws Exception {

	int a = 33;
	int b = 44;

	String resultStr = null;
	int result = -1;

	MessageDisplayer.display("");
	MessageDisplayer.display("Testing call()...");
	resultStr = remoteSession
		.call("org.kawanfw.file.test.api.server.CalculatorNotAuthenticated.add",
			a, b);
	result = Integer.parseInt(resultStr);

	MessageDisplayer
		.display("CalculatorNotAuthenticated Result: " + result);

	Assert.assertEquals("a + b = 77", a + b, result);

	resultStr = remoteSession.call(
		"org.kawanfw.file.test.api.server.Calculator.add", a, b);
	result = Integer.parseInt(resultStr);

	Assert.assertEquals("a + b must be result", a + b, result);
	MessageDisplayer.display("Calculator Result: " + result);

	// Testing a method not allowed that will be refused by our
	// TestFileConfigurator.allowCallAfterAnalysis method:

	String exceptionMessage = "";

	try {
	    resultStr = remoteSession
		    .call("org.kawanfw.file.test.api.server.CalculatorNotAllowed.add",
			    a, b);

	    // This line must not be reached!
	    Assert.assertEquals("line not to be reached.", true, false);

	} catch (Exception e) {
	    exceptionMessage = e.getMessage();
	    System.out
		    .println("org.kawanfw.file.test.api.server.CalculatorNotAllowed.add exception: "
			    + e.getMessage());
	}

	Assert.assertEquals(
		"exceptionMessage must contains org.kawanfw.file.test.api.server.CalculatorNotAllowed",
		true,
		exceptionMessage
			.contains("org.kawanfw.file.test.api.server.CalculatorNotAllowed"));

	try {
	    resultStr = remoteSession.call(
		    "org.kawanfw.file.test.api.server.Calculator2.add", a, b);
	    result = Integer.parseInt(resultStr);
	} catch (Exception e) {
	    exceptionMessage = e.getMessage();
	    //e.printStackTrace();
	    System.out
		    .println("org.kawanfw.file.test.api.server.Calculator2.add exception: "
			    + e.getCause());
	}
	
	try {
	    resultStr = remoteSession.call(
		    "org.kawanfw.file.test.api.server.Calculator.notExists", a,
		    b);
	    result = Integer.parseInt(resultStr);
	} catch (Exception e) {
	    exceptionMessage = e.getMessage();
	    //e.printStackTrace();
	    System.out
		    .println("org.kawanfw.file.test.api.server.notExists.add exception: "
			    + e.getCause());
	}
	
	new CallTestServletRequest().test();

    }

    @Test(expected = RemoteException.class)
    public void test2() throws Exception {

	int a = 33;
	int b = 44;

	RemoteSession remoteSession = new RemoteSession(TestParms.KREMOTE_FILES,
		TestParms.REMOTE_USER, TestParms.REMOTE_PASSWORD.toCharArray());

	String resultStr = remoteSession.call(
		"org.kawanfw.test.api.server.CalculatorNotCallable.add", a, b);
	int result = Integer.parseInt(resultStr);

	MessageDisplayer.display("Calculator Result: " + result);

	Assert.assertEquals(a + b, result);
    }

}
