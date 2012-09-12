package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

class StoreController {

    def springSecurityService;
    
    static allowedMethods = [uploadFile: 'POST'];

	def connectorSystem = new ConnectorSubsystemService();
    
    def index() { 
        log.error("In the store controller - specified context = " + params.context + " and store: " + params.store);
        
        def specifiedContext = params.context;
        def specifiedStore = params.store;
		
		def return404 = false;
		
		// Get the specified store to check that it exists..
		def actualContext = Context.findByName(specifiedContext);
		if ( actualContext == null ) {
			return404 = true;	
		} else {
			def actualStore = ContentStore.findByNameAndStoreContext(specifiedStore, actualContext);
		
			if ( actualStore == null ) {
				return404 = true;
			} else {
			
				// We have a store as requested - continue with processing..
			
				// Work out whether the user is logged in and whether they have permissions to upload to this store
				def principal = null;
				if ( ( springSecurityService.principal ) && !(springSecurityService.principal instanceof String) && ( springSecurityService.principal.id ) ) {
					principal = User.get(springSecurityService.principal.id)
				}

				def uploadPermissions = false;
				
				if ( principal != null ) {
					if ( principal.id == actualStore.storeContext.owner.id ) 
						uploadPermissions = true;		
				}
		                
		        return [specifiedContext: specifiedContext, specifiedStore: specifiedStore, uploadPermissions: uploadPermissions];
			}
		}
		
		if ( return404 ) {
			// The specified context / store not found - return a 404 error
			response.sendError(404)
			
		}
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def createContentStore() {
      
    // If we have received a GET request then set up the blank form as required, otherwise (if POST) then
    // validate and save the specified data
    
    def result = [:]
    
    def principal;
    if ( ( springSecurityService.principal ) && !(springSecurityService.principal instanceof String) && ( springSecurityService.principal.id ) ) {
        principal = User.get(springSecurityService.principal.id)
    }
    
	// Check that the user has permissions to create a store here and fail if not..
	def actualContext = Context.findByName(params.storeOwner);
	if ( actualContext == null || actualContext.owner.id != principal.id ) {
		// No context, or no permissions to create a store here...
		flash.message = "You can't create a store here";
		response.sendError(403, "You can't create a store here");
	} else {
		
	    if ( request.post ) {
	        // A POST submission
	        log.debug("In the createContentStore method with a POST. Params: name:" + params.storeName + " type:" + params.storeType + " owner: " + params.storeOwner)
	        
	        // Convert the given store name into something that is valid
	        def newStoreName = ContentStoreUtils.createValidStoreName(params.storeName);
	        log.debug("newStoreName worked out to be: " + newStoreName)
	        
	        // Set up the new content store with the specified values
	        def existingContentStore = ContentStore.findByName(newStoreName);
	        if ( existingContentStore ) {
	            // Already a store with the chosen name - complain
	            log.debug("Attempt to create a content store with a name that is already in use..");
	            
	            flash.error = "Specified content store name already in use please try another";
	            result.storeName = params.storeName;
	            result.storeType = params.storeType;
	            result.storeOwner = params.storeOwner;
	        } else {
	            
	            def storeContext = Context.findByName(params.storeOwner);
	            def storeType = ContentStoreType.findByName(params.storeType);
	
	            log.debug("storeContext = " + storeContext + " and type: " + storeType + " and name: " + newStoreName);
	                
	            def newContentStore = new ContentStore(name: newStoreName, storeContext: storeContext, storeType: storeType);
	//            newContentStore.storeContext = storeContext;
	            if ( !newContentStore.save() ) {
	                newContentStore.errors.each {
	                    println it;
	                }
	            }
	            
	                log.debug("newContentStore = " + newContentStore);
	            
	            // Content store created - send the user to the created store
	            flash.message = newStoreName + " content store successfully created";
	            redirect(uri:"/" + params.storeOwner + "/" + newContentStore.name);
	        }
	        
	    } else {
	        // A GET request
	        log.debug("In the createContentStore method with a GET.");
	        
	        if ( params.storeOwner) {
	            result.storeOwner = params.storeOwner;
	        }
	            
	
	        
	        // Get the list of possible owners (contexts)
	        def possibleContexts = Context.findAllByOwner(principal);
	        def possibleStoreTypes = ContentStoreType.list();
	            
	        
	        result.possibleContexts = possibleContexts;
	        result.possibleStoreTypes = possibleStoreTypes;
	        
	    }
	}
	    
    return result
  }
  
  def checkNewContentStoreName() {
    
    log.debug("In the checkNewContentStoreName method with name: " + params.storeName + " and owner: " + params.storeOwner);
        def retval = true;
        
        def newName = params.storeName
        def storeOwner = params.storeOwner
        
        def validNewName = ContentStoreUtils.createValidStoreName(newName)
        def storeContext = Context.findByName(storeOwner);
        
        log.debug("About to check for existing store with name: " + validNewName + " and context: " + storeContext);
    
        def existingStore = ContentStore.findByNameAndStoreContext(validNewName, storeContext);
        
        if ( existingStore ) {
            // There's already a store with that name
            retval = false;
        }
    
        log.debug("retval = " + retval);
        
        render retval;
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def uploadFile() {
        log.debug("In the uploadFile method with store name: " + params.store + " and context: " + params.context);
        
        
        // TODO - put some processing in place..

        flash.message = "Upload received..";
        
        redirect(uri: "/" + params.context + "/" + params.store);
        
    }

    def viewConnector() {
        log.debug("In the viewConnector method with store name: " + params.store + " and context:"  + params.context + " and sub action: " + params.subAction);
        
        if ( "list".equals(params.subAction) ) {
            // Go and get the actual context and store that are specified
            def actualContext = Context.findByName(params.context);
            def actualStore = ContentStore.findByStoreContextAndName(actualContext, params.store);

            // Now find all of the connectors that are connected to this store
            def allConnectors = RepositoryConnector.findAllByStore(actualStore);


            // If the user is authenticated then work out whether they own this
            // store so that we can give admin access if required
            def isOwner = false;
            def principal;
            if ( springSecurityService.principal instanceof String ) {
                principal = null;
            } else if ( ( springSecurityService.principal ) && ( springSecurityService.principal.id ) ) {
                principal = User.get(springSecurityService.principal.id)
            }

            if ( principal ) {
                // We have a user - are they the owner of this store?
                if ( principal.id == actualContext.owner.id ) {
                    log.debug("This is the owner..");
                    isOwner = true;
                } else {
                    log.debug("This isn't the owner..");
                }
            } else {
                log.debug("Not logged in so can't check if owner")
            }


            def retval = ["connectors": allConnectors, "isOwner": isOwner]

            render retval as JSON;

        } else if ( "show".equals(params.subAction) ) {
            
            if ( params.connectorId ) {
                // Go and get the connector specified by the id
                def connector = RepositoryConnector.findById(params.connectorId);
                
                render connector as JSON;
            } else {
                // No ID - complain..
                // TODO
            }
        } 
    }
        
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def adminConnector() {
        
        log.debug("In the adminConnector method of the store controller with sub action: "  + params.subAction );
        
        if ( "add".equals(params.subAction) ) {
            
            def actualContext = Context.findByName(params.context);
            def actualStore = ContentStore.findByStoreContextAndName(actualContext, params.store);
            def existingConnector = RepositoryConnector.findByNameAndStore(params.oaiName, actualStore);
            
            if ( existingConnector ) {
                // Already got a connector with the specified name - complain
                log.debug("Attempt to create a connector for a store with a name that is already in use: " + params.oaiName);
                
                // TODO - how can we complain!!!!
            } else {
                // New connector name - so create one
				def idleStatus = ConnectorStatus.findByName("Idle");
                def newConnector = new RepositoryConnector(name: params.oaiName, url: params.oaiUrl, encoding: params.oaiEncoding, setSpec: params.oaiSet, metadataPrefix: params.oaiPrefix, store: actualStore, connectorStatus: idleStatus, statusChangeTime: new Date());
                
                if ( !newConnector.save(flush:true) ) {
                    // Deal with the thrown error...
                    newConnector.errors.each {
                        log.error(it);
                    }
                } else {
                    // Saved - continue..
                    flash.message = "New repository connector added successfully";
                    
                    redirect(uri: "/" + params.context + "/" + params.store);
                }
                
                
            }
        } else if ( "edit".equals(params.subAction) ) {
            
            // First get back the existing connector to make the changes to
            def existingConnector = RepositoryConnector.findById(params.connectorId);
            existingConnector.name = params.editOaiName;
            existingConnector.url = params.editOaiUrl;
            existingConnector.setSpec = params.editOaiSet;
            existingConnector.metadataPrefix = params.editOaiPrefix;
            existingConnector.encoding = params.editOaiEncoding;
            
            if ( !existingConnector.save(flush: true) ) {
                // Deal with the thrown error..
                existingConnector.errors.each {
                    log.error(it);
                }
            } else {
                // Saved - continue
                flash.message = "Repository connector successfully updated";
                redirect(uri: "/" + params.context + "/" + params.store);
            }
            // TODO
        } else if ( "checkConnectorName".equals(params.subAction) ) {
            // Two possible cases - adding a connector (no ID specified) and editing
            // an existing connector (ID specified)
            
            log.debug("context: " + params.context + " store: " + params.store + " oai name: " + params.name + " connectorId: " + params.connectorId);
            def retval = false;
            def context = Context.findByName(params.context);
            def store = ContentStore.findByStoreContextAndName(context, params.store);
            def existingConnector = RepositoryConnector.findByNameAndStore(params.name, store);
            
            if ( !existingConnector ) {
                // No existing connector - definitely OK to use this name
                log.debug("No existing connector found - ok to use this name");
                retval = true;
            } else {
                // Existing connector - if we're editing then check that this is the same connector 
                // (and so names aren't changed)
                if ( params.connectorId ) {
                    Long connIdLong = new Long(params.connectorId);
                    
                    if ( connIdLong.equals(existingConnector.id) ) {
                        // We have an ID and it matches the found connector - valid
                        log.debug("connector id specified and it is the same as the found connector - ok to use this name still");
                        retval = true;
                    } else {
                        log.debug("connector id specified but it's different to the connector we've found - not ok to use this name: id: *" + params.connectorId + "* existing id: *" + existingConnector.id + "*");
                        retval = false;
                    }
                } else {
                    log.debug("no connector id specified or it's not the same as the found connector - not ok to use this name");
                    retval = false;
                }
            }
            
            render retval;
        } else if ( "delete".equals(params.subAction) ) {
            
            log.debug("Delete sub action called with connector id: " + params.connectorId + " and context: " + params.context + " and store: " + params.store);
            
            // Load the context and store to check that the connector is connected to this store before
            // performing the deletion as a sanity check
            def context = Context.findByName(params.context);
            def store = ContentStore.findByStoreContextAndName(context, params.store);
            
            def existingConnector = RepositoryConnector.findByIdAndStore(params.connectorId, store);
            
            if ( !existingConnector ) {
                // The connector either doesn't exist, or isn't connected to this store - log it but don't do anything
                log.info("Attempt to delete a connector with id: " + params.connectorId + " and specified context: " + params.context + " and store: " + params.store + " but the connector can't be found linked to that store - no deletion");
            } else {
                // We have a connector that is connected to this store, so delete it
                log.debug("Connector found ready to be deleted");
                existingConnector.delete();
            }
            
            render true;
        } else if ( "startHarvest".equals(params.subAction) ) {
			log.debug("Start Harvest action called with connector id: " + params.connectorId);
			
			// Go and get the connector so that we can set it going
			def connectorDetails = RepositoryConnector.findById(params.connectorId);
			
			if ( connectorDetails ) {
				// We have found the connector - set it as queued and then we'll pick it up and start
				// elsewhere
				def queuedStatus = ConnectorStatus.findByName("Queued");
				connectorDetails.connectorStatus = queuedStatus;
				connectorDetails.statusChangeTime = new Date();
				
				connectorDetails.save(flush:true);
								
				// TODO - want to move the following code out into a timer task at some point...

//				// Clear existing harvests.. // TODO - is that what this does??
//				connectorSystem.removeAll();
//				
//				// Register the connector with the subsystem (and so set it going)
//				connectorSystem.registerConnector([type:'oai', 
//													shortcode: connectorDetails.name,
//													baseuri: connectorDetails.url,
//													setname: connectorDetails.setSpec,
//													prefix: connectorDetails.metadataPrefix,
//													connector: 'com.k_int.sgdrm.OAIConnector',
//													maxbatch: 100]);
//												
//				log.debug("Connector subsystem connectors: " + connectorSystem.listConnectors());
//				
//				def startTime = System.currentTimeMillis();
//				
//				connectorSystem.syncRepository(connectorDetails.name);
//				
//				log.debug("Finished syncing.. after: ${System.currentTimeMillis() - startTime}ms");
//	
//								// TODO
			} else {
				// No connector found - can't set it going..
				
				// TODO - how should we fail here?
			}
		
			render true;
        }
        
        // TODO
    }
}
