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
package org.kawanfw.file.test.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ProgressUtil {

    /**
     * Procteected Constructor
     */
    protected ProgressUtil() {

    }

    /**
     * Return the date in shodrt format in dd/mm/yyyy hh:mm in French, etc.
     * @param date      the date
     * @return  the formated date
     */
    public static String formatTime(Date date) {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);        
        return df.format(date);
    }
    
    /**
     * Displays the percent progress on a PrintStream
     * 
     * @param cpt	the percent progress
     * @param pr 	the print stream
     */
    public static void percentPrintl(int cpt, PrintStream pr) {
	if (cpt < 10)
	    pr.print("0" + cpt++ + "%" + " ");
	else
	    pr.print(cpt++ + "%" + " ");
	if ((cpt % 20) == 0)
	    pr.println();

    }

}
