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
package org.kawanfw.file.servlet.convert;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.commons.util.convert.Pbe;
import org.kawanfw.file.api.server.FileConfigurator;

/**
 * 
 * Wrapper/holder for org.apache.commons.fileupload.util.Streams that will allow
 * to decrypt correctly the request.getParameter()
 * 
 * @author Nicolas de Pomereu
 * 
 */

public class StreamsEncrypted {

    /**
     * Protected constructor
     */
    protected StreamsEncrypted() {

    }

    /**
     * @param stream
     *            The ServletFileUpload input stream
     * @param fileConfigurator
     *            Used to get the password for encryption
     */
    public static String asString(InputStream stream,
	    FileConfigurator fileConfigurator)
	    throws IOException {
	String value = Streams.asString(stream);

	if (isEncrypted(value, fileConfigurator)) {
	    try {
		value = StringUtils.substringAfter(value, Pbe.KAWANFW_ENCRYPTED);		
		
		//value = new Pbe().decryptFromHexa(value,
		//	CommonsConfiguratorCall.getEncryptionPassword(commonsConfigurator));		
		
		return value;
	    } catch (Exception e) {
		String message = Tag.PRODUCT_USER_CONFIG_FAIL
			+ " Impossible to decrypt the value " + value;
		message += ". Check that password values are the same on client and server side.";

		throw new IOException(message, e);
	    }
	} else {
	    return value;
	}
    }

    /**
     * Says it the request is encrypted
     * 
     * @param parameterName
     *            the parameter name
     * @return if the request is encrypted
     */
    private static boolean isEncrypted(String value,
	    FileConfigurator fileConfigurator) throws IOException {	
	
	// Futur usage
	// char [] password = fileConfigurator.getEncryptionPassword();

	char [] password = {};
	
	if (fileConfigurator != null
		&& password != null) {
	    if (value != null && !value.isEmpty()
		    && value.startsWith(Pbe.KAWANFW_ENCRYPTED)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}	
	
    }

}
