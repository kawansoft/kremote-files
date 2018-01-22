/**
 * 
 */
package org.kawanfw.file.test.misc;

import org.kawanfw.file.api.server.session.DefaultJwtSessionConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultSessionConfiguratorTest {

    /**
     * 
     */
    public DefaultSessionConfiguratorTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	DefaultJwtSessionConfigurator defaultSessionConfigurator = new DefaultJwtSessionConfigurator();
	String token = defaultSessionConfigurator.generateToken("username1");
	System.out.println("token: " + token);
	
	boolean verified = defaultSessionConfigurator.verifyToken(token);
	System.out.println("verified: " + verified);

    }

}
