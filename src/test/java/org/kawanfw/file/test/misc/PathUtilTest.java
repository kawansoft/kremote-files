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
package org.kawanfw.file.test.misc;

import org.junit.Assert;
import org.junit.Test;
import org.kawanfw.file.api.util.client.PathUtil;

public class PathUtilTest {

    @Test
    public void test() {
	Assert.assertEquals("PathUtil.rewriteToUnixSyntax(null)", null, PathUtil.rewriteToUnixSyntax(null));
	Assert.assertEquals("PathUtil.rewriteToUnixSyntax(\"c:\\\"));", "/", PathUtil.rewriteToUnixSyntax("c:\\"));
	Assert.assertEquals("PathUtil.rewriteToUnixSyntax(\"c:\\.kremote-server-root-file\"));", "/.kremote-server-root-file", PathUtil.rewriteToUnixSyntax("c:\\.kremote-server-root-file"));
	Assert.assertEquals("athUtil.rewriteToUnixSyntax(\"c:\\.kremote-server-root-file\\username\"));", "/.kremote-server-root-file/username", PathUtil.rewriteToUnixSyntax("c:\\.kremote-server-root-file\\username"));
	Assert.assertEquals("PathUtil.rewriteToUnixSyntax(\"c:\\.kremote-server-root-file\\username\\Tulips.jpg\"));", "/.kremote-server-root-file/username/Tulips.jpg", PathUtil.rewriteToUnixSyntax("c:\\.kremote-server-root-file\\username\\Tulips.jpg"));
	Assert.assertEquals("PathUtil.rewriteToUnixSyntax(\"username\\my_rép1\"));", "/username/my_rép1", PathUtil.rewriteToUnixSyntax("username\\my_rép1"));
	  
    }
    
    public static void main(String[] args) throws Exception {

	System.out.println(PathUtil.rewriteToUnixSyntax(null));
	System.out.println(PathUtil.rewriteToUnixSyntax("c:\\"));
	System.out.println(PathUtil.rewriteToUnixSyntax("c:\\.kremote-server-root-file"));	
	System.out.println(PathUtil.rewriteToUnixSyntax("c:\\.kremote-server-root-file\\username"));
	System.out.println(PathUtil.rewriteToUnixSyntax("c:\\.kremote-server-root-file\\username\\Tulips.jpg"));
	System.out.println(PathUtil.rewriteToUnixSyntax("username\\my_rép1"));
	
    }
    

}
