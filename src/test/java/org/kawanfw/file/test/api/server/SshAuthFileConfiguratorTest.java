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
package org.kawanfw.file.test.api.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.junit.Test;
import org.kawanfw.file.api.server.SshAuthFileConfigurator;

public class SshAuthFileConfiguratorTest {

    @Test
    public void test() throws IOException, SQLException {
	String username = "admin";
	String password = null;
	
	JPasswordField pf = new JPasswordField();
	pf.setFocusable(true);
	pf.requestFocus();
	int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

	char [] chars = null;
	
	if (okCxl == JOptionPane.OK_OPTION) {
	  password = new String(pf.getPassword());
	  chars =  password.toCharArray();
	}
	
	System.out.println(new Date());
	boolean connected = new SshAuthFileConfigurator().login(username, chars, "127.0.0.1");
	System.out.println(new Date());
	System.out.println("connected: " + connected);

    }

    public static void main(String[] args) throws Exception {

	new SshAuthFileConfiguratorTest().test();
    }
    
}
