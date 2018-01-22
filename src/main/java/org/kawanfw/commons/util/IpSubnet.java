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

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Description: This class allows to check if an IP-Address is contained in a
 * subnet.<BR>
 * Supported Formats for the Subnets are: 1.1.1.1/255.255.255.255 or 1.1.1.1/32
 * (CIDR-Notation)<BR>
 * <BR>
 * <BR>
 * Example1:<BR>
 * <center>IpSubnet ips = new IpSubnet("192.168.1.0/24");<BR>
 * System.out.println("Result: "+ ips.contains("192.168.1.123"));<BR>
 * </center> <BR>
 * <BR>
 * Example2:<BR>
 * <center>IpSubnet ips = new IpSubnet("192.168.1.0/255.255.255.0");<BR>
 * System.out.println("Result: "+ ips.contains("192.168.1.123"));<BR>
 * </center> <BR>
 * <BR>
 * Example3:<BR>
 * <center>IpSubnet ips = new IpSubnet();<BR>
 * ips.setNetAddress("192.168.1.0/255.255.255.0");
 * System.out.println("Result: "+ ips.contains("192.168.1.123"));<BR>
 * </center>
 * 
 * @author Karsten Pawlik
 */
public class IpSubnet {

    private StringTokenizer ni = null;
    private StringTokenizer nm = null;
    private StringTokenizer ia = null;

    private int[] netmask = new int[4];
    private int[] netid = new int[4];
    private int[] ip = new int[4];
    private int i = 0;

    private StringBuffer sba = new StringBuffer();
    private StringBuffer sbb = new StringBuffer();

    /**
     * Create an empty IpSubnet.<BR>
     * i.e.: IpSubnet subnet = new IpSubnet();<BR>
     * <BR>
     * Please note that the netMask and netId have to be set before using any
     * method.
     * */
    public IpSubnet() {
    }

    /**
     * Create IpSubnet using the CIDR or normal Notation<BR>
     * i.e.: IpSubnet subnet = new IpSubnet("10.10.10.0/24"); or IpSubnet subnet
     * = new IpSubnet("10.10.10.0/255.255.255.0");
     * 
     * @param netAddress
     *            a network address as string.
     * */
    public IpSubnet(String netAddress) {
	setNetAddress(netAddress);
    }

    /**
     * Sets the Network Address in either CIDR or Decimal Notation.<BR>
     * i.e.: setNetAddress("1.1.1.1/24"); or<BR>
     * setNetAddress("1.1.1.1/255.255.255.0");<BR>
     * 
     * @param netAddress
     *            a network address as string.
     * */
    public void setNetAddress(String netAddress) {
	Vector<String> vec = new Vector<String>();
	StringTokenizer st = new StringTokenizer(netAddress, "/");
	while (st.hasMoreTokens()) {
	    vec.add((String) st.nextElement());
	}

	if (vec.get(1).toString().length() < 3) {
	    setNetId(vec.get(0).toString());
	    setCidrNetMask(vec.get(1).toString());
	} else {
	    setNetId(vec.get(0).toString());
	    setNetMask(vec.get(1).toString());
	}
    }

    /**
     * Sets the IP-Address to compare (currently only used internally). Format
     * of IP: "1.1.1.1"<BR>
     * 
     * @param ipAddr
     *            an IP address.
     * */
    public void setIpAddr(String ipAddr) {
	ia = new StringTokenizer(ipAddr, ".");
	i = 0;

	while (ia.hasMoreTokens()) {
	    ip[i] = Integer.parseInt(ia.nextToken());
	    i++;
	}
    }

    /**
     * Sets the BaseAdress of the Subnet.<BR>
     * i.e.: setNetId("192.168.1.0");
     * 
     * @param netId
     *            a network ID
     * */
    public void setNetId(String netId) {
	ni = new StringTokenizer(netId, ".");
	i = 0;

	while (ni.hasMoreTokens()) {
	    netid[i] = Integer.parseInt(ni.nextToken());
	    i++;
	}
    }

