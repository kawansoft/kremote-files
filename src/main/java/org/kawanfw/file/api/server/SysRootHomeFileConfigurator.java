/**
 * 
 */
package org.kawanfw.file.api.server;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

/**
 * This File Configurator extends {@link DefaultFileConfigurator} and implements
 * only {@code getHomeDir} which always returns the root directory of the system
 * ({@code "/"} for Unix/Linux, {@code "C:\"} for Windows). <br>
 * <br>
 * This means client user have potential access to all files on Kremote Files
 * Server, depending on security setting of the user on which JavaEE web server is
 * running. <br>
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SysRootHomeFileConfigurator extends DefaultFileConfigurator
	implements FileConfigurator {

    /**
     * Returns the system root directory. <br>
     * "/" for Unix/Linux and "C:\" for Windows. <br>
     * <br>
     * Note that the client has access potential read/write to all files on the
     * system, with the same privileges as the user that started the Java EE
     * web server. 
     * 
     * @return "/" for Unix/Linux and "C:\" for Windows.
     */
    @Override
    public File getHomeDir(String username) {
	if (SystemUtils.IS_OS_WINDOWS) {
	    return new File("C:\\");
	} else if (SystemUtils.IS_OS_UNIX) {
	    // All other cases are Unix
	    return new File("/");
	} else {
	    throw new IllegalArgumentException(
		    "Unsupported Operating System: " + SystemUtils.OS_NAME);
	}
    }

}
