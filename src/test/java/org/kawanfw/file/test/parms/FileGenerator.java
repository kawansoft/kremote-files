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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.kawanfw.file.api.client.RemoteSession;

/**
 * @author Nicolas de Pomereu
 *
 */
public class FileGenerator {

    
    
    /**
     * Contructor
     */
    protected FileGenerator() {
	super();
	
    }

    /**
     * Return the home directory to use
     * @return the home directory to use
     */
    public static File getHomeDirectory() {
	File anyFile = TestParms.getFileFromUserHome("anyfile.txt");
	File HomeDirectory = anyFile.getParentFile();
	return HomeDirectory;
    }
    
    /**
     * Generates a file of with length long
     * @param file	the file to generate
     * @param length	the length of file to generate
     */
    private static void generate(File file, long length) throws Exception {


	try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file));){
	    
	    long cpt = 0;
	    while (true) {
		
		if (length == 0) {
		    break;
		}
		
		out.write("a".getBytes());
		cpt++;
		if (cpt == length) {
		    break;
		}
	    }
	} finally {
	    //IOUtils.closeQuietly(out);
	}
    }
    
    /**
     * Generates a file of with length long with the name length in Mb + "Mb.txt"
     * @param length	the length of file to generate
     */
    public static void generate(long length) throws Exception {
	
	String lengthMb = (length / RemoteSession.MB) + "";	
	String fileName = getHomeDirectory() +  File.separator +  lengthMb + "Mb.txt";
	System.out.println(new Date() + " Generating " + fileName + "...");
	generate(new File(fileName), length);
    }

    /**
     * Generate files with the length in Mb
     * @param lengthsthe length in Mb
     * @throws Exception
     */
    public static void generate(long [] lengths) throws Exception {
	
	for (int i = 0; i < lengths.length; i++) {
	    generate(lengths[i] * RemoteSession.MB);
	}
    }
    
    
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	
	System.out.println("Delete directories...");
	initDeleteLocalDirectories();
	
	System.out.println("HomeDirectory: " + getHomeDirectory());
	
	// 0
	// 1 Mb
	// 2 Mb
	// 2,5 Mb
	// 3Mb
	// 9 Mb
	// 10Mb
	// 11 Mb
	// 33Mb
	// 1OO Mb
	// 150 Mb
	// 333Mb
	
	long [] lengths = {0, 1, 2, 2,5, 3, 9, 10, 11, 33, 100, 150, 333};
	generate(lengths);
	System.out.println("new Date() + Done!");

    }

    /**
     * @throws IOException
     */
    public static void initDeleteLocalDirectories()
            throws IOException {
        
        File downloadDir = new File(getHomeDirectory().toString() + File.separator + "download");
        System.out.println("Deleting Directory " + downloadDir);
    
        if (downloadDir.exists())
        {
            FileUtils.forceDelete(downloadDir);
        }
    }

}
