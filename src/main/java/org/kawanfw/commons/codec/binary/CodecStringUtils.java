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
package org.kawanfw.commons.codec.binary;

import java.io.UnsupportedEncodingException;

import org.kawanfw.commons.codec.CharEncoding;

/**
 * Converts String to and from bytes using the encodings required by the Java specification. These encodings are specified in <a
 * href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
 * 
 * @see CharEncoding
 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: StringUtils.java 801391 2009-08-05 19:55:54Z ggregory $
 * @since 1.4
 */
public class CodecStringUtils {

    /**
     * Encodes the given string into a sequence of bytes using the ISO-8859-1 charset, storing the result into a new
     * byte array.
     * 
     * @param string
     *            the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never according the the Java specification.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesIso8859_1(String string) {
        return CodecStringUtils.getBytesUnchecked(string, CharEncoding.ISO_8859_1);
    }

    /**
     * Encodes the given string into a sequence of bytes using the US-ASCII charset, storing the result into a new byte
     * array.
     * 
     * @param string
     *            the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never according the the Java specification.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUsAscii(String string) {
        return CodecStringUtils.getBytesUnchecked(string, CharEncoding.US_ASCII);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16 charset, storing the result into a new byte
     * array.
     * 
     * @param string
     *            the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never according the the Java specification.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16(String string) {
        return CodecStringUtils.getBytesUnchecked(string, CharEncoding.UTF_16);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16BE charset, storing the result into a new byte
     * array.
     * 
     * @param string
     *            the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never according the the Java specification.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16Be(String string) {
        return CodecStringUtils.getBytesUnchecked(string, CharEncoding.UTF_16BE);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16LE charset, storing the result into a new byte
     * array.
     * 
     * @param string
     *            the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never according the the Java specification.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16Le(String string) {
        return CodecStringUtils.getBytesUnchecked(string, CharEncoding.UTF_16LE);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte
     * array.
     * 
     * @param string
     *            the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when the charset is missing, which should be never according the the Java specification.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf8(String string) {
        return CodecStringUtils.getBytesUnchecked(string, CharEncoding.UTF_8);
    }

    /**
     * Encodes the given string into a sequence of bytes using the named charset, storing the result into a new byte
     * array.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and rethrows it as {@link IllegalStateException}, which
     * should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.
     * </p>
     * 
     * @param string
     *            the String to encode
     * @param charsetName
     *            The name of a required {@link java.nio.charset.Charset}
     * @return encoded bytes
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a
     *             required charset name.
     * @see CharEncoding
     * @see String#getBytes(String)
     */
    public static byte[] getBytesUnchecked(String string, String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw CodecStringUtils.newIllegalStateException(charsetName, e);
        }
    }

    private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the given charset.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and re-throws it as {@link IllegalStateException}, which
     * should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.
     * </p>
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @param charsetName
     *            The name of a required {@link java.nio.charset.Charset}
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a
     *             required charset name.
     * @see CharEncoding
     * @see String#String(byte[], String)
     */
    public static String newString(byte[] bytes, String charsetName) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw CodecStringUtils.newIllegalStateException(charsetName, e);
        }
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the ISO-8859-1 charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the
     *             charset is required.
     */
    public static String newStringIso8859_1(byte[] bytes) {
        return CodecStringUtils.newString(bytes, CharEncoding.ISO_8859_1);
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the US-ASCII charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the
     *             charset is required.
     */
    public static String newStringUsAscii(byte[] bytes) {
        return CodecStringUtils.newString(bytes, CharEncoding.US_ASCII);
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-16 charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the
     *             charset is required.
     */
    public static String newStringUtf16(byte[] bytes) {
        return CodecStringUtils.newString(bytes, CharEncoding.UTF_16);
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-16BE charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the
     *             charset is required.
     */
    public static String newStringUtf16Be(byte[] bytes) {
        return CodecStringUtils.newString(bytes, CharEncoding.UTF_16BE);
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-16LE charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the
     *             charset is required.
     */
    public static String newStringUtf16Le(byte[] bytes) {
        return CodecStringUtils.newString(bytes, CharEncoding.UTF_16LE);
    }

    /**
     * Constructs a new <code>String</code> by decoding the specified array of bytes using the UTF-8 charset.
     * 
     * @param bytes
     *            The bytes to be decoded into characters
     * @return A new <code>String</code> decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     *             Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen since the
     *             charset is required.
     */
    public static String newStringUtf8(byte[] bytes) {
        return CodecStringUtils.newString(bytes, CharEncoding.UTF_8);
    }

}
