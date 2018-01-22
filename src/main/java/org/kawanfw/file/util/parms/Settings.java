/**
 * 
 */
package org.kawanfw.file.util.parms;

import org.kawanfw.file.api.client.RemoteSession;

/**
 * 
 * Define settings for some behaviors.
 * @author Nicolas de Pomereu
 *
 */
public class Settings {

//    /** 
//     * Says if the FilenameFilter and FileFilter base64 serialized strings 
//     * should be transported by string (request) or per file upload
//     */
//    public static final boolean TRANSFER_FILTER_PER_FILE_UPLOAD = true;
    
    /** 
     * Define the max size for a String for http request. Greater Stringd should be
     * uloaded with a file to lower server memory usage
     */
    public static final long MAX_STRING_SIZE_FOR_HTTP_REQUEST = RemoteSession.KB * 4;

    /**
     * 
     */
    protected Settings() {
	
    }

}
