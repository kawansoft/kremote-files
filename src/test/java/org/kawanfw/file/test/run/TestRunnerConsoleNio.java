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

import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.SystemUtils;
import org.kawanfw.commons.util.FrameworkSystemUtil;
import org.kawanfw.file.api.client.RemoteSession;
import org.kawanfw.file.api.client.SessionParameters;
import org.kawanfw.file.test.api.client.nio.CallTestNio;
import org.kawanfw.file.test.api.client.nio.DeleteAllNio;
import org.kawanfw.file.test.api.client.nio.DownloadFilesNio;
import org.kawanfw.file.test.api.client.nio.MkdirsRemoteNio;
import org.kawanfw.file.test.api.client.nio.RenameFilesNio;
import org.kawanfw.file.test.api.client.nio.UploadFilesNio;
import org.kawanfw.file.test.api.client.nio.engines.EngineDownloadBigFilesNew;
import org.kawanfw.file.test.api.client.nio.engines.EngineUploadBigFilesNew;
import org.kawanfw.file.test.parms.FileGenerator;
import org.kawanfw.file.test.parms.ProxyLoader;
import org.kawanfw.file.test.parms.TestParms;
import org.kawanfw.file.test.run.filter.BuiltInFilterTest;
import org.kawanfw.file.test.run.remotefiles.RemoteFileTest;
import org.kawanfw.file.test.util.MessageDisplayer;

public class TestRunnerConsoleNio {

    /**
     * @param remoteSession
     *            the KRemote Files Session
     * @throws Exception
     * @throws SQLException
     */
    public static void testAll(RemoteSession remoteSession)
	    throws Exception, SQLException {
	
	//TestRunnerConsole.startIt();
	
	MessageDisplayer.display("");
	MessageDisplayer.display("Local Java Version : "
		+ System.getProperty("java.version"));
	MessageDisplayer.display("Remote Java Version: "
		+ remoteSession.getRemoteJavaVersion());
	MessageDisplayer.display("version: " + remoteSession.getVersion());
	MessageDisplayer.display("url    : " + remoteSession.getUrl());
	MessageDisplayer.display("");

	FileGenerator.initDeleteLocalDirectories();
	
	new DeleteAllNio().test(remoteSession);
	new CallTestNio().test(remoteSession);
	
	new MkdirsRemoteNio().test(remoteSession);
		
	new UploadFilesNio().test(remoteSession);
	new DownloadFilesNio().test(remoteSession);
	new RenameFilesNio().test(remoteSession);

	// Filters
	new BuiltInFilterTest().test(remoteSession);
	
	// Remote Files
	new RemoteFileTest().test(remoteSession);
	
	if (TestParms.TEST_BIG_FILES && ! FrameworkSystemUtil.isAndroid()) {
	    new EngineUploadBigFilesNew().test(remoteSession);
	    new EngineDownloadBigFilesNew().test(remoteSession);
	}
    }

    public static void startIt() throws Exception {
	if (SystemUtils.IS_JAVA_1_7) {
	    System.setProperty("java.net.preferIPv4Stack", "true");
	}

	ProxyLoader proxyLoader = new ProxyLoader();
	Proxy proxy = proxyLoader.getProxy();
	PasswordAuthentication passwordAuthentication = proxyLoader.getPasswordAuthentication();
	SessionParameters sessionParameters = new SessionParameters();
	
	/*
	if (TestParms.KREMOTE_FILES.startsWith("https:") && TestParms.KREMOTE_FILES.contains("localhost"))
	{
	    sessionParameters.setAcceptAllSslCertificates(true);
	}
	*/
	
	sessionParameters.setCompressionOn(TestParms.COMPRESSION_ON);

	while (true) {

	    MessageDisplayer.display("new RemoteSession()...");
	    RemoteSession remoteSession = null;
	    
	    remoteSession = new RemoteSession(TestParms.KREMOTE_FILES_URL_REMOTE,
		    TestParms.REMOTE_USER,
		    TestParms.REMOTE_PASSWORD.toCharArray(), proxy, passwordAuthentication,
		    sessionParameters);
	   	    
	    MessageDisplayer.display(new Date());
	   
	    Date begin = new Date(); 
	    long longBegin = System.currentTimeMillis();
	    testAll(remoteSession);
	    Date end = new Date();
	    long longEnd = System.currentTimeMillis();
	    
	    MessageDisplayer.display("");
	    MessageDisplayer.display("Begin: " + begin);
	    MessageDisplayer.display("End  : " + end);
	    MessageDisplayer.display("Elapsed  : " + (longEnd - longBegin));
	    remoteSession.logoff();

	    if (!TestParms.LOOP_MODE) {
		return;
	    }

	}
    }

    public static void main(String[] args) throws Exception {

	startIt();
    }
}
