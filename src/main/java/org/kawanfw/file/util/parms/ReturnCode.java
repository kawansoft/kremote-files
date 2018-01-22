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
package org.kawanfw.file.util.parms;
/**
 * Class contains all return codes
 * 
 * @author Nicolas de Pomereu
 * 
 */

public class ReturnCode {
    public static final String OK = "OK";
    public static final String INVALID_LOGIN_OR_PASSWORD = "INVALID_LOGIN_OR_PASSWORD";
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    public static final String FILE_SIZE_TOO_BIG = "FILE_SIZE_TOO_BIG";
    public static final String HTTP_NOT_LOGGED = "HTTP_NOT_LOGGED";
    public static final String LIMIT_TOO_BIG = "LIMIT_TOO_BIG";
    public static final String SERVER_ON_ERROR = "SERVER_ON_ERROR";
    public static final String INVALID_FILE_NAME = "INVALID_FILE_NAME";
    public static final String IO_EXCEPTION = "IO_EXCEPTION";
    public static final String HTTP_CONNECTION_ERROR = "HTTP_CONNECTION_ERROR";
    public static final String ERR_HTTP_MALFORMED_URL_EXCEPTION = "ERR_HTTP_MALFORMED_URL_EXCEPTION";
    public static final String ERR_HTTP_CONNECT_EXCEPTION = "ERR_HTTP_CONNECT_EXCEPTION";
    public static final String ERR_HTTP_SOCKET_EXCEPTION = "ERR_HTTP_SOCKET_EXCEPTION";
    public static final String ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION = "ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION";
    public static final String ERR_HTTP_PROTOCOL_EXCEPTION = "ERR_HTTP_PROTOCOL_EXCEPTION";
    public static final String ERR_HTTP_IO_EXCEPTION = "ERR_HTTP_IO_EXCEPTION";
    public static final String ERR_HTTP_OPERATION_NOT_ALLOWED = "ERR_HTTP_OPERATION_NOT_ALLOWED";
    /**
     * Class not to be instancied
     */
    protected ReturnCode() {
    }
    /**
     * Return the Error Label corresponding to the Error Code
     * 
     * @param ErrorCode
     *            The Error Code
     * @return the Error Label corresponding to the Error Code
     */
    /*
     * public static String getErrorLabel(String ErrorCode) { String errorLabel
     * = null;
     * 
     * if (ErrorCode.equals(FILE_NOT_FOUND)) errorLabel =
     * "Ce fichier n'existe pas."; else if (ErrorCode.equals(FILE_SIZE_TOO_BIG))
     * errorLabel = "Fichier trop grand."; else if
     * (ErrorCode.equals(HTTP_CONNECTION_ERROR)) errorLabel =
     * "La connexion en HTTP ne fonctionne pas."; else if
     * (ErrorCode.equals(HTTP_NOT_LOGGED)) errorLabel =
     * "Vous n'êtes pas connecté."; else if
     * (ErrorCode.equals(INVALID_FILE_NAME)) errorLabel =
     * "Ce nom de fichier est invalide."; else if
     * (ErrorCode.equals(LIMIT_TOO_BIG)) errorLabel =
     * "La limite de 1000 lignes & été atteinte"; else if
     * (ErrorCode.equals(SERVER_ON_ERROR)) errorLabel =
     * "le Serveur est en Erreur."; else if
     * (ErrorCode.equals(INVALID_LOGIN_OR_PASSWORD)) errorLabel =
     * "le Login ou le Mot de passe est invalide.";
     * 
     * return errorLabel; }
     */
}
/**
 * 
 */