    /**
     * Sets the Subnet's Netmask in Decimal format.<BR>
     * i.e.: setNetMask("255.255.255.0");
     * 
     * @param netMask
     *            a network mask
     * */
    public void setNetMask(String netMask) {
	nm = new StringTokenizer(netMask, ".");
	i = 0;

	while (nm.hasMoreTokens()) {
	    netmask[i] = Integer.parseInt(nm.nextToken());
	    i++;
	}
    }

    /**
     * Transforms the CIDR Netmask to a Decimal Netmask and then sets it.<BR>
     * i.e.: setCidrNetMask("24");
     * 
     * @param cidrNetMask
     *            a netmask in CIDR notation
     * */
    public void setCidrNetMask(String cidrNetMask) {
	StringBuffer cnm = new StringBuffer();
	StringBuffer txtValue = new StringBuffer();
	for (i = 0; i < 32; i++) {
	    cnm.append('0');
	}
	for (i = 0; i < Integer.parseInt(cidrNetMask); i++) {
	    cnm.setCharAt(i, '1');
	}

	txtValue.append(Integer.parseInt(cnm.substring(0, 8), 2));
	txtValue.append(".");
	txtValue.append(Integer.parseInt(cnm.substring(8, 16), 2));
	txtValue.append(".");
	txtValue.append(Integer.parseInt(cnm.substring(16, 24), 2));
	txtValue.append(".");
	txtValue.append(Integer.parseInt(cnm.substring(24, 32), 2));

	setNetMask(txtValue.toString());
    }

    /**
     * Compares the given IP-Address against the Subnet and returns true if the
     * ip is in the subnet-ip-range and false if not.
     * 
     * @param ipAddr
     *            an ipaddress
     * @return returns true if the given IP address is inside the currently set
     *         network.
     * */
    public boolean contains(String ipAddr) {
	setIpAddr(ipAddr);

	for (i = 0; i < ip.length; i++) {
	    int a = netid[i];
	    int b = netmask[i];
	    int c = (b & a);

	    int d = ip[i];
	    int e = (d & b);
	    sba.append(c);
	    sbb.append(e);
	}

	return sba.toString().equals(sbb.toString());
    }

    /**
     * Compares the given IP-Address against the Subnet and returns true if the
     * ip is in the subnet-ip-range and false if not.
     * 
     * @param netAddress
     *            a network address
     * @param ipAddr
     *            an IP address
     * @return returns true if the given IP is inside the given network, false
     *         otherwise.
     * */
    public boolean contains(String netAddress, String ipAddr) {
	setNetAddress(netAddress);
	return contains(ipAddr);
    }

    /**
     * returns a string representation of this object.
     */
    public String toString() {
	return intArrayToOctetString(netid) + "/"
		+ intArrayToOctetString(netmask);
    }

    /**
     * converts an int array into a string of the form xxx.xxx.xxx.xxx<BR>
     * i.e.: 255.255.0.0
     * 
     * @param intArray
     *            array of ints
     * @return returns a formatted string.
     */
    public String intArrayToOctetString(int[] intArray) {
	StringBuffer out = new StringBuffer();
	for (int i = 0; i < intArray.length; i++) {
	    int currentId = intArray[i];
	    out.append(currentId);
	    if (i < intArray.length - 1)
		out.append(".");
	}
	return out.toString();
    }

    /**
     * @return returns the network id as int[]
     */
    public int[] getNetId() {
	return netid;
    }

    /**
     * @return returns the network mask as int[]
     */
    public int[] getNetMask() {
	return netmask;
    }

    /**
     * @return returns the network id as formatted string of the form
     *         xxx.xxx.xxx.xxx i.e.: 1.234.5.67
     */
    public String getNetIdAsString() {
	return intArrayToOctetString(netid);
    }

    /**
     * @return returns the network mask as formatted string of the form
     *         xxx.xxx.xxx.xxx<BR>
     *         i.e.: 255.255.255.0
     */
    public String getNetMaskAsString() {
	return intArrayToOctetString(netmask);
    }
}
