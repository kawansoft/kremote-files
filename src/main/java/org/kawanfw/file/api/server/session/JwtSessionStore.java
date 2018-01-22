/**
 * 
 */
package org.kawanfw.file.api.server.session;

import java.util.logging.Level;

import org.kawanfw.commons.server.util.ServerLogger;

/**
 * 
 * Allows to store the secret value to use for JWT token generation and verification.
 * 
 * @author Nicolas de Pomereu
 */
public class JwtSessionStore {

    private static boolean DEBUG = false;
    
    /**
     * The secret value used by JWT fot the session This value can be set at
     * startup with {@code jwt_secret_value} servlet init parameter in you
     * {@code web.xml}.
     * <br>
     * You can also set it with a program/servlet after server startup if you
     * want no stored value.
     */
    public static String JWT_SECRET_VALUE = "changeit";
    
    protected JwtSessionStore() {

    }

    /**
     * Returns the JWT secret value to use for JWT token generation and
     * verification.
     * 
     * @return the JWT secret value to use for JWT token generation and
     *         verification.
     */
    public static String getJwtSecretValue() {
	debug("jwt_secret_value: " + JWT_SECRET_VALUE);
	return JWT_SECRET_VALUE;
    }
    
    private static void debug(String s) {
	if (DEBUG) {
	    ServerLogger.getLogger().log(Level.WARNING, s);
	}
    }

}
