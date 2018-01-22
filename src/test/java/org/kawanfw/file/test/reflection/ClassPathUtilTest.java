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
package org.kawanfw.file.test.reflection;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

import org.junit.Test;

public class ClassPathUtilTest {

    @Test
    public void test() {
	fail("Not yet implemented");
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
//	File filterFile = new File("I:\\_dev_awake\\awake-file-3.0\\bin\\org\\kawanfw\\file\\test\\reflection\\ClassFileLocatorTest$TestFilenameFilter.class");
//	
//	ClassPathUtil.moveToFinalClasspath("username", filterFile, "org.kawanfw.file.test.reflection.ClassFileLocatorTest$TestFilenameFilter");
//	System.out.println("done!");
	
	//ClassPathUtil.displayClasspath();
	
	System.out.println();
	System.out.println(new Date());
	ClassLoader cl = ClassLoader.getSystemClassLoader();

	URL[] urls = ((URLClassLoader) cl).getURLs();

	for (URL url : urls) {
	    String fileStr = url.getFile().substring(1);
	    System.out.println(fileStr);
	    System.out.println(new File(fileStr).exists());
	}

    }
}
