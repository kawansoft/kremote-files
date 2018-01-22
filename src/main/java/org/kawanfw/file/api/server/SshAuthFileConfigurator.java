/**
 * 
 */
package org.kawanfw.file.api.server;

import java.io.IOException;

import org.kawanfw.file.api.server.util.Ssh;

/**
 * A concrete {@code FileConfigurator} that extends {@code DefaultFileConfigurator} and allows zero-code client 
 * {@code (usernname, password)} authentication using SSH.
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class SshAuthFileConfigurator extends DefaultFileConfigurator
	implements FileConfigurator {

    /** 
     * Allows using SSH to authenticate the remote {@code (usernname, password)}  couple
     * sent by the client side
     * <p>
     * Returns the result of {@link Ssh#login(String, char[])} method.
     * 
     * @param username
     *            the client username
     * @param password
     *            the password to connect to the server
     * @return <code>true</code> if the (login, password) couple is
     *         correct/valid as a SSH user on this host. If false, the client
     *         side will not be authorized to send any command.
     * @throws IOException
     *             if wrapped {@code Ssh.login(String, char[])} throws an
     *             I/O Exception.
     */
    @Override
    public boolean login(String username, char[] password, String ipAddress) throws IOException {
	return Ssh.login(username, password);
    }

}
