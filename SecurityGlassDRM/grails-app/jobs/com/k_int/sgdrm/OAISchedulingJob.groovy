package com.k_int.sgdrm



class OAISchedulingJob {
    static triggers = {
		cron name: 'simpleOAIScheduleTrigger', cronExpression: "0 * * * * ?", startDelay:120000l // Run every minute on the minute after a delay of 2 minutes at start up
    }

	def concurrent = false;
	
	def connectorSystem = new ConnectorSubsystemService();
	
    def execute() {
		
		log.debug("In the execute method of the OAI scheduling job... " + new Date());
		
		// Check that this job should be running and only continue if it should
		def jobStatus = JobStatus.findByName("OAISchedulingJob");
		if ( !jobStatus ) {
			// No job status found in the database - need to set one up for use later
			jobStatus = new JobStatus(name: "OAISchedulingJob", description: "OAI client scheduling task", active: false, lastRunTime: null);
			
			if ( !jobStatus.save(flush: true) ) {
				log.error("Error thrown when trying to create a new JobStatus object for the OAISchedulingJob class");
				jobStatus.errors.each() {
					log.error("Error: " + it);
				}
			}
		} else if ( jobStatus.active ) {
		
			// We have a status object and this job should be active - process
			log.debug("JobStatus object found and this OAISchedulingJob job should be active - starting processing");

			def starttime = System.currentTimeMillis();
			def startDate = new Date(starttime);
			
			// Go and get any repository connectors that are in state 'Harvest requested'
			def requestedStatus = ConnectorStatus.findByName("Harvest requested");
			def toQueue = RepositoryConnector.findAllByConnectorStatus(requestedStatus, [sort: "statusChangeTime",order:"asc"]);



			if ( toQueue ) {

				// Loop through each job that's waiting and set it to queued
				def queuedStatus = ConnectorStatus.findByName("Queued");
				toQueue.each() {
					log.debug("Setting status to queued for connector with id: " + it.id);
					it.connectorStatus = queuedStatus;
					it.save(flush: true);
				}


				// Loop through each job that's waiting to be harvested and add it to the OAI subsystem.
				log.debug("Before adding 'harvest requested' connectors.. Connector subsystem connectors: " + connectorSystem.listConnectors());

				def runningStatus = ConnectorStatus.findByName("Running");
				def completedStatus = ConnectorStatus.findByName("Completed");

				toQueue.each() {

					// Store that we're starting to run it
					it.connectorStatus = runningStatus;
					it.save(flush:true)

					// Register the connector with the subsystem (and so set it going)
					connectorSystem.registerConnector([type:'oai',
								shortcode: it.name,
								baseuri: it.url,
								setname: it.setSpec,
								prefix: it.metadataPrefix,
								connector: 'com.k_int.sgdrm.OAIConnector',
								maxbatch: 800,
								owner: it.store.storeContext.name + "/" + it.store.name]);

					log.debug("Just registered a connector (with name: " + it.name + "). Connector subsystem connectors: " + connectorSystem.listConnectors());

					def startTime = System.currentTimeMillis();

					connectorSystem.syncRepository(it.name);

					it.connectorStatus = completedStatus;

					log.debug("Finished syncing.. after: ${System.currentTimeMillis() - startTime}ms");

				};

			} else {
				log.debug("No connectors to queue this time..");
			}
			
			// Update the job status so we know when it last ran
			jobStatus.lastRunTime = startDate;
			jobStatus.save(flush: true);
			
		} else {
			log.debug("JobStatus object found for the OAISchedulingJob, but active is false so not doing any processing");	
		}
    }
}
