/**
 * 
 */
package org.kawanfw.file.api.server;

import java.io.File;

/**
 * This File Configurator extends {@link DefaultFileConfigurator} and implements
 * only {@code getHomeDir} which returns a home directory with a root of <code>user.home/.kremote-server-root</code>
 * and with the sub-directory of client {@code username}.
 * <br><br>
 * Example: 
 * <br><code>user.home/.kremote-server-root/mike</code> for {@code username} {@code "mike"}.
 * <br>
 * <br>
 * The directory is created if it does not exists.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PerUserHomeFileConfigurator extends DefaultFileConfigurator
	implements FileConfigurator {


    /* (non-Javadoc)
     * @see org.kawanfw.file.api.server.FileConfigurator#getHomeDir(java.lang.String)
     */
    /**
     * Returns <code>user.home/.kremote-server-root/username</code> for passed {@code username}.
     * @return <code>user.home/.kremote-server-root/username</code>.
     */
    @Override
    public File getHomeDir(String username) {
	File parent = super.getHomeDir(username);
	File homeDir = new File(parent.toString() + File.separator + username);
	
	
	if (! homeDir.exists()) {
	    homeDir.mkdirs();
	}
	
	return homeDir;
	
    }

}
