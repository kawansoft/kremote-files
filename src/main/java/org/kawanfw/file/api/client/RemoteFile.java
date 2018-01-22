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
package org.kawanfw.file.api.client;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
import org.kawanfw.commons.util.ClientLogger;
import org.kawanfw.commons.util.FrameworkDebug;
import org.kawanfw.file.api.client.exception.InvalidLoginException;
import org.kawanfw.file.api.client.exception.RemoteException;
import org.kawanfw.file.api.server.FileConfigurator;
import org.kawanfw.file.api.util.client.CallerFinder;
import org.kawanfw.file.api.util.client.RemoteFileUtil;

/**
 * An abstract representation of file and directory pathnames <i>on a remote
 * host</i>.
 * <p>
 * Methods have the same names, signatures and behaviors as {@link java.io.File}
 * methods: <br>
 * a {@code RemoteFile} method is a {@code File} method that is executed on the
 * remote host.
 * <p>
 * For the sake of clarity, we have thus kept unchanged the {@code File} Javadoc
 * of each described method, the few differences are detailed in this header.
 * <p>
 * The few differences with {@code java.io.File} are:
 * <ul>
 * <li>There is only one constructor that takes as parameter a
 * {@link RemoteSession} and a {@code pathname}. The {@code pathname} is defined
 * with a "/" separator on all platforms and must be absolute.</li>
 * <li>{@code File.compareTo(File)} and {@code File.renameTo(File)} methods take
 * a {@code RemoteFile} parameter in this class.</li>
 * <li>{@code File} methods that return {@code File} instance(s) return
 * {@code RemoteFile} instance(s) in this class.</li>
 * <li>The {@code File.toURI()} and {@code File.toURL()} methods are meaningless
 * in this context and are thus not ported.</li>
 * <li>The static {@code File.listRoots()} and {@code File.createTempFile()} are
 * not ported.</li>
 * <li>The Java 7+ {@code File.toPath()} method is not ported as this KRemote
 * Files version does not support remote {@code Path} objects.</li>
 * </ul>
 * <p>
 * Note that the <i>real</i> pathname used on host for {@code File} method
 * execution depends on the KRemote Files configuration of
 * {@link FileConfigurator#getHomeDir(String)}. This follow the same principle
 * of FTP server mechanisms.
 * <p>
 * Examples:
 * <p>
 * If {@code FileConfigurator.getHomeDir("mike")} returns {@code "/home/mike"}: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * {@code new RemoteFile(remoteSession, "/myfile.txt").exists()} <br>
 * will return the result of the execution on remote host of: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * {@code new File("/home/mike/myfile.txt").exists()}.
 * <p>
 * If {@code FileConfigurator.getHomeDir("mike")} returns {@code "/"}: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * {@code new RemoteFile(remoteSession, "/myfile.txt").exists()} <br>
 * will return the result of the execution on remote host of: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * {@code new File("/myfile.txt").exists()}.
 * 
 * <p>
 * See User Documentation for more info.
 * <p>
 * When using {@code FilenameFilter} and {@code FileFilter} filters, the filter
 * implementation must follow these rules:
 * <ul>
 * <li>The filter must implement {@code Serializable}.</li>
 * <li>Thus, the filter class must already exist on the server side.</li>
 * <li>When using anonymous inner class for {@code FilenameFilter} and
 * {@code FileFilter}: it must be public static.</li>
 * </ul>
 * <p>
 * Note that <a href="http://commons.apache.org/proper/commons-io/">Apache
 * Commons IO 2.5</a> are included in this software and the <a href=
 * "http://commons.apache.org/proper/commons-io/javadocs/api-release/org/apache/commons/io/filefilter/package-summary.html"
 * >org.apache.commons.io.filefilter package</a> contains many built-in classes
 * to be used directly. See code example below.
 * <p>
 * {@code RemoteFile} methods throw following Exceptions that are wrapped by a
 * {@code RuntimeException}:
 * <ul>
 * <li>{@code InvalidLoginException}: the session has been closed by a
 * {@code RemoteSession.logoff()}.</li>
 * <li>{@code UnknownHostException}: if host URL (http://www.acme.org) does not
 * exists or no Internet connection.</li>
 * <li>{@code ConnectException}: if the Host is correct but the
 * ServerFileManager Servlet is not reachable
 * (http://www.acme.org/ServerFileManager) and access failed with a status != OK
 * (200). (If the host is incorrect, or is impossible to connect to - Tomcat
 * down - the {@code ConnectException} will be the sub exception
 * {@code HttpHostConnectException}.)</li>
 * <li>{@code SocketException}: if network failure during transmission.</li>
 * <li>{@link RemoteException}: an exception has been thrown on the server
 * side.</li>
 * </ul>
 * <br>
 * Note: the user rights are the rights of the servlet container when accessing
 * remote files.
 * <h3>Example of RemoteFile usage</h3> <blockquote>
 * 
 * <pre>
 * // Define URL of the path to the ServerFileManager servlet
 * String url = &quot;https://www.acme.org/ServerFileManager&quot;;
 * 
 * // The login info for strong authentication on server side
 * String username = &quot;myUsername&quot;;
 * char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };
 * 
 * // Establish a session with the remote server
 * RemoteSession remoteSession = new RemoteSession(url, username, password);
 * 
 * // Create a new RemoteFile that maps a file on remote server
 * RemoteFile remoteFile = new RemoteFile(remoteSession, &quot;/Koala.jpg&quot;);
 * 
 * // We can use all the familiar java.io.File methods on our RemoteFile
 * if (remoteFile.exists()) {
 *     System.out.println(
 * 	    remoteFile.getName() + &quot; length  : &quot; + remoteFile.length());
 *     System.out.println(
 * 	    remoteFile.getName() + &quot; canWrite: &quot; + remoteFile.canWrite());
 * }
 * 
 * // List files on our remote root directory
 * remoteFile = new RemoteFile(remoteSession, &quot;/&quot;);
 * 
 * RemoteFile[] files = remoteFile.listFiles();
 * for (RemoteFile file : files) {
 *     System.out.println(&quot;Remote file: &quot; + file);
 * }
 * 
 * // List all text files in out root directory
 * // using an Apache Commons IO 2.5 FileFiter
 * FileFilter fileFilter = new SuffixFileFilter(&quot;.txt&quot;);
 * 
 * files = remoteFile.listFiles(fileFilter);
 * for (RemoteFile file : files) {
 *     System.out.println(&quot;Remote text file: &quot; + file);
 * }
 * 
 * // Etc.
 * </pre>
 * 
 * </blockquote>
 * 
 * @see org.kawanfw.file.api.client.RemoteInputStream
 * @see org.kawanfw.file.api.client.RemoteOutputStream
 * 
 * @author Nicolas de Pomereu
 * @since 1.0
 */

