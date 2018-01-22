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

import java.util.Comparator;

/**
 * Strings are comparable, and this comparator allows 
 * you to configure it with an instance of a class
 * which implements StringEncoder.  This comparator
 * is used to sort Strings by an encoding scheme such
 * as Soundex, Metaphone, etc.  This class can come in
 * handy if one need to sort Strings by an encoded
 * form of a name such as Soundex.
 *
 * @author Apache Software Foundation
 * @version $Id: StringEncoderComparator.java 793391 2009-07-12 18:38:08Z ggregory $
 */
@SuppressWarnings("rawtypes")
public class StringEncoderComparator implements Comparator {

    /**
     * Internal encoder instance.
     */
    private final StringEncoder stringEncoder;

    /**
     * Constructs a new instance.
     * @deprecated as creating without a StringEncoder will lead to a 
     *             broken NullPointerException creating comparator.
     */
    public StringEncoderComparator() {
        this.stringEncoder = null;   // Trying to use this will cause things to break
    }

    /**
     * Constructs a new instance with the given algorithm.
     * @param stringEncoder the StringEncoder used for comparisons.
     */
    public StringEncoderComparator(StringEncoder stringEncoder) {
        this.stringEncoder = stringEncoder;
    }

    /**
     * Compares two strings based not on the strings 
     * themselves, but on an encoding of the two 
     * strings using the StringEncoder this Comparator
     * was created with.
     * 
     * If an {@link EncoderException} is encountered, return <code>0</code>.
     * 
     * @param o1 the object to compare
     * @param o2 the object to compare to
     * @return the Comparable.compareTo() return code or 0 if an encoding error was caught.
     * @see Comparable
     */
    @SuppressWarnings({"unchecked" })
    public int compare(Object o1, Object o2) {

        int compareCode = 0;

        try {
            Comparable s1 = (Comparable) this.stringEncoder.encode(o1);
            Comparable s2 = (Comparable) this.stringEncoder.encode(o2);
            compareCode = s1.compareTo(s2);
        } 
        catch (EncoderException ee) {
            compareCode = 0;
        }
        return compareCode;
    }

}
