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
package org.kawanfw.file.reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.IOUtils;
import org.kawanfw.commons.util.Base64;
import org.kawanfw.file.api.client.SessionParameters;

/**
 * 
 */

/**
 * @author Nicolas de Pomereu
 *
 * Allows to serialize any serializable class instance to base64 string
 * with serialization and to get back the instance with deserialization.
 * <br><br>
 * The {@link #toPropertyValue(Object)} allows to serialize a {@link HttpProxy} or {@link SessionParameters} instance and to
 * pass the serialized instance as a property value to the {@link RemoteDriver}.
 * 
 */
public class ClassSerializer<E> {

    /**
     * Constructors
     */
    public ClassSerializer() {

    }

    /**
     * Serializes a class instance into a base64 String.
     * 
     * @param element
     *            the class instance to Serialize
     * @return the base64 string containing the serialized class instance
     * 
     * @throws IOException
     *             Any exception thrown by the underlying OutputStream.
     */
    public String toBase64(E element) throws IOException {

	String serializedBase64 = null;

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = null;

	try {
	    oos = new ObjectOutputStream(bos);
	    oos.writeObject(element);
	    oos.flush();
	    byte[] byteArray = bos.toByteArray();
	    serializedBase64 = Base64.byteArrayToBase64(byteArray);

	} finally {
	    IOUtils.closeQuietly(oos);
	}

	return serializedBase64;
    }

    /**
     * Deserializes a class instance from a Base64 String created with {@link #toBase64(Object)}
     * 
     * @param serializedBase64
     *            the Base64 serialized class
     * @return the deserialized class instance
     * 
     * @throws IOException
     *             Any of the usual Input/Output related exceptions.
     * @throws ClassNotFoundException
     *             Class of a serialized object cannot be found.
     */
    @SuppressWarnings("unchecked")
    public E fromBase64(String serializedBase64) throws IOException,
	    ClassNotFoundException {

	E element = null;

	byte[] byteArray = Base64.base64ToByteArray(serializedBase64);
	
	ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
	ObjectInputStream ois = null;

	try {
	    ois = new ObjectInputStream(bis);
	    element = (E) ois.readObject();
	    return element;
	} finally {
	    IOUtils.closeQuietly(ois);
	}
    }

    /**
     * Returns a clean serialized Base64 representation of the class instance
     * that can be passed as a property value to the {@code RemoteDriver} that
     * will be able to deserialize all properties of the class instance and add them
     * as properties values.
     * 
     * @param element
     *            the class instance to pass
     * 
     * @return a clean serialized Base64 representation of the class instance
     *         that can be passed as a property value to the
     *         {@code RemoteDriver}.
     */

    public static String toPropertyValue(Object element) {

	ClassSerializer<Object> classeStorer = new ClassSerializer<Object>();
	String serializedBase64 = null;

	try {
	    serializedBase64 = classeStorer.toBase64(element);
	} catch (IOException e) {
	    throw new IllegalArgumentException(e);
	}

	return serializedBase64;
    }

}
