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
package org.kawanfw.commons.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Aas it says, class to split in chunks names the filename.
 * 
 * @author Nicolas de Pomereu
 * 
 */
public class FilenameSplitter {
        
    private List<String> fileParts = new ArrayList<String>();

    
    /**
     * Constructor
     */
    public FilenameSplitter() {
	
    }

    /**
     * Splits the designated file name into chunk file names
     * 
     * @param file
     *            the file to split
     * @param splitlen
     *            the length in bytes of each split
     * @throws IOException
     */
    public void split(File file, String remoteFile, long splitlen) throws IOException {
	
	long total = 0;
	int countFile = 1;
	    
	while(true) {
	    String remoteFilePart = getFilePart(remoteFile, countFile);

	    //debug("remoteFilePart: " + remoteFilePart);
	    
	    countFile++;
	    
	    fileParts.add(remoteFilePart);
	    
	    total+=splitlen;
	    
	    if (total> file.length()) {
		break;
	    }
	}	
    }

        
    /**
     * @return the fileParts
     */
    public List<String> getFileParts() {
        return fileParts;
    }

    /**
     * Return the file part name from the file
     * 
     * @param filePath
     *            the original file path
     * @param countFile
     *            the number of the file
     * @return the full file part in form /dir/filename.1.kawanfw.chunk, /dir/filename.2.kawanfw.chunk, etc...
     */
    private String getFilePart(String filePath, int countFile) {
	return filePath + "." + countFile + ".kawanfw.chunk";
    }    
    

}
