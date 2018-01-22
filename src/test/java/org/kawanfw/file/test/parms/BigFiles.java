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
import java.io.FileFilter;
import java.util.Arrays;

import org.apache.commons.io.comparator.SizeFileComparator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class BigFiles {

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
        
    /**
     * Constuctior
     */
    protected BigFiles() {

    }
    
    /**
     * Get big files until 250 Mb
     * @return big files until 250 Mb
     */
    public static File [] getBig() {
	
	FileFilter fileFilter = new FileFilter() {
	    
	   // @Override
	    public boolean accept(File pathname) {
		if (pathname.toString().endsWith("Mb.txt")) {
		    return true;
		}
		return false;
	    }
	};
	
	File[] files = FileGenerator.getHomeDirectory().listFiles(fileFilter);
	Arrays.sort(files, SizeFileComparator.SIZE_COMPARATOR);
		
	return files;

    }
    
    /**
     * Get big files ending with _2 until 250 Mb
     * @return big files until 250 Mb
     */
    public static File [] getBig_2() {

	File [] files = getBig();
	
	File [] files_2 = new File[files.length];
		
	for (int i = 0; i < files.length; i++) {
	    
	    String name = files[i].getName();
	    name = name.replace("Mb.txt", "Mb_2.txt");
	    
	    files_2[i] = new File(TestParms.C_KREMOTE_SERVER_ROOT_FILE_USERNAME + File.separator + name);
	    
	}
	
	return files_2;
    }
    
    
    /**
     * Get big files with huge file 3Gb
     * @return bbig files with huge file 3Gb
     */
    public static File [] getBigWithHuge() {
	File [] bigFiles = getBig();
	
	File [] bigFilesWithHuge = new File[bigFiles.length + 1];

	for (int i = 0; i < bigFilesWithHuge.length; i++) {
	    
	    if (i < bigFilesWithHuge.length - 1) {
		bigFilesWithHuge[i] = bigFiles[i];
	    }
	    else {
		bigFilesWithHuge[i] = new File(FileGenerator.getHomeDirectory().toString() + File.separator + "photos-130711.zip");
	    }
	}
	
	return bigFilesWithHuge;

    }
    
    /**
     * Get big files with huge file 3Gb ending with _2
     * @return bbig files with huge file 3Gb
     */
    public static File [] getBigWithHuge_2() {
	File [] bigFiles_2 = getBig_2();
	
	File [] bigFilesWithHuge_2 = new File[bigFiles_2.length + 1];

	for (int i = 0; i < bigFilesWithHuge_2.length; i++) {
	    
	    if (i < bigFilesWithHuge_2.length - 1) {
		bigFilesWithHuge_2[i] = bigFiles_2[i];
	    }
	    else {
		bigFilesWithHuge_2[i] = new File(TestParms.C_KREMOTE_SERVER_ROOT_FILE_USERNAME + File.separator + "photos-130711.zip");
	    }
	}
	
	return bigFilesWithHuge_2;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	File [] files = getBig();
	
	for (int i = 0; i < files.length; i++) {
	    System.out.println(files[i]);
	}
	
	File [] filesWithHuge = getBigWithHuge();
	for (int i = 0; i < filesWithHuge.length; i++) {
	    System.out.println(filesWithHuge[i]);
	}
	
	File [] files_2 = getBig_2();
	
	for (int i = 0; i < files_2.length; i++) {
	    System.out.println(files_2[i]);
	}
	
	File [] filesWithHuge_2 = getBigWithHuge_2();
	for (int i = 0; i < filesWithHuge_2.length; i++) {
	    System.out.println(filesWithHuge_2[i]);
	}
	
    }
    
    

}
