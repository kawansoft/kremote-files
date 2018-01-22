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
package org.kawanfw.file.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * LoginSpeedReducer 24 avr. 2005 18:34:31
 * 
 * <br>
 * Purpose of this class is to add a delay between username attempt, to prevent
 * robots to "guess" username and passwords. <br>
 * If a second username attempt fail, there is a 3 seconds delay before next
 * username.
 */
public class LoginSpeedReducer {

    /** Container for session storage with (sessionId, Attempt Number) */
    private static Map<String, Integer> mapLoginAttempt = new HashMap<String, Integer>();

    /** The delay in seconds between Two username */
    private static int LOGIN_DELAY = 3;

    /** The number of attempts authorized before delay **/
    private static int LOGIN_MAX_ATTEMPTS = 3;

    /** The username */
    private String username = null;

    /**
     * Constructor
     * 
     * @param username
     *            the passed Login Id
     */
    public LoginSpeedReducer(String username) {
	if (username == null) {
	    throw new IllegalArgumentException("username can not be null!");
	}

	this.username = username.toLowerCase();
    }

    /**
     * TestReload the attempts on Login
     */
    public void checkAttempts() {
	int attempt = 0;

	// TestReload if the username already exists in Map
	if (mapLoginAttempt.containsKey(username)) {
	    // If yes, test the attempt number
	    attempt = mapLoginAttempt.get(username).intValue();
	} else {
	    mapLoginAttempt.put(username, 1);
	}

	// Increment the Attempt:
	attempt++;

	if (attempt >= LOGIN_MAX_ATTEMPTS) {
	    // OK, total of maximum attempts is reached !
	    // delay for n seconds & re-init values
	    try {
		Thread.sleep(LOGIN_DELAY * 1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	    // OK to purge now! For this username only.
	    this.removeUsername();
	} else {
	    // Push back with new value
	    mapLoginAttempt.put(username, attempt);
	}

    }

    /**
     * Purge the username if success
     */
    public void removeUsername() {
	if (mapLoginAttempt.containsKey(username)) {
	    mapLoginAttempt.remove(username);
	}
    }

}