public class RemoteFile {

    private static boolean DEBUG = FrameworkDebug.isSet(RemoteFile.class);

    private RemoteSession remoteSession = null;
    private String pathname = null;

    /** Delegate to execute all methods */
    private RemoteFileListExecutor remoteFileExecutor = null;

    /**
     * Constructor
     * 
     * @param remoteSession
     *            the remote session
     * @param pathname
     *            the pathname on host with "/" as file separator. Must be
     *            absolute.
     * 
     * @throws InvalidLoginException
     *             if sessions is closed
     */
    public RemoteFile(RemoteSession remoteSession, String pathname)
	    throws InvalidLoginException {

	if (remoteSession == null) {
	    throw new IllegalArgumentException("remoteSession is null!");
	}

	if (remoteSession.getUsername() == null
		|| remoteSession.getAuthenticationToken() == null) {
	    throw new InvalidLoginException(
		    RemoteSession.REMOTE_SESSION_IS_CLOSED);
	}

	if (pathname == null) {
	    throw new IllegalArgumentException("pathname is null!");
	}

	if (!pathname.startsWith("/")) {
	    throw new IllegalArgumentException(
		    "pathname must be asbsolute and start with \"/\"");
	}

	// Remove last unecessary /
	if (pathname.length() > 1 && pathname.endsWith("/")) {
	    pathname = StringUtils.substringBeforeLast(pathname, "/");
	}

	debug("pathname: " + pathname);
	this.remoteSession = remoteSession;
	this.pathname = pathname;
	this.remoteFileExecutor = new RemoteFileListExecutor(this);
    }

    /**
     * Returns the {@code RemoteSession} of this {@code RemoteFile}
     * 
     * @return the {@code RemoteSession} of this {@code RemoteFile}
     */
    public RemoteSession getRemoteSession() {
	return remoteSession;
    }

    private void debug(String s) {
	if (DEBUG) {
	    ClientLogger.getLogger().log(Level.WARNING, s);
	}
    }

