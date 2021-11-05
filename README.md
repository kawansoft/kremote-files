
# KRemote Files v1.0 - User Guide
<img src="https://docs.aceql.com/favicon.png" alt="AceQ HTTP Icon"/>

   * [Fundamentals](#fundamentals)
      * [What is KRemote Files?](#what-is-kremote-files)
         * [Accessing remote files](#accessing-remote-files)
         * [Files upload and download](#files-upload-and-download)
         * [Secure RPC calls](#secure-rpc-calls)
      * [Architecture](#architecture)
      * [Features](#features)
      * [Requirements](#requirements)
      * [License](#license)
   * [Installation](#installation)
      * [Server installation](#server-installation)
      * [Client installation (including Android)](#client-installation-including-android)
         * [Android Project settings](#android-project-settings)
   * [Configuration on the server side](#configuration-on-the-server-side)
      * [The Server File Manager servlet](#the-server-file-manager-servlet)
         * [Configure the webapp web.xml](#configure-the-webapp-webxml)
      * [Configurators fundamentals](#configurators-fundamentals)
      * [FileConfigurator interface](#fileconfigurator-interface)
      * [Coding a FileConfigurator](#coding-a-fileconfigurator)
         * [Login method: authenticating client username and password](#login-method-authenticating-client-username-and-password)
         * [getHomeDir method:  defining the user files locations](#gethomedir-method--defining-the-user-files-locations)
         * [getLogger method: defining where server Exceptions are stored](#getlogger-method-defining-where-server-exceptions-are-stored)
      * [Passing concrete Configurator classes](#passing-concrete-configurator-classes)
         * [The jwt_secret_value init parameter](#the-jwt_secret_value-init-parameter)
      * [RPC rules for calling Java methods from the client side](#rpc-rules-for-calling-java-methods-from-the-client-side)
      * [Testing you server configuration](#testing-you-server-configuration)
   * [Usage: client site programming](#usage-client-site-programming)
      * [The RemoteSession class](#the-remotesession-class)
         * [Session creation and authentication](#session-creation-and-authentication)
         * [Defining a proxy](#defining-a-proxy)
         * [Handling Exceptions thrown by RemoteSession](#handling-exceptions-thrown-by-remotesession)
         * [RPC: Calling Java methods on the KRemote Files Server](#rpc-calling-java-methods-on-the-kremote-files-server)
      * [The RemoteFile class](#the-remotefile-class)
         * [Using FilenameFilter and FileFilter](#using-filenamefilter-and-filefilter)
         * [Handling Exceptions thrown by RemoteFile methods](#handling-exceptions-thrown-by-remotefile-methods)
         * [Example of RemoteFile usage](#example-of-remotefile-usage)
      * [The RemoteInputStream and RemoteOutputStream  classes](#the-remoteinputstream-and-remoteoutputstream--classes)
      * [Using Progress Bars with file upload and download](#using-progress-bars-with-file-upload-and-download)
   * [Advanced Usage](#advanced-usage)
      * [Session security](#session-security)
         * [The DefaultJwtSessionConfigurator class](#the-defaultjwtsessionconfigurator-class)
         * [Creating your own session management](#creating-your-own-session-management)
      * [Data transport](#data-transport)
         * [Transport format](#transport-format)
         * [Content Streaming &amp; memory management](#content-streaming--memory-management)
         * [Chunked upload of large files](#chunked-upload-of-large-files)
         * [Chunked download of large files](#chunked-download-of-large-files)
         * [File chunking and stateful or stateless architecture](#file-chunking-and-stateful-or-stateless-architecture)
         * [Large file upload &amp; download recovery](#large-file-upload--download-recovery)
      * [Using multiple RemoteSession in Threads](#using-multiple-remotesession-in-threads)
      * [Using RemoteSession to different KRemote Files Servers](#using-remotesession-to-different-kremote-files-servers)
      * [Session parameters](#session-parameters)
      * [Managing temporary files](#managing-temporary-files)


# Fundamentals

## What is KRemote Files?

KRemote Files is a secure Open Source client/server framework that allows to program very easily remote file access, file uploads/downloads and RPC through HTTP.  File transfers include powerful features like file chunking and automatic recovery mechanism.  KRemote Files allows 3 types of operation over HTTP:

1. Accessing remote files with `java.io.File` syntax.
2. Files upload and download using `java.io.OutputStream` and `java.io.InputStream` syntax.
3. Secure RPC calls.

### Accessing remote files

We first create a `RemoteSession` that establishes a link with the KRremote Files server:

```Java
// Parameters for connection to our remote server
String url = "https://www.acme.com/ServerFileManager";
String username = "username";
char[] password = { 'd', 'e', 'm', 'o' };

// Establish a session with the remote server
RemoteSession remoteSession = new RemoteSession(url, username,
	password);
```

We can now create a `RemoteFile` and use it as if it were a `java.io.File`:

```java
// Display info on our remote /mydir/Koala.jpg
RemoteFile koala = new RemoteFile(remoteSession, "/mydir/Koala.jpg");

System.out.println("Display remote " + koala + " info...");
System.out.println("last modified: " + new Date(koala.lastModified()));
System.out.println("length       : " + koala.length());
System.out.println("Parent       : " + koala.getParent());
```

We display the files located in remote directories with `RemoteFile.listFiles(`), which acts as `java.io.File.listFiles()` method:

```java
// List all files located in remote directory /mydir
RemoteFile remoteDir = new RemoteFile(remoteSession, "/mydir");
RemoteFile[] remoteFiles = remoteDir.listFiles();
System.out.println();
System.out.println("All files located in " + remoteDir + ": "
	+ Arrays.asList(remoteFiles));
```

### Files upload and download

We want now to upload a new file to remote `mydir`. We simply use a `RemoteOutputStream` which implements a `java.io.OutputStream` :

```java
// Upload user.home/Tulips.jpg to remote directory /mydir
String userHome = System.getProperty("user.home") + File.separator;
File file = new File(userHome + "Tulips.jpg");

Path path = file.toPath();
try (OutputStream outputStream = new RemoteOutputStream(remoteSession,
     	"/mydir/Tulips.jpg");) {
    Files.copy(path, outputStream);
}
```

We have of course a complete access to all `OutputStream` methods; this will be helpful if we want to create a nice progress indicator for our users. This is the same `Tulips.jpg` upload with alternate syntax using only stream methods:

```java
File file = new File(userHome + "Tulips.jpg");

try (InputStream in = new BufferedInputStream(
    new FileInputStream(file));
     OutputStream out = new RemoteOutputStream(remoteSession,
		"/mydir/Tulips.jpg")) {
    byte[] buffer = new byte[4096];
    int n = 0;
    while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
    }
}
```

Download is as straightforward, we use a `RemoteInputStream` which implements a `java.io.InputStream` :

```java
File file = new File(userHome + "Tulips.jpg");

try (InputStream in = new RemoteInputStream(remoteSession,
		"/mydir/Tulips.jpg");
     OutputStream out = new BufferedOutputStream(
         new FileOutputStream(file));) {
    byte[] buffer = new byte[4096];
    int n = 0;
    while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
    }
}
```

### Secure RPC calls

KRemote Files include a simple to use RPC mechanism,  without stubbing, code generation, XML or JSON.  It is secured so that only authenticated users can call remote Java methods. 

This is a calculator class located on  remote server:

```java
package com.kremotefiles.quickstart;
import org.kawanfw.file.api.server.ClientCallable;

/**
 * A simple calculator to be called from the client side. 
 * Requires the client to be authenticated.
 */
public class Calculator implements ClientCallable {

    public Calculator() {
    }

    public int add(int a, int b) {
        return (a + b);
    }
}
```

The `ClientCallable` interface is a marker interface that indicates to KRemote Files server that `Calculator`class is callable only  by valid and authenticated users.

We use `RemoteSession.call` method to call the `add` method from client side:

```java
String resultStr = remoteSession
        .call("com.kremotefiles.quickstart.Calculator.add", 20, 22);
int result = Integer.parseInt(resultStr);

System.out.println("Result: " + result);
```

## Architecture

The KRemote Files framework consists of:

- A Client  Library. 
- A Server Manager.     
- User Configuration classes injected at runtime (start of server container).

The Client Library is installed on the client side - typically a PC/Mac or an Android device. The client application - typically a Desktop or Android application - accesses the remote files or java classes it through APIs. The execution of each KRemote Files command is conditioned by the rules defined in the User Configuration classes. 

All communications between the PC and the Server are done using HTTP protocol on the standard 80 and 443 ports. 

## Features

- [x] Easy file upload/download & RPC programming.
- [x] Optimized and designed to manage heavy traffic.
- [x] Supports file chunking & automatic recovery.
- [x] Built-in security.
- [x] Easy access of remote files with `java.io.File` API
- [x] Power of FTP and RMI without the hassles.
- [x] Free and Open Source Software.

## Requirements

KRemote Files  is 100% written in Java, and functions identically under Android Microsoft Windows, Linux and all versions of UNIX supporting Java 7+ and Servlet 2.5.

The following environments are supported in this version:

| OS            | **JVM (Java Virtual Machine)**           |
| ------------- | ---------------------------------------- |
| Android       | Dalvik  4.0.3+                           |
| Windows       | Oracle Java SE 7, Java SE 8, Java SE 9   |
| UNIX/Linux    | Oracle Java SE 7,  Java SE 8 and Java SE 9  <br />OpenJDK 7, OpenJDK 8, OpenJDK 9 |
| OS X  / macOS | Oracle Java SE 7 for OS X 10.7.3+ <br />Oracle Java SE 8 for OS X 10.8+<br />Oracle Java SE 9 for mac OS 10.10+ |

| **Servlet Containers**                   |
| ---------------------------------------- |
| All  Servlet containers that implement the Servlet 2.5+ specifications. |

## License

KRemote Files is licensed through the GNU Lesser General Public License (LGPL v2.1): you can use it for free and without any constraints in your open source projects as well as in your commercial applications.

# Installation

[Download](https://github.com/kawansoft/kremote-files/releases/tag/v1.0) and unzip `kremote-files-1.0-bin.zip` or download and untar `kremote-files-1.0-bin.tar.gz`.

## Server installation

Add the jars of the `/lib` subdirectory to your Servlet container webapp library folder.  (Typically in `/WEB-INF/lib`).

Create a “Server” project and add the jars of the `/lib` subdirectory to your development `CLASSPATH`, or for Maven users:

```xml
<groupId>com.kremote-files</groupId>
<artifactId>kremote-files</artifactId>
<version>1.0</version>
```

## Client installation (including Android)

Create a “Client” project and add to your development `CLASSPATH` the path to the jars located in the `/lib` subdirectory of your installation folder, or for Maven users:

```xml
<groupId>com.kremote-files</groupId>
<artifactId>kremote-files</artifactId>
<version>1.0</version>
```

### Android Project settings

Add the 3 following lines to your `AndroidManifest.xml`:

```xml
<uses-permission android:name=*"android.permission.INTERNET"*/>
<uses-permission android:name=*"android.permission.ACCESS_NETWORK_STATE"*/>
<uses-permission android:name=*"android.permission.WRITE_EXTERNAL_STORAGE"*/>
```

# Configuration on the server side

 The Configuration on the server side addresses:

- Client login and password verification.
- Defining the uploaded user files location on the server.
- Defining the session security management.

## The Server File Manager servlet 

All Commands sent by the client side are received by the Server File Manager servlet. The Server Manager servlet then: 

- Authenticates the client call.
- For file commands : 
- - Executes the file command asked.
  - Sends the result of the command back to the client side.
- For RPC commands :
  - Tests if client user is authenticated.
  - Sends the return value of the called Java method to the client.

### Configure the webapp web.xml

Add the Server File Manager servlet to your `web.xml`:

```xml
<servlet>
    <servlet-name>ServerFileManager</servlet-name>
    <servlet-class>org.kawanfw.file.servlet.ServerFileManager</servlet-class>

    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>ServerFileManager</servlet-name>
    <url-pattern>ServerFileManager</url-pattern>
</servlet-mapping>
```

## Configurators fundamentals

All server configurations are done through Java classes called “Configurators” in our terminology. A Configurator is a user-developed Java class that implements one of the Configurator Java interfaces. The Configurator instance is dynamically loaded by the KRemote Files Server at bootstrap through DI (Dependency injection). The methods of the Configurator instance are  then called internally by the Server File Manager servlet when necessary.

KRemote Files uses one main Configurator:

- `FileConfigurator`.

The second type of Configurator is `SessionConfigurator`. There is no setup required for this Configurator and it is recommended to let KRemote Files automatically use the default `DefaultJwtSessionConfigurator`implementation. Session Configurators are discussed in [Session Security](#session-security).

**Note that KRemote Files comes with Default Configurator classes:**

**it’s not required to write your own  Configurator**

**(in case you only want to test KRemote Files, etc.).**

**See the** [Quick Start](https://github.com/kawansoft/kremote-files/blob/master/kremote-files-1.0-quick-start.md).

## FileConfigurator interface

The [FileConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/FileConfigurator.html) interface allows to define basic access and security settings. A concrete implementation code will let you:

- Define How to authenticate a remote client user.
- Define for each username the home directory of files on the server.
- Optionally define the `java.util.logging.Logger` that KRemote Files will use for logging.

Note that KRemote Files comes with a default `FileConfigurator` implementation that is very liberal and should be extended: [DefaultFileConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/DefaultFileConfigurator.html).

If no `FileConfigurator` is implemented, the server loads and uses the `DefaultFileConfigurator` class.

## Coding a FileConfigurator

The KRemote Files Server settings are coded in a concrete implementation of the `FileConfigurator` interface with the methods:

1. `login`
2. `getHomeDir`
3. `getLogger`

### Login method: authenticating client username and password

You  don't want your KRemote Files server be accessible to the whole world. KRemote Files  provides a mechanism that allows to check the username and password sent by the remote client program. This is done through the login method of `FileConfigurator`  interface. If login returns `true`, access is granted. 

The username and password should be checked against an  applicative access mechanism, such as a LDAP directory, a login table in the database, etc.

The following example will check that the username and password passed by the client match an access list defined in a SQL table of your host  database:

```java
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
```

**Note**: 

The included [SshAuthFileConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/SshAuthFileConfigurator.html) is a concrete `FileConfigurator` that extends `DefaultFileConfigurator`. 

It allows zero-code client (username, password) authentication using SSH.

### getHomeDir method:  defining the user files locations

`getHomeDir` allows to define the home directory for each client user, aka where the users files will be stored when uploaded.

Default implementation in `DefaultFileConfigurator` always return:

`user.home/.kremote-server-root`

where `user.home` is the value returned by `System.getProperty("user.home")`.

See the included Configurators [PerUserHomeFileConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/PerUserHomeFileConfigurator.html) and  [SysRootHomeFileConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/SysRootHomeFileConfigurator.html) and if you want to define other standard implementations, or you can always code your own `getHomeDir`.

### getLogger method: defining where server Exceptions are stored

Default implementation in `DefaultFileConfigurator` defines a `java.util.logging.Logger` whose pattern is `user.home/.kawansoft/log/kremote_files.log` and that logs 50Mb into 4 rotating files.

## Passing concrete Configurator classes

Your concrete `FileConfigurator` implementations are passed to KRemote Files as parameters of the `ServerFileManager` servlet in your `web.xml` configuration file. 

The `FileConfiguratorClassName` parameter lets you define your `FileConfigurator` concrete implementation. Following is an example if you want to inject your `org.acme.config.MyFileConfigurator`class:

```xml
<servlet>
    <servlet-name>ServerFileManager</servlet-name>
    <servlet-class>org.kawanfw.file.servlet.ServerFileManager</servlet-class>
  
        <init-param>        
            <param-name>fileConfiguratorClassName</param-name>
            <param-value>org.acme.config.MyFileConfigurator</param-value>
        </init-param>  
  
         <init-param>        
            <param-name>jwt_secret_value</param-name>
            <param-value>MySecretValue</param-value>
        </init-param>
  
        <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>ServerFileManager</servlet-name>
    <url-pattern>ServerFileManager</url-pattern>
</servlet-mapping>
```

If you don’t provide a parameter for `fileConfiguratorClassName`, KRemote Files will use the default implementation `DefaultFileConfigurator`.

### The jwt_secret_value init parameter

The init parameter `jwt_secret_value` allows to set the secret value used by JWT (Java Web Toolkit) for token creation. See [Session Security](#session-security).

if you don't want the secret value to be stored in plain text in `web.xml`: it is possible to set this value at runtime just after server start using the public static String `JwtSessionStore.JWT_SECRET_VALUE`.

## RPC rules for calling Java methods from the client side

The RPC mechanism has been designed to be very simple to use but sill secure 

**How to declare your server class as callable by the client side**

Any server Java class method maybe called by the client side if the server class follows the following requirements:

- The class must implement the [ClientCallable](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/ClientCallable.html) or [ClientCallableNoAuth](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/ClientCallableNoAuth.html) interface.


- The class must have a default  visible constructor with no parameters. (It is the constructor that will     be invoked by the Server Manager servlet).

`ClientCallable` and `ClientCallableNoAuth` are marker interfaces and have no method: they allow to indicate to the Server File Manager that the classes are callable from the client side. 

The interface to implement depends on your authentication need:

- Implement `ClientCallable` if you require that users must be authenticated to use the class. 
- Implement `ClientCallableNoAuth` if you want your class to be callable by any user, without authentication.

## Testing you server configuration

After restarting you server, type the http address of the `ServerFileManager` servlet in a browser:

`http://www.yourhost.com/path-to-servlet/ServerFileManager`

It will display your configuration and a status line that should display: `OK & Running`.

If not, the configuration errors are detailed in red for correction.

# Usage: client site programming

The client API is composed of 4 main classes:

| Name               | Role                                     |
| ------------------ | ---------------------------------------- |
| RemoteSession      | Main class for establishing an http session with a remote host and for executing  from client side some basic operations |
| RemoteFile         | Allows to  execute `java.io.File` methods on remote files. |
| RemoteInputStream  | Obtains  input bytes from a remote file. Used to download a remote file. |
| RemoteOutputStream | An output stream for writing data to a remote file. Used to upload a file. |

## The RemoteSession class

[RemoteSession](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteSession.html) is the main class for establishing an http session with a remote host.

The `RemoteSession` instance is passed to all other client API classes and is used by them for session identification and authentication.  

`RemoteSession` is also used and for executing some basic client side operations: 

- Get the Java version of the servlet container on the remote server. 
- Call remote Java methods. 
- Returns with one call the length of a list of files located on the remote host. 

### Session creation and authentication

The `RemoteSession` constructor takes at least three parameters:

- `url`: the URL of the path to the KRemote Files Manager Servlet.
- `username`:  the user username for authentication.
- `password`: the authentication password.

The `RemoteSession` is created only if the KRemote Files Manager has authenticated the user. The authentication is done by invoking the `login` method of the `FileConfigurator` instance.  

Example of `RemoteSession` creation:

```java
// Define URL of the path to the ServerFileManager servlet
String url = "https://www.acme.org/ServerFileManager";

// The login info for strong authentication on server side
String username = "myUsername";
char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };

// Establish a session with the remote server
RemoteSession remoteSession = new RemoteSession(url, username, password);
```

### Defining a proxy

Communication via an (authenticating) proxy server is done using a `java.net.Proxy` instance. If proxy requires authentication, pass the credentials via a `java.net. PasswordAuthentication` instance:

```java
Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
    "proxyHostname", 8080));

PasswordAuthentication passwordAuthentication = null;

// If proxy requires authentication:
passwordAuthentication = new PasswordAuthentication("proxyUsername",
	"proxyPassword".toCharArray());

RemoteSession remoteSession = new RemoteSession(url, username,
	password, proxy, passwordAuthentication);
```

### Handling Exceptions thrown by RemoteSession 

 `RemoteSession` methods throw exclusively the following exceptions:

**Table of Exceptions thrown by RemoteSession methods**

| **Exception**                                                | **Signification**                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| java.net.UnknownHostException                                | The URL is malformed.                                        |
| [InvalidLoginException](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/exception/InvalidLoginException.html) | The (username, password) authentication  failed on the remote KRemote Files Manager.  (The implemented `File.Configurator.login` method returned false). |
| java.net.UnknownHostException                                | There is no Internet connection. There is an error in HTTP address in the `url` parameter. |
| java.net.ConnectException                                    | The HTTP Request returned a HTTP Status Code  != OK (200).   Use `RemoteSession.getHttpStatusCode()` to retrieve the Status Code. |
| java.net.SocketException                                     | Network failure during transmission.                         |
| [RemoteException](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/exception/RemoteException.html) | An unexpected Exception has been thrown by  the server. <br />Signals KRemote Files product failure. |
| java.io.IOException                                          | For all other IO / Network / System Error.                   |

### RPC: Calling Java methods on the KRemote Files Server

Use `RemoteSession.call` to call a remote Java method:

First parameter is the full name of the method to call with the notation: `package_name.className.methodName`.  Example: `org.kawanfw.examples.Calculator.add`

All following parameters are optional and are the parameters of the remote method.

The result is always returned as a String.

The requirements for the Java class on the server are explained in [RPC rules for calling Java methods from the client side](#rpc-rules-for-calling-java-methods-from-the-client-side).

Example of a `RemoteSession` call:

```java
// OK: call the add(int a, int b) remote method that returns a + b:
String result = remoteSession.call(
    "org.kawanfw.examples.Calculator.add" ", 41, 42);
    System.out.println("Calculator Result: " + result);
```

 of  the  remote `org.kawanfw.examples.Calculator` class:

```java
package org.kawanfw.examples;
import org.kawanfw.file.api.server.ClientCallable;

/**
 * Simple calculator to be called from the client side. 
 * Requires the client to be authenticated.
 */

public class Calculator implements ClientCallable {

    /**
     * Constructor
     */
    public Calculator() throws Exception {
    }

    public int add(int a, int b) {
		return (a + b);
    }
}
```

## The RemoteFile class

[RemoteFile](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteFile.html) is the main class  for accessing remote files.

`RemoteFile` methods have the same names, signatures and behaviors as `java.io.File` methods:

a `RemoteFile` method is a `java.io.File` method that is executed on the remote host.

The few differences with `java.io.File` are:

- There is only one constructor that takes  as parameter a `RemoteSession` and a pathname. The pathname is defined with a  `"/"` separator on all platforms and must be absolute.
- `File.compareTo(File)` and `File.renameTo(File)` methods take a `RemoteFile` parameter in this class.
- File methods that return `java.io.File` instance(s) return `RemoteFile` instance(s) in this class.
- The `File.toURI()` and `File.toURL(`) methods are meaningless in this context and are thus not ported.
- The static `File.listRoots()` and `File.createTempFile()` are not ported.
- The Java 7+ `File.toPath()` method is not ported as this KRemote Files version does not support remote `java.nio.file.Path` objects.

Note that the real pathname used on host for File method execution depends on the KRemote Files configuration of `FileConfigurator.getHomeDir()`. This follows the same principle of FTP server mechanisms.

Example:

If `FileConfigurator.getHomeDir(String username)` returns `/home/<username>`  and client username is `"mike"` : 

```java
new RemoteFile(remoteSession, "/myfile.txt").exists();
```

will return he result of the execution on remote host of: 

```java
new File("/home/mike/myfile.txt").exists();
```

Note: the user rights are the rights of the servlet container when accessing remote files.

### Using FilenameFilter and FileFilter

When using `java.io.FilenameFilter` and `java.io.FileFilter` filters, the filter implementation must follow these rules:

- The filter must implement `Serializable`.
- Thus, the filter class must already exist  on the server side.
- When using anonymous inner class for `FilenameFilter` or `FileFilter`: it must be public static.

### Handling Exceptions thrown by RemoteFile methods

`RemoteFile` methods throw corresponding `java.io.File` Exceptions plus the following Exceptions that are wrapped by a `RuntimeException` (except `java.lang.SecurityException` which is not wrapped).

**Table of Exceptions thrown by RemoteFile methods**

| Exception                                                    | **Signification**                                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| java.net.UnknownHostException                                | There is  no more Internet connection.                       |
| [InvalidLoginException](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/exception/InvalidLoginException.html) | The `RemoteSession` has been closed.                         |
| java.net.ConnectException                                    | The HTTP Request returned a Http Status Code  != OK (200).  Use `RemoteSession.getHttpStatusCode()` to retrieve the Status Code. |
| java.net.SocketException                                     | Network failure during transmission.                         |
| java.lang.SecurityException                                  | The remote `java.io.File` throwed a   `java.lang.SecurityException`. |
| [RemoteException](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/exception/RemoteException.html) | The remote `java.io.File` throwed an Exception.              |
| java.io.IOException                                          | For all other IO / Network / System Error.                   |

### Example of RemoteFile usage

```java
// Define URL of the path to the ServerFileManager servlet
String url = "https://www.acme.org/ServerFileManager";

// The login info for strong authentication on server side
String username = "myUsername";
char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };

// Establish a session with the remote server
RemoteSession remoteSession = new RemoteSession(url, username, password);

// Create a new RemoteFile that maps a file on remote server
RemoteFile remoteFile = new RemoteFile(remoteSession, "/Koala.jpg");

// We can use all the familiar java.io.File methods on our RemoteFile
if (remoteFile.exists()) {
    System.out.println(remoteFile.getName() + " length  : "
                       + remoteFile.length());
    System.out.println(remoteFile.getName() + " canWrite: "
                       + remoteFile.canWrite());
}

// List files on our remote root directory
remoteFile = new RemoteFile(remoteSession, "/");

RemoteFile[] files = remoteFile.listFiles();
for (RemoteFile file : files) {
    System.out.println("Remote file: " + file);
}

// List all text files in out root directory
// using an Apache Commons IO 2.5 FileFiter
FileFilter fileFilter = new SuffixFileFilter(".txt");

files = remoteFile.listFiles(fileFilter);
for (RemoteFile file : files) {
    System.out.println("Remote text file: " + file);
}
```

## The RemoteInputStream and RemoteOutputStream  classes

A [RemoteInputStream](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteOutputStream.html) obtains input bytes from a remote File.

The remote file bytes are read with standards `java.io.InputStream` read methods and can thus be downloaded into a local file.

A [RemoteOutputStream](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteOutputStream.html) is an output stream for writing data to a remote file.

It allows to create a remote file by writing bytes on the `RemoteOutputStream` with standards `OutputStream` write methods.

These classes are provided to :

1. Offer APIs with no learning curve.
2. Allow easy existing code migration, because they implement `java.io.InputStream` and `java.io.OutputStream`.
3. Allow easy use of progress bars for file uploads and downloads.

`RemoteInputStream` and `RemoteInputStream` implement automatic file chunking and recovery mechanism as described in [Data transport](#data-transport).

## Using Progress Bars with file upload and download

Simply use `RemoteInputStream` or `RemoteOutputStream` to download or upload the files, and increment the Progress Bar in the read/write loop.

At the end of the read/write, set the Progress Bar progress indicator to 100.

**Example**:

Declare the global variables used by the Progress Monitor and in the read/write loop:

```java
/**
 * Progress between 0 and 100.
 * Updated by doFileUpload() at each 1% input stream read
 */
private int progress = 0;

/** Says to doFileUpload() code if transfer is cancelled */
private boolean cancelled = false;
```

The `doFileUpload` method is called to upload a file:

```java
/**
 * Do the file upload.
*/
private void doFileUpload() {
    try {

        // BEGIN MODIFY WITH YOUR VALUES
        String userHome = System.getProperty("user.home");

        String url = "http://localhost:8080/kremote-files/ServerFileManager";
        String username = "username";
        char[] password = "password".toCharArray();

        File file = new File(userHome + File.separator + "image_1.jpg");
        String pathname = "/image_1_1.jpg"; // remote file path
        // END MODIFY WITH YOUR VALUES

        RemoteSession remoteSession = new RemoteSession(url, username,
            password);

        long fileLength = file.length();

        try (InputStream in = new BufferedInputStream(
            new FileInputStream(file));
            OutputStream out = new RemoteOutputStream(remoteSession,
                pathname, fileLength);) {

        int tempLen = 0;
        byte[] buffer = new byte[1024 * 4];
        int n = 0;

        while ((n = in.read(buffer)) != -1) {
            tempLen += n;

            // Test if user has cancelled the upload
            if (cancelled)
            throw new InterruptedException(
                "Upload cancelled by User!");

            // Add 1 to progress for each 1% upload
            if (tempLen > fileLength / 100) {
                tempLen = 0;
            progress++;
            }

            out.write(buffer, 0, n);
        }

        out.close();

        } finally {
        // When finished, set to the maximum value to stop the
        // ProgressMonitor
            progress = 100;
        }

        remoteSession.logoff();

        System.out.println("File upload done.");
    } catch (Exception e) {

        if (e instanceof InterruptedException) {
        System.out.println(e.getMessage());
            return;
        }

        System.err.println("Exception thrown during Upload:");
        e.printStackTrace();
    }
}
```

Assuming you want to display a progress indicator using `javax.swing.SwingWorker`, you would start as a Thread the previous code. To update the progress bar, the `SwingWorker.doInBackground()` method would be overridden :

```java
@Override
public Void doInBackground() {
    // Reset values at each upload
    cancelled = false;
    progress = 0;
    setProgress(0);

    // progress is ++ at each
    // 1% file transfer in doFileUpload()
    while (progress < 100) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignore) {
        }

        // Say to doFileUpload() that
        // user has cancelled the upload
        if (isCancelled()) {
            cancelled = true;
            break;
        }

        setProgress(Math.min(progress, 100));
    }

    return null;
}
```

 A complete example is available in [FileTransferProgressMonitorDemo.java](http://www.kremote-files.com/soft/1.0/src/FileTransferProgressMonitorDemo.java).

# Advanced Usage

## Session security

Session management (token creation, token verification, etc.) is defined with the [SessionConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/session/SessionConfigurator.html) interface and the selected concrete implementation.

### The DefaultJwtSessionConfigurator class

It is neither required nor advised to define a `SessionConfigurator` implementation, the default implementation coded in [DefaultJwtSessionConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/session/DefaultJwtSessionConfigurator.html) requires no setup, except passing a secret value at JavaEE web server startup.

`DefaultJwtSessionConfigurator`uses the standardized [JWT](https://jwt.io/) (JSON Web Tokens) session management protocol. See [DefaultJwtSessionConfigurator](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/server/session/DefaultJwtSessionConfigurator.html) Javadoc or source code for more information and for passing the secret value.

### Creating your own session management

If you want to create your own session management: create  a session configurator class that implements `SessionConfigurator`.

Then modify your `web.xml` to pass your class name as the  init parameter value of a `sessionConfiguratorClassName` init parameter name of the`ServerFileManager` servlet :

```xml
<servlet>
    <servlet-name>ServerFileManager</servlet-name>
    <servlet-class>org.kawanfw.file.servlet.ServerFileManager</servlet-class>
    
    <init-param>        
        <param-name>fileConfiguratorClassName</param-name>
        <param-value>org.acme.config.MyFileConfigurator</param-value>
    </init-param>  
        
    <init-param>        
        <param-name>sessionConfiguratorClassName</param-name>
        <param-value>org.acme.MySessionConfigurator</param-value>
    </init-param> 
    
    <load-on-startup>1</load-on-startup>

</servlet>

<servlet-mapping>
    <servlet-name>ServerFileManager</servlet-name>
    <url-pattern>ServerFileManager</url-pattern>
</servlet-mapping>
```

## Data transport

### Transport format

 KRemote Files transfers the least possible meta-information:

- Request parameters are transported in UTF-8 format.
- JSON format is used for data  & classes transport. 

### Content Streaming & memory management

All requests are streamed, especially for file uploads and downloads:

- Output requests (from client side)  are streamed to avoid buffering any content body by streaming directly to the socket to the server.
- input response (for client side) are streamed efficiently read the response body by streaming directly from the socket to the server. 

### Chunked upload of large files

Large files are split in chunks that are uploaded in sequence when using [RemoteOutputStream](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteOutputStream.html). The default chunk length is 10Mb. You can change the default value with [SessionParameters.setUploadChunkLength(long)](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/SessionParameters.html#setUploadChunkLength(long))  before passing `SessionParameters` to `RemoteSession` class constructor. 

[SessionParameters](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/SessionParameters.html) usage is described in [Session parameters](#session-parameters). 

### Chunked download of large files

Large file are split in chunks that are downloaded in sequence when using [RemoteInputStream](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteInputStream.html).  The default chunk length is 10Mb. You can change the default value with [SessionParameters.setDownloadChunkLength(long)](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/SessionParameters.html#setDownloadChunkLength(long)) before passing `SessionParameters` to `RemoteSession` class constructor.

### File chunking and stateful or stateless architecture

Note that file chunking requires that all chunks be uploaded/downloaded to/from to the same web server. Thus, file chunking does not support true stateless architecture with multiple identical web servers. If you want to set a full stateless architecture with multiple identical web servers, you must disable file chunking.  

This is done by setting a 0 upload and download chunk length value using: 

- [SessionParameters.setUploadChunkLength(long)](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/SessionParameters.html#setUploadChunkLength(long))  
- [SessionParameters.setDownloadChunkLength(long)](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/SessionParameters.html#setDownloadChunkLength(long)) 

### Large file upload & download recovery

KRemote Files supports recovery for large files upload and download.

In case of recoverable I/O or communication `Exception`, aka `SocketException`, the recall of the upload or download sequence will restart the transfer at the last chunk non completely transmitted.

The only condition is to recall the upload/download in the same JVM run (so  recovery will not be supported if application is completely stopped and restarted.) 

For example, when using default chunk length of 10Mb: if the upload of a 2Gb file is interrupted at 1,8Gb, only the remaining 200Mb will be resent when re-invoking `RemoteInputStream` or `RemoteOutputStream` sequence in the same JVM life cycle.

Chunks are stored in temporary directories. See [Managing temporary files](#managing-temporary-files) for more information.

## Using multiple RemoteSession in Threads

You may use multiple different `RemoteSession` in your programs. And you may use them in background threads. 

Please note that `RemoteSession` is not thread safe: only one thread may access an `RemoteSession` instance at a time. Otherwise, results are unpredictable.

However, `RemoteSession` is cloneable. Just `clone` your current `RemoteSession` to get a new one to use for simultaneous file operations:

```java
// Establish a session with the remote server
RemoteSession remoteSession = new RemoteSession(url, username, password);

// Establish a secondary RemoteSession for background thread:
RemoteSession secondaryRemoteSession = remoteSession.clone();
```

## Using RemoteSession to different KRemote Files Servers 

You may use multiple `RemoteSession` that access different KRemote Files Servers in the same program.

There is nothing to set on the client side. Simply use different `url` parameters in your `RemoteSession` constructors. 

For each `url` defined, there must be a corresponding Server File Manager servlet on the server side:

```java
// The main Server File Manager
String url = "https://www.acme.org/ServerFileManager";

// The second Server File Manager
String url2 = "https://www.acme.org/ServerFileManager2";

// The login info for strong authentication on server side
// (Assuming it's the same for the two Server File Managers)
String username = "myUsername";
char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };

// Establish a session with the first remote server
RemoteSession remoteSession = new RemoteSession(url, username,
	password);

// Establish a session with the second remote server
RemoteSession remoteSession2 = new RemoteSession(url2,
	username, password);
```

There is some configuration on the server side: a second Server File Manager servlet must be defined in `web.xml` with the corresponding  new Configurators classes passed as parameters to the new servlet:

```xml
<servlet>
    <servlet-name>ServerFileManager2</servlet-name>
    <servlet-class>org.kawanfw.file.servlet.ServerFileManager</servlet-class>

    <init-param>
        <param-name>FileConfiguratorClassName</param-name>
        <param-value>org.acme.config.MyFileConfigurator2</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>ServerFileManager2</servlet-name>
    <url-pattern>ServerFileManager2</url-pattern>
</servlet-mapping>
```

## Session parameters

The [SessionParameters](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/SessionParameters.html) class allows also  to define some settings for the KRemote Files session:

- Timeout value, in milliseconds, to be used when opening a communications link with the remote server. Defaults to 0 (no timeout).


- Read timeout, in milliseconds, that specifies the timeout when reading from remote Input stream. Defaults to 0 (no timeout).


- Boolean to say if HTTP content must be compressed. Defaults to true. 


- Download chunk length to be used by `RemoteInputStream`. Defaults to 10Mb. 0 means files are not chunked.


- Upload chunk length to be used by `RemoteOutputStream`. Defaults to 10Mb. 0 means files are not chunked. 

This example shows how to change some timeout default values:

```java
String url = "https://www.acme.org/ServerFileManager";
String username = "myUsername";
char[] password = { 'm', 'y', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd' };

SessionParameters sessionParameters = new SessionParameters();

// Sets the timeout until a connection is established to 10 seconds
sessionParameters.setConnectTimeout(10);

// Sets the read timeout to 60 seconds
sessionParameters.setReadTimeout(60);

// We will use no proxy
Proxy proxy = null;
PasswordAuthentication passwordAuthentication = null;

RemoteSession remoteSession = new RemoteSession(url, username,
	password, proxy, passwordAuthentication, sessionParameters);
// Etc.
```

##  Managing temporary files

KRemote Files uses temporary files on client side only. These temporary files contain: 

- Result of the `RemoteFile.list` methods.
- Result of the `RemoteFile.listFiles` method.
- Chunks created by `RemoteInputStream` and `RemoteOutputStream`.

Temporary files are created to allows streaming and/or to release as soon as possible network resources (servlet streams).

These temporary files are automatically cleaned (deleted).

If you want to insure the cleaning of temporary files, they are located in the `user.home/.kawansoft/tmp` directory. 

(Where `user.home` is the `System.getProperty("user.home")` value of the user that starts the client application and/or the servlet container on the server side.)

------











 

 
