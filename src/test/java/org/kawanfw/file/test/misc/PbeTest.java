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

import java.io.File;
import java.util.Date;

import org.kawanfw.commons.util.FrameworkFileUtil;
import org.kawanfw.commons.util.convert.Pbe;
import org.kawanfw.file.test.util.MessageDisplayer;

/**
 * @author Nicolas de Pomereu
 *
 */
public class PbeTest {

    /**
     * 
     */
    public PbeTest() {
	
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {


	MessageDisplayer.display(new Date() + " Starting file encryption...");

	for (int i = 0; i < 100; i++) {
	    String userHome = FrameworkFileUtil.getUserHome();
	    File fileIn = new File(userHome + File.separator + "Koala.jpg");
	    File fileOut = new File(fileIn.toString() + ".enc");

	    Pbe pbe = new Pbe();
	    pbe.encryptFile(fileIn, fileOut, "loveme$".toCharArray());

	    File fileEnc = new File(fileOut.toString());
	    File fileDec = new File(fileIn.toString() + ".dec.jpg");

	    Pbe pbe2 = new Pbe();
	    pbe2.decryptFile(fileEnc, fileDec, "loveme$".toCharArray());
	}

	MessageDisplayer.display(new Date() + " Done.");

    }

}
