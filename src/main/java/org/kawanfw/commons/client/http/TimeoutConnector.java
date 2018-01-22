/**
 * 
 */
package org.kawanfw.commons.client.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

/**
 * Allows to get the HttpUrlConnection output stream with a real timeout using a
 * thread and a timer.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class TimeoutConnector {

    private HttpURLConnection conn = null;

    /** The network output stream */
    private OutputStream os = null;

    /** boolean that says if we are connected to remote server */
    private boolean connected = false;

    /** Exception thrown by URL.connect() */
    private IOException exception = null;

    private int connectTimeout = 0;

    /**
     * Constructor.
     * 
     * @param conn
     *            the current connection
     * @param connectTimeout
     *            the connection timeout
     */
    public TimeoutConnector(HttpURLConnection conn, int connectTimeout) {
	this.conn = conn;
	this.connectTimeout = connectTimeout;
    }

    /**
     * Gets an output stream from the HttpUrlConnection in less than connectTimeout milliseconds, otherwise
     * throws a SocketTimeoutException
     * 
     * @return the HttpUrlConnection output stream 
     * @throws IOException
     * @throws SocketTimeoutException
     */
    public OutputStream getOutputStream() throws IOException,
	    SocketTimeoutException {
	
	os = null;
	connected = false;
	exception = null;

	Thread t = new Thread() {
	    public void run() {
		try {
		    os = conn.getOutputStream();
		    connected = true;
		} catch (IOException e) {
		    exception = e;
		}
	    }
	};

	t.start();

	long begin = System.currentTimeMillis();

	while (true) {

	    if (connected) {
		return os;
	    }

	    if (exception != null) {
		throw exception;
	    }

	    if (connectTimeout != 0) {
		long end = System.currentTimeMillis();
		if ((end - begin) > connectTimeout) {
		    throw new SocketTimeoutException(
			    "Unable to establish connection in less than required "
				    + connectTimeout + " milliseconds.");
		}
	    }

	    try {
		Thread.sleep(1); // Very, very short sleep...s
	    } catch (InterruptedException ie) {
		ie.printStackTrace();
	    }
	}
    }

}
