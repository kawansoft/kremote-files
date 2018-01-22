package com.kremotefiles.quickstart;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.kawanfw.file.api.server.DefaultFileConfigurator;
import org.kawanfw.file.api.server.util.Sha1;

public class MyCommonsConfigurator extends DefaultFileConfigurator {

    /**
     * Our own Acme Company authentication of remote client users. This methods
     * overrides the {@code DefaultFileConfigurator.login} method. <br>
     * The (username, password) values are checked against the user_login table.
     * 
     * @param username
     *            the username sent by client side
     * @param password
     *            the user password sent by client side
     * @param iPAddress
     *            the client IP address value (For security tests).
     * 
     * @return true if access is granted, else false
     */
    @Override
    public boolean login(String username, char[] password, String iPAddress)
	    throws IOException, SQLException {
	Connection connection = null;

	try {
	    // Extract a Connection from our Pool
	    connection = this.getConnection();

	    // Compute the hash of the password
	    Sha1 sha1 = new Sha1();
	    String hashPassword = null;

	    try {
		hashPassword = sha1.getHexHash(new String(password).getBytes());
	    } catch (Exception e) {
		throw new IOException("Unexpected Sha1 failure", e);
	    }

	    // Check (username, password) existence in user_login table
	    String sql = "SELECT username FROM user_login "
		    + "WHERE username = ? AND hash_password = ?";
	    PreparedStatement prepStatement = connection.prepareStatement(sql);
	    prepStatement.setString(1, username);
	    prepStatement.setString(2, hashPassword);

	    ResultSet rs = prepStatement.executeQuery();

	    if (rs.next()) {
		// Yes! (username, password) are authenticated
		return true;
	    }

	    return false;
	} finally {
	    if (connection != null) {
		connection.close();
	    }
	}
    }

    /**
     * Implement this method to extract a Connection from your Connection pool
     * 
     * @return a connection extracted from the pool
     */
    private Connection getConnection() {
	Connection connection = null;

	// Implement connection set
	// connection = MyPool.getConnection;

	return connection;
    }

}
