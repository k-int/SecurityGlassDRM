package com.k_int.sgdrm



class OAISchedulingJob {
    static triggers = {
		cron name: 'simpleOAIScheduleTrigger', cronExpression: "0 * * * * ?", startDelay:120000l // Run every minute on the minute after a delay of 2 minutes at start up
    }

	def concurrent = false;
	
	def connectorSystem = new ConnectorSubsystemService();
	
    def execute() {
		
		log.debug("In the execute method of the OAI scheduling job... " + new Date());
		
		// Go and get any repository connectors that are in state 'Harvest requestsed'
		def requestedStatus = ConnectorStatus.findByName("Harvest requested");
		def toQueue = RepositoryConnector.findByConnectorStatus(requestedStatus, [sort: "statusChangeTime",order:"asc"]);
		
		
		
		if ( toQueue ) {

			// Loop through each job that's waiting to be harvested and add it to the OAI subsystem.
			log.debug("Before adding 'harvest requested' connectors.. Connector subsystem connectors: " + connectorSystem.listConnectors());
			toQueue.each() {
			
				// Register the connector with the subsystem (and so set it going)
				connectorSystem.registerConnector([type:'oai',
											shortcode: it.name,
											baseuri: it.url,
											setname: it.setSpec,
											prefix: it.metadataPrefix,
											connector: 'com.k_int.sgdrm.OAIConnector',
											maxbatch: 100]);

				log.debug("Just registered a connector (with name: " + it.name + "). Connector subsystem connectors: " + connectorSystem.listConnectors());

				def startTime = System.currentTimeMillis();
				
				connectorSystem.syncRepository(it.name);
				
				log.debug("Finished syncing.. after: ${System.currentTimeMillis() - startTime}ms");
	
			};
			
		} else {
			log.debug("No connectors to queue this time..");
		}
			
    }
}
