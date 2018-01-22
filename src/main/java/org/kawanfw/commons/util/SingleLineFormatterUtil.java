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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.kawanfw.file.api.server.DefaultFileConfigurator;


/**
 * This {@code Formatter} is used by
 * {@link DefaultFileConfigurator#getLogger()}.
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
public class SingleLineFormatterUtil extends Formatter {

    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator");
    
    Date dat = new Date();
    private final static String format = "{0,date, short} {0,time}";
    private MessageFormat formatter;

    private Object args[] = new Object[1];

//    // Line separator string. This is the value of the line.separator
//    // property at the moment that the SimpleFormatter was created.
//    private String lineSeparator = (String) java.security.AccessController
//	    .doPrivileged(new sun.security.action.GetPropertyAction(
//		    "line.separator"));

    /** if true, full package name of class will be displayed when logging */
    private boolean doDisplayPackageName = true;

    /**
     * Constructor.
     * 
     * @param doDisplayPackageName
     *            if true, full package name of class will be displayed when
     *            logging, else only simple class name will be displayed
     */
    public SingleLineFormatterUtil(boolean doDisplayPackageName) {
	super();
	this.doDisplayPackageName = doDisplayPackageName;
    }

    /**
     * Format the given LogRecord. <br>
     * Format like {@link SimpleFormatter}, but:
     * <ul>
     * <li>1) date is in short format dd/MM/yyyy,</li>
     * <li>2) each log output is contained in only one line.</li>
     * </ul>
     * If class was instancied with {@code new SingleLineFormatter(false)},
     * package names are not displayed.
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
	StringBuffer sb = new StringBuffer();
	// Minimize memory allocations here.
	dat.setTime(record.getMillis());
	args[0] = dat;
	StringBuffer text = new StringBuffer();
	if (formatter == null) {
	    formatter = new MessageFormat(format);
	}
	formatter.format(args, text, null);
	sb.append(text);
	sb.append(" ");
	if (record.getSourceClassName() != null) {

	    if (!doDisplayPackageName) {
		try {
		    String simpleName = Class.forName(
			    record.getSourceClassName()).getSimpleName();
		    sb.append(simpleName);
		} catch (ClassNotFoundException e) {
		    sb.append(record.getSourceClassName());
		    e.printStackTrace();
		}
	    } else {
		sb.append(record.getSourceClassName());
	    }

	} else {
	    sb.append(record.getLoggerName());
	}
	if (record.getSourceMethodName() != null) {
	    sb.append(" ");
	    sb.append(record.getSourceMethodName());
	}

	// sb.append(lineSeparator);
	sb.append(" ");

	String message = formatMessage(record);
	sb.append(record.getLevel().getLocalizedName());
	sb.append(": ");
	sb.append(message);
	sb.append(CR_LF);
	if (record.getThrown() != null) {
	    try {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		record.getThrown().printStackTrace(pw);
		pw.close();
		sb.append(sw.toString());
	    } catch (Exception ex) {
	    }
	}
	return sb.toString();
    }

}
