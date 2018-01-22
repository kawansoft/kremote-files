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

import java.util.List;

/**
 * @author Nicolas de Pomereu
 *
 */
public class IpUtil {

    /**
     * Protected
     */
    protected IpUtil() {
	
    }

    /**
     * TestReload if an ip is blacklisted against a blacklist of ip
     * 
     * @param ip
     *            the IP address to test
     * @param ipsBlacklist
     *            the list of blacklisted ip
     * 
     * @return true if the ip is banned, else false
     */
    public static boolean isIpBlacklisted(String ip, List<String> ipsBlacklist) {
        if (ip == null) {
            return false;
        }
    
        if (ipsBlacklist == null) {
            return false;
        }
    
        for (String theBannedIp : ipsBlacklist) {
            if (theBannedIp.contains("/")) {
        	IpSubnet ipSubnet = new IpSubnet(theBannedIp);
        	if (ipSubnet.contains(ip)) {
    
        	    return true;
        	} else {
        	    // Nothing
        	}
            } else {
        	if (ip.equals(theBannedIp)) {
        	    return true;
        	}
            }
        }
    
        return false;
    }

    /**
     * TestReload if an ip is whitelisted against a whilelist of ip
     * 
     * @param ip
     *            the IP address to test
     * @param ipsWhitelist
     *            the list of whitelisted ip
     * 
     * @return true if the ip is whitelisted, else false
     */
    public static boolean isIpWhitelisted(String ip, List<String> ipsWhitelist) {
        if (ip == null) {
            return false;
        }

        if (ipsWhitelist == null || ipsWhitelist.isEmpty()) {
            return true;
        }
    
        for (String theWhitelistedIp : ipsWhitelist) {
            if (theWhitelistedIp.contains("/")) {
        	IpSubnet ipSubnet = new IpSubnet(theWhitelistedIp);
        	if (ipSubnet.contains(ip)) {
    
        	    return true;
        	} else {
        	    // Nothing
        	}
            } else {
        	if (ip.equals(theWhitelistedIp)) {
        	    return true;
        	}
            }
        }
    
        return false;
    }

    
    
}
