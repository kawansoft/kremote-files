/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2017,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * AceQL HTTP is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * AceQL HTTP is distributed in the hope that it will be useful,               
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
package org.kawanfw.file.api.server.session;

/**
 * Interface that defines how to generate and verify token for sessions.
 * <br>
 * <br>
 * Interface implementation allows to:
 * <br><br>
 * <ul>
 * <li>Define how to generate a token.</li>
 * <li>Define the sessions lifetime.</li>
 * <li>Define how to verify that the stored session is valid and not
 * expired.</li>
 * </ul>
 * <p>
 * The default {@link DefaultJwtSessionConfigurator} implementation generates self
 * contained JWT (JSON Web Tokens) and there is no info storage on the
 * server.
 * 
 * @author Nicolas de Pomereu
 */
public interface SessionConfigurator {
    /**
     * Generates a unique token for a username. The
     * token is used to authenticate clients calls that pass it at each
     * HTTP call.<br>
     * 
     * @param username
     *            the client username
     * @return a unique session id for the username.
     */
    public String generateToken(String username);

    /**
     * Loads the username stored for the passed token.
     * 
     * @param token
     *            the token id
     * @return the username stored for the passed token
     */
    public String getUsername(String token);

    /**
     * Loads the creation time of the instance
     * 
     * @param token
     *            the token id
     * @return the creation time of the instance
     */
    public long getCreationTime(String token);

    /**
     * Removes storage for the passed token. Should be called when user ends session.
     * 
     * @param token
     * 		the token id
     */
    public void remove(String token);

    /**
     * Perform the verification against the given token.
     * 
     * @param token
     *            the token verify
     * @return true if the sessionId is valid
     */
    public boolean verifyToken(String token);

    /**
     * Allows to define the sessions lifetime in minutes. 0 means that the session never expires.
     * 
     * @return the sessions lifetime in minutes
     */
    public int getSessionTimelife();
}
