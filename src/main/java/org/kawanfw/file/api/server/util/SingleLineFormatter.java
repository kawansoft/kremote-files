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
package org.kawanfw.file.api.server.util;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.kawanfw.commons.util.SingleLineFormatterUtil;
import org.kawanfw.file.api.server.FileConfigurator;

/**
 * This {@code Formatter} is used by
 * {@link FileConfigurator#getLogger()}.
 * 
 * Works exactly as standard {@link SimpleFormatter}, but:
 * <ul>
 * <li>1) date is in short format dd/MM/yyyy,</li>
 * <li>2) each log output is contained in only one line,</li>
 * <li>3) a constructor with a boolean allows to display class name without
 * package name to reduce line length.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class SingleLineFormatter extends SingleLineFormatterUtil {

    /**
     * Constructor.
     * 
     * @param doDisplayPackageName
     *            if true, full package name of class will be displayed when
     *            logging, else only simple class name will be displayed
     */
    public SingleLineFormatter(boolean doDisplayPackageName) {
	super(doDisplayPackageName);
    }

    /**
     * Format the given LogRecord. <br>
     * Format like {@link SimpleFormatter}, but:
     * <ul>
     * <li>1) date is in short format dd/MM/yyyy,</li>
     * <li>2) each log output is contained in only one line.</li>
     * </ul>
     * If class was instanced with {@code new SingleLineFormatter(false)},
     * package names are not displayed.
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
	return super.format(record);
    }


}
