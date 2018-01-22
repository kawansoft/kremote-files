/**
 * 
 */
package org.kawanfw.file.api.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.kawanfw.commons.util.FrameworkFileUtil;
import org.kawanfw.commons.util.Tag;
import org.kawanfw.file.api.server.DefaultFileConfigurator;
import org.kawanfw.file.api.server.FileConfigurator;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 
 * This class provides static methods for SSH authentication to be used directly
 * in {@link FileConfigurator#login(String, char[], String)} implementations.
 * 
 * @see org.kawanfw.file.api.server.SshAuthFileConfigurator
 * @author Nicolas de Pomereu
 * @since 1.0
 */
public class Ssh {

    /**
     * Tries to open a SSH session on a host for authentication.
     * <ul>
     * <li>If the {@code user.home/.kawansoft/sshAuth.properties} file exists: <br>
     * the {@code (usernname, password)} couple is checked against the SSH server of this
     * host with the properties {@code host} for the hostname and {@code port}
     * for the port in the {@code user.home/.kawansoft/sshAuth.properties} file.
     * </li>
     * <li>If {@code sshAuth.properties} file does not exists: <br>
     * the host IP is used as hostname value and port is 22.</li>
     * </ul>
     * <br>
     * {@code user.home} is the one of the running servlet container.
     * <p>
     * The internal SSH client Java library used is <a
     * href="http://www.jcraft.com/jsch/">JSch</a>. <br>
     * Note that there is no host key checking ({@code "StrictHostKeyChecking"}
     * is set to {@code "no"}).
     * 
     * @param username
     *            the client username
     * @param password
     *            the password to connect to the server
     * 
     * @return <code>true</code> if the user is able to open a SSH session with
     *         the passed parameters
     * 
     * @throws IOException
     *             if a {@code host} or {@code port} property can not be found
     *             in the {@code sshAuth.properties} or error reading property
     *             file or IP address of the host can not be accessed.
     * @throws NumberFormatException
     *             if the {@code port} property is no numeric
     * 
     */
    public static boolean login(String username, char[] password)
	    throws IOException, NumberFormatException {
	String host = null;
	int port = -1;

	String userHomeKawanSoft = FrameworkFileUtil
		.getUserHomeDotKawansoftDir();
	File file = new File(userHomeKawanSoft + File.separator
		+ "sshAuth.properties");

	if (file.exists()) {
	    Properties prop = new Properties();
	    InputStream in = null;

	    try {
		in = new FileInputStream(file);
		prop.load(in);
	    } finally {
		IOUtils.closeQuietly(in);
	    }

	    host = prop.getProperty("host");
	    String portStr = prop.getProperty("port");

	    if (host == null) {
		throw new IOException(
			Tag.PRODUCT
				+ " property host not found in sshAuth.properties file.");
	    }

	    if (portStr == null) {
		throw new IOException(
			Tag.PRODUCT
				+ " property port not found in sshAuth.properties file.");
	    }

	    port = Integer.parseInt(portStr);

	} else {
	    
	    try {
		InetAddress ip = InetAddress.getLocalHost();
		host = ip.getHostAddress();
		port = 22;
		    
	    } catch (Exception e) {
		throw new IOException(Tag.PRODUCT
			+ " Can not retrieve IP address of the host.");
	    }
		
	}

	return login(host, port, username, password);
    }

    /**
     * Tries to open a SSH session on a passed host for authentication.
     * <p>
     * The internal SSH client Java library used is <a
     * href="http://www.jcraft.com/jsch/">JSch</a>. <br>
     * Note that there is no host key checking ( {@code "StrictHostKeyChecking"}
     * is set to {@code "no"}).
     * 
     * @param host
     *            the host name or IP address of the SSH server
     * @param port
     *            the port number of the SSH server
     * @param username
     *            the user name for authentication
     * @param password
     *            the password for authentication
     * 
     * @return <code>true</code> if the user is able to open a SSH session with
     *         the passed parameters
     * 
     * @throws IOException
     *             if <code>username</code> or <code>host</code> are invalid.
     * 
     */
    public static boolean login(String host, int port, String username,
	    char[] password) throws IOException {
	// Create a JSch Session with passed values
	JSch jsch = new JSch();
	Session session = null;

	try {
	    session = jsch.getSession(username, host, port);
	} catch (JSchException e) {
	    throw new IOException(
		    Tag.PRODUCT + " username or host is invalid.", e);
	}

	session.setPassword(new String(password));
	session.setConfig("StrictHostKeyChecking", "no");

	// Ok try to connect
	boolean connected = false;
	try {
	    session.connect();
	    connected = true;
	    session.disconnect();
	} catch (JSchException e) {
	    new DefaultFileConfigurator().getLogger().log(
		    Level.WARNING,
		    "SSH connection impossible for " + username + "@" + host + ":"
			    + port + ". (" + e.toString() + ")");
	}

	return connected;
    }

}
