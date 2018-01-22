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
package org.kawanfw.file.test.run;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.kawanfw.file.test.api.client.nio.CallTestNio;
import org.kawanfw.file.test.api.client.nio.DownloadFilesNio;
import org.kawanfw.file.test.api.client.nio.MkdirsRemoteNio;
import org.kawanfw.file.test.api.client.nio.UploadFilesNio;
import org.kawanfw.file.test.util.MessageDisplayer;


public class TestRunnerJunit {

    public static void main(String[] args) {

	TestRunnerJunit testRunnerJunit = new TestRunnerJunit();
	testRunnerJunit.test();

    }

    /**
     * test
     */
    @Test
    public void test() {
	    testAll();
    }


    /**
     * TestReload all tests, in order.
     */
    private void testAll() {
	Result result = JUnitCore.runClasses(

	// Put here the junit tests
	CallTestNio.class, MkdirsRemoteNio.class, UploadFilesNio.class, DownloadFilesNio.class
	
	);

	MessageDisplayer.display("");
	MessageDisplayer.display("FAILURES:");

	for (Failure failure : result.getFailures()) {
	    System.err.println(failure.toString());
	}
    }
}
