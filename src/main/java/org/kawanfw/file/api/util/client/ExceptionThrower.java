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
package org.kawanfw.file.api.util.client;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.kawanfw.commons.util.FrameworkFileUtil;

/**
 * 
 * Allows to throw an Exceptio  if a c:\\temp\\flag.txt fie exists
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ExceptionThrower {

    /**
     * 
     */
    protected ExceptionThrower() {
    }

    /**
     * Allows to simulation an Exception on chunk uplaod in order to test the
     * recovery mechanism
     * 
     * @throws IOException
     */
    public static void throwSocketExceptionIfFlagFileExists()
	    throws IOException {
	{
	    
	    File flagFile = new File(FrameworkFileUtil.getUserHomeDotKawansoftDir() + File.separator + "throwSocketExceptionFlag.txt");
	 
	    if (flagFile.exists()) {
		
		// Slow down all for our tests
		try {
		    Thread.sleep(10);
		} catch (InterruptedException e) {
		    
		    e.printStackTrace();
		}
		
		String content = FileUtils.readFileToString(flagFile, Charset.defaultCharset());

		if (content.contains("1")) {
		    FileUtils.write(flagFile, "2", Charset.defaultCharset());
		    throw new SocketException(
			    "Exception simulation on upload(): " + flagFile
				    + " exists and contains 1");
		}
	    }
	}
    }

}
