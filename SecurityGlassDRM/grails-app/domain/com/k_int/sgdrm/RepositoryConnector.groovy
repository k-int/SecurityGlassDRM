package com.k_int.sgdrm

/**
 * Domain object for the repository connector 
 * @author rpb
 */
class RepositoryConnector {
	
    String          name;
    String          url;
    String          encoding;
    String          setSpec;
    String          metadataPrefix;
    ContentStore    store;
    
    
    static constraints = {
    }
}

