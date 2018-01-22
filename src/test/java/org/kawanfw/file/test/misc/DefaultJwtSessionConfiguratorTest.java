/**
 * 
 */
package org.kawanfw.file.test.misc;

import java.util.Date;

import org.kawanfw.file.api.server.session.DefaultJwtSessionConfigurator;

/**
 * @author Nicolas de Pomereu
 *
 */
public class DefaultJwtSessionConfiguratorTest {

    /**
     * 
     */
    public DefaultJwtSessionConfiguratorTest() {
    }

    /**
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
	DefaultJwtSessionConfigurator defaultJwtSessionConfigurator = new DefaultJwtSessionConfigurator();
	String token = defaultJwtSessionConfigurator.generateToken("username");
	
	System.out.println(defaultJwtSessionConfigurator.getCreationTime(token));
	System.out.println("getSessionTime     : " + new Date(defaultJwtSessionConfigurator.getCreationTime(token)));
	System.out.println("getSessionTimelife(): " + defaultJwtSessionConfigurator.getSessionTimelife());
	
	int cpt = 0;
	while (true) {
	    cpt++;
	    Thread.sleep(1000);
	    System.out.println(defaultJwtSessionConfigurator.verifyToken(token) + " " + cpt);
	}

    }

}