    /**
     * Tests whether the application can execute the file denoted by this
     * abstract pathname.
     * 
     * @return <code>true</code> if and only if the abstract pathname exists
     *         <em>and</em> the application is allowed to execute the file
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkExec(java.lang.String)}</code> method
     *             denies execute access to the file
     */
    public boolean canExecute() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "canExecute");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether the application can read the file denoted by this abstract
     * pathname.
     * 
     * @return <code>true</code> if and only if the file specified by this
     *         abstract pathname exists <em>and</em> can be read by the
     *         application; <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file
     */
    public boolean canRead() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "canRead");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether the application can modify the file denoted by this
     * abstract pathname.
     * 
     * @return <code>true</code> if and only if the file system actually
     *         contains a file denoted by this abstract pathname <em>and</em>
     *         the application is allowed to write to the file;
     *         <code>false</code> otherwise.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             denies write access to the file
     */
    public boolean canWrite() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "canWrite");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Compares two abstract pathnames lexicographically. The ordering defined
     * by this method depends upon the underlying system. On UNIX systems,
     * alphabetic case is significant in comparing pathnames; on Microsoft
     * Windows systems it is not.
     * 
     * @param pathname
     *            The abstract pathname to be compared to this abstract pathname
     * @return Zero if the argument is equal to this abstract pathname, a value
     *         less than zero if this abstract pathname is lexicographically
     *         less than the argument, or a value greater than zero if this
     *         abstract pathname is lexicographically greater than the argument
     */
    public int compareTo(RemoteFile pathname) {
	try {

	    if (pathname == null) {
		throw new NullPointerException();
	    }

	    // TestReload if we have the same RemoteSession parameters (aka same
	    // host and username)
	    if (!this.remoteSession.equals(pathname.getRemoteSession())) {
		throw new IllegalArgumentException(
			"Remote files must have identical RemoteSession (url, username) parameters.");
	    }

	    // We will transport a File because server does know what is a
	    // RemoteFile
	    // File file = new File(pathname.toString());

	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "compareTo", pathname);
	    return Integer.parseInt(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (NullPointerException e) {
	    throw e;
	} catch (IllegalArgumentException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Atomically creates a new, empty file named by this abstract pathname if
     * and only if a file with this name does not yet exist. The check for the
     * existence of the file and the creation of the file if it does not exist
     * are a single operation that is atomic with respect to all other
     * filesystem activities that might affect the file.
     * <P>
     * Note: this method should <i>not</i> be used for file-locking, as the
     * resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock} facility should be used
     * instead.
     * 
     * @return <code>true</code> if the named file does not exist and was
     *         successfully created; <code>false</code> if the named file
     *         already exists
     * @throws IOException
     *             If an I/O error occurred
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkWrite(java.lang.String)}
     *             </code> method denies write access to the file
     */
    public boolean createNewFile() throws IOException {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "createNewFile");
	    return Boolean.parseBoolean(result);
	} catch (RemoteException remoteexception) {
	    Throwable cause = remoteexception.getCause();
	    if (cause != null && cause instanceof IOException) {
		throw new IOException(remoteexception.getMessage());
	    } else {
		throw remoteexception;
	    }
	} catch (IOException e) {
	    throw e;
	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname. If this
     * pathname denotes a directory, then the directory must be empty in order
     * to be deleted.
     * 
     * @return <code>true</code> if and only if the file or directory is
     *         successfully deleted; <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkDelete}</code> method denies delete
     *             access to the file
     */
    public boolean delete() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "delete");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Requests that the file or directory denoted by this abstract pathname be
     * deleted when the virtual machine terminates. Files (or directories) are
     * deleted in the reverse order that they are registered. Invoking this
     * method to delete a file or directory that is already registered for
     * deletion has no effect. Deletion will be attempted only for normal
     * termination of the virtual machine, as defined by the Java Language
     * Specification.
     * 
     * <p>
     * Once deletion has been requested, it is not possible to cancel the
     * request. This method should therefore be used with care.
     * 
     * <P>
     * Note: this method should <i>not</i> be used for file-locking, as the
     * resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock} facility should be used
     * instead.
     * 
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkDelete}</code> method
     *             denies delete access to the file
     */
    public void deleteOnExit() {
	try {
	    remoteFileExecutor.fileMethodOneReturn(this.pathname,
		    "deleteOnExit");
	    return;

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests this abstract pathname for equality with the given object. Returns
     * <code>true</code> if and only if the argument is not <code>null</code>
     * and is an abstract pathname that denotes the same file or directory as
     * this abstract pathname. Whether or not two abstract pathnames are equal
     * depends upon the underlying system. On UNIX systems, alphabetic case is
     * significant in comparing pathnames; on Microsoft Windows systems it is
     * not.
     * 
     * @param obj
     *            The object to be compared with this abstract pathname
     * @return <code>true</code> if and only if the objects are the same;
     *         <code>false</code> otherwise
     */
    public boolean equals(Object obj) {
	try {

	    if (obj == null) {
		return false;
	    }

	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "equals", obj);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether the file or directory denoted by this abstract pathname
     * exists.
     * 
     * @return <code>true</code> if and only if the file or directory denoted by
     *         this abstract pathname exists; <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file or directory
     */
    public boolean exists() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "exists");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the absolute form of this abstract pathname. Equivalent to
     * <code>new&nbsp;File(this.{@link #getAbsolutePath})</code>.
     * 
     * @return The absolute abstract pathname denoting the same file or
     *         directory as this abstract pathname
     * @throws SecurityException
     *             If a required system property value cannot be accessed.
     */
    public RemoteFile getAbsoluteFile() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getAbsoluteFile");
	    return new RemoteFile(remoteSession, result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the absolute pathname string of this abstract pathname.
     * 
     * <p>
     * If this abstract pathname is already absolute, then the pathname string
     * is simply returned as if by the <code>{@link #getPath}</code> method. If
     * this abstract pathname is the empty abstract pathname then the pathname
     * string of the current user directory, which is named by the system
     * property <code>user.dir</code>, is returned. Otherwise this pathname is
     * resolved in a system-dependent way. On UNIX systems, a relative pathname
     * is made absolute by resolving it against the current user directory. On
     * Microsoft Windows systems, a relative pathname is made absolute by
     * resolving it against the current directory of the drive named by the
     * pathname, if any; if not, it is resolved against the current user
     * directory.
     * 
     * @return The absolute pathname string denoting the same file or directory
     *         as this abstract pathname
     * @throws SecurityException
     *             If a required system property value cannot be accessed.
     */
    public String getAbsolutePath() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getAbsolutePath");
	    return result;

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the canonical form of this abstract pathname. Equivalent to
     * <code>new&nbsp;File(this.{@link #getCanonicalPath})</code>.
     * 
     * @return The canonical pathname string denoting the same file or directory
     *         as this abstract pathname
     * @throws IOException
     *             If an I/O error occurs, which is possible because the
     *             construction of the canonical pathname may require filesystem
     *             queries
     * @throws SecurityException
     *             If a required system property value cannot be accessed, or if
     *             a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead}</code> method denies read access
     *             to the file
     */
    public RemoteFile getCanonicalFile() throws IOException {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getCanonicalFile");
	    return new RemoteFile(remoteSession, result);
	} catch (RemoteException remoteexception) {
	    Throwable cause = remoteexception.getCause();
	    if (cause != null && cause instanceof IOException) {
		throw new IOException(remoteexception.getMessage());
	    } else {
		throw remoteexception;
	    }
	} catch (IOException e) {
	    throw e;
	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the canonical pathname string of this abstract pathname.
     * 
     * <p>
     * A canonical pathname is both absolute and unique. The precise definition
     * of canonical form is system-dependent. This method first converts this
     * pathname to absolute form if necessary, as if by invoking the
     * {@link #getAbsolutePath} method, and then maps it to its unique form in a
     * system-dependent way. This typically involves removing redundant names
     * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
     * symbolic links (on UNIX platforms), and converting drive letters to a
     * standard case (on Microsoft Windows platforms).
     * 
     * <p>
     * Every pathname that denotes an existing file or directory has a unique
     * canonical form. Every pathname that denotes a nonexistent file or
     * directory also has a unique canonical form. The canonical form of the
     * pathname of a nonexistent file or directory may be different from the
     * canonical form of the same pathname after the file or directory is
     * created. Similarly, the canonical form of the pathname of an existing
     * file or directory may be different from the canonical form of the same
     * pathname after the file or directory is deleted.
     * 
     * @return The canonical pathname string denoting the same file or directory
     *         as this abstract pathname
     * @throws IOException
     *             If an I/O error occurs, which is possible because the
     *             construction of the canonical pathname may require filesystem
     *             queries
     * @throws SecurityException
     *             If a required system property value cannot be accessed, or if
     *             a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkRead}</code> method
     *             denies read access to the file
     */
    public String getCanonicalPath() throws IOException {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getCanonicalPath");
	    return result;
	} catch (RemoteException remoteexception) {
	    Throwable cause = remoteexception.getCause();
	    if (cause != null && cause instanceof IOException) {
		throw new IOException(remoteexception.getMessage());
	    } else {
		throw remoteexception;
	    }
	} catch (IOException e) {
	    throw e;
	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the number of unallocated bytes in the partition
     * <a href="#partName">named</a> by this abstract path name.
     * 
     * <p>
     * The returned number of unallocated bytes is a hint, but not a guarantee,
     * that it is possible to use most or any of these bytes. The number of
     * unallocated bytes is most likely to be accurate immediately after this
     * call. It is likely to be made inaccurate by any external I/O operations
     * including those made on the system outside of this virtual machine. This
     * method makes no guarantee that write operations to this file system will
     * succeed.
     * 
     * @return The number of unallocated bytes on the partition <tt>0L</tt> if
     *         the abstract pathname does not name a partition. This value will
     *         be less than or equal to the total file system size returned by
     *         {@link #getTotalSpace}.
     * @throws SecurityException
     *             If a security manager has been installed and it denies
     *             {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *             or its {@link SecurityManager#checkRead(String)} method
     *             denies read access to the file named by this abstract
     *             pathname
     */
    public long getFreeSpace() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getFreeSpace");
	    return Long.parseLong(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the name of the file or directory denoted by this abstract
     * pathname. This is just the last name in the pathname's name sequence. If
     * the pathname's name sequence is empty, then the empty string is returned.
     * 
     * @return The name of the file or directory denoted by this abstract
     *         pathname, or the empty string if this pathname's name sequence is
     *         empty
     */
    public String getName() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getName");
	    return result;

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the pathname string of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     * 
     * <p>
     * The <em>parent</em> of an abstract pathname consists of the pathname's
     * prefix, if any, and each name in the pathname's name sequence except for
     * the last. If the name sequence is empty then the pathname does not name a
     * parent directory.
     * 
     * @return The pathname string of the parent directory named by this
     *         abstract pathname, or <code>null</code> if this pathname does not
     *         name a parent
     */
    public String getParent() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getParent");
	    return result;

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the abstract pathname of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     * 
     * <p>
     * The <em>parent</em> of an abstract pathname consists of the pathname's
     * prefix, if any, and each name in the pathname's name sequence except for
     * the last. If the name sequence is empty then the pathname does not name a
     * parent directory.
     * 
     * @return The abstract pathname of the parent directory named by this
     *         abstract pathname, or <code>null</code> if this pathname does not
     *         name a parent
     */
    public RemoteFile getParentFile() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getParentFile");

	    if (result == null) {
		return null;
	    } else {
		return new RemoteFile(remoteSession, result);
	    }

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Converts this abstract pathname into a pathname string. The resulting
     * string uses the {@link File#separator default name-separator character}
     * to separate the names in the name sequence.
     * 
     * @return The string form of this abstract pathname
     */
    public String getPath() {
	return pathname;
    }

    /**
     * Returns the size of the partition <a href="#partName">named</a> by this
     * abstract pathname.
     * 
     * @return The size, in bytes, of the partition or <tt>0L</tt> if this
     *         abstract pathname does not name a partition
     * @throws SecurityException
     *             If a security manager has been installed and it denies
     *             {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *             or its {@link SecurityManager#checkRead(String)} method
     *             denies read access to the file named by this abstract
     *             pathname
     */
    public long getTotalSpace() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getTotalSpace");
	    return Long.parseLong(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the number of bytes available to this virtual machine on the
     * partition <a href="#partName">named</a> by this abstract pathname. When
     * possible, this method checks for write permissions and other operating
     * system restrictions and will therefore usually provide a more accurate
     * estimate of how much new data can actually be written than
     * {@link #getFreeSpace}.
     * 
     * <p>
     * The returned number of available bytes is a hint, but not a guarantee,
     * that it is possible to use most or any of these bytes. The number of
     * unallocated bytes is most likely to be accurate immediately after this
     * call. It is likely to be made inaccurate by any external I/O operations
     * including those made on the system outside of this virtual machine. This
     * method makes no guarantee that write operations to this file system will
     * succeed.
     * 
     * @return The number of available bytes on the partition or <tt>0L</tt> if
     *         the abstract pathname does not name a partition. On systems where
     *         this information is not available, this method will be equivalent
     *         to a call to {@link #getFreeSpace}.
     * @throws SecurityException
     *             If a security manager has been installed and it denies
     *             {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *             or its {@link SecurityManager#checkRead(String)} method
     *             denies read access to the file named by this abstract
     *             pathname
     */
    public long getUsableSpace() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "getUsableSpace");
	    return Long.parseLong(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Computes a hash code for this abstract pathname. Because equality of
     * abstract pathnames is inherently system-dependent, so is the computation
     * of their hash codes. On UNIX systems, the hash code of an abstract
     * pathname is equal to the exclusive <em>or</em> of the hash code of its
     * pathname string and the decimal value <code>1234321</code>. On Microsoft
     * Windows systems, the hash code is equal to the exclusive <em>or</em> of
     * the hash code of its pathname string converted to lower case and the
     * decimal value <code>1234321</code>. Locale is not taken into account on
     * lowercasing the pathname string.
     * 
     * @return A hash code for this abstract pathname
     */
    public int hashCode() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "hashCode");
	    return Integer.parseInt(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether this abstract pathname is absolute. The definition of
     * absolute pathname is system dependent. On UNIX systems, a pathname is
     * absolute if its prefix is <code>"/"</code>. On Microsoft Windows systems,
     * a pathname is absolute if its prefix is a drive specifier followed by
     * <code>"\\"</code>, or if its prefix is <code>"\\\\"</code>.
     * 
     * @return <code>true</code> if this abstract pathname is absolute,
     *         <code>false</code> otherwise
     */
    public boolean isAbsolute() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "isAbsolute");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a directory.
     * 
     * @return <code>true</code> if and only if the file denoted by this
     *         abstract pathname exists <em>and</em> is a directory;
     *         <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file
     */
    public boolean isDirectory() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "isDirectory");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a normal
     * file. A file is <em>normal</em> if it is not a directory and, in
     * addition, satisfies other system-dependent criteria. Any non-directory
     * file created by a Java application is guaranteed to be a normal file.
     * 
     * @return <code>true</code> if and only if the file denoted by this
     *         abstract pathname exists <em>and</em> is a normal file;
     *         <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file
     */
    public boolean isFile() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "isFile");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Tests whether the file named by this abstract pathname is a hidden file.
     * The exact definition of <em>hidden</em> is system-dependent. On UNIX
     * systems, a file is considered to be hidden if its name begins with a
     * period character (<code>'.'</code>). On Microsoft Windows systems, a file
     * is considered to be hidden if it has been marked as such in the
     * filesystem.
     * 
     * @return <code>true</code> if and only if the file denoted by this
     *         abstract pathname is hidden according to the conventions of the
     *         underlying platform
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file
     */
    public boolean isHidden() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "isHidden");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the time that the file denoted by this abstract pathname was last
     * modified.
     * 
     * @return A <code>long</code> value representing the time the file was last
     *         modified, measured in milliseconds since the epoch (00:00:00 GMT,
     *         January 1, 1970), or <code>0L</code> if the file does not exist
     *         or if an I/O error occurs
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file
     */
    public long lastModified() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "lastModified");
	    return Long.parseLong(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the length of the file denoted by this abstract pathname. The
     * return value is unspecified if this pathname denotes a directory.
     * 
     * @return The length, in bytes, of the file denoted by this abstract
     *         pathname, or <code>0L</code> if the file does not exist. Some
     *         operating systems may return <code>0L</code> for pathnames
     *         denoting system-dependent entities such as devices or pipes.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the file
     */
    public long length() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "length");
	    return Long.parseLong(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname.
     * 
     * <p>
     * If this abstract pathname does not denote a directory, then this method
     * returns <code>null</code>. Otherwise an array of strings is returned, one
     * for each file or directory in the directory. Names denoting the directory
     * itself and the directory's parent directory are not included in the
     * result. Each string is a file name rather than a complete path.
     * 
     * <p>
     * There is no guarantee that the name strings in the resulting array will
     * appear in any specific order; they are not, in particular, guaranteed to
     * appear in alphabetical order.
     * 
     * @return An array of strings naming the files and directories in the
     *         directory denoted by this abstract pathname. The array will be
     *         empty if the directory is empty. Returns <code>null</code> if
     *         this abstract pathname does not denote a directory, or if an I/O
     *         error occurs.
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkRead(java.lang.String)}
     *             </code> method denies read access to the directory
     */
    public String[] list() {
	try {
	    String[] filenames = remoteFileExecutor.list(this.pathname, null,
		    null);
	    return filenames;

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    if (e instanceof RemoteException) {
		Throwable cause = e.getCause();
		throw new RuntimeException(cause);
	    }
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname that satisfy the specified
     * filter. The behavior of this method is the same as that of the
     * <code>{@link #list()}</code> method, except that the strings in the
     * returned array must satisfy the filter. If the given <code>filter</code>
     * is <code>null</code> then all names are accepted. Otherwise, a name
     * satisfies the filter if and only if the value <code>true</code> results
     * when the <code>{@link
    FilenameFilter#accept}</code> method of the filter is invoked on this
     * abstract pathname and the name of a file or directory in the directory
     * that it denotes.
     * 
     * @param filter
     *            A filename filter
     * @return An array of strings naming the files and directories in the
     *         directory denoted by this abstract pathname that were accepted by
     *         the given <code>filter</code>. The array will be empty if the
     *         directory is empty or if no names were accepted by the filter.
     *         Returns <code>null</code> if this abstract pathname does not
     *         denote a directory, or if an I/O error occurs.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the directory
     */
    public String[] list(FilenameFilter filter) {

	String[] filenames = null;

	try {

	    String callerClassName = CallerFinder.getCallerCallerClassName();

	    filenames = remoteFileExecutor.list(this.pathname, filter,
		    callerClassName);

	} catch (SecurityException e) {
	    throw e;
	} catch (Throwable e) {
	    RemoteFileUtil.decodeTrowableForFilterUsage(e);
	}

	return filenames;
    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname.
     * 
     * <p>
     * If this abstract pathname does not denote a directory, then this method
     * returns <code>null</code>. Otherwise an array of <code>File</code>
     * objects is returned, one for each file or directory in the directory.
     * Pathnames denoting the directory itself and the directory's parent
     * directory are not included in the result. Each resulting abstract
     * pathname is constructed from this abstract pathname using the
     * <code>{@link File#File(java.io.File, java.lang.String)
      File(File,&nbsp;String)}</code> constructor. Therefore if this pathname is
     * absolute then each resulting pathname is absolute; if this pathname is
     * relative then each resulting pathname will be relative to the same
     * directory.
     * 
     * <p>
     * There is no guarantee that the name strings in the resulting array will
     * appear in any specific order; they are not, in particular, guaranteed to
     * appear in alphabetical order.
     * 
     * @return An array of abstract pathnames denoting the files and directories
     *         in the directory denoted by this abstract pathname. The array
     *         will be empty if the directory is empty. Returns
     *         <code>null</code> if this abstract pathname does not denote a
     *         directory, or if an I/O error occurs.
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkRead(java.lang.String)}
     *             </code> method denies read access to the directory
     */
    public RemoteFile[] listFiles() {
	try {
	    RemoteFile[] remoteFiles = remoteFileExecutor
		    .listFiles(this.pathname, null, null, null);
	    return remoteFiles;

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {

	    if (e instanceof RemoteException) {
		Throwable cause = e.getCause();
		throw new RuntimeException(cause);
	    }
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns an array of abstract pathnames denoting the files and directories
     * in the directory denoted by this abstract pathname that satisfy the
     * specified filter. The behavior of this method is the same as that of the
     * <code>{@link #listFiles()}</code> method, except that the pathnames in
     * the returned array must satisfy the filter. If the given
     * <code>filter</code> is <code>null</code> then all pathnames are accepted.
     * Otherwise, a pathname satisfies the filter if and only if the value
     * <code>true</code> results when the
     * <code>{@link FileFilter#accept(java.io.File)}</code> method of the filter
     * is invoked on the pathname.
     * 
     * @param filter
     *            A file filter
     * @return An array of abstract pathnames denoting the files and directories
     *         in the directory denoted by this abstract pathname. The array
     *         will be empty if the directory is empty. Returns
     *         <code>null</code> if this abstract pathname does not denote a
     *         directory, or if an I/O error occurs.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the directory
     */
    public RemoteFile[] listFiles(FilenameFilter filter) {

	RemoteFile[] remoteFiles = null;

	try {

	    String callerClassName = CallerFinder.getCallerCallerClassName();

	    remoteFiles = remoteFileExecutor.listFiles(this.pathname, filter,
		    null, callerClassName);

	} catch (SecurityException e) {
	    throw e;
	} catch (Throwable e) {
	    RemoteFileUtil.decodeTrowableForFilterUsage(e);
	}

	return remoteFiles;
    }

    /**
     * Returns an array of abstract pathnames denoting the files and directories
     * in the directory denoted by this abstract pathname that satisfy the
     * specified filter. The behavior of this method is the same as that of the
     * <code>{@link #listFiles()}</code> method, except that the pathnames in
     * the returned array must satisfy the filter. If the given
     * <code>filter</code> is <code>null</code> then all pathnames are accepted.
     * Otherwise, a pathname satisfies the filter if and only if the value
     * <code>true</code> results when the
     * <code>{@link FileFilter#accept(java.io.File)}</code> method of the filter
     * is invoked on the pathname.
     * 
     * @param filter
     *            A file filter
     * @return An array of abstract pathnames denoting the files and directories
     *         in the directory denoted by this abstract pathname. The array
     *         will be empty if the directory is empty. Returns
     *         <code>null</code> if this abstract pathname does not denote a
     *         directory, or if an I/O error occurs.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             denies read access to the directory
     */
    public RemoteFile[] listFiles(FileFilter filter) {

	RemoteFile[] remoteFiles = null;

	try {

	    String callerClassName = CallerFinder.getCallerCallerClassName();

	    remoteFiles = remoteFileExecutor.listFiles(this.pathname, null,
		    filter, callerClassName);

	} catch (SecurityException e) {
	    throw e;
	} catch (Throwable e) {
	    RemoteFileUtil.decodeTrowableForFilterUsage(e);
	}

	return remoteFiles;
    }

    /**
     * Creates the directory named by this abstract pathname.
     * 
     * @return <code>true</code> if and only if the directory was created;
     *         <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             does not permit the named directory to be created
     */
    public boolean mkdir() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "mkdir");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Creates the directory named by this abstract pathname, including any
     * necessary but nonexistent parent directories. Note that if this operation
     * fails it may have succeeded in creating some of the necessary parent
     * directories.
     * 
     * @return <code>true</code> if and only if the directory was created, along
     *         with all necessary parent directories; <code>false</code>
     *         otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkRead(java.lang.String)}</code> method
     *             does not permit verification of the existence of the named
     *             directory and all necessary parent directories; or if the
     *             <code>{@link 
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             does not permit the named directory and all necessary parent
     *             directories to be created
     */
    public boolean mkdirs() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "mkdirs");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Renames the file denoted by this abstract pathname.
     * 
     * <p>
     * Many aspects of the behavior of this method are inherently
     * platform-dependent: The rename operation might not be able to move a file
     * from one filesystem to another, it might not be atomic, and it might not
     * succeed if a file with the destination abstract pathname already exists.
     * The return value should always be checked to make sure that the rename
     * operation was successful.
     * 
     * @param dest
     *            The new abstract pathname for the named file
     * @return <code>true</code> if and only if the renaming succeeded;
     *         <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkWrite(java.lang.String)}
     *             </code> method denies write access to either the old or new
     *             pathnames
     * @throws NullPointerException
     *             If parameter <code>dest</code> is <code>null</code>
     */
    public boolean renameTo(RemoteFile dest) {
	try {

	    if (dest == null) {
		throw new NullPointerException();
	    }

	    // TestReload if we have the same RemoteSession parameters (aka same
	    // host and username)
	    if (!this.remoteSession.equals(dest.getRemoteSession())) {
		throw new IllegalArgumentException(
			"Remote files must have identical RemoteSession (url, username) parameters.");
	    }

	    // We will transport a File because server does know what is a
	    // RemoteFile
	    // File file = new File(dest.toString());

	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "renameTo", dest);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (NullPointerException e) {
	    throw e;
	} catch (IllegalArgumentException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * A convenience method to set the owner's execute permission for this
     * abstract pathname.
     * 
     * <p>
     * An invocation of this method of the form <tt>file.setExcutable(arg)</tt>
     * behaves in exactly the same way as the invocation
     * 
     * <blockquote>
     * 
     * <pre>
     * file.setExecutable(arg, true)
     * </pre>
     * 
     * </blockquote>
     * 
     * @param executable
     *            If <code>true</code>, sets the access permission to allow
     *            execute operations; if <code>false</code> to disallow execute
     *            operations
     * @return <code>true</code> if and only if the operation succeeded. The
     *         operation will fail if the user does not have permission to
     *         change the access permissions of this abstract pathname. If
     *         <code>executable</code> is <code>false</code> and the underlying
     *         file system does not implement an excute permission, then the
     *         operation will fail.
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkWrite(java.lang.String)}
     *             </code> method denies write access to the file
     */
    public boolean setExecutable(boolean executable) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setExecutable", executable);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Sets the owner's or everybody's execute permission for this abstract
     * pathname.
     * 
     * @param executable
     *            If <code>true</code>, sets the access permission to allow
     *            execute operations; if <code>false</code> to disallow execute
     *            operations
     * @param ownerOnly
     *            If <code>true</code>, the execute permission applies only to
     *            the owner's execute permission; otherwise, it applies to
     *            everybody. If the underlying file system can not distinguish
     *            the owner's execute permission from that of others, then the
     *            permission will apply to everybody, regardless of this value.
     * @return <code>true</code> if and only if the operation succeeded. The
     *         operation will fail if the user does not have permission to
     *         change the access permissions of this abstract pathname. If
     *         <code>executable</code> is <code>false</code> and the underlying
     *         file system does not implement an execute permission, then the
     *         operation will fail.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             denies write access to the file
     */
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setExecutable", executable, ownerOnly);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Sets the last-modified time of the file or directory named by this
     * abstract pathname.
     * 
     * <p>
     * All platforms support file-modification times to the nearest second, but
     * some provide more precision. The argument will be truncated to fit the
     * supported precision. If the operation succeeds and no intervening
     * operations on the file take place, then the next invocation of the
     * <code>{@link #lastModified}</code> method will return the (possibly
     * truncated) <code>time</code> argument that was passed to this method.
     * 
     * @param time
     *            The new last-modified time, measured in milliseconds since the
     *            epoch (00:00:00 GMT, January 1, 1970)
     * @return <code>true</code> if and only if the operation succeeded;
     *         <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             If the argument is negative
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkWrite(java.lang.String)}
     *             </code> method denies write access to the named file
     */
    public boolean setLastModified(long time) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setLastModified", time);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Marks the file or directory named by this abstract pathname so that only
     * read operations are allowed. After invoking this method the file or
     * directory is guaranteed not to change until it is either deleted or
     * marked to allow write access. Whether or not a read-only file or
     * directory may be deleted depends upon the underlying system.
     * 
     * @return <code>true</code> if and only if the operation succeeded;
     *         <code>false</code> otherwise
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             denies write access to the named file
     */
    public boolean setReadOnly() {
	try {
	    String result = remoteFileExecutor
		    .fileMethodOneReturn(this.pathname, "setReadOnly");
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * A convenience method to set the owner's read permission for this abstract
     * pathname.
     * 
     * <p>
     * An invocation of this method of the form <tt>file.setReadable(arg)</tt>
     * behaves in exactly the same way as the invocation
     * 
     * <blockquote>
     * 
     * <pre>
     * file.setReadable(arg, true)
     * </pre>
     * 
     * </blockquote>
     * 
     * @param readable
     *            If <code>true</code>, sets the access permission to allow read
     *            operations; if <code>false</code> to disallow read operations
     * @return <code>true</code> if and only if the operation succeeded. The
     *         operation will fail if the user does not have permission to
     *         change the access permissions of this abstract pathname. If
     *         <code>readable</code> is <code>false</code> and the underlying
     *         file system does not implement a read permission, then the
     *         operation will fail.
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkWrite(java.lang.String)}
     *             </code> method denies write access to the file
     */
    public boolean setReadable(boolean readable) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setReadable", readable);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Sets the owner's or everybody's read permission for this abstract
     * pathname.
     * 
     * @param readable
     *            If <code>true</code>, sets the access permission to allow read
     *            operations; if <code>false</code> to disallow read operations
     * @param ownerOnly
     *            If <code>true</code>, the read permission applies only to the
     *            owner's read permission; otherwise, it applies to everybody.
     *            If the underlying file system can not distinguish the owner's
     *            read permission from that of others, then the permission will
     *            apply to everybody, regardless of this value.
     * @return <code>true</code> if and only if the operation succeeded. The
     *         operation will fail if the user does not have permission to
     *         change the access permissions of this abstract pathname. If
     *         <code>readable</code> is <code>false</code> and the underlying
     *         file system does not implement a read permission, then the
     *         operation will fail.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             denies write access to the file
     */
    public boolean setReadable(boolean readable, boolean ownerOnly) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setReadable", readable, ownerOnly);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * A convenience method to set the owner's write permission for this
     * abstract pathname.
     * 
     * <p>
     * An invocation of this method of the form <tt>file.setWritable(arg)</tt>
     * behaves in exactly the same way as the invocation
     * 
     * <blockquote>
     * 
     * <pre>
     * file.setWritable(arg, true)
     * </pre>
     * 
     * </blockquote>
     * 
     * @param writable
     *            If <code>true</code>, sets the access permission to allow
     *            write operations; if <code>false</code> to disallow write
     *            operations
     * @return <code>true</code> if and only if the operation succeeded. The
     *         operation will fail if the user does not have permission to
     *         change the access permissions of this abstract pathname.
     * @throws SecurityException
     *             If a security manager exists and its <code>
     *             {@link java.lang.SecurityManager#checkWrite(java.lang.String)}
     *             </code> method denies write access to the file
     */
    public boolean setWritable(boolean writable) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setWritable", writable);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Sets the owner's or everybody's write permission for this abstract
     * pathname.
     * 
     * @param writable
     *            If <code>true</code>, sets the access permission to allow
     *            write operations; if <code>false</code> to disallow write
     *            operations
     * @param ownerOnly
     *            If <code>true</code>, the write permission applies only to the
     *            owner's write permission; otherwise, it applies to everybody.
     *            If the underlying file system can not distinguish the owner's
     *            write permission from that of others, then the permission will
     *            apply to everybody, regardless of this value.
     * @return <code>true</code> if and only if the operation succeeded. The
     *         operation will fail if the user does not have permission to
     *         change the access permissions of this abstract pathname.
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
          java.lang.SecurityManager#checkWrite(java.lang.String)}</code> method
     *             denies write access to the named file
     */
    public boolean setWritable(boolean writable, boolean ownerOnly) {
	try {
	    String result = remoteFileExecutor.fileMethodOneReturn(
		    this.pathname, "setWritable", writable, ownerOnly);
	    return Boolean.parseBoolean(result);

	} catch (SecurityException e) {
	    throw e;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Returns the pathname string of this abstract pathname. This is just the
     * string returned by the <code>{@link #getPath}</code> method.
     * 
     * @return The string form of this abstract pathname
     */
    public String toString() {
	return getPath();
    }

    // /**
    // * Constructs a <tt>file:</tt> URI that represents this abstract pathname.
    // *
    // * <p>
    // * The exact form of the URI is system-dependent. If it can be determined
    // * that the file denoted by this abstract pathname is a directory, then
    // the
    // * resulting URI will end with a slash.
    // *
    // * <p>
    // * For a given abstract pathname <i>f</i>, it is guaranteed that
    // *
    // * <tt>
    // new {@link #File(java.net.URI) File}(</tt><i>&nbsp;f</i>
    // * <tt>.toURI()).equals(</tt><i>&nbsp;f</i>
    // * <tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
    // </tt>
    // *
    // * so long as the original abstract pathname, the URI, and the new
    // abstract
    // * pathname are all created in (possibly different invocations of) the
    // same
    // * Java virtual machine. Due to the system-dependent nature of abstract
    // * pathnames, however, this relationship typically does not hold when a
    // * <tt>file:</tt> URI that is created in a virtual machine on one
    // operating
    // * system is converted into an abstract pathname in a virtual machine on a
    // * different operating system.
    // *
    // * @return An absolute, hierarchical URI with a scheme equal to
    // * <tt>"file"</tt>, a path representing this abstract pathname, and
    // * undefined authority, query, and fragment components
    // * @throws SecurityException
    // * If a required system property value cannot be accessed.
    // */
    // public URI toURI() {
    // throw new UnsupportedOperationException(Tag.PRODUCT
    // + "Method not implemented.");
    // // try {
    // // String result = remoteFileExecutor.fileMethodOneReturn(this.pathname,
    // // "toURI");
    // // return new URI(result);
    // //
    // // } catch (SecurityException e) {
    // // throw e;
    // // } catch (Exception e) {
    // // throw new RuntimeException(e);
    // // }
    // }
    //
    // /**
    // * Converts this abstract pathname into a <code>file:</code> URL. The
    // exact
    // * form of the URL is system-dependent. If it can be determined that the
    // * file denoted by this abstract pathname is a directory, then the
    // resulting
    // * URL will end with a slash.
    // *
    // * @return A URL object representing the equivalent file URL
    // * @throws MalformedURLException
    // * If the path cannot be parsed as a URL
    // */
    // public URL toURL() throws MalformedURLException {
    // throw new UnsupportedOperationException(Tag.PRODUCT
    // + "Method not implemented.");
    // // try {
    // // String result = remoteFileExecutor.fileMethodOneReturn(this.pathname,
    // // "toURL");
    // // return new URL(result);
    // // } catch (MalformedURLException malformedurlexception) {
    // // throw malformedurlexception;
    // // } catch (SecurityException e) {
    // // throw e;
    // // } catch (Exception e) {
    // // throw new RuntimeException(e);
    // // }
    // }

}
