

# KRemote Files v1.0 - Quick Start Guide
<img src="https://www.aceql.com/favicon.png" alt="AceQ HTTP Icon"/>

* [Installation](#installation)
  * [Requirements](#requirements)
  * [Development Environment](#development-environment)
* [Server Side Settings](#server-side-settings)
   * [Client Side Settings](#client-side-settings)
   * [From now on…](#from-now-on)

# Installation

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

We use a desktop environment only for this Quick Start Guide.

## Development Environment

[*Download*](https://github.com/kawansoft/kremote-files/releases/tag/v1.0) and unzip `kremote-files-1.0-bin.zip` or download and untar `kremote-files-1.0-bin.tar.gz`.

Add the jar libraries of the `/lib` subdirectory to your IDE project or development CLASSPATH.

Or for Maven users:

```xml
<groupId>com.kremote-files</groupId>
<artifactId>kremote-files</artifactId>
<version>1.0</version>
```

# Server Side Settings

1. Create an `kremote-files-quickstart` webapp on you Servlet container.

2. Add the following lines to `web.xml` of the `kremote-files-quickstart` webapp.

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

3. Deploy the jar libraries of the installation folder `/lib ` subdirectory to your Servlet container webapp library folder - typically in `/WEB-INF/lib`. Restart your Servlet container.

4. Test you configuration in a browser with the URL: [*http://localhost:8080/kremote-files-quickstart/ServerFileManager* (assuming you test  KRemote Files your machine with Tomcat running on default 8080

   port). The status line should display OK & Running. If not, fix the displayed error
   and retry. That's it!

# Client Side Settings

1. Create an `com.kremotefiles.quickstart` package in your IDE.

2. Download this java source file: [SessionExample.java](http://www.kremote-files.com/soft/1.0/src/SessionExample.java). Then insert it in the package.

3. The session to the remote server is created using a *[RemoteSession](http://www.kremote-files.com/soft/1.0/javadoc/org/kawanfw/file/api/client/RemoteSession.html)*.

4. Modify if necessary the `url` variable in the `remoteSessionBuilder()` method. It must point to the `ServerFileManager` servlet as defined in your `web.xml`:

   ```java
   /**
    * RemoteSession Quick Start client example. Creates an Awake RemoteSession.
    * 
    * @return the Awake Remote Session established with the remote Awake FILE
    *         server
    * @throws IOException
    *             if communication or configuration error is raised
    */
   public static RemoteSession remoteSessionBuilder() throws IOException {

       // Path to the ServerFileManager Servlet:
       String url = "http://localhost:8080/kremote-files/ServerFileManager";
    
       // (usename, password) for authentication on server side.
       // No authentication will be done for our Quick Start:
       String username = "username";
       char[] password = { 'd', 'e', 'm', 'o' };
    
       // Establish a session with the remote server
       RemoteSession remoteSession = new RemoteSession(url, username,
           password);
       return remoteSession;
   }
   ```

5. Download the two files [Koala.jpg](http://www.kremote-files.com/soft/1.0/src/Koala.jpg) and [Tulips.jpg](http://www.kremote-files.com/soft/1.0/src/Tulips.jpg) in your `"user.home"` directory on the client side.

6. Compile and run from your IDE the `SessionExample` class. It will create a remote directory, upload the two files to the server, list directories & files on the server, rename a remote file and download files from the server. Note that all operations on remote files use the same method name and signature as in `java.io.File`:

   ```java
   /**
    * Do some KRemote Files operations. This example:
    * <ul>
    * <li>Creates a remote directory.</li>
    * <li>Uploads two files to the remote directory.</li>
    * <li>Lists the content of the remote directory.</li>
    * <li>Displays some info on a remote file.</li>
    * <li>Renames a remote file.</li>
    * <li>Downloads the files from the remote directory.</li>
    * </ul>
    * 
    * The example uses {@code RemoteFile} objects for operations on remote
    * files. The {@code RemoteFile} methods used are the same as in
    * {@code java.io.File}.
    * 
    * @throws IOException
    *             if communication or configuration error is raised
    */
   public void doIt() throws IOException {
       
       // Define userHome var
       String userHome = System.getProperty("user.home") + File.separator;
       System.out.println("\"user.home\" is: " + userHome);
       
       // Create a remote directory on the server
       // RemoteFile methods are the same as java.io.File methods
       System.out.println("Creating remote /mydir...");
       RemoteFile remoteDir = new RemoteFile(remoteSession, "/mydir");
       remoteDir.mkdirs();
       
       // Creating a subdirectory on the server
       System.out.println("Creating remote /mydir/subdir...");
       RemoteFile remoteSubdir = new RemoteFile(remoteSession,
           "/mydir/subdir");
       remoteSubdir.mkdir();
       
       // Upload two files Koala.jpg and Tulips.jpg file located in our
       // user.home directory to the remote directory /mydir
       File image1 = new File(userHome + "Koala.jpg");
       File image2 = new File(userHome + "Tulips.jpg");
       
       System.out.println(
           "Uploading " + userHome + " files to remote /mydir...");
       
       Path pathImage1 = image1.toPath();
       try (OutputStream outImage1 = new RemoteOutputStream(remoteSession,
           "/mydir/Koala.jpg");) {
           Files.copy(pathImage1, outImage1);
       }
       
       Path pathImage2 = image2.toPath();
       try (OutputStream outImage2 = new RemoteOutputStream(remoteSession,
           "/mydir/Tulips.jpg");) {
           Files.copy(pathImage2, outImage2);
       }
       
       // List all files located in remote directory /mydir
       RemoteFile[] remoteFiles = remoteDir.listFiles();
       System.out.println();
       System.out.println("All files located in " + remoteDir + "     : "
           + Arrays.asList(remoteFiles));
       
       // List sub-directories only of remote directory /mydir
       // Uses an Apache Commons IO FilterFilter
       FileFilter fileFilter = DirectoryFileFilter.DIRECTORY;
       
       remoteFiles = remoteDir.listFiles(fileFilter);
       System.out.println("Subdirectories of " + remoteDir + ": "
           + Arrays.asList(remoteFiles));
       
       RemoteFile koala = new RemoteFile(remoteSession, "/mydir/Koala.jpg");
       System.out.println();
       System.out.println("Display remote " + koala + " info...");
       System.out.println("last modified: " + new Date(koala.lastModified()));
       System.out.println("length       : " + koala.length());
       System.out.println("Parent       : " + koala.getParent());
       
       // Rename a file on server
       System.out.println();
       RemoteFile koalaRenamed = new RemoteFile(remoteSession,
           "/mydir/Koala_RENAMED.jpg");
       System.out.println("Renaming " + koala + " to " + koalaRenamed + "...");
       boolean renameDone = koala.renameTo(koalaRenamed);
       System.out.println("Rename done: " + renameDone);
       
       // Download the files - with a new name - in our user.home directory
       File downloadedImage1 = new File(userHome + "downloaded-Koala.jpg");
       File downloadedImage2 = new File(userHome + "downloaded-Tulips.jpg");
       
       System.out.println();
       System.out.println(
           "Downloading files from remote /mydir in " + userHome + "...");
       
       Path pathDownloadedImage1 = downloadedImage1.toPath();
       try (InputStream inImage1 = new RemoteInputStream(remoteSession,
           "/mydir/Koala_RENAMED.jpg");) {
           Files.copy(inImage1, pathDownloadedImage1,
               StandardCopyOption.REPLACE_EXISTING);
       }
       
       Path pathDownloadedImage2 = downloadedImage2.toPath();
       try (InputStream inImage2 = new RemoteInputStream(remoteSession,
           "/mydir/Tulips.jpg");) {
           Files.copy(inImage2, pathDownloadedImage2,
               StandardCopyOption.REPLACE_EXISTING);
       }
       
       System.out.println("Done!");
   }
   ```

Note that the root of the file server for the demo user will be located in the following directory:

`user.home/.kremote-server-root/mydir`

where:

`user.home`: value of `System.getProperty("user.home")` of your Servlet container.

This default server configuration is of course modifiable.

# From now on…

You can read the [*User Guide*](https://github.com/kawansoft/kremote-files/blob/master/README.md) or run through the *[Javadoc](http://www.kremote-files.com/soft/1.0/javadoc)*. You will learn:

-   How to configure the KRemote Files Server to set the location of the user files on the server.

-   How to create a secure authentication on the server.

-   How to call a remote java method without complicated setup and how to secure these calls.

-   How to handle Exceptions thrown by the client side or the server side.

-   How to display nice progress indicators to your users during uploads & downloads.


