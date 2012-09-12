package com.k_int.sgdrm

/**
 * Domain object for the repository connector 
 * @author rpb rich@k-int.com
 */
class RepositoryConnector {
	
    String          name;
    String          url;
    String          encoding;
    String          setSpec;
    String          metadataPrefix;
    ContentStore    store;
	ConnectorStatus connectorStatus;
	Date			statusChangeTime;
    
    
    static constraints = {
		statusChangeTime (nullable: true)
    }
	
	// Note - Custom JSON marshaller specified in the bootstrap.groovy class..
	
}

