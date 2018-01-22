/**
 * 
 */
package org.kawanfw.file.reflection;

/**
 * 
 * Test that a FileFilter or FileNamefilter has a no-args/nullary constructor and that it is public static if inner
 * 
 * @author Nicolas de Pomereu
 *
 */
public class FilterLoader {

    /**
     * 
     */
    protected FilterLoader() {
    }
    
//    public static void load(Class<?> clazz) {
//	try {
//	    String modifiers = Modifier.toString(clazz.getModifiers());
//	    if (!modifiers.contains("static") || !modifiers.contains("public")) {
//		clazz.newInstance();
//	    }
//	} catch (Exception e) {
//	    if (e instanceof InstantiationException) {
//		throw new IllegalArgumentException(
//			Tag.PRODUCT
//				+ " Invalid FilenameFilter or FileFilter. Filter class must have a nullary constructor: "
//				+ clazz.getName());
//	    } else if (e instanceof IllegalAccessException) {
//		throw new IllegalArgumentException(
//			Tag.PRODUCT
//				+ " Invalid FilenameFilter or FileFilter. Filter inner class must be static and public: "
//				+ clazz.getName());
//	    } else {
//		throw new IllegalArgumentException(
//			Tag.PRODUCT
//				+ " Invalid FilenameFilter or FileFilter. Filter class can not be invoked with new(classname) : "
//				+ clazz.getName());
//	    }
//	}
//    }
    
    

}
