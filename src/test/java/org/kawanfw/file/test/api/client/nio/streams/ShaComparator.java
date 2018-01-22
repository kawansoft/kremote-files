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
package org.kawanfw.file.test.api.client.nio.streams;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.kawanfw.commons.util.Sha1Util;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ShaComparator {

    protected ShaComparator() {
	
    }

    public static void compare(File file, File file_2) throws Exception {
    
        long begin = System.currentTimeMillis();
        MessageDisplayer.display("Comparing SHAs for " + file + " / " + file_2);
        Sha1Util sha1Util = new Sha1Util();
        String clientSha = sha1Util.getHexFileHash(file);
        String serverSha = sha1Util.getHexFileHash(file_2);
    
        Assert.assertEquals("Comparing SHA-1", clientSha, serverSha);
    
        long end = System.currentTimeMillis();
        MessageDisplayer.display(new Date() + " End Download");
        long elapsed = end - begin;
        System.out.println("Elapsed: " + (elapsed / 1000));
    }

    public static void compare(List<File> bigFiles, List<File> bigFiles_2)
            throws Exception {
    
        for (int i = 0; i < bigFiles.size(); i++) {
            compare(bigFiles.get(i), bigFiles_2.get(i));
        }
    }

}
