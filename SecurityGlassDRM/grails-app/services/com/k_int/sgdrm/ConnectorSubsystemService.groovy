package com.k_int.sgdrm


class ConnectorSubsystemService {

  def mongo = new com.gmongo.GMongo()
  def db = mongo.getDB("media_rcs")
  

  def registerConnector(Map params) {
    log.debug("Looking up existing connector with shortcode ${params.shortcode}");
    def existing_entry = db.connectors.findOne(shortcode:params.shortcode)
    if ( existing_entry ) {
      log.debug("Update existing connector");
		existing_entry.baseuri = params.baseuri;
		existing_entry.setname = params.setname;
		existing_entry.prefix = params.prefix;
		existing_entry.un = params.un;
		existing_entry.pw = params.pw;
		existing_entry.connector = params.connector;
		existing_entry.owner = params.owner;
		existing_entry.maxbatch = params.maxbatch;
		
		db.connectors.save(existing_entry);
    }
    else {
      println("Register new connector");
      db.connectors.save(params);
    }
  }

  def listConnectors() {
    def result = []
    db.connectors.find().each { c ->
      result.add(c);
    }
    result
  }

  def syncAll() {
  }

  def removeAll() {
    db.connectors.remove([:]);
  }

  def syncRepository(shortcode) {
    def existing_entry = db.connectors.findOne(shortcode:shortcode)
    if ( existing_entry ) {
      log.debug("sync with ${shortcode} - Get instance of ${existing_entry.connector}");
	  
      GroovyClassLoader gcl = new GroovyClassLoader();
      def connector_class = gcl.loadClass(existing_entry.connector);
      def connector = connector_class.newInstance();
	  
	  log.debug("connector class: " + connector_class.getClass().toString());
      connector.sync(existing_entry);
      // Update any returned params
	  existing_entry.lastRunFinished = new Date();
      log.debug("Updating entry: ${existing_entry}");
	  
      db.connectors.save(existing_entry);
    }
    else {
      throw new Exception("Unknown Connector Shortcode ${shortcode}");
    }
  }
}
