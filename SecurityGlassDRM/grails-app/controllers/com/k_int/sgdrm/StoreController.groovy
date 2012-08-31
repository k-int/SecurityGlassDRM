package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

class StoreController {

    def springSecurityService;
    
    static allowedMethods = [uploadFile: 'POST'];

    
    def index() { 
        // TODO - what do we want here?
        log.error("In the store controller - specified context = " + params.context + " and store: " + params.store);
        
        def specifiedContext = params.context;
        def specifiedStore = params.store;
                
        return [specifiedContext: specifiedContext, specifiedStore: specifiedStore];
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def createContentStore() {
      
    // If we have received a GET request then set up the blank form as required, otherwise (if POST) then
    // validate and save the specified data
    
    def result = [:]
    
    def principal;
    if ( ( springSecurityService.principal ) && ( springSecurityService.principal.id ) ) {
        principal = User.get(springSecurityService.principal.id)
    }
    
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

    def listConnectors() {
        
        log.debug("In the listConnectors method with store name: " + params.store + " and context:"  + params.context);
        
        // Go and get the actual context and store that are specified
        def actualContext = Context.findByName(params.context);
        def actualStore = ContentStore.findByStoreContextAndName(actualContext, params.store);
        
        // Now find all of the connectors that are connected to this store
        def allConnectors = RepositoryConnector.findAllByStore(actualStore);
        
        
        // If the user is authenticated then work out whether they own this
        // store so that we can give admin access if required
        def isOwner = false;
        def principal;
        if ( ( springSecurityService.principal ) && ( springSecurityService.principal.id ) ) {
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
        // TODO
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
                def newConnector = new RepositoryConnector(name: params.oaiName, url: params.oaiUrl, encoding: params.oaiEncoding, setSpec: params.oaiSet, metadataPrefix: params.oaiPrefix, store: actualStore);
                
                if ( !newConnector.save(flush:true) ) {
                    // Deal with the thrown error...
                    newConnector.errors.each {
                        log.error(it);
                    }
                } else {
                    // Saved - continue..
                    
                    // TODO
                }
                
                
            }
        }
        
        // TODO
    }
}
