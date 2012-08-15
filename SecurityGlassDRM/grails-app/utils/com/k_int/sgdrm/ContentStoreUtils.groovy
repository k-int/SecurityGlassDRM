package com.k_int.sgdrm

/**
 * Utility classes relating to content stores
 * @author rpb rich@k-int.com
 * @version 1.0 13.08.12
 */
class ContentStoreUtils {
	
    /**
    * A method to take a specified store name and convert it into something that doesn't have spaces, etc.
    * to ensure that it can be used in URLs, etc.
    */
    public static String createValidStoreName(String storeName) {
        def retval = storeName;
        
        if ( storeName.contains(" ") ) {
            retval = retval.replaceAll(" ", "-");
        }
        
        // TODO - what else do we want to remove?
        return retval;
    }
}

