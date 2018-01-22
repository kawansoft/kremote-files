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
package org.kawanfw.commons.codec;
/**
 * <p>Provides the highest level of abstraction for Decoders.
 * This is the sister interface of {@link Encoder}.  All
 * Decoders implement this common generic interface.</p>
 * 
 * <p>Allows a user to pass a generic Object to any Decoder 
 * implementation in the codec package.</p>
 * 
 * <p>One of the two interfaces at the center of the codec package.</p>
 * 
 * @author Apache Software Foundation
 * @version $Id: Decoder.java 797690 2009-07-24 23:28:35Z ggregory $
 */
public interface Decoder {
    /**
     * Decodes an "encoded" Object and returns a "decoded"
     * Object.  Note that the implementation of this
     * interface will try to cast the Object parameter
     * to the specific type expected by a particular Decoder
     * implementation.  If a {@link ClassCastException} occurs
     * this decode method will throw a DecoderException.
     * 
     * @param pObject an object to "decode"
     * 
     * @return a 'decoded" object
     * 
     * @throws DecoderException a decoder exception can
     * be thrown for any number of reasons.  Some good
     * candidates are that the parameter passed to this
     * method is null, a param cannot be cast to the
     * appropriate type for a specific encoder.
     */
    Object decode(Object pObject) throws DecoderException;
}  
